package client.scenes;

import commons.Note;
import commons.NoteCollection;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class KeyboardShortcutsCtrl {

    private TextField searchBar;
    private ListView<Note> notesList;
    private ChoiceBox<NoteCollection> collectionChoice;
    private BorderPane rootPane;

    private Runnable addNoteAction;

    public void setupFields(TextField searchBar, BorderPane rootPane,
                            ListView<Note> notesList, ChoiceBox<NoteCollection> collectionChoice) {
        this.searchBar = searchBar;
        this.rootPane = rootPane;
        this.notesList = notesList;
        this.collectionChoice = collectionChoice;
    }

    public void setupActions(Runnable addNoteAction) {
        this.addNoteAction = addNoteAction;
    }

    public void setupScene(Scene scene) {
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                handleEscapeKey();
                event.consume();
            } else {
                if (event.isControlDown()) {
                    handleControlKeyCode(event.getCode());
                }
            }
        });
    }

    private void handleControlKeyCode(KeyCode keyCode) {
        switch (keyCode) {
            case UP -> navigateNotes("up");
            case DOWN -> navigateNotes("down");
            case LEFT -> navigateCollections("left");
            case RIGHT -> navigateCollections("right");
            case N -> {
                if (addNoteAction != null) {
                    addNoteAction.run();
                }
            }
            default -> {
            }
        }
    }

    private void navigateNotes(String direction) {
        if (notesList == null || notesList.getSelectionModel() == null) {
            System.out.println("notesList or its SelectionModel is not initialized.");
            return;
        }

        if (!notesList.isFocused() && notesList.getItems() != null
                && !notesList.getItems().isEmpty()) {
            notesList.requestFocus();
        }

        int selectedIndex = notesList.getSelectionModel().getSelectedIndex();
        if ("up".equalsIgnoreCase(direction) && selectedIndex > 0) {
            notesList.getSelectionModel().select(selectedIndex - 1);
        } else if ("down".equalsIgnoreCase(direction) && selectedIndex < notesList.getItems().size() - 1) {
            notesList.getSelectionModel().select(selectedIndex + 1);
        }
    }

    private void navigateCollections(String direction) {
        if (collectionChoice == null || collectionChoice.getSelectionModel() == null) {
            System.out.println("collectionChoice or its SelectionModel is not initialized.");
            return;
        }

        if (!collectionChoice.isFocused() && collectionChoice.getItems() != null
                && !collectionChoice.getItems().isEmpty()) {
            collectionChoice.requestFocus();
        }

        int selectedIndex = collectionChoice.getSelectionModel().getSelectedIndex();
        if ("right".equalsIgnoreCase(direction) && selectedIndex < collectionChoice.getItems().size() - 1) {
            collectionChoice.getSelectionModel().select(selectedIndex + 1);
        } else if ("left".equalsIgnoreCase(direction) && selectedIndex > 0) {
            collectionChoice.getSelectionModel().select(selectedIndex - 1);
        }
    }

    private void handleEscapeKey() {
        if (searchBar == null) {
            System.out.println("Search bar is not initialized. " +
                    "Creating a new instance of the search bar.");
            searchBar = new TextField();

            if (rootPane != null) {
                rootPane.setTop(searchBar);
            } else {
                System.out.println("Root pane is not initialized.");
                return;
            }
        }

        searchBar.requestFocus();
        searchBar.selectAll();
    }

}
