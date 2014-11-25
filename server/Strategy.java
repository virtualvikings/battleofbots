package nl.davidlieffijn.battleofbots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Strategy {
	
	private String strategy;
	//private ArrayList<String> variables = new ArrayList<String>();
	private HashMap<String, Integer> userVariables = new HashMap<String, Integer>();
	private HashMap<String, Integer> moves = new HashMap<String, Integer>();
	private boolean start = false;
	private ArrayList<ArrayList<String>> partedCode;
	private int nextMove;
	
	public Strategy(String strategy) {
		createMoves();
		userVariables.put("counter", 0);
		this.strategy = strategy;
		this.removeWhitespaces();
		partedCode = this.readParts(this.stringToParts(this.strategy));
	}
	
	private void createMoves() {
		moves.put("GoUp", 0);
		moves.put("GoRight", 1);
		moves.put("GoDown", 2);
		moves.put("GoLeft", 3);
		moves.put("Attack", 4);
	}
	
	private void readCode(ArrayList<ArrayList<String>> parts) {
		for (int i = 0; i < parts.size(); i++) {
			String firstPart = parts.get(i).get(0);
			if (firstPart.contains("if")) {
				String statement = firstPart.split("\\(|\\)")[1];
				if (evaluateStatement(statement)) {
					System.out.println("True statement: " + statement);
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
		System.out.println("Expression: " + expression);
		if (expression.contains("=")) {
			System.out.println("=");
			String[] parts = expression.split("=");
			String name = parts[0];
			if (userVariables.containsKey(name)) {
				userVariables.put(name, Integer.parseInt(parts[1]));
			}
		} else if (expression.contains("++")) {
			System.out.println("++");
			String name = expression.substring(0, expression.length() - 2);
			if (userVariables.containsKey(name)) {
				int oldValue = userVariables.get(name);
				userVariables.put(name, oldValue + 1);
			}
		} else {
			System.out.println("x");
			if (moves.containsKey(expression)) {
				nextMove = moves.get(expression);
			}
		}
		System.out.println(userVariables.get("counter"));
	}
	
	private ArrayList<String> partExpressions(String expressions) {
		String[] parts = expressions.split(";");
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < parts.length; i++) {
			result.add(parts[i]);
		}
		return result;
	}
	
	private boolean evaluateStatement(String statement) {
		if (statement.contains("==")) {
			String[] parts = statement.split("==");
			if (userVariables.containsKey(parts[0])) {
				if (userVariables.get(parts[0]) == Integer.parseInt(parts[1])) {
					return true;
				}
			}
		} else {
			if (statement.equals("START")) {
				return start;
			} else {
				
			}
		}
		return false;
	}
	
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
		System.out.println("\n");
		return greaterParts;
	}
	
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
		System.out.println("\n");
		
		return parts;
	}
	
	private void removeWhitespaces() {
		strategy = strategy.replaceAll("\\s+","");
	}
	
	public int nextMove() {
		readCode(partedCode);
		return nextMove;
	}
}
