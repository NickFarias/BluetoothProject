package com.test.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.*;
import java.lang.Thread.*;
import android.view.*;
import android.view.View.*;

public class Main_Activity extends Activity implements OnItemClickListener, OnTouchListener{

	ArrayAdapter<String> listAdapter;
	TextView tvStatus;
	ListView listView;
	EditText edtMessage;
	Button bt;
	Button bTouch;
	ImageView bTouched;
	FrameLayout ll;
	BluetoothAdapter btAdapter;
	Set<BluetoothDevice> devicesArray;
	ArrayList<String> pairedDevices;
	ArrayList<BluetoothDevice> devices;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	protected static final int SUCCESS_CONNECT = 0;
	protected static final int MESSAGE_READ = 1;
	protected static final int TEST = 2;
	IntentFilter filter;
	BroadcastReceiver receiver;
	ConnectThread connectThread;
	ConnectedThread connectedThread;
	String message = "";
	String t = "";
	String tag = "debugging";
	int Xi;
	int Yi;
	
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.i(tag, "in handler");
			super.handleMessage(msg);
			switch(msg.what){
				
			case SUCCESS_CONNECT:
				// DO something
			    connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
				Toast.makeText(getApplicationContext(), "CONNECT", 0).show();
				String s = "successfully connected";
				connectedThread.write(s.getBytes());
				Log.i(tag, "connected");
				setStatus("Connected");
				break;
			case MESSAGE_READ:
				Log.i(tag,"waiting message");
				byte[] readBuf = (byte[])msg.obj;
				String string = new String(readBuf);
				Toast.makeText(getApplicationContext(), string, 0).show();
				break;
			case TEST:
				
				connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
				
				if(t.equals("")) t = "erro";
				connectedThread.write(t.getBytes());
				//Toast.makeText(getApplicationContext(), "Message Sent", 0).show();
				t = "";
				break;
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
        if(btAdapter==null){
        	Toast.makeText(getApplicationContext(), "No bluetooth detected", 0).show();
        	finish();
        }
        else{
        	if(!btAdapter.isEnabled()){
        		turnOnBT();
        	}
			Log.i(tag,"--------Starting-------");
        	getPairedDevices();
        	startDiscovery();
        }


    }
	private void startDiscovery() {
		// TODO Auto-generated method stub
		btAdapter.cancelDiscovery();
		btAdapter.startDiscovery();
		
		Log.i(tag,"--------Discovering-------");
	}
	private void turnOnBT() {
		// TODO Auto-generated method stub
		Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(intent, 1);
	}
	private void getPairedDevices() {
		// TODO Auto-generated method stub
		devicesArray = btAdapter.getBondedDevices();
		if(devicesArray.size()>0){
			for(BluetoothDevice device:devicesArray){
				pairedDevices.add(device.getName());
				
			}
		}
	}
	
	public void touch(View e){
		
		setContentView(R.layout.touch);
		ll = (FrameLayout) findViewById(R.id.llTouch);
		bTouched = (ImageView) findViewById(R.id.bTouched);
		bTouched.setOnTouchListener(this);
	
		
	}
	private void init() {
		// TODO Auto-generated method stub
		listView=(ListView)findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		bt= (Button) findViewById(R.id.bConnectNew);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		edtMessage = (EditText) findViewById(R.id.edtMessage);
		bTouch = (Button) findViewById(R.id.bTouch);
		
		listAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,0);
		listView.setAdapter(listAdapter);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = new ArrayList<String>();
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		devices = new ArrayList<BluetoothDevice>();
	
		receiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				
				if(BluetoothDevice.ACTION_FOUND.equals(action)){
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					devices.add(device);
					String s = "";
					for(int a = 0; a < pairedDevices.size(); a++){
						if(device.getName().equals(pairedDevices.get(a))){
							//append 
							s = "(Paired)";
							break;
						}
					}
			
					listAdapter.add(device.getName()+" "+s+" "+"\n"+device.getAddress());
				}
				
				else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
					// run some code
				}
				else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
					// run some code
			
					
				
				}
				else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
					if(btAdapter.getState() == btAdapter.STATE_OFF){
						turnOnBT();
					}
				}
		  
			}
		};
		
		registerReceiver(receiver, filter);/*
		 filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(receiver, filter);
		 filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver, filter);
		 filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver, filter);*/
	}
	
	public void setStatus(String text){
		
		tvStatus.setText(text);
	}
	
	public void mandarMensagem(View v){

		t = edtMessage.getText().toString();
		mHandler.obtainMessage(TEST, connectThread.getSocket()).sendToTarget();
	}
	
	

	public void mandarMensagem(View v, String text){

		t = text;
		mHandler.obtainMessage(TEST, connectThread.getSocket()).sendToTarget();
		
	}

	@Override
	public boolean onTouch(View p1, MotionEvent event)
	{
		// TODO: Implement this method
		
		

		if((event.getAction() == MotionEvent.ACTION_DOWN)){
			
			Xi = (int) event.getX();
			Yi = (int) event.getY();
			//Toast.makeText(getApplicationContext(), Xi+" - "+Yi, Toast.LENGTH_SHORT).show();
			return true;
		}else{
		

		
			
			int Xtmp = (int) event.getX();
			int Ytmp = (int) event.getY();
			int distX = Xi - Xtmp;
			int distY = Yi - Ytmp;
			//Toast.makeText(getApplicationContext(),"Distancia"+ Xi+" - "+Yi, Toast.LENGTH_SHORT).show();
			
			mandarMensagem(getCurrentFocus(),("X: "+distX+" - Y: "+distY).toString()+" ");
			Xi = (int) event.getX();
			Yi = (int) event.getY();
			return true;
		}
		
	
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		

		registerReceiver(receiver, filter);
	}
	



		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			if(resultCode == RESULT_CANCELED){
				Toast.makeText(getApplicationContext(), "Bluetooth must be enabled to continue", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
			if(btAdapter.isDiscovering()){
				btAdapter.cancelDiscovery();
			}
			if(listAdapter.getItem(arg2).contains("Paired")){
		
				BluetoothDevice selectedDevice = devices.get(arg2);
				connectThread = new ConnectThread(selectedDevice);
				connectThread.start();
				Log.i(tag, "in click listener");
			}
			else{
				Toast.makeText(getApplicationContext(), "device is not paired", 0).show();
			}
		}
		
		private class ConnectThread extends Thread {
		
			private final BluetoothSocket mmSocket;
		    private final BluetoothDevice mmDevice;
		 
		    public ConnectThread(BluetoothDevice device) {
		        // Use a temporary object that is later assigned to mmSocket,
		        // because mmSocket is final
		        BluetoothSocket tmp = null;
		        mmDevice = device;
		        Log.i(tag, "construct");
		        // Get a BluetoothSocket to connect with the given BluetoothDevice
		        try {
		            // MY_UUID is the app's UUID string, also used by the server code
		            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
		        } catch (IOException e) { 
		        	Log.i(tag, "get socket failed");
		        	
		        }
		        mmSocket = tmp;
		    }
		 
		    public void run() {
		        // Cancel discovery because it will slow down the connection
		        btAdapter.cancelDiscovery();
		        Log.i(tag, "connect - run");
		        try {
		            // Connect the device through the socket. This will block
		            // until it succeeds or throws an exception
		            mmSocket.connect();
		            Log.i(tag, "connect - succeeded");
		        } catch (IOException connectException) {	
				
				Log.i(tag, "connect failed");
					Log.e(tag,"--------Erro-------"+connectException.getMessage());
		            // Unable to connect; close the socket and get out
		            try {
		                mmSocket.close();
		            } catch (IOException closeException) { }
		            return;
		        }
		 
		        // Do work to manage the connection (in a separate thread)
		   
		        mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
		    }
			
			public BluetoothSocket getSocket(){
				
				return mmSocket;
			}

			/** Will cancel an in-progress connection, and close the socket */
		    public void cancel() {
		        try {
		            mmSocket.close();
		        } catch (IOException e) { }
		    }
		}

		private class ConnectedThread extends Thread {
		    private final BluetoothSocket mmSocket;
		    private final InputStream mmInStream;
		    private final OutputStream mmOutStream;
			
		 
		    public ConnectedThread(BluetoothSocket socket) {
		        mmSocket = socket;
		        InputStream tmpIn = null;
		        OutputStream tmpOut = null;
		 
		        // Get the input and output streams, using temp objects because
		        // member streams are final
		        try {
		            tmpIn = socket.getInputStream();
		            tmpOut = socket.getOutputStream();
		        } catch (IOException e) { }
		 
		        mmInStream = tmpIn;
		        mmOutStream = tmpOut;
		    }
		 
		    public void run() {
		        byte[] buffer;  // buffer store for the stream
		        int bytes; // bytes returned from read()
	
		        // Keep listening to the InputStream until an exception occurs
		        while (true) {
		            try {
		                // Read from the InputStream
		            	buffer = new byte[1024];
		                bytes = mmInStream.read(buffer);
						Log.i(tag, buffer.toString());
		                // Send the obtained bytes to the UI activity
		                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
		                        .sendToTarget();
		               
		            } catch (IOException e) {
		                break;
		            }
		        }
				cancel();
		    }
		 
		    /* Call this from the main activity to send data to the remote device */
		    public void write(byte[] bytes) {
		        try {
		            mmOutStream.write(bytes);
					mmOutStream.flush();
					Log.i(tag,"data sent");
		        } catch (IOException e) { 
				
				
				}
		    }
		 
		    /* Call this from the main activity to shutdown the connection */
		    public void cancel() {
		        try {
		            mmSocket.close();
		        } catch (IOException e) { }
		    }
		}
}
