package com.virtualvikings.battleofthebots;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends ActionBarActivity{

	Button save;
	EditText code;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_edit);

		save = (Button) findViewById(R.id.saveBtn);
		code = (EditText)findViewById(R.id.codeTxt);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true); //place back button in action bar
		
		final String FileName = "Strategy";
		
		SharedPreferences settings = getSharedPreferences(FileName, MODE_PRIVATE);
	    String readCode = settings.getString("code", "void stap()\n{\n    \n}");
	    code.setText(readCode);
	    
		save.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				
				String text = code.getText().toString();
				
				try{
					SharedPreferences settings = getSharedPreferences(FileName, MODE_PRIVATE);
				    SharedPreferences.Editor editor = settings.edit();
				    editor.putString("code", text);
				    editor.commit();

					Toast.makeText(getApplicationContext(), "Saved!",
							   Toast.LENGTH_LONG).show();
				} catch(Exception e){
					Toast.makeText(getApplicationContext(), "An error occured while saving.",
							   Toast.LENGTH_LONG).show();
				}
				
				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: 
            onBackPressed();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
}
