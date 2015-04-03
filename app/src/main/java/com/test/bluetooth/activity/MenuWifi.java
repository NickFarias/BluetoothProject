package com.test.bluetooth.activity;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import android.view.View.*;

public class MenuWifi extends Activity{

	public EditText edtWifi;
	private Socket socket;
	private PrintWriter out;
	
	
	
	public final String TAG = "BluetoothProject";
	
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
				
				Toast.makeText(getApplicationContext(), edtWifi.getText().toString(), 0).show();
			}
		});
	}
	
	public void enviar(View v){
		
		
		String texto = edtWifi.getText().toString();
		
		Toast.makeText(getApplicationContext(), texto, 0).show();
		listenSocket();
		//out.println(texto);
		
		
	}
	
	public void listenSocket(){
		
		consulta c = new consulta();
		c.setIp(edtWifi.getText().toString());
		c.execute();
		c.write("ttttt");
	}
	
	public void voltaMenuPrincipal(View v){

		Log.i(TAG, "BACK TO MAIN MENU");
		
		finish();

	}
	
}

class consulta extends AsyncTask<String, String, Boolean>{

	public final String TAG = "BluetoothProject";
	private String ip;
	
	OutputStream out = null;

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
				

			} catch(UnknownHostException e){

				//Toast.makeText(getApplicationContext(), "host desconhecido", 0).show();
			} catch(IOException e){

				//Toast.makeText(getApplicationContext(), "sem entrada/saida", 0).show();
			}
		
		
		
		return true;
	}

	public void write(String texto){
		
		try{
			
			out.write(texto.getBytes());
		}
		catch (IOException e){
			
			
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result)
	{
		// TODO: Implement this method
		
		super.onPostExecute(result);
	}

	
	
}
