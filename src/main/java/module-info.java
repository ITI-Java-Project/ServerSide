module com.mycompany.serverside {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.serverside to javafx.fxml;
    exports com.mycompany.serverside;
}
