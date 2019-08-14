package sample;

import javafx.scene.control.Alert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Tests {

    SensorUtility su = new SensorUtility();

    @Test
    void conWind() {
        assertEquals(su.conWind("50"), "31");
        assertEquals(su.conWind("-10"), "-6");
        assertEquals(su.conWind("") ,"");
        assertEquals(su.conWind("aa") ,"");

    }

    @Test
    void conTemp() {
        assertEquals(su.conTemp("0") ,"32");
        assertEquals(su.conTemp("10000") ,"212");
        assertEquals(su.conTemp("-2500"), "-13");
        assertEquals(su.conWind("") ,"");
        assertEquals(su.conTemp("aa") ,"");
    }



}