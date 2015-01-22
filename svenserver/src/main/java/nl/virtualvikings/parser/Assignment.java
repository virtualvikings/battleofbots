package nl.virtualvikings.parser;

import java.lang.*;

public class Assignment implements Statement {
	UserVariable left;
	Expression right;
	
	public Assignment(UserVariable left, Expression right) {
		this.left = left;
		this.right = right;
	}
	
	public String result(byte[] stats) {
		left.setValue(new Number(right.result(stats)));
		return null;
	}
	
	public String toString() {
		return left + "=" + right;
	}
	
}
