package com.mycompany.serverside;

import com.mycompany.serverside.dao.PlayerDao;
import java.net.URL;
import javafx.util.Duration;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import network.*;
import session.SessionManager;
import services.ServerService;

/**
 * @author hends
 */
public class ServerController implements Initializable {

    @FXML
    private Button toggleButton;
    @FXML
    private AnchorPane chartContainer;

    private BarChart<String, Number> barChart;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private boolean serverRunning = false;
    private ServerService serverService;
    private SessionManager sessionManager;
    private int allPlayersCount, waitingPlayersCount, playersInSessionCount, offlinePlayersCount;
    private XYChart.Series<String, Number> series;
    private final String[] COLORS = {"#f97316", "#22c55e", "#dc2626"};  // Orange, Green, Red

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sessionManager = new SessionManager();

        initalizeBarChar();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(10), e -> Platform.runLater(() -> updateBarChartData()))
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Platform.runLater(() -> {
            applyColors();
            updateBarChartData();
        });
    }

    @FXML
    private void toggleAction(ActionEvent event) {
        if (!serverRunning) {
            startServer();
        } else {
            stopServer();
        }
    }

    private void initalizeBarChar() {
        xAxis = new CategoryAxis();
        xAxis.setLabel("Player Status");

        yAxis = new NumberAxis();
        yAxis.setLabel("Count");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(10);
        yAxis.setTickUnit(1);

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Player Statistics");
        barChart.setLegendVisible(false);
        barChart.setAnimated(true);
        barChart.setPrefSize(600, 500);

        chartContainer.getChildren().add(barChart);

        AnchorPane.setTopAnchor(barChart, 0.0);
        AnchorPane.setBottomAnchor(barChart, 0.0);
        AnchorPane.setLeftAnchor(barChart, 0.0);
        AnchorPane.setRightAnchor(barChart, 0.0);

        series = new XYChart.Series<>();
        series.setName("Players");

        series.getData().add(new XYChart.Data<>("Waiting", 0));
        series.getData().add(new XYChart.Data<>("In Session", 0));
        series.getData().add(new XYChart.Data<>("Offline", 0));

        barChart.getData().add(series);
    }

    private void updateBarChartData() {
        prepareChartData();

        Platform.runLater(() -> {
            series.getData().get(0).setYValue(waitingPlayersCount);
            series.getData().get(1).setYValue(playersInSessionCount);
            series.getData().get(2).setYValue(offlinePlayersCount);

            applyColors();
        });
    }

    private void prepareChartData() {
        allPlayersCount = PlayerDao.getAllPlayers().size();
        waitingPlayersCount = sessionManager.getWaitingClients().size();
        playersInSessionCount = SessionManager.getPlayersInSessionCount();
        offlinePlayersCount = (allPlayersCount - (waitingPlayersCount + playersInSessionCount));
    }

    private void applyColors() {
        barChart.applyCss();
        barChart.layout();

        Platform.runLater(() -> {
            for (int i = 0; i < series.getData().size() && i < COLORS.length; i++) {
                final XYChart.Data<String, Number> data = series.getData().get(i);

                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: " + COLORS[i] + ";");
                }
            }
        });
    }

    private void startServer() {
        try {
            ServerListener listener = new ServerListener(sessionManager);

            serverService = new ServerService(5000, listener);
            serverService.setDaemon(true);
            serverService.start();

            Platform.runLater(() -> updateBarChartData());

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