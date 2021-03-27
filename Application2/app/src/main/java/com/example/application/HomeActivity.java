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
    BluetoothAdapter mBluetothAdapter;
    String TAG = "HomeActivity";

    final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE_OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver: STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE_ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver: STATE_TURNING_ON");
                        break;
                }

            }
        }
    };

    @Override
    protected void onDestroy(){
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBluetothAdapter = BluetoothAdapter.getDefaultAdapter();
        TextView mTextViewLogout = (TextView) findViewById(R.id.textview_logout);
        mTextViewLogout.setOnClickListener (view -> {
            Intent LogoutIntent = new Intent (HomeActivity.this, LoginActivity.class);
            startActivity(LogoutIntent);
        });

        Button btnCollect = (Button) findViewById(R.id.collect);



        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 enableDisableBT();
            }
        });
    }

    public void enableDisableBT () {
        if (mBluetothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities");
        }
        if (!mBluetothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: enabling Bluetooth");

            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if (mBluetothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: disabling Bluetooth");

            mBluetothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

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