package nl.davidlieffijn.battleofbots.interpreter;

import java.util.ArrayList;

public class Parser {
	
	String[] actions = {"TurnLeft", "TurnRight", "GoForward", "GoBackward"};
	String[] operators = {"<", ">", "<=", ">=", "==", "!="};
	String[] botVariables = {"HP", "POS-X", "POS-Y", "VIEW", "DIRECTION", "RANDOM"};
	
	ArrayList<UserVariable> userVariables;
	
	char split = ';';
	Statement start;
	
	public Parser(ArrayList<UserVariable> variables) {
		this.userVariables = variables;
	}
	
	public Statement parse(String strategy) {
		strategy = strategy.replaceAll("\\s+","");
		System.out.println(strategy + "\n");
		ArrayList<String> lines = new ArrayList<String>();
		int i = 0;
		while (i < strategy.length()) {
			int j = i;
			String currentLine = "";
			while (strategy.charAt(j) != split) {
				currentLine += strategy.charAt(j);
				j++;
			}
			lines.add(currentLine);
			i = j + 1;
		}
		for (int x = 0; x < lines.size(); x++) {
			//System.out.println(lines.get(x));
		}
		//System.out.println();
		return readLines(lines);
	}
	
	public Statement readLines(ArrayList<String> lines) {
		
		ArrayList<Statement> block = new ArrayList<Statement>();
		for (int i = 0; i < lines.size(); i++) {
			String currentLine = lines.get(i);
			
			if (currentLine.contains("if(")) {
				ArrayList<String> primary = new ArrayList<String>();
				ArrayList<String> secondary = new ArrayList<String>();
				int nestCount = 1;
				boolean elseFound = false;
				String whileLine;
				while (nestCount != 0) {
					whileLine = lines.get(i+1);
					
					if (whileLine.contains("if(")) {
						nestCount++;
					} else if (whileLine.equals("end")) {
						nestCount--;
					} else if (whileLine.equals("else") && nestCount == 1) {
						elseFound = true;
					}
					
					if (!elseFound) {
						primary.add(whileLine);
					} else {
						secondary.add(whileLine);
					}
					//System.out.println(whileLine + " (" + nestCount + ")");
					i++;
				}
				Condition c = parseCondition(currentLine.substring(3, currentLine.length() - 1));
				block.add(new If(c, readLines(primary), readLines(secondary)));	
			} else if (isAction(currentLine)) {
				block.add(new Action(currentLine));
			} else if (isAssignment(currentLine)) {
				Assignment a = parseAssignment(currentLine);
				block.add(a);	
			} else if (!(currentLine.contains("else") || currentLine.contains("e") || currentLine.equals(""))){
				System.err.println("Regel " + (i + 1) + " (" + currentLine + ") is geen geldige code!");
			}
			
		}
		return new Block(block);
	}
	
	public Assignment parseAssignment(String s) {
		// Split into left-hand and right-hand side.
		String[] parts = s.split("=");
		
		// Right-hand side is an expression.
		Expression right;
		
		// Test if right-hand side is a variable or a number.
		if (isUserVariable(parts[1])) {
			right = getUserVariable(parts[1]);
		} else if (isBotVariable(parts[1])){
			right = new BotVariable(parts[1]);
		} else {
			right = new Number(Integer.parseInt(parts[1]));
		}
		
		UserVariable left = getUserVariable(parts[0]);
		return new Assignment(left, right);
			
	}
	
	public UserVariable getUserVariable(String s) {
		for (int i = 0; i < userVariables.size(); i++) {
			if (userVariables.get(i).getName().equals(s)) {
				return userVariables.get(i);
			}
		}
		return null;
	}
	
	public boolean isUserVariable(String v) {
		for (int i = 0; i < userVariables.size(); i++) {
			if (userVariables.get(i).getName().equals(v)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isBotVariable(String v) {
		for (int i = 0; i < botVariables.length; i++) {
			if (botVariables[i].equals(v)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAction(String s) {
		for (int i = 0; i < actions.length; i++) {
			if (s.equals(actions[i])) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAssignment(String s) {
		if (s.contains("=")) {
			String[] parts = s.split("=");
			return isUserVariable(parts[0]);
		}
		return false;
	}
	
	public Condition parseCondition(String condition) {
		String operator = "";
		for (int i = 0; i < operators.length; i++) {
			if (condition.contains(operators[i])) {
				operator = operators[i];
			}
		}
		String[] parts = condition.split(operator);
		Expression[] parsedParts = new Expression[2];
		for (int i = 0; i < parts.length; i++) {
			try {
				int parsed = Integer.parseInt(parts[i]);
				parsedParts[i] = new Number(parsed);
			} catch (Exception e) {
				Variable v;
				if (isUserVariable(parts[i])) {
					v = getUserVariable(parts[i]);
				} else {
					v = new BotVariable(parts[i]);
				}
				parsedParts[i] = v;
				
			}
		}
		System.out.println(parsedParts[0].getClass() + " " + parsedParts[1].getClass());
		
		return new Condition(parsedParts[0], operator, parsedParts[1]);
	}
}
