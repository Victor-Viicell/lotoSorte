module com.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;

    opens com.app to javafx.fxml;
    exports com.app;
}
