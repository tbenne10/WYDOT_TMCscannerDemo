package sample;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.net.ftp.FTPClient;


//*
// SENSOR UTILITY
// Contains methods to retrieve data from CSV files and parse them into Sensor.
// setLocations(): Creates hash map and sets data, returns a class array for sensor. Also sets the WindClosures document.
// getData(): Reloads the CSV files from server to be compared, returns a class array for sensor.
//




public class SensorUtility {


    private static int BUFFER_SIZE = 4096;

    //static ArrayList<Sensor> oldMap = new ArrayList<>();
    static HashMap<String, Sensor> oldMap;

    //generic sensor for creation
    Sensor tempSensor = new Sensor();

    /**
     *Convert wind from kmh to mph
     * Long to int conversions for exceptionally large values are unnecessary here.
     */
    public String conWind(String val){
        try {
            double d = Double.parseDouble(val);
            d = d * .621371;
            int i = (int) d;
            return Integer.toString(i);
        }
         catch(NumberFormatException nfe){
            return "";
        }
    }

    /**
     * Convert temperature from Celsius * 100 to Fahrenheit
     */
    public String conTemp(String val){
        try {
            double d = Double.parseDouble(val);
            d = ((d/100.0) * (9.0/5.0) ) + 32.0;
            int i = (int) d;
            return Integer.toString(i);
        }
        catch(NumberFormatException nfe){
            return "";
        }

    }


    /**
     * setLocations()
     * Reads sensor_locations.csv, establishes which
     * district and road location each sensor ID is at.
     */
    public void setLocations(){
        try {
            InputStream file = getClass().getResourceAsStream("sensor_locations.csv");
            //File file = new File("sample/sensor_locations.csv");
            String[] token;
            String current_line;
            Scanner inStream = new Scanner(file, "UTF-8");
            inStream.useDelimiter("\n");
            inStream.next(); //Ignore first line
            while (inStream.hasNextLine()) {
                current_line = inStream.next();
                token = current_line.split(",");
                int i = Controller.getSensorMap().size();
                //If a sensor already exists, then we are adding an additional
                //location in a different district for it.
                tempSensor = new Sensor();
                if(Controller.getSensorMap().containsKey(token[1]) && token[0].equals("")) {
                    tempSensor = Controller.getSensorMap().get(token[1]);
                    if(!token[3].equals("")){tempSensor.getSenId().add(token[3]);}
                    if(!token[5].equals("")){tempSensor.getAdditional_info().add(token[5]);}
                    Controller.getSensorMap().put(token[1], tempSensor);

                }else{

                    if(!token[0].equals("")){tempSensor.setDistrict(token[0]);}
                    if(!token[1].equals("")){tempSensor.setSiteid(token[1]);}
                    if(!token[2].equals("")){tempSensor.setTown_name(token[2]);}
                    if(!token[3].equals("")){tempSensor.getSenId().add(token[3]);}
                    if(!token[4].equals("")){tempSensor.setSensor_location(token[4]);}
                    if(!token[5].equals("")){tempSensor.getAdditional_info().add(token[5]);}

                    if(Controller.getSensorMap().containsKey(token[1])){
                        Controller.getDuplicateSensors().put(token[1], tempSensor);
                    }else {
                        Controller.getSensorMap().put(token[1], tempSensor);
                    }
                }


            }

        } catch (Exception e) {
            System.out.println("Error in setLocations");
        }
    }


    /**
     * getData(i)
     * @param i used in testing to iterate example data files
     * This retrieves updated sensor values. Old values returned as oldMap
     * SensorMap and oldMap are processed in AlertLogic
     */
    public HashMap<String, Sensor> getData(int i){
        //Clone current map to old map
        oldMap = new HashMap<>();
        //oldMap.putAll(Controller.SensorMap);
        for(Map.Entry<String, Sensor> entry : Controller.getSensorMap().entrySet()){
            Sensor s = new Sensor(entry.getValue());
            /**
             * Don't use overridden equals here, it returns null for each value.
             */
            oldMap.put(entry.getKey(), s);
        }

        /**
         * Parse CSV 1
         */
        String[] token;
        String current_line;
        i = i+1;
        InputStream file1 = getClass().getResourceAsStream("sensorFolder/w1_" + i +".csv");

        Scanner inStream = new Scanner(file1, "UTF-8");
        inStream.useDelimiter("\n");
        inStream.next(); //Ignore first line
        while (inStream.hasNext()) {
            current_line = inStream.next();
            if (!current_line.isEmpty()) {
                token = current_line.split(",", -1);
                if(Controller.getSensorMap().containsKey(token[0])) {
                    tempSensor = Controller.getSensorMap().get(token[0]);
                    if (!token[1].isEmpty()) { tempSensor.setDtTm(token[1]); }
                    if (!token[2].isEmpty()) { tempSensor.setAirTemp(conTemp(token[2])); }
                    if (!token[3].isEmpty()) { tempSensor.setDewpoint(token[3]); }
                    if (!token[4].isEmpty()) { tempSensor.setRh(token[4]); }
                    if (!token[5].isEmpty()) { tempSensor.setSpdAvg(conWind(token[5])); }
                    if (!token[6].isEmpty()) { tempSensor.setSpdGust(conWind(token[6])); }
                    if (!token[7].isEmpty()) { tempSensor.setDirMin(token[7]); }
                    if (!token[8].isEmpty()) { tempSensor.setDirAvg(token[8]); }
                    if (!token[9].isEmpty()) { tempSensor.setDirMax(token[9]); }
                    if (!token[10].isEmpty()) { tempSensor.setPressure(token[10]); }
                    /**
                     * IMPORTANT NOTE: Demo files have intensity / type mixed up compared to
                     * actual data retrieved from FTP file.
                     */
                    if (!token[11].isEmpty()) { tempSensor.setPcIntens(token[12]); }
                    if (!token[12].isEmpty()) { tempSensor.setPcType(token[11]); }
                    if (!token[13].isEmpty()) { tempSensor.setPcRate(token[13]); }
                    if (!token[14].isEmpty()) { tempSensor.setPcAccum(token[14]); }
                    if (!token[15].isEmpty()) { tempSensor.setVisibility(token[15]); }
                    Controller.getSensorMap().put(token[0], tempSensor);
                }
            }
        }

        /**
         * Clear each arraylist to avoid repeats each iteration
         * Note: It seems intuitive to do this while parsing CSV2, but
         * a key may appear multiple times so it would wipe data that had just
         * been added.
         */
        for(Map.Entry<String, Sensor> entry : Controller.getSensorMap().entrySet()) {
            tempSensor = entry.getValue();
            tempSensor.getSenId().clear();
            tempSensor.getSfcond().clear();
            tempSensor.getSftemp().clear();
            tempSensor.getFrztemp().clear();
            tempSensor.getChemfactor().clear();
            tempSensor.getChempct().clear();
            tempSensor.getDepth().clear();
            tempSensor.getIcepct().clear();
            tempSensor.getSubsftemp().clear();
            tempSensor.getWaterlevel().clear();
            Controller.getSensorMap().put(entry.getKey(), tempSensor);
        }


        /**
         * Parse CSV 2
         */
        InputStream file2 = getClass().getResourceAsStream("sensorFolder/w2_" + i +".csv");
        Scanner inStream2 = new Scanner(file2, "UTF-8");

        inStream2.useDelimiter("\n");
        inStream2.next(); //Ignore first line
        while (inStream2.hasNext()) {
            current_line = inStream2.next();
            token = current_line.split(",", -1);

            if(Controller.getSensorMap().containsKey(token[0])){
                tempSensor = Controller.getSensorMap().get(token[0]);
                if (!token[1].isEmpty()) { tempSensor.getSenId().add(token[1]); }
                if (!token[3].isEmpty()) { tempSensor.getSfcond().add(token[3]); } //skipped date and time
                if (!token[4].isEmpty()) { tempSensor.getSftemp().add(conTemp(token[4])); }
                if (!token[5].isEmpty()) { tempSensor.getFrztemp().add(conTemp(token[5])); }
                if (!token[6].isEmpty()) { tempSensor.getChemfactor().add(token[6]); }
                if (!token[7].isEmpty()) { tempSensor.getChempct().add(token[7]); }
                if (!token[8].isEmpty()) { tempSensor.getDepth().add(token[8]); }
                if (!token[9].isEmpty()) { tempSensor.getIcepct().add(token[9]); }
                if (!token[10].isEmpty()) { tempSensor.getSubsftemp().add(conTemp(token[10])); }
                if (!token[11].isEmpty()) { tempSensor.getWaterlevel().add(token[11]); }
                Controller.getSensorMap().put(token[0], tempSensor);
            }

        }
        return oldMap;

    }



}



