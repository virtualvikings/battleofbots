package com.virtualvikings.battleofthebots;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

import com.virtualvikings.battleofthebots.GameView.Bot.State;


public class GameView extends View {
	
	public static class Bot {
		
		public static class State {
			
			private Point position;
			private byte direction;
			
			public State(Point position, byte direction) {
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
	public PointF cameraPos = new PointF();
	
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
		Point posPlayer = new Point(2, 4);
		Point posEnemy = new Point(3, 7);
		
		//Simuleer een gevecht, dit moet eigenlijk op de server gebeuren maar dit is om het te testen
		try
		{
			for (int i = 0; i < timeSegments; i++) {

				//Kies willekeurige richting
				byte direction = (byte) r.nextInt(4); //0-3
				Point toAdd = new Point((int)Math.round(Math.cos(direction / 2f * Math.PI)), (int)Math.round(Math.sin(direction / 2f * Math.PI)));
				
				posPlayer.offset(toAdd.x, toAdd.y);
				posEnemy.offset(toAdd.x, toAdd.y);
				
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
				
				statesPlayer[i] = new State(new Point(posPlayer), direction);
				statesEnemy[i] = new State(new Point(posEnemy), direction);
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
		
		canvas.translate(cameraPos.x, cameraPos.y);
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

		//Teken obstakels en bots
		Point pos = new Point(0, 0);
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
	
	final private int INVALID_POINTER_ID = -1;
	private int activePointerId = INVALID_POINTER_ID;
	private float lastTouchX;
	private float lastTouchY;

	
	public boolean onTouchEvent(MotionEvent ev) { //https://developer.android.com/training/gestures/scale.html

		System.out.println("onTouchEvent triggered!");
		
		final int action = MotionEventCompat.getActionMasked(ev); 
        
	    switch (action) { 
		    case MotionEvent.ACTION_DOWN: {
		        final int pointerIndex = MotionEventCompat.getActionIndex(ev); 
		        final float x = MotionEventCompat.getX(ev, pointerIndex); 
		        final float y = MotionEventCompat.getY(ev, pointerIndex); 
		            
		        lastTouchX = x;
		        lastTouchY = y;
		        activePointerId = MotionEventCompat.getPointerId(ev, 0);
		        break;
		    }
		            
		    case MotionEvent.ACTION_MOVE: {

		        final int pointerIndex = 
		                MotionEventCompat.findPointerIndex(ev, activePointerId);  
		            
		        final float x = MotionEventCompat.getX(ev, pointerIndex);
		        final float y = MotionEventCompat.getY(ev, pointerIndex);

		        final float dx = x - lastTouchX;
		        final float dy = y - lastTouchY;
	
		        lastTouchX = x;
		        lastTouchY = y;
		        
		        cameraPos.offset(dx, dy);
		        invalidate();
	
		        break;
		    }
		            
		    case MotionEvent.ACTION_UP: {
		        activePointerId = INVALID_POINTER_ID;
		        break;
		    }
		            
		    case MotionEvent.ACTION_CANCEL: {
		        activePointerId = INVALID_POINTER_ID;
		        break;
		    }
		        
		    case MotionEvent.ACTION_POINTER_UP: {
		            
		        final int pointerIndex = MotionEventCompat.getActionIndex(ev); 
		        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex); 
	
		        if (pointerId == activePointerId) {
		            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
		            lastTouchX = MotionEventCompat.getX(ev, newPointerIndex); 
		            lastTouchY = MotionEventCompat.getY(ev, newPointerIndex); 
		            activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
		        }
		        break;
		    }
	    }      

        return true;
	}
}


