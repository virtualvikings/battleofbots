package nl.davidlieffijn.battleofbots;

import java.util.ArrayList;

public class ParseCode {
	public static ArrayList<String> parse(String strategy) {
		//System.out.println(strategy);
		strategy = strategy.replaceAll("\\s+","");
		ArrayList<String> parts = new ArrayList<String>();
		int i = 0;
		while (i < strategy.length()) {
			int charsToEnd = strategy.length() - (i + 1);
			if (charsToEnd > 2 && strategy.substring(i, i + 3).equals("if(")) {
				parts.add("if");
				i += 3;
				String statement = "";
				int parentheses = 1;
				while (parentheses != 0) {
					if (strategy.charAt(i) == '(') {
						parentheses++;
					}
					if (strategy.charAt(i) == ')') {
						parentheses--;
					}
					if (parentheses != 0) {
						statement += strategy.charAt(i);
					}
					i++;
				}
				parts.add(statement);
				i++;
				String exec = "";
				int braces = 1;
				while (braces != 0) {
					if (strategy.charAt(i) == '{') {
						braces++;
					}
					if (strategy.charAt(i) == '}') {
						braces--;
					}
					if (braces != 0) {
						exec += strategy.charAt(i);
					}
					i++;
				}
				// System.out.println(exec + " to " + this.parseCode(exec));
				parts.addAll(ParseCode.parse(exec));
				if (charsToEnd > 3
						&& strategy.substring(i, i + 4).equals("else")) {
					parts.add("else");
					i += 5;
					exec = "";
					braces = 1;
					while (braces != 0) {
						if (strategy.charAt(i) == '{') {
							braces++;
						}
						if (strategy.charAt(i) == '}') {
							braces--;
						}
						if (braces != 0) {
							exec += strategy.charAt(i);
						}
						i++;
					}
					parts.addAll(ParseCode.parse(exec));
					parts.add("end");
				} else {
					parts.add("end");
				}
				i--;
			} else {
				String part = "";
				while (i < strategy.length() && strategy.charAt(i) != ';') {
					part += strategy.charAt(i);
					i++;
				}
				parts.add(part);
			}
			i++;
		}
		
		/*System.out.println();
		for (int j = 0; j < parts.size(); j++) {
			System.out.println(parts.get(j)); 
		}*/
		
		return parts;

	}
}
