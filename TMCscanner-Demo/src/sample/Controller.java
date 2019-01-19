package sample;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;



public class Controller{


    public static Logger LOGGER;

    //Public SensorMap: maps sensor ID with relative sensor class object
    public static HashMap<String, Sensor> SensorMap = new HashMap<>();

    //Public OldData: Used for comparing in AlertLogic
    public static HashMap<String, Sensor> OldData = new HashMap<>();

    //used to store the results from AlertLogic.execute()
    public static ArrayList<TableRow> generalRows = new ArrayList<>();

    //used to store the results from AlertLogic.execute()
    public static ArrayList<TableRow> priorityRows = new ArrayList<>();

    private CheckBox[] check_boxes = new CheckBox[5];

    private static boolean shutdown = false;

    public static String jarPath;


    @FXML
    private TableView<TableRow> generalTable;

    @FXML
    private TableColumn<TableRow, String> gDisCol, gLocCol, gSenCol, gMesCol, gTimCol;

    @FXML
    private TableView<TableRow> priorityTable;

    @FXML
    private TableColumn<TableRow, String> pDisCol, pLocCol, pSenCol, pMesCol, pTimCol;


    @FXML
    private CheckBox d1_checkbox, d2_checkbox, d3_checkbox, d4_checkbox, d5_checkbox;

    @FXML
    private Text refreshing;

    @FXML
    private Slider volume;

    @FXML
    private MenuItem settings_m, close_m, help_m;



    @FXML
    private void checkClick(ActionEvent event) {
        checkBoxAction();
    }

    /**
     * Set the alarm for when priority events occur
     * */
    private MediaPlayer mPlayer;


    /**BUTTON METHODS*/
    public void clear_click(){
        generalTable.getItems().clear();
        generalRows.clear();
        AlertLogic.genRows.clear();
    }

    public void acknowledge_click() {
        for(TableRow t:priorityRows){
            t.setFlashAlert(false);
            t.setSoundAlert(false);
        }
        mPlayer.stop(); //Stop alarm sound
    }

    public void select_all() {
        boolean allSelected = true;
        for(CheckBox b:check_boxes) if (!b.isSelected()) allSelected = false;

        if(allSelected){
            for(CheckBox b:check_boxes) b.setSelected(false);
        }else{
            for(CheckBox b:check_boxes) b.setSelected(true);
        }

        checkBoxAction();
    }


    /**
     * checkBoxAction
     * this function cleans the table items and adds them for district boxes are checked.
     * I.e, if a user clicks only district one, it will clean the other district rows off the table.
     */
    private void checkBoxAction(){

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
                nfe.printStackTrace();
                return;
            }
        }
        for(TableRow tp:priorityRows){
            try{
                int district = Integer.parseInt(tp.getDistrict());
                if(district >= 0 && district <= 5){
                    if(district == 0){
                        priorityTable.getItems().add(tp);
                        if(tp.isSoundAlert()) mPlayer.play();
                        tp.setSoundAlert(false);
                    }
                    else if(AlertLogic.settingsEnabled[6]){ //If Lead Operator Mode enabled
                        priorityTable.getItems().add(tp);
                        if(check_boxes[Integer.parseInt(tp.getDistrict()) - 1].isSelected()
                                && tp.isSoundAlert()){
                            mPlayer.play();
                            tp.setSoundAlert(false);
                        }
                    }
                    else if(check_boxes[Integer.parseInt(tp.getDistrict()) - 1].isSelected()){ //default
                        priorityTable.getItems().add(tp);
                        if(tp.isSoundAlert()) mPlayer.play();
                        tp.setSoundAlert(false);
                    }
                }
            }catch(NumberFormatException | NullPointerException nfe){
                System.out.println("number exception");
                nfe.printStackTrace();
                return;
            }
        }
    }


    /**
     * Shutdown
     * Called by Main when application is closed. Used to destroy threads
     * */
    void Shutdown(){
        shutdown = true;
    }


    /**
     * Close_event
     * Menu item: exit
     */
    private EventHandler<ActionEvent> close_event = e -> {
        shutdown = true;
        System.exit(1);
    };

    /**
     * Settings_event
     * Menu item: exit
     */
    private EventHandler<ActionEvent> settings_event = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
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
    private EventHandler<ActionEvent> help_event = new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e) {

            FXMLLoader settings_window = new FXMLLoader();
            settings_window.setLocation(getClass().getResource("help.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(settings_window.load());
            } catch (IOException e1) {
                e1.printStackTrace();
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
    public void initialize() throws IOException, URISyntaxException {

        /**
         * Set path of jar file where files are located
         */
        File file = new File(Controller.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        jarPath = file.getParent() + File.separator + "Resources" + File.separator;
        System.setProperty("userApp.root", jarPath);
        System.out.println(jarPath);


        /**
         * Declare new Parameters class for reading para.txt
         */
        Parameters para = new Parameters();

        /**
         * Configure Logger
         */
        LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        try{
        LoggerUtil.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
        LOGGER.info(">....Application Start");

        /**
         * Set media player to play audio from parameter rather than default
         */
        Media mp3MusicFile = new Media(getClass().getResource("alert.mp3").toExternalForm());
        //TODO: Fix pathing of sound file
        //String MP3path = para.getParam("Audio filename");
        //Media mp3MusicFile = new Media(jarPath + MP3path);
        mPlayer = new MediaPlayer(mp3MusicFile);
        mPlayer.setAutoPlay(true);
        mPlayer.setVolume(.75);   // from 0 to 1
        mPlayer.stop();
        mPlayer.setOnEndOfMedia(() -> mPlayer.seek(Duration.ZERO));

        /**
         * Set volume listener
         */
        volume.valueProperty().addListener((observable, oldValue, newValue)
                -> mPlayer.setVolume(volume.getValue()/100));

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

        for(CheckBox cb:check_boxes){
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
            refreshing.setVisible(false);

        }, delay, delay, TimeUnit.SECONDS);

        /**
         * Error Thread
         * If system is in error for a given time, shut down the program.
         * Waits 45 seconds after error occurs
         */
        AtomicInteger errorcount = new AtomicInteger();
        ScheduledExecutorService errorChecker = Executors.newScheduledThreadPool(2);
        errorChecker.scheduleAtFixedRate(()->{
            if(shutdown) errorChecker.shutdownNow();;
            if(refreshing.isVisible()){
                errorcount.getAndIncrement();
            }else{
                errorcount.set(0);
            }
            if(errorcount.get() >= 3){
                Shutdown();
                System.exit(1);
            }
        }, 5, 15, TimeUnit.SECONDS);


    } //End of initialize()

}
