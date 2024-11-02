module com.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires kernel;
    requires layout;
    requires io;

    opens com.app to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.app;
}
