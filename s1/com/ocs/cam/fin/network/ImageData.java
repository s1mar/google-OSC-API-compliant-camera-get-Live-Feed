package s1.com.ocs.cam.fin.network;


public class ImageData {
    private byte[] mRawData;
    private Double pitch = 0.0d;
    private Double roll = 0.0d;
    private Double yaw = 0.0d;

   
    public byte[] getRawData() {
        return mRawData;
    }

   
    public void setRawData(byte[] rawData) {
        mRawData = rawData;
    }

   
    public Double getPitch() {
        return pitch;
    }

  
    public void setPitch(Double pitch) {
        this.pitch = pitch;
    }

   
    public Double getRoll() {
        return roll;
    }

   
    public void setRoll(Double roll) {
        this.roll = roll;
    }

   
    public Double getYaw() {
        return yaw;
    }

    
    public void setYaw(Double yaw) {
        this.yaw = yaw;
    }
}
