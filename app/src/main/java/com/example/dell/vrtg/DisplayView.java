package com.example.dell.vrtg;

import android.content.Context;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;



/**
 * This is a simple class for activating camera view
 */
public class DisplayView extends SurfaceView implements
        SurfaceHolder.Callback {

    DisplayView arView;
    SurfaceHolder holder;
    Camera camera;
    int screenWidth, screenHeight;
    int rotation;

    public DisplayView(Context context) {
        super(context);

        Context mcontext = context;

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        rotation = windowManager.getDefaultDisplay()
                .getRotation();

        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            try {
                List<Camera.Size> supportedSizes = null;
                supportedSizes = camera.getParameters().getSupportedPreviewSizes();


                Iterator<Camera.Size> itr = supportedSizes.iterator();
                while(itr.hasNext()) {
                    Camera.Size element = itr.next();
                    element.width -= w;
                    element.height -= h;
                }
                Collections.sort(supportedSizes, new ResolutionOrders());
                parameters.setPreviewSize(w + supportedSizes.get(supportedSizes.size()-1).width, h + supportedSizes.get(supportedSizes.size()-1).height);
            } catch (Exception ex) {
                parameters.setPreviewSize(arView.screenWidth, arView.screenHeight);
            }

            camera.setParameters(parameters);
            camera.startPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Grab the camera
        camera = Camera.open();

        // Set Display orientation
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);


        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        camera.setDisplayOrientation((info.orientation - degrees + 360) % 360);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ignore) {
                }
                try {
                    camera.release();
                } catch (Exception ignore) {
                }
                camera = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class ResolutionOrders implements java.util.Comparator<Camera.Size>{
        public int compare(Camera.Size left, Camera.Size right) {
            return Float.compare(left.width + left.height, right.width + right.height);
        }
    }
}
