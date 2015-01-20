package com.virtualvikings.battleofthebots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_splash);
		Thread Continue = new Thread(){
			
			public void run(){
				try {
					Thread.sleep(3500);
					startActivity(new Intent(getApplicationContext(), MainActivity.class));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		Continue.start();
	}

	
}
