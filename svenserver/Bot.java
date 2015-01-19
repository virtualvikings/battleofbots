package nl.virtualvikings.battleofbots;

public class Bot {
	String name, code;
	int health = 100;
	long connectedThread;
	
	public String Name() { return name; }
	public String Code() { return code; }
	public int Health() { return health; }
	public long getConnectedThread() { return connectedThread; }
	
	public Bot(String Name, String Code, long ConnectedThreadID){
		this.name = Name;
		this.code = Code;
		this.connectedThread = ConnectedThreadID;
	}
}
