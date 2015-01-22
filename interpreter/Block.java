package nl.davidlieffijn.battleofbots.interpreter;

import java.util.ArrayList;

public class Block implements Statement{
	ArrayList<Statement> statements = new ArrayList<Statement>();
	
	public Block(ArrayList<Statement> statements) {
		this.statements = statements;
	}
	
	public String result(byte[] stats) {
		for (int i = 0; i < statements.size(); i++) {
			if (statements.get(i).result(stats) != null) {
				return statements.get(i).result(stats);
			}
		}
		return null;
	}
	
	public String toString() {
		String result = "";
		for (int i = 0; i < statements.size(); i++) {
			if (i < statements.size() - 1) {
				result += statements.get(i).toString() + "; ";
			} else {
				result += statements.get(i).toString();
			}
		}
		return result;
	}
}
