package nl.davidlieffijn.battleofbots;

public class BattleTest {

	public static void main(String[] args) {
		Bot bot1 = new Bot("Henk, if (START) { GoRight; counter=0;}if(counter==1){GoDown;}if(counter==2){GoRight;counter=0;}counter++;if(HP==1){GoLeft;}", null);
		Bot bot2 = new Bot("Piet, if (START) { GoUp; counter=0;}if(counter==2){GoDown;}if(counter==4){GoUp;counter=0;}counter++;if(HP==1){GoLeft;}", null);
		
		Battle battle = new Battle(bot1, bot2);
		battle.start();
	}
}