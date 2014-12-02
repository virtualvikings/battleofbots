package com.virtualvikings.battleofthebots;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.virtualvikings.battleofthebots.GameView.Bot.State;

class Vector2<E>
{
	public E x;
	public E y;
	public Vector2(E x, E y)
	{
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Vector2<E> other) {
		return other.x.equals(x) && other.y.equals(y);
	}
}

public class GameView extends View {
	
	public static class Bot {
		
		public static class State {
			
			Vector2<Integer> position;
			byte direction;
			
			public State(Vector2<Integer> position, byte direction) {
				this.position = position;
				this.direction = direction;
			}
		}
		
		State[] states;
		
		public Bot(State[] states) {
			this.states = states;
		}
	}
	
	private int cellCount = 10;
	private byte[][][] cells;
	private int timeSegments;
	private int currentTime;
	
	private Bot player;
	private Bot enemy;
	private Paint brush;
	
	public int getDuration() {
		return timeSegments;
	}
	
	public int getCurrentTime() {
		return currentTime;
	}

	public void seek(int progress) {
		currentTime = progress;
		invalidate();
	}
	
	public void step(int i) {
		currentTime += i;
		invalidate();
	}
	
	public GameView(Context context) {
		super(context);
		
		//Waarschuwing - deze constructor wordt opnieuw aangeroepen als het scherm draait!
		timeSegments = 15;
		cells = new byte[cellCount][cellCount][timeSegments];
		
		//Plaats bots op willekeurige plekken
		Random r = new Random();
		State[] statesPlayer = new State[timeSegments];
		State[] statesEnemy = new State[timeSegments];
		
		for (int i = 0; i < timeSegments; i++) {
			byte direction = (byte) r.nextInt(3); //0-3
			statesPlayer[i] = new State(new Vector2<Integer>(r.nextInt(cellCount), r.nextInt(cellCount)), direction);
			statesEnemy[i] = new State(new Vector2<Integer>(r.nextInt(cellCount), r.nextInt(cellCount)), direction);
		}
		
		player = new Bot(statesPlayer);
		enemy = new Bot(statesEnemy);
		
		//Maak willekeurig test level
		for (int i = 0; i < cellCount; i++)
			for (int j = 0; j < cellCount; j++)
				for (int k = 0; k < timeSegments; k++)
				{
					byte value = (byte) r.nextInt(7);
					if (k > 0) //Alle andere arrays kopieren de eerste
						value = cells[i][j][0];
					cells[i][j][k] = value;
				}
		
		brush = new Paint();
		brush.setStrokeWidth(2);
		brush.setAntiAlias(true);
		
		invalidate();
	}
	
	@Override
	public void onDraw (Canvas canvas)
	{
		//Bepaal of breedte of hoogte kleiner is
		int minWH = Math.min(canvas.getWidth(), canvas.getHeight());
		float cellS = minWH / (float)cellCount;
		
		//Centreer
		if (minWH == canvas.getWidth())
			canvas.translate(0, canvas.getHeight() / 2f - minWH / 2f);
		else
			canvas.translate(canvas.getWidth() / 2f - minWH / 2f, 0);
		
		//Teken lijnen
		brush.setColor(Color.rgb(100, 100, 100));
		for (int i = 0; i <= cellCount; i++)
		{
			for (int j = 0; j <= cellCount; j++)
			{
				float x = i * cellS;
				float y = j * cellS;
				
				canvas.drawLine(x, 0, x, minWH, brush);
				canvas.drawLine(0, y, minWH, y, brush);
			}
		}

		Vector2<Integer> pos = new Vector2<Integer>(0, 0);
		//Teken obstakels en bots
		for (int i = 0; i < cellCount; i++)
		{
			for (int j = 0; j < cellCount; j++)
			{
				float x = i * cellS;
				float y = j * cellS;
				float radius = cellS / 2f;
				
				brush.setColor(Color.WHITE);
				if (cells[i][j][currentTime] == 0)
					canvas.drawCircle(x + radius, y + radius, radius, brush);
				
				pos.x = i;
				pos.y = j;
				
				State playerState = player.states[currentTime];
			
				if (playerState.position.equals(pos)) {
					brush.setColor(Color.GREEN);
					canvas.drawCircle(x + radius, y + radius, radius, brush);
				}
				
				State enemyState = enemy.states[currentTime];

				if (enemyState.position.equals(pos)) {
					brush.setColor(Color.RED);
					canvas.drawCircle(x + radius, y + radius, radius, brush);
				}
			}
		}
	}

}
