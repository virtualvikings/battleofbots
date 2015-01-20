package com.virtualvikings.battleofthebots;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class MainActivity extends Activity {

	ImageButton matchBtn, editBtn, quitBtn;
	
    public static final String PREFS_NAME = "B0TB";
	public static SharedPreferences settings;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        settings = getSharedPreferences(PREFS_NAME, 0);
        
        matchBtn = (ImageButton) findViewById(R.id.matchBtn);
        editBtn = (ImageButton) findViewById(R.id.editBtn);
        quitBtn = (ImageButton) findViewById(R.id.quitBtn);
        
        matchBtn.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View v){
        		matchBtn.setImageResource(R.drawable.fightdown);
        		startActivity(new Intent(getApplicationContext(), MatchMaking.class));
        	}
        });
        
        editBtn.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View v){
        		editBtn.setImageResource(R.drawable.editdown);
        		startActivity(new Intent(getApplicationContext(), SimpleEditActivity.class));
        	}
        });
        
        quitBtn.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View v){
        		finish();
        	}
        });
    }

	@Override
	protected void onResume() {
		super.onResume();
		matchBtn.setImageResource(R.drawable.fight);
		editBtn.setImageResource(R.drawable.edit);
	}
    
    
}
