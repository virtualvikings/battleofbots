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
		
			Path file = FileSystems.getDefault().getPath("/Users/David/Documents", "test.txt");
			List<String> fileArray;
			fileArray = Files.readAllLines(file, Charset.defaultCharset());
			String code = "";
			for (int i = 0; i < fileArray.size(); i++) {
				code += fileArray.get(i) + ";";
			}
			
			Parser parser = new Parser();
			try {
				Statement parsed = parser.parse(code);
				System.out.println("PARSED: " + parsed);
				// 			{DIR, HP, X, Y, TURNS, VL, VF, VR}
				int[] stats = {1, 10, 0, 0, 10, 0, 0, 0};
				System.out.println("RESULT: " + parsed.result(stats));
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
			
	}
}
