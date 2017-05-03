package s1.com.ocs.cam.fin.network;


public interface HttpDownloadListener {
    
    void onTotalSize(long totalSize);
    
    void onDataReceived(int size);
}
