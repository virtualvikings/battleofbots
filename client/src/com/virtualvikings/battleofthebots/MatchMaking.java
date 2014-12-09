package com.virtualvikings.battleofthebots;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MatchMaking extends ActionBarActivity {
	
	TextView matchTxt;
	Boolean matchFound = false;
	
	final String FileName = "Strategy";
	String code;
	
	final int port = 4444;
	final String IP = "10.0.2.2";//"145.107.119.237";
	Boolean connected = false;
	Socket socket;
	PrintWriter out;
	BufferedReader in;

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
	
	
	private void makeToast(final String s)
	{
		runOnUiThread(new Runnable() {
		    public void run() {
		        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
		    }
		});
	}
	
	Thread TalkToServer = new Thread(){

		public void run(){
			try {
				InetAddress serverAddr = InetAddress.getByName(IP);
				makeToast("Connecting...");
				
				int timeout = 10 * 1000; //Wacht 10 sec
				socket = new Socket();
				socket.connect(new InetSocketAddress(serverAddr, port), timeout);
				
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				connected = true;
				//matchTxt.setText("Searching opponent");
				
				try {
					makeToast("Searching opponent...");
					String fromServer;
					while ((fromServer = in.readLine()) != null) {
							if(fromServer.equals("requestKey"))
								out.println("Correct_Key");
							else if(fromServer.equals("requestData"))
								out.println("poep, " + code);
							else if(fromServer.equals("matchFound") || true){
								runOnUiThread(new Runnable() {

			                        @Override
			                        public void run() {
			                            Intent i = new Intent("android.intent.action.FIGHTACTIVITY");
			                            i.putExtra("mapData", "TODO put map data here");
			                            startActivity(i);
			                            finish();
			                        }
			                    });
							}
							else if (fromServer.equals("Server too busy, try again later."))
								throw new IOException(fromServer);
							else
								System.out.println("Received misc data: " + fromServer);
					}
				} catch (IOException e) {
					makeToast("Could not transmit data to server: " + e.getMessage());
					finish();
				}
				
			} catch (IOException e) {
				makeToast("Could not connect to server: " + e.getMessage());
				finish();
			}
		
		}
	};
	
	public void closeConnection() {
		if (connected) {
			try {
				out.println("exit");
				socket.close();
			} catch (IOException e) {
				makeToast("Could not close the connection");
			}
		} 
	}
}
