package com.example.macetestsample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.macetestsample.mace.AppModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkStoragePermission();
        initMaceGPUContext();
        initUI();
    }

    private void checkStoragePermission() {
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initMaceGPUContext() {
        // For V2
        AppModel.maceCreateGPUContext();
    }

    private void initUI() {
        Button runButton = (Button) findViewById(R.id.run_button);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTask();
            }
        });
    }

    private void runTask() {
        // Task V2 (Multi Model Test Task)
        MaceMultiModelTestTask task = new MaceMultiModelTestTask();
        task.start();
    }

}
