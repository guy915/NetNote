package client.scenes;

import client.Flag;
import client.scenes.config.ConfigService;
import client.utils.KeyboardShortcutsUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Note;
import commons.NoteCollection;
import commons.NoteTag;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Controller for managing the note overview screen.
 * <p>
 * This class provides functionalities to display and interact with the list of notes,
 * apply collection filters, and perform note-specific actions such as editing or adding to collections.
 * </p>
 */
public class NoteOverviewCtrl implements Initializable {

    private static final long DEBOUNCE_DELAY = 50; // milliseconds
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Set<NoteTag> chosenTags;
    private final ConfigService configService;
    private Note activeNote;
    private NoteCollection collectionFilter;
    private boolean isRefreshing = false;
    private Flag currentFlag;
    private boolean isSelectingLang;
    private WebEngine webEngine;
    private NoteCollection defaultCollection;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledRenderTask;

    @FXML
    private ListView<Note> notesList;
    @FXML
    private TextField noteName;
    @FXML
    private TextArea contentEdit;
    @FXML
    private WebView renderedDisplay;
    @FXML
    private ChoiceBox<NoteCollection> collectionChoice;
    @FXML
    private ListView<NoteTag> tagsChoice;
    @FXML
    private TextField searchBar;
    @FXML
    private ComboBox<Flag> languageChoice;
    @Inject
    private KeyboardShortcutsCtrl keyboardShortcutsCtrl;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Button addNoteButton;
    @FXML
    private Button deleteNoteButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button searchButton;

    /**
     * Constructs an instance of {@code NoteOverviewCtrl}.
     *
     * @param server        an instance of {@link ServerUtils} to interact with the server.
     * @param mainCtrl      an instance of {@link MainCtrl} to manage the main application flow.
     * @param configService an instance of {@link ConfigService} to manage application configuration.
     */
    @Inject
    public NoteOverviewCtrl(ServerUtils server, MainCtrl mainCtrl, ConfigService configService) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.configService = configService;
        activeNote = null;
        collectionFilter = null;
        chosenTags = new HashSet<>();
    }

    public static List<Note> filterByTags(Set<NoteTag> tags, List<Note> notes) {
        if (tags == null) {
            return notes;
        }
        for (NoteTag tag : tags) {
            notes = notes.stream().filter(n -> n.hasTag(tag)).toList();
        }
        return notes;
    }

    /**
     * Initializes the controller and sets up listeners for UI components.
     * <p>
     * This method is called automatically after the FXML file has been loaded.
     * It sets up the choice box for collection filtering and the list view for note selection,
     * and starts periodic synchronization for saving note content.
     * </p>
     *
     * @param location  the location used to resolve relative paths for the root object, or null if not known.
     * @param resources the resources used to localize the root object, or null if not applicable.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Optional<NoteCollection> serverDefault = server.getCollectionByName("Default");
        defaultCollection = serverDefault.orElseGet(() -> server.addCollection(new NoteCollection("Default")));

        notesList.getSelectionModel().selectedItemProperty().addListener((_, oldValue, newValue) -> {
            if (oldValue != null) {
                saveContent();
            }
            if (newValue != null) {
                activeNote = newValue;
                refresh();
            }
        });

        flagSetup();
        tagsSetup();
        webViewSetup();

        contentEdit.setWrapText(true);
        startSynchronizing();
        setupMarkdownRendering();
        setupKeyboardShortcuts();
        setupCollectionChoice();
    }

    /**
     * Sets up tag ListView and populates it with CheckBoxes so they are selectable.
     * Tags are checked based on if they are in the chosenTags set.
     */
    private void tagsSetup() {
        tagsChoice.setCellFactory(_ -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            public void updateItem(NoteTag tag, boolean empty) {
                super.updateItem(tag, empty);

                if (empty || tag == null) {
                    setGraphic(null);
                } else {
                    checkBox.setText(tag.getName());
                    checkBox.setSelected(chosenTags.contains(tag));

                    checkBox.selectedProperty().addListener((_, _, newValue) -> {
                        if (Boolean.TRUE.equals(newValue)) {
                            chosenTags.add(tag);
                        } else {
                            chosenTags.remove(tag);
                        }
                        updateNoteListView();
                    });

                    setGraphic(checkBox);
                }
            }
        });
    }

    /**
     * Sets up the webview. Adds .CSS file and links JavaScript to this controller file.
     */
    private void webViewSetup() {
        webEngine = renderedDisplay.getEngine();
        webEngine.setJavaScriptEnabled(true);

        webEngine.getLoadWorker().stateProperty().addListener((_, _, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("noteApp", this);
            }
        });

        URL cssFile = getClass().getResource("/style.css");
        if (cssFile != null) {
            webEngine.setUserStyleSheetLocation(cssFile.toExternalForm());
        }
    }

    private void setupKeyboardShortcuts() {
        KeyboardShortcutsUtils keyboardShortcutsUtils = new KeyboardShortcutsUtils(
                searchBar, rootPane, notesList, collectionChoice, this::addNote
        );

        rootPane.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null) {
                keyboardShortcutsUtils.setupScene(newScene);
            }
        });
    }

    public void restoreCollection(NoteCollection collection) {
        if (collection != null) {
            updateCollectionsChoice(); // Refreshes the collection list

            // Only set the collection if it still exists
            if (collectionChoice.getItems().contains(collection)) {
                collectionFilter = collection;
                collectionChoice.setValue(collection);
                refresh();
            }
        }
    }

    private void setupCollectionChoice() {
        collectionChoice.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        collectionFilter = newValue;
                        try {
                            configService.updateLastSelectedCollection(newValue); // Save the selection
                        } catch (IOException e) {
                            System.err.println("Failed to save last selected collection: " + e.getMessage());
                        }
                        refresh();
                    }
                });
    }

    /**
     * Sets up flag dropdown with correct images.
     */
    private void flagSetup() {
        List<String> langCodes = List.of("en", "nl", "de", "pl", "es");

        List<Flag> flags = new ArrayList<>();
        for (String code : langCodes) {
            flags.add(new Flag(code));
        }

        languageChoice.getItems().addAll(flags);

        languageChoice.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Flag item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(item.getImage());
                    setText(null);
                }
            }
        });

        languageChoice.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Flag item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(item.getImage());
                    setText(null);
                }
            }
        });

        languageChoice.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null && !newValue.equals(currentFlag)) {
                handleFlagSelection(newValue, flags);
            }
        });
    }

    /**
     * Handles the logic when a flag is selected.
     *
     * @param selectedFlag the newly selected flag.
     * @param flags        the list of all flags.
     */
    private void handleFlagSelection(Flag selectedFlag, List<Flag> flags) {
        if (!isSelectingLang) {
            isSelectingLang = true;
            String langCode = selectedFlag.getLangCode();

            currentFlag = selectedFlag;
            mainCtrl.setLanguage(selectedFlag.getLangCode(), selectedFlag);

            languageChoice.getItems().clear(); // Clear and re-add the items after reload
            languageChoice.getItems().addAll(flags);

            for (Flag flag : flags) {
                if (flag.getLangCode().equals(langCode)) {
                    languageChoice.getSelectionModel().select(flag); // Re-select chosen flag image from before
                    break;
                }
            }

            refresh();
            isSelectingLang = false;
        }
    }

    /**
     * Navigates to the add note screen.
     * <p>
     * This method clears the active note and collection filter before transitioning to the add note screen.
     * </p>
     */
    public void addNote() {
        mainCtrl.showAddNote();
    }

    /**
     * Navigates to the collections overview screen.
     * <p>
     * This method clears the active note and collection filter before transitioning to the collections overview screen.
     * </p>
     */
    public void collectionOverview() {
        mainCtrl.showCollectionsOverview();
    }

    /**
     * Adds the active note to a selected collection.
     * <p>
     * If no active note is selected, a warning is displayed. If a note is active, the method navigates to
     * the screen for adding the note to a collection.
     * </p>
     */
    public void addToCollection() {
        if (activeNote == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(mainCtrl.getBundle().getString("no.activenote.title"));
            alert.setHeaderText(null);
            alert.setContentText(mainCtrl.getBundle().getString("no.activenote.content"));
            alert.showAndWait();
        } else {
            mainCtrl.setActiveNote(activeNote);
            mainCtrl.showAddNoteToCollection();
        }
    }

    /**
     * Starts periodic synchronization to save note content.
     * <p>
     * This method creates a timeline that automatically saves the content of the active note every 5 seconds.
     * </p>
     */
    private void startSynchronizing() {
        Timeline autoSave = new Timeline(new KeyFrame(Duration.seconds(2), _ -> saveContent()));
        autoSave.setCycleCount(Animation.INDEFINITE);
        autoSave.play();
    }

    /**
     * Saves the content of the active note to the server.
     * <p>
     * If the content has been modified, the updated content is sent to the server. This method also removes
     * the focus from the content editor to trigger any pending updates.
     * </p>
     */
    public void saveContent() {
        if (activeNote != null) {
            String content = contentEdit.getText();
            if (!content.equals(activeNote.getContent())) {
                activeNote.setContent(content);
                server.updateNoteContent(content, activeNote.getId());
            }
        }
    }

    /**
     * Saves the changed title of a note to the database
     */
    public void saveTitle() {
        noteName.getParent().requestFocus();
        String title = noteName.getText().trim();

        if (activeNote == null || title.isEmpty()) {
            showErrorAlert();
            return;
        }

        Note existingNote = server.getNoteByTitle(title);
        boolean isDuplicateInSameCollection = (existingNote != null)
                && existInCollection(existingNote);

        if (isDuplicateInSameCollection) {
            showErrorAlert();
            return;
        }

        if (!title.equals(activeNote.getTitle())) {
            String oldTitle = activeNote.getTitle();
            activeNote.setTitle(title);
            server.updateNoteTitle(title, activeNote.getId());
            activeNote = server.updateReferences(activeNote, oldTitle);
            contentEdit.setText(activeNote.getContent());
            refresh();
            notesList.refresh();
        }
    }

    /**
     * Checks if an existing note with the same title is in the same collection as the active note.
     * @param existingNote the note to compare to
     * @return returns true if the note exists in the same collection
     */
    private boolean existInCollection(Note existingNote) {
        return existingNote.getNoteCollection().equals(activeNote.getNoteCollection());
    }

    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(mainCtrl.getBundle().getString("error.title.change.on.null"));
        alert.setHeaderText(mainCtrl.getBundle().getString("error.title.change.on.null.header"));
        alert.setContentText(mainCtrl.getBundle().getString("error.title.change.on.null.content"));
        alert.showAndWait();
    }

    /**
     * Deletes the active note, if there is one.
     */
    public void deleteNote() {
        if (activeNote != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(mainCtrl.getBundle().getString("delete.title"));
            alert.setHeaderText(mainCtrl.getBundle().getString("delete.header"));
            alert.setContentText(activeNote.getTitle());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                server.deleteNote(activeNote);
                activeNote = null;
                var alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.initModality(Modality.APPLICATION_MODAL);
                alert2.setTitle(mainCtrl.getBundle().getString("success"));
                alert2.setHeaderText(mainCtrl.getBundle().getString("deleted.note.success"));
                alert2.showAndWait();
                refresh();
            }
        }
    }

    /**
     * Refreshes the note list and updates the UI components.
     * This method fetches the latest notes from the server, applies the selected collection filter, and updates
     * the note list. It also updates the content editor and note title based on the active note.
     * </p>
     */
    public void refresh() {
        if (isRefreshing) {
            return;
        } else isRefreshing = true;

        updateNoteListView();

        if (activeNote != null) {
            noteName.setText(activeNote.getTitle());
            noteName.setEditable(true);
            contentEdit.setText(activeNote.getContent());
            contentEdit.setEditable(true);
            notesList.getSelectionModel().select(activeNote);
            renderNote();
        } else {
            noteName.setText(mainCtrl.getBundle().getString("note.name"));
            noteName.setEditable(false);
            contentEdit.setText(mainCtrl.getBundle().getString("note.content"));
            contentEdit.setEditable(false);
            webEngine.loadContent(mainCtrl.getBundle().getString("empty.webview"));
        }

        updateCollectionsChoice();
        updateTagsChoice();
        isRefreshing = false;
    }

    /**
     * Sets the WebView with the rendered html of the selected note
     */
    private void renderNote() {
        String markdown = activeNote.getContent();
        if (markdown == null || markdown.isBlank()) {
            webEngine.loadContent("");
            server.resetTags(activeNote);
        } else {
            webEngine.loadContent(server.getRenderedHTML(markdown, activeNote.getId()));
        }
    }

    /**
     * Checks whether there have been any changes in the visible notes,
     * if so sets the notes to the new ones.
     */
    public void updateNoteListView() {
        List<Note> notes;
        try {
            String colTitle = collectionFilter == null ? "" : collectionFilter.getName();
            notes = server.searchNotes(searchBar.getText(), colTitle);
            notes = filterByTags(chosenTags, notes);

            if (!notes.contains(activeNote)) {
                activeNote = null;
            }

            ObservableList<Note> newData = FXCollections.observableList(notes);

            if (!notesList.getItems().equals(newData)) { // Doesn't update the list if it hasn't changed
                notesList.setItems(newData);
            }
        } catch (Exception e) {
            System.out.println("Unable to update note list view.");
            System.err.println(e.getMessage());
        }

    }

    public void updateCollectionsChoice() {
        var collections = server.getCollections();
        collectionChoice.getItems().clear();
        collectionChoice.getItems().addAll(collections);

        if (collectionFilter != null) {
            collections.stream()
                    .filter(c -> c.equals(collectionFilter))
                    .findFirst()
                    .ifPresent(c -> collectionChoice.setValue(c));
        }
    }

    public void updateTagsChoice() {
        List<NoteTag> filteredTags = FXCollections.observableList(notesList.getItems().stream()
                .flatMap(n -> n.getNoteTag().stream())
                .distinct().toList());
        List<NoteTag> currentTags = tagsChoice.getItems();

        // Add new tags
        for (NoteTag tag : filteredTags) {
            if (!currentTags.contains(tag)) {
                currentTags.add(tag);
            }
        }

        // Remove unused tags
        currentTags.removeIf(tag -> !filteredTags.contains(tag));
    }

    public void clearTags() {
        chosenTags.clear();
        tagsChoice.getSelectionModel().clearSelection();
        tagsChoice.refresh();
        refresh();
    }

    /**
     * This method is called when you click on a tag in the rendered view
     * It filters the notes on the clicked tag
     *
     * @param tagName to filter on
     */
    public void buttonTagFilter(String tagName) {
        for (NoteTag tag : server.getAllTags()) {
            if (tag.getName().equals("#" + tagName)) {
                chosenTags.add(tag);
                tagsChoice.getSelectionModel().select(tag);
                updateNoteListView();
                break;
            }
        }

        updateTagsChoice();
    }

    /**
     * This method is called when clicking on a reference to another note, to change the
     * active one
     *
     * @param id target note id
     */
    public void changeActiveNote(long id) {
        Note note = server.getNoteById(id);
        if (activeNote != note) {
            activeNote = note;
            refresh();
        }
    }

    public void openKeyboardShortcutsWindow() {
        KeyboardShortcutsOverviewCtrl controller = mainCtrl.getKeyboardShortcutsOverviewCtrl();
        Stage shortcutsStage = new Stage();
        shortcutsStage.setTitle("Keyboard Shortcuts");
        shortcutsStage.setScene(mainCtrl.getKeyboardShortcutsOverviewScene());
        shortcutsStage.initModality(Modality.APPLICATION_MODAL);
        controller.setStage(shortcutsStage);
        shortcutsStage.showAndWait();
    }

    /**
     * Sets the active note to the note that is clicked on in the list view.
     */
    private void setupMarkdownRendering() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        contentEdit.textProperty().addListener((_, _, newValue) -> {
            if (activeNote == null) return;

            if (scheduledRenderTask != null && !scheduledRenderTask.isDone()) {
                scheduledRenderTask.cancel(false);
            }

            scheduledRenderTask = scheduler.schedule(() -> Platform.runLater(() -> {
                try {
                    if (newValue == null || newValue.isBlank()) {
                        webEngine.loadContent("");
                    } else {
                        webEngine.loadContent(server.getRenderedHTML(newValue, activeNote.getId()));
                    }
                } catch (Exception e) {
                    webEngine.loadContent("<p class='error'>Error rendering Markdown: " + e.getMessage() + "</p>");
                }
            }), DEBOUNCE_DELAY, TimeUnit.MILLISECONDS);
        });
    }

    /**
     * Stops the scheduled rendering task when the controller is stopped.
     */
    @FXML
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    @FXML
    public void onMouseEnterAddNote() {
        addNoteButton.setText(mainCtrl.getBundle().getString("add.note"));
    }

    @FXML
    public void onMouseExitAddNote() {
        addNoteButton.setText("➕");
    }

    @FXML
    public void onMouseEnterDeleteNote() {
        deleteNoteButton.setText(mainCtrl.getBundle().getString("delete.active.note"));
    }

    @FXML
    public void onMouseExitDeleteNote() {
        deleteNoteButton.setText("🗑");
    }

    @FXML
    public void onMouseEnterRefresh() {
        refreshButton.setText(mainCtrl.getBundle().getString("refresh"));
    }

    @FXML
    public void onMouseExitRefresh() {
        refreshButton.setText("🔄");
    }

    @FXML
    public void onMouseEnterSearch() {
        searchButton.setText(mainCtrl.getBundle().getString("search"));
    }

    @FXML
    public void onMouseExitSearch() {
        searchButton.setText("🔎");
    }

    @FXML
    public void clearCollectionFilter() {
        collectionFilter = null;
        collectionChoice.getSelectionModel().clearSelection();
        refresh();
    }

    public NoteCollection getDefaultCollection() {
        return defaultCollection;
    }

    public NoteCollection getCollectionFilter() {
        return collectionFilter;
    }
}


