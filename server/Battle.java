package nl.davidlieffijn.battleofbots;

public class Battle {
	
	private Bot bot1, bot2;
	private Arena arena;

	public Battle(Bot bot1, Bot bot2) {
		this.bot1 = bot1;
		this.bot2 = bot2;
		//bot1.setLocation(1,1);
		//bot2.setLocation(18,18);
		arena = new Arena();
	}
	
	public String start() {
		while (bot1.getHealth() > 0 && bot2.getHealth() > 0) {
			int[] move1 = bot1.nextMove();
			int[] move2 = bot2.nextMove();
			
			if (arena.isValidMove(1, move1[0], move1[1])) {
				arena.moveBot(1, move1);
			}
			
			if (arena.isValidMove(2, move2[0], move2[1])) {
				arena.moveBot(2, move2);
			}
			//arena.showGraphicView();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
