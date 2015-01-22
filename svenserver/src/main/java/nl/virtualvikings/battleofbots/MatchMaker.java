package nl.virtualvikings.battleofbots;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.sql.Wrapper;
import java.util.ArrayList;

import nl.virtualvikings.parser.Constant;
import nl.virtualvikings.parser.Parser;
import nl.virtualvikings.parser.UserVariable;
import org.json.JSONArray;
import org.json.JSONObject;

public class MatchMaker {
	ArrayList<Bot>freeBots = new ArrayList<Bot>();
	ArrayList<Match.Result>matches = new ArrayList<Match.Result>();
	ArrayList<Long>matchedThreads = new ArrayList<Long>();
	//private Match.Result result;

	ArrayList<String> names = new ArrayList<>();

	public int add(Bot bot){
		freeBots.add(bot);
		return freeBots.size();
	}
	
	public void remove(Bot bot){
		freeBots.remove(bot);
	}
	
	public boolean createMatch() throws Exception {
		if(freeBots.size() >= 2){

			ArrayList<UserVariable> variables = new ArrayList<UserVariable>();
			String[] userVariables = {"a", "b", "c", "d", "e"};
			for (int i = 0; i < userVariables.length; i++) {
				variables.add(new UserVariable(userVariables[i], new Constant(0)));
			}

			//Parser parser = new Parser(variables);
			//Every bot needs their own parser, or variables will be shared

			Match match = new Match(
					new Parser(variables).parse(freeBots.get(0).code),
					new Parser(variables).parse(freeBots.get(1).code));
			//TODO: abort if parse() throws exception

			//Een match wordt slechts door een MultiThread getriggered, dus moet de andere kunnen kijken of er al een match voor hem is gevonden.
			//Kan door in de matchedThreads list te zoeken naar een eigen threadId
			matchedThreads.add(freeBots.get(0).getConnectedThread());
			matchedThreads.add(freeBots.get(1).getConnectedThread());

			names.clear();
			for (Bot freeBot : freeBots)
				names.add(freeBot.Name());
			
			//Match wordt aangemaakt en vervolgens wordt twee keer het eerste element in de list verwijdert, zodat altijd freeBots.get(0) en freeBots.get(1) gebruikt kan worden
			matches.add(match.getResult());
			//TODO: bug: if you get an exception here because the list is empty, one of the clients doesn't receive the result
			freeBots.remove(0);
			freeBots.remove(0);

			return true;
		} else{
			return false;
		}
	}
	
	public String Field(int Matchindex){

		try {
			byte[][] field = matches.get(Matchindex).getField();

			ByteOutputStream bytes = new ByteOutputStream();
			DataOutputStream data = new DataOutputStream(bytes);

			data.writeByte(field.length); //Width = height!
			for (int i = 0; i < field.length; i++)
				for (int j = 0; j < field[0].length; j++) //[0] is not needed because width = height
					data.writeByte(field[i][j]);
			data.close();

			return bytes.toString() + "field";
			//TODO: als een obstakel waarde 10 heeft breekt alles (newline) dus gebruik waarden 0-9 en 11-127 (of negatief)
			//TODO: let op, Moves() wordt alleen aangeroepen als deze string "field" bevat
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String Moves(int Matchindex) {
		try {
			ArrayList<ArrayList<Robot.State>> states = matches.get(Matchindex).getStates();
			ArrayList<Robot.State> firstStates = states.get(0);

			//int botCount = states.size(); //Don't remove this, might be useful for later
			int stateCount = firstStates.size(); //States per bot (so total amount of states is botCount * stateCount)

			JSONArray bots = new JSONArray();

			//TODO: THIS IS REALLY IMPORTANT
			//There is no way yet to determine which bot is yours and which is the enemy's!
			//Think of a clever fix!
			//Maybe send their id in the ArrayList? So if you send id 1 to player 0, this player will have to do states.get(i) to get their own states
			//Something like this
				//JSONObject wrapper = new JSONObject();
				//wrapper.put("your_id", i); //But where do you get i from?
				//wrapper.put("moves", botMoves);

			//[
			//	{"name": "a","moves": []},
			//  {"name": "b", "moves": []}
			//]

			for (int i = 0; i < states.size(); i++) { //For every bot...

				ArrayList<Robot.State> botState = states.get(i);

				JSONObject botObj = new JSONObject();
				bots.put(botObj);

				String botName = names.get(i);
				botObj.put("name", botName);
				JSONArray currentBotStates = new JSONArray();
				botObj.put("moves", currentBotStates);

				for (int s = 0; s < stateCount; s++) { //For every timeslot...
					Robot.State currentState = botState.get(s);

					JSONObject obj = new JSONObject();
					currentBotStates.put(obj);

					obj.put("x", currentState.getPosition().x);
					obj.put("y", currentState.getPosition().y);
					obj.put("dir", currentState.getDirection());
					obj.put("hp", currentState.getHealth());
				}
			}

			if(matches.get(Matchindex).getStateCount >= 2)
				matches.remove(Matchindex);
			
			return "moves_start" + bots.toString(); //toString(void) adds no whitespace/newlines!

		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
