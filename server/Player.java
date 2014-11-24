package nl.davidlieffijn.battleofbots;

public class Player {
	String name;
	Bot bot;
	Battle battle;
	
	public Player(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Battle getBattle() {
		return this.battle;
	}
	
	public void addBot(Bot bot) {
		this.bot = bot;
	}
	
	public Bot getBot() {
		return bot;
	}
}
