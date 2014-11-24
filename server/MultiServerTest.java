package nl.davidlieffijn.battleofbots;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class MultiServerTest {
	ArrayList<MultiServerThreadTest> threads = new ArrayList<MultiServerThreadTest>();
	//ArrayList<Bot> bots = new ArrayList<Bot>();
	MatchCentre matchCentre = new MatchCentre();
	int portNumber;
	boolean listening = true;
	
	public MultiServerTest(int portNumber) {
		this.portNumber = portNumber;
		this.listen();
	}
	
	public void listen() {
		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			while (listening) {
				Socket clientSocket = serverSocket.accept();
				if (threads.size() < 4) {
					
					MultiServerThreadTest test = new MultiServerThreadTest(clientSocket, this, matchCentre);
					test.start();
					threads.add(test);

					System.out.println(listClients());
				} else {
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server too busy, try again later.");
					os.close();	
				}
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
	}
	
	public void removeThread(Thread t) {
		threads.remove(t);
	}

	private String listClients() {
		String list = "Currently connected clients:\n";
		for (int i = 0; i < threads.size(); i++) {
			list += threads.get(i).getId() + "\n";
		}
		return list;
	}
}
