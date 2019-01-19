package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;

import java.io.IOException;

import static sample.AlertLogic.*;

/**
 * SETTINGS:
 * 1. Disable Precipitation changes
 * 2. Disable Wind Incrementation
 * 3. Make Drop Alerts Priority
 * 4. Disable Wind Alarm Sound
 * 5. Disable Black Ice Alarm Sound
 * 6. Disable Low-Visibility Alarm Sound
 * 7. Lead Operator Mode
 */

public class settingsController {

    @FXML
    private CheckBox set_1, set_2, set_3, set_4, set_5, set_6, set_7;

    boolean[] settingsEnabled = new boolean[7];

    EventHandler<ActionEvent> set = e -> {
        settingsEnabled[0] = set_1.isSelected();
        settingsEnabled[1] = set_2.isSelected();
        settingsEnabled[2] = set_3.isSelected();
        settingsEnabled[3] = set_4.isSelected();
        settingsEnabled[4] = set_5.isSelected();
        settingsEnabled[5] = set_6.isSelected();
        settingsEnabled[6] = set_7.isSelected();
    };

    public void save(javafx.scene.input.MouseEvent mouseEvent) {
        AlertLogic.settingsEnabled = settingsEnabled;
        ((Node)(mouseEvent.getSource())).getScene().getWindow().hide();
    }


    @FXML
    public void initialize() throws IOException {
        //
        System.arraycopy(AlertLogic.settingsEnabled, 0, settingsEnabled, 0, settingsEnabled.length);
        if(!set_1.isSelected() && settingsEnabled[0]) set_1.setSelected(true);
        if(!set_2.isSelected() && settingsEnabled[1]) set_2.setSelected(true);
        if(!set_3.isSelected() && settingsEnabled[2]) set_3.setSelected(true);
        if(!set_4.isSelected() && settingsEnabled[3]) set_4.setSelected(true);
        if(!set_5.isSelected() && settingsEnabled[4]) set_5.setSelected(true);
        if(!set_6.isSelected() && settingsEnabled[5]) set_6.setSelected(true);
        if(!set_7.isSelected() && settingsEnabled[6]) set_7.setSelected(true);

        set_1.setOnAction(set);
        set_2.setOnAction(set);
        set_3.setOnAction(set);
        set_4.setOnAction(set);
        set_5.setOnAction(set);
        set_6.setOnAction(set);
        set_7.setOnAction(set);

    }
}
