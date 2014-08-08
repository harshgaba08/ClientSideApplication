package com.weexcel.bluetooth;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;





import android.annotation.SuppressLint;
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
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class ListDevicesAvailable extends Activity implements OnItemClickListener {

	private TextView txt_my_device_name;
	private Button btn_refresh;
	public TextView txt_server_name;
	private ListView list_paired_devices;
	private  TextView txt_my_device_address;
	private ListView list_available_devices;
	
	
	
	//Strings Useds...
	public String servername;
	public String my_device_name;
	public String my_device_address;
	public String tag = "debugging";
	
	//unique UUId (same as sever UUID) and Status Flags
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	protected static final int SUCCESS_CONNECT = 0;
	protected static final int MESSAGE_READ = 1;
	
	
	private BluetoothAdapter myBluetoothAdapter;
	public ConnectedThread connectedThread;
	public ConnectThread connectThread;
	public BluetoothDevice selectedDevice;
	public IntentFilter filter;
	public BroadcastReceiver receiver;
	private View view;
	private Timer timer;
	
	//Addapters and ArrayLists..
	  private Set<BluetoothDevice> devicesArray;
	  private ArrayList<BluetoothDevice> pairedDevices;
	  private ArrayList<BluetoothDevice> devices;
	  private ArrayAdapter<String> listAdapter;
	  private ArrayAdapter<String> BTArrayAdapter;
	  
	  
	  //Handler (Handles the Threads Responses)	
		Handler mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				Log.i(tag, "in handler");
				super.handleMessage(msg);
				switch(msg.what){
			
				case SUCCESS_CONNECT:   // If Connection is Established
					 connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
					Toast.makeText(getApplicationContext(), "CONNECT", 0).show();
					String s = "successfully connected with"+"/"+my_device_name+"/"+my_device_address;
					txt_server_name.setText("Connected To:-"+" "+servername);
					connectedThread.write(s.getBytes());
					Log.i(tag, "connected");
					break;
					
				case MESSAGE_READ:
					byte[] readBuf = (byte[])msg.obj;
					String string = new String(readBuf);
					Toast.makeText(getApplicationContext(), string, 0).show();
                	start();
					break;
				}
			}
		};
		
		
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listdevicesavailable);
		
		myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		txt_my_device_address=(TextView)findViewById(R.id.txt_my_device_address);
		txt_my_device_name=(TextView) findViewById(R.id.txt_my_device_name);
		list_available_devices = (ListView)findViewById(R.id.list_available_devices);
		list_paired_devices=(ListView)findViewById(R.id.list_paired_devices);
		txt_server_name=(TextView)findViewById(R.id.txt_server_name);
		list_available_devices.setOnItemClickListener(this);
		list_paired_devices.setOnItemClickListener(this);
		btn_refresh=(Button)findViewById(R.id.btn_refresh);
		pairedDevices = new ArrayList<BluetoothDevice>();
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		devices = new ArrayList<BluetoothDevice>();
		BTArrayAdapter = new ArrayAdapter<String>(ListDevicesAvailable.this, R.layout.listview);
		listAdapter= new ArrayAdapter<String>(ListDevicesAvailable.this,R.layout.listview);
		refreshtimer();  //Starts the refresh Button Timer
		start();        // makes the previous threads clear and any allocation if any 
		list_available_devices.setAdapter(BTArrayAdapter);
        list_paired_devices.setAdapter(listAdapter); 
          
          //Refresh Button Click Event...
          btn_refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				find(view);
				Log.e(tag, ".....refresh button click......");
			}
		});
	          
	             
	      //...Checks Bluetooth Enables if yes starts Starts Discovering...
	      if (!myBluetoothAdapter.isEnabled()) {
	    	  
	    	  Toast.makeText(this,"Please Turn On the Bluetooth",
	         		 Toast.LENGTH_LONG).show();
	    	  } else{
	    		  my_device_name=myBluetoothAdapter.getName();
	    		  txt_my_device_name.setText("Name:-"+" "+my_device_name);
	    		   my_device_address=myBluetoothAdapter.getAddress();
	    		   txt_my_device_address.setText("MAC Address:-"+" "+my_device_address);
	    		   getPairedDevices(); // Returns the already Paired Devices 
	    		  find(view); // starts Discovering of Available BlueTooth Devices..
	    	  }
	      
	}
	
	// Clears the Previous Threads
	public synchronized void start()   
	{   
		if (connectThread !=null) {
			connectThread.cancel();
			//connectThread.stop(null);
			connectThread=null;
		}
		if(connectedThread !=null){
			connectedThread.cancel();
			//connectedThread.stop(null);
			connectedThread=null;
		}
		
	}
	
	//Broadcast Receiver
	final BroadcastReceiver bReceiver = new BroadcastReceiver() {
	    @SuppressLint("NewApi")
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	             // Get the BluetoothDevice object from the Intent
	        	 BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	        	 // add the name and the MAC address of the object to the arrayAdapter
	        	 devices.add(device);
	             BTArrayAdapter.add(device.getName()+"\n"+device.getAddress());
	             BTArrayAdapter.notifyDataSetChanged();
	             listAdapter.notifyDataSetChanged();
	        }
	    }
	};
	

	// Finds the all Available Devices...
	public void find(View view) {
		   if (myBluetoothAdapter.isDiscovering()) {
			   myBluetoothAdapter.cancelDiscovery();
			   timer.cancel();
				timer.purge();
		   }
		   else {
			   timer.cancel();
				timer.purge();
				BTArrayAdapter.clear();
				myBluetoothAdapter.startDiscovery();
				refreshtimer();
				registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));	
				btn_refresh.startAnimation( 
 					    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.refresh_rotate) );
			}    
	   }
	
	
// timer for Refresh Button	
	public void refreshtimer() {
		timer=new Timer();
        timer.schedule(new TimerTask() {
       	  @Override
       	  public void run() {
       	    // Your database code here
       		  btn_refresh.clearAnimation();
       	  }
       	}, 60000);
	}
	
	
	// Finds and Returns the already Paired Bluetooth Devices..
	public void getPairedDevices() {
			//get all Bonded Devices...
		devicesArray = myBluetoothAdapter.getBondedDevices();
		if(devicesArray.size()>0){
			for(BluetoothDevice device:devicesArray){
				String s="(Paired)";
				listAdapter.add(device.getName()+s+"\n"+device.getAddress());
				pairedDevices.add(device);
			}
			}
		}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		timer.cancel();
		timer.purge();
		super.onDestroy();
		
		try {
			unregisterReceiver(bReceiver);	
		} catch (Exception e) {
			Log.e(tag, " reciver can't be unregister");
		}
		
	}
	
	
	@Override
	protected void onPause() {
	
		try {
			unregisterReceiver(bReceiver);
		} catch (Exception e) {
			Log.e(tag, " reciver can't be unregister");
		}
		timer.cancel();
		timer.purge();
		super.onPause();
	}
	
	
   // Connection Thread which establishes the connection with server...	
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
	        myBluetoothAdapter.cancelDiscovery();
	        Log.i(tag, "connect - run");
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	            Log.i(tag, "connect - succeeded");
	        } catch (IOException connectException) {	Log.i(tag, "connect failed");
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	   
	        mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
	    }
	 


		/** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}

	//Connected Thread which sends and receive the data to and from the server....
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
	                // Send the obtained bytes to the UI activity
	                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
	                        .sendToTarget();
	               
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	        	mmInStream.close();
		        mmOutStream.close();
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}

	
	// List Item Click Events....
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch(parent.getId())
		{
		case R.id.list_paired_devices:
			
			if(myBluetoothAdapter.isDiscovering()){
				myBluetoothAdapter.cancelDiscovery();
			}
			if(listAdapter.getItem(position).contains("Paired")){

				btn_refresh.clearAnimation();
				timer.cancel();
				timer.purge();
				selectedDevice = pairedDevices.get(position);
				servername=(String) list_paired_devices.getItemAtPosition(position);
				ConnectThread connect = new ConnectThread(selectedDevice);
				connect.start();
				Log.e(tag, "in click list_available_devices listener");
			}
			else{
				Toast.makeText(getApplicationContext(), "device is not paired", 0).show();
			}
			
			break;
			
			
			
		case R.id.list_available_devices:
			
			if(myBluetoothAdapter.isDiscovering()){
				myBluetoothAdapter.cancelDiscovery();
			}
			btn_refresh.clearAnimation();
			timer.cancel();
			timer.purge();
			selectedDevice = devices.get(position);
			servername=(String) list_available_devices.getItemAtPosition(position);
			connectThread = new ConnectThread(selectedDevice);
			connectThread.start();
			Log.e(tag, String.valueOf(position));
			Log.e(tag, "listavailabledevices");
			break;
		}
	}


	
	
}
