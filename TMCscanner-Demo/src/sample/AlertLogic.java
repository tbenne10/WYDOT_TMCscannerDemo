package sample;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * ALERTLOGIC
 * This class does all the logic for changes in the sensor.
 * I.E, determine if Wind Gusts have gone above 40+ mph and make an alert.
 */
public class AlertLogic {

    //In class event lists
    private static ArrayList<String> wind40 = new ArrayList<>();
    private static ArrayList<String> lowVis = new ArrayList<>();
    private static ArrayList<String> watchBI = new ArrayList<>();
    private static HashMap<String,Timer> timed_events = new HashMap<>();

    //These are words we don't want to use, can be added on.
    private static Set<String> nullTerms = new HashSet<>(Arrays.asList("", "NULL", "Error","Other"," ", null));

    //This is just a changeable TableRow to prevent reuse in some functions below.
    private static TableRow genericTR = new TableRow();

    //These are to store contents to be shown on screen in Controller
    static ArrayList<TableRow> genRows = new ArrayList<>();
    static ArrayList<TableRow> prioRows = new ArrayList<>();

    static HashMap<String, Integer> AssignedClosures = new HashMap<>(); //<Sensor ID, SectionID>
    static HashMap<Integer, WindClosures> WindClosureSections = new HashMap<>(); //<SectionID, WindClosures object>

    public static Parameters para = new Parameters();

    //Stores the settings created from settingsController
    public static Boolean settingsEnabled[] = new Boolean[7];

    //generic sensor
    private static Sensor tempSensor, oldSensor;


    /**
     * Class Constructor
     * sets the wind closures list
     */
    static void setWind(){
        try {
            int sectionID = 0;
            InputStream file1 = AlertLogic.class.getResourceAsStream("wind_closures.txt");
            String[] token_values;
            String current_line;
            Scanner inStream = new Scanner(file1, "UTF-8");
            inStream.useDelimiter("\n");
            inStream.next(); //Ignore first line
            while(inStream.hasNextLine()) {
                current_line = inStream.next();
                if (!current_line.isEmpty()){
                    token_values = current_line.split(",", -1);
                    WindClosures W = new WindClosures();
                    W.setHasEBOR(false);
                    W.setMessageName(token_values[1]);
                    WindClosureSections.put(sectionID, W);
                    int length = Integer.parseInt(token_values[0]);
                    for (int i = 0; i < length; i++) {
                        current_line = inStream.next();
                        AssignedClosures.put(current_line, sectionID);
                    }
                    sectionID++;
                }

            }
        } catch (NumberFormatException e) {
            System.out.println("Error in setWind");
        }
    }

    /**
     * enableSettings
     * Sets default settings on program start
     */
    static void enableSettings(){
        for (int i = 0; i <  settingsEnabled.length; i++)
            settingsEnabled[i] = false;
    }

    /**
     * timedEvent
     * Operates as a "timed hash set" for the EBORs
     */
    public static void timedEvent(String key){

        Sensor s = Controller.getSensorMap().get(key);
        System.out.println(" EBOR " + s.getTown_name() + " " + s.getSensor_location());
        System.out.println("Event created at " + Instant.now());
        if(!timed_events.containsKey(key)) {
            if (AssignedClosures.containsKey(key)) {
                int acKey = AssignedClosures.get(key);
                WindClosures tempW = WindClosureSections.get(acKey);
                if (tempW.getEborCount() == 0) {
                    setTableRow(key, prioRows, "Dangerous Wind 60+ MPH (@"
                            + s.getSpdGust() + " MPH)", true);
                    setTableRow(prioRows, "Close road section "
                            + tempW.getMessageName(), true);
                }
                tempW.incrementEborCount();
                WindClosureSections.put(acKey, tempW);
            } else {
                setTableRow(key, prioRows, "Dangerous Wind 60+ MPH (@"
                        + s.getSpdGust() + " MPH)", true);
            }
        }else{
            timed_events.remove(key);
        }

        TimerTask task = new TimerTask() {
            public void run() {
                if (!AssignedClosures.containsKey(key)) {
                    setTableRow(key, prioRows, "Drop Dangerous Winds (@"
                            + s.getSpdGust() + " MPH)", true);
                } else {
                    int acKey = AssignedClosures.get(key);
                    WindClosures tempW = WindClosureSections.get(acKey);
                    tempW.decrementEborCount();
                    WindClosureSections.put(acKey, tempW);
                    if (tempW.getEborCount() <= 0) {
                        setTableRow(key, prioRows, "Remove Dangerous Wind (@"
                                + s.getSpdGust() + " MPH)", true);
                        setTableRow(prioRows, "Open road section "
                                + tempW.getMessageName(), true);
                    }
                }
            }
        };
        Timer timer = new Timer(key);

        long delay = Long.parseLong(para.getParam("EBOR")) * 60000L;
        timer.schedule(task, delay);
        timed_events.put(key, timer);
    }




    /**
     * setTableRow
     * Creates a row of elements to add to Tableview
     * @param key -- id of a sensor, i.e 178008
     * @param rowType -- "genRows" or "prioRows"
     * @param message -- Event to read to user, such as "Drop black ice"
     * @param setAlarm -- If settings specify, choose to/not to ring alarm on event
     */
    public static void setTableRow(String key, ArrayList<TableRow> rowType, String message, Boolean setAlarm){
        try {
            //Set time parameter
            Date dNow = new Date( );
            SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

            Sensor sen = Controller.getSensorMap().get(key);
            genericTR = new TableRow();
            genericTR.setDistrict(sen.getDistrict());
            genericTR.setLocation(sen.getTown_name());
            genericTR.setSensor(sen.getSensor_location());
            genericTR.setMessage(message);
            genericTR.setTime(ft.format(dNow));
            genericTR.setSoundAlert(setAlarm);
            rowType.add(genericTR);

            if(Controller.getDuplicateSensors().containsKey(key)){
                sen = Controller.getDuplicateSensors().get(key);
                genericTR.setDistrict(sen.getDistrict());
                genericTR.setLocation(sen.getTown_name());
                genericTR.setSensor(sen.getSensor_location());
                genericTR.setMessage(message);
                genericTR.setTime(ft.format(dNow));
                genericTR.setSoundAlert(setAlarm);
                rowType.add(genericTR);
            }

        } catch (Exception e) {
            System.out.println("Error in setTableRow");
        }

    }

    /**
     * Overloaded setTableRow
     * Creates a row of elements to add to Tableview SPECIFIC TO DROPPING ROAD SECTION CLOSURES
     * @param rowType -- "genRows" or "prioRows"
     * @param message -- Event to read to user, such as "Drop black ice"
     */
    public static void setTableRow(ArrayList<TableRow> rowType, String message, Boolean setAlarm){
        //Set time parameter
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

        try {
            //loop districts and create TR
            //Note: this creates the message for all districts. May want to change later.
            genericTR = new TableRow();
            genericTR.setDistrict("0");
            genericTR.setLocation(" ");
            genericTR.setSensor(" ");
            genericTR.setMessage(message);
            genericTR.setTime(ft.format(dNow));
            genericTR.setSoundAlert(setAlarm);
            rowType.add(genericTR);
        } catch (Exception e) {
            System.out.println("Error in setTableRow");
        }
    }






    /**
     * InitialSet
     * If a user opens the window for the first time, we don't want all the existing events to suddenly appear.
     * This stores them so that they won't be read immediately, but may interact with future events.
     */
    public static void initialSet() {
        try {
            for(Map.Entry<String, Sensor> entry : Controller.getSensorMap().entrySet()){
                tempSensor = entry.getValue();
                String key = entry.getKey();

                if (!nullTerms.contains(tempSensor.getSpdGust())){
                    int speed = Integer.parseInt(tempSensor.getSpdGust());
                    if(speed >= 40 && speed < 60){
                        wind40.add(key);
                    }else if(speed >= 60 && speed < 120){
                        timedEvent(key);
                    }
                }

                if (!nullTerms.contains(tempSensor.getVisibility())){
                    int visibility = Integer.parseInt(tempSensor.getVisibility());
                    if(visibility <= 400 && visibility > 0){
                        lowVis.add(key);
                    }
                }

                Boolean hasBI = false;
                String precipitation = tempSensor.getPcType();
                String rh = tempSensor.getRh();
                for (int i = 0; i < tempSensor.getSftemp().size(); i++) {
                    String surfaceCon = tempSensor.getSfcond().get(i);
                    String surfaceTemp = tempSensor.getSftemp().get(i);

                    if (precipitation.equals("Rain")
                            || precipitation.equals("Snow")
                            || surfaceCon.equals("Wet")) {
                        if (!nullTerms.contains(surfaceTemp)) {
                            int sfTemp = Integer.parseInt(surfaceTemp);
                            if (sfTemp <= 32) hasBI = true;
                        }
                        if (!nullTerms.contains(rh)) {
                            int relHum = Integer.parseInt(rh);
                            if (relHum > 90) hasBI = true;
                        }
                    }
                }
                if(hasBI) watchBI.add(key);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error in initialSet");
        }
    }



    /**
     * Execute
     * Primary function of this class.
     * Compare changes from old and new data, and alert for significant changes.
     */
    public static void execute() {
        try {
            for(Map.Entry<String, Sensor> entry : Controller.getSensorMap().entrySet()){
                tempSensor = entry.getValue();
                String key = entry.getKey();
                oldSensor = Controller.getOldData().get(key);

                if (!nullTerms.contains(tempSensor.getSpdGust())){
                    int newSpeed = Integer.parseInt(tempSensor.getSpdGust());
                    int oldSpeed = Integer.parseInt(oldSensor.getSpdGust());
                    if(newSpeed >= 60 && newSpeed < 120){ //Do event regardless of old speed
                        timedEvent(key);
                    }else if((newSpeed < 60 && newSpeed >= 50) && timed_events.containsKey(key)){
                        //in EBOR but not below 50
                        timedEvent(key);
                    }else if(newSpeed < 60 && !timed_events.containsKey(key)) {
                        if(!settingsEnabled[1]) {
                            if(newSpeed >= 45 && oldSpeed < 60){
                                int dif = abs(newSpeed - oldSpeed);
                                if(dif >= 5){
                                    setTableRow(key, genRows, "Wind Gusts changed from "
                                            + oldSpeed + " to " + newSpeed, false);
                                }
                            }
                        }
                        if( newSpeed >= 40 && oldSpeed < 40 && !wind40.contains(key)){
                            setTableRow(key, prioRows, "Wind Gusts 40+ MPH (@"
                                    + newSpeed + " MPH)", !settingsEnabled[3]);
                            wind40.add(key);
                        } else if( newSpeed < 30 && wind40.contains(key)){
                            if(!settingsEnabled[2]){
                                setTableRow(key, genRows, "Drop High Wind (@"
                                        + newSpeed + " MPH)", false);
                            }else{
                                setTableRow(key, prioRows, "Drop High Wind (@"
                                        + newSpeed + " MPH)", false);
                            }
                            wind40.remove(key);
                        }
                    }
                }

                if (!nullTerms.contains(tempSensor.getVisibility())){
                    int newVis = Integer.parseInt(tempSensor.getVisibility());
                    if(newVis <= 400 && newVis > 0  && !lowVis.contains(key)){
                        lowVis.add(key);
                        setTableRow(key, prioRows, "Low Visibility (@"
                                + newVis + " ft.)", !settingsEnabled[5]);
                    }else if(newVis >= 720 && lowVis.contains(key)){
                        lowVis.remove(key);
                        if(!settingsEnabled[2]) {
                            setTableRow(key, genRows, "Remove Low Visibility (@"
                                    + newVis + " ft.)", false);
                        }else{
                            setTableRow(key, prioRows, "Remove Low Visibility (@"
                                    + newVis + " ft.)", !settingsEnabled[5]);
                        }
                    }
                }


                Boolean hasBI = false;
                String newPrecipitation = tempSensor.getPcType();
                String oldPrecipitation = oldSensor.getPcType();
                String newIntensity= tempSensor.getPcIntens();
                String oldIntensity= oldSensor.getPcIntens();
                String rh = tempSensor.getRh();
                String prioSensor = "None";
                String addOn = "";

                if(!nullTerms.contains(newIntensity)
                        && !nullTerms.contains(oldIntensity)
                        && !newPrecipitation.equals(oldPrecipitation)) {
                    if (!(newPrecipitation.equals("")
                            || newPrecipitation.equals(" ")
                            || newPrecipitation.equals("None"))){
                        addOn = ":" + newIntensity;
                    }
                    if (!(oldPrecipitation.equals("") || oldPrecipitation.equals(" "))) {
                        prioSensor = oldPrecipitation;
                    }
                    if (newPrecipitation.equals("") || newPrecipitation.equals(" ")) {
                        newPrecipitation = "None";
                    }
                    if(!settingsEnabled[0]) {
                        setTableRow(key, genRows, "Precipitation changed from " +
                                prioSensor + " to " + newPrecipitation + addOn, false);
                    }
                }

                for (int i = 0; i < tempSensor.getSftemp().size(); i++) {
                    String newSurfaceCon = tempSensor.getSfcond().get(i);
                    String surfaceTemp = tempSensor.getSftemp().get(i);
                    if (newPrecipitation.equals("Rain")
                            || newPrecipitation.equals("Snow")
                            || newSurfaceCon.equals("Wet")) {
                        if (!nullTerms.contains(surfaceTemp)) {
                            int sfTemp = Integer.parseInt(surfaceTemp);
                            if (sfTemp <= 32) hasBI = true;
                        }
                        if (!nullTerms.contains(rh)) {
                            int relHum = Integer.parseInt(rh);
                            if (relHum > 90) hasBI = true;
                        }
                    }
                }
                if(hasBI && !watchBI.contains(key)){
                    watchBI.add(key);
                    setTableRow(key, prioRows, "Watch for Black Ice", !settingsEnabled[4]);
                }else if(!hasBI && watchBI.contains(key)){
                    watchBI.remove(key);
                    if(!settingsEnabled[2]){
                    setTableRow(key, genRows, "Remove Black Ice Warning", false);
                    }else{
                        setTableRow(key, prioRows, "Remove Black Ice Warning", !settingsEnabled[4]);
                    }
                }


            }
        } catch (NumberFormatException e) {
            System.out.println("Error in execute");
        }
    }

}





