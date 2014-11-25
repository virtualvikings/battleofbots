package com.virtualvikings.battleofthebots;

import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends Activity{

	Button home, save;
	EditText code;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_edit);
		home = (Button) findViewById(R.id.homeBtn);
		save = (Button) findViewById(R.id.saveBtn);
		code = (EditText)findViewById(R.id.codeTxt);
		
		home.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				finish();
			}
		});
		
		save.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				String FileName = "Strategy";// + selectedEvent;
				String text = code.getText().toString();//.toString();
				FileOutputStream osw;
				
				try{
					osw = openFileOutput(FileName, Context.MODE_PRIVATE);
					osw.write(text.getBytes());
					osw.close();
					
					Toast.makeText(getApplicationContext(), "Opgeslagen!",
							   Toast.LENGTH_LONG).show();
				} catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
		});
	}
	
}
