module com.tanksdinos.tanksdinos {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.media;

    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;
    requires java.sql;

    opens com.tanksdinos.tanksdinos to javafx.fxml;
    exports com.tanksdinos.tanksdinos;
}