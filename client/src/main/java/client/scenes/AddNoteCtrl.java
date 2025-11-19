package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Note;
import commons.NoteCollection;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

public class AddNoteCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField noteName;


    /**
     * AddNoteCtrl constructor
     *
     * @param server   an instance of ServerUtils
     * @param mainCtrl an instance of MainCtrl
     */
    @Inject
    public AddNoteCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Cancels adding a new note and returns to notes overview.
     */
    public void cancel() {
        clearFields();
        mainCtrl.showNotesOverview();
    }

    /**
     * Adds a new note to the database if it was inputted correctly (non-empty).
     * Then returns to notes overview.
     */
    public void add() {
        try {
            String title = noteName.getText().trim();
            if (!title.isEmpty()) {
                NoteCollection nc;
                if (mainCtrl.getCollectionFilter() == null) {
                    nc = mainCtrl.getDefaultCollection();
                } else {
                    nc = mainCtrl.getCollectionFilter();
                }

                Note note = new Note(noteName.getText(), "", nc);

                if(titleNotUnique(nc.getName(), noteName.getText())) {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setTitle(mainCtrl.getBundle().getString("error"));
                    alert.setHeaderText(mainCtrl.getBundle().getString("already.note.with") + noteName.getText() + " " +
                            mainCtrl.getBundle().getString("in.col") + " " + nc.getName());
                    alert.showAndWait();
                    return;
                }
                nc = server.addNoteToCollection(note, nc.getId());

                clearFields();

                var alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle(mainCtrl.getBundle().getString("success"));
                alert.setHeaderText(mainCtrl.getBundle().getString("note.add.success"));
                alert.showAndWait();

            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle(mainCtrl.getBundle().getString("empty.title.title"));
                alert.setHeaderText(mainCtrl.getBundle().getString("empty.title.content"));
                alert.showAndWait();
                return;
            }
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainCtrl.showNotesOverview();
    }

    /**
     *
     * @param colName collections name
     * @param title note's title
     * @return boolean
     */
    public boolean titleNotUnique(String colName, String title){
        return server.getNotes().stream().anyMatch(note -> (note.getNoteCollection().getName().equals(colName)
                && note.getTitle().equals(title)));

    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        noteName.clear();
    }
}
