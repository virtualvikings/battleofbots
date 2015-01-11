package com.virtualvikings.battleofthebots;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SimpleEditActivity extends ActionBarActivity {

    private static final String PREFS_NAME = "B0B";
    private final String identifier = "code_storage";
    private LinearLayout mainList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_edit);

        mainList = (LinearLayout) this.findViewById(R.id.mainList);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        load(settings.getString(identifier, null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.simple_edit_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addNewPair("", "");
                return true;
            case R.id.action_save:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save() {

        try {

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor edit = settings.edit();

            JSONObject json = new JSONObject();
            JSONArray everything = new JSONArray();

            int children = mainList.getChildCount();
            for (int i = 0; i < children; i++)
                everything.put(layoutToJSON((ConditionActionPair) mainList.getChildAt(i)));

            json.put(identifier, everything);
            String result = json.toString(2);

            edit.putString(identifier, result);
            edit.commit();
            //edit.apply(); //not supported on API level 8

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Failed to save code.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private void load(String str) {

        if (str == null) return; //Load nothing

        try {

            JSONArray json = new JSONObject(str).getJSONArray(identifier);
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < json.length(); i++) {
                JSONObject obj = json.getJSONObject(i);

                addPair(layoutFromJSON(obj), mainList);
                builder.append(codeFromJSON(obj));
            }

            String result = builder.toString();
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Failed to load code.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private ConditionActionPair layoutFromJSON(JSONObject json) throws JSONException {

        String condition = json.optString("condition");
        JSONArray actions = json.optJSONArray("actions");

        ConditionActionPair parent = new ConditionActionPair(getApplicationContext(), condition, null);

        for (int i = 0; i < actions.length(); i++) {
            Object value = actions.get(i);
            if (value instanceof String)
                parent.addActionText(getApplicationContext(), (String) value);
            else if (value instanceof JSONObject)
                addPair(layoutFromJSON((JSONObject) value), parent);
        }

        return parent;
    }

    private String codeFromJSON(JSONObject json) throws JSONException {
        String condition = json.optString("condition");
        JSONArray actions = json.optJSONArray("actions");

        String actionStr = "\n";
        String indentation = "  ";

        for (int i = 0; i < actions.length(); i++) {

            Object value = actions.get(i);

            String[] linesToAdd = null;

            if (value instanceof String)
                linesToAdd = ((String) value).split("\n");
            else if (value instanceof JSONObject)
                linesToAdd = codeFromJSON((JSONObject)value).split("\n");

            for (String s : linesToAdd) {
                actionStr += indentation + s + "\n";
            }

        }

        //TODO use StringBuilder for extra performance
        String rest = String.format("{%s}\n", actionStr);

        String result;
        if (condition.equalsIgnoreCase("else"))
            result = String.format("%s %s", condition, rest);
        else
            result = String.format("if (%s) %s", condition, rest);

        return result;
    }

    private JSONObject layoutToJSON(ConditionActionPair pair) throws JSONException {

        List<Object> actions = pair.getActions();
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();

        for (Object obj : actions) {
            if (obj instanceof String)
                array.put(obj);
            else if (obj instanceof ConditionActionPair)  {
                array.put(layoutToJSON((ConditionActionPair) obj));
            }
        }

        object.put("condition", pair.getCondition());
        object.put("actions", array);
        return object;
    }

    public ConditionActionPair addNewPair(String condition, String actions) {
        return addPair(new ConditionActionPair(getApplicationContext(), condition, actions), mainList);
    }

    private ConditionActionPair addPair(ConditionActionPair a, final LinearLayout parent) {

        if (parent instanceof ConditionActionPair)
            ((ConditionActionPair)parent).add(a);
        else //just a regular LinearLayout
            parent.addView(a);

        return a;
    }

    class ConditionActionPair extends LinearLayout {

        private final EditText condition;
        private final LinearLayout actionWrapper;

        public String getCondition() {
            return condition.getText().toString();
        }

        public List<Object> getActions() {

            List<Object> actions = new ArrayList<Object>();

            int children = actionWrapper.getChildCount();
            for (int i = 0; i < children; i++) {

                View child = actionWrapper.getChildAt(i);

                if (child instanceof ConditionActionPair)
                    actions.add(child);
                else
                    actions.add(((EditText) child).getText().toString());

            }
            return actions;
        }

        public void add(ConditionActionPair pair) {
            actionWrapper.addView(pair);
        }

        final int hintColor = Color.argb(100, 255, 255, 255);
        final int pad;

        @SuppressLint("NewApi") //Warning - this might cause crashes on devices API level < 11
        
		public ConditionActionPair(final Context context, String conditionText, String tempActions) {
            super(context);

            setOrientation(VERTICAL);
            
            //TODO add animations here

            actionWrapper = new LinearLayout(context);
            actionWrapper.setBackgroundColor(Color.argb(60, 255, 255, 255));
            actionWrapper.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams paramsWrapper = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsCondWrapper = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsCondition = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams paramsCondButton = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0f);
            LinearLayout.LayoutParams paramsCondIcon = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0f);

            pad = getResources().getDimensionPixelSize(R.dimen.text_padding);
            paramsCondWrapper.setMargins(pad, pad, pad, 0);
            paramsWrapper.setMargins(pad * 2, 0, pad * 2, pad);
            paramsCondIcon.setMargins(0, 0, 0, 0);

            actionWrapper.setLayoutParams(paramsWrapper);

            int cwc = Color.rgb(50, 127, 255);
            final LinearLayout conditionWrapper = new LinearLayout(context);
            conditionWrapper.setBackgroundColor(cwc);
            conditionWrapper.setLayoutParams(paramsCondWrapper);

            condition = new EditText(context);
            condition.setText(conditionText);
            condition.setLayoutParams(paramsCondition);
            condition.setSingleLine(true);
            condition.setHint("Conditions");
            condition.setHintTextColor(hintColor);
            condition.setTypeface(Typeface.MONOSPACE);

           //TODO add icon here

            final Button condButton = new Button(context);
            condButton.setLayoutParams(paramsCondButton);
            condButton.setText("...");

            conditionWrapper.addView(condition);
            conditionWrapper.addView(condButton);

            final PopupMenu popup = new PopupMenu(SimpleEditActivity.this, condButton);
            Menu popupMenu = popup.getMenu();
            condButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popup.show();
                }
            });

            popupMenu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    ((ViewGroup)ConditionActionPair.this.getParent()).removeView(ConditionActionPair.this);
                    return false;
                }
            });
            popupMenu.add("Add condition inside").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    addPair(new ConditionActionPair(context, "", ""), ConditionActionPair.this);
                    return false;
                }
            });
           /* popupMenu.add("Add action inside").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    ConditionActionPair.this.addActionText(context, ""); //Don't enter null here or it won't do anything
                    return false; 
                }
            });*/
            popupMenu.add("Move up").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    swap(-1);
                    return false;
                }
            });
            popupMenu.add("Move down").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    swap(1);
                    return false;
                }
            });

            if (tempActions != null)
                addActionText(context, tempActions);

            addView(conditionWrapper);
            addView(actionWrapper);
        }

        public void addActionText(Context context, String tempActions) {

            LayoutParams paramsAction = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsAction.setMargins(pad / 2, 0, pad / 2, 0);

            EditText action = new EditText(context);
            action.setHint("Actions");
            action.setHintTextColor(hintColor);
            action.setLayoutParams(paramsAction);
            action.setTypeface(Typeface.MONOSPACE);
            action.setPadding(pad * 2, pad * 2, pad * 2, pad * 2);
            action.setText(tempActions);

            actionWrapper.addView(action);
        }

        private void swap(int relativeIndex) {
            View toReorder = ConditionActionPair.this;
            ViewGroup parent = (ViewGroup)toReorder.getParent();
            int childCount = parent.getChildCount();

            int index = parent.indexOfChild(toReorder);
            int newIndex = index + relativeIndex;
            while (newIndex < 0)
                newIndex += childCount; //Wrap at beginning
            newIndex %= childCount; //Wrap at end

            parent.removeView(toReorder);
            parent.addView(toReorder, newIndex);
        }
    }
}