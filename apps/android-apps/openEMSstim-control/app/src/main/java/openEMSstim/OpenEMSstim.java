/**
 * openEMSstim is a mod by Pedro Lopes of the EMSTookit by Max Pfeiffer & Tim Dünte.
 * code, samples, examples and etc on openEMSstim are at: plopes.org/ems
 *
 * ---- original license below -----
 *
 *  Copyright 2016 by Tim Dünte <tim.duente@hci.uni-hannover.de>
 *  Copyright 2016 by Max Pfeiffer <max.pfeiffer@hci.uni-hannover.de>
 *
 *  Licensed under "The MIT License (MIT) – military use of this product is forbidden – V 0.2".
 *  Some rights reserved. See LICENSE.
 *
 * @license "The MIT License (MIT) – military use of this product is forbidden – V 0.2"
 * <https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home/License>
 */

package openEMSstim;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.view.View.OnFocusChangeListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import openEMSstim.ems.EMSModule;
import openEMSstim.ems.IEMSModule;


public class OpenEMSstim extends Activity implements OnTouchListener, Observer {
    private Button buttonRightOn;
    private Button buttonLeftOn;
    private String greyColor = "#ffeaf6ff";
    private String blueColor = "#BBDEFB";
    private String greenColor = "#AEEA00";

    private String configFileName = "openEMSstim_configuration.txt";
    private File configFile;

    private IEMSModule currentEmsModule;
    private String currentDeviceName = "openEMS2";
    private EditText device_name_text_field;

    private void writeDeviceNameToConfigFile() {
        try {
            FileWriter fileWriter = new FileWriter(configFile);
            fileWriter.write(currentDeviceName + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(openEMSstim.R.layout.open_ems_stim_main_window);
        // Use this check to determine whether BLE is supported on the device.
        // Then you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            finish();
        }

        //ems device name text field
        device_name_text_field = (EditText) findViewById(R.id.device_name_text);

        configFile = new File(this.getFilesDir(), configFileName);
        currentEmsModule = new EMSModule((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE), "");
        currentEmsModule.getBluetoothLEConnector().addObserver(this);

        if (configFile.exists()) {
            try {
                Log.i("CONFIG:","File exists");
                //check if there is a last saved device, if so update the textfield
                FileReader reader = new FileReader(configFile);
                BufferedReader bufferedReader = new BufferedReader(reader);
                currentDeviceName = bufferedReader.readLine();
                Log.i("CONFIG:","Current device name exists" + currentDeviceName);
                currentEmsModule.setDeviceName(currentDeviceName);
                device_name_text_field.setText(currentDeviceName);
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //save the device name
            writeDeviceNameToConfigFile();
        }

        //create UI
        buttonRightOn = (Button) findViewById(R.id.buttonRight);
        buttonLeftOn = (Button) findViewById(R.id.buttonLeft);


        buttonRightOn.setOnTouchListener(this);
        buttonLeftOn.setOnTouchListener(this);

        //options switch
        Switch toggle = (Switch) findViewById(R.id.connect_toggle);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    currentEmsModule.setDeviceName(device_name_text_field.getText().toString());
                    currentDeviceName = device_name_text_field.getText().toString();
                    currentEmsModule.connect();
                    writeDeviceNameToConfigFile();
                } else {
                    currentEmsModule.disconnect();
                }
            }
        });


        //sliders, needs tweaking
        SeekBar intensityLeft = (SeekBar) findViewById(R.id.seekBarLeft);
        SeekBar intensityRight = (SeekBar) findViewById(R.id.seekBarRight);

        buttonLeftOn.setText("Channel 1\n (at " + "100" + "% power)");
        buttonRightOn.setText("Channel 2\n (at " + "100" + "% power)");

        intensityLeft.setLeft(0);
        intensityLeft.setRight(100);
        intensityLeft.setProgress(100);
        intensityRight.setLeft(0);
        intensityRight.setRight(100);
        intensityRight.setProgress(100);

        intensityLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int channel = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Change intensity of channel 0
                currentEmsModule.setMAX_INTENSITY(progress, channel);
                currentEmsModule.setIntensity(progress, channel);
                buttonLeftOn.setText("Channel 1\n (at " + progress + "% power)");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        intensityRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int channel = 1;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //Change intensity of channel 1
                currentEmsModule.setMAX_INTENSITY(progress, channel);
                currentEmsModule.setIntensity(progress, channel);
                buttonRightOn.setText("Channel 2\n (at " + progress + "% power)");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == buttonRightOn) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                currentEmsModule.startCommand(1);
                buttonRightOn.setBackgroundColor(Color.RED);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                currentEmsModule.stopCommand(1);
                buttonRightOn.setBackgroundColor(Color.parseColor(blueColor));
            }
        } else if (v == buttonLeftOn) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                currentEmsModule.startCommand(0);
                buttonLeftOn.setBackgroundColor(Color.parseColor(greenColor));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                currentEmsModule.stopCommand(0);
                buttonLeftOn.setBackgroundColor(Color.parseColor(blueColor));
            }
        }
        v.performClick();
        return false;
    }

    @Override
    public void update(Observable observable, Object data) {
        this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   if (currentEmsModule.isConnected()) {
                                       Log.i("CONNECTION:", "Success");
                                       buttonLeftOn.setEnabled(true);


                                       buttonLeftOn.setBackgroundColor(Color.parseColor(blueColor));
                                       buttonRightOn.setEnabled(true);

                                       buttonRightOn.setBackgroundColor(Color.parseColor(blueColor));
                                       //buttonRightOn.setBackgroundColor(Color.RED);
                                       buttonLeftOn.invalidate();
                                       buttonRightOn.invalidate();
                                   } else {
                                       Log.i("CONNECTION:", "Not working yet");
                                       buttonLeftOn.setEnabled(false);
                                       buttonLeftOn.setBackgroundColor(Color.parseColor(greyColor));
                                       buttonRightOn.setEnabled(false);
                                       buttonRightOn.setBackgroundColor(Color.parseColor(greyColor));
                                       buttonLeftOn.invalidate();
                                       buttonRightOn.invalidate();
                                   }
                               }
                           }
        );

    }
}
