package com.virtualvikings.battleofthebots;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameView extends View {

	Paint brush = new Paint();
	int cellCount = 10;
	int[][] cells;
	
	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		cells = new int[cellCount][cellCount];
		invalidate();
		
		//Maak willekeurig test level
		Random r = new Random();
		for (int i = 0; i < cellCount; i++)
		{
			for (int j = 0; j < cellCount; j++)
			{
				cells[i][j] = r.nextInt(7);
			}
		}
		
		
	}
	

	
	@Override
	public void onDraw (Canvas canvas)
	{
		brush.setStrokeWidth(2);
		brush.setAntiAlias(true);
		
		//Bepaal of breedte of hoogte kleiner is
		int minWH = Math.min(canvas.getWidth(), canvas.getHeight());
		float cellS = minWH / (float)cellCount;
		
		//Centreer
		if (minWH == canvas.getWidth())
			canvas.translate(0, canvas.getHeight() / 2f - minWH / 2f);
		else
			canvas.translate(canvas.getWidth() / 2f - minWH / 2f, 0);
		
		//Teken lijnen
		brush.setColor(Color.GREEN);
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

		//Teken obstakels enzo
		brush.setColor(Color.WHITE);
		for (int i = 0; i < cellCount; i++)
		{
			for (int j = 0; j < cellCount; j++)
			{
				float x = i * cellS;
				float y = j * cellS;
					
				if (cells[i][j] == 0)
					canvas.drawCircle(x + cellS / 2f, y + cellS / 2f, cellS / 2f, brush);
			}
		}
	}

}
