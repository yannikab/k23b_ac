package k23b.am.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import k23b.am.dao.RequestDao;
import k23b.am.dao.RequestStatus;
import k23b.am.model.Admin;
import k23b.am.model.Request;
import k23b.am.srv.AgentSrv;
import k23b.am.srv.RequestSrv;
import k23b.am.srv.SrvException;

public class RequestsController {

    private Admin admin = null;

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    @FXML
    private ComboBox<RequestStatus> statusComboBox;

    @FXML
    private TableView<Request> requestsTable;

    @FXML
    private TableColumn<Request, Number> idColumn;

    @FXML
    private TableColumn<Request, String> hashColumn;

    @FXML
    private TableColumn<Request, String> deviceNameColumn;

    @FXML
    private TableColumn<Request, String> interfaceIpColumn;

    @FXML
    private TableColumn<Request, String> interfaceMacColumn;

    @FXML
    private TableColumn<Request, String> osVersionColumn;

    @FXML
    private TableColumn<Request, String> nmapVersionColumn;

    @FXML
    private TableColumn<Request, String> timeReceivedColumn;

    @FXML
    private TableColumn<Request, RequestStatus> statusColumn;

    @FXML
    private Button acceptButton;

    @FXML
    private Button rejectButton;

    @FXML
    private Button pendingButton;

    private List<Request> requests;

    private ObservableList<Request> filteredRequests;

    private boolean loaded = false;

    public RequestsController() {

        requests = new ArrayList<Request>();
        filteredRequests = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {

        acceptButton.setDisable(true);
        rejectButton.setDisable(true);
        pendingButton.setDisable(true);

        idColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getRequestIdProperty();
        });

        hashColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();
            String hash = cellData.getValue().getHash();
            sp.setValue(hash.substring(0, hash.length() < 7 ? hash.length() : 7));
            return sp;
        });

        deviceNameColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getDeviceNameProperty();
        });

        interfaceIpColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getInterfaceIpProperty();
        });

        interfaceMacColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getInterfaceMacProperty();
        });

        osVersionColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getOsVersionProperty();
        });

        nmapVersionColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getNmapVersionProperty();
        });

        timeReceivedColumn.setCellValueFactory((cellData) -> {
            StringProperty sp = new SimpleStringProperty();
            DateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
            sp.setValue(df.format(cellData.getValue().getTimeReceived()));
            return sp;
        });

        statusColumn.setCellValueFactory((cellData) -> {
            return cellData.getValue().getRequestStatusProperty();
        });

        idColumn.setStyle("-fx-alignment: CENTER;");
        hashColumn.setStyle("-fx-alignment: CENTER;");
        deviceNameColumn.setStyle("-fx-alignment: CENTER;");
        interfaceIpColumn.setStyle("-fx-alignment: CENTER;");
        interfaceMacColumn.setStyle("-fx-alignment: CENTER;");
        osVersionColumn.setStyle("-fx-alignment: CENTER;");
        nmapVersionColumn.setStyle("-fx-alignment: CENTER;");
        statusColumn.setStyle("-fx-alignment: CENTER;");

        requestsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, prev, curr) -> {

                    Request r = (Request) curr;
                    if (r == null)
                        return;

                    acceptButton.setDisable(r.getRequestStatus() == RequestStatus.ACCEPTED ? true : false);
                    rejectButton.setDisable(r.getRequestStatus() == RequestStatus.REJECTED ? true : false);
                    pendingButton.setDisable(r.getRequestStatus() == RequestStatus.PENDING ? true : false);

                });

        requestsTable.setItems(filteredRequests);

        statusComboBox.getItems().clear();
        for (RequestStatus rs : RequestStatus.values())
            statusComboBox.getItems().add(rs);

        statusComboBox.getSelectionModel().select(0);

    }

    public void refresh() {

        if (!loaded)
            refreshRequests();

        loaded = true;
    }

    @FXML
    private void refreshPressed(Event ev) {

        refreshRequests();
    }

    @FXML
    private void selectionChanged() {

        filterRequests();
    }

    private void refreshRequests() {

        if (admin == null || !admin.getActive())
            return;

        requests.clear();

        try {

            for (RequestDao r : RequestSrv.findAllWithStatus(RequestStatus.ALL)) {

                Request request = new Request(r.getRequestId(), r.getHash(), r.getDeviceName(), r.getInterfaceIP(), r.getInterfaceMAC(), r.getOsVersion(), r.getNmapVersion(), r.getRequestStatus(), r.getTimeReceived());

                requests.add(request);
            }

            requests.sort((first, second) -> {
                return first.getRequestId() > second.getRequestId() ? -1 : 1;
            });

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);

            requests.clear();
        }

        filterRequests();
    }

    private void filterRequests() {

        if (admin == null || !admin.getActive())
            return;

        filteredRequests.clear();

        RequestStatus rs = statusComboBox.getSelectionModel().getSelectedItem();

        if (rs == null)
            return;

        for (Request r : requests) {
            if (rs == RequestStatus.ALL || r.getRequestStatus() == rs)
                filteredRequests.add(r);
        }

        acceptButton.setDisable(true);
        rejectButton.setDisable(true);
        pendingButton.setDisable(true);
    }

    @FXML
    private void acceptPressed(Event ev) {

        if (this.admin == null || !this.admin.getActive())
            return;

        Request r = requestsTable.getSelectionModel().getSelectedItem();

        if (r == null)
            return;

        try {

            AgentSrv.create(r.getRequestId(), this.admin.getAdminId());

            RequestDao rd = RequestSrv.findById(r.getRequestId());

            if (rd == null)
                return;

            r.setRequestStatus(rd.getRequestStatus());

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);
        }

        acceptButton.setDisable(true);
        rejectButton.setDisable(false);
        pendingButton.setDisable(false);
    }

    @FXML
    private void rejectPressed(Event ev) {

        if (this.admin == null || !this.admin.getActive())
            return;

        Request r = requestsTable.getSelectionModel().getSelectedItem();

        if (r == null)
            return;

        try {

            RequestSrv.reject(r.getRequestId(), this.admin.getAdminId());

            RequestDao rd = RequestSrv.findById(r.getRequestId());

            if (rd == null)
                return;

            r.setRequestStatus(rd.getRequestStatus());

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);
        }

        acceptButton.setDisable(false);
        rejectButton.setDisable(true);
        pendingButton.setDisable(false);
    }

    @FXML
    private void pendingPressed(Event ev) {

        if (this.admin == null || !this.admin.getActive())
            return;

        Request r = requestsTable.getSelectionModel().getSelectedItem();

        if (r == null)
            return;

        try {

            RequestSrv.pending(r.getRequestId(), this.admin.getAdminId());

            RequestDao rd = RequestSrv.findById(r.getRequestId());

            if (rd == null)
                return;

            r.setRequestStatus(rd.getRequestStatus());

        } catch (SrvException e) {
            // e.printStackTrace();
            showerror(e);
        }

        acceptButton.setDisable(false);
        rejectButton.setDisable(false);
        pendingButton.setDisable(true);
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
