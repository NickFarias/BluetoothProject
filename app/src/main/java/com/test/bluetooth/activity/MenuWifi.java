package com.test.bluetooth.activity;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;

public class MenuWifi extends Activity{

	public EditText edtWifi;
	public Socket socket;
	
	//Moacir babo :D

	private consulta cons;
	
	private Thread cThread;
	private ClientThread cliThread;
	public OutputStream out;
	private BufferedWriter bufwriter;
	
	public final String TAG = "BluetoothProject";
	
	public int SERVERPORT = 4321;
	public String SERVER_IP = "127.0.0.1";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.menuwifi);
		
		edtWifi = (EditText) findViewById(R.id.edtWifi);
		edtWifi.setOnFocusChangeListener(new View.OnFocusChangeListener(){
			
			@Override
			public void onFocusChange(View v, boolean b){
				
				if(edtWifi.getText().toString() != null && !edtWifi.getText().toString().equals(""))
					
				Toast.makeText(getApplicationContext(), edtWifi.getText().toString(), 0).show();
			}
		});
	}
	
	public void enviar(View v){
		
		
		SERVER_IP = edtWifi.getText().toString();
		listenSocket();

	}
		
	
	public void listenSocket(){
		
		
			if(cons == null){
				cons = new consulta();
				cons.setIp(SERVER_IP);
				cons.execute();
			}
			cons.write("");
			
			
	}
	
	public void voltaMenuPrincipal(View v){

		Log.i(TAG, "BACK TO MAIN MENU");
		
		finish();

	}
	
	
	class ClientThread implements Runnable {

		@Override
		public void run() {

			restartSocket();

		}
		
		public void restartSocket(){
			
			try {

				if(socket == null){
					InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
					socket = new Socket(serverAddr, SERVERPORT);

				}
				//Toast.makeText(getApplicationContext(), "foi", 0).show();

			} catch (UnknownHostException e1) {

				//Toast.makeText(getApplicationContext(), e1.getMessage(), 0).show();
				e1.printStackTrace();
			} catch (IOException e1) {

				//Toast.makeText(getApplicationContext(), e1.getMessage(), 0).show();

				e1.printStackTrace();
			}
		}
		public void write(){
//			
//			
//			try
//			{
//				if (outwriter == null && socket != null){
//					outwriter = socket.getOutputStream();
//				}else if(socket != null){
//					outwriter.write("test".getBytes());
//					outwriter.flush();
//				}else{
//					
//					Log.d(TAG, "null socket");
//				}
//			}
//			catch (IOException e)
//			{}
		}

	}
	

class consulta extends AsyncTask<String, String, Boolean>{

	public final String TAG = "BluetoothProject";
	private String ip;
	
	

	public void setIp(String ip){
		this.ip = ip;
	}

	public String getIp(){
		return ip;
	}
	
	@Override
	protected Boolean doInBackground(String[] p1)
	{
		// TODO: Implement this method
		
		Socket socket = null;
		
		
		try{

				Log.d(TAG, "criandoSocket");
				socket = new Socket(getIp(), 4321);
				Log.d(TAG, "conectado");
				out = socket.getOutputStream();
				//byte[] buffer = new byte[1024];
				Log.i(TAG, "conexao estabelecida");

		} catch(UnknownHostException e){

				//Toast.makeText(getApplicationContext(), "host desconhecido", 0).show();
		} catch(IOException e){

				//Toast.makeText(getApplicationContext(), "sem entrada/saida", 0).show();
		}catch(Exception e){
			
			
		}

		return true;
	}

	public void write(String texto){
		
		try{
			
			out.write("testesss".getBytes());
			out.flush();
			Log.d(TAG, "tentativa enviar out");
			
			

			if(socket.isBound())
				edtWifi.setEnabled(false);
		}
		catch (Exception e){
			
			Log.e(TAG, "out eerroo");
			
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result)
	{
		// TODO: Implement this method
		try{
		
			super.onPostExecute(result);
		}catch(Exception e){
			Log.e(TAG,e.getMessage());
		}
	}
	
	
}
}


