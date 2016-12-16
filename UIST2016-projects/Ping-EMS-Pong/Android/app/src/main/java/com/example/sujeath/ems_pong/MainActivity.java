package com.example.sujeath.ems_pong;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/*
    Protocol for android - paddle communication
    android sends:
              'c' Check connection [Connection OK if paddle responds 'y']
              'v' Activate viscous power (slow hands) [Paddle signals power timeout with 't']
              'd' Activate deflect power (magnet) [Paddle signals power timeout with 't']
    paddle sends:
              'd' openEMS disconnected
*/

public class MainActivity extends AppCompatActivity {
    public final static String DEVICE_ADDRESS = "com.example.myfirstapp.BT_ADDRESS";
    public EditText deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(turnOn, 0);

        deviceName = (EditText) findViewById(R.id.dev_name);
    }

    public void connectBluetooth(View view) {
        Intent intent = new Intent(this, pongfury.class);
        EditText editText = (EditText) findViewById(R.id.dev_name);
        String message = editText.getText().toString();
        intent.putExtra(DEVICE_ADDRESS, message);
        startActivity(intent);
    }

}
