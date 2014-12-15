package com.virtualvikings.battleofthebots;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends ActionBarActivity{

	AutoCompleteTextView code;
	Boolean changed = false;
	final String FileName = "Strategy";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_edit);
		
		code = (AutoCompleteTextView) findViewById(R.id.codeTxt);
		code.setTypeface(Typeface.MONOSPACE); 
		
		 String[] countries  = new String[] {
		         "Belgium", "France", "Italy", "Germany", "Spain"
		     };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, countries);
		code.setAdapter(adapter);
		
		getSupportActionBar().setTitle("Edit Bot");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		SharedPreferences settings = getSharedPreferences(FileName, MODE_PRIVATE);
		String readCode = settings.getString("code", "");
		code.setText(readCode);
		
		code.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				changed = true;
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.edit_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: 
        	if(!changed)
				finish();
			else{
				AlertDialog ad = new AlertDialog.Builder(EditActivity.this).create();
				ad.setTitle("Discard unsaved changes?");
				ad.setMessage("The code has been changed, but not saved. What do you want to do?");
				ad.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Save();
						finish();						
					}
				});
				ad.setButton(DialogInterface.BUTTON_NEGATIVE, "Discard", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						
					}
				});
				ad.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener(){
					
					@Override
					public void onClick(DialogInterface dialog, int which){
						//Do Nothing
					}
				});
				ad.show();
			}	
            return true;

        case R.id.action_save:
        	Save();
			Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
			return true;
        	
        case R.id.action_clear:
        	AlertDialog ad = new AlertDialog.Builder(EditActivity.this).create();
        	ad.setTitle("Clear code");
        	ad.setMessage("Are you sure you want to erase your code?");
        	ad.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					code.setText("");
				}
			});
        	ad.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Do Nothing					
				}
			});
        	ad.show();
        	return true;
        	
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	public void Save(){
		try{
			String text = code.getText().toString();
			SharedPreferences settings = getSharedPreferences(FileName, MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("code", text);
			editor.commit();
			changed = false;
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
