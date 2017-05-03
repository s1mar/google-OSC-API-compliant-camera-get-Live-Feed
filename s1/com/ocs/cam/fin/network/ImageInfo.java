package s1.com.ocs.cam.fin.network;


public class ImageInfo {
    public static String FILE_FORMAT_CODE_EXIF_JPEG = "JPEG";
    public static String FILE_FORMAT_CODE_EXIF_MPEG = "MPEG";

    private String mFileName;
    private String mFileId;
    private long mFileSize;
    private String mCaptureDate;
    private String mFileFormat;
    private int mWidth;
    private int mHeight;

   
    public String getFileName() {
        return mFileName;
    }

   
    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    
    public String getFileId() {
        return mFileId;
    }

    
    public void setFileId(String fileId) {
        mFileId = fileId;
    }

   
    public long getFileSize() {
        return mFileSize;
    }

    
    public void setFileSize(long fileSize) {
        mFileSize = fileSize;
    }

    
    public String getCaptureDate() {
        return mCaptureDate;
    }

   
    public void setCaptureDate(String captureDate) {
        mCaptureDate = captureDate;
    }

    
    public String getFileFormat() {
        return mFileFormat;
    }

   
    public void setFileFormat(String fileFormat) {
        mFileFormat = fileFormat;
    }

    
    public int getWidth() {
        return mWidth;
    }

    
    public void setWidth(int width) {
        mWidth = width;
    }

   
    public int getHeight() {
        return mHeight;
    }

   
    public void setHeight(int height) {
        mHeight = height;
    }
}
