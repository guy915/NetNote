package client;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        var serverUtils = INJECTOR.getInstance(ServerUtils.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "Server needs to be started before the client, but it does not seem to be available. Shutting down.";
            System.err.println(msg);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Server Unavailable");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
            return;
        }

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, FXML);

    }

}
