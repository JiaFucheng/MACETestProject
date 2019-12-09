package com.example.macetestsample;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.macetestsample.result.InitData;
import com.example.macetestsample.tools.Representation;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AppModel.CreateEngineCallback {

    private final String TAG = "MainActivity";
    private final String MODEL_PATH = "mace_workspace/models";

    private Spinner mDeviceSpinner;
    private Button mRunButton;

    private boolean initOK = false;
    private InitData initData = new InitData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkStoragePermission();

        initUI();
    }

    private void checkStoragePermission() {
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //if (this.shouldShowRequestPermissionRationale(
            //        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            //}

            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }// else {
            // Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            //Log.e(TAG_SERVICE, "checkPermission: 已经授权！");
        //}
    }

    private void initUI() {
        mDeviceSpinner = (Spinner) findViewById(R.id.device_spinner);
        ArrayList<String> list = new ArrayList<>();
        list.add("CPU");
        list.add("GPU");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDeviceSpinner.setAdapter(adapter);

        mRunButton = (Button) findViewById(R.id.run_button);
        mRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!initOK) {
                    initInitData();
                    initJni();
                    initOK = true;
                }

                mDeviceSpinner.setEnabled(false);
                runTask();
                mDeviceSpinner.setEnabled(true);
            }
        });
    }

    private void initInitData() {
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        String modelFilePath = String.format(
                Locale.CHINA, "%s/%s/%s",
                externalStorageDirectory, MODEL_PATH, "tf_resnet152.pb");
        String modelDataPath = String.format(
                Locale.CHINA, "%s/%s/%s",
                externalStorageDirectory, MODEL_PATH, "tf_resnet152.data");
        String deviceName = InitData.DEVICES[mDeviceSpinner.getSelectedItemPosition()];

        Log.i(TAG, String.format(Locale.CHINA, "Initialize device %s", deviceName));

        initData.setModel(modelFilePath);
        initData.setModelData(modelDataPath);
        initData.setDevice(deviceName);
    }

    private void initJni() {
        AppModel.instance.maceCreateGPUContext(initData);
        AppModel.instance.maceCreateEngine(initData, this);
    }

    private void runTask() {
        runModelTask("tf_resnet152.pb", "tf_resnet152.data", Representation.IFRAME);
        runModelTask("tf_resnet18_mv.pb", "tf_resnet18_mv.data", Representation.MV);
        runModelTask("tf_resnet18_residual.pb", "tf_resnet18_residual.data", Representation.RESIDUAL);
    }

    private void runModelTask(String modelFileName, String modelDataName, int representation) {
        try {
            final int device_index = mDeviceSpinner.getSelectedItemPosition();

            resetModelData(modelFileName, modelDataName);
            resetDevice(InitData.DEVICES[device_index]);
            resetMaceEngine();
            MaceTestTask task = new MaceTestTask(representation);
            task.start();
            task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateEngineFail(final boolean quit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Failed to create inference engine with current setting:\n" + initData.getModel() + ", " + initData.getDevice());
        builder.setCancelable(false);
        builder.setPositiveButton(quit ? "Quit" : "Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (quit) {
                    System.exit(0);
                } else {
                    dialog.dismiss();
                    resetCpu();
                }
            }
        });
        builder.show();
    }

    private void resetCpu() {
        String content = InitData.DEVICES[0];
        //mSelectPhoneType.setText(content);
        initData.setDevice(content);
        AppModel.instance.maceCreateEngine(initData, MainActivity.this);
    }

    private void resetModelData(String modelFileName, String modelDataName) {
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        String modelFilePath = String.format(
                Locale.CHINA, "%s/%s/%s",
                externalStorageDirectory, MODEL_PATH, modelFileName);
        String modelDataPath = String.format(
                Locale.CHINA, "%s/%s/%s",
                externalStorageDirectory, MODEL_PATH, modelDataName);

        initData.setModel(modelFilePath);
        initData.setModelData(modelDataPath);
    }

    private void resetDevice(String device) {
        initData.setDevice(device);
    }

    private void resetMaceEngine() {
        AppModel.instance.maceCreateEngine(initData, MainActivity.this);
    }
}
