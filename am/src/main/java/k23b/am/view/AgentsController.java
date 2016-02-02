package k23b.am.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
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
import k23b.am.dao.AgentDao;
import k23b.am.dao.JobDao;
import k23b.am.dao.RequestDao;
import k23b.am.dao.RequestStatus;
import k23b.am.model.Admin;
import k23b.am.model.Agent;
import k23b.am.model.AgentStatus;
import k23b.am.srv.AdminSrv;
import k23b.am.srv.AgentSrv;
import k23b.am.srv.JobSrv;
import k23b.am.srv.RequestSrv;
import k23b.am.srv.SrvException;

public class AgentsController {

    private Admin admin = null;

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    @FXML
    private ComboBox<AgentStatus> statusComboBox;

    @FXML
    private TableView<Agent> agentsTable;

    @FXML
    private TableColumn<Agent, Number> agentIdColumn;

    @FXML
    private TableColumn<Agent, String> requestHashColumn;

    @FXML
    private TableColumn<Agent, String> adminUsernameColumn;

    @FXML
    private TableColumn<Agent, String> agentTimeAcceptedColumn;

    @FXML
    private TableColumn<Agent, String> agentTimeJobRequestColumn;

    @FXML
    private TableColumn<Agent, String> agentTimeTerminatedColumn;

    @FXML
    private TableColumn<Agent, AgentStatus> agentStatusColumn;

    @FXML
    private Button terminateAgentButton;

    private List<Agent> agents;

    private ObservableList<Agent> filteredAgents;

    boolean loaded = false;

    public AgentsController() {

        agents = new ArrayList<Agent>();
        filteredAgents = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {

        terminateAgentButton.setDisable(true);

        agentIdColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getAgentIdProperty();
        });

        requestHashColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();
            String hash = cellData.getValue().getRequestHash();
            sp.setValue(hash.substring(0, hash.length() < 7 ? hash.length() : 7));
            return sp;
        });

        adminUsernameColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getAdminUsernameProperty();
        });

        agentTimeAcceptedColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();
            DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
            sp.setValue(df.format(cellData.getValue().getTimeAcceptedProperty().getValue()));
            return sp;
        });

        agentTimeJobRequestColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();

            Date date = cellData.getValue().getTimeJobRequestProperty().getValue();
            if (date == null)
                sp.setValue("-");
            else {
                DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
                sp.setValue(df.format(date));
            }

            return sp;
        });

        agentTimeTerminatedColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();

            Date date = cellData.getValue().getTimeTerminatedProperty().getValue();
            if (date == null)
                sp.setValue("-");
            else {
                DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
                sp.setValue(df.format(date));
            }

            return sp;
        });

        agentStatusColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getAgentStatusProperty();
        });

        agentIdColumn.setStyle("-fx-alignment: CENTER;");
        requestHashColumn.setStyle("-fx-alignment: CENTER;");
        adminUsernameColumn.setStyle("-fx-alignment: CENTER;");
        agentTimeAcceptedColumn.setStyle("-fx-alignment: CENTER;");
        agentTimeJobRequestColumn.setStyle("-fx-alignment: CENTER;");
        agentTimeTerminatedColumn.setStyle("-fx-alignment: CENTER;");
        agentStatusColumn.setStyle("-fx-alignment: CENTER;");

        agentsTable.setItems(filteredAgents);

        agentsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, prev, curr) -> {
                    checkAgent();
                });

        statusComboBox.getItems().clear();
        for (AgentStatus as : AgentStatus.values())
            statusComboBox.getItems().add(as);

        statusComboBox.getSelectionModel().select(AgentStatus.values().length - 1);
    }

    public void refresh() {

        if (!loaded)
            refreshAgents();

        loaded = true;
    }

    public void refreshPressed(Event ev) {

        refreshAgents();
    }

    public void selectionChanged() {

        filterAgents();
    }

    private void refreshAgents() {

        if (admin == null || !admin.getActive())
            return;

        agents.clear();

        try {

            for (AgentDao a : AgentSrv.findAllWithRequestStatus(RequestStatus.ACCEPTED)) {

                RequestDao rd = RequestSrv.findById(a.getRequestId());

                AdminDao ad = AdminSrv.findById(a.getAdminId());

                Agent agent = new Agent(a.getAgentId(), rd.getHash(), ad.getUsername(), a.getTimeAccepted(), a.getTimeJobRequest(), a.getTimeTerminated(), Settings.getJobRequestInterval());

                agents.add(agent);
            }

            agents.sort((first, second) -> {
                return first.getAgentId() > second.getAgentId() ? 1 : -1;
            });

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);

            agents.clear();
        }

        filterAgents();
    }

    private void filterAgents() {

        if (admin == null || !admin.getActive())
            return;

        filteredAgents.clear();

        AgentStatus as = statusComboBox.getSelectionModel().getSelectedItem();

        if (as == null)
            return;

        for (Agent a : agents) {

            if (as == AgentStatus.ALL || a.getAgentStatus() == as)
                filteredAgents.add(a);
        }

        terminateAgentButton.setDisable(true);
    }

    private void checkAgent() {

        terminateAgentButton.setDisable(true);

        Agent agent = agentsTable.getSelectionModel().getSelectedItem();

        if (agent == null)
            return;

        terminateAgentButton.setDisable(false);
    }

    public void terminateAgentPressed(Event ev) {

        if (admin == null || !admin.getActive())
            return;

        Agent agent = agentsTable.getSelectionModel().getSelectedItem();

        if (agent == null)
            return;

        try {

            String params = "exit";

            JobDao j = JobSrv.create(agent.getAgentId(), admin.getAdminId(), Date.from(Instant.now()), params, false, 0);

            if (j == null)
                return;

            AgentSrv.terminate(agent.getAgentId());

            AgentDao a = AgentSrv.findById(agent.getAgentId());

            if (a != null)
                agent.setTimeTerminated(a.getTimeTerminated());

            filterAgents();

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);

            terminateAgentButton.setDisable(true);
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
