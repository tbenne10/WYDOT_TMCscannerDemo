package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("TMC Event View (Demo)");
        Scene scene  = new Scene(root, 1070, 700);
        scene.getStylesheets().add(Controller.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(650);
        primaryStage.setMinWidth(600);


        Controller controller = loader.getController();

        //Call shutdown to kill threads
        primaryStage.setOnCloseRequest(e -> controller.Shutdown());

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
