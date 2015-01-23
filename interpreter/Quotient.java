package nl.davidlieffijn.battleofbots.interpreter;

public class Quotient implements Expression {
	Expression left;
	Expression right;
	
	public Quotient(Expression left, Expression right) {
		this.left = left;
		this.right = right;
	}
	
	public int result(int[] stats) {
		return (int) Math.round(Math.round((float) left.result(stats) / right.result(stats)));
	}
	
	public String toString() {
		return left + " / " + right;
	}
}
