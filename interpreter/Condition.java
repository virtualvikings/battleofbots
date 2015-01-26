package nl.davidlieffijn.battleofbots.interpreter;

public class Condition implements Expression {
	Expression left;
	String type;
	Expression right;
	
	public Condition(Expression left, String type, Expression right) {
		this.left = left;
		this.type = type;
		this.right = right;
	}
	
	public int result(int[] stats) {
		switch (type) {
		case "==":
			if (left.result(stats) == right.result(stats)) { return 1; };
			break;
		case ">":
			if (left.result(stats) > right.result(stats)) { return 1; };
			break;
		case "<":
			if (left.result(stats) < right.result(stats)) { return 1; };
			break;
		case ">=":
			if (left.result(stats) >= right.result(stats)) { return 1; };
			break;
		case "<=":
			if (left.result(stats) <= right.result(stats)) { return 1; };
			break;
		case "!=":
			if (left.result(stats) != right.result(stats)) { return 1; };
			break;
		}
		return 0;
	}
	
	public String toString() {
		return left + " " + type + " " + right;
	}
}
