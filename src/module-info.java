module projekti {

    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;
    requires mysql.connector.java;
    requires itextpdf;

    opens sample;
    opens sample.connectivity;
}