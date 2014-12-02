package com.virtualvikings.battleofthebots;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toolbar.LayoutParams;

public class GameActivity extends Activity {
	
	//Zie https://stackoverflow.com/questions/6812003/difference-between-oncreate-and-onstart
	
	private boolean playing;
	private ImageButton buttonPlay;
	private GameView game;
	private SeekBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.test);

		game = new GameView(getApplicationContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = 1;
		game.setLayoutParams(params);
		
		final TextView text = (TextView) findViewById(R.id.textTime);
		bar = (SeekBar) findViewById(R.id.seekBar); 
		buttonPlay = (ImageButton) findViewById(R.id.button2);
		bar.setMax(game.getDuration() - 1); 
		
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				GameActivity.this.game.seek(progress);
				text.setText(String.format("%d/%d", progress + 1, game.getDuration()));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}});
		
		layout.addView(game); //Plaats de GameView voor de andere views
		
	}
	
	private void seek(int progress) {
		bar.setProgress(progress);
	}
	
	private void step(int progress) {
		bar.setProgress(game.getCurrentTime() + progress);
	}

	private void setPlaying(boolean b) {
		playing = b;
		if (playing)
			buttonPlay.setImageResource(R.drawable.ic_action_pause);
		else
			buttonPlay.setImageResource(R.drawable.ic_action_play);
	}
	
	
	public void clickPlay(View v) {
		
		setPlaying(!playing);
		if (!playing) return;
		
		if (game.getCurrentTime() >= game.getDuration() - 1) //Ga naar begin als we aan het einde zijn
			seek(0);
		
		int speed = 250; //Milliseconden
		
		final Timer t = new Timer();
		t.schedule(new TimerTask(){
			@Override
			public void run() {
				GameActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						if (!playing) {
							t.cancel();
							return;
						}
						GameActivity.this.step(1);
						if (GameActivity.this.game.getCurrentTime() >= GameActivity.this.game.getDuration() - 1) {
							t.cancel();
							setPlaying(false); //Stop als we bij het einde zijn
						}
					}});
			}}, 0, speed);
	}
	
	public void clickStart(View v) {
		setPlaying(false);
		seek(0);
	}
	
	public void clickEnd(View v) {
		setPlaying(false);
		seek(game.getDuration());
	}

	public void clickNext(View v) {
		setPlaying(false);
		step(1);
	}
	
	public void clickPrevious(View v) {
		setPlaying(false);
		step(-1);
	}
	
	
}
