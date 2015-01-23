package nl.davidlieffijn.battleofbots.interpreter;

public class Constant implements Expression {
	int value;
	
	public Constant(int value) {
		this.value = value;
	}

	public int result(int[] stats) {
		return value;
	}
	
	public String toString() {
		return Integer.toString(value);
	}
}
