package s1.com.ocs.cam.fin.network;


public class StorageInfo {
    int mRemainingPictures = 0;
    long mRemainingSpace = 0;
    long mTotalSpace = 0;

   
    public int getFreeSpaceInImages() {
        return mRemainingPictures;
    }

    
    public void setFreeSpaceInImages(int remainingPictures) {
        mRemainingPictures = remainingPictures;
    }

   
    public long getFreeSpaceInBytes() {
        return mRemainingSpace;
    }

   
    public void setFreeSpaceInBytes(long remainingSpace) {
        mRemainingSpace = remainingSpace;
    }

   
    public long getMaxCapacity() {
        return mTotalSpace;
    }

   
    public void setMaxCapacity(long totalSpace) {
        mTotalSpace = totalSpace;
    }
}
