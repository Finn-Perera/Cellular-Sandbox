module finnperera.sandbox {
    requires javafx.controls;
    requires javafx.fxml;


    opens finnperera.sandbox to javafx.fxml;
    exports finnperera.sandbox;
}