package k23b.am.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import k23b.am.Settings;
import k23b.am.dao.AdminDao;
import k23b.am.dao.UserDao;
import k23b.am.model.Admin;
import k23b.am.model.AgentStatus;
import k23b.am.model.User;
import k23b.am.model.UserStatus;
import k23b.am.srv.AdminSrv;
import k23b.am.srv.SrvException;
import k23b.am.srv.UserSrv;

public class UsersController {

    private Admin admin = null;

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    @FXML
    private ComboBox<UserStatus> statusComboBox;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Number> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> adminUsernameColumn;

    @FXML
    private TableColumn<User, String> timeRegisteredColumn;

    @FXML
    private TableColumn<User, String> timeAcceptedColumn;

    @FXML
    private TableColumn<User, String> timeActiveColumn;

    @FXML
    private TableColumn<User, UserStatus> statusColumn;

    @FXML
    private Button acceptButton;

    @FXML
    private Button rejectButton;

    private List<User> users;

    private ObservableList<User> filteredUsers;

    private boolean loaded = false;

    public UsersController() {

        users = new ArrayList<User>();
        filteredUsers = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {

        acceptButton.setDisable(true);
        rejectButton.setDisable(true);

        idColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getUserIdProperty();
        });

        usernameColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getUsernameProperty();
        });

        adminUsernameColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getAdminUsernameProperty();
        });

        timeRegisteredColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();
            DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
            sp.setValue(df.format(cellData.getValue().getTimeRegistered()));
            return sp;
        });

        timeAcceptedColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();

            Date date = cellData.getValue().getTimeAccepted();
            if (date == null)
                sp.setValue("-");
            else {
                DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
                sp.setValue(df.format(date));
            }

            return sp;
        });

        timeActiveColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();

            Date date = cellData.getValue().getTimeActive();
            if (date == null)
                sp.setValue("-");
            else {
                DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
                sp.setValue(df.format(date));
            }

            return sp;
        });

        statusColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getUserStatusProperty();
        });

        idColumn.setStyle("-fx-alignment: CENTER;");
        usernameColumn.setStyle("-fx-alignment: CENTER;");
        adminUsernameColumn.setStyle("-fx-alignment: CENTER;");
        timeRegisteredColumn.setStyle("-fx-alignment: CENTER;");
        timeAcceptedColumn.setStyle("-fx-alignment: CENTER;");
        timeActiveColumn.setStyle("-fx-alignment: CENTER;");
        statusColumn.setStyle("-fx-alignment: CENTER;");

        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, prev, curr) -> {

                    User r = (User) curr;
                    if (r == null)
                        return;

                    acceptButton.setDisable(r.getTimeAccepted() != null);
                    rejectButton.setDisable(r.getTimeAccepted() == null);

                });

        usersTable.setItems(filteredUsers);

        statusComboBox.getItems().clear();
        for (UserStatus rs : UserStatus.values())
            statusComboBox.getItems().add(rs);

        statusComboBox.getSelectionModel().select(UserStatus.values().length - 1);
    }

    public void refresh() {

        if (!loaded)
            refreshUsers();

        loaded = true;
    }

    @FXML
    private void refreshPressed(Event ev) {

        refreshUsers();
    }

    @FXML
    private void selectionChanged() {

        filterUsers();
    }

    private void refreshUsers() {

        if (admin == null || !admin.getActive())
            return;

        users.clear();

        try {

            for (UserDao ud : UserSrv.findAll()) {

                String adminUsername = null;

                if (ud.getAdminId() > 0) {

                    AdminDao ad = AdminSrv.findById(ud.getAdminId());

                    adminUsername = ad.getUsername();
                }

                User user = new User(ud.getUserId(), ud.getUsername(), adminUsername == null ? "-" : adminUsername, ud.getTimeRegistered(), ud.getTimeAccepted(), ud.getTimeActive(), Settings.getUserSessionMinutes());

                users.add(user);
            }

            users.sort((first, second) -> {
                return first.getUserId() > second.getUserId() ? 1 : -1;
            });

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);

            users.clear();
        }

        filterUsers();
    }

    private void filterUsers() {

        if (admin == null || !admin.getActive())
            return;

        filteredUsers.clear();

        UserStatus rs = statusComboBox.getSelectionModel().getSelectedItem();

        if (rs == null)
            return;

        for (User r : users) {
            if (rs == UserStatus.ALL || r.getUserStatus() == rs)
                filteredUsers.add(r);
        }

        acceptButton.setDisable(true);
        rejectButton.setDisable(true);
    }

    @FXML
    private void acceptPressed(Event ev) {

        if (this.admin == null || !this.admin.getActive())
            return;

        User u = usersTable.getSelectionModel().getSelectedItem();

        if (u == null)
            return;

        try {

            UserSrv.accept(u.getUserName(), admin.getAdminId());

            UserDao ud = UserSrv.findById(u.getUserId());

            if (ud == null)
                return;

            u.setAdminUserName(admin.getUsername());
            u.setTimeAccepted(ud.getTimeAccepted());

            filterUsers();

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);
        }
    }

    @FXML
    private void rejectPressed(Event ev) {

        if (this.admin == null || !this.admin.getActive())
            return;

        User u = usersTable.getSelectionModel().getSelectedItem();

        if (u == null)
            return;

        try {

            UserSrv.reject(u.getUserName(), admin.getAdminId());

            UserDao ud = UserSrv.findById(u.getUserId());

            if (ud == null)
                return;

            u.setAdminUserName(null);
            u.setTimeAccepted(ud.getTimeAccepted());

            filterUsers();

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);
        }
    }

    private void showerror(Exception e) {

        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
        alert.setTitle("Application Error");
        alert.setHeaderText("An error has occured!");
        alert.setContentText(e.getMessage());

        alert.showAndWait();
    }
}
