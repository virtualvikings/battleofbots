package nl.virtualvikings.parser;

import java.util.Random;

public class BotVariable extends Variable {
	public BotVariable(String name) {
		super(name, new Constant(0));
	}
	
		
	public int result(int[] stats) {
		switch (name) {
		case "DIRECTION": return stats[0];
		case "HP": return stats[1];
		case "X": return stats[2];
		case "Y": return stats[3];
		case "RANDOM": return new Random().nextInt(100);
		case "TURNS": return stats[4];
		case "VIEW_L": return stats[5];
		case "VIEW_F": return stats[6];
		case "VIEW_R": return stats[7];
		}
		return -1;
	}
}
