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
	
	@Override
	public void onDraw (Canvas canvas)
	{
		Paint paint = new Paint();
		paint.setStrokeWidth(3);
		paint.setColor(Color.WHITE);
		
		int linesW = 20;
		int linesH = 20;
		
		for (int i = 0; i <= linesW; i++)
		{
			for (int j = 0; j <= linesH; j++)
			{
				canvas.drawLine(i * canvas.getWidth() / (float)linesW, 0, i * canvas.getWidth() / (float)linesW, canvas.getHeight(), paint );
				canvas.drawLine(0, j  * canvas.getHeight() / (float)linesH, canvas.getWidth(), j * canvas.getHeight() / (float)linesH, paint );
			}
		}
	}

}
