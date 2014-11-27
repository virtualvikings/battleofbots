package com.virtualvikings.battleofthebots;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.test);

		GameView game = new GameView(getApplicationContext());
		game.setLayoutParams(new LinearLayout.LayoutParams(
		                                     LinearLayout.LayoutParams.MATCH_PARENT,
		                                     LinearLayout.LayoutParams.MATCH_PARENT));
		layout.addView(game);
	}
}
