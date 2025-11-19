package client.scenes;

import javafx.stage.Stage;

public class KeyboardShortcutsOverviewCtrl {

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void closeWindow() {
        if (stage != null) {
            stage.close();
        } else {
            System.out.println("Stage is not initialized.");
        }
    }
}
