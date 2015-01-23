package nl.davidlieffijn.battleofbots.interpreter;

public class Assignment implements Statement {
	UserVariable left;
	Expression right;

	public Assignment(UserVariable left, Expression right) {
		this.left = left;
		this.right = right;
	}
	
	public String result(int[] stats) {
		left.setValue(new Constant(right.result(stats)));
		return null;
	}
	
	public String toString() {
		return left + "=" + right;
	}
	
}
