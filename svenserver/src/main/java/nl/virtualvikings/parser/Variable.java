package nl.virtualvikings.parser;

public class Variable implements Expression {
	String name;
	Expression value;
	
	public Variable(String name, Expression value) {
		this.name = name;
		this.value = value;
	}

	public int result(byte[] stats) {
		return value.result(stats);
	}
	
	public String toString() {
		return name;
	}
	
}
