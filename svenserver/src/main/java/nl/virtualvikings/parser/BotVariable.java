package nl.virtualvikings.parser;

import java.lang.*;
import java.util.Random;

public class BotVariable extends Variable {
	public BotVariable(String name) {
		super(name, new Number(0));
	}
	
		
	public int result(byte[] stats) {
		switch (name) {
		case "DIRECTION": return stats[0];
		case "HP": return stats[1];
		case "POS-X": return stats[2];
		case "POS-Y": return stats[3];
		case "RANDOM": return new Random().nextInt(100);
		case "VIEW": return stats[4];
		}
		return -1;
	}
}
