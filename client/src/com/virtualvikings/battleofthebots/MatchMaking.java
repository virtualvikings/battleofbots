package com.virtualvikings.battleofthebots;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
		
		new Thread(new ClientThread()).start();
	}
	
	class ClientThread implements Runnable {

		@Override
		public void run() {
			
			Toast.makeText(getApplicationContext(), "Waiting...", Toast.LENGTH_SHORT);

			System.out.println("starten...");
			try {
				InetAddress serverAddr = InetAddress.getByName("172.20.10.2"/*"10.0.2.2"*/);
				//Dat werkt!

				Socket socket = new Socket(serverAddr, 4444);
				
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println("Correct_Key");
				socket.close();
				
				System.out.println("voltooid");

			} catch (Exception e1) {
				e1.printStackTrace();
				System.out.println("fout opgetreden");
			}

		}

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
