package nl.virtualvikings.battleofbots;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server {

	static int port = 4444;
	static boolean listening = true;
	static ArrayList<Thread>Clients = new ArrayList<Thread>();
	static MatchMaker arena;
	
	public static void main(String[] args) {
		Initialize();
		Thread startConnection = new Thread(run());
		startConnection.start();
	}
	
	private static Runnable run(){
		System.out.println("(" +  new SimpleDateFormat("HH:mm:ss").format(new Date()) + ") Started listening on port: " + port);
		try(ServerSocket socket = new ServerSocket(port)){
			while(listening){
				if(Clients.size() < 4){
					Socket clientSocket = socket.accept();
					
					MultiThread connection = new MultiThread(clientSocket, arena);
					Clients.add(connection);
					
					System.out.println("Client connected from " + clientSocket.getRemoteSocketAddress() + " Assigned ID: " + connection.getId());
					System.out.println("Currently connected clients(" + Clients.size() + "): ");
					for(int i = 0; i < Clients.size(); i++)
						System.out.println("   - " + Clients.get(i).getId());
					
					connection.start();
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static void Initialize(){
		arena = new MatchMaker();
	}

}
