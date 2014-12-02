package com.virtualvikings.battleofthebots;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toolbar.LayoutParams;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.test);

		final GameView game = new GameView(getApplicationContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = 1;
		game.setLayoutParams(params);
		
		layout.addView(game);
		
		final TextView text = (TextView) findViewById(R.id.textTime);
		SeekBar bar = (SeekBar) findViewById(R.id.seekBar);
		bar.setMax(game.getTimeSegments() - 1); 
		
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				game.setProgress(progress);
				text.setText(String.format("Time: %d/%d", progress + 1, game.getTimeSegments()));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}});
	}
}
