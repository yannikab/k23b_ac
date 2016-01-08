package k23b.am.model;

import java.util.Date;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import k23b.am.dao.RequestStatus;
import k23b.am.srv.SrvException;

public class Request {

    private LongProperty requestIdProperty;
    private StringProperty hashProperty;
    private StringProperty deviceNameProperty;
    private StringProperty interfaceIpProperty;
    private StringProperty interfaceMacProperty;
    private StringProperty osVersionProperty;
    private StringProperty nmapVersionProperty;
    private ObjectProperty<Date> timeReceivedProperty;
    private ObjectProperty<RequestStatus> requestStatusProperty;

    public LongProperty getRequestIdProperty() {
        return requestIdProperty;
    }

    public StringProperty getHashProperty() {
        return hashProperty;
    }

    public StringProperty getDeviceNameProperty() {
        return deviceNameProperty;
    }

    public StringProperty getInterfaceIpProperty() {
        return interfaceIpProperty;
    }

    public StringProperty getInterfaceMacProperty() {
        return interfaceMacProperty;
    }

    public StringProperty getOsVersionProperty() {
        return osVersionProperty;
    }

    public StringProperty getNmapVersionProperty() {
        return nmapVersionProperty;
    }

    public ObjectProperty<Date> getTimeReceivedProperty() {
        return timeReceivedProperty;
    }

    public ObjectProperty<RequestStatus> getRequestStatusProperty() {
        return requestStatusProperty;
    }

    public Request(long requestId, String hash, String deviceName, String interfaceIP, String interfaceMAC, String osVersion, String nmapVersion, RequestStatus requestStatus, Date timeReceived) {
        super();
        this.requestIdProperty = new SimpleLongProperty(requestId);
        this.hashProperty = new SimpleStringProperty(hash);
        this.deviceNameProperty = new SimpleStringProperty(deviceName);
        this.interfaceIpProperty = new SimpleStringProperty(interfaceIP);
        this.interfaceMacProperty = new SimpleStringProperty(interfaceMAC);
        this.osVersionProperty = new SimpleStringProperty(osVersion);
        this.nmapVersionProperty = new SimpleStringProperty(nmapVersion);
        this.timeReceivedProperty = new SimpleObjectProperty<Date>(timeReceived);
        this.requestStatusProperty = new SimpleObjectProperty<RequestStatus>(requestStatus);
    }

    public long getRequestId() {
        return requestIdProperty.get();
    }

    public void setRequestId(long requestId) {
        this.requestIdProperty.set(requestId);
    }

    public String getHash() {
        return this.hashProperty.getValue();
    }

    public void setHash(String hash) {
        this.hashProperty.setValue(hash);
    }

    public RequestStatus getRequestStatus() {
        return requestStatusProperty.get();
    }

    public void setRequestStatus(RequestStatus requestStatus) throws SrvException {
        this.requestStatusProperty.set(requestStatus);
    }

    public String getDeviceName() {
        return this.deviceNameProperty.get();
    }

    public void setDeviceName(String deviceName) {
        this.deviceNameProperty.set(deviceName);
    }

    public String getInterfaceIP() {
        return this.interfaceIpProperty.get();
    }

    public void setInterfaceIP(String interfaceIP) {
        this.interfaceIpProperty.set(interfaceIP);
    }

    public String getInterfaceMAC() {
        return this.interfaceMacProperty.get();
    }

    public void setInterfaceMAC(String interfaceMAC) {
        this.interfaceMacProperty.set(interfaceMAC);
    }

    public String getOsVersion() {
        return this.osVersionProperty.get();
    }

    public void setOsVersion(String osVersion) {
        this.osVersionProperty.set(osVersion);
    }

    public String getNmapVersion() {
        return this.nmapVersionProperty.get();
    }

    public void setNmapVersion(String nmapVersion) {
        this.nmapVersionProperty.set(nmapVersion);
    }

    public Date getTimeReceived() {
        return this.timeReceivedProperty.get();
    }

    public void setTimeReceived(Date time) {
        this.timeReceivedProperty.set(time);
    }
}
