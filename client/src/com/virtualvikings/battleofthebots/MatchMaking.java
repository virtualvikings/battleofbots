package com.virtualvikings.battleofthebots;

import java.io.IOException;
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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class MatchMaking extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_match);
		
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
					InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
					
					int timeout = 10 * 1000; //Wacht 10 seconden

					socket.connect(new InetSocketAddress(serverAddr, 4444), timeout);
					
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println("Correct_Key");
					socket.close();
					
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
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		default:
			return false;
		}
	}

}
