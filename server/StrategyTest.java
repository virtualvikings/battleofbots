package nl.davidlieffijn.battleofbots;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class StrategyTest {

	public static void main(String[] args) throws IOException {
		Path file = FileSystems.getDefault().getPath("/Users/David/Desktop", "code2.txt");
		List<String> fileArray;
		fileArray = Files.readAllLines(file, Charset.defaultCharset());
		String code = "";
		for (int i = 0; i < fileArray.size(); i++) {
			code += fileArray.get(i);
		}
		System.out.println(code);
		Strategy strategy = new Strategy(null, code);
		System.out.println("Value: " + strategy.getVariableValue("HEALTH"));
	}
}
