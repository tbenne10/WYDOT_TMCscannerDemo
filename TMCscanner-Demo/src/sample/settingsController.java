package sample;

import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;

import java.io.IOException;

public class settingsController {

    @FXML
    private JFXToggleButton set_1, set_2, set_3, set_4, set_5, set_6, set_7;

    Boolean settingsEnabled[] = new Boolean[7];

    EventHandler<ActionEvent> set = e -> {
        if(set_1.isSelected()) settingsEnabled[0] = true;
            else settingsEnabled[0] = false;
        if(set_2.isSelected()) settingsEnabled[1] = true;
            else settingsEnabled[1] = false;
        if(set_3.isSelected()) settingsEnabled[2] = true;
            else settingsEnabled[2] = false;
        if(set_4.isSelected()) settingsEnabled[3] = true;
            else settingsEnabled[3] = false;
        if(set_5.isSelected()) settingsEnabled[4] = true;
            else settingsEnabled[4] = false;
        if(set_6.isSelected()) settingsEnabled[5] = true;
            else settingsEnabled[5] = false;
        if(set_7.isSelected()) settingsEnabled[6] = true;
            else settingsEnabled[6] = false;
    };

    public void save(javafx.scene.input.MouseEvent mouseEvent) {
        AlertLogic.settingsEnabled = settingsEnabled;
        ((Node)(mouseEvent.getSource())).getScene().getWindow().hide();
    }


    @FXML
    public void initialize() throws IOException {


        for (int i = 0; i <  settingsEnabled.length; i++) {
            settingsEnabled[i] = AlertLogic.settingsEnabled[i];
        }
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
