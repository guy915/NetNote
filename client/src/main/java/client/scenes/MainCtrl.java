package client.scenes;

import client.Flag;
import client.MyFXML;
import client.scenes.config.Config;
import client.scenes.config.ConfigService;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Note;
import commons.NoteCollection;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Controller for managing the main application flow.
 * <p>
 * This class coordinates the transitions between different scenes and manages shared state,
 * such as the active note and default collection.
 * </p>
 */
public class MainCtrl {
    private final ConfigService configService;
    private final ServerUtils server;
    private Stage primaryStage;
    private MyFXML fxmlLoader;
    private ResourceBundle bundle;
    private NoteOverviewCtrl noteOverviewCtrl;
    private CollectionOverviewCtrl collectionOverviewCtrl;
    private AddNoteToCollectionCtrl addNoteToCollectionCtrl;
    private KeyboardShortcutsOverviewCtrl keyboardShortcutsOverviewCtrl;
    private Scene noteOverviewScene;
    private Scene collectionOverviewScene;
    private Scene addNoteToCollectionScene;
    private Scene addNoteScene;
    private Scene addCollectionScene;
    private Scene keyboardShortcutsOverviewScene;
    private Note activeNote;
    private NoteCollection defaultCollection;

    @Inject
    public MainCtrl(ConfigService configService, ServerUtils server) {
        this.configService = configService;
        this.server = server;
    }

    /**
     * Initializes the MainCtrl with the primary stage and scenes.
     *
     * @param primaryStage current primaryStage instance
     * @param fxmlLoader   MyFXML loader for the .fxml files
     */
    public void initialize(Stage primaryStage, MyFXML fxmlLoader) {
        this.primaryStage = primaryStage;
        this.fxmlLoader = fxmlLoader;

        Config config = configService.getConfig();

        // First load all scenes
        setLanguage(config.preferredLanguage(), null);

        // After scenes are loaded, restore the last selected collection
        NoteCollection lastSelected = config.lastSelectedCollection();
        if (lastSelected != null && noteOverviewCtrl != null) {
            noteOverviewCtrl.restoreCollection(lastSelected);
        }

        showNotesOverview();
        primaryStage.show();
    }

    /**
     * Loads all .FXML files and sets up the scenes, using the selected language as resourceBundle
     *
     * @param bundle the resources of the selected language
     */
    private void loadScenes(ResourceBundle bundle) {
        var noteOverview = fxmlLoader.load(NoteOverviewCtrl.class, bundle, "client", "scenes", "NoteOverview.fxml");
        var collectionOverview = fxmlLoader.load(CollectionOverviewCtrl.class, bundle, "client", "scenes", "CollectionOverview.fxml");
        var addNoteToCollection = fxmlLoader.load(AddNoteToCollectionCtrl.class, bundle, "client", "scenes", "AddNoteToCollection.fxml");
        var addNote = fxmlLoader.load(AddNoteCtrl.class, bundle, "client", "scenes", "AddNote.fxml");
        var addCollection = fxmlLoader.load(AddCollectionCtrl.class, bundle, "client", "scenes", "AddCollection.fxml");
        var keyboardShortcutsOverview = fxmlLoader.load(KeyboardShortcutsOverviewCtrl.class, bundle, "client", "scenes", "KeyboardShortcutsOverview.fxml");

        this.noteOverviewCtrl = noteOverview.getKey();
        this.collectionOverviewCtrl = collectionOverview.getKey();
        this.addNoteToCollectionCtrl = addNoteToCollection.getKey();
        this.keyboardShortcutsOverviewCtrl = keyboardShortcutsOverview.getKey();

        this.noteOverviewScene = new Scene(noteOverview.getValue());
        this.collectionOverviewScene = new Scene(collectionOverview.getValue());
        this.addNoteToCollectionScene = new Scene(addNoteToCollection.getValue());
        this.addNoteScene = new Scene(addNote.getValue());
        this.addCollectionScene = new Scene(addCollection.getValue());
        this.keyboardShortcutsOverviewScene = new Scene(keyboardShortcutsOverview.getValue());
        primaryStage.setScene(noteOverviewScene);
        primaryStage.setTitle(bundle.getString("title.note"));
    }

    public KeyboardShortcutsOverviewCtrl getKeyboardShortcutsOverviewCtrl() {
        return keyboardShortcutsOverviewCtrl;
    }

    public Scene getKeyboardShortcutsOverviewScene() {
        return keyboardShortcutsOverviewScene;
    }

    /**
     * Creates a resource bundle of the selected language and reloads the scenes/controllers
     *
     * @param code of the language to display
     * @param flag of the language to display
     */
    public void setLanguage(String code, Flag flag) {
        try {
            if (flag != null) {
                code = flag.getLangCode();
            }
            this.bundle = ResourceBundle.getBundle("client.i18n.Bundle_" + code);
            loadScenes(bundle);

            // Save the new language preference
            try {
                Config newConfig = new Config(configService.getConfig().defaultCollection(), code, configService.getConfig().lastSelectedCollection());
                configService.updateConfig(newConfig);
            } catch (IOException e) {
                System.err.println("Failed to save language preference: " + e.getMessage());
            }
        } catch (MissingResourceException e) {
            System.err.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Resources for selected language not found");
            alert.showAndWait();
        }
    }

    public void updateLastSelectedCollection(NoteCollection collection) {
        try {
            configService.updateLastSelectedCollection(collection);
        } catch (IOException e) {
            // Handle error appropriately - maybe show an alert to the user
            e.printStackTrace();
        }
    }

    /**
     * Shows the collections overview scene.
     */
    public void showCollectionsOverview() {
        primaryStage.setTitle(bundle.getString("title.col"));
        primaryStage.setScene(collectionOverviewScene);
        collectionOverviewCtrl.refresh();
    }

    /**
     * Shows the note overview scene.
     */
    public void showNotesOverview() {
        primaryStage.setTitle(bundle.getString("title.note"));
        primaryStage.setScene(noteOverviewScene);
        noteOverviewCtrl.refresh();
    }

    /**
     * Shows the add collection scene.
     */
    public void showAddCollection() {
        primaryStage.setTitle(bundle.getString("title.col.add"));
        primaryStage.setScene(addCollectionScene);
    }

    /**
     * Shows the add note scene.
     */
    public void showAddNote() {
        primaryStage.setTitle(bundle.getString("title.note.add"));
        primaryStage.setScene(addNoteScene);
    }

    /**
     * Shows the add note to collection scene.
     */
    public void showAddNoteToCollection() {
        primaryStage.setTitle(bundle.getString("title.note.col"));
        primaryStage.setScene(addNoteToCollectionScene);
        addNoteToCollectionCtrl.refresh();
    }

    /**
     * Getter for the active note.
     *
     * @return the currently active note.
     */
    public Note getActiveNote() {
        return activeNote;
    }

    /**
     * Setter for the active note.
     *
     * @param activeNote the note to set as active.
     */
    public void setActiveNote(Note activeNote) {
        this.activeNote = activeNote;
    }

    public NoteCollection getDefaultCollection() {
        return noteOverviewCtrl.getDefaultCollection();
    }

    public NoteCollection getCollectionFilter() {
        return noteOverviewCtrl.getCollectionFilter();
    }

    /**
     * Getter for the ResourceBundle
     *
     * @return the current resource bundle
     */
    public ResourceBundle getBundle() {
        return bundle;
    }
}
