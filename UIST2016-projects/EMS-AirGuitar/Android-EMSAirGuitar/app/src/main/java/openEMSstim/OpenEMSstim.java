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
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Switch;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.NumberPicker;
import android.widget.CompoundButton;
import android.widget.AdapterView;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;
import android.media.MediaPlayer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.*;
import com.illposed.osc.*;

import openEMSstim.ems.EMSModule;
import openEMSstim.ems.IEMSModule;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;


public class OpenEMSstim extends Activity implements OnTouchListener, Observer {

    /* Parameters                             */
    /* ************************************** */

    // Custom Pattern buttons
    int bpm = 72;
    private Spinner [] spinners = new Spinner [4];
    private String[] dropdown = {"0", "1", "2"};
    private int [] emsPatterns = {0, 1, 2};
    // Store custom patterns
    private int [] patterns = new int [4];

    int curSong;

    // Store patterns of the song

    private String songPath1 = Environment.getExternalStorageDirectory() + "/Music/bob_dylan_like_a_rolling_stone_cut.mp4";
    private int bpmSong1 = 95;
    private int songStart1 = 1000; // milliseconds
    private int [] patternsSong1 = { 1,0,1,0, 1,0,1,0, 1,0,1,0, 1,0,1,0,
            1,0,1,2, 1,0,1,2, 1,0,1,2, 1,0,1,2,
            1,0,1,2, 1,1,1,1, 1,1,2,2, 1,1,2,2,
            1,1,2,2, 1,1,2,2, 1,1,2,2, 1,1,2,2,
            1,1,1,1, 1,1,2,2, 1,1,2,2, 1,1,2,2,
            1,1,2,2, 1,1,2,2, 1,1,2,2, 1,1,2,2};

    private String songPath2 = Environment.getExternalStorageDirectory() + "/Music/mayday_love_ing_cut.mp4";
    private int bpmSong2 = 196;
    private int songStart2 = 8600; // milliseconds
    private int [] patternsSong2 = { 1,0,1,0, 1,0,1,0, 1,0,1,0, 1,0,1,0,
                                    1,0,1,0, 1,0,1,0, 1,0,1,0, 1,0,1,0,
                                    1,0,1,0, 1,0,1,0, 1,0,1,0, 1,0,1,0,
                                    1,0,1,0, 1,0,1,0, 1,0,1,1, 1,1,1,2,

                                    1,2,2,2, 1,2,2,2, 1,2,2,2, 1,2,2,2,
                                    1,2,2,2, 1,2,2,2, 1,2,2,2, 1,2,2,2,
                                    1,2,2,2, 1,2,2,2, 1,2,2,2, 1,2,2,2,
                                    1,2,2,2, 1,2,2,2, 1,2,2,2, 1,2,2,2 };

    private String songPath3 = Environment.getExternalStorageDirectory() + "/Music/perfume_cosmic_explorer_cut.mp4";
    private int bpmSong3 = 100;
    private int songStart3 = 0; // milliseconds
    private int [] patternsSong3 = { 1,0,1,0, 1,0,1,0, 1,0,1,0, 1,0,1,0,
            1,0,1,0, 1,0,1,2, 1,0,1,2, 1,0,1,2,
            1,1,1,2, 1,1,1,2, 1,1,1,2, 1,1,1,2,
            1,1,1,2, 1,1,1,2, 1,1,1,2};




    // Current scale values from number pickers
    //   Channel 1
    private int LScale1 = 100;
    private int LScale2 = 100;
    private int LScale3 = 100;
    //   Channel 2
    private int RScale1 = 100;
    private int RScale2 = 100;
    private int RScale3 = 100;

    /* ************************************** */

    // Music player
    private MediaPlayer media;

    // Buttons to trigger specific scale EMS firing
    //   Channel 1
    private Button buttonLScale1;
    private Button buttonLScale2;
    //   Channel 2
    private Button buttonRScale1;
    private Button buttonRScale2;

    private TextView connectStatus;

    private Button buttonPlay;
    private Button buttonStop;

    // bpm seekbar
    private SeekBar seekBar;
    private TextView seekBarValue;

    // Colors for buttonLeftOn / buttonRightOn
    // Indicate not connecting to BLE
    private String greyColor = "#607D8B";
    // Indicate connecting to BLE
    private String blueColor = "#90A4AE";
    // Match Channel 1 LED color: green
    private String greenColor = "#4CAF50";
    // Match Channel 2 LED color: red
    private String redColor = "#F06292";

    private String configFileName = "openEMSstim_configuration.txt";
    private File configFile;

    private IEMSModule currentEmsModule;
    private String currentDeviceName = "openEMS1";
    private EditText device_name_text_field;

    private FeatureCoverFlow mCoverFlow;
    private CoverFlowAdapter mAdapter;
    private ArrayList<GameEntity> mData = new ArrayList<>(0);
    private TextSwitcher mTitle;

    // Send to computer via OSC
    private String remoteIP = "192.168.2.100";
    private int outgoingPort = 8000;

    // This is used to send messages
    private OSCPortOut oscPortOut;

    private Thread oscThread;
    private Thread emsThread;
    private boolean oscThreadRunning = false;
    private boolean emsThreadRunning = false;

    // This thread will contain all the code that pertains to OSC
    private Runnable oscRunnable = new Runnable() {
        @Override
        public void run() {
            // Log.d("TAG", "RUN");
      /* The first part of the run() method initializes the OSCPortOut for sending messages.
       *
       * For more advanced apps, where you want to change the address during runtime, you will want
       * to have this section in a different thread, but since we won't be changing addresses here,
       * we only have to initialize the address once.
       */
            Log.e("OSC", "Enter new thread ...");
            try {
                // Connect to some IP address and port
                oscPortOut = new OSCPortOut(InetAddress.getByName(remoteIP), outgoingPort);
            } catch(UnknownHostException e) {
                // Error handling when your IP isn't found
                 Log.e("err", e.toString());
                return;
            } catch(Exception e) {
                // Error handling for any other errors
                 Log.e("err", e.toString());
                return;
            }

            Log.d("TAG", "RUN2");
      /* The second part of the run() method loops infinitely and sends messages every 500
       * milliseconds.
       */
            //while (true) {
            if (oscPortOut != null) {

                ArrayList<Object> toSendReset = new ArrayList<Object>();
                toSendReset.add(1);

                OSCMessage msgReset = new OSCMessage("/reset", toSendReset);
                try {
                    // Send the messages
                    oscPortOut.send(msgReset);
                    Log.d("TAG", "SENT");
                    // Pause for half a second
                    // sleep(500);
                } catch (Exception e) {
                    // Error handling for some error
                }

                ArrayList<Object> toSendBpm = new ArrayList<Object>();
                toSendBpm.add(bpm);

                OSCMessage msgBpm = new OSCMessage("/bpm", toSendBpm);
                try {
                    // Send the messages
                    oscPortOut.send(msgBpm);
                    Log.d("TAG", "SENT");
                    // Pause for half a second
                    // sleep(500);
                } catch (Exception e) {
                    // Error handling for some error
                }

                ArrayList<Object> toSendPat = new ArrayList<Object>();
                String patternStr = "";
                for (int i = 0; i < patterns.length; ++i) {
                    patternStr += emsPatterns[patterns[i]];
                }
                Log.d("send", patternStr);
                toSendPat.add(patternStr);

                OSCMessage msgPat = new OSCMessage("/pattern", toSendPat);
                try {
                    // Send the messages
                    oscPortOut.send(msgPat);
                    Log.d("TAG", "SENT");
                    // Pause for half a second
                    // sleep(500);
                } catch (Exception e) {
                    // Error handling for some error
                }
            }
        }
    };

    // This thread will contain all the code that pertains to OSC
    private Runnable oscStopRunnable = new Runnable() {
        @Override
        public void run() {
            // Log.d("TAG", "RUN");
      /* The first part of the run() method initializes the OSCPortOut for sending messages.
       *
       * For more advanced apps, where you want to change the address during runtime, you will want
       * to have this section in a different thread, but since we won't be changing addresses here,
       * we only have to initialize the address once.
       */
            Log.e("OSC", "Enter new thread ...");
            try {
                // Connect to some IP address and port
                oscPortOut = new OSCPortOut(InetAddress.getByName(remoteIP), outgoingPort);
            } catch(UnknownHostException e) {
                // Error handling when your IP isn't found
                Log.e("err", e.toString());
                return;
            } catch(Exception e) {
                // Error handling for any other errors
                Log.e("err", e.toString());
                return;
            }

            Log.d("TAG", "RUN2");
      /* The second part of the run() method loops infinitely and sends messages every 500
       * milliseconds.
       */
            //while (true) {
            if (oscPortOut != null) {

                ArrayList<Object> toSendReset = new ArrayList<Object>();
                toSendReset.add(1);

                OSCMessage msgReset = new OSCMessage("/reset", toSendReset);
                try {
                    // Send the messages
                    oscPortOut.send(msgReset);
                    Log.d("TAG", "SENT");
                    // Pause for half a second
                    // sleep(500);
                } catch (Exception e) {
                    // Error handling for some error
                }

            }
        }
    };

    // This thread will contain all the code that pertains to OSC
    private Runnable oscSongRunnable = new Runnable() {
        @Override
        public void run() {
            // Log.d("TAG", "RUN");
      /* The first part of the run() method initializes the OSCPortOut for sending messages.
       *
       * For more advanced apps, where you want to change the address during runtime, you will want
       * to have this section in a different thread, but since we won't be changing addresses here,
       * we only have to initialize the address once.
       */
            Log.e("OSC", "Enter new thread ...");
            try {
                // Connect to some IP address and port
                oscPortOut = new OSCPortOut(InetAddress.getByName(remoteIP), outgoingPort);
            } catch(UnknownHostException e) {
                // Error handling when your IP isn't found
                Log.e("err", e.toString());
                return;
            } catch(Exception e) {
                // Error handling for any other errors
                Log.e("err", e.toString());
                return;
            }

            Log.d("TAG", "RUN2");
      /* The second part of the run() method loops infinitely and sends messages every 500
       * milliseconds.
       */
            //while (true) {
            if (oscPortOut != null) {

                ArrayList<Object> toSendSong = new ArrayList<Object>();
                toSendSong.add(curSong);

                OSCMessage msgSong = new OSCMessage("/song", toSendSong);
                try {
                    // Send the messages
                    oscPortOut.send(msgSong);
                    Log.d("TAG", "SENT");
                    // Pause for half a second
                    // sleep(500);
                } catch (Exception e) {
                    // Error handling for some error
                }

                ArrayList<Object> toSendBpm = new ArrayList<Object>();
                toSendBpm.add(bpm);

                OSCMessage msgBpm = new OSCMessage("/bpm", toSendBpm);
                try {
                    // Send the messages
                    oscPortOut.send(msgBpm);
                    Log.d("TAG", "SENT");
                    // Pause for half a second
                    // sleep(500);
                } catch (Exception e) {
                    // Error handling for some error
                }

                ArrayList<Object> toSendWait = new ArrayList<Object>();
                switch(curSong) {
                    case 1:
                        toSendWait.add(songStart1);
                        break;
                    case 2:
                        toSendWait.add(songStart2);
                        break;
                    case 3:
                        toSendWait.add(songStart3);
                        break;
                }

                OSCMessage msgWait = new OSCMessage("/wait", toSendWait);
                try {
                    // Send the messages
                    oscPortOut.send(msgWait);
                    Log.d("TAG", "SENT");
                    // Pause for half a second
                    // sleep(500);
                } catch (Exception e) {
                    // Error handling for some error
                }

                ArrayList<Object> toSendPat = new ArrayList<Object>();
                String patternStr = "";

                switch(curSong) {
                    case 1:
                        for (int i = 0; i < patternsSong1.length; ++i) {
                            patternStr += emsPatterns[patternsSong1[i]];
                        }
                        break;
                    case 2:
                        for (int i = 0; i < patternsSong2.length; ++i) {
                            patternStr += emsPatterns[patternsSong2[i]];
                        }
                        break;
                    case 3:
                        for (int i = 0; i < patternsSong3.length; ++i) {
                            patternStr += emsPatterns[patternsSong3[i]];
                        }
                        break;
                }

                toSendPat.add(patternStr);

                OSCMessage msgPat = new OSCMessage("/pattern", toSendPat);
                try {
                    // Send the messages
                    oscPortOut.send(msgPat);
                    Log.d("TAG", "SENT");
                    // Pause for half a second
                    // sleep(500);
                } catch (Exception e) {
                    // Error handling for some error
                }

            }
        }
    };

    // Start EMS for each measure
    private Runnable emsRunnable = new Runnable() {
        @Override
        public void run() {
            // Play each pattern
            while (emsThreadRunning) {
                for (int i = 0; i < patterns.length; ++i) {
                    startEMSbyPattern(patterns[i], bpm);
                }
            }
        }
    };

    // Start EMS for each measure
    private Runnable emsSongRunnable = new Runnable() {
        @Override
        public void run() {
            switch(curSong) {
                case 1:
                    // Wait until begin playing guitar
                    try {
                        Thread.sleep(songStart1);
                    } catch (InterruptedException e) {
                    }
                    // Play each pattern
                    for (int i = 0; i < patternsSong1.length; ++i) {
                        if (emsThreadRunning)
                            startEMSbyPattern(patternsSong1[i], bpmSong1);
                    }
                    break;
                case 2:
                    // Wait until begin playing guitar
                    try {
                        Thread.sleep(songStart2);
                    } catch (InterruptedException e) {
                    }
                    // Play each pattern
                    for (int i = 0; i < patternsSong2.length; ++i) {
                        if (emsThreadRunning)
                            startEMSbyPattern(patternsSong2[i], bpmSong2);
                    }
                    break;
                case 3:
                    // Wait until begin playing guitar
                    try {
                        Thread.sleep(songStart3);
                    } catch (InterruptedException e) {
                    }
                    // Play each pattern
                    for (int i = 0; i < patternsSong3.length; ++i) {
                        if (emsThreadRunning)
                            startEMSbyPattern(patternsSong3[i], bpmSong3);
                    }
                    break;
            }
        }
    };


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



        configFile = new File(this.getFilesDir(), configFileName);
        currentEmsModule = new EMSModule((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE), "");
        currentEmsModule.getBluetoothLEConnector().addObserver(this);

        // Music player
        media = new MediaPlayer();


        // UI Assignments:

        // Play/Stop buttons
        buttonPlay = (Button) findViewById(R.id.buttonPlay);
        buttonStop = (Button) findViewById(R.id.buttonStop);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBarValue = (TextView)findViewById(R.id.seekBarValue);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                         boolean fromUser) {
                bpm = progress;
                seekBarValue.setText(String.valueOf(progress) + " bpm");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

            // Pattern buttons
        spinners[0] = (Spinner) findViewById(R.id.spinner1);
        spinners[1] = (Spinner) findViewById(R.id.spinner2);
        spinners[2] = (Spinner) findViewById(R.id.spinner3);
        spinners[3] = (Spinner) findViewById(R.id.spinner4);



        // Set up spinners
        for (int i = 0; i<4; ++i) {
            Spinner spinner = spinners[i];
            ArrayAdapter<String> patternList = new ArrayAdapter<String>(this, R.layout.spinner_item, dropdown);
            spinner.setAdapter(patternList);
        }

        // Create threads
        oscThread = new Thread(oscRunnable);
        emsThread = new Thread(emsRunnable);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("buttonPlay", "Click!");

                // Read patterns from spinners
                for (int i = 0; i<4; ++i) {
                    Spinner spinner = spinners[i];
                    patterns[i] = spinner.getSelectedItemPosition();
                }

                // Send to computer via OSC
                if (oscThreadRunning) {
                    Log.d("oscThread", "stop");
                    oscThreadRunning = false;
                    oscThread.interrupt();
                }
                oscThread = new Thread(oscRunnable);
                oscThread.start();
                oscThreadRunning = true;

                // Send to EMS
                if (emsThreadRunning) {
                    Log.d("emsThread", "stop");
                    emsThreadRunning = false;
                    emsThread.interrupt();
                }
                emsThread = new Thread(emsRunnable);
                emsThread.start();
                emsThreadRunning = true;
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("buttonStop", "Click!");

                // Stop the music
                if (media.isPlaying()) {
                    media.reset();
                }

                // Send to computer via OSC
                if (oscThreadRunning) {
                    Log.d("oscThread", "stop");
                    oscThreadRunning = false;
                    oscThread.interrupt();
                }

                oscThread = new Thread(oscStopRunnable);
                oscThread.start();
                oscThreadRunning = true;

                // Send to EMS
                if (emsThreadRunning) {
                    Log.d("emsThread", "stop");
                    emsThreadRunning = false;
                    emsThread.interrupt();
                }
            }
        });


        mData.add(new GameEntity(R.drawable.bob_dylan, R.string.title1));
        mData.add(new GameEntity(R.drawable.mayday, R.string.title2));
        mData.add(new GameEntity(R.drawable.perfume, R.string.title3));

        mTitle = (TextSwitcher) findViewById(R.id.title);
        mTitle.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                LayoutInflater inflater = LayoutInflater.from(OpenEMSstim.this);
                TextView textView = (TextView) inflater.inflate(R.layout.item_title, null);
                return textView;
            }
        });
        Animation in = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
        Animation out = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
        mTitle.setInAnimation(in);
        mTitle.setOutAnimation(out);

        mAdapter = new CoverFlowAdapter(this);
        mAdapter.setData(mData);
        mCoverFlow = (FeatureCoverFlow) findViewById(R.id.coverflow);
        mCoverFlow.setAdapter(mAdapter);

        mCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("coverflow",  String.valueOf(position));
                switch(position) {
                    case 3:
                        // Song 1
                        curSong = 1;
                        // Play Music
                        Log.d("mediaPlayer", "Playing sound...");
                        playsong(songPath1);

                        seekBarValue.setText(String.valueOf(bpmSong1) + " bpm");
                        seekBar.setProgress(bpmSong1);

                        // Send to computer via OSC
                        if (oscThreadRunning) {
                            Log.d("oscThread", "stop");
                            oscThreadRunning = false;
                            oscThread.interrupt();
                        }
                        oscThread = new Thread(oscSongRunnable);
                        oscThread.start();
                        oscThreadRunning = true;

                        // Send to EMS
                        if (emsThreadRunning) {
                            Log.d("emsThread", "stop");
                            emsThreadRunning = false;
                            emsThread.interrupt();
                        }
                        emsThread = new Thread(emsSongRunnable);
                        emsThread.start();
                        emsThreadRunning = true;
                        break;
                    case 1:
                        // Song 2
                        curSong = 2;
                        // Play Music
                        Log.d("mediaPlayer", "Playing sound...");
                        playsong(songPath2);

                        seekBarValue.setText(String.valueOf(bpmSong2) + " bpm");
                        seekBar.setProgress(bpmSong2);

                        // Send to computer via OSC
                        if (oscThreadRunning) {
                            Log.d("oscThread", "stop");
                            oscThreadRunning = false;
                            oscThread.interrupt();
                        }
                        oscThread = new Thread(oscSongRunnable);
                        oscThread.start();
                        oscThreadRunning = true;

                        // Send to EMS
                        if (emsThreadRunning) {
                            Log.d("emsThread", "stop");
                            emsThreadRunning = false;
                            emsThread.interrupt();
                        }
                        emsThread = new Thread(emsSongRunnable);
                        emsThread.start();
                        emsThreadRunning = true;
                        break;
                    case 2:
                        // Song 3
                        curSong = 3;
                        // Play Music
                        Log.d("mediaPlayer", "Playing sound...");
                        playsong(songPath3);

                        seekBarValue.setText(String.valueOf(bpmSong3) + " bpm");
                        seekBar.setProgress(bpmSong3);

                        // Send to computer via OSC
                        if (oscThreadRunning) {
                            Log.d("oscThread", "stop");
                            oscThreadRunning = false;
                            oscThread.interrupt();
                        }
                        oscThread = new Thread(oscSongRunnable);
                        oscThread.start();
                        oscThreadRunning = true;

                        // Send to EMS
                        if (emsThreadRunning) {
                            Log.d("emsThread", "stop");
                            emsThreadRunning = false;
                            emsThread.interrupt();
                        }
                        emsThread = new Thread(emsSongRunnable);
                        emsThread.start();
                        emsThreadRunning = true;
                        break;
                    default:
                }
            }
        });

        mCoverFlow.setOnScrollPositionListener(new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                mTitle.setText(getResources().getString(mData.get(position).titleResId));
            }

            @Override
            public void onScrolling() {
                mTitle.setText("");
            }
        });

    }

    private void startEMSbyPattern(int pattern, int bpm) {
        int channel;
        int scale;
        int millisecPerBeat = 60000/bpm;
        // Resolve movements in a pattern
        switch (emsPatterns[pattern]) {
            case 0:
                Log.d("START", "PAUSE");
                try {
                    Thread.sleep(millisecPerBeat);
                } catch (InterruptedException e) {
                }
                break;
            case 1:
                channel = 0;
                scale = LScale1;
                Log.d("START LSCALE1 INTENSITY", String.valueOf(scale));
                currentEmsModule.setMAX_INTENSITY(scale, channel);
                currentEmsModule.setIntensity(scale, channel);
                currentEmsModule.startCommand(channel);
                try {
                    Thread.sleep(millisecPerBeat/2);
                } catch (InterruptedException e) {
                }
                currentEmsModule.stopCommand(channel);
                try {
                    Thread.sleep(millisecPerBeat/2);
                } catch (InterruptedException e) {
                }
                break;
            case 2:
                channel = 1;
                scale = LScale2;
                Log.d("START LScale2 INTENSITY", String.valueOf(scale));
                currentEmsModule.setMAX_INTENSITY(scale, channel);
                currentEmsModule.setIntensity(scale, channel);
                currentEmsModule.startCommand(channel);
                try {
                    Thread.sleep(millisecPerBeat/2);
                } catch (InterruptedException e) {
                }
                currentEmsModule.stopCommand(channel);
                try {
                    Thread.sleep(millisecPerBeat/2);
                } catch (InterruptedException e) {
                }
                break;
        }
    }

    private void playsong(String string) {
        try{
            media.reset();
            media.setDataSource(string);
            media.prepare();
            media.start();
        }catch(Exception e){

        }
    }

    // Update BLE connection status
    @Override
    public void update(Observable observable, Object data) {
        this.runOnUiThread(new Runnable() {
           @Override
           public void run() {
               if (currentEmsModule.isConnected()) {
                   Log.i("CONNECTION:", "Success");
                   connectStatus.setText("Connected!");
               } else {
                   Log.i("CONNECTION:", "Not working yet");
                   connectStatus.setText("Please restart!");
               }
           }
       });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d("ActionMenu", "Click!");

                // Create view from .xml
                LayoutInflater inflater = LayoutInflater.from(OpenEMSstim.this);
                final View viewTest = inflater.inflate(R.layout.ems_test, null);

                Builder builder = new AlertDialog.Builder(OpenEMSstim.this);
                builder.setTitle(R.string.test_ems_intensity);
                builder.setView(viewTest);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                // ble connection status
                connectStatus = (TextView) viewTest.findViewById(R.id.connectStatus);

                //ems device name text field
                device_name_text_field = (EditText) viewTest.findViewById(R.id.device_name_text);

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

                // BLE switch
                Switch toggle = (Switch) viewTest.findViewById(R.id.connect_toggle);
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

                // Buttons: Scale Control
                buttonLScale1 = (Button) viewTest.findViewById(R.id.buttonLScale1);
                buttonRScale1 = (Button) viewTest.findViewById(R.id.buttonRScale1);

                // Number Pickers: Update LScale* / RScale*

                NumberPicker numberPickerLScale1 = (NumberPicker) viewTest.findViewById(R.id.numberPickerLScale1);
                numberPickerLScale1.setMaxValue(100);
                numberPickerLScale1.setMinValue(0);
                numberPickerLScale1.setValue(100);

                NumberPicker numberPickerRScale1 = (NumberPicker) viewTest.findViewById(R.id.numberPickerRScale1);
                numberPickerRScale1.setMaxValue(100);
                numberPickerRScale1.setMinValue(0);
                numberPickerRScale1.setValue(100);

                numberPickerLScale1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        LScale1 = i1;
                        Log.d("LScale1", String.valueOf(LScale1));
                    }
                });
                numberPickerRScale1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        RScale1 = i1;
                        Log.d("LScale1", String.valueOf(RScale1));
                    }
                });

                // Scale button touched: set to the corresponding intensity (LScale* / RScale*) and trigger EMS

                buttonLScale1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            Log.d("buttonLScale1", "Touch!");
                            Log.d("INTENSITY", String.valueOf(LScale1));
                            currentEmsModule.setMAX_INTENSITY(LScale1, 0);
                            currentEmsModule.setIntensity(LScale1, 0);
                            currentEmsModule.startCommand(0);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            currentEmsModule.stopCommand(0);
                        }
                        return false;
                    }
                });
                buttonRScale1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            Log.d("buttonRScale1", "Touch!");
                            Log.d("INTENSITY", String.valueOf(RScale1));
                            currentEmsModule.setMAX_INTENSITY(RScale1, 1);
                            currentEmsModule.setIntensity(RScale1, 1);
                            currentEmsModule.startCommand(1);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            currentEmsModule.stopCommand(1);
                        }
                        return false;
                    }
                });


                builder.show();

                break;
            default:
                break;
        }

        return true;
    }
}
