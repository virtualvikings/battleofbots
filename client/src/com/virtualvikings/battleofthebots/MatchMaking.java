package com.virtualvikings.battleofthebots;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MatchMaking extends ActionBarActivity {
	
	TextView matchTxt;
	Boolean matchFound = false;
	
	final String FileName = "Strategy";
	String code;
	
	final int port = 4444;
	final String IP = "10.0.2.2";
	Boolean connected = false;
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	
	private static final String TAG = "MatchMaking";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_match);
		matchTxt = (TextView) findViewById(R.id.matchTxt);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		SharedPreferences readCode = getSharedPreferences(FileName, MODE_PRIVATE);
		code = readCode.getString("code", "");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		TalkToServer.start();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			closeConnection();
			finish();
			return true;

		default:
			return false;
		}
	}
	
	Thread TalkToServer = new Thread(){
		
		public void run(){
			
			Log.w(TAG, "connecting...");
			
			try {
				InetAddress serverAddr = InetAddress.getByName(IP);
				socket = new Socket(serverAddr, port);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				connected = true;
			} catch (IOException e) {
				Log.w(TAG, "failed to connect");
				//Toast.makeText(getApplicationContext(), "Could not connect to server, please try again later", Toast.LENGTH_LONG).show();
				finish();
				return; //stop
			}
			
			Log.w(TAG, "connected");
			
			try {
				String fromServer;
				while ((fromServer = in.readLine()) != null) {
						if(fromServer.equals("requestKey"))
							Log.w(TAG, "Correct_Key");
						/*else*/ if(fromServer.equals("requestData"))
							Log.w(TAG, "poep, " + code);
						/*else*/ if(fromServer.startsWith("matchFound") || true){
							final String mapData = fromServer.substring(10, fromServer.length());
							closeConnection();
							Log.w(TAG, "received map");
							runOnUiThread(new Runnable() {

		                        @Override
		                        public void run() {
		                        	Log.w(TAG, "going to activity");
		                            Intent i = new Intent("android.intent.action.GAMEACTIVITY");
		                            i.putExtra("mapData", mapData);
		                            startActivity(i);
		                            finish();
		                        }
		                    });
						}
				}
			} catch (IOException e) {
				Log.w(TAG, "failed to transmit data " + e);
				//Toast.makeText(getApplicationContext(), "Could not transmit data to server, please try again later", Toast.LENGTH_LONG).show();
				finish();
				return; //stop
			}
		}
	};
	
	public void closeConnection() {
		if (connected) {
			try {
				out.println("exit");
				socket.close();
			} catch (IOException e) {
				Log.w(TAG, "could not close the connection");
				//Toast.makeText(getApplicationContext(), "Could not close the connection", Toast.LENGTH_SHORT).show();
			}
		} 
	}
}
