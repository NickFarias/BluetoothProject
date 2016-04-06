package com.test.bluetooth.activity;

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.util.*;
import org.apache.http.impl.conn.*;

public class MenuBluetooth extends Activity implements OnItemClickListener, OnTouchListener {

	ArrayAdapter<String> adaptadorListaDispositivos;
	
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public final String TAG = "BluetoothProject";
	
	// Objetos de interface
	private ListView lvDispositivos;
	
	private ImageView bTouched;
	private TextView tvStatus;
//eh o ultimo teste nao aguento mais
	
	public  BluetoothAdapter btAdapter;
	private Set<BluetoothDevice> arrayDispositivos;
	private ArrayList<BluetoothDevice> dispositivos;
	private ArrayList<String> dispositivosPareados;
	
	private IntentFilter filtroBT;
	
	private BroadcastReceiver receiverBT;
	

	private ConnectThread connectThread = null;
	private ConnectedThread connectedThread;
	
	private boolean connect = false;
	
	int X =0;
	int Y =0;
	int Xi = 0;
	int Yi = 0;
	
	String t="";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);

		setContentView(R.layout.menubluetooth);
		Log.i(TAG, "MENUBLUETOOTH STARTED");
		menuBluetoothPrincipal();
		
	}

	public void menuBluetoothPrincipal(){
		
		
		lvDispositivos = (ListView) findViewById(R.id.lvDispositivos);
		adaptadorListaDispositivos = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,0);
		lvDispositivos.setAdapter(adaptadorListaDispositivos);
		lvDispositivos.setOnItemClickListener(this);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		
		receiver();
		
        if(btAdapter==null){
			
        	Toast.makeText(getApplicationContext(), "No bluetooth detected", 0).show();
        	Log.i(TAG, "NAO");
			finish();
        }else{
			
        	if(!btAdapter.isEnabled()){
				
        		ligarBluetooth();
        	}
			Log.i(TAG,"--------Starting-------");
			
        	getPairedDevices();
        	startDiscovery();
			Log.i(TAG, "Fim.....");
        }
	}
	
	public void voltaMenuPrincipal(View v){
	
		Log.i(TAG, "BACK TO MAIN MENU");
		finish();
		
	}
	
	public void voltaMenuBluetooth(View v){
		
		Log.i(TAG, "BACK TO MAIN MENU");
		connectThread.cancel();
		setContentView(R.layout.menubluetooth);
		encontrarDispositivo(getCurrentFocus());
		tvStatus.setText("Desconectado");
	}
	
	public void encontrarDispositivo( View v){
		
		dispositivos.clear();
		adaptadorListaDispositivos = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,0);
		lvDispositivos.setAdapter(adaptadorListaDispositivos);
		dispositivosPareados = new ArrayList<String>();
		
		getPairedDevices();
		startDiscovery();
	}
	
	private void startDiscovery() {
		
		Log.i(TAG,"--------Cancel Discovering-------");
		btAdapter.cancelDiscovery();
		btAdapter.startDiscovery();

		Log.i(TAG,"--------Discovering-------");
	}
	
	
	private void ligarBluetooth() {

		Intent inBluetooth =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		
		startActivityForResult(inBluetooth, 1);
	}

	private void getPairedDevices() {

		arrayDispositivos = btAdapter.getBondedDevices();
		
		if(arrayDispositivos.size() > 0){
			
			for(BluetoothDevice dispositivo : arrayDispositivos){
				
				dispositivosPareados.add(dispositivo.getName());

			}
		}
		Log.i(TAG, "Dispositivos pareados: "+dispositivosPareados.size());
	}
	
	public void mandarMensagem(View v, String text){

		t = text;
		mHandler.obtainMessage(TEST, connectThread.getSocket()).sendToTarget();

	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
		// TODO Auto-generated method stub

		if(btAdapter.isDiscovering()){
			btAdapter.cancelDiscovery();
		}
		if(adaptadorListaDispositivos.getItem(arg2).contains("Pareado")){

			BluetoothDevice selectedDevice = dispositivos.get(arg2);
			Log.i(TAG, selectedDevice.getName());
			try{
			if(connectThread == null){
				
				connectThread = new ConnectThread(selectedDevice);
				connectThread.start();
				Log.i(TAG, "fim connect thread");
				Log.i(TAG, "OnItemClick dispositivos encontrados");
				tvStatus.setText("Conectado");
				
				
			}else if(!selectedDevice.equals(connectThread.mmDevice)){
		
				connectThread = new ConnectThread(selectedDevice);
				connectThread.start();
				Log.i(TAG, "fim connect thread");
				Log.i(TAG, "dispositivo diferente");
				tvStatus.setText("Conectado");
			}
			
			setContentView(R.layout.touch);
			bTouched = (ImageView) findViewById(R.id.bTouched);
			bTouched.setOnTouchListener(this);
			Toast.makeText(getApplicationContext(),"Conectado",0).show();
			
			}catch(Exception e){
				
				Toast.makeText(getApplicationContext(),e.getMessage(), 0).show();
			}
		}
		else{
			Toast.makeText(getApplicationContext(), "device is not paired", 0).show();
		}
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

	public void rightClick(View v){


		mandarMensagem(getCurrentFocus(),"RIGHTCLICK");
	}


	public void leftClick(View v){


		mandarMensagem(getCurrentFocus(),"LEFTCLICK");
	}
	
	public void receiver() {
		
		dispositivosPareados = new ArrayList<String>();
		btAdapter = BluetoothAdapter.getDefaultAdapter();

		filtroBT = new IntentFilter(BluetoothDevice.ACTION_FOUND);

		dispositivos = new ArrayList<BluetoothDevice>();

		receiverBT = new BroadcastReceiver(){
			
			@Override
			public void onReceive(Context context, Intent intent) {
				
				Log.i(TAG, "Receiver");
				String action = intent.getAction();

				if(BluetoothDevice.ACTION_FOUND.equals(action)){
					
					BluetoothDevice dispositivo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					
					dispositivos.add(dispositivo);
					
					String dispositivoTexto = "";
					Log.i(TAG, "for: "+dispositivos.size());
					// verificar dispositivos pareados 
					for(int i = 0; i < dispositivosPareados.size(); i++){
						Log.i(TAG, "dispositivo: " + dispositivo.getName() + " Pareado: " + dispositivosPareados.get(i));
					
						if( (dispositivo.getName() == null) && (dispositivo.getAddress().equals(dispositivosPareados.get(i))) ){
							//escrever
							dispositivoTexto = "(Pareado)";
							break;
						}else
						  if(dispositivo.getName().equals(dispositivosPareados.get(i))){
							//escrever
							dispositivoTexto = "(Pareado)";
							break;
						}
					}
					Log.i(TAG,"Dispositivo encontrado: "+dispositivo.getName());
					adaptadorListaDispositivos.add(dispositivo.getName() + 
								" "+ dispositivoTexto + " " + "\n" + dispositivo.getAddress());
				}

				else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
					// run some code
					Log.i(TAG, "ACTION_DISCOVERY_STARTED");

				}
				else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
					// run some code
					Log.i(TAG, "ACTION_DISCOVERY_FINISHED");
				}
				else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){

					Log.i(TAG, "ACTION_STATE_CHANGED");
					if(btAdapter.getState() == btAdapter.STATE_OFF){
						
						ligarBluetooth();
					}
				}

			}
		};
	
		registerReceiver(receiverBT, filtroBT);
		filtroBT = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(receiverBT, filtroBT);
		filtroBT = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiverBT, filtroBT);
		filtroBT = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiverBT, filtroBT);
	}
	
	


	public static final int SUCCESS_CONNECT = 0;
	public static final int MESSAGE_READ = 1;
	public static final int TEST = 2;

	 


	public Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			String TAG = "BluetoothProject";

			Log.i(TAG, "in handler");
			

			super.handleMessage(msg);
			switch(msg.what){

				case SUCCESS_CONNECT:
					// DO something
					connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
					//Toast.makeText(getApplicationContext(), "CONNECT", 0).show();
					String s = "successfully connected";
					connectedThread.write(s.getBytes());
					Log.i(TAG, "connected");
					//setMensagem("Connected");
					break;
				case MESSAGE_READ:
					Log.i(TAG,"waiting message");
					byte[] readBuf = (byte[])msg.obj;
					String string = new String(readBuf);
					// Toast.makeText(getApplicationContext(), string, 0).show();
					break;
				case TEST:

					connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);

					if(t.equals("")) t= "erro";
					connectedThread.write(t.getBytes());
					Log.i(TAG, "teste handler");
					//Toast.makeText(getApplicationContext(), "Message Sent", 0).show();

					break;
			}
		}
	};

	public String mensagem ="";

	public void setMensagem(String mensagem){

	 	this.mensagem = mensagem;
	}
	public String getMensagem(){

		return mensagem;
	}
	

	public class ConnectThread extends Thread {

		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final

			BluetoothSocket tmp = null;
			mmDevice = device;

			Log.i(TAG, "construct");
			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server code
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) { 

				Log.i(TAG, "get socket failed");

			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			
			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				Log.i(TAG, "connect - run");
				btAdapter.cancelDiscovery();

				mmSocket.connect();
				Log.i(TAG, "connect - succeeded");
			} catch (IOException connectException) {	

				Log.e(TAG, "connect failed");
				Log.e(TAG,connectException.getMessage());
				// Unable to connect; close the socket and get out
				try {
					Log.e(TAG,"nao conectou");

					mmSocket.close();
				} catch (IOException closeException) {

					Log.e(TAG,"Erro ao fechar");
				}catch(Exception e){

					Log.e(TAG, "travvvvvvvooooouuu "+e.getMessage());
				}
				return;
			}

			// Do work to manage the connection (in a separate thread)

			Log.i(TAG,"fooooiuiii");
		}

		public BluetoothSocket getSocket(){

			return mmSocket;
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {

				Log.i(TAG, "conect thread close socket");
				mmSocket.close();
			} catch (IOException e) { }
		}
	}
	
	
	public class ConnectedThread extends Thread {

		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private final String TAG = "BluetoothProject";

		public ConnectedThread(BluetoothSocket socket) {

			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) { 

				Log.i(TAG, "pau na conexão");
			}

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
					Log.i(TAG,buffer.toString());
					// Send the obtained bytes to the UI activity

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
				//mmOutStream.flush();
				Log.i(TAG,"data sent");
			} catch (IOException e) { 

				Log.i(TAG,"pau na escrita");
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


