package sample;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.sun.javafx.tools.packager.Param;
import org.apache.commons.net.ftp.FTPClient;


/**
// SENSOR UTILITY
// Contains methods to retrieve data from CSV files and parse them into Sensor.
// setLocations(): Creates hash map and sets data, returns a class array for sensor. Also sets the WindClosures document.
// getData(): Reloads the CSV files from server to be compared, returns a class array for sensor.
*/
public class SensorUtility {

    //generic sensor for creation
    private Sensor tempSensor = new Sensor();

    static HashMap<String, Sensor> oldMap;

    private Parameters para = new Parameters();

    /**
     *Convert wind from kmh to mph
     * Long to int conversions for exceptionally large values are unnecessary here.
     */
    private String conWind(String val){
        try {
            double d = Double.parseDouble(val);
            d = d * .621371;
            int i = (int) d;
            return Integer.toString(i);
        }
         catch(NumberFormatException nfe){
            System.out.println("Error in converting wind");
            nfe.printStackTrace();
            return "";
        }
    }

    /**
     * Convert temperature from Celsius * 100 to Fahrenheit
     */
    private String conTemp(String val){
        try {
            double d = Double.parseDouble(val);
            d = ((d/100.0) * (9/5) ) + 32.0;
            int i = (int) d;
            return Integer.toString(i);
        }
        catch(NumberFormatException nfe){
            System.out.println("Error in converting wind");
            nfe.printStackTrace();
            return "";
        }

    }


    /**
     * setLocations()
     * Reads sensor_locations.csv, establishes which
     * district and road location each sensor ID is at.
     */
    void setLocations(){
        try {
            InputStream file = getClass().getResourceAsStream("sensor_locations.csv");
            //File file = new File(Controller.jarPath + "sensor_locations.csv");
            String[] token;
            String current_line;
            Scanner inStream = new Scanner(file, String.valueOf(StandardCharsets.UTF_8));
            inStream.useDelimiter("\n");
            inStream.next(); //Ignore first line
            while (inStream.hasNextLine()) {
                current_line = inStream.next();
                token = current_line.split(",");
                //If a sensor already exists, then we are adding an additional
                //location in a different district for it.
                tempSensor = new Sensor();
                if(Controller.SensorMap.containsKey(token[1])){
                    tempSensor = Controller.SensorMap.get(token[1]);
                    if(token[0].equals("")){
                        //Same sensor, different sensor section
                        //Format: ,178025,, 1,, SB passing lane
                        if(!token[3].equals("")){tempSensor.getSenId().add(token[3]);}
                        if(!token[5].equals("")){tempSensor.getAdditional_info().add(token[5]);}
                    }else{
                        //Same sensor, different district
                        //Format: 2,178001,,,,-
                        if(!token[0].equals("")){tempSensor.getDistrict().add(token[0]);}
                    }
                    Controller.SensorMap.put(token[1], tempSensor);
                }else{
                    //New Sensor
                    //Format: 1,178025,Cheyenne, 0, I-25 NB bridge deck MP 4, SB driving lane
                    if(!token[0].equals("")){tempSensor.getDistrict().add(token[0]);}
                    if(!token[1].equals("")){tempSensor.setSiteid(token[1]);}
                    if(!token[2].equals("")){tempSensor.setTown_name(token[2]);}
                    if(!token[3].equals("")){tempSensor.getSenId().add(token[3]);}
                    if(!token[4].equals("")){tempSensor.setSensor_location(token[4]);}
                    if(!token[5].equals("")){tempSensor.getAdditional_info().add(token[5]);}
                    Controller.SensorMap.put(token[1], tempSensor);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in setLocations");
            e.printStackTrace();
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
        for(Map.Entry<String, Sensor> entry : Controller.SensorMap.entrySet()){
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
                if(Controller.SensorMap.containsKey(token[0])) {
                    tempSensor = Controller.SensorMap.get(token[0]);
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
                    Controller.SensorMap.put(token[0], tempSensor);
                }
            }
        }

        /**
         * Clear each arraylist to avoid repeats each iteration
         * Note: It seems intuitive to do this while parsing CSV2, but
         * a key may appear multiple times so it would wipe data that had just
         * been added.
         */
        for(Map.Entry<String, Sensor> entry : Controller.SensorMap.entrySet()) {
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
            Controller.SensorMap.put(entry.getKey(), tempSensor);
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

            if(Controller.SensorMap.containsKey(token[0])){
                tempSensor = Controller.SensorMap.get(token[0]);
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
                Controller.SensorMap.put(token[0], tempSensor);
            }

        }
        return oldMap;

    }


    /**
     * FtpFileDownload
     * Retrieve data from FTP server
     */
    private void FtpFileDownload(){
        FTPClient client = new FTPClient();
        FileOutputStream fos1 = null;
        FileOutputStream fos2 = null;
        try {
            client.connect(para.getParam("FTP Link"));
            client.login(para.getParam("FTP User"), para.getParam("FTP Pass"));
            client.listFiles();
            // Create an OutputStream for the file
            fos1 = new FileOutputStream(System.getProperty("user.dir") + "/weather_sensor1.csv");
            fos2 = new FileOutputStream(System.getProperty("user.dir") + "/weather_sensor2.csv");
            // Fetch file from server
            client.retrieveFile("/" + "weather_sensor1.csv", fos1);
            client.retrieveFile("/" + "weather_sensor2.csv", fos2);
        } catch (IOException e) {
            System.out.println("Failed to connect to client");
            e.printStackTrace();
        } finally {
            try {
                if (fos1 != null) {
                    fos1.close();
                }
                if (fos2 != null) {
                    fos2.close();
                }
                client.disconnect();
            } catch (IOException e) {
                System.out.println("Failed to disconnect from client");
                e.printStackTrace();
            }
        }
    }




}




