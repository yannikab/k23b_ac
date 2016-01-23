package k23b.am.view;

import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import k23b.am.App;
import k23b.am.Settings;
import k23b.am.dao.AdminDao;
import k23b.am.dao.AgentDao;
import k23b.am.dao.JobDao;
import k23b.am.dao.RequestDao;
import k23b.am.dao.RequestStatus;
import k23b.am.model.Admin;
import k23b.am.model.Agent;
import k23b.am.model.AgentStatus;
import k23b.am.model.Job;
import k23b.am.model.JobStatus;
import k23b.am.srv.AdminSrv;
import k23b.am.srv.AgentSrv;
import k23b.am.srv.JobSrv;
import k23b.am.srv.RequestSrv;
import k23b.am.srv.SrvException;

public class JobsController {

    private Admin admin = null;

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private Button assignJobButton;

    // agents
    @FXML
    private ComboBox<AgentStatus> agentStatusComboBox;

    @FXML
    private TableView<Agent> agentsTable;

    @FXML
    private TableColumn<Agent, Number> agentIdColumn;

    @FXML
    private TableColumn<Agent, String> requestHashColumn;

    @FXML
    private TableColumn<Agent, String> admin1UsernameColumn;

    @FXML
    private TableColumn<Agent, String> agentTimeAcceptedColumn;

    @FXML
    private TableColumn<Agent, String> agentTimeActiveColumn;

    @FXML
    private TableColumn<Agent, AgentStatus> agentStatusColumn;

    private List<Agent> agents;

    private ObservableList<Agent> filteredAgents;

    // jobs
    @FXML
    private ComboBox<JobStatus> jobStatusComboBox;

    @FXML
    private TableView<Job> jobsTable;

    @FXML
    private TableColumn<Job, Number> jobIdColumn;

    @FXML
    private TableColumn<Job, String> admin2UsernameColumn;

    @FXML
    private TableColumn<Job, String> timeAssignedColumn;

    @FXML
    private TableColumn<Job, String> timeSentColumn;

    @FXML
    private TableColumn<Job, String> paramsColumn;

    @FXML
    private TableColumn<Job, String> periodicColumn;

    @FXML
    private TableColumn<Job, Number> periodColumn;

    @FXML
    private TableColumn<Job, String> timeStoppedColumn;

    @FXML
    private TableColumn<Job, JobStatus> jobStatusColumn;

    @FXML
    private Button stopPeriodicButton;

    @FXML
    private CheckBox checkOneTimeJobs;

    @FXML
    private CheckBox checkPeriodicJobs;

    @FXML
    private CheckBox checkPeriodicStops;

    @FXML
    private CheckBox checkAgentExits;

    private List<Job> jobs;

    private ObservableList<Job> filteredJobs;

    boolean loaded = false;

    public JobsController() {

        agents = new ArrayList<Agent>();
        filteredAgents = FXCollections.observableArrayList();
        jobs = new ArrayList<Job>();
        filteredJobs = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {

        assignJobButton.setDisable(true);
        stopPeriodicButton.setDisable(true);
        
        initializeAgentColumns();

        initializeJobColumns();

        agentsTable.setItems(filteredAgents);

        agentsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, prev, curr) -> {
                    refreshJobs();
                });

        agentStatusComboBox.getItems().clear();
        for (AgentStatus as : AgentStatus.values())
            agentStatusComboBox.getItems().add(as);

        agentStatusComboBox.getSelectionModel().select(agentStatusComboBox.getItems().size() - 1);

        jobsTable.setItems(filteredJobs);

        jobsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, prev, curr) -> {
                    checkPeriodic();
                });

        jobStatusComboBox.getItems().clear();
        jobStatusComboBox.getItems().add(JobStatus.ASSIGNED);
        jobStatusComboBox.getItems().add(JobStatus.SENT);
        jobStatusComboBox.getItems().add(JobStatus.ALL);

        jobStatusComboBox.getSelectionModel().select(jobStatusComboBox.getItems().size() - 1);

        stopPeriodicButton.setDisable(true);

        checkOneTimeJobs.setSelected(true);

        checkOneTimeJobs.selectedProperty().addListener((obs, prev, curr) -> {
            filterJobs();
        });

        checkPeriodicJobs.setSelected(true);

        checkPeriodicJobs.selectedProperty().addListener((obs, prev, curr) -> {
            filterJobs();
        });

        checkPeriodicStops.setSelected(false);

        checkPeriodicStops.selectedProperty().addListener((obs, prev, curr) -> {
            filterJobs();
        });

        checkAgentExits.setSelected(false);

        checkAgentExits.selectedProperty().addListener((obs, prev, curr) -> {
            filterJobs();
        });
    }

    private void initializeAgentColumns() {

        agentIdColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getAgentIdProperty();
        });

        requestHashColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();
            String hash = cellData.getValue().getRequestHash();
            sp.setValue(hash.substring(0, hash.length() < 7 ? hash.length() : 7));
            return sp;
        });

        admin1UsernameColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getAdminUsernameProperty();
        });

        agentTimeAcceptedColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();
            DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
            sp.setValue(df.format(cellData.getValue().getTimeAcceptedProperty().getValue()));
            return sp;
        });

        agentTimeActiveColumn.setCellValueFactory((cellData) -> {
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

        agentStatusColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getAgentStatusProperty();
        });

        agentIdColumn.setStyle("-fx-alignment: CENTER;");
        requestHashColumn.setStyle("-fx-alignment: CENTER;");
        admin1UsernameColumn.setStyle("-fx-alignment: CENTER;");
        agentTimeAcceptedColumn.setStyle("-fx-alignment: CENTER;");
        agentTimeActiveColumn.setStyle("-fx-alignment: CENTER;");
        agentStatusColumn.setStyle("-fx-alignment: CENTER;");
    }

    private void initializeJobColumns() {

        jobIdColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getJobIdProperty();
        });

        admin2UsernameColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getAdminUsernameProperty();
        });

        timeAssignedColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();
            DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
            sp.setValue(df.format(cellData.getValue().getTimeAssigned()));
            return sp;
        });

        timeSentColumn.setCellValueFactory((cellData) -> {
            boolean sent = cellData.getValue().getTimeSent() != null;
            StringProperty sp = new SimpleStringProperty();
            DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
            sp.setValue(sent ? df.format(cellData.getValue().getTimeSent()) : "-");
            return sp;
        });

        paramsColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getParamsProperty();
        });

        periodicColumn.setCellValueFactory((cellData) -> {
            boolean periodic = cellData.getValue().getPeriodicProperty().getValue();
            return new SimpleStringProperty(periodic ? "Yes" : "No");
        });

        periodColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getPeriodProperty();
        });

        timeStoppedColumn.setCellValueFactory((cellData) -> {
            boolean terminated = cellData.getValue().getTimeStopped() != null;
            StringProperty sp = new SimpleStringProperty();
            DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
            sp.setValue(terminated ? df.format(cellData.getValue().getTimeStopped()) : "-");
            return sp;
        });

        jobStatusColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getJobStatusProperty();
        });

        jobIdColumn.setStyle("-fx-alignment: CENTER;");
        admin2UsernameColumn.setStyle("-fx-alignment: CENTER;");
        timeAssignedColumn.setStyle("-fx-alignment: CENTER;");
        timeSentColumn.setStyle("-fx-alignment: CENTER;");
        paramsColumn.setStyle("-fx-alignment: CENTER;");
        periodicColumn.setStyle("-fx-alignment: CENTER;");
        periodColumn.setStyle("-fx-alignment: CENTER;");
        timeStoppedColumn.setStyle("-fx-alignment: CENTER;");
        jobStatusColumn.setStyle("-fx-alignment: CENTER;");
    }

    public void refresh() {

        if (!loaded)
            refreshAgents();

        loaded = true;
    }

    @FXML
    private void refreshAgentsPressed(Event ev) {

        refreshAgents();
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

        AgentStatus as = agentStatusComboBox.getSelectionModel().getSelectedItem();

        if (as == null)
            return;

        for (Agent a : agents) {

            if (as == AgentStatus.ALL || a.getAgentStatus() == as)
                filteredAgents.add(a);
        }
    }

    @FXML
    private void agentSelectionChanged() {

        filterAgents();
    }

    private void refreshJobs() {

        if (admin == null || !admin.getActive())
            return;

        jobs.clear();
        filterJobs();

        Agent agent = agentsTable.getSelectionModel().getSelectedItem();

        assignJobButton.setDisable(agent == null);

        if (agent == null)
            return;

        try {

            for (JobDao j : JobSrv.findAllWithAgentId(agent.getAgentId())) {

                AdminDao ad = AdminSrv.findById(j.getAdminId());

                Job job = new Job(j.getJobId(), j.getAgentId(), ad.getUsername(), j.getTimeAssigned(), j.getTimeSent(), j.getParams(), j.getPeriodic(), j.getPeriod(), j.getTimeStopped());

                jobs.add(job);
            }

            jobs.sort((first, second) -> {
                return first.getJobId() > second.getJobId() ? 1 : -1;
            });

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);

            jobs.clear();
        }

        filterJobs();
    }

    @FXML
    private void refreshJobsPressed(Event ev) {

        refreshJobs();
    }

    private void filterJobs() {

        if (admin == null || !admin.getActive())
            return;

        filteredJobs.clear();

        JobStatus js = jobStatusComboBox.getSelectionModel().getSelectedItem();

        if (js == null)
            return;

        for (Job job : jobs) {

            if (false ||
                    js == JobStatus.ALL ||
                    js == JobStatus.SENT && job.getJobStatus() == JobStatus.STOPPED ||
                    js == job.getJobStatus()) {

                if (job.isAgentTermination()) {
                    if (checkAgentExits.isSelected())
                        filteredJobs.add(job);
                } else if (job.isPeriodicStop()) {
                    if (checkPeriodicStops.isSelected())
                        filteredJobs.add(job);
                } else if (!job.getPeriodic()) {
                    if (checkOneTimeJobs.isSelected())
                        filteredJobs.add(job);
                } else if (job.getPeriodic()) {
                    if (checkPeriodicJobs.isSelected())
                        filteredJobs.add(job);
                }
            }
        }
    }

    @FXML
    private void jobSelectionChanged() {

        filterJobs();
    }

    @FXML
    private void assignJobPressed(Event ev) {

        if (admin == null || !admin.getActive())
            return;

        showAssignJobWindow();
    }

    private void showAssignJobWindow() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/fxml/AssignJobView.fxml"));
            Pane assignJobView = (Pane) loader.load();

            Stage assignJobStage = new Stage();
            assignJobStage.setTitle("Assign Job");
            assignJobStage.initModality(Modality.WINDOW_MODAL);
            assignJobStage.initOwner(primaryStage);
            Scene scene = new Scene(assignJobView);
            assignJobStage.setScene(scene);

            AssignJobController assignJobController = loader.getController();
            assignJobController.setDialogStage(assignJobStage);
            assignJobController.setAdmin(this.admin);

            assignJobController.setAgent(agentsTable.getSelectionModel().getSelectedItem());

            assignJobStage.showAndWait();

            if (assignJobController.getJob() != null)
                refreshJobs();

        } catch (IOException e) {
            // e.printStackTrace();
            showerror(e);
            setAdmin(null);
        }
    }

    private void checkPeriodic() {

        stopPeriodicButton.setDisable(true);

        Job job = jobsTable.getSelectionModel().getSelectedItem();

        if (job == null)
            return;

        stopPeriodicButton.setDisable(!job.getPeriodic() || job.getJobStatus() == JobStatus.ASSIGNED);
    }

    @FXML
    private void stopPeriodicPressed(Event ev) {

        if (admin == null || !admin.getActive())
            return;

        Job job = jobsTable.getSelectionModel().getSelectedItem();

        if (job == null)
            return;

        if (!job.getPeriodic() || job.getJobStatus() == JobStatus.ASSIGNED)
            return;

        String params = "stop " + job.getJobId();

        try {

            JobDao j = JobSrv.create(job.getAgentId(), admin.getAdminId(), Date.from(Instant.now()), params, false, 0);

            if (j == null)
                return;

            jobs.add(new Job(j.getJobId(), j.getAgentId(), admin.getUsername(), j.getTimeAssigned(), j.getTimeSent(), j.getParams(), j.getPeriodic(), j.getPeriod(), j.getTimeStopped()));

            filterJobs();

            JobSrv.stopPeriodic(job.getJobId());

            JobDao jd = JobSrv.findById(job.getJobId());

            if (jd != null)
                job.getTimeStoppedProperty().set(jd.getTimeStopped());

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);
        }

        stopPeriodicButton.setDisable(true);
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
