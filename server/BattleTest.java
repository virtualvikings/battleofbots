package nl.davidlieffijn.battleofbots;

public class BattleTest {

	public static void main(String[] args) {
		Battle battle = new Battle(new Bot("Henk, strategy", null), new Bot("Piet, strategy", null));
		battle.start();
	}
}