package com.example.application;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class BluetoothConnectionService {

    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "MYAPP";
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");


    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private BluetoothDevice mmDevice;
    private ConnectThread mConnectThread;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;
    private ConnectedThread mConnectedThread;
    private AcceptThread mInsecureAcceptThread;
    public int mState;
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState=STATE_NONE;

    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            mmServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "run: AcceptThread Running.");

            BluetoothSocket socket = null;

            try{
                Log.d(TAG, "run: RFCOM server socket start.....");
                socket = mmServerSocket.accept();
                Log.d(TAG, "run: RFCOM server socket accepted connection.");

            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            if(socket != null){
                connected(socket,mmDevice);
            }

            Log.i(TAG, "END mAcceptThread ");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage() );
            }
        }

    }

    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG, "startClient: Started.");

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");


            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        +MY_UUID_INSECURE );
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;
            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
                Log.d(TAG, "run: ConnectThread connected.");
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE );
            }
            connected(mmSocket,mmDevice);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in ConnectThread failed. " + e.getMessage());
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;


            try{
                mProgressDialog.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }


            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            mState=STATE_CONNECTED;
            int bytes;

            while (true) {

                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                    DataExchange informationRecevied= new Gson().fromJson(incomingMessage ,DataExchange.class);
                    Log.d(TAG, "InputStream: " +incomingMessage);
                    File file = new File(mContext.getFilesDir(), "text");
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    try {
                        String fileName="";
                        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
                        Date date = new Date();
                        fileName= formatter.format(date);
                        File txtfile = new File(file, fileName);
                        txtfile.createNewFile();
                        FileWriter writer = new FileWriter(txtfile);
                        writer.append(incomingMessage);
                        writer.flush();
                        writer.close();
                    } catch (Exception e) {
                    }

                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                    mState = STATE_NONE;
                    break;
                }
                catch (Exception e){
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                    mState = STATE_NONE;
                    break;
                }



            }
        }
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
                mState=STATE_NONE;
            } catch (IOException e) { }
        }
    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    public synchronized void start() {
        Log.d(TAG, "start");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }
    public void write(byte[] out) {
        Log.d(TAG, "write: Write Called.");
        mConnectedThread.write(out);
    }

}
