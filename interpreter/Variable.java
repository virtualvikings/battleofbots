package nl.davidlieffijn.battleofbots.interpreter;

public class Variable implements Expression {
	String name;
	Expression value;
	
	public Variable(String name, Expression value) {
		this.name = name;
		this.value = value;
	}

	public int result(int[] stats) {
		return value.result(stats);
	}
	
	public String toString() {
		return name;
	}
	
}
