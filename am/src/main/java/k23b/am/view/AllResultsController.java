package k23b.am.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import jfxtras.scene.control.LocalDateTimePicker;
import k23b.am.dao.AgentDao;
import k23b.am.dao.JobDao;
import k23b.am.dao.RequestDao;
import k23b.am.dao.ResultDao;
import k23b.am.model.Admin;
import k23b.am.model.Result;
import k23b.am.srv.AgentSrv;
import k23b.am.srv.JobSrv;
import k23b.am.srv.RequestSrv;
import k23b.am.srv.ResultSrv;
import k23b.am.srv.SrvException;

public class AllResultsController {

    private Admin admin = null;

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    // results
    @FXML
    private TableView<Result> resultsTable;

    @FXML
    private TableColumn<Result, Number> resultIdColumn;

    @FXML
    private TableColumn<Result, Number> jobIdColumn;

    @FXML
    private TableColumn<Result, String> requestHashColumn;

    @FXML
    private TableColumn<Result, String> timeReceivedColumn;

    private ObservableList<Result> results;

    @FXML
    private TreeView<String> xmlTreeView;

    @FXML
    private LocalDateTimePicker startTimePicker;

    @FXML
    private LocalDateTimePicker endTimePicker;

    @FXML
    private Button refreshResultsButton;

    private XmlTreeParser xmlParser;

    boolean loaded = false;

    public AllResultsController() {

        results = FXCollections.observableArrayList();

        xmlParser = new XmlTreeParser();
    }

    @FXML
    private void initialize() {

        initializeResultColumns();

        initializeTimePickers();

        resultsTable.setItems(results);

        resultsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, prev, curr) -> {
                    refreshTreeView();
                });
    }

    private void initializeResultColumns() {

        resultIdColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getResultIdProperty();
        });

        requestHashColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();
            String hash = cellData.getValue().getRequestHash();
            sp.setValue(hash.substring(0, hash.length() < 7 ? hash.length() : 7));
            return sp;
        });

        jobIdColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getJobIdProperty();
        });

        timeReceivedColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();
            DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
            sp.setValue(df.format(cellData.getValue().getTimeReceived()));
            return sp;
        });

        resultIdColumn.setStyle("-fx-alignment: CENTER;");
        requestHashColumn.setStyle("-fx-alignment: CENTER;");
        jobIdColumn.setStyle("-fx-alignment: CENTER;");
        timeReceivedColumn.setStyle("-fx-alignment: CENTER;");
    }

    private void initializeTimePickers() {

        Date now = Date.from(Instant.now().truncatedTo(ChronoUnit.MINUTES));
        Date yesterday = Date.from(now.toInstant().minus(1, ChronoUnit.DAYS));
        Date tomorrow = Date.from(now.toInstant().plus(1, ChronoUnit.DAYS));

        startTimePicker.setLocalDateTime(LocalDateTime.ofInstant(yesterday.toInstant(), ZoneId.systemDefault()));
        endTimePicker.setLocalDateTime(LocalDateTime.ofInstant(tomorrow.toInstant(), ZoneId.systemDefault()));

        startTimePicker.setValueValidationCallback(date -> {
            // refreshResults();
            refreshResults(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()), Date.from(endTimePicker.getLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
            return true;
        });

        endTimePicker.setValueValidationCallback(date -> {
            // refreshResults();
            refreshResults(Date.from(startTimePicker.getLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()), Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
            return true;
        });

        // startTimePicker.setDateTimeFormatter(DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss"));
        // endTimePicker.setDateTimeFormatter(DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss"));

        startTimePicker.setFocusTraversable(false);
        endTimePicker.setFocusTraversable(false);

        // startTimePicker.setParseErrorCallback((t) -> {
        // return null;
        // });
        //
        // endTimePicker.setParseErrorCallback((t) -> {
        // return null;
        // });

        startTimePicker.setAllowNull(false);
        endTimePicker.setAllowNull(false);
    }

    public void refresh() {

        if (!loaded)
            refreshResults(Date.from(startTimePicker.getLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()), Date.from(endTimePicker.getLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));

        loaded = true;
    }

    @FXML
    void refreshPressed(ActionEvent event) {

        refreshResults(Date.from(startTimePicker.getLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()), Date.from(endTimePicker.getLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
    }

    @FXML
    private void refreshResultsPressed() {
        refreshResults(Date.from(startTimePicker.getLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()), Date.from(endTimePicker.getLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
    }

    private void refreshResults(Date startDate, Date endDate) {

        if (admin == null || !admin.getActive())
            return;

        results.clear();

        try {

            for (ResultDao r : ResultSrv.findAllWithinDates(startDate, endDate)) {

                JobDao jd = JobSrv.findById(r.getJobId());

                AgentDao ad = AgentSrv.findById(jd.getAgentId());

                RequestDao rd = RequestSrv.findById(ad.getRequestId());

                Result result = new Result(r.getResultId(), rd.getHash(), r.getJobId(), r.getTimeReceived(), r.getOutput());
                results.add(result);
            }

            results.sort((first, second) -> {
                return first.getResultId() > second.getResultId() ? -1 : 1;
            });

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);

            results.clear();
        }
    }

    private void refreshTreeView() {

        if (admin == null || !admin.getActive())
            return;

        Result result = resultsTable.getSelectionModel().getSelectedItem();

        if (result == null) {
            xmlTreeView.setRoot(null);
            return;
        }

        TreeItem<String> treeItem = xmlParser.treeItemForXml(result.getOutput());

        xmlTreeView.setRoot(treeItem);
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
