package nl.virtualvikings.parser;

import java.lang.*;
import java.util.Random;

public class BotVariable extends Variable {
	public BotVariable(String name) {
		super(name, new Number(0));
	}

	Random r = new Random();
		
	public int result(byte[] stats) {
		switch (name) {
			case "DIRECTION": return stats[0];
			case "HP": return stats[1];
			case "POS-X": return stats[2];
			case "POS-Y": return stats[3];
			case "RANDOM": r.nextInt(100); //return stats[4];
			case "VIEW": return stats[5];
		}
		return -1;
	}
}
