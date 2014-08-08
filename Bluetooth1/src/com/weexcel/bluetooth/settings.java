package com.weexcel.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class settings extends Activity {

	ListView list;
	//list contents stored in array
	  String[] text = {
	    "Bluetooth (Enable/Disable)",
	      "NFC (Enable/Disable)",
	      "WiFi (Enable/Disable)"
	      
	  } ;
	  //integer array to store the image ids
	  Integer[] imageId = {
	      R.drawable.bluetooth_icon,
	      R.drawable.nfc_icon,
	      R.drawable.wifi_icon
	  };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.settings);
		super.onCreate(savedInstanceState);
		
		
		
		List_Setting_Custom_Adapter adapter = new
		        List_Setting_Custom_Adapter(settings.this, text, imageId);
		    list=(ListView)findViewById(R.id.list_settings);
		        list.setAdapter(adapter);
		        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		                @Override
		                public void onItemClick(AdapterView<?> parent, View view,
		                                        int position, long id) {
		                    
		                	switch (position) {
							case 0:
								Intent intentOpenBluetoothSettings = new Intent();
								intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS); //opens the Bluetooth Settings 
								startActivity(intentOpenBluetoothSettings); 
								break;
							case 1:
								Intent intentOpenNFCSettings = new Intent();
								intentOpenNFCSettings.setAction(Settings.ACTION_NFC_SETTINGS); //opens the NFC Settings
				                startActivity(intentOpenNFCSettings);
								break;
							case 2:
								Intent intentOpenWifiSettings = new Intent();
								intentOpenWifiSettings.setAction(android.provider.Settings.ACTION_WIFI_SETTINGS);  //opens the WIFI Settings
								startActivity(intentOpenWifiSettings); 
								
								break;
							default:
								break;
							}
		                }
		            });
		  }
		
		
		
	
}
