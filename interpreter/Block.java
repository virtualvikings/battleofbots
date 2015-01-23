package nl.davidlieffijn.battleofbots.interpreter;

import java.util.ArrayList;

public class Block implements Statement{
	ArrayList<Statement> statements = new ArrayList<Statement>();
	
	public Block(ArrayList<Statement> statements) {
		this.statements = statements;
	}
	
	public String result(int[] stats) {
		for (int i = 0; i < statements.size(); i++) {
			//System.out.println(i + " " + statements.get(i) + " "+ statements.get(i).result(stats));
			String s = statements.get(i).result(stats);
			if (s != null) {	
				return s;
			}
		}
		return null;
	}
	
	public String toString() {
		String result = "[";
		for (int i = 0; i < statements.size(); i++) {
			if (i < statements.size() - 1) {
				result += statements.get(i).toString() + ", ";
			} else {
				result += statements.get(i).toString() + "]";
			}
		}
		return result;
	}
}
