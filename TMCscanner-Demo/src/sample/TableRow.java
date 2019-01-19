package sample;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class TableRow {
    
    private String district;
    private String location;
    private String sensor;
    private String message;
    private String time;


    //Priority rows will flash if this is true
    //Dependent on settings
    //Acknowledge button sets to false
    BooleanProperty soundAlert = new SimpleBooleanProperty( true );
    BooleanProperty flashAlert = new SimpleBooleanProperty( true );



    public TableRow(){
        this.district = "";
        this.location = "";
        this.sensor = "";
        this.message = "";
        this.time = "";
        this.setSoundAlert(true);
        this.setFlashAlert(true);

    }

    public TableRow(String a, String b, String c, String d, String e){
        this.district = a;
        this.location = b;
        this.sensor = c;
        this.message = d;
        this.time = e;
        this.setSoundAlert(true);
        this.setFlashAlert(true);
    }



    public boolean isFlashAlert() {
        return flashAlert.get();
    }

    public BooleanProperty flashAlertProperty() {
        return flashAlert;
    }

    public void setFlashAlert(boolean flashAlert) {
        this.flashAlert.set(flashAlert);
    }

    public boolean isSoundAlert() {
        return soundAlert.get();
    }

    public BooleanProperty soundAlertProperty() {
        return soundAlert;
    }

    public void setSoundAlert(boolean newAlert) {
        this.soundAlert.set(newAlert);
    }


    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void printTR(){
        System.out.println(" " + this.district + " " + this.location + " " + this.sensor +
                " " + this.message + " " + this.time);

    }



}
