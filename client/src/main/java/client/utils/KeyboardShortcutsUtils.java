package client.utils;

import commons.Note;
import commons.NoteCollection;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class KeyboardShortcutsUtils {

    private final TextField searchBar;
    private final ListView<Note> notesList;
    private final ChoiceBox<NoteCollection> collectionChoice;
    private final Runnable addNoteAction;

    public KeyboardShortcutsUtils(TextField searchBar, BorderPane rootPane,
                                  ListView<Note> notesList,
                                  ChoiceBox<NoteCollection> collectionChoice,
                                  Runnable addNoteAction) {
        this.searchBar = searchBar;
        this.notesList = notesList;
        this.collectionChoice = collectionChoice;
        this.addNoteAction = addNoteAction;
    }

    public void setupScene(Scene scene) {
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                handleEscapeKey();
                event.consume();
            } else if (event.isControlDown()) {
                switch (event.getCode()) {
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
        });
    }

    private void navigateNotes(String direction) {
        if (notesList == null || notesList.getSelectionModel() == null) return;

        if (!notesList.isFocused()) notesList.requestFocus();

        int selectedIndex = notesList.getSelectionModel().getSelectedIndex();
        if ("up".equalsIgnoreCase(direction) && selectedIndex > 0) {
            notesList.getSelectionModel().select(selectedIndex - 1);
        } else if ("down".equalsIgnoreCase(direction) && selectedIndex < notesList.getItems().size() - 1) {
            notesList.getSelectionModel().select(selectedIndex + 1);
        }
    }

    private void navigateCollections(String direction) {
        if (collectionChoice == null || collectionChoice.getSelectionModel() == null) return;

        if (!collectionChoice.isFocused()) collectionChoice.requestFocus();

        int selectedIndex = collectionChoice.getSelectionModel().getSelectedIndex();
        if ("right".equalsIgnoreCase(direction) && selectedIndex < collectionChoice.getItems().size() - 1) {
            collectionChoice.getSelectionModel().select(selectedIndex + 1);
        } else if ("left".equalsIgnoreCase(direction) && selectedIndex > 0) {
            collectionChoice.getSelectionModel().select(selectedIndex - 1);
        }
    }

    private void handleEscapeKey() {
        if (searchBar == null) return;

        searchBar.requestFocus();
        searchBar.selectAll();
    }
}
