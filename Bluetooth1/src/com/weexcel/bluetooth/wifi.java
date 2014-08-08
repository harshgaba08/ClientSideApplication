package com.weexcel.bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class wifi extends Activity {

    /***** Initializations and declarations of the datatypes and variables *****/
	
	

    TextView textResponse, txt_date;    // TextViews objects are declared, these can be accessed throughout the activity
    EditText editTextAddress, editTextPort, welcomeMsg ;   //EditTexts objects are declared these can be accessed throughout the activity
    Button buttonConnect, buttonClear;      //Buttons objects are declared these can be accessed throughout the activity
    PrintWriter printwriter;            //Object of PrintWriter has been created which would be used to take the data from client to server
    //Socket socket = null;              //Socket object is declared and initialised as null
    String mydate = "";                //String type variable "mydate" has been declared and initialised as empty
    String device_model = "";          //String type variable "device_model" has been declared and initialised as empty
    String part1 = "";
    String part2="";
    Socket socket1;
    InputStream inputStream;
    ByteArrayOutputStream byteArrayOutputStream;
    Thread thread;
    private String serverIpAddress = "";

    /***** onCreate Method of the Activity *****/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi);   //Layout has been set here which activity (xml file) is to be called

        /**** The objects of EditTexts, TextViews and Buttons are being initialized here *****/
        
        
        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        welcomeMsg = (EditText) findViewById(R.id.edtnew);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);
        txt_date = (TextView) findViewById(R.id.txtdate);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);  //Declaration of the method OnClick of Button

        /***** Click event of Clear Button *****/
        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textResponse.setText("");   // if Button is clicked the TextView which shows response should be set as empty
                editTextAddress.setText("");
                welcomeMsg.setText("");
                editTextPort.setText("");
            }
        });

    }
        /***** The definition of the Click Event of the Connect Button *****/

    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                		
                    String port = editTextPort.getText().toString();
                    String ip = editTextAddress.getText().toString();
                    String msgg = welcomeMsg.getText().toString();
                    if (port.equals("") && ip.equals("") && msgg.equals(""))
                    {
                        Toast.makeText(getApplicationContext(), "*Fields Cannot be left blank", Toast.LENGTH_SHORT).show();
                    }
                    else if (port.equals("") || ip.equals("") || msgg.equals(""))
                    {
                        if (port.equals(""))
                            Toast.makeText(getApplicationContext(), "*Port Number should be entered", Toast.LENGTH_SHORT).show();
                        if (ip.equals(""))
                            Toast.makeText(getApplicationContext(), "*IP Address should be entered", Toast.LENGTH_SHORT).show();
                        if (msgg.equals(""))
                            Toast.makeText(getApplicationContext(), "*Enter Message for the Server", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        
                       
                    	String device = ","+Build.MODEL;
                    	String msgforserver = msgg+device;
                    	
                    	/***** The object of MyClientTask has been created *****/
                    	
                    	 MyClientTask myClientTask = new MyClientTask(editTextAddress.getText().toString(), Integer.parseInt(editTextPort.getText().toString()),msgforserver);
                         myClientTask.execute();

                        SimpleDateFormat sdf = new SimpleDateFormat("dd - MMM - yyy, hh:mm:ss");  //Using this method to set the desired format of date and time
                        mydate = sdf.format(new Date());  //String "mydate" holds the date with the specified format using the Date method
                    }

                }
            };


    /***** MyClientTask Class has been inherited from AsyncTask to use the UI thread properly
     * and to perform the background operations on UI thread
     * without manipulating the UI thread *****/

           
            
    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;      //String type Variable Declaration to store the IP Address
        int dstPort;            //Integer type variable declaration to store the Port Number
        String response = "";   //String to store the response sent by the Server
        String msgToServer;

        /***** Parameterized Constructor to initialize the variables *****/

        MyClientTask(String addr, int port, String msgTo) {
            dstAddress = addr;
            dstPort = port;
            msgToServer = msgTo;
        }

        /***** The overriding methods of the AsyncTask class *****/

        @Override

        //Method 1...to run the long operations in the Background

        protected Void doInBackground(Void... params) {
        	Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;
            
            try {
            	
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if(msgToServer != null){
                    dataOutputStream.writeUTF(msgToServer);
                }

                response = dataInputStream.readUTF();
                
                String splitmessage = response; 
                String[] parts = splitmessage.split("\\,"); 
                part1 = parts[0];
                part2 = parts[1];

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                part1 = "Invalid IP Address or Port number";
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                part1 = "Invalid IP Address or Port number";
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        

        @Override

        //Method 2...Result from doInBackground method is passed in this overriding method of AsyncTask

        protected void onPostExecute(Void result) {
            textResponse.setText(part1);  //The response from the server has been set to the TextView(textResponse)

            if (part1.equals("Invalid IP Address or Port number"))  //if condition has been used here...if the client has entered wrong ip address or port number
                txt_date.setText("");   //txt_date is being used to show date so it should be set as empty
            else
                txt_date.setText("Connected To: "+part2+"\nConnection Date and Time: "+ mydate); //if client enters correct values then the date should be shown

            super.onPostExecute(result);
        }

    } //onPostExecute method ends here

}


