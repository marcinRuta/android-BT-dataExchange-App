package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private int mCount = 0;
    String TAG = "HomeActivity";
    BluetoothStarter bluetoothStarter = new BluetoothStarter();



    @Override
    protected void onDestroy(){
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(bluetoothStarter.mBroadcastReceiver1);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bluetoothStarter.setmBluetothAdapter(BluetoothAdapter.getDefaultAdapter());
        TextView mTextViewLogout = (TextView) findViewById(R.id.textview_logout);
        mTextViewLogout.setOnClickListener (view -> {
            Intent LogoutIntent = new Intent (HomeActivity.this, LoginActivity.class);
            startActivity(LogoutIntent);
        });

        Button btnCollect = (Button) findViewById(R.id.collect);



        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableBTIntent = new Intent();
                bluetoothStarter.enableDisableBT(enableBTIntent);
            }
        });
    }



    @SuppressLint("SetTextI18n")
    public void SendMessage(View view) {
        TextView mShowCount = (TextView) findViewById(R.id.msg_count_n);
        Toast toast = Toast.makeText(this, R.string.sent_message,
                Toast.LENGTH_SHORT);
        toast.show();
        mCount++;
        if (mShowCount != null)
            mShowCount.setText(Integer.toString(mCount));

    }


}