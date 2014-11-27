package com.virtualvikings.battleofthebots;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class MatchMaking extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_match);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Thread socketSender = new Thread() {

			public void run() {
				try {
					//Toast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT);
					int portNumber = 4444;
					Socket socket = new Socket("localhost", portNumber);
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println("HOI!");
					socket.close();
					//Toast.makeText(getApplicationContext(), "Verstuurd", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
					//Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		};
		socketSender.start();
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
