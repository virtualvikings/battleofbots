package com.virtualvikings.battleofthebots;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class GameActivity extends Activity {
	
	//Zie https://stackoverflow.com/questions/6812003/difference-between-oncreate-and-onstart

	
	private GameView game;
	private SeekBar bar;
	
	private ImageButton buttonPrevious;
	private ImageButton buttonBegin;
	private ImageButton buttonPlay;
	private ImageButton buttonNext;
	private ImageButton buttonEnd;
	
	private boolean playing;
	private boolean rewinding;
	private boolean forwarding;
	private boolean waitDone;
	//private boolean cancelled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		game = new GameView(getApplicationContext(),
				getIntent().getExtras().getString("mapData"),
				getIntent().getExtras().getString("moveData") );
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
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
				else if (v == buttonPlay)
					clickPlay(v);
				else if (v == buttonEnd)
					clickEnd(v);
			}};
			
		
		OnTouchListener touchListener = new OnTouchListener(){
			
			Timer longPressTimer;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				//Dit wordt steeds opnieuw aangeroepen als de muis beweegt
				int action = event.getAction();
				if (v == buttonPrevious) {
					if (action == MotionEvent.ACTION_DOWN) {
						rewinding = true;
						clickPrevious(v, 1);
					}
					if (action == MotionEvent.ACTION_UP) {
						rewinding = false;
					}
				} else if (v == buttonNext) {
					if (action == MotionEvent.ACTION_DOWN) {
						forwarding = true;
						clickNext(v, 1);
					}
					if (action == MotionEvent.ACTION_UP) {
						forwarding = false;
					}
				}
				
				if (action == MotionEvent.ACTION_DOWN) {
					longPressTimer = new Timer(); //Je moet een nieuwe Timer maken nadat je cancel() hebt aangeroepen
					longPressTimer.schedule(new TimerTask(){
						@Override
						public void run() {
							if ((!forwarding && !rewinding)) return;
							waitDone = true;
						}
					}, 300); //Wacht 300ms voordat we gaan spoelen
				}
				if (action == MotionEvent.ACTION_UP) {
					longPressTimer.cancel();
					waitDone = false;
				}
				
				return false; //Consumeer deze gebeurtenis NIET
			}
		};
		
		new Timer().schedule(new TimerTask(){
			@Override
			public void run() {
				GameActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						if (!waitDone) return;
						if (forwarding)
							clickNext(buttonNext, 1);
						if (rewinding)
							clickPrevious(buttonPrevious, 1);
					}});
			}}, 0, 50); //Spoel 1 stap elke 50ms
			
		buttonBegin.setOnClickListener(clickListener);
		buttonPrevious.setOnTouchListener(touchListener);
		buttonPlay.setOnClickListener(clickListener);
		buttonNext.setOnTouchListener(touchListener);
		buttonEnd.setOnClickListener(clickListener);
		
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
