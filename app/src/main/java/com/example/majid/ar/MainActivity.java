package com.example.majid.ar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera.Size;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.majid.ar.adapters.CameraProjectionAdapter;
import com.example.majid.ar.renders.ARCubeRenderer;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import filters.ar.ARFilter;
import filters.ar.ImageDetectionFilter;
import filters.ar.NoneARFilter;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2, OnTouchListener {
    private static final String TAG = "OCV::Activity";
    private  boolean found;
   private ARFilter mFilter;
    private CameraView mOpenCvCameraView;
    Dialog myDialog;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)  {
        @RequiresApi(api = VERSION_CODES.P)
        @Override
        public void onManagerConnected(int status)  {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                    //Put data in database
                    int[] tapis = {R.drawable.tapis31, R.drawable.tapis12, R.drawable.tapis1,
                            R.drawable.tapis10,R.drawable.tapis2, R.drawable.tapis3, R.drawable.tapis4,
                            R.drawable.tapis5, R.drawable.tapis6, R.drawable.tapis7, R.drawable.tapis8,
                            R.drawable.tapis9, R.drawable.tapis11,R.drawable.tapis13,
                            R.drawable.tapis14, R.drawable.tapis15, R.drawable.tapis18, R.drawable.tapis19,
                            R.drawable.tapis20,
                                   R.drawable.tapis21, R.drawable.tapis22,R.drawable.tapis23, R.drawable.tapis24,
                            R.drawable.tapis25, R.drawable.tapis26, R.drawable.tapis27,
                            R.drawable.tapis28, R.drawable.tapis30, R.drawable.tapis16, R.drawable.tapis29,R.drawable.tapis17 };

                    try {
                        mFilter = new ImageDetectionFilter(
                                MainActivity.this,
                                tapis,
                                mCameraProjectionAdapter, 1.0);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " +
                                "mFilter");
                        e.printStackTrace();
                        break;
                    }

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    // An adapter between the video camera and projection matrix.
    private CameraProjectionAdapter mCameraProjectionAdapter;

    // The renderer for 3D augmentations.
    private ARCubeRenderer mARRenderer;

//    TextView log;
    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        myDialog = new Dialog(this);
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.tutorial3_surface_view);

        final FrameLayout layout = (FrameLayout) findViewById(R.id.main_holder);

        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.getHolder().setFormat(
                PixelFormat.TRANSPARENT);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        glSurfaceView.setZOrderOnTop(true);
        glSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        layout.addView(glSurfaceView);

        mCameraProjectionAdapter = new CameraProjectionAdapter();


        mARRenderer = new ARCubeRenderer();
        mARRenderer.cameraProjectionAdapter =
                mCameraProjectionAdapter;

        mARRenderer.scale = 0.5f;
        glSurfaceView.setRenderer(mARRenderer);

        mOpenCvCameraView = (CameraView) findViewById(R.id.activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

    }


    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    public void onCameraViewStarted(int width, int height) {

    }

    public void onCameraViewStopped() {
    }
    int taskStatus = 0;
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        final Mat rgba = inputFrame.rgba();


            if (taskStatus == 0) {
                new ImageInitAsyncTask().execute(rgba);
                if(found){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowPopup(4)    ;                }
                });}
            }




        return rgba;


    }
    //android:screenOrientation="landscape"
    public void ShowPopup(int v) {
        TextView txtclose;
        myDialog.setContentView(R.layout.popup_window);
        txtclose = myDialog.findViewById(R.id.txtclose);
        //txtclose.setText("M");
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }


    final class ImageInitAsyncTask extends AsyncTask<Mat, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskStatus = 1;
        }

        @Override
        protected void onProgressUpdate(String... message){
            super.onProgressUpdate();

        }
        @Override
        protected String doInBackground(Mat... mats) {
            mARRenderer.filter = mFilter;
           found =  mFilter.apply(
                    mats[0], mats[0]);

            return "Done ...";
        }

        @Override
        protected void onPostExecute(String result) {
            taskStatus = 0;
        }
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG, "onTouch event");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                "/sample_picture_" + currentDateandTime + ".jpg";
        mOpenCvCameraView.takePicture(fileName);
      return true;


    }
}