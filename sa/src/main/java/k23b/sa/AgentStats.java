package k23b.sa;

/**
 * 	A class holding the statistics for this exact SA
 */
public class AgentStats {

    private String deviceName;
    private String interfaceIp;
    private String interfaceMac;
    private String osVersion;
    private String nmapVersion;
    private String hash;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getInterfaceIp() {
        return interfaceIp;
    }

    public void setInterfaceIp(String interfaceIp) {
        this.interfaceIp = interfaceIp;
    }

    public String getInterfaceMac() {
        return interfaceMac;
    }

    public void setInterfaceMac(String interfaceMac) {
        this.interfaceMac = interfaceMac;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getNmapVersion() {
        return nmapVersion;
    }

    public void setNmapVersion(String nmapVersion) {
        this.nmapVersion = nmapVersion;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Software Agent [deviceName=" + deviceName + ", interfaceIp=" + interfaceIp + ", interfaceMac=" + interfaceMac + ", osVersion=" + osVersion + ", nmapVersion=" + nmapVersion + ", hash=" + hash + "]";
    }
}
