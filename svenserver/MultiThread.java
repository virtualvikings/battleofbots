package nl.virtualvikings.battleofbots;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class MultiThread extends Thread {

	MatchMaker arena;
	int matchIndex;
	long threadId;
	
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	boolean listening = true;
	
	Timer timeOut = new Timer();
	Timer timer = new Timer();
	int time = 0;

	public MultiThread(Socket clientSocket, MatchMaker Arena){
		try{
			this.threadId = this.getId();
			this.arena = Arena;
			this.socket = clientSocket;
			this.out = new PrintWriter(clientSocket.getOutputStream(), false); //Disable autoflush!!!
			this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		try {
			Send("requestKey");
			timeOut.schedule(checkTimeOut, 0, 1000);
			
			String fromClient;
			while((fromClient = in.readLine()) != null && listening){
					System.out.println("Received from Client " + threadId + ": " + fromClient);
					if(fromClient.equals("Correct_Key")){ //Correct key ontvangen, vraag data
						time = 0;
						Send("requestData");
					} 
					
					else if(fromClient.startsWith("Data:")){ //Data ontvangen, zet data in bot en kijk of er een match is gevonden
						String[] info = fromClient.split(",");
						String botName = info[0]; //TODO: filter het stukje 'Data:' voor de naam weg
						String code = info[1];
						
						Bot bot = new Bot(botName, code, threadId);
						matchIndex = (arena.add(bot) - 1) / 2; //Manier om bij te houden in welke playThrough de Thread zit.
						
						if(arena.matchedThreads.contains(threadId) || arena.createMatch()){ //Kijk eerst of 'ik' al in een match zit, zoniet probeer er dan een aan te maken
							
							Send("matchFound");
						}
						else //Er is nog geen match gevonden, dus ga om de seconde kijken (timeOut wordt uitgesteld in timerTask)
							timer.schedule(timerTask, 0, 1000);
					}
					
					else if(fromClient.equals("requestField"))
						Send(arena.Field());
					
					else if(fromClient.equals("requestMoves"))
						Send(arena.Moves());
					
					else if(fromClient.equals("exit")){
						//TODO: zorg ervoor dat de weggevallen client uit de lijst van in MatchMaker wordt verwijdert
						System.out.println("Client " + socket.getRemoteSocketAddress() + " has left.");
						timeOut.cancel();
						break;
					}
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	private void Send(String text) {
		System.out.println("Send to Client " + threadId + ": " + text);
		out.println(text);
		out.flush(); //Required because autoflush is disabled
	}

	//Check of er ondertussen al een match is gevonden.
	public TimerTask timerTask = new TimerTask(){

		@Override
		public void run() {
			time = 0;
			if(arena.matchedThreads.contains(threadId) || arena.createMatch()){ //Is er een match gevonden
				Send("matchFound");
				timer.cancel();
			} 
		}
		
	};
	
	//Check of de client nog verbonden is met de server, huidige timeout is 5 seconde.
	//WARNING: het kan voorkomen dat er een timeout optreed tijdens het zoeken naar een match, omdat de time ondanks de reset wel 3 kan worden...
	public TimerTask checkTimeOut = new TimerTask(){

		@Override
		public void run() {
			time++;
			if(time >= 5){
				try{
					System.out.println("Timeout occurred. Lost connection with client " + socket.getRemoteSocketAddress() + " Assigned ID: "  + threadId);
					listening = false;
					timeOut.cancel();
				} catch(Exception e){
					e.printStackTrace();
				}
			} 
		}
		
	};
}
