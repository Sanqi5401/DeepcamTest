package com.deepcam.access;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;

import com.deepcam.access.utils.SharedPreferencesUtils;
import com.deepcam.deblocks.SDKManager;
import com.deepcam.deblocks.listeners.InitListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends BaseActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private boolean isInit = false;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isFirst = (boolean) SharedPreferencesUtils.getParam(this, Constants.IS_FISRT, true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        } else {
            if (!isInit) {
                SDKManager.newInstant().init(this, new InitListener() {
                    @Override
                    public void initSuccess() {
                        isInit = true;
                        startNextActivity();
                    }

                    @Override
                    public void errorMsg(String msg) {
                        Log.e(TAG, msg);
                        showDialog(R.string.error_title, msg, SweetAlertDialog.ERROR_TYPE);
                    }

                });
            } else {
                startNextActivity();
            }
        }
    }

    private void startNextActivity() {
        if (!isFirst) {
            startActivity(new Intent(MainActivity.this, CameraActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            SharedPreferencesUtils.setParam(MainActivity.this, Constants.IS_FISRT, false);
        }
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
