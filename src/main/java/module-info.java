// src/main/java/module-info.java
module com.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;

    // Open the com.app package to JavaFX FXML and Jackson Databind
    opens com.app to javafx.fxml, com.fasterxml.jackson.databind;

    // Export your package if needed
    exports com.app;
}
