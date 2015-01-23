package nl.davidlieffijn.battleofbots.interpreter;

public class Action implements Statement {
	String action;
			
	public Action(String action) {
		this.action = action;
	}
	
	public String result(int[] stats) {
		return action;
	}
	
	public String toString() {
		return action;
	}
}
