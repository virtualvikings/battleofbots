package com.virtualvikings.battleofthebots;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends ActionBarActivity {

	EditText code;
	
	final String FileName = "Strategy";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_edit);

		code = (EditText)findViewById(R.id.codeTxt);
		code.setTypeface(Typeface.MONOSPACE); 
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true); //place back button in action bar
		
		SharedPreferences settings = getSharedPreferences(FileName, MODE_PRIVATE);
	    String readCode = settings.getString("code", "void stap()\n{\n    \n}");
	    code.setText(readCode);
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
            onBackPressed();
            return true;
        case R.id.action_save:
        	save();
        	return true;
        case R.id.action_clear:
        	clear();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	private void clear() {
		code.setText(""); 
	}

	public void save()
	{
		String text = code.getText().toString();
		
		try {
			SharedPreferences settings = getSharedPreferences(FileName, MODE_PRIVATE);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putString("code", text);
		    editor.commit();

			Toast.makeText(getApplicationContext(), "Saved!",
					   Toast.LENGTH_LONG).show();
		} catch(Exception e) {
			Toast.makeText(getApplicationContext(), "An error occured while saving.",
					   Toast.LENGTH_LONG).show();
		}
	}
	
}
