package nl.davidlieffijn.battleofbots;

public class StrategyTest {

	public static void main(String[] args) {
		Strategy strategy = new Strategy("if (START) { Direction = UP; counter=0;}if(counter==2){Direction=DOWN;}if(counter==5){Direction=UP;counter=0;}counter++;if(HP==1){Direction=LEFT;}");
	}
}
