package com.mycompany.serverside;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import network.*;
import session.SessionManager;
import services.ServerService;

/**
 * @author hends
 */
public class ServerController implements Initializable {

    @FXML
    private PieChart pieChart;
    @FXML
    private Button toggleButton;
    
    private boolean serverRunning = false;
    private ServerService serverService;

 @Override
public void initialize(URL url, ResourceBundle rb) {
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
        "#a855f7",
        "#f97316",
        "#dc2626",
        "#fbbf24",
        "#84cc16",
        "#06b6d4",
        "#3b82f6",
        "#1e40af"
    };

    // مهم جدًا
    pieChart.applyCss();
    pieChart.layout();

    int i = 0;
    for (PieChart.Data data : pieChart.getData()) {
        data.getNode().setStyle(
            "-fx-pie-color: " + colors[i] + ";" +
            "-fx-border-width: 0;" +
            "-fx-border-color: transparent;"
        );

        String value = String.format("%.1f", data.getPieValue());
        data.setName(value);

        i++;
    }
}
   

    @FXML
    private void toggleAction(ActionEvent event) {
        if (!serverRunning) {
            startServer();
        } else {
            stopServer();
        }
    }
    

    private void startServer() {
        try {
            SessionManager sessionManager = new SessionManager();

            ServerListener listener = new ServerListener(sessionManager);

            serverService = new ServerService(5000, listener);
            serverService.setDaemon(true);
            serverService.start();

            toggleButton.setText("Stop");
            toggleButton.getStyleClass().remove("start-button");
            toggleButton.getStyleClass().add("stop-button");
            serverRunning = true;

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to start server: " + e.getMessage());
        }
    }


    private void stopServer() {
        if (serverService != null) {
            try {
                serverService.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error stopping server: " + e.getMessage());
            }
        }

        toggleButton.setText("Start");
        toggleButton.getStyleClass().remove("stop-button");
        toggleButton.getStyleClass().add("start-button");
        serverRunning = false;
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Server Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}