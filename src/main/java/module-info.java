module com.tanksdinos.tanksdinos {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.media;
    requires javafx.fxml;
    requires java.sql;

    exports com.tanksdinos.tanksdinos;
    opens com.tanksdinos.tanksdinos to javafx.fxml;
}