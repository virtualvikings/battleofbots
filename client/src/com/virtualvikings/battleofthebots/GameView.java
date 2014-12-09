package com.virtualvikings.battleofthebots;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Canvas.VertexMode;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.virtualvikings.battleofthebots.GameView.Bot.State;

class Vector2
{
	public int x;
	public int y;
	public Vector2(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Vector2 other) {
		return other.x == x && other.y == y;
	}
	
	@Override
	protected Object clone() {
		return new Vector2(x, y);
	}

	public void add(Vector2 toAdd) {
		x += toAdd.x;
		y += toAdd.y;
	}
}

public class GameView extends View {
	
	public static class Bot {
		
		public static class State {
			
			private Vector2 position;
			private byte direction;
			
			public State(Vector2 position, byte direction) {
				this.position = position;
				this.direction = direction;
			}
			
			@Override
			protected Object clone() throws CloneNotSupportedException {
				return super.clone();
			}
		}
		
		State[] states;
		
		public Bot(State[] states) {
			this.states = states;
		}
		
	}
	
	private int cellCount;
	private byte[][][] cells;
	private int timeSegments;
	private int currentTime;
	
	private Bot player;
	private Bot enemy;
	private Paint brush;
	private Random r = new Random();
	private boolean trackPlayer;
	
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
	
	public void setTrackPlayer(boolean track) {
		trackPlayer = track;
		invalidate();
	}
	
	public GameView(Context context, String mapData) {
		super(context);
		
		this.setBackgroundColor(Color.WHITE);
		
		//Waarschuwing - deze constructor wordt opnieuw aangeroepen als het scherm draait!

		System.out.println("Map data is: " + mapData);
		//deserialize(mapData);
		makeDefaultLevel();
		
		brush = new Paint();
		brush.setAntiAlias(true);
		
		invalidate();
	}
	
	private void deserialize(String mapData) {
		try {
			
			InputStream input = new ByteArrayInputStream(mapData.getBytes());
			ObjectInputStream ois = new ObjectInputStream(input);
			cells = (byte[][][]) ois.readObject(); 
			ois.close();
			
			//TODO doe iets anders met de bots, misschien toch JSON?
			//Zoiets:
			//JSONObject obj = new JSONObject(mapData);
			//JSONArray timeSlices = obj.getJSONArray("mapThings");
			//timeSegments = timeSlices.length();
			//JSONArray[] rowSlices = timeSlices.getJSONArray(0);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void makeDefaultLevel() {

		cellCount = 20;
		timeSegments = 30;
		cells = new byte[timeSegments][cellCount][cellCount];
		
		//Plaats bots op willekeurige plekken
		State[] statesPlayer = new State[timeSegments];
		State[] statesEnemy = new State[timeSegments];
		Vector2 posPlayer = new Vector2(2, 4);
		Vector2 posEnemy = new Vector2(3, 7);
		
		//Simuleer een gevecht, dit moet eigenlijk op de server gebeuren maar dit is om het te testen
		try
		{
			for (int i = 0; i < timeSegments; i++) {

				//Kies willekeurige richting
				byte direction = (byte) r.nextInt(4); //0-3
				Vector2 toAdd = new Vector2((int)Math.round(Math.cos(direction / 2f * Math.PI)), (int)Math.round(Math.sin(direction / 2f * Math.PI)));
				
				posPlayer.add(toAdd);
				posEnemy.add(toAdd);
				
				//Spring naar andere kant als bot uit het veld gelopen is
				posPlayer.x = posPlayer.x % cellCount;
				posPlayer.y = posPlayer.y % cellCount;
				posEnemy.x = posEnemy.x % cellCount;
				posEnemy.y = posEnemy.y % cellCount;
				
				while (posPlayer.x < 0)
					posPlayer.x += cellCount;
				while (posPlayer.y < 0)
					posPlayer.y += cellCount;
				while (posEnemy.x < 0)
					posEnemy.x += cellCount;
				while (posEnemy.y < 0)
					posEnemy.y += cellCount;
				
				statesPlayer[i] = new State((Vector2) posPlayer.clone(), direction);
				statesEnemy[i] = new State((Vector2) posEnemy.clone(), direction);
			}
		}
		catch (Exception e)
		{}
		
		player = new Bot(statesPlayer);
		enemy = new Bot(statesEnemy);
		
		//Maak willekeurig level, ook eigenlijk een verantwoordelijkheid van de server
		for (int i = 0; i < cellCount; i++)
			for (int j = 0; j < cellCount; j++)
				for (int t = 0; t < timeSegments; t++)
				{
					byte value = (byte) r.nextInt(7);
					if (t > 0) //Alle andere arrays kopieren de eerste
						value = cells[0][i][j];
					cells[t][i][j] = value;
				}
	}

	@Override
	public void onDraw (Canvas canvas)
	{
		//Bepaal of breedte of hoogte kleiner is
		int minWH = Math.min(canvas.getWidth(), canvas.getHeight());
		float cellS = minWH / (float)cellCount;
		
		State playerState = player.states[currentTime];
		State enemyState = enemy.states[currentTime];
		
		if (!trackPlayer) {
			if (minWH == canvas.getWidth())
				canvas.translate(0, canvas.getHeight() / 2f - minWH / 2f);
			else
				canvas.translate(canvas.getWidth() / 2f - minWH / 2f, 0);
		} else {
			canvas.translate(canvas.getWidth() / 2f, canvas.getHeight() / 2f); //Centreer
			canvas.scale(3, 3); //Zoom in
			canvas.translate(-(playerState.position.x + 0.5f) * cellS, -(playerState.position.y + 0.5f) * cellS); //Richt camera op speler
		}
		
		//Teken blokken
		brush.setColor(Color.rgb(200, 200, 200));
		canvas.drawRect(new RectF(0, 0, minWH, minWH), brush);
		
		RectF rect = new RectF(0, 0, cellS, cellS);
		for (int i = 0; i < cellCount; i++)
		{
			for (int j = 0; j < cellCount; j++)
			{
				int gray = 210; 
				if ((i + j) % 2 == 0) continue; //Maak schaakbord patroon
				
				float x = i * cellS;
				float y = j * cellS;
				
				brush.setColor(Color.rgb(gray, gray, gray));
				rect.offsetTo(x, y);
				canvas.drawRect(rect, brush);
			}
		}
		
		//Teken lijnen NIET
		if (false) {
			brush.setStrokeWidth(2);
			brush.setColor(Color.WHITE);
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
		}

		//Teken obstakels en bots
		Vector2 pos = new Vector2(0, 0);
		for (int i = 0; i < cellCount; i++)
		{
			for (int j = 0; j < cellCount; j++)
			{
				float x = i * cellS;
				float y = j * cellS;
				float radius = cellS / 2f;
				
				canvas.save();
				canvas.translate(x + radius, y + radius);
				
				brush.setColor(Color.GRAY);
				if (cells[currentTime][i][j] == 0)
					drawObstacle(canvas, radius);
				
				pos.x = i;
				pos.y = j;
				
				if (playerState.position.equals(pos)) {
					brush.setColor(Color.GREEN);
					drawBot(canvas, radius, playerState.direction); //Waarom niet bot.draw()?
				}

				if (enemyState.position.equals(pos)) {
					brush.setColor(Color.RED);
					drawBot(canvas, radius, enemyState.direction);
				}
				
				canvas.restore();
			}
		}
	}
	
	private void drawObstacle(Canvas canvas, float radius) {
		canvas.drawCircle(0, 0, radius, brush);
		//brush.setColor(Color.BLACK);
		//canvas.drawCircle(x + radius, y + radius, radius / 2, brush);
	}
	
	private void drawBot(Canvas canvas, float halfSize, byte rotation) {
		brush.setStyle(Style.FILL);
		
		Path path = new Path();
		path.moveTo(-halfSize, -halfSize);
		path.lineTo(halfSize, 0);
		path.lineTo(-halfSize, halfSize);
		path.lineTo(-halfSize, -halfSize);

		canvas.save();
		canvas.rotate(rotation * 90);
		canvas.drawPath(path, brush);
		canvas.restore();
	}

}
