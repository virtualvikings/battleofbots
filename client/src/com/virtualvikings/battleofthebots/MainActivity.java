package com.virtualvikings.battleofthebots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {

	Button matchBtn, editBtn, quitBtn;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        matchBtn = (Button) findViewById(R.id.matchBtn);
        editBtn = (Button) findViewById(R.id.editBtn);
        quitBtn = (Button) findViewById(R.id.quitBtn);
        
        matchBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		startActivity(new Intent(getApplicationContext(), MatchMaking.class));
        	}
        });
        
        editBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		startActivity(new Intent(getApplicationContext(), SimpleEditActivity.class));
        	}
        });
        
        quitBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		finish();
        	}
        });
    }
}
