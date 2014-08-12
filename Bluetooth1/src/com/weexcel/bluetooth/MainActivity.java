package com.weexcel.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.provider.Settings;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private static final int REQUEST_ENABLE_BT = 0;  
	 private Boolean bt_already_On=false;
	 
	   private Button button_nfc;
	   private Button button_bt;
	   private Button button_wifi;
	   private Button button_setting;
	   private TextView txt_status;
	   NfcAdapter mNfcAdapter;
	   Context context;
	   private BluetoothAdapter myBluetoothAdapter;
	   private View v;  
public boolean nfcstatus;
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		button_bt=(Button) findViewById(R.id.button_bt);
		button_setting=(Button)findViewById(R.id.button_setting);
		button_wifi=(Button)findViewById(R.id.button_wifi);
		button_nfc=(Button)findViewById(R.id.button_nfc);
		txt_status=(TextView)findViewById(R.id.txt_status);
		button_bt.setOnClickListener(this);	
		button_setting.setOnClickListener(this);
		button_wifi.setOnClickListener(this);
		button_nfc.setOnClickListener(this);
		setBluetoothStatus();
		checkWifiState();
		check_nfc_status();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setBluetoothStatus();
		checkWifiState();
		check_nfc_status();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_bt:
			checkBluetoothSupported();
			checkBluetoothStatus();
			if (bt_already_On==true) {
				intentPassingToListDevicesAvailable();	
			}
			
			break;
		case R.id.button_setting:
			Intent intent=new Intent(MainActivity.this,settings.class);
			startActivity(intent);
	break;
		case R.id.button_wifi:
			
			enablewifi();
			Intent intent1=new Intent(MainActivity.this,wifi.class);
			startActivity(intent1);
			break;
			
case R.id.button_nfc:
			
	enable_nfc();
	if (nfcstatus) {
		Intent intent11=new Intent(MainActivity.this,NFC.class);
		startActivity(intent11);
	
	}
			break;
		default:
			break;
		}
	}
	//method to enable the wifi
	public void enablewifi() {
		WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		if(wifi.getConnectionInfo().getSSID()!= null){
			button_wifi.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_wifi_runtime, 0, 0);
		}else{
			wifi.setWifiEnabled(true);
			button_wifi.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_wifi_runtime, 0, 0);
		}
	}
	//check Bluetooth is supported or not...
	public void checkBluetoothSupported() {
		
		if(myBluetoothAdapter == null) {
	    	  txt_status.setText("Status: not supported");
	    	  
	    	  Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
	         		 Toast.LENGTH_LONG).show();
	      } 
	}
	
	
	// Check Bluetooth is On or Off if off make Bluetooth Enabled...
	
	public void checkBluetoothStatus(){
		   bt_already_On=false;
	      if (!myBluetoothAdapter.isEnabled()) {
	         Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	         startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

	         Toast.makeText(getApplicationContext(),"Bluetooth turning on..." ,
	        		 Toast.LENGTH_LONG).show();
	         
	      }
	      else{
	         Toast.makeText(getApplicationContext(),"Bluetooth is already on",
	        		 Toast.LENGTH_LONG).show();
	         bt_already_On=true;
	         
	      }
	   }
	
	public void check_nfc_status() {
		if (mNfcAdapter == null)
        {
            Toast.makeText(getApplicationContext(), "No NFC availability in this device", Toast.LENGTH_SHORT).show();
        }
        else if (mNfcAdapter != null)
        {
            if (mNfcAdapter.isEnabled() == false)
            {
                Toast.makeText(getApplicationContext(), "Please Switch on NFC", Toast.LENGTH_SHORT).show();
                button_nfc.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_nfc, 0, 0);
                nfcstatus=false;
            } else
            {
            	button_nfc.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_nfc_runtime, 0, 0);
            	nfcstatus=true;
            }
        }
	}
	public void enable_nfc() {
		if (mNfcAdapter.isEnabled() == false)
        {
			Toast.makeText(getApplicationContext(), "Please Switch on NFC", Toast.LENGTH_SHORT).show();
			Intent intentOpenNFCSettings = new Intent();
			intentOpenNFCSettings.setAction(Settings.ACTION_NFC_SETTINGS); //opens the NFC Settings
            startActivity(intentOpenNFCSettings);
            button_nfc.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_nfc_runtime, 0, 0);
            check_nfc_status();
        }else{
    		check_nfc_status();
    	}
	}
	//check the wifi enabling state
	public void checkWifiState(){
		WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		if(wifi.getConnectionInfo().getSSID()!= null){
			button_wifi.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_wifi_runtime, 0, 0);
		}else{
			button_wifi.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_wifi, 0, 0);
			
		}
	}
	
	// set Bluetooth Status on TextView..
	
	public void  setBluetoothStatus() {
		
		if(myBluetoothAdapter.isEnabled()) {
			txt_status.setText("Status: Enabled");
     button_bt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_bluetooth_runtime, 0, 0);	
		   } else {   
			   txt_status.setText("Status: Disabled");
			   button_bt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_select, 0, 0);
		   }
	}
	
	
	//GOTO the ListDevicesAvailable Activity if Bluetooth is enabled
	
			public void intentPassingToListDevicesAvailable() {
			
				if (txt_status.getText()=="Status: Enabled") {
					Intent i= new Intent(this, ListDevicesAvailable.class);
					startActivity(i);
				}
				}
		
	//onActivity Result...
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if(requestCode == REQUEST_ENABLE_BT){
			setBluetoothStatus();
			intentPassingToListDevicesAvailable();
			
		}else{
			txt_status.setText("Status: Disabled");
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}