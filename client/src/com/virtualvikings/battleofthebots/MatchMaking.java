package com.virtualvikings.battleofthebots;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.app.ProgressDialog;
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

		//Tijdelijk: ga naar spel activity (zelfs als verbinding niet gelukt is)
		Intent goToNextActivity = new Intent(getApplicationContext(), GameActivity.class);
		startActivity(goToNextActivity);
		
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
