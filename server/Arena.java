package nl.davidlieffijn.battleofbots;

import java.util.Random;

public class Arena {
	private static final int HEIGHT = 20;
	private static final int WIDTH = 20;
	private int[][] field = new int[WIDTH][HEIGHT];
	
	private static final int EMPTY = 0;
	private static final int BOT1 = 1;
	private static final int BOT2 = 2;
	private static final int OBSTACLE = 3;
	
	private int[] locationBot1 = {1, 1};
	private int[] locationBot2 = {18, 18};
	
	private int numberOfObstacles = 0;
	
	public Arena() {
		this.generate();
		this.updateBots();
	}
	
	public void generate() {
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				if (!((x == 1 && y == 1) || (x == 18 && y == 18))) {
					int random = (new Random()).nextInt(10);
					if (random == 0) {
						field[x][y] = OBSTACLE;
						numberOfObstacles++;
					} else {
						field[x][y] = EMPTY;
					}
				} else {
					field[x][y] = x;
				}
			}
		}
	}
	
	public void updateBots() {
		field[locationBot1[0]][locationBot1[1]] = BOT1;
		field[locationBot2[0]][locationBot2[1]] = BOT2;
	}
	
	public void moveBot(int bot, int[] location) {
		if (bot == 1) {
			field[locationBot1[0]][locationBot1[1]] = 0;
			locationBot1[0] += location[0];
			locationBot1[1] += location[1];
			field[locationBot1[0]][locationBot1[1]] = bot;
		} else {
			field[locationBot2[0]][locationBot2[1]] = 0;
			locationBot2[0] += location[0];
			locationBot2[1] += location[1];
			field[locationBot2[0]][locationBot2[1]] = bot;
		}
		
		
	}
	
	public int getNumberOfObstacles() {
		return numberOfObstacles;
	}
	
	public boolean isValidMove(int bot, int dx, int dy) {
		int x, y;
		if (bot == 1) {
			x = locationBot1[0] + dx;
			y = locationBot1[1] + dy;
		} else {
			x = locationBot2[0] + dx;
			y = locationBot2[1] + dy;
		}
		return isExistingField(x, y) && !(isObstacle(x, y));
	}
	
	public boolean isExistingField(int x, int y) {
		if (x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT) {
			return true;
		}
		return false;
	}
	
	public boolean isObstacle(int x, int y) {
		return field[x][y] == 3;
	}
	
	public String toString() {
		String result = "";
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				result += field[x][y] + " ";
			}
			result += "\n";
		}
		return result;
	}
	
	public void showGraphicView() {
		String view = this.toString();
		view = view.replace('3','x');
		view = view.replace('0', '.');
		System.out.println(view);
	}
}
