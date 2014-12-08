package nl.davidlieffijn.battleofbots;

import java.io.*;
import java.net.*;

public class ClientTest {
	
	String hostName;
	int portNumber;
	
	public ClientTest(String hostName, int portNumber) {
		this.hostName = hostName;
		this.portNumber = portNumber;
		this.connect();
	}
	
	public void connect() {
		try (
				Socket kkSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(kkSocket.getOutputStream(),true);
				BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
				) {
					String fromServer;
					String fromUser;

					// Message from server.
					while ((fromServer = in.readLine()) != null) {
						//System.out.println("Server: " + fromServer);
						fromUser = "";
						if (fromServer.equals("exit"))
							break;
						if (fromServer.equals("requestKey"))
							fromUser = "Correct_Key";
						if (fromServer.equals("requestData"))
							fromUser = "Bobbie, for (int i = 0; i < tiles; i++) { getHealth(); }";
							
						if (!fromServer.equals("RESULT")) {
							out.println(fromUser);
							//System.out.println("Client: " + fromUser);
						}
						
					}
			} catch (UnknownHostException e) {
				System.err.println("Don't know about host " + hostName);
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to "
						+ hostName);
				System.exit(1);
			}
	}
}
