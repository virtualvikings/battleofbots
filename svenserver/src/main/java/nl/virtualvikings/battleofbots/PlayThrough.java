package nl.virtualvikings.battleofbots;

import java.util.Arrays;
import java.util.Random;

public class PlayThrough {

	Bot bot1, bot2;
	byte fieldHeight = 20;
	byte fieldWidth = 20;
	int maxTimeSlots = 300;
	
	byte[][] field = new byte[fieldWidth][fieldHeight];
	byte[][][] moves = new byte[fieldWidth][fieldHeight][maxTimeSlots];
	
	public String Field() { 
		String result = "field:";
		for(int x = 0; x < fieldWidth; x++)
			for(int y = 0; y < fieldHeight; y++)
				result += "[" + x + "]" + "[" + y + "]=" + field[x][y] + ",";
		return result; 
	}
	
	public String Moves() { 
		
		return Arrays.toString(moves); 
	}
	
	public PlayThrough(Bot Bot1, Bot Bot2){
		this.bot1 = Bot1;
		this.bot2 = Bot2;
		generateField();
		calculateMoves();
	}
	
	public void generateField(){
		Random rand = new Random();
		for(int x = 0; x < fieldWidth; x++)
			for(int y = 0; y < fieldHeight; y++){
				field[x][y] = (byte) rand.nextInt(5);
			}
	}
	
	public void calculateMoves(){
		moves[0][0][0] = 1;
	}
}
