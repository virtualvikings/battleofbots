package nl.davidlieffijn.battleofbots;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BattleTest {

	public static void main(String[] args) throws IOException {
		Path file = FileSystems.getDefault().getPath("/Users/David/Desktop", "code1.txt");
		List<String> fileArray;
		fileArray = Files.readAllLines(file, Charset.defaultCharset());
		String code = "";
		for (int i = 0; i < fileArray.size(); i++) {
			code += fileArray.get(i);
		}
		
		Bot bot1 = new Bot("Bot1, " + code, null);
		
		file = FileSystems.getDefault().getPath("/Users/David/Desktop", "code2.txt");
		fileArray = Files.readAllLines(file, Charset.defaultCharset());
		code = "";
		for (int i = 0; i < fileArray.size(); i++) {
			code += fileArray.get(i);
		}
		
		Bot bot2 = new Bot("Bot2, " + code, null);
		
		Battle battle = new Battle(bot1, bot2);
		battle.start();
	}
}