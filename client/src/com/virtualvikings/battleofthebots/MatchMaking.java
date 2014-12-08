package com.virtualvikings.battleofthebots;

<<<<<<< HEAD
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
=======
import java.io.IOException;
>>>>>>> origin/master
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

<<<<<<< HEAD
=======
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
>>>>>>> origin/master
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
	final String IP = "145.107.119.237";
	Boolean connected = false;
	Socket socket;
	PrintWriter out;
	BufferedReader in;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_match);
<<<<<<< HEAD
		matchTxt = (TextView) findViewById(R.id.matchTxt);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		SharedPreferences readCode = getSharedPreferences(FileName, MODE_PRIVATE);
		code = readCode.getString("code", "");
=======
		
		final ProgressDialog ringProgressDialog = ProgressDialog.show(this, "Please wait",	"Searching for match...", true);
		ringProgressDialog.setCancelable(true);
		
		final Socket socket = new Socket();
		
		ringProgressDialog.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					socket.close(); //Stop verbinding als de gebruiker annuleert
				} catch (IOException e) {
					e.printStackTrace();
				}
				MatchMaking.this.finish();
		}});
		
		new Thread(new Runnable(){
			private void makeToast(final String s)
			{
				MatchMaking.this.runOnUiThread(new Runnable() {
				    public void run() {
				        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
				    }
				});
			}
			@Override
			public void run() {
				try {
					
					//"172.20.10.2"
					/*InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
					
					int timeout = 10 * 1000; //Wacht 10 seconden
					socket.connect(new InetSocketAddress(serverAddr, 4444), timeout);
					
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println("Correct_Key");
					socket.close();*/
					
					//TODO: verkrijg speelveld hier
					
					Intent goToNextActivity = new Intent(getApplicationContext(), GameActivity.class);
					startActivity(goToNextActivity);
					
					makeToast("Connection successful.");
				} catch (Exception e) {
					
					e.printStackTrace();
					String error = "Couldn't connect: ";
					
					if (e instanceof SocketTimeoutException)
						error += "timeout occured.";
					else if (e instanceof PortUnreachableException)
						error += "port unreachable.";
					else if (e instanceof NoRouteToHostException)
						error += "host unreachable.";
					else if (e instanceof ConnectException)
						error += "connection refused.";
					else if (e instanceof BindException)
						error += "port unusable.";
					else
						error += "user canceled.";
					
					makeToast(error);
					ringProgressDialog.cancel();
				}
			}}).start();
>>>>>>> origin/master
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
			try {
				InetAddress serverAddr = InetAddress.getByName(IP);
				socket = new Socket(serverAddr, port);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				connected = true;
				//matchTxt.setText("Searching opponent");
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Could not connect to server, please try again later", Toast.LENGTH_LONG).show();
				finish();
			}
			
			try {
				String fromServer;
				while ((fromServer = in.readLine()) != null) {
						if(fromServer.equals("requestKey"))
							out.println("Correct_Key");
						else if(fromServer.equals("requestData"))
							out.println("poep, " + code);
						else if(fromServer.equals("matchFound")){
							runOnUiThread(new Runnable() {

		                        @Override
		                        public void run() {
		                            Intent i = new Intent("android.intent.action.FIGHTACTIVITY");
		                            startActivity(i);
		                            finish();
		                        }
		                    });
						}
				}
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Could not transmit data to server, please try again later", Toast.LENGTH_LONG).show();
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
				Toast.makeText(getApplicationContext(), "Could not close the connection", Toast.LENGTH_SHORT).show();
			}
		} 
	}
}
