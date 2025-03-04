module com.tanksdinos.tanksdinos {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;

    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.tanksdinos.tanksdinos to javafx.fxml;
    exports com.tanksdinos.tanksdinos;
}