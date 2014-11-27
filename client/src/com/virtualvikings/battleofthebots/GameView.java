package com.virtualvikings.battleofthebots;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameView extends View {

	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		invalidate();
		
	}
	
	Paint brush = new Paint();
	int cells = 10;
	
	@Override
	public void onDraw (Canvas canvas)
	{
		brush.setStrokeWidth(2);
		brush.setColor(Color.WHITE);
		
		int minWH = Math.min(canvas.getWidth(), canvas.getHeight());
		float cellS = minWH / (float)cells;
		
		if (minWH == canvas.getWidth())
			canvas.translate(0, canvas.getHeight() / 2f - minWH / 2f);
		else
			canvas.translate(canvas.getWidth() / 2f - minWH / 2f, 0);

		for (int i = 0; i <= cells; i++)
		{
			for (int j = 0; j <= cells; j++)
			{
				canvas.drawLine(i * cellS, 0, i * cellS, minWH, brush);
				canvas.drawLine(0, j * cellS, minWH, j * cellS, brush);
			}
		}
	}

}
