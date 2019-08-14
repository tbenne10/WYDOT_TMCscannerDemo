package sample;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;




public class Parameters {
    private String current_line = null;
    private String[] token_values;
    private HashMap<String, String> map = new HashMap<>();


    /*Constructor*/
    /*Reads para.rtf and puts each value in hash map*/
    Parameters(){
        try {
            InputStream in = getClass().getResourceAsStream("para.txt");
            Scanner inStream = new Scanner(in, "UTF-8");
            inStream.useDelimiter("\n");
            while (inStream.hasNext()) {
                current_line = inStream.next();
                token_values = current_line.split(":-");
                map.put(token_values[0], token_values[1]);
            }
        } catch (Exception e) {
           System.out.println("Error in Parameters");
        }
    }

    /*getParam Function
    * Pulls any parameter value from para.txt and returns the value
    * */
    public String getParam(String s){
        return map.get(s);
        //TODO: Error call if parameter does not exist
    }

}
