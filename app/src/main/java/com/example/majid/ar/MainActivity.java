package com.example.majid.ar;

import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.hardware.Camera.Size;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.majid.ar.adapters.CameraProjectionAdapter;
import com.example.majid.ar.renders.ARCubeRenderer;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import filters.ar.ARFilter;
import filters.ar.ImageDetectionFilter;
import filters.ar.NoneARFilter;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2, OnTouchListener {
    private static final String TAG = "OCVSample::Activity";

    private CameraView mOpenCvCameraView;
    private List<Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;
    private MenuItem nextTrackerMenu;

    // The image sizes supported by the active camera.
    private List<Size> mSupportedImageSizes;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);

                    final ARFilter tapis1;
                    try {
                        // Define The Starry Night to be 1.0 units tall.
                        tapis1 = new ImageDetectionFilter(
                                MainActivity.this,
                                R.drawable.tapis1,
                                mCameraProjectionAdapter, 1.0);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " +
                                "tapis1");
                        e.printStackTrace();
                        break;
                    }

                    final ARFilter tapis2;
                    try {

                        tapis2 = new ImageDetectionFilter(
                                MainActivity.this,
                                R.drawable.tapis2,
                                mCameraProjectionAdapter, 1.0);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " +
                                "tapis2");
                        e.printStackTrace();
                        break;
                    }
                    final ARFilter tapis3;
                    try {
                        tapis3 = new ImageDetectionFilter(
                                MainActivity.this,
                                R.drawable.tapis3,
                                mCameraProjectionAdapter, 1.0);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " +
                                "tapis3");
                        e.printStackTrace();
                        break;
                    }

                    final ARFilter tapis4;
                    try {
                        tapis4 = new ImageDetectionFilter(
                                MainActivity.this,
                                R.drawable.tapis4,
                                mCameraProjectionAdapter, 1.0);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " +
                                "tapis4");
                        e.printStackTrace();
                        break;
                    }

                    final ARFilter tapis5;
                    try {

                        tapis5 = new ImageDetectionFilter(
                                MainActivity.this,
                                R.drawable.tapis5,
                                mCameraProjectionAdapter, 1.0);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " +
                                "tapis5");
                        e.printStackTrace();
                        break;
                    }

                    final ARFilter tapis6;
                    try {
                        tapis6 = new ImageDetectionFilter(
                                MainActivity.this,
                                R.drawable.tapis6,
                                mCameraProjectionAdapter, 1.0);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " +
                                "tapis6");
                        e.printStackTrace();
                        break;
                    }


                    final ARFilter tapis7;
                    try {
                        // Define The Starry Night to be 1.0 units tall.
                        tapis7 = new ImageDetectionFilter(
                                MainActivity.this,
                                R.drawable.tapis7,
                                mCameraProjectionAdapter, 1.0);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " +
                                "tapis7");
                        e.printStackTrace();
                        break;
                    }


                    final ARFilter tapis8;
                    try {
                        tapis8 = new ImageDetectionFilter(
                                MainActivity.this,
                                R.drawable.tapis8,
                                mCameraProjectionAdapter, 1.0);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " +
                                "tapis8");
                        e.printStackTrace();
                        break;
                    }

                    final ARFilter tapis9;
                    try {
                        tapis9 = new ImageDetectionFilter(
                                MainActivity.this,
                                R.drawable.tapis9,
                                mCameraProjectionAdapter, 1.0);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " +
                                "tapis9");
                        e.printStackTrace();
                        break;
                    }

                    final ARFilter tapis10;
                    try {
                        // Define The Starry Night to be 1.0 units tall.
                        tapis10 = new ImageDetectionFilter(
                                MainActivity.this,
                                R.drawable.tapis10,
                                mCameraProjectionAdapter, 1.0);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " +
                                "tapis10");
                        e.printStackTrace();
                        break;
                    }

                    mImageDetectionFilters = new ARFilter[] {
                            new NoneARFilter(),
                            tapis1,
                            tapis2,
                            tapis3,
                            tapis4,
                            tapis5,
                            tapis6,
                            tapis7,
                            tapis8,
                            tapis9,
                            tapis10
                    };

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    // Keys for storing the indices of the active filters.
    private static final String STATE_IMAGE_DETECTION_FILTER_INDEX = "imageDetectionFilterIndex";


    // The filters.
    private ARFilter[] mImageDetectionFilters;

    // An adapter between the video camera and projection matrix.
    private CameraProjectionAdapter mCameraProjectionAdapter;

    // The renderer for 3D augmentations.
    private ARCubeRenderer mARRenderer;

    // The indices of the active filters.
    private int mImageDetectionFilterIndex;

//    TextView log;

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(savedInstanceState != null){
            mImageDetectionFilterIndex = savedInstanceState.getInt(
                    STATE_IMAGE_DETECTION_FILTER_INDEX, 0);
        }else{
            mImageDetectionFilterIndex = 0;
        }


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

        //log = (TextView) findViewById(R.id.log);
        mOpenCvCameraView = (CameraView) findViewById(R.id.tutorial3_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    public void setMessage(String s){
        //log.setText("\n" + s + "" + log.getText().toString());
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

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        // Save the current filter indices.
        savedInstanceState.putInt(STATE_IMAGE_DETECTION_FILTER_INDEX,
                mImageDetectionFilterIndex);

        super.onSaveInstanceState(savedInstanceState);
    }

    public void onCameraViewStarted(int width, int height) {

//        mRgba = new Mat();
//        mGray = new Mat();
//        mView = new Mat();
//        mObject = new Mat();

    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        final Mat rgba = inputFrame.rgba();

        // Apply the active filters.
        if (mImageDetectionFilters != null) {
            mImageDetectionFilters[mImageDetectionFilterIndex].apply(
                         rgba, rgba);
        }

        return rgba;

        //return inputFrame.rgba();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        nextTrackerMenu = menu.add(0, 10001 , Menu.NONE  ,"Next Tracker");

        List<String> effects = mOpenCvCameraView.getEffectList();

        //solarize

        if (effects == null) {
            Log.e(TAG, "Color effects are not supported by device!");
            return true;
        }


        mColorEffectsMenu = menu.addSubMenu("Color Effect");
        mEffectMenuItems = new MenuItem[effects.size()];

        int idx = 0;
        ListIterator<String> effectItr = effects.listIterator();
        while(effectItr.hasNext()) {
            String element = effectItr.next();
            mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, element);
            idx++;
        }

        mResolutionMenu = menu.addSubMenu("Resolution");
        mResolutionList = mOpenCvCameraView.getResolutionList();
        mResolutionMenuItems = new MenuItem[mResolutionList.size()];

        ListIterator<Size> resolutionItr = mResolutionList.listIterator();
        idx = 0;
        while(resolutionItr.hasNext()) {
            Size element = resolutionItr.next();
            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
            idx++;
        }

        //Log.d("test" , mResolutionList.get(0).width + " h: " + mResolutionList.get(0).height);
        mCameraProjectionAdapter.setCameraParameters(
                mOpenCvCameraView.getParameters(),
                mResolutionList.get(0));

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if(item.getItemId() == 10001){
            mImageDetectionFilterIndex++;
            if (mImageDetectionFilterIndex == mImageDetectionFilters.length) {
                mImageDetectionFilterIndex = 0;
            }
            mARRenderer.filter = mImageDetectionFilters[
                    mImageDetectionFilterIndex];

            return true;
        }

        if (item.getGroupId() == 1)
        {
            mOpenCvCameraView.setEffect((String) item.getTitle());
            Toast.makeText(this, mOpenCvCameraView.getEffect(), Toast.LENGTH_SHORT).show();
        }
        else if (item.getGroupId() == 2)
        {
            int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution();
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
        }

        return true;
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