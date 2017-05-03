package s1.com.ocs.frags;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;


import com.squareup.picasso.Picasso;
import com.victor.loading.newton.NewtonCradleLoading;


import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


import com.s1mar.phobxp.R;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import s1.com.ocs.utils.Utilities;
import s1.com.ocs.cam.fin.network.HttpEventListener;
import s1.com.ocs.cam.fin.view.MJpegInputStream;
import s1.com.ocs.cam.fin.view.MJpegView;
import s1.com.ocs.cam.fin.network.HttpConnector;


public class LiveFeed extends Fragment {
    /**Toolbar Menu**/
    Toolbar toolbar;
    View contentHamburger;
    RelativeLayout root;
    void init_Toolbar(View root){

        toolbar = (Toolbar)root.findViewById(R.id.toolbar);
        this.root = (RelativeLayout)getActivity().findViewById(R.id.activity_main);
        contentHamburger = root.findViewById(R.id.content_hamburger);
    }

    void toolbar_insideOnCreateView(){
        if (toolbar != null) {
            getActivity().setActionBar(toolbar);
            getActivity().getActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(getActivity()).inflate(R.layout.guillotine, null);
         root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(250)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();
    }



    Toast toast;

    ImageView image_clicked;

    MJpegView mLiveView;

    ImageView btn_save;



    NewtonCradleLoading progressBar;

    void init_Views(View root){

        image_clicked = (ImageView)root.findViewById(R.id.image_clicked);
        mLiveView = (MJpegView)root.findViewById(R.id.live_feed);
        btn_save = (ImageView) root.findViewById(R.id.save_photo);

        progressBar = (NewtonCradleLoading)root.findViewById(R.id.nc_progress);
    }


    ShowLiveViewTask mLiveViewLoader=null;
    boolean FLAG_clicked;
    LiveFeedInterface Listner;

    public interface LiveFeedInterface{

        public void goto_FeedCapture(String fileId,byte[] DataImage);
        public void goto_Gallery();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Listner = (LiveFeedInterface)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(Listner!=null){
            Listner = null;
        }
    }

    public LiveFeed() {
        // Required empty public constructor
    }

    void toastGen(String s){
        if(toast!=null){
            toast.cancel();
        }
        toast= Toast.makeText(getActivity().getApplicationContext(),s,Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_live_feed, container, false);
        init_Views(view);
        //setBackImage();
        boolean flag_SB= SB();
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShootTask().execute();
            }
        });



        image_clicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        Listner.goto_Gallery();
            }
        });

        Utilities.enableBtns(false,new View[]{btn_save});


        init_Toolbar(view);
        toolbar_insideOnCreateView();
        return view;
    }


    boolean SB(){

      
        File path = new File(getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES.concat("//360Sure")).toString());

        if(path.exists() && path.isDirectory()){

            File[] files = path.listFiles();
            int last_index = files.length-1;
            if(last_index<0){
                return false;
            }
            File firstFile = files[last_index];
            if(firstFile.exists() && !firstFile.isDirectory()){

                try{

                    Picasso.with(getActivity()).load(firstFile).centerCrop().fit().into(image_clicked);


                }catch (Exception ex){
                    Log.e("SB",ex.toString());
                }
            }
                return true;
        }
return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mLiveView.stopPlay();
        Utilities.enableBtns(false,new View[]{btn_save});
    }

    @Override
    public void onResume() {
        super.onResume();
        mLiveView.play();
        init_LiveLoader();

    }

    @Override
    public void onStop() {
        super.onStop();
        Utilities.enableBtns(false,new View[]{btn_save});
        if (mLiveViewLoader != null)
        {mLiveViewLoader.cancel(true);
            mLiveViewLoader = null;
        }
    }
    private class ShowLiveViewTask extends AsyncTask<String, String, MJpegInputStream> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.start();
        }

        @Override
        protected MJpegInputStream doInBackground(String... ipAddress) {
            MJpegInputStream mjis = null;
            final int MAX_RETRY_COUNT = 20;

            for (int retryCount = 0; retryCount < MAX_RETRY_COUNT; retryCount++) {
                try {
                    publishProgress("start Live view");
                    HttpConnector camera = new HttpConnector(ipAddress[0]);
                    InputStream is = camera.getLivePreview();
                    mjis = new MJpegInputStream(is);
                    retryCount = MAX_RETRY_COUNT;
                } catch (IOException e) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } catch (JSONException e) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            return mjis;
        }



        @Override
        protected void onPostExecute(MJpegInputStream mJpegInputStream) {
            progressBar.stop();
            progressBar.setVisibility(View.GONE);
            if (mJpegInputStream != null) {
                mLiveView.setSource(mJpegInputStream);
                Utilities.enableBtns(true,new View[]{btn_save});
            } else {
                //failed to init live view
            }
        }
    }
    void init_LiveLoader(){
        if(mLiveViewLoader!=null){
            mLiveViewLoader.cancel(true);
        }
        mLiveViewLoader = new ShowLiveViewTask();
        mLiveViewLoader.execute( getResources().getString(R.string.camera_ip_address));
    }


    private class ShootTask extends AsyncTask<Void, Void, HttpConnector.ShootResult> {

        String error;

        @Override
        protected void onPreExecute() {
            //take picture
            progressBar.setVisibility(View.VISIBLE);
            progressBar.start();
        }

        @Override
        protected HttpConnector.ShootResult doInBackground(Void... params) {
            CaptureListener postviewListener = new CaptureListener();
            HttpConnector camera = new HttpConnector(getResources().getString(R.string.camera_ip_address));
            HttpConnector.ShootResult result = camera.takePicture(postviewListener);

            return result;
        }

        @Override
        protected void onPostExecute(HttpConnector.ShootResult result) {
            if (result == HttpConnector.ShootResult.FAIL_CAMERA_DISCONNECTED) {
                //cam disconn
            } else if (result == HttpConnector.ShootResult.FAIL_STORE_FULL) {
                //cam storage full
            } else if (result == HttpConnector.ShootResult.FAIL_DEVICE_BUSY) {
                // cam busy
            } else if (result == HttpConnector.ShootResult.SUCCESS) {
                // bada bing bada boom,success !!!
            }
        }

        private class CaptureListener implements HttpEventListener {
            private String latestCapturedFileId;
            private boolean ImageAdd = false;

            @Override
            public void onCheckStatus(boolean newStatus) {
                if(newStatus) {
                    // stat:finished
                } else {
                    // stat:inProgress
                }
            }

            @Override
            public void onObjectChanged(String latestCapturedFileId) {
                this.ImageAdd = true;
                this.latestCapturedFileId = latestCapturedFileId;
              
            }

            @Override
            public void onCompleted() {
                
                if (ImageAdd) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utilities.enableBtns(true,new View[]{btn_save});
                            progressBar.stop();
                            progressBar.setVisibility(View.GONE);
                          
                            new GetThumbnailTask(latestCapturedFileId).execute();
                        }
                    });
                }
            }

            @Override
            public void onError(final String errorMessage) {
               

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastGen("Failed to take a snap,err : "+errorMessage );
                        Utilities.enableBtns(true,new View[]{btn_save});
                       
                    }
                });
            }
        }

    }
    private class GetThumbnailTask extends AsyncTask<Void, String, Void> {

        private String fileId;
        byte[] image_bytes;
        public GetThumbnailTask(String fileId) {
            this.fileId = fileId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.start();

        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpConnector camera = new HttpConnector(getResources().getString(R.string.camera_ip_address));
            Bitmap thumbnail = camera.getThumb(fileId);
            if (thumbnail != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                image_bytes = baos.toByteArray();
                return null;
               
            } else {
                publishProgress("failed to get the file data.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.stop();
            progressBar.setVisibility(View.GONE);
            init_LiveLoader();
            if(image_bytes!=null){
                image_clicked.setImageBitmap(BitmapFactory.decodeByteArray(image_bytes,0,image_bytes.length));
                Listner.goto_FeedCapture(fileId,image_bytes);
            }
        }
    }



}
