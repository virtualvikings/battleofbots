package nl.virtualvikings.battleofbots;


import nl.virtualvikings.parser.Parser;
import nl.virtualvikings.parser.Statement;

import java.util.ArrayList;
import java.util.Random;

public class Match {

    private final Machine vm;
    private final byte botCount;
    private final int timeLimit;
    private boolean matchOver;

    private final Statement[] codes;

    public Match(Statement... codes) {

        botCount = 2;
        timeLimit = 2000;
        vm = new Machine(botCount, timeLimit, 12); //TODO: should be 20

        this.codes = codes;
    }

    private void copyAllStates(ArrayList<ArrayList<Robot.State>> states) throws CloneNotSupportedException {
        for (int i = 0; i < botCount; i++)
            states.get(i).add(vm.copyState(i)); //TODO: this is right, right?
    }

    Random r = new Random();

    public Result getResult() {

        if (matchOver)
            throw new IllegalStateException("Match cannot be started twice");

        int turns = 0;
        ArrayList<ArrayList<Robot.State>> states = new ArrayList<ArrayList<Robot.State>>(botCount);
        for (int i = 0; i < botCount; i++)
            states.add(new ArrayList<Robot.State>()); //Every bot has their own state list

        int winnerId = -1;

        try {
            copyAllStates(states); //First make a copy of all bots starting state
            while (true) {
                //System.out.println("----TURN " + (turns+1) + "----");

                for (int i = 0; i < botCount; i++) {

                    //System.out.print("[BOT " + (i + 1) + "]");

                    Robot bot = vm.getBotById(i);
                    int[] stats = new int[] {
                            bot.getDirection(),
                            bot.getHealth(),
                            bot.getPosition().x,
                            bot.getPosition().y,
                            turns,
                            vm.scanAhead(i, 1, 0), //left
                            vm.scanAhead(i, 0, 1), //front
                            vm.scanAhead(i, -1, 0), //right
                    };

                    //left/right is reversed, apparently

                    String result = codes[i].result(stats);
                    Machine.Command command;

                    if (result == null)
                        command = new Machine.Command(Machine.Command.Type.None);
                    else
                        switch(result) {
                            case "TurnLeft": command = new Machine.Command(Machine.Command.Type.Turn, -1); break;
                            case "TurnRight": command = new Machine.Command(Machine.Command.Type.Turn, 1); break;
                            case "GoForward": command = new Machine.Command(Machine.Command.Type.Move, 1); break;
                            case "GoBackward": command = new Machine.Command(Machine.Command.Type.Move, -1); break;
                            case "Attack": command = new Machine.Command(Machine.Command.Type.Attack); break;
                            default: throw new Exception("Unknown command " + result);
                        }

                    if (!vm.executeCommand(i, command))
                        ;//System.out.println("Failed to do anything useful");
                    else
                        vm.printStateForDebugging();
                }

                turns++;
                copyAllStates(states);
                //System.out.println();

                if (vm.getAliveCount() == 0) //Everyone is dead
                    break;

                if (turns >= timeLimit) {
                    System.out.println("Turn limit reached!");
                    break;
                }

                Robot winner = vm.getWinner();
                if (winner != null) {
                    winnerId = vm.getBotId(winner);
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //TODO: in the client you'll need to merge the lists of states together, interleaving them
        //So iterate for(... i < time/botcount; ...) first, then nest for(... i < botcount; ...)
        //there is no time variable here, so just get the length of the first arraylist in the arraylist-arraylist

        //TODO: how about this, instead of keeping arraylists of arraylists, simply have 1 big list.
        //Now the list will be size time*botcount, and the State class will need to be changed so that it also holds a botid.
        //then vm.CopyState(botid) would set botid in the newly copied state

        //TODO: State is not serializable yet, but ArrayLists are. Either make it serializable or make your own string representation (json?)

        matchOver = true;
        byte[][] field = vm.getField();

        return new Result(winnerId, states, field);
    }

    public static class Result {

        private final int winnerId;
        public int getStateCount = 0;
        private final ArrayList<ArrayList<Robot.State>> states;
        private final byte[][] field;

        public int getWinnerId() {
            return winnerId;
        }

        public Result(int winnerId, ArrayList<ArrayList<Robot.State>> states, byte[][] field) {
            this.winnerId = winnerId;
            this.states = states;
            this.field = field;
        }

        public boolean winnerExists() {
            return winnerId > -1; //-1 and lower are invalid winner ids
        }

        public ArrayList<ArrayList<Robot.State>> getStates() {
        	getStateCount++;
            return states;
        }

        public byte[][] getField() {
            return field;
        }
    }
}
