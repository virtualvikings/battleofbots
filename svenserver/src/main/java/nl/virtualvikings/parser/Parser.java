package nl.virtualvikings.parser;

import java.util.ArrayList;

public class Parser {
	
	String[] actions = {"TurnLeft", "TurnRight", "GoForward", "GoBackward", "Attack"};
	String[] operators = {"<", ">", "<=", ">=", "==", "!="};
	String[] botVariables = {"HP", "X", "Y", "VIEW_L", "VIEW_F", "VIEW_R", "DIRECTION", "RANDOM", "TURNS"};
	
	ArrayList<UserVariable> userVariables;
	
	char split = ';';
	
	public Parser() {
		userVariables = new ArrayList<UserVariable>();
		String[] names = {"a", "b", "c", "d", "e"};
		for (int i = 0; i < names.length; i++) {
			userVariables.add(new UserVariable(names[i], new Constant(0)));
		}
	}
	
	/**
	 * Removes all the whitespace characters from the code, then adds all the statements to an ArrayList.
	 * @param strategy
	 * @return
	 * @throws Exception
	 */
	public Statement parse(String strategy) throws Exception {
		// Remove all whitespace characters.
		strategy = strategy.replaceAll("\\s+","");
		
		ArrayList<String> lines = new ArrayList<String>();
		int i = 0;
		
		// Add all characters to a String until a the split characters is reached. Then add the String to the list and proceed.
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
		return readLines(lines);
	}
	
	/**
	 * Scans through all the lines of the code and parses them accordingly.
	 * @param s
	 * @return
	 */
	public Statement readLines(ArrayList<String> lines) throws Exception {
		// List of the top level statements.
		ArrayList<Statement> block = new ArrayList<Statement>();
		
		// Evaluate all lines.
		for (int i = 0; i < lines.size(); i++) {
			
			// Current line, that is being evaluated.
			String currentLine = lines.get(i);
			
			// If the current line contains an "if(", it is an if-statement.
			if (currentLine.contains("if(")) {
				// The primary body of the if-statement.
				ArrayList<String> primary = new ArrayList<String>();
				// The secondary (else) body of the if-statement.
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
			// If the current line is an predefined Action.
			} else if (isAction(currentLine)) {
				block.add(new Action(currentLine));
			// If the current line is an Assignment.
			} else if (isAssignment(currentLine)) {
				Assignment a = parseAssignment(currentLine);
				block.add(a);	
			// Nothing is recognized, throw an Exception.
			} else if (!(currentLine.equals("else") || currentLine.equals("end") || currentLine.equals(""))){
				throw new Exception("Line " + (i + 1) + " (" + currentLine + ") could not be parsed correctly.");
			}
			
		}
		
		// The top level Block.
		return new Block(block);
	}

	/**
	 * Parses a given String into an Assignment.
	 * @param s
	 * @return
	 */
	public Assignment parseAssignment(String s) {
		// Split into left-hand and right-hand side.
		String[] parts = s.split("=");
		
		// Right-hand side is an expression.
		Expression right;
		
		// Test if right-hand side is a variable or a number.
		right = parseMath(parts[1]);
		
		if (right == null && isBotVariable(parts[1])) {
			right = new BotVariable(parts[1]);
		}
		
		//System.out.println(right);
		
		UserVariable left = getUserVariable(parts[0]);
		return new Assignment(left, right);
			
	}
	
	/**
	 * Parses a given String into mathematical Expressions.
	 * @param s
	 * @return
	 */
	public Expression parseMath(String e) {
		String[] splitParts = e.split("\\+|\\-|\\*|\\/|\\%");
		ArrayList<Expression> parts = new ArrayList<Expression>();
		
		String operatorString = e.replaceAll("[A-Za-z0-9]","");
		String[] operatorParts = operatorString.split("");
		ArrayList<String> operators = new ArrayList<String>();
		
		for (int i = 0; i < splitParts.length; i++) {
			if (!splitParts[i].equals("")) {
				try {
					parts.add(new Constant(Integer.parseInt(splitParts[i])));
				} catch (Exception x) {
					if (isUserVariable(splitParts[i])) {
						parts.add(getUserVariable(splitParts[i]));
					} else if (isBotVariable(splitParts[i])){
						parts.add(new BotVariable(splitParts[i]));
					} else {
						parts.add(null);
					}
				}
			}
		}
		
		if (parts.contains(null)) {
			return null;
		}
		
		for (int i = 0; i < operatorParts.length; i++) {
			if (!operatorParts[i].equals("")) {
				operators.add(operatorParts[i]);
			}
		}

		String[] allOperators = {"*/%", "+-"};
		
		for (int ao = 0; ao < allOperators.length; ao++) {
			for (int i = 0; i < operators.size(); i++) {
				if (allOperators[ao].contains(operators.get(i))) {
					Expression left = parts.get(i);
					Expression right = parts.get(i + 1);
					parts.remove(left);
					parts.remove(right);
					switch (operators.get(i)) {
					case "*": parts.add(i, new Product(left, right)); break;
					case "/": parts.add(i, new Quotient(left, right)); break;
					case "%": parts.add(i, new Modulo(left, right)); break;
					case "+": parts.add(i, new Sum(left, right)); break;
					case "-": parts.add(i, new Difference(left, right)); break;
					}
					operators.remove(operators.get(i));
					i = i - 1;
				}
			}
		}
		return parts.get(0);
	}
	
	/**
	 * Returns the corresponding UserVariable, given a String.
	 * @param s
	 * @return
	 */
	public UserVariable getUserVariable(String s) {
		for (int i = 0; i < userVariables.size(); i++) {
			if (userVariables.get(i).getName().equals(s)) {
				return userVariables.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Determines if a given String is a UserVariable.
	 * @param v
	 * @return
	 */
	public boolean isUserVariable(String v) {
		for (int i = 0; i < userVariables.size(); i++) {
			if (userVariables.get(i).getName().equals(v)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines if a given String is a BotVariable.
	 * @param v
	 * @return
	 */
	public boolean isBotVariable(String v) {
		for (int i = 0; i < botVariables.length; i++) {
			if (botVariables[i].equals(v)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines if a given String is an Action.
	 * @param s
	 * @return
	 */
	public boolean isAction(String s) {
		for (int i = 0; i < actions.length; i++) {
			if (s.equals(actions[i])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines if a given String is an Assignment.
	 * @param s
	 * @return
	 */
	public boolean isAssignment(String s) {
		if (s.contains("=")) {
			String[] parts = s.split("=");
			return isUserVariable(parts[0]);
		}
		return false;
	}
	
	/**
	 * Parses a Condition into Expressions.
	 * @param condition
	 * @return
	 */
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
			Expression e = parseMath(parts[i]);
			if (e == null) {
				Variable v;
				if (isUserVariable(parts[i])) {
					v = getUserVariable(parts[i]);
				} else {
					v = new BotVariable(parts[i]);
				}
				parsedParts[i] = v;	
			} else {
				parsedParts[i] = e;
			}
		}
		
		return new Condition(parsedParts[0], operator, parsedParts[1]);
	}
}
