package nl.virtualvikings.parser;

public class Modulo implements Expression {
	Expression left;
	Expression right;
	
	public Modulo(Expression left, Expression right) {
		this.left = left;
		this.right = right;
	}
	
	public int result(int[] stats) {
		return left.result(stats) % right.result(stats);
	}
	
	public String toString() {
		return left + " % " + right;
	}
}
