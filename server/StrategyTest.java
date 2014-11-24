package nl.davidlieffijn.battleofbots;

public class StrategyTest {

	public static void main(String[] args) {
		Strategy strategy = new Strategy("if (START) { GoUp; counter=0;}if(counter==2){GoDown;}if(counter==5){GoUp;counter=0;}counter++;if(HP==1){GoLeft;}");
	}
}
