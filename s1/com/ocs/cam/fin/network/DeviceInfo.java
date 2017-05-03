package s1.com.ocs.cam.fin.network;


public class DeviceInfo {
    private String mModel = "";
    private String mDeviceVersion = "";
    private String mSerialNumber = "";

   
    public DeviceInfo() {
    }

    
    public String getModel() {
        return mModel;
    }

    
    public void setModel(String model) {
        mModel = model;
    }

    
    public String getSerialNumber() {
        return mSerialNumber;
    }

   
    public void setSerialNumber(String serialNumber) {
        mSerialNumber = serialNumber;
    }

    
    public String getDeviceVersion() {
        return mDeviceVersion;
    }

    
    public void setDeviceVersion(String version) {
        mDeviceVersion = version;
    }
}
