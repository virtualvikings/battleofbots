package com.virtualvikings.battleofthebots;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
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
			private int health;
			
			public State(Point position, byte direction, int health) {
				this.position = position;
				this.direction = direction;
				this.health = health;
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
	private byte[][] cells;
	private int timeSegments;
	private int currentTime;
	
	private Bot player;
	private Bot enemy;
	
	private Paint brush;
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
	
	HashMap<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();
	
	public GameView(Context context, String mapData, String moveData) {
		super(context);
		
		setBackgroundColor(Color.rgb(238, 238, 238)); //or the grass color?
		
		System.out.println("Map data is: " + mapData);
		System.out.println("Move data is: " + moveData);
		
		//BitmapFactory.decodeResource is VERY slow so this needs to be done beforehand
		bitmaps.put("bluebot", BitmapFactory.decodeResource(getResources(), R.drawable.bluebot));
		bitmaps.put("redbot", BitmapFactory.decodeResource(getResources(), R.drawable.redbot));
		bitmaps.put("tree", BitmapFactory.decodeResource(getResources(), R.drawable.tree));
		bitmaps.put("stone", BitmapFactory.decodeResource(getResources(), R.drawable.stone));
		bitmaps.put("water", BitmapFactory.decodeResource(getResources(), R.drawable.water));
		bitmaps.put("bg", BitmapFactory.decodeResource(getResources(), R.drawable.backgroundtile));

		try {
			cells = decodeField(mapData);
			cellCount = cells.length; //Length of 1 side
			
			ArrayList<ArrayList<State>> moves = decodeMoves(moveData); //First arraylist is the player
			timeSegments = moves.get(0).size();
			
			State[] statesPlayer = new State[timeSegments];
			State[] statesEnemy = new State[timeSegments];
			
			for (int b = 0; b < moves.size(); b++) {
				for (int s = 0; s < timeSegments; s++) {
					if (b == 0) //Player
						statesPlayer[s] = moves.get(b).get(s);
					else if (b == 1) //Enemy
						statesEnemy[s] = moves.get(b).get(s);
					else
						throw new Exception("More than 2 players not supported right now");
				}
			}
			
			player = new Bot(statesPlayer);
			enemy = new Bot(statesEnemy);
			
			//TODO: maybe you have to double the amount of states/copy them? 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		brush = new Paint();
		brush.setAntiAlias(true);

		invalidate();
	}
	
	private byte[][] decodeField(String fromServer) throws IOException {

		String stripped = fromServer.substring(0, fromServer.length() - "field".length()); //Remove the field string from the end
		ByteArrayInputStream bytes = new ByteArrayInputStream(stripped.getBytes()); //Convert string to byte stream
		DataInputStream data = new DataInputStream(bytes); //Prepare to read data from byte stream

		byte fieldSize = data.readByte();
		byte[][] field = new byte[fieldSize][fieldSize]; //Width = height
		for (int x = 0; x < fieldSize; x++)
            for (int y = 0; y < fieldSize; y++)
                field[x][y] = data.readByte(); //TODO: hidden bug, if a byte is 10 (newline) the message gets split and everything breaks

		data.close();
		return field;
	}

	private ArrayList<ArrayList<State>> decodeMoves(String fromServer) throws JSONException {

		String stripped = fromServer.replace("moves_start", ""); //Remove identifier

		ArrayList<ArrayList<State>> moves = new ArrayList<ArrayList<State>>();

		JSONArray bots = new JSONArray(stripped);
		int botCount = bots.length();
		
		String myName = MainActivity.settings.getString("name", null);
		String loserName = null;
		boolean reverse = false;
		
		Map<String, Integer> lastHP = new HashMap<String, Integer>();

		for (int b = 0; b < botCount; b++) {
			
			JSONObject stuff = bots.getJSONObject(b);
			String name = stuff.getString("name");
			
			if (b == 0 && !name.equals(myName)) //If this is the first bot and it's the enemy, swap the lists
				reverse = true;
			
            JSONArray botStates = stuff.getJSONArray("moves");
            
            int stateCount = botStates.length(); //TODO: ensure all lists are the same length

            ArrayList<State> stateList = new ArrayList<State>();
            moves.add(stateList);

            for (int s = 0; s < stateCount; s++) {

                JSONObject obj = botStates.getJSONObject(s);

                Point pos = new Point(obj.getInt("x"), obj.getInt("y"));
                byte dir = (byte) obj.getInt("dir"); //TODO: conversion from int to byte might break
                int hp = obj.getInt("hp");

				stateList.add(new State(pos, dir, hp));
				
				if (s == stateCount - 1) //Last round
					lastHP.put(name, hp);

            }
        }
		
		//Still not perfect, sometimes both bots lose when there is clearly a winner
		//TODO: go debug this!
		
		if (lastHP.size() != 2)
			throw new IllegalStateException("Two players have the same name");
		
		Integer firstHP = (Integer) lastHP.values().toArray()[0];
		boolean allTheSame = true;
		for (Integer i = 0; i < lastHP.values().toArray().length; i++) {
			if (!i.equals(firstHP)) {
				allTheSame = false;
				break;
			}
		}

		if (!allTheSame) {
			int lowestHP = 10;
			for (String name : lastHP.keySet()) {
				Integer hp = lastHP.get(name);
				if (hp < lowestHP) {
					lowestHP = hp;
					loserName = name;
				}
			}
		}
		
		if (reverse)
			Collections.reverse(moves); //After this, the first bot in the arraylist is guaranteed to be the player

		System.out.println("The loser is " + loserName);
		if (loserName == null)
			GameActivity.won = 0; //draw
		else if (loserName.equals(myName))
			GameActivity.won = -1; //lost
		else
			GameActivity.won = 1; //won
		
		return moves;
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
		//brush.setColor(Color.rgb(200, 200, 200));
		//canvas.drawRect(new RectF(0, 0, minWH, minWH), brush);
		
		Bitmap b = bitmaps.get("bg");
		canvas.drawBitmap(b, new Rect(0, 0, b.getWidth(), b.getHeight()), new RectF(0, 0, minWH, minWH), brush);
	
		
		RectF rect = new RectF(0, 0, cellS, cellS);
		/*for (int i = 0; i < cellCount; i++)
		{
			for (int j = 0; j < cellCount; j++)
			{
				float x = i * cellS;
				float y = j * cellS;
				
				brush.setColor(Color.rgb(gray, gray, gray));
				rect.offsetTo(x, y);
				canvas.drawRect(rect, brush);
			}
		}*/

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
				
				if (cells[i][j] != 0)
					drawObstacle(canvas, radius, cells[i][j], i, j); 
				
				pos.x = i;
				pos.y = j;
				
				if (playerState.position.equals(pos))
					drawBot(canvas, radius, playerState.direction, playerState.health, true);

				if (enemyState.position.equals(pos)) 
					drawBot(canvas, radius, enemyState.direction, enemyState.health, false);
				
				canvas.restore();
			}
		}
	}
	
	//Random r = new Random();
	
	private void drawObstacle(Canvas canvas, float radius, int obs, int x, int y) {
		String res;
		boolean randomlyRotate = false;
		switch (obs) {
			case 1: res = "tree"; break;
			case 2: res = "stone"; break;
			case 3: res = "water"; randomlyRotate = true; break;
			default: res = "stone"; break;
		}
		
		canvas.save();
		if (randomlyRotate) {
			int rotation = (int) (x + y);
			canvas.rotate(rotation * 90); //Rotate randomly but not every frame
		}
		
		Bitmap b = bitmaps.get(res);
		canvas.drawBitmap(b, new Rect(0, 0, b.getWidth(), b.getHeight()), new RectF(-radius, -radius, radius, radius), brush);
		canvas.restore();
	}
	
	private void drawBot(Canvas canvas, float halfSize, byte rotation, int hp, boolean isPlayer) {
		brush.setStyle(Style.FILL);
		
		canvas.save();
		canvas.rotate(rotation * 90);
		Bitmap b = bitmaps.get(isPlayer ? "bluebot" : "redbot");
		canvas.drawBitmap(b, new Rect(0, 0, b.getWidth(), b.getHeight()), new RectF(-halfSize, -halfSize, halfSize, halfSize), brush);
		canvas.restore();
		
		//TODO: make health display less ugly
		brush.setColor(Color.WHITE);
		brush.setTextSize(20);
		brush.setTextAlign(Align.CENTER);
		canvas.drawText(hp == 0 ? "DEAD" : Integer.toString(hp), 0, 0, brush);
	}
	
	final private int INVALID_POINTER_ID = -1;
	private int activePointerId = INVALID_POINTER_ID;
	private float lastTouchX;
	private float lastTouchY;

	
	@Override
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


