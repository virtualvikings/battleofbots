package nl.davidlieffijn.battleofbots;

import java.util.ArrayList;

public class MatchCentre {
	private ArrayList<Bot> bots = new ArrayList<Bot>();
	private ArrayList<Bot> freeBots = new ArrayList<Bot>();
	private ArrayList<Battle> battles = new ArrayList<Battle>();
	
	public MatchCentre() {
	}
	
	public void addBot(Bot bot) {
		this.bots.add(bot);
		this.freeBots.add(bot);
		this.update();
	}
	
	public void removeBot(Bot bot) {
		this.bots.remove(bot);
		this.freeBots.remove(bot);
		//bot.getBattle().stop();
	}
	
	public String getResult() {
		return "RESULT";
	}
	
	public void update() {
		if (this.freeBots.size() >= 2) { 
			Bot bot1 = freeBots.get(0);
			Bot bot2 = freeBots.get(1);
			Battle battle = new Battle(bot1, bot2); 
			this.battles.add(battle);
			String result = battle.start();
			freeBots.remove(bot1);
			freeBots.remove(bot2);
			System.out.println("Started a new match: " + bot1.getName() + " vs " + bot2.getName());
		}
	}
}
