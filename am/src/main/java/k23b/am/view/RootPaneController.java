package k23b.am.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import k23b.am.App;
import k23b.am.dao.AdminDao;
import k23b.am.model.Admin;
import k23b.am.srv.AdminSrv;
import k23b.am.srv.SrvException;

public class RootPaneController {

    private Admin admin;

    public Admin getAdmin() {
        return admin;
    }

    @FXML
    private Tab requestsTab;

    @FXML
    private Tab agentsTab;

    @FXML
    private Tab jobsTab;

    @FXML
    private Tab resultsTab;

    @FXML
    private Tab allResultsTab;

    @FXML
    private TabPane tabPane;

    @FXML
    private MenuItem menuLogin;

    @FXML
    private MenuItem menuLogout;

    @FXML
    private MenuItem menuClose;

    @FXML
    private void initialize() {

        try {

            FXMLLoader requestsLoader = new FXMLLoader();
            requestsLoader.setLocation(App.class.getResource("/fxml/RequestsView.fxml"));
            requestsTab.setContent((Node) requestsLoader.load());
            this.requestsController = requestsLoader.getController();

            FXMLLoader agentsLoader = new FXMLLoader();
            agentsLoader.setLocation(App.class.getResource("/fxml/AgentsView.fxml"));
            agentsTab.setContent((Node) agentsLoader.load());
            this.agentsController = agentsLoader.getController();

            FXMLLoader jobsLoader = new FXMLLoader();
            jobsLoader.setLocation(App.class.getResource("/fxml/JobsView.fxml"));
            jobsTab.setContent((Node) jobsLoader.load());
            this.jobsController = jobsLoader.getController();

            FXMLLoader resultsLoader = new FXMLLoader();
            resultsLoader.setLocation(App.class.getResource("/fxml/ResultsView.fxml"));
            resultsTab.setContent((Node) resultsLoader.load());
            this.resultsController = resultsLoader.getController();

            FXMLLoader allResultsLoader = new FXMLLoader();
            allResultsLoader.setLocation(App.class.getResource("/fxml/AllResultsView.fxml"));
            allResultsTab.setContent((Node) allResultsLoader.load());
            this.allResultsController = allResultsLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }

        setAdmin(null);

        // fetchAdmin();

        showLoginWindow();

        tabChanged();

        // tabPane.getSelectionModel().select(0);

        return;
    }

    private void fetchAdmin() {

        String username = "Yannis";
        String password = "5994471ABB01112AFCC18159F6CC74B4F511B99806DA59B3CAF5A9C173CACFC5";

        try {

            AdminSrv.login(username, password);

            AdminDao a = AdminSrv.findByUsername(username);

            setAdmin(new Admin(a.getAdminId(), a.getUsername(), a.getPassword(), a.getActive()));

        } catch (SrvException e) {
            // e.printStackTrace();
            showError(e);
            setAdmin(null);
        }
    }

    private RequestsController requestsController;
    private AgentsController agentsController;
    private JobsController jobsController;
    private ResultsController resultsController;
    private AllResultsController allResultsController;

    @FXML
    private void tabChanged() {

        if (requestsTab != null) {
            if (requestsTab.isSelected() && requestsController != null)
                requestsController.refresh();
        }

        if (agentsTab != null) {
            if (agentsTab.isSelected() && agentsController != null)
                agentsController.refresh();
        }

        if (jobsTab != null) {
            if (jobsTab.isSelected() && jobsController != null)
                jobsController.refresh();
        }

        if (resultsTab != null) {
            if (resultsTab.isSelected() && resultsController != null)
                resultsController.refresh();
        }

        if (allResultsTab != null) {
            if (allResultsTab.isSelected() && allResultsController != null)
                allResultsController.refresh();
        }

        return;
    }

    @FXML
    private void menuLogin() {
        showLoginWindow();
    }

    @FXML
    private void menuLogout() {

        if (getAdmin() == null)
            return;

        try {

            AdminSrv.logout(getAdmin().getUsername());

            setAdmin(null);

        } catch (SrvException e) {
            // e.printStackTrace();
            showError(e);
        }
    }

    @FXML
    private void menuClose() {

        menuLogout();

        System.exit(0);
    }

    Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void showLoginWindow() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/fxml/LoginView.fxml"));
            Pane loginView = (Pane) loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.initModality(Modality.WINDOW_MODAL);
            loginStage.initOwner(primaryStage);
            Scene scene = new Scene(loginView);
            loginStage.setScene(scene);

            LoginController loginController = loader.getController();
            loginController.setDialogStage(loginStage);

            loginStage.showAndWait();

            setAdmin(loginController.getAdmin());

        } catch (IOException e) {
            // e.printStackTrace();
            showError(e);
            setAdmin(null);
        }
    }

    public void setAdmin(Admin admin) {

        requestsController.setAdmin(admin);
        agentsController.setAdmin(admin);
        jobsController.setAdmin(admin);
        resultsController.setAdmin(admin);
        allResultsController.setAdmin(admin);

        this.admin = admin;

        tabPane.setDisable(admin == null);

        if (admin != null)
            tabChanged();

        menuLogin.setDisable(admin != null);
        menuLogout.setDisable(admin == null);
    }

    private void showError(Exception e) {

        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
        alert.setTitle("Application Error");
        alert.setHeaderText("An error has occured!");
        alert.setContentText(e.getMessage());

        alert.showAndWait();
    }
}
