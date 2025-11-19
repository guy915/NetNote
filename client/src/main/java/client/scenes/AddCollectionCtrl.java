package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.NoteCollection;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import java.util.Optional;

public class AddCollectionCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField collectionName;

    /**
     * Add collection constructor, uses dependency injection
     *
     * @param server   ServerUtils entity
     * @param mainCtrl MainCtrl entity
     */
    @Inject
    public AddCollectionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Cancels adding new collection, goes back to collection overview.
     */
    public void cancel() {
        clearFields();
        mainCtrl.showCollectionsOverview();
    }

    /**
     * Adds a new collection to the database if it's not empty.
     * Then goes back to collections overview.
     */
    public void add() {
        try {
            Optional<NoteCollection> optionalCollection = getCollection();
            if (optionalCollection.isPresent()) {

                NoteCollection collection = optionalCollection.get();
                if (server.getCollectionByName(collection.getName()).isPresent()) {

                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText("Collection " + collection.getName() + " already exists");
                    alert.showAndWait();
                    return;

                }
                else {
                    server.addCollection(collection);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle(mainCtrl.getBundle().getString("empty.col.title"));
                alert.setContentText(mainCtrl.getBundle().getString("empty.col.content"));
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
        mainCtrl.showCollectionsOverview();
    }

    /**
     * Gets the input from text field, creates an Optional of NoteCollections.
     * It is empty if input was blank, else it's a new Collection with given name.
     *
     * @return an Optional of NoteCollection
     */
    private Optional<NoteCollection> getCollection() {
        Optional<NoteCollection> collection = Optional.empty();
        if (!collectionName.getText().isBlank()) {
            collection = Optional.of(new NoteCollection(collectionName.getText()));
        }
        return collection;
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        collectionName.clear();
    }
}
