package com.virtualvikings.battleofthebots;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

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
	
	class Bot
	{
		Vector2<Integer>[] positions;
		byte[] directions; //0-3
		
		public Bot(Vector2<Integer>[] positions) {
			this.positions = positions;
		}
	}

	Paint brush;
	int cellCount = 10;
	int timeSegments;
	int currentTime;
	byte[][][] cells;
	
	Bot player;
	Bot enemy;
	
	public int getTimeSegments() {
		return timeSegments;
	}
	

	public void setProgress(int progress) {
		currentTime = progress;
		invalidate();
	}
	
	public GameView(Context context) {
		super(context);
		
		timeSegments = 10;
		cells = new byte[cellCount][cellCount][timeSegments];
		
		//Plaats bots op willekeurige plekken
		Random r = new Random();
		Vector2<?>[] positionsPlayer = new Vector2<?>[timeSegments];
		Vector2<?>[] positionsEnemy = new Vector2<?>[timeSegments];
		for (int i = 0; i < timeSegments; i++) {
			positionsPlayer[i] = new Vector2<Integer>(r.nextInt(cellCount), r.nextInt(cellCount));
			positionsEnemy[i] = new Vector2<Integer>(r.nextInt(cellCount), r.nextInt(cellCount));
			System.out.println("player[" + i + "]: " + positionsPlayer[i].x + ", " + positionsPlayer[i].y);
			System.out.println("enemy[" + i + "]: " + positionsEnemy[i].x + ", " + positionsEnemy[i].y);
		}
		
		player = new Bot((Vector2<Integer>[]) positionsPlayer);
		enemy = new Bot((Vector2<Integer>[]) positionsEnemy);
		
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
		
		/*Timer t = new Timer();
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				GameView.this.post(new Runnable(){
					@Override
					public void run() {
						GameView.this.invalidate();
					}});
			}};
		t.schedule(task , 0, 1000); //Doe dit elke seconde*/
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

		//Teken obstakels en bots
		for (int i = 0; i < cellCount; i++)
		{
			for (int j = 0; j < cellCount; j++)
			{
				float x = i * cellS;
				float y = j * cellS;
				Vector2<Integer> pos = new Vector2<Integer>(i, j);
				
				brush.setColor(Color.WHITE);
				if (cells[i][j][currentTime] == 0)
					canvas.drawCircle(x + cellS / 2f, y + cellS / 2f, cellS / 2f, brush);
				
				brush.setColor(Color.GREEN);
				if (player.positions[currentTime].equals(pos))
					canvas.drawCircle(x + cellS / 2f, y + cellS / 2f, cellS / 2f, brush);
				brush.setColor(Color.RED);
				if (enemy.positions[currentTime].equals(pos))
					canvas.drawCircle(x + cellS / 2f, y + cellS / 2f, cellS / 2f, brush);
			}
		}
	}

}
