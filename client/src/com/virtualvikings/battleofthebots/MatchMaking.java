package com.virtualvikings.battleofthebots;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MatchMaking extends Activity{

	Button home;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_match);
		
		home = (Button) findViewById(R.id.homeBtn);
		
		home.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				finish();
			}
		});
	}

}
