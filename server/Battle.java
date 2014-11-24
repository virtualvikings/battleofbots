package nl.davidlieffijn.battleofbots;

public class Battle {
	
	private Bot bot1, bot2;

	public Battle(Bot bot1, Bot bot2) {
		this.bot1 = bot1;
		this.bot2 = bot2;
	}
	
	public String start() {
		return "resultOfBattle";
	}
}
