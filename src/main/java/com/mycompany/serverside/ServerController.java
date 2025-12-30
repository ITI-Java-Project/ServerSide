package com.mycompany.serverside;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * @author hends
 */
public class ServerController implements Initializable {

    @FXML
    private PieChart pieChart;
    @FXML
    private Button toggleButton;
    
    private boolean isToggle;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isToggle = false;
         ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Purple", 5),
            new PieChart.Data("Orange", 15),
            new PieChart.Data("Red", 12.5),
            new PieChart.Data("Yellow", 12.5),
            new PieChart.Data("Lime", 12.5),
            new PieChart.Data("Cyan", 12.5),
            new PieChart.Data("Blue", 12.5),
            new PieChart.Data("Navy", 12.5)
        );
        
        pieChart.setData(pieChartData);
          String[] colors = {
            "#a855f7", // Purple
            "#f97316", // Orange
            "#dc2626", // Red
            "#fbbf24", // Yellow
            "#84cc16", // Lime
            "#06b6d4", // Cyan
            "#3b82f6", // Blue
            "#1e40af"  // Navy
        };
        pieChart.layout();

        int i = 0;
        for (PieChart.Data data : pieChart.getData()) {
            data.getNode().setStyle("-fx-pie-color: " + colors[i] + "; -fx-border-width: 0;-fx-border-color: transparent;");
            String value = String.format("%.1f", data.getPieValue());
            Label label = new Label(value);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
            data.setName(value); 
            i++;
        }
    }    

    @FXML
    private void toggleAction(ActionEvent event) {
        if(!isToggle){
            toggleButton.setText("Stop");
            toggleButton.getStyleClass().remove("start-button");
            toggleButton.getStyleClass().add("stop-button");
            isToggle = true;
        }else{
            toggleButton.setText("Start");
            toggleButton.getStyleClass().remove("stop-button");
            toggleButton.getStyleClass().add("start-button");
            isToggle = false;
        }
        
    }
}