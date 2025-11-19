package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Note;
import commons.NoteCollection;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Modality;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for adding a note to a specific note collection.
 * <p>
 * This class manages the interaction between the user interface and the backend logic
 * for selecting a note collection and adding a note to it.
 * </p>
 */
public class AddNoteToCollectionCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Label myLabel;
    @FXML
    private ChoiceBox<NoteCollection> collectionChoice;

    private NoteCollection selectedCollection;

    /**
     * Constructs an instance of {@code AddNoteToCollectionCtrl}.
     *
     * @param server   an instance of {@link ServerUtils} to interact with the server.
     * @param mainCtrl an instance of {@link MainCtrl} to manage the main application flow.
     */
    @Inject
    public AddNoteToCollectionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        collectionChoice = null;
    }

    /**
     * Initializes the controller and sets up the choice box with available note collections.
     * <p>
     * This method is called automatically after the FXML file has been loaded.
     * It populates the collection choice with a list of collections fetched from the server
     * and sets up a listener to track the selected collection.
     * </p>
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        collectionChoice.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                selectedCollection = newValue;
                updateLabel();
            }
        });

        refresh();
    }

    /**
     * Updates the label text based on the current selection and active note.
     * <p>
     * If no active note is selected, a prompt is displayed. If no collection is selected,
     * another appropriate message is displayed. Otherwise, it confirms the action to add
     * the active note to the selected collection.
     * </p>
     */
    public void updateLabel() {
        Note note = mainCtrl.getActiveNote();
        if (note == null) {
            myLabel.setText(mainCtrl.getBundle().getString("no.activenote.content"));
        } else if (selectedCollection == null) {
            if (server.getCollections().isEmpty()) {
                myLabel.setText(mainCtrl.getBundle().getString("no.collections"));
            } else {
                myLabel.setText(mainCtrl.getBundle().getString("select.col.add1") + " "
                        + note.getTitle() + mainCtrl.getBundle().getString("select.col.add2"));
            }
        } else {
            myLabel.setText(mainCtrl.getBundle().getString("conf.note.add1") + " " + note.getTitle()
                    + mainCtrl.getBundle().getString("conf.note.add2") + " " + selectedCollection.getName()
                    + mainCtrl.getBundle().getString("conf.note.add3"));
        }
    }

    public void refresh() {
        List<NoteCollection> collections = server.getCollections();
        collectionChoice.setItems(FXCollections.observableList(collections));
        updateLabel();
    }

    /**
     * Adds the active note to the selected collection.
     * <p>
     * If no collection is selected, a warning is displayed to the user. If an error occurs while
     * adding the note to the collection, an error alert is shown.
     * </p>
     *
     * @throws WebApplicationException if the server operation fails.
     */
    public void add() {
        if (selectedCollection == null) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(mainCtrl.getBundle().getString("no.col.selected"));
            alert.showAndWait();
            return;
        }

        try {
            Note note = mainCtrl.getActiveNote();
            if (note.getNoteCollection().equals(selectedCollection)) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(mainCtrl.getBundle().getString("already.in.col") + selectedCollection.getName());
                alert.showAndWait();
                return;
            }

            if (titleNotUnique(selectedCollection.getName(), note.getTitle())) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(mainCtrl.getBundle().getString("already.note.with") + note.getTitle() + " " +
                        mainCtrl.getBundle().getString("in.col") + selectedCollection.getName());
                alert.showAndWait();
                return;
            }

            selectedCollection = server.addNoteToCollection(note, selectedCollection.getId());

            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle(mainCtrl.getBundle().getString("success"));
            alert.setHeaderText(mainCtrl.getBundle().getString("changed.col.success"));
            alert.showAndWait();

            mainCtrl.showNotesOverview();

        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(mainCtrl.getBundle().getString("error.add"));
            alert.showAndWait();
        }
    }

    /**
     * Cancels the current operation and returns to the main notes overview.
     * <p>
     * This method is invoked when the user decides not to add a note to a collection.
     * </p>
     */
    public void cancel() {
        mainCtrl.showNotesOverview();
    }

    public boolean titleNotUnique(String colName, String title) {
        return server.getNotes().stream().anyMatch(note -> (note.getNoteCollection().getName().equals(colName)
                && note.getTitle().equals(title)));

    }
}
