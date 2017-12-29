package com.deepcam.access;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.unstoppable.submitbuttonview.SubmitButton;

public class RegisterActivity extends DrawBaseActivity {

    public final static int REQUEST_CODE = 1000;

    private EditText edtName, edtGender, edtSector, edtNumber;
    private SubmitButton submitSingleButton, submitBulkButton;
    private ImageView imgFace;

    private String facePath, dataPath, picPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        titleString = getString(R.string.register_title);

        edtName = findViewById(R.id.edtName);
        edtGender = findViewById(R.id.edtGender);
        edtSector = findViewById(R.id.edtSector);
        edtNumber = findViewById(R.id.edtNumber);

        submitBulkButton = findViewById(R.id.submitBulkbutton);
        submitSingleButton = findViewById(R.id.submitbutton);

        imgFace = findViewById(R.id.imgFace);

        submitSingleButton.setOnClickListener(singleClickListener);
        submitBulkButton.setOnClickListener(blukClickListener);
        imgFace.setOnClickListener(imgClickListener);

    }


    private View.OnClickListener singleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener blukClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    private View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RegisterActivity.this, SelectImageActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    };


}
