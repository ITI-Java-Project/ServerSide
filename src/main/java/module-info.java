module com.mycompany.serverside {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires derbyclient;
    requires java.base;
    requires javafx.graphics;

    opens com.mycompany.serverside.dto to com.google.gson;
    opens com.mycompany.serverside to javafx.fxml;
    exports com.mycompany.serverside;
    requires com.google.gson;
}
