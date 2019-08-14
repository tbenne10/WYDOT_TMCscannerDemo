package sample;
import com.jfoenix.controls.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


import static java.lang.Integer.parseInt;



public class Controller{

    //Parameters class
    private static Parameters para = new Parameters();

    //Alert Logic class
    private static AlertLogic Logic = new AlertLogic();

    //SensorMap: maps sensor ID with relative sensor class object
    private static HashMap<String, Sensor> SensorMap = new HashMap<>();

    //Store duplicate sensor IDs to retrieve and set additional tablerows.
    private static HashMap<String, Sensor> DuplicateSensors = new HashMap<>();

    //OldData: Used for comparing in AlertLogic
    private static HashMap<String, Sensor> OldData = new HashMap<>();

    //used to store the results from AlertLogic.execute()
    private static ArrayList<TableRow> generalRows = new ArrayList<>();

    //used to store the results from AlertLogic.execute()
    private static ArrayList<TableRow> priorityRows = new ArrayList<>();

    private JFXCheckBox[] check_boxes = new JFXCheckBox[5];

    private static boolean shutdown = false;


    @FXML
    private TableView<TableRow> generalTable;

    @FXML
    private TableColumn<TableRow, String> gDisCol, gLocCol, gSenCol, gMesCol, gTimCol;

    @FXML
    private TableView<TableRow> priorityTable;

    @FXML
    private TableColumn<TableRow, String> pDisCol, pLocCol, pSenCol, pMesCol, pTimCol;


    @FXML
    private JFXCheckBox d1_checkbox, d2_checkbox, d3_checkbox, d4_checkbox, d5_checkbox;

    @FXML
    private JFXSpinner spinner;

    @FXML
    private Text refreshing;

    @FXML
    private JFXSlider volume;

    @FXML
    private MenuItem settings_m, close_m, history_m, help_m;



    @FXML
    void checkClick(ActionEvent event) {
        checkBoxAction();
    }

    /**
     * Set the alarm for when priority events occur
     * */
    MediaPlayer mPlayer;

    /**BUTTON METHODS*/
    public void clear_click(javafx.scene.input.MouseEvent mouseEvent) {
        generalTable.getItems().clear();
        generalRows.clear();
        AlertLogic.genRows.clear();
    }

    public void acknowledge_click(javafx.scene.input.MouseEvent mouseEvent) {
        for(TableRow t:priorityRows) {
            t.setSoundAlert(false);
            t.setFlashAlert(false);
        }
        mPlayer.stop(); //Stop alarm sound
    }

    public void select_all(javafx.scene.input.MouseEvent mouseEvent) {
        Boolean allSelected = true;
        for(JFXCheckBox b:check_boxes) if (b.isSelected() == false) allSelected = false;

        if(allSelected == true){
            for(JFXCheckBox b:check_boxes) b.setSelected(false);
        }else{
            for(JFXCheckBox b:check_boxes) b.setSelected(true);
        }

        checkBoxAction();
    }


    /**
     * checkBoxAction
     * this function cleans the table items and adds them for district boxes are checked.
     * I.e, if a user clicks only district one, it will clean the other district rows off the table.
     */
    public void checkBoxAction(){

       if(!generalTable.getItems().isEmpty()){generalTable.getItems().clear();}
       if(!priorityTable.getItems().isEmpty()){priorityTable.getItems().clear();}

        for(TableRow tg:generalRows){
            try{
                int district = Integer.parseInt(tg.getDistrict());
                if(district >= 0 && district <= 5){
                    if(district == 0) generalTable.getItems().add(tg);
                    else if(check_boxes[Integer.parseInt(tg.getDistrict()) - 1].isSelected()){
                        generalTable.getItems().add(tg);
                    }
                }
            }catch(NumberFormatException | NullPointerException nfe){
                System.out.println("number exception");
                return;
            }
        }
        for(TableRow tp:priorityRows){
            try{
                int district = Integer.parseInt(tp.getDistrict());
                if(district >= 0 && district <= 5){
                    if(district == 0) priorityTable.getItems().add(tp);
                    else if(Logic.settingsEnabled[6]){ //If Lead Operator Mode enabled
                        priorityTable.getItems().add(tp);
                        if(check_boxes[Integer.parseInt(tp.getDistrict()) - 1].isSelected()
                             && tp.isSoundAlert()) mPlayer.play();
                    }
                    else if(check_boxes[Integer.parseInt(tp.getDistrict()) - 1].isSelected()){
                        priorityTable.getItems().add(tp);
                        if(tp.isSoundAlert()) mPlayer.play();
                    }
                }
            }catch(NumberFormatException | NullPointerException nfe){
                System.out.println("number exception");
                return;
            }
        }
    }


    /**
     * Shutdown
     * Called by Main when application is closed. Used to destroy threads
     * */
    public void Shutdown(){
        shutdown = true;
    }


    /**
     * Close_event
     * Menu item: exit
     */
    private EventHandler<ActionEvent> close_event = new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e)
        {
            shutdown = true;
            System.exit(1);
        }
    };

    /**
     * Settings_event
     * Menu item: settings
     */
    private EventHandler<ActionEvent> settings_event = new EventHandler<>() {
        public void handle(ActionEvent e) {

            FXMLLoader settings_window = new FXMLLoader();
            settings_window.setLocation(getClass().getResource("settings.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(settings_window.load());
            } catch (IOException e1) {
                System.out.println("Failed to load settings");
            }
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(scene);
            stage.show();

        }
    };

    /**
     * Help_event
     * Menu item: help
     */
    private EventHandler<ActionEvent> help_event = new EventHandler<>() {
        public void handle(ActionEvent e) {

            FXMLLoader settings_window = new FXMLLoader();
            settings_window.setLocation(getClass().getResource("help.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(settings_window.load());
            } catch (IOException e1) {
                System.out.println("Failed to load help window");
            }
            Stage stage = new Stage();
            stage.setTitle("Help");
            stage.setScene(scene);
            stage.show();

        }
    };

    /**
     * Initialize
     * All the magic happens here
     * */
    @FXML
    public void initialize() throws URISyntaxException {

        File file = new File(Controller.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        File in = new File(file.getParent() + "/para.txt");
        System.out.println(in.getAbsolutePath());

        /**
         * Set volume listener
         */
        volume.valueProperty().addListener((observable, oldValue, newValue)
                -> mPlayer.setVolume(volume.getValue()/100));

        /**
         * Set media player to play audio from parameter rather than default
         */
        Media mp3MusicFile = new Media(getClass().getResource("alert.mp3").toExternalForm());
        //String MP3path = para.getParam("Audio filename");
        //Media mp3MusicFile = new Media(jarPath + MP3path);
        mPlayer = new MediaPlayer(mp3MusicFile);
        mPlayer.setAutoPlay(true);
        mPlayer.setVolume(.75);   // from 0 to 1
        mPlayer.stop();
        mPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                mPlayer.seek(Duration.ZERO);
            }
        });

        /**
         * Set menu item listeners
         */
        close_m.setOnAction(close_event);
        settings_m.setOnAction(settings_event);
        help_m.setOnAction(help_event);


        /**
         * Initialize checkboxes
         */
        check_boxes[0] = d1_checkbox;
        check_boxes[1] = d2_checkbox;
        check_boxes[2] = d3_checkbox;
        check_boxes[3] = d4_checkbox;
        check_boxes[4] = d5_checkbox;
        d1_checkbox.setOnAction(this::checkClick);
        d2_checkbox.setOnAction(this::checkClick);
        d3_checkbox.setOnAction(this::checkClick);
        d4_checkbox.setOnAction(this::checkClick);
        d5_checkbox.setOnAction(this::checkClick);

        for(JFXCheckBox cb:check_boxes){
        cb.setSelected(true);
        }


        /**
         * Initialize cell factories
         */
        gDisCol.setCellValueFactory(new PropertyValueFactory<>("district"));
        gLocCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        gSenCol.setCellValueFactory(new PropertyValueFactory<>("sensor"));
        gMesCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        gTimCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        pDisCol.setCellValueFactory(new PropertyValueFactory<>("district"));
        pLocCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        pSenCol.setCellValueFactory(new PropertyValueFactory<>("sensor"));
        pMesCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        pTimCol.setCellValueFactory(new PropertyValueFactory<>("time"));


        /**
         * Set Row Factory
         * Priority rows set to flash if a row is new and unacknowledged.
         * **/
        priorityTable.setRowFactory(tv -> {
            //Made the mistake of naming TableRow class after Java's TableRow.
            //However, changing everything is more revision than the following line.
            javafx.scene.control.TableRow<TableRow> row = new javafx.scene.control.TableRow<>();
            Timeline flasher = new Timeline(

                    new KeyFrame(Duration.seconds(0.5), e -> {
                        row.getStyleClass().removeAll("table-row-cell", "table-row-cell:odd");
                        row.getStyleClass().add("flashing");

                    }),

                    new KeyFrame(Duration.seconds(1.0), e -> {
                        row.getStyleClass().removeAll("flashing");
                        row.getStyleClass().addAll("table-row-cell", "table-row-cell:odd");
                    })
            );
            flasher.setCycleCount(Animation.INDEFINITE);

            ChangeListener<Boolean> newAlertListener = (obs, cautionWasSet, cautionIsNowSet) -> {
                if (cautionIsNowSet) {
                    flasher.play();
                } else {
                    row.getStyleClass().removeAll("flashing");
                    row.getStyleClass().addAll("table-row-cell", "table-row-cell:odd");
                    flasher.stop();
                }
            };

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (oldItem != null) {
                    oldItem.flashAlertProperty().removeListener(newAlertListener);
                }
                if (newItem == null) {
                    row.getStyleClass().removeAll("flashing");
                    row.getStyleClass().addAll("table-row-cell", "table-row-cell:odd");
                    flasher.stop();
                } else {
                    newItem.flashAlertProperty().addListener(newAlertListener);
                    if (newItem.flashAlertProperty().get()) {
                        flasher.play();
                    } else {
                        flasher.stop();
                    }
                }
            });
            return row ;
        });



        /**
         * Sets the data from wind_closures.txt in AlertLogic, then sets in
         * WindClosures Class. Enable default settings.
         */
        AlertLogic.setWind();
        AlertLogic.enableSettings();


        /**
         * BELOW IS RUNNING PROGRAM
         */

        //Used for Demo testing
        AtomicInteger k = new AtomicInteger();
        k.getAndIncrement();


        /**
         * Set initial data
         */
        SensorUtility s_util = new SensorUtility();
        s_util.setLocations(); //Done only once

        OldData = s_util.getData(k.get()); //OldData won't change in this statement. Only used to initialize SensorData.
        //OldData = s_util.getData();
        spinner.setVisible(false);
        refreshing.setVisible(false);
        AlertLogic.initialSet();


        /**
         * Scheduled update
         * Run every x seconds specified in Parameter file
         * Pull new data, compare it to old data, and create events.
         */
        int delay = parseInt(para.getParam("RefreshTime"));        //NOTE: Delay is in seconds
        ScheduledExecutorService sched = Executors.newScheduledThreadPool(2);
        sched.scheduleAtFixedRate(()->{

            if (shutdown) sched.shutdownNow();

            k.getAndIncrement();
            spinner.setVisible(true);
            refreshing.setVisible(true);
            OldData = s_util.getData(k.get());
            AlertLogic.execute();
            generalRows.clear();
            priorityRows.clear();
            for(int i = 0; i < AlertLogic.genRows.size(); i++){
                generalRows.add(0, AlertLogic.genRows.get(i));
            }
            for(int i = 0; i < AlertLogic.prioRows.size(); i++){
                priorityRows.add(0, AlertLogic.prioRows.get(i));
            }
            checkBoxAction();
            spinner.setVisible(false);
            refreshing.setVisible(false);

        }, delay, delay, TimeUnit.SECONDS);


    } //End of initialize()


    /**
     * Getters and Setters
     */
    public static Parameters getPara() {
        return para;
    }

    public static AlertLogic getLogic() {
        return Logic;
    }

    public static void setLogic(AlertLogic logic) {
        Logic = logic;
    }

    public static HashMap<String, Sensor> getSensorMap() {
        return SensorMap;
    }


    public static HashMap<String, Sensor> getDuplicateSensors() {
        return DuplicateSensors;
    }


    public static HashMap<String, Sensor> getOldData() {
        return OldData;
    }


    public static ArrayList<TableRow> getGeneralRows() {
        return generalRows;
    }


    public static ArrayList<TableRow> getPriorityRows() {
        return priorityRows;
    }


}
