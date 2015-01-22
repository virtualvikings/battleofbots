package nl.virtualvikings.parser;

public class Number implements Expression {
	int value;
	
	public Number(int value) {
		this.value = value;
	}

	public int result(byte[] stats) {
		return value;
	}
	
	public String toString() {
		return Integer.toString(value);
	}
}
