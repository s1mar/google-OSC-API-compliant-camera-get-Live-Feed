package s1.com.ocs.cam.fin.network;


public interface HttpEventListener {
   
    void onCheckStatus(boolean newStatus);

   
    void onObjectChanged(String latestCapturedFileId);

   
    void onCompleted();

    
    void onError(String errorMessage);
}
