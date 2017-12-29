package com.deepcam.access;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.deepcam.access.view.CameraPreview;
import com.deepcam.deblocks.SDKManager;
import com.deepcam.deblocks.jnis.FaceInfo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends DrawBaseActivity implements CameraPreview.CameraCallback {

    private CameraPreview cameraPreview;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        titleString = "人脸考勤";
        cameraPreview = findViewById(R.id.cameraView);
        cameraPreview.setCameraCallback(this);
    }

    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera) {
        if ((System.currentTimeMillis() - time) > 1000) {
            time = System.currentTimeMillis();
            final int width = camera.getParameters().getPreviewSize().width;
            final int height = camera.getParameters().getPreviewSize().height;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        recognition(bytes, width, height);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }


    private void recognition(byte[] nv21, int width, int height) throws Exception {
        int[] argb = new int[width * height];
        SDKManager.newInstant().convertYUV420SPToARGB8888(nv21, argb, width, height);
        List<FaceInfo> faceInfos = SDKManager.newInstant().detection(argb, width, height, false);
        if (faceInfos != null) {
            for (FaceInfo faceInfo : faceInfos) {
                float[] feature = SDKManager.newInstant().recognition(argb, width, height, faceInfo);
                // TODO: match
            }
        } else {
            throw new Exception("Not found face");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraPreview.stopPreview();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraPreview.stop();
    }
}
