package com.deepcam.access;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.deepcam.deblocks.FaceManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by zsq on 2017/12/19.
 */

public class BaseActivity extends AppCompatActivity {

    private SweetAlertDialog dialog = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showDialog(int title, int msg, int type) {
        showDialog(getResources().getString(title), getResources().getString(msg), type);
    }

    public void showDialog(int title, String msg, int type) {
        showDialog(getResources().getString(title), msg, type);
    }

    public void showDialog(final String title, final String msg, final int type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog == null) {
                    dialog = new SweetAlertDialog(BaseActivity.this, type);
                } else {
                    dialog.changeAlertType(type);
                }
                dialog.setTitleText(title);
                dialog.setContentText(msg);
                dialog.show();
            }
        });
    }

}
