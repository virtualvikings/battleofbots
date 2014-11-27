package com.virtualvikings.battleofthebots;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class MatchMaking extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_match);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		final Activity t = this;
		
		new Thread(new Runnable(){
			
			private void makeToast(final String s)
			{
				t.runOnUiThread(new Runnable() {
				    public void run() {
				        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
				    }
				});
			}

			@Override
			public void run() {
				
				makeToast("Starten...");
				
				try {
					
					//"172.20.10.2"
					InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
					//Dat werkt!

					Socket socket = new Socket(serverAddr, 4444);
					
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println("Correct_Key");
					socket.close();
					
					makeToast("Voltooid.");

				} catch (Exception e1) {
					e1.printStackTrace();
					makeToast("Fout opgetreden.");
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
