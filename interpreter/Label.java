package nl.davidlieffijn.battleofbots.interpreter;

public class Label implements Statement {
	String name;
	Statement block;
	
	public Label(String name, Statement block) {
		this.name = name;
		this.block = block;
	}
	
	public String getName() {
		return name;
	}
	
	public String result(int[] stats) {
		return block.result(stats);
	}
	
	public String toString() {
		return "label " + name + " " + block.toString();
	}
}
