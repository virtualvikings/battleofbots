package nl.davidlieffijn.battleofbots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Strategy {
	
	private String strategy;
	//private ArrayList<String> variables = new ArrayList<String>();
	private HashMap<String,Integer> variables = new HashMap<String,Integer>();
	private boolean start = true;
	
	public Strategy(String strategy) {
		variables.put("counter", 5);
		this.strategy = strategy;
		this.removeWhitespaces();
		this.readCode(this.readParts(this.stringToParts()));
		
	}
	
	private void readCode(ArrayList<ArrayList<String>> parts) {
		for (int i = 0; i < parts.size(); i++) {
			String firstPart = parts.get(i).get(0);
			if (firstPart.contains("if")) {
				String statement = firstPart.split("\\(|\\)")[1];
				System.out.println(statement);
				System.out.println(evaluateStatement(statement));
			}
		}
	}
	
	private boolean evaluateStatement(String statement) {
		if (statement.contains("==")) {
			String[] parts = statement.split("==");
			if (variables.containsKey(parts[0])) {
				if (variables.get(parts[0]) == Integer.parseInt(parts[1])) {
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
	
	private ArrayList<String> stringToParts() {
		String[] lines = strategy.split("\\{|\\}");
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
		start = false;
		return (new Random()).nextInt(4);
	}
}
