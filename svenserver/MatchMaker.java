package nl.virtualvikings.battleofbots;

import java.util.ArrayList;

public class MatchMaker {
	ArrayList<Bot>freeBots = new ArrayList<Bot>();
	ArrayList<PlayThrough>matches = new ArrayList<PlayThrough>();
	ArrayList<Long>matchedThreads = new ArrayList<Long>();
	
	public int add(Bot bot){
		freeBots.add(bot);
		return freeBots.size();
	}
	
	public void remove(Bot bot){
		freeBots.remove(bot);
	}
	
	public boolean createMatch(){
		if(freeBots.size() >= 2){
			PlayThrough playThrough = new PlayThrough(freeBots.get(0), freeBots.get(1));
			
			//Een match wordt slechts door een MultiThread getriggered, dus moet de andere kunnen kijken of er al een match voor hem is gevonden.
			//Kan door in de matchedThreads list te zoeken naar een eigen threadId
			matchedThreads.add(freeBots.get(0).getConnectedThread());
			matchedThreads.add(freeBots.get(1).getConnectedThread());
			
			//Match wordt aangemaakt en vervolgens wordt twee keer het eerste element in de list verwijdert, zodat altijd freeBots.get(0) en freeBots.get(1) gebruikt kan worden
			matches.add(playThrough);
			freeBots.remove(0);
			freeBots.remove(0);
			return true;
		} else{
			return false;
		}
	}
	
	public String Field(int Index){	
		//Wordt slechts een keer opgevraagd aan het begin van het spel.
		return matches.get(Index).Field();	
	}
	
	public String Moves(int Index){	
		//Dit is het laatste wat kan worden opgevraagd,
		//TODO: Zorg ervoor dat de playthrough na de return verwijdert wordt....
		return matches.get(Index).Moves();	
	}
}
