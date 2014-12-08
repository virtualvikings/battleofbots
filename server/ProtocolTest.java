package nl.davidlieffijn.battleofbots;

public class ProtocolTest {
	private static final int INITIAL = 0;
    private static final int WAITINGFORKEY = 1;
    private static final int WAITINGFORDATA = 2;
    private static final int PROCESSING = 3;
 
    private int state = INITIAL;
    private int incorrectKeys = 0;
 
    public String processInput(String input) {
        String output = "pause";
 
        if (state == INITIAL) {
        	output = "requestKey";
        	state = WAITINGFORKEY;
        } else if (state == WAITINGFORKEY) {
        	if (input.equals("Correct_Key")) {
        		output = "requestData";
        		state = WAITINGFORDATA;
        	} else {
        		output = "exit";
        	}
        } else if (state == WAITINGFORDATA) {
        	// Name is ok.
        	if (true) {
        		output = "matchFound";
        		state = PROCESSING;
        	} else {
        		output = "requestData";
        	}
        }
        return output;
    }
}
