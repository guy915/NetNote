package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.NoteCollection;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CollectionOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TableView<NoteCollection> table;
    @FXML
    private TableColumn<NoteCollection, String> colName;

    /**
     * CollectionOverviewCtrl constructor
     *
     * @param server   the instance of ServerUtils used
     * @param mainCtrl the instance of MainCtrl used
     */
    @Inject
    public CollectionOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the column table and adds selection handling.
     *
     * @param location  URL location
     * @param resources Resource bundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Add selection listener for table rows
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        });
    }



    /**
     * Returns to main scene
     */
    public void goBack() {
        mainCtrl.showNotesOverview();
    }

    /**
     * Switches scene to Add Collection
     */
    public void addCollection() {
        mainCtrl.showAddCollection();
    }

    /**
     * Refreshes the data in table
     */
    public void refresh() {
        try {
            var collections = server.getCollections();
            ObservableList<NoteCollection> data = FXCollections.observableList(collections);
            table.setItems(data);
            table.getSelectionModel().clearSelection();
            table.refresh();
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Deletes the selected collection after confirmation aslong as it isn't the default.
     */
    @FXML
    public void deleteCollection() {
        NoteCollection selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showNoSelectionAlert();
            return;
        }

        try {
            preventDefaultCollectionDeletion(selected);
        } catch (IllegalStateException e) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        setupConfirmationDialog(confirm, selected);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            performDeletion(selected);
        }
    }

    private void showNoSelectionAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(mainCtrl.getBundle().getString("no.col.delete.title"));
        alert.setHeaderText(null);
        alert.setContentText(mainCtrl.getBundle().getString("no.col.delete.content"));
        alert.showAndWait();
    }

    private void setupConfirmationDialog(Alert dialog, NoteCollection collection) {
        dialog.setTitle(mainCtrl.getBundle().getString("delete.col.title"));
        dialog.setHeaderText(mainCtrl.getBundle().getString("delete.col.header") + collection.getName());
        dialog.setContentText(mainCtrl.getBundle().getString("delete.col.content"));
    }

    private void performDeletion(NoteCollection collection) {
        try {
            server.deleteCollection(collection.getId());
            refresh();
        } catch (WebApplicationException e) {
            showDeletionError(e);
        }
    }

    private void preventDefaultCollectionDeletion(NoteCollection collection) {
        if (collection.equals(mainCtrl.getDefaultCollection())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(mainCtrl.getBundle().getString("error.delete.default.title"));
            alert.setHeaderText(null);
            alert.setContentText(mainCtrl.getBundle().getString("error.delete.default.content"));
            alert.showAndWait();
            throw new IllegalStateException("Cannot delete default collection");
        }
    }

    private void showDeletionError(WebApplicationException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(mainCtrl.getBundle().getString("error"));
        alert.setHeaderText(null);
        alert.setContentText(mainCtrl.getBundle().getString("error.delete.col.content") + e.getMessage());
        alert.showAndWait();
    }

    /**
     * Brings up a alert to allow the user to change the title of the collection.
     */
    @FXML
    public void changeCollection() {
        NoteCollection selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            noSelectionAlert();
            return;
        }

        try {
            preventDefaultCollectionChange(selected);
        } catch (IllegalStateException e) {
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle(mainCtrl.getBundle().getString("edit.col.title"));
        dialog.setHeaderText(mainCtrl.getBundle().getString("edit.col.header"));
        dialog.setContentText(mainCtrl.getBundle().getString("edit.col.content"));

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            if (newName.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(mainCtrl.getBundle().getString("empty.col.title"));
                alert.setHeaderText(null);
                alert.setContentText(mainCtrl.getBundle().getString("empty.col.content"));
                alert.showAndWait();
                return;
            } else if (server.getCollectionByName(newName).isPresent()) {

                var alert = new Alert(Alert.AlertType.WARNING);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle(mainCtrl.getBundle().getString("identical.col.title"));
                alert.setHeaderText(mainCtrl.getBundle().getString("identical.col.header"));
                alert.setContentText(mainCtrl.getBundle().getString("identical.col.content") + ": " + newName);
                alert.showAndWait();

            }
            try {
                selected.setName(newName);
                server.updateCollection(selected);
                refresh();
            } catch (WebApplicationException e) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle(mainCtrl.getBundle().getString("error.update.col") + " " + e.getMessage());
                err.showAndWait();
            }
        });
    }

    private void noSelectionAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(mainCtrl.getBundle().getString("no.col.change.title"));
        alert.setHeaderText(null);
        alert.setContentText(mainCtrl.getBundle().getString("no.col.change.content"));
        alert.showAndWait();
    }

    private void preventDefaultCollectionChange(NoteCollection selected) {
        if (selected.equals(mainCtrl.getDefaultCollection())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(mainCtrl.getBundle().getString("error.change.default.title"));
            alert.setHeaderText(null);
            alert.setContentText(mainCtrl.getBundle().getString("error.change.default.content"));
            alert.showAndWait();
            throw new IllegalStateException("Cannot delete default collection");
        }
    }
}
