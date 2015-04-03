package com.test.bluetooth.activity;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.test.bluetooth.*;

public class Main extends Activity 
{
	public final String TAG = "BluetoothProject";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		try{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Log.i(TAG, "APPLICATION STATED");
		
		}catch(Exception e){
			
			
		}
    }
	
	public void menuBluetooth(View v){
		
		try{
			
			Intent in = new Intent(this, MenuBluetooth.class);
			Log.i(TAG, "START MENUBLUETOOTH");
			startActivity(in);
			
			
		}catch(Exception e){
			
			Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
		}
		
	}
	
	public void menuWifi(View v){
		
		Toast.makeText(getApplicationContext(),"Wifi",0).show();
		try{
			
			Intent in = new Intent(this, MenuWifi.class);
			Log.i(TAG, "START MENUWIFI");
			startActivity(in);
			
			
		} catch(Exception e){
			
			Log.i(TAG, "MENUWIFI PAU");
			Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
		}
	}
}

