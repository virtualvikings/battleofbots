package nl.davidlieffijn.battleofbots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Strategy {
	
	private String strategy;
	//private ArrayList<String> variables = new ArrayList<String>();
	private HashMap<String, Integer> userVariables = new HashMap<String, Integer>();
	private HashMap<String, Integer> botVariables = new HashMap<String, Integer>();
	private HashMap<String, Integer> moves = new HashMap<String, Integer>();
	private boolean start = true;
	private ArrayList<String> parsedCode;
	private int nextMove;
	private Bot bot;
	
	public Strategy(Bot bot, String strategy) {
		//System.out.println("- " + bot.getName() + " ---");
		createMoves();
		userVariables.put("counter", 0);
		this.strategy = strategy;
		this.removeWhitespaces();
		this.bot = bot;
		//Parse the code
		//partedCode = this.readParts(this.stringToParts(this.strategy));
		parsedCode = ParseCode.parse(strategy);
	}
	
	public void runCode() {
		for (int i = 0; i < parsedCode.size(); i++) {
			System.out.println(parsedCode.get(i));
			String currentLine = parsedCode.get(i);
			if (currentLine.equals("if")) {
				if (evaluateStatement(parsedCode.get(i+1))) {
					int n = 2;
					while (!parsedCode.get(i+n).equals("end")) {
						//System.out.println("n: " + parsedCode.get(i+n));
						n++;
					}
				}
			}
		}
	}
	
	private boolean evaluateStatement(String statement) {
		if (statement.equals("START")) {
			return start;
		}
		if (statement.contains("==")) {
			String[] parts = statement.split("==");
			return true;
		}
		return false;
	}
	
	private int getVariableValue() {
		return 0;
	}
	
	
	
	private void createMoves() {
		moves.put("GoUp", 0);
		moves.put("GoRight", 1);
		moves.put("GoDown", 2);
		moves.put("GoLeft", 3);
		moves.put("Attack", 4);
		moves.put("GoRandom", 5);
	}
	
	private void createBotVariables() {
		
	}
	
	private void readCode(ArrayList<ArrayList<String>> parts) {
		for (int i = 0; i < parts.size(); i++) {
			String firstPart = parts.get(i).get(0);
			if (firstPart.contains("if")) {
				String statement = firstPart.split("\\(|\\)")[1];
				if (evaluateStatement(statement)) {
					execute(parts.get(i).get(1));
				}
			} else {
				doExpression(parts.get(i).get(0));
			}
		}
	}
	
	private void execute(String parts) {
		ArrayList<String> list = partExpressions(parts);
		for (int i = 0; i < list.size(); i++) {
			doExpression(list.get(i));
		}
	}
	
	private void doExpression(String expression) {
		// Assign a new value to a variable
		if (expression.charAt(expression.length() - 1) == ';') {
			expression = expression.substring(0, expression.length() - 1);
		}
		if (expression.contains("=")) {
			String[] parts = expression.split("=");
			String name = parts[0];
			if (userVariables.containsKey(name)) {
				userVariables.put(name, Integer.parseInt(parts[1]));
				System.out.println(name + " is now " + parts[1]);
			}
		// Increase a variable with 1
		} else if (expression.contains("++")) {
			String name = expression.substring(0, expression.length() - 2);
			if (userVariables.containsKey(name)) {
				int oldValue = userVariables.get(name);
				userVariables.put(name, oldValue + 1);
				System.out.println(name + " is increased to " + (oldValue + 1));
			}
		// A command
		} else {
			if (moves.containsKey(expression)) {
				int move = moves.get(expression);
				if (move == 5) {
					nextMove = (new Random()).nextInt(4);
				} else {
					nextMove = move;
				}
				System.out.println(expression + " is executed (" + nextMove + ")");
			}
		}
	}
	
	private int getVariableValue(String variable) {
		boolean allUpperCase = true;
		boolean allLowerCase = true;
		for (int i = 0; i < variable.length(); i++) {
			if (Character.isUpperCase(variable.charAt(i))) {
				allLowerCase = false;
			} else {
				allUpperCase = false;
			}
		}
		if (allUpperCase) {
			if (variable.equals("HEALTH")) {
				//return bot.getHealth();
				return 100;
			}
			if (variable.equals("OBSTACLENEXT")) {
				return 1;
			}
			if (variable.equals("OPPONENTNEXT")) {
				return 0;
			}
		}
		if (allLowerCase && userVariables.containsKey(variable)) {
			return userVariables.get(variable);
		}
		
		return -1;
	}
	
	private ArrayList<String> partExpressions(String expressions) {
		String[] parts = expressions.split(";");
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < parts.length; i++) {
			result.add(parts[i]);
		}
		return result;
	}
	
	/*private boolean evaluateStatement(String statement) {
		if (statement.contains("==")) {
			String[] parts = statement.split("==");
			if (userVariables.containsKey(parts[0])) {
				if (getVariableValue(parts[0]) == Integer.parseInt(parts[1])) {
					return true;
				}
			} else {
				System.out.println(statement + " is " + (getVariableValue(parts[0]) == Integer.parseInt(parts[1])));
				if (getVariableValue(parts[0]) == Integer.parseInt(parts[1])) {
					
					return true;
				}
			}
		} else {
			if (statement.equals("START")) {
				if (start) {
					start = false;
					return true;
				}
			} else {
				if (getVariableValue(statement) == 1) {
					return true;
				} 
			}
		}
		return false;
	}*/
	
	private ArrayList<ArrayList<String>> readParts(ArrayList<String> parts) {
		ArrayList<ArrayList<String>> greaterParts = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < parts.size(); i++) {
			if (parts.get(i).contains("if")) {
				greaterParts.add(new ArrayList<String>(parts.subList(i, i+2)));
				i++;
			} else {
				ArrayList<String> notIf = new ArrayList<String>();
				notIf.add(parts.get(i));
				greaterParts.add(notIf);
			}
		}
		for (int i = 0; i < greaterParts.size(); i++) {
			System.out.println(greaterParts.get(i));
		}
		System.out.println("");
		return greaterParts;
	}
	
	// 1. 
	private ArrayList<String> stringToParts(String s) {
		String[] lines = s.split("\\{|\\}");
		ArrayList<String> parts = new ArrayList<String>();
		for (int i = 0; i < lines.length; i++) {
			
			if (lines[i].contains("if")) {
				String[] ifParts = lines[i].split(";");
				for (int j = 0; j < ifParts.length; j++) {
					parts.add(ifParts[j]);
				}
			} else {
				parts.add(lines[i]);
			}
			
		}
		for (int i = 0; i < parts.size(); i++) {
			System.out.println(parts.get(i));
		}
		System.out.println("");
		
		return parts;
	}
	
	private void removeWhitespaces() {
		strategy = strategy.replaceAll("\\s+","");
	}
	
	public int nextMove() {
		//System.out.println("\n- " + bot.getName() + " ---");
		runCode();
		return nextMove;
	}
}
