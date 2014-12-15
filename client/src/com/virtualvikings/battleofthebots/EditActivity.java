package com.virtualvikings.battleofthebots;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class EditActivity extends ActionBarActivity{

	MultiAutoCompleteTextView code;
	Boolean changed = false;
	final String FileName = "Strategy";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_edit);
		
		code = (MultiAutoCompleteTextView) findViewById(R.id.codeTxt);
		code.setTypeface(Typeface.MONOSPACE); 
		
		setupAutocomplete();
		
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

	private void setupAutocomplete() {
		String[] suggestions  = new String[] {
		         "move_up()", "move_left()", "move_right()", "move_down()", "scan()",
		         "if (hp<3) {\n\n}", 
		     };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, suggestions);
		code.setAdapter(adapter);
		code.setThreshold(1);
		code.setTokenizer(new MultiAutoCompleteTextView.Tokenizer() { //http://grepcode.com/file_/repository.grepcode.com/java/ext/com.google.android/android/4.0.1_r1/android/widget/MultiAutoCompleteTextView.java/?v=source
			
			private final char token = '\n';
			private final char end = ';';
			private final char space = ' ';
			
			public int findTokenStart(CharSequence text, int cursor) {
	            int i = cursor;

	            while (i > 0 && text.charAt(i - 1) != token) {
	                i--;
	            }
	            while (i < cursor && text.charAt(i) == space) {
	                i++;
	            }

	            return i;
	        }

	        public int findTokenEnd(CharSequence text, int cursor) {
	            int i = cursor;
	            int len = text.length();

	            while (i < len) {
	                if (text.charAt(i) == token) {
	                    return i;
	                } else {
	                    i++;
	                }
	            }

	            return len;
	        }

	        public CharSequence terminateToken(CharSequence text) {
	            int i = text.length();

	            while (i > 0 && text.charAt(i - 1) == space) {
	                i--;
	            }

	            if (i > 0 && text.charAt(i - 1) == token) {
	                return text;
	            } else {
	            	String autoCompleted = new StringBuilder().append(text).append(end).append(token).toString();
	                if (text instanceof Spanned) {
	                    SpannableString sp = new SpannableString(autoCompleted);
	                    TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
	                                            Object.class, sp, 0);
	                    return sp;
	                } else {
	                    return autoCompleted;
	                }
	            }
	        }
		} );
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
