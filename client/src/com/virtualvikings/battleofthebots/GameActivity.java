package com.virtualvikings.battleofthebots;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
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
	
	private GameView game;
	private SeekBar bar;
	
	private ImageButton buttonPrevious;
	private ImageButton buttonBegin;
	private ImageButton buttonPlay;
	private ImageButton buttonNext;
	private ImageButton buttonEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		game = new GameView(getApplicationContext(), getIntent().getExtras().getString("mapData"));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		game.setLayoutParams(params);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.test);
		layout.addView(game, 0); //Plaats de GameView voor de andere views
		
		final TextView text = (TextView) findViewById(R.id.textTime);
		//LinearLayout mediaButtons = (LinearLayout) findViewById(R.id.mediaButtons);
		//mediaButtons.setVisibility(View.GONE);
		
		bar = (SeekBar) findViewById(R.id.seekBar); 
		bar.setMax(game.getDuration() - 1); 
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				GameActivity.this.game.seek(progress);
				text.setText(String.format("%d/%d", progress + 1, game.getDuration()));
				
				if (fromUser)
					setPlaying(false);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	
		buttonBegin = (ImageButton) findViewById(R.id.buttonBegin);
		buttonPrevious = (ImageButton) findViewById(R.id.buttonPrevious);
		buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
		buttonNext = (ImageButton) findViewById(R.id.buttonNext);
		buttonEnd = (ImageButton) findViewById(R.id.buttonEnd);
		
		OnClickListener clickListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (v == buttonBegin)
					clickBegin(v);
				else if (v == buttonPrevious)
					clickPrevious(v, 1);
				else if (v == buttonPlay)
					clickPlay(v);
				else if (v == buttonNext)
					clickNext(v, 1);
				else if (v == buttonEnd)
					clickEnd(v);
			}};
			
		OnLongClickListener longClickListener = new OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				if (v == buttonPrevious)
					clickPrevious(v, 25);
				else if (v == buttonNext)
					clickNext(v, 25);
				
				return true;
			}};
			
		buttonBegin.setOnClickListener(clickListener);
		buttonPrevious.setOnClickListener(clickListener);
		buttonPlay.setOnClickListener(clickListener);
		buttonNext.setOnClickListener(clickListener);
		buttonEnd.setOnClickListener(clickListener);
		
		buttonPrevious.setOnLongClickListener(longClickListener);
		buttonNext.setOnLongClickListener(longClickListener);
		//TODO laat dit herhalen
		
		CheckBox checkPlayer = (CheckBox) findViewById(R.id.checkBoxTrack);
		checkPlayer.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				game.setTrackPlayer(((CheckBox)v).isChecked());
			}
		});
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
	
	public void clickBegin(View v) {
		setPlaying(false);
		seek(0);
	}
	
	public void clickEnd(View v) {
		setPlaying(false);
		seek(game.getDuration());
	}

	public void clickNext(View v, int steps) {
		setPlaying(false);
		step(steps);
	}
	
	public void clickPrevious(View v, int steps) {
		setPlaying(false);
		step(-steps);
	}
	
	
}
