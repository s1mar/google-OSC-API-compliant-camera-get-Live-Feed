package s1.com.ocs.cam.fin.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;


public class MJpegView extends SurfaceView implements SurfaceHolder.Callback {
    private MJpegViewThread mMJpegViewThread = null;
    private MJpegInputStream mMJpegInputStream = null;
    private boolean existSurface = false;
    private int mDisplayWidth;
    private int mDisplayHeight;
    DisplayMetrics displayMetrics;



    
    public MJpegView(Context context) {
        super(context);
        init();
    }


    
    public MJpegView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

   
    public MJpegView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    
    private void init() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
        mDisplayWidth = getWidth();
        mDisplayHeight = getHeight();
        //mDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        //mDisplayHeight = getResources().getDisplayMetrics().heightPixels;
    }

   
    public void play() {
        if (mMJpegViewThread != null) {
            stopPlay();
        }

        if(mMJpegInputStream != null) {
            if (mMJpegViewThread != null) {
                if (mMJpegViewThread.getState() == Thread.State.NEW) {
                    mMJpegViewThread.start();
                }
            } else {
                mMJpegViewThread = new MJpegViewThread(getHolder());
                mMJpegViewThread.start();
            }
        }
    }

   
    public void stopPlay() {
        if (mMJpegViewThread != null) {
            mMJpegViewThread.cancel();
            boolean retry = true;
            while (retry) {
                try {
                    mMJpegViewThread.join();
                    retry = false;
                    mMJpegViewThread = null;
                } catch (InterruptedException e) {
                    e.getStackTrace();
                }
            }
        }
    }

    
    public void setSource(MJpegInputStream source) {
        mMJpegInputStream = source;
        play();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        existSurface = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized(holder) {
            mDisplayWidth = width;
            mDisplayHeight = height;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        existSurface = false;
        stopPlay();
    }

   
    private class MJpegViewThread extends Thread {
        private final SurfaceHolder mSurfaceHolder;
        private boolean keepRunning = true;

      
        public MJpegViewThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

       
        private Rect getImageRect(int bitmapWidth, int bitmapHeight) {
            //float bitmapAspectRatio = (float) bitmapWidth / (float) bitmapHeight;
            //float bitmapAspectRatio = 16.0f/9.0f;
            float bitmapAspectRatio = 4f/4f;

            bitmapWidth = mDisplayWidth;
            bitmapHeight = (int) (mDisplayWidth / bitmapAspectRatio);
            if (bitmapHeight > mDisplayHeight) {
                bitmapHeight = mDisplayHeight;
                bitmapWidth = (int) (mDisplayHeight * bitmapAspectRatio);
            }
            int bitmapX = (mDisplayWidth / 2) - (bitmapWidth / 2);
            int bitmapY = (mDisplayHeight / 2) - (bitmapHeight / 2);
            return new Rect(bitmapX, bitmapY, bitmapWidth + bitmapX, bitmapHeight + bitmapY);
            //return new Rect(mDisplayWidth,mDisplayHeight,mDisplayWidth+bitmapX,mDisplayHeight+bitmapY);
        }

       
        public void cancel() {
            keepRunning = false;
        }

        @Override
        public void run() {
            Bitmap bitmap;
            Rect bitmapRect;
            Canvas bitmapCanvas = null;

            while (keepRunning) {
                if (existSurface) {
                    try {
                        bitmapCanvas = mSurfaceHolder.lockCanvas();
                        synchronized (mSurfaceHolder) {
                            try {
                                if ((mMJpegInputStream != null) && (bitmapCanvas != null)) {
                                    bitmap = mMJpegInputStream.readMJpegFrame();
                                    bitmapRect = getImageRect(bitmap.getWidth(), bitmap.getHeight());
                                    bitmapCanvas.drawColor(Color.BLACK);
                                    bitmapCanvas.drawBitmap(bitmap, null, bitmapRect, new Paint());
                                    bitmap.recycle();
                                }
                            } catch (IOException e) {
                                e.getStackTrace();
                                keepRunning = false;
                            }
                        }
                    } finally {
                        if (bitmapCanvas != null) {
                            mSurfaceHolder.unlockCanvasAndPost(bitmapCanvas);
                        }
                    }
                }
            }

            bitmapCanvas = mSurfaceHolder.lockCanvas();
            synchronized (mSurfaceHolder) {
                if (bitmapCanvas != null) {
                    bitmapCanvas.drawColor(Color.BLACK);
                }
            }

            if (bitmapCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(bitmapCanvas);
            }

            if (mMJpegInputStream != null) {
                try {
                    mMJpegInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
