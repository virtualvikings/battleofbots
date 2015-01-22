package nl.virtualvikings.parser;

public class Sum implements Expression {
	Expression left;
	Expression right;
	
	public Sum(Expression left, Expression right) {
		this.left = left;
		this.right = right;
	}
	
	public int result(byte[] stats) {
		return left.result(stats) + right.result(stats);
	}
	
	public String toString() {
		return left + " + " + right;
	}
}
