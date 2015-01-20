package com.virtualvikings.battleofthebots;

import java.math.BigInteger;
import java.security.SecureRandom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {

	Button matchBtn, editBtn, quitBtn;
	
    public static final String PREFS_NAME = "B0TB";
	public static SharedPreferences settings;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        settings = getSharedPreferences(PREFS_NAME, 0);
        
        String key = "name";
        
        if (!settings.contains(key)) {
        	SecureRandom r = new SecureRandom();
        	String a = new BigInteger(130, r).toString(32);
        	
        	Editor editor = settings.edit();
        	editor.putString(key, a);
        	Log.e("stuff", a);
        	editor.commit();
        }
        
        matchBtn = (Button) findViewById(R.id.matchBtn);
        editBtn = (Button) findViewById(R.id.editBtn);
        quitBtn = (Button) findViewById(R.id.quitBtn);
        
        matchBtn.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View v){
        		startActivity(new Intent(getApplicationContext(), MatchMaking.class));
        	}
        });
        
        editBtn.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View v){
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
}
