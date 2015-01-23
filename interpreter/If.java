package nl.davidlieffijn.battleofbots.interpreter;

public class If implements Statement {
	Expression condition;
	Statement primary;
	Statement secondary;
			
	public If(Expression condition, Statement primary, Statement secondary) {
		this.condition = condition;
		this.primary = primary;
		this.secondary = secondary;
	}
	
	
	public String result(int[] stats) {
		if (condition.result(stats) == 1) {
			return primary.result(stats);
		} else {
			return secondary.result(stats);
		}
	}
	
	public String toString() {
		return "if (" + condition + ") {" + primary + "} else {" + secondary + "}";
	}
}
