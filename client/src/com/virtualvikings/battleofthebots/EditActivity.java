package com.virtualvikings.battleofthebots;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
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

			//boolean avoidStackOverflow = false;
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			//@SuppressLint("NewApi")
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				changed = true;
				//if (avoidStackOverflow) return;
				//avoidStackOverflow = true;
				//code.setText(code.getText(), false); //zet filtering uit!
				//avoidStackOverflow = false;
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});
	}

	private void setupAutocomplete() {

		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.autocomplete, suggestions);
		//code.setAdapter(adapter);
		
		code.setAdapter(new AutoCompleteAdapter());
		
		code.setThreshold(1);
		/*code.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				code.showDropDown();
			}});*/
		
		code.setTokenizer(new MultiAutoCompleteTextView.Tokenizer() { //http://grepcode.com/file_/repository.grepcode.com/java/ext/com.google.android/android/4.0.1_r1/android/widget/MultiAutoCompleteTextView.java/?v=source
			
			private final char token = '\n';
			private final char end = ';';
			private final char space = ' ';
			
			@Override
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

	        @Override
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

	        @Override
			public CharSequence terminateToken(CharSequence text) {
	            int i = text.length();

	            while (i > 0 && text.charAt(i - 1) == space) {
	                i--;
	            }

	            if (i > 0 && text.charAt(i - 1) == token) {
	                return text;
	            } else {
	            	
	            	String autoCompleted = new StringBuilder().append(text).append(end).toString();
	            	if (((String) text).endsWith("}")) //Geen ; achter }
	            		autoCompleted = new StringBuilder().append(text).toString();
	            	
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
	
	class AutoCompleteAdapter extends BaseAdapter implements Filterable { //https://github.com/android/platform_frameworks_base/blob/master/core/java/android/widget/ArrayAdapter.java
		
		String[] suggestions = new String[] {
		         "if (?) {\n\n}", "else {\n\n}",  "else if (?) {\n\n}", /*"hp<",*/ 
		         "move_up()", "move_left()", "move_right()", "move_down()", "scan()"
		     };
		
		List<String> suggestionsFiltered = new ArrayList<String>();
		String lastConstraint;	
		
		@Override
		public int getCount() {
			return suggestionsFiltered.size();
		}

		@Override
		public Object getItem(int position) {
			return suggestionsFiltered.get(position);
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			TextView view = new TextView(parent.getContext());
			view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
			view.setPadding(pad, pad, pad, pad);
			
			String text = (String)getItem(position);
			int start = text.indexOf(lastConstraint);
			int end = start + lastConstraint.length();

			SpannableString content = new SpannableString(text);
			//content.setSpan(new UnderlineSpan(), start, end, 0);
			content.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
			content.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, 0);
			view.setText(content);
			
			view.setTypeface(Typeface.MONOSPACE);
			return view;
			
		}

		@Override
		public Filter getFilter() {
			return new Filter() {
				
				@SuppressLint("NewApi")
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					
					constraint = ((String) constraint).trim(); //Remove spaces
					lastConstraint = (String) constraint;
					//However this method never gets called when the constraint is empty!
					
					List<String> resultList = new ArrayList<String>();
					for (int i = 0; i < suggestions.length; i++) {
						String suggestion = suggestions[i];
						if (/*constraint.length() == 0 || */suggestion.contains(constraint))
							resultList.add(suggestion);
					}
					
					results.values = resultList;
					results.count = resultList.size();
					return results;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					suggestionsFiltered = (List<String>) results.values;
					if (suggestionsFiltered == null)
						suggestionsFiltered = new ArrayList<String>(); //Avoid nullreference
					
					if (results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}};
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.edit_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
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
