package nl.davidlieffijn.battleofbots.interpreter;

public class Difference implements Expression {
	Expression left;
	Expression right;
	
	public Difference(Expression left, Expression right) {
		this.left = left;
		this.right = right;
	}
	
	public int result(int[] stats) {
		return left.result(stats) - right.result(stats);
	}
	
	public String toString() {
		return left + " - " + right;
	}
}
