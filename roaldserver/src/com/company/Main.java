package com.company;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        String codeA = "todo fill in";
        String codeB = "todo fill in";

        Parser parser = new Parser();
        Match match = new Match(parser.parse(codeA), parser.parse(codeB));
        Match.Result result = match.getResult();

        //Results...
        ArrayList<ArrayList<Bot.State>> states = result.getStates(); //One ArrayList for every bot: states.get(0) for bot 1, states.get(1) for bot 2
        byte[][] field = result.getField(); //Obstacles (not bots!)
        int winner = result.getWinnerId(); //-1 if no winner

        if (result.winnerExists())
            System.out.println("Match is over, the winner is BOT " + (winner + 1));
        else
            System.out.println("Match is over but nobody won!");

        //TODO: Send the results (winner, states and field) back to the client
        //TODO: Why not just serialize the result in its entirety? (or just JSON it, but make the 2d array a long string instead)
    }

}


