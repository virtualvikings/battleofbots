package nl.virtualvikings.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Test {
	
	public static void main(String[] args) throws IOException {
		//Statement[] parts = {new If(new Condition(new Number(2), ">", new Number(1)), new Action("UP")), new Action("DOWN")};
		//Statement block = new Block(parts);
		//System.out.println("RESULT: " + block.result());
		//System.out.println(block.toString());
		
			Path file = FileSystems.getDefault().getPath("/Users/David/Desktop", "code2.txt");
			List<String> fileArray;
			fileArray = Files.readAllLines(file, Charset.defaultCharset());
			String code = "";
			for (int i = 0; i < fileArray.size(); i++) {
				code += fileArray.get(i) + ";";
			}
			ArrayList<UserVariable> variables = new ArrayList<UserVariable>();
			String[] userVariables = {"a", "b", "c", "d", "e"};
			for (int i = 0; i < userVariables.length; i++) {
				variables.add(new UserVariable(userVariables[i], new Constant(0)));
			}
			
			Parser parser = new Parser(variables);
			try {
				Statement parsed = parser.parse(code);
				System.out.println("PARSED: " + parsed);
				// 			{DIR, HP, X, Y, TURNS, VL, VF, VR}
				int[] stats = {1, 100, 0, 0, 10, 0, 0, 0};
				System.out.println("RESULT: " + parsed.result(stats));
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
			
	}
}
