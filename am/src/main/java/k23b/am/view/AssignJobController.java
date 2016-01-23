package k23b.am.view;

import java.time.Instant;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import k23b.am.dao.JobDao;
import k23b.am.model.Admin;
import k23b.am.model.Agent;
import k23b.am.model.Job;
import k23b.am.srv.JobSrv;
import k23b.am.srv.SrvException;

public class AssignJobController {

    @FXML
    private TextField username;

    @FXML
    private TextField agentHash;

    @FXML
    private TextField jobParameters;

    @FXML
    private RadioButton periodicNo;

    @FXML
    private RadioButton periodicYes;

    @FXML
    private TextField periodText;

    @FXML
    void assignPressed(ActionEvent event) {

        this.assignJobStage.close();

        try {

            long agentId = agent.getAgentId();
            long adminId = admin.getAdminId();
            String parameters = jobParameters.getText();
            boolean periodic = periodicYes.isSelected();
            int period = 0;
            try {
                period = Integer.parseInt(periodText.getText());
            } catch (NumberFormatException e) {
            }

            JobDao j = JobSrv.create(agentId, adminId, Date.from(Instant.now()), parameters, periodic, periodic ? period : 0);

            if (j != null)
                this.job = new Job(j.getJobId(), j.getAgentId(), admin.getUsername(), j.getTimeAssigned(), j.getTimeSent(), j.getParams(), j.getPeriodic(), j.getPeriod(), j.getTimeStopped());

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);
        }
    }

    @FXML
    void cancelPressed(ActionEvent event) {
        this.job = null;
        this.assignJobStage.close();
    }

    @FXML
    void periodicChanged(ActionEvent event) {
        periodText.setDisable(periodicNo.isSelected());
        periodText.setEditable(periodicYes.isSelected());
    }

    @FXML
    public void initialize() {

        this.periodText.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    periodText.setText(oldValue);
                }
            }
        });
    }

    public AssignJobController() {

    }

    private Job job;

    public Job getJob() {
        return job;
    }

    Admin admin;

    public void setAdmin(Admin admin) {

        this.admin = admin;

        username.setEditable(false);
        username.setFocusTraversable(false);
        username.setText(this.admin.getUsername());

    }

    Agent agent;

    public void setAgent(Agent agent) {

        this.agent = agent;

        agentHash.setEditable(false);
        agentHash.setFocusTraversable(false);
        agentHash.setText(this.agent.getRequestHash().substring(0, 7));

        periodicNo.setSelected(true);
        periodText.setDisable(true);
    }

    private Stage assignJobStage;

    public void setDialogStage(Stage assignJobStage) {
        this.assignJobStage = assignJobStage;
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
