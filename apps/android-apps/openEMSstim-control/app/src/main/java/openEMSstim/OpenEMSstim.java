/**
 * OpenEMSstim
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
    private RadioGroup radiosLeft;
    private RadioGroup radiosRight;

    private String greyColor = "#ffeaf6ff";

    private String configFileName = "openEMSstim_configuration.txt";
    private File configFile;

    private IEMSModule currentEmsModule;
    private String currentDeviceName = "";

    private int currentDeviceIndex = 0;
    private EditText device_name_text_field;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "OpenEMSstim";
    private EditText input;

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
                FileReader reader = new FileReader(configFile);
                BufferedReader bufferedReader = new BufferedReader(reader);
                currentDeviceName = bufferedReader.readLine();
                //auto connect read
                currentEmsModule.setDeviceName(currentDeviceName);
                device_name_text_field.setText(currentDeviceName);
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            writeDeviceNameToConfigFile();
        }

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
                    currentEmsModule.connect();
                    } else {
                    currentEmsModule.disconnect();
                }
            }
        });


        //sliders
        SeekBar intensityLeft = (SeekBar) findViewById(R.id.seekBarLeft);
        SeekBar intensityRight = (SeekBar) findViewById(R.id.seekBarRight);

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

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private AlertDialog.Builder getNewAlertDialog() {
        AlertDialog.Builder alert;
        //Creation of an AlertDialog
        alert = new AlertDialog.Builder(this);

        alert.setTitle("device ID");
        alert.setMessage("Enter the name of the device you wish to connect.");

        // Set an EditText view to get user input
        input = new EditText(this);
        input.setText(currentDeviceName);

        alert.setView(input);


        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                currentDeviceName = input.getText().toString();
                currentEmsModule.setDeviceName(input.getText().toString());
                writeDeviceNameToConfigFile();
                dialog.dismiss();

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.cancel();
            }
        });

        return alert;
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.open_ems_stim_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.getItem(1).setTitle("connect to: " + currentDeviceName);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_connect) {
            currentEmsModule.connect(); //currentEmsModule.get(currentDeviceIndex).connect();
            return true;
        }
        if (id == R.id.action_settings) {

            getNewAlertDialog().show();

        }
        if (id == R.id.action_disconnect) {
            currentEmsModule.disconnect(); //currentEmsModule.get(currentDeviceIndex).disconnect();
        }
        return super.onOptionsItemSelected(item);
    }*/


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == buttonRightOn) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (currentEmsModule.getPattern(1) == 5) {
                    //EditText tf = (EditText) findViewById(R.id.emsCommandRight);
                    //String mes = tf.getText().toString();

                    //Log.i(TAG, "Message send to Device: " + mes);
                    //if (mes != "" && mes != " ") {
                    //    currentEmsModule.sendMessageToBoard(mes);
                    //
                    //}
                } else {
                    currentEmsModule.startCommand(1);
                }

                buttonRightOn.setBackgroundColor(Color.RED);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (currentEmsModule.getPattern(1) == 5) {
                } else {

                    currentEmsModule.stopCommand(1);
                }
                buttonRightOn.setBackgroundColor(Color.GREEN);
            }
        } else if (v == buttonLeftOn) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {


                if (currentEmsModule.getPattern(0) == 5) {
                    //EditText tf = (EditText) findViewById(R.id.emsCommandLeft);
                    //String mes = tf.getText().toString();

                    //Log.i(TAG, "Message send to Device: " + mes);
                    //if (mes != "" && mes != " ") {
                    // currentEmsModule.sendMessageToBoard(mes);
                    //}
                } else {
                    Log.i(TAG, "SEND START COMMAND 0");
                    currentEmsModule.startCommand(0);
                }

                buttonLeftOn.setBackgroundColor(Color.RED);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (currentEmsModule.getPattern(0) == 5) {
                } else {
                    Log.i(TAG, "SEND STOP COMMAND 0");
                    currentEmsModule.stopCommand(0);
                }
                buttonLeftOn.setBackgroundColor(Color.GREEN);
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
                                       buttonLeftOn.setEnabled(true);
                                       buttonLeftOn.setBackgroundColor(Color.GREEN);
                                       buttonRightOn.setEnabled(true);
                                       buttonRightOn.setBackgroundColor(Color.GREEN);
                                       buttonLeftOn.invalidate();
                                       buttonRightOn.invalidate();
                                   } else {
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