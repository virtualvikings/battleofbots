package nl.davidlieffijn.battleofbots;

public class Bot {
	private String name;
	private Strategy strategy;
	private Thread thread;
	
	private static final int DEFAULTHEALTH = 100;
	private static final int UP = 0;
	private static final int RIGHT = 1;
	private static final int DOWN = 2;
	private static final int LEFT = 3;
	private static final int ATTACK = 4;
	
	private int health;
	//private int x, y;
	
	public Bot(String data, Thread thread) {
		String[] dataParts = data.split(", ");
		this.name = dataParts[0];
		this.strategy = new Strategy(this, dataParts[1]);
		this.health = DEFAULTHEALTH;
	}
	
	public int[] nextMove() {
		int move = strategy.nextMove();
		int x = 0;
		int y = 0;
		switch (move) {
			case UP: y--; break;
			case RIGHT: x++; break;
			case DOWN: y++; break;
			case LEFT: x--; break;
			case ATTACK: break;
		}
		int[] result = {x, y};
		return result;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getHealth() {
		return health;
	}
	
	/*public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}*/
	
	public void sendToClient(String data) {
		
	}
}
