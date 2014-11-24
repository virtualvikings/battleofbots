package nl.davidlieffijn.battleofbots;

public class Bot {
	String name;
	Strategy strategy;
	Thread thread;
	
	public Bot(String data, Thread thread) {
		String[] dataParts = data.split(", ");
		this.name = dataParts[0];
		this.strategy = new Strategy(dataParts[1]);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void sendToClient(String data) {
		
	}
}
