package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.application.DTO.DataExchange;
import com.example.application.DTO.DataSent;
import com.example.application.DTO.LogResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    BluetoothConnectionService mBluetoothConnection;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    ConnectionListenerThread mConnectionListenerThread;
    private String mAssignedID = "b3e98320-a4f5-11eb-aa15-174bc8821ae7";
    private int mRSSI = 0;
    APIInterface apiInterface;
    private Encryption mEncryptor;
    private String mUsername = "Test";
    private String mPassword = "Test";


    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        mBluetoothConnection.start();
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        btnEnableDisable_Discoverable();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {

                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        btnDiscover();

                        break;

                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        btnDiscover();

                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mRSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                Connect(device);
            }
        }
    };


    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device, uuid);
    }

    public void enableBT() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableBT: Does not have BT capabilities.");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);

        }
        if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);

            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent2 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent2);

        }

    }

    public void btnEnableDisable_Discoverable() {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);

    }

    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    public void btnDiscover() {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if (!mBluetoothAdapter.isDiscovering()) {
            Log.d(TAG, "btnDiscover: sprawdzamy");

            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");


            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    public void Connect(BluetoothDevice mBTDevice) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {

            mBTDevice.createBond();
            startBTConnection(mBTDevice, MY_UUID_INSECURE);
            Log.d(TAG, "Try: " + mBTDevice.getName());
        }
    }

    private ArrayList<DataExchange> getData() {

        File folder= new File(HomeActivity.this.getFilesDir().getAbsolutePath()+"/text");
        File[] listOfFiles= folder.listFiles();


        ArrayList<DataExchange> list = new ArrayList<DataExchange>();

        for(File file: listOfFiles){
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file));
                while ((line = in.readLine()) != null) stringBuilder.append(line);
                in.close();

            } catch (FileNotFoundException e) {
                Log.d(TAG, e.toString());
            } catch (IOException e) {
               Log.d(TAG, e.toString());
            }
            String readData=stringBuilder.toString();
            DataExchange informationReceived= new Gson().fromJson(readData ,DataExchange.class);
            list.add(informationReceived);


        }

            return list;

    }

    public void SendData(){
        int i=0;

        File folder= new File(HomeActivity.this.getFilesDir().getAbsolutePath()+"/text");
        File[] listOfFiles= folder.listFiles();
        Log.d(TAG,String.valueOf(listOfFiles.length));

        for(DataExchange data: this.getData()){


            Log.d(TAG,String.valueOf(listOfFiles.length));
            Log.d(TAG,listOfFiles[i].getName());
            this.SendDataApi(data, listOfFiles[i]);
            i++;
        }

    }
    public void createTestFiles(){
      int i=0;
        File file = new File(HomeActivity.this.getFilesDir(), "text");
        if (!file.exists()) {
            file.mkdir();
        }
        while( i<10){
            DataExchange dumpData=new DataExchange(mAssignedID, mEncryptor.encrypt(Build.MODEL), mEncryptor.encrypt(Integer.toString(mRSSI)));
            String incomingMessage= (new Gson().toJson(dumpData));
            i++;
            try {
                String fileName = "";
                SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
                Date date = new Date();
                fileName = formatter.format(date);
                File txtfile = new File(file, fileName);
                txtfile.createNewFile();
                FileWriter writer = new FileWriter(txtfile);
                writer.append(incomingMessage);
                writer.flush();
                writer.close();

            } catch (Exception e){

            }

        }

    }

    public int msgCount(){
        File folder= new File(HomeActivity.this.getFilesDir().getAbsolutePath()+"/text");
        File[] listOfFiles= folder.listFiles();
        return listOfFiles.length;
    }

    public float msgWeight(){
        File folder= new File(HomeActivity.this.getFilesDir().getAbsolutePath()+"/text");

        return folder.getTotalSpace();
    }

    public void SendDataApi(DataExchange data, File file) {

        DataSent dataToSend = new DataSent(data.signalStrength, data.DeviceModel, data.ID, mAssignedID, data.Date);


        Call call = apiInterface.postMessage(dataToSend, mUsername, mPassword);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    LogResponse resObj = (LogResponse) response.body();

                    if (resObj.getResp().equals("błąd autoryzacji")) {
                        Log.d(TAG, "Wrong authorization");
                    } else {
                        Log.d(TAG, "Messenge Sent");
                        file.delete();
                    }
                } else {
                    Log.d(TAG, "Sent unsuccessfully");
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d(TAG, "Sent unsuccessfully");
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mEncryptor = new Encryption();
        TextView mTextViewLogout = (TextView) findViewById(R.id.textview_logout);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        mTextViewLogout.setOnClickListener(view -> {
            Intent LogoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(LogoutIntent);
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mAssignedID = (String) bundle.get("ID");
            mPassword = (String) bundle.get("password");
            mUsername = (String) bundle.get("username");
        }

        Button btnCollect = (Button) findViewById(R.id.send);
        Button btnDebug = (Button) findViewById(R.id.debug);


        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*createTestFiles();*/
                SendData();

            }

        });

        btnDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Button Debug clicked");
                enableBT();
                mBluetoothConnection = new BluetoothConnectionService(HomeActivity.this);
                mConnectionListenerThread = new ConnectionListenerThread();

            }
        });

    }

    private class ConnectionListenerThread extends Thread {
        ConnectionListenerThread() {
            start();
        }


        public void run() {
            while (true) {
                if (mBluetoothConnection.mState == 3) {
                    DataExchange informationToSend = new DataExchange(mAssignedID, mEncryptor.encrypt(Build.MODEL), mEncryptor.encrypt(Integer.toString(mRSSI)));
                    String jsonToSend = (new Gson().toJson(informationToSend));
                    byte[] bytesToSend = jsonToSend.getBytes();
                    mBluetoothConnection.write(bytesToSend);
                    break;
                }
            }
        }
    }


}

