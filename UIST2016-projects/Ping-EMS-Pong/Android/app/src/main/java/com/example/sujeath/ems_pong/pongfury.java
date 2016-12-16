package com.example.sujeath.ems_pong;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ble.EMSBluetoothLEService;
import ble.IEMSBluetoothLEService;

import java.util.Timer;
import java.util.TimerTask;

public class pongfury extends AppCompatActivity {

    public int viscous_uses_left = 2;
    public int deflect_uses_left = 2;

    public Button deflect_button;
    public Button viscous_button;
    public Button reset_button;

    public IEMSBluetoothLEService ble;

    int black, red, gray;

    public void deactivateButtons(){
        // Makes buttons grayed out
        deflect_button.setEnabled(false);
        viscous_button.setEnabled(false);
        if(viscous_button.getCurrentTextColor() == black){
            viscous_button.setTextColor(gray);
        }
        if(deflect_button.getCurrentTextColor() == black){
            deflect_button.setTextColor(gray);
        }
        reset_button.setEnabled(false);
        reset_button.setTextColor(gray);
    }

    public void activateButtons(){
        // Makes buttons normal
        deflect_button.setEnabled(true);
        viscous_button.setEnabled(true);
        updateLabels();
        reset_button.setEnabled(true);
        reset_button.setTextColor(black);
    }

    public void updateLabels(){
        viscous_button.setText("Magnet\n\n\n"+java.lang.Integer.toString(viscous_uses_left) + " Uses Left");
        if(viscous_uses_left == 0){
            viscous_button.setTextColor(red);
        }
        else{
            viscous_button.setTextColor(black);
        }
        deflect_button.setText("Shaky hands\n\n"+java.lang.Integer.toString(deflect_uses_left) + " Uses Left");
        if(deflect_uses_left == 0){
            deflect_button.setTextColor(red);
        }
        else{
            deflect_button.setTextColor(black);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pongfury);
        Log.i("My Activity", "Started PongFury");

        viscous_button = (Button) findViewById(R.id.viscous_button);
        deflect_button = (Button) findViewById(R.id.deflect_button);
        reset_button = (Button) findViewById(R.id.reset_button);

        ble = new EMSBluetoothLEService(BluetoothAdapter.getDefaultAdapter());
        ble.connectTo(getIntent().getStringExtra(MainActivity.DEVICE_ADDRESS));

        red = Color.parseColor("#ff0000");
        black = Color.parseColor("#000000");
        gray = Color.parseColor("#F3F3F3");
        updateLabels();

    }

    public void sendMessage(String msg){
        Log.i("BT send", "Sent: " + msg);
        if(ble != null && ble.isConnected()){
            ble.sendMessageToEMSDevice(msg);
        }
    }

    public void viscous(View view){
        if(viscous_uses_left > 0 && ble != null && ble.isConnected()){
            viscous_uses_left--;
            sendMessage("v");
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                Log.i("BT", "Inside viscous");
                activateButtons();
            }
        }, 3000);
        updateLabels();
        deactivateButtons();
    }

    public void randomDeflect(View view){
        if(deflect_uses_left > 0 && ble != null && ble.isConnected()){
            deflect_uses_left--;
            sendMessage("d");
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                Log.i("BT", "Inside shaky");
                activateButtons();
            }
        }, 3000);
        updateLabels();
        deactivateButtons();
    }

    public void resetButtons(View view){
        viscous_uses_left = 2;
        deflect_uses_left = 2;
        updateLabels();
    }

    public void handleSocketClose() {
        Log.i("My Activity", "PongFury disconnected!");
        Toast.makeText(this, "Device disconnected!", Toast.LENGTH_LONG).show();
    }
}
