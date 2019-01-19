package sample;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;




public class Parameters {
    private HashMap<String, String> map = new HashMap<>();


    /*Constructor*/
    /*Reads para.txt and puts each value in hash map*/
    Parameters(){
        try {
            //InputStream in = getClass().getResourceAsStream("para.txt");
            InputStream in = getClass().getResourceAsStream("para.txt");
            Scanner inStream = new Scanner(in, String.valueOf(StandardCharsets.UTF_8));
            inStream.useDelimiter("\n");
            while (inStream.hasNext()) {
                String current_line = inStream.next();
                String[] token_values = current_line.split(":-");
                map.put(token_values[0], token_values[1]);
            }
        } catch (Exception e) {
           System.out.println("Error in Parameters");
           e.printStackTrace();
        }
    }

    /*getParam Function
    * Pulls any parameter value from para.txt and returns the value
    * */
    String getParam(String s){
        return map.get(s);
    }

}
