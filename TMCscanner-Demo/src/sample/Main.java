package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("TMC Event View (Alpha Version)");
        Scene scene  = new Scene(root, 1080, 570);
        scene.getStylesheets().add(Controller.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        //primaryStage.setMaxHeight(1055);
        primaryStage.setMinHeight(650);
        //primaryStage.setMaxWidth(1800);
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
