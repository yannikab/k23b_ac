package k23b.am.view;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import k23b.am.dao.AdminDao;
import k23b.am.model.Admin;
import k23b.am.srv.AdminSrv;
import k23b.am.srv.SrvException;

public class LoginController {

    private Admin admin;

    public Admin getAdmin() {
        return admin;
    }

    @FXML
    TextField usernameField;

    @FXML
    PasswordField passwordField;

    public LoginController() {

    }

    @FXML
    private void initialize() {

        usernameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    logInPressed();
                }
            }
        });

        passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    logInPressed();
                }
            }
        });
    }

    @FXML
    private void logInPressed() {

        String username = usernameField.getText();
        String password = passwordField.getText();

        try {

            AdminSrv.login(username, password);

            AdminDao a = AdminSrv.findByUsername(username);

            this.admin = new Admin(a.getAdminId(), a.getUsername(), a.getPassword(), a.getActive());

            loginStage.close();

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);

            this.admin = null;
            loginStage.close();
        }
    }

    @FXML
    private void cancelPressed() {
        this.admin = null;
        this.loginStage.close();
    }

    private void showerror(Exception e) {

        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
        alert.setTitle("Application Error");
        alert.setHeaderText("An error has occured!");
        alert.setContentText(e.getMessage());

        alert.showAndWait();
    }

    private Stage loginStage;

    public void setDialogStage(Stage loginStage) {
        this.loginStage = loginStage;
    }
}
