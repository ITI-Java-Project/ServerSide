module com.mycompany.serverside {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires derbyclient;

    opens com.mycompany.serverside to javafx.fxml;
    exports com.mycompany.serverside;
}
