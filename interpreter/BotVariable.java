package nl.davidlieffijn.battleofbots.interpreter;

import java.util.Random;

public class BotVariable extends Variable {
	public BotVariable(String name) {
		super(name, new Constant(0));
	}
	
	public int result(int[] stats) {
		if (name.equals("RANDOM")) {
			return new Random().nextInt(100);
		} else {
			String[] variables = Parser.BOT_VARIABLES;
			for (int i = 0; i < variables.length; i++) {
				String currentVariable = variables[i];
				if (name.equals(currentVariable)) {
					return stats[i];
				}
			}
		}
		return -1;
	}
}
