package nl.davidlieffijn.battleofbots;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;

public class MultiServerThreadTest extends Thread {
    private Socket socket = null;
    private MultiServerTest mst;
    private MatchCentre matchCentre;
 
    public MultiServerThreadTest(Socket socket, MultiServerTest mst, MatchCentre matchCentre) {
        super("MultiServerThreadTest");
        this.socket = socket;
        this.mst = mst;
        this.matchCentre = matchCentre;
        System.out.println("Created thread " + this.getId() + " (" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ")");
    }
    
    public void run() {
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            ProtocolTest kkp = new ProtocolTest();
            outputLine = kkp.processInput(null);
            out.println(outputLine);
            Bot bot = null;
            
            while ((inputLine = in.readLine()) != null) {
            	System.out.println("Received: " + inputLine);
                outputLine = kkp.processInput(inputLine);
                if (outputLine.equals("matchFound")) {
                	bot = new Bot(inputLine, this);
                	matchCentre.addBot(bot);
                	outputLine = matchCentre.getResult();
                }
                if (outputLine.equals("exit"))
                    break;
                out.println(outputLine);
            	System.out.println("Sent: " + outputLine);
            }
            mst.removeThread(this);
            matchCentre.removeBot(bot);
            
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
