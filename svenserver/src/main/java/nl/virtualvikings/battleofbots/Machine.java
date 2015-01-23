package nl.virtualvikings.battleofbots;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Machine {

    private final Robot[] bots;
    private final byte[][] field;
    private final byte fieldSize;

    private final boolean printDebug = false;
    //private int time;

    public Machine(byte botCount, int time, int size) {

        fieldSize = (byte) size;
        int hp = 10;

        Random r = new Random();

        bots = new Robot[botCount];
        for (int i = 0; i < botCount; i++) {

            int x = 0;
            int y = 0;
            byte dir = 3; //Right

            if (i > 1) { //If more than 2 bots, put them a random position
                x = r.nextInt(fieldSize);
                y = r.nextInt(fieldSize);
                dir = (byte) r.nextInt(4);
            } else if (i == 1) {
                x = fieldSize - 1;
                y = fieldSize - 1;
                dir = 1; //Left
            }

            bots[i] = new Robot(new Point(x, y), dir, hp);
        }

        field = new byte[fieldSize][fieldSize];
        makeTestLevel();

    }

    private void makeTestLevel() {
        Random r = new Random();
        for (int y = 1; y < fieldSize - 1; y++)
            for (int x = 1; x < fieldSize - 1; x++)
                field[x][y] = r.nextDouble() > 0.7 ? (byte) r.nextInt(4) : 0;
        printStateForDebugging();
    }

    public boolean executeCommand(int botId, Command command) {
        //time++;
        return bots[botId].executeCommand(this, command);
    }

    public Robot getWinner() {

        int aliveCount = getAliveCount();
        if (aliveCount == 1) {
            Robot winner = null;

            for (Robot bot : bots)
                if (!bot.isDead())
                    winner = bot;

            if (winner == null)
                throw new NullPointerException("Invalid winner!");
            return winner;
        }

        return null;
    }

    public int getAliveCount() {
        int aliveCount = 0;

        for (Robot bot : bots)
            if (!bot.isDead())
                aliveCount++;

        return aliveCount;
    }

    public int getBotId(Robot bot) {
        for (int i = 0; i < bots.length; i++) {
            if (bot == bots[i])
                return i;
        }
        return -1;
    }

    public byte scanAhead(int botId, int x, int y) {
        return scanAhead(bots[botId], x, y);
    }

    private byte scanAhead(Robot current, int x, int y) { //Scan the cell in front of the bot, returns 0 if nothing, -1 if enemy and something else if obstacle
        Point scanPos = getLocalPoint(current, x, y);
        if (!insideBounds(scanPos)) return 1; //Outside bounds is always solid

        Robot scanBot = getBotAt(scanPos);

        boolean enemySeen = scanBot != null && scanBot != current && !scanBot.isDead();
        byte obstacle = field[scanPos.x][scanPos.y];

        if (enemySeen)
            return 100; //-1;
        else
            return obstacle; //Should be 0 if no obstacle
    }

    private Robot scanBot(Robot current) {
        Point scanPos = getPointInFrontOf(current);

        if (!insideBounds(scanPos)) return null;
        return getBotAt(scanPos);
   }

    private Point getPointInFrontOf(Robot current) {
        return getLocalPoint(current, 0, 1);
    }

    private Point getLocalPoint(Robot current, int x, int y) {
        Point currentPos = current.getPosition();
        Point scanPos = (Point) currentPos.clone();
        Point rotated = current.rotate(x, y, current.getDirection());
        scanPos.translate(rotated.x, rotated.y);
        return scanPos;
    }

    private Robot getBotAt(Point scanPos) {
        return getBotAt(scanPos.x, scanPos.y);
    }

    public boolean insideBounds(Point scanPos) {
        return insideBounds(scanPos.x, scanPos.y);
    }

    public boolean insideBounds(int x, int y) {
        return (x >= 0 && x < fieldSize && y >= 0 && y < fieldSize);
    }

    private Robot getEnemy(Robot bot) {
        for (Robot other : bots)
            if (other != bot)
                return other;
        return null;
    }

    public Robot.State copyState(int botId) throws CloneNotSupportedException {
        return bots[botId].copyState();
    }

    public void printStateForDebugging() {

        if (!printDebug) return;

        for (int y = 0; y < fieldSize; y++) {
            for (int x = 0; x < fieldSize; x++) {

                byte value = field[x][y];
                String character = ".";

                Robot b = getBotAt(x, y);
                if (b != null) {
                    if (!b.isDead())
                        switch (b.getDirection()) {
                            case 0: character = "v"; break;
                            case 1: character = "<"; break;
                            case 2: character = "^"; break;
                            case 3: character = ">"; break;
                        }
                    else
                        character = "X"; //rip
                }

                if (value > 0) character = String.valueOf(value);
                System.out.print(character + " ");

                if (x == fieldSize - 1)
                    System.out.println();
            }
        }
    }

    private Robot getBotAt(int x, int y) {

        ArrayList<Robot> candidates = new ArrayList<Robot>();

        for (Robot bot : bots) {
            Point pos = bot.getPosition();
            if (pos.x == x && pos.y == y)
                candidates.add(bot);
        }

        if (candidates.isEmpty()) return null; //No bots at this position

        int aliveCount = 0;
        for (Robot candidate : candidates) {
            if (!candidate.isDead())
                aliveCount++;
        }

        if (aliveCount == 0) //Every bot on this position is dead, so get the first one
            return candidates.get(0);
        else
            for (Robot candidate : candidates) { //Not all bots are dead, so get the first alive one
                if (!candidate.isDead())
                    return candidate;
            }

        return null;
    }

    public Robot getBotById(int id) {
        return bots[id];
    }

    public byte[][] getField() {
        return field;
    }

    public static class Command {

        static enum Type {
            None, Move, Turn, Attack, SetVariable //Last one is doubtful because these variables are not stored in the bot itself, right?
            //Also, commands can only take ints, while SetVariable would need a string to name the variable...
            //UNLESS you use registers for variables (so 0="A", 1="B", 25="Z", etc)
        }

        public final Type type;
        public final int[] arguments; //Any command takes only a list of integers or nothing (no strings)
        private boolean executed;

        public Command(Type toUse, int... args) {
            arguments = args;
            type = toUse;
        }

        public boolean execute(Robot bot, Machine vm) {

            if (executed) {
                System.out.println("Warning - same command cannot be executed twice!");
                return false;
            }
            executed = true;

            int x = bot.getPosition().x;
            int y = bot.getPosition().y;

            switch (type) {

                case Attack:

                    boolean attacked = bot.attack();
                    if (attacked) {
                        Robot enemy = vm.scanBot(bot);
                        if (enemy != null && !enemy.isDead()) {
                            enemy.hurt(1);
                            //if (enemy.isDead())
                            //    System.out.println("Bot attacked the other bot and killed it!");
                            //else
                            //    System.out.println("Bot attacked the other bot!");
                            return true;
                        }
                        else {
                            //System.out.println("Bot tried to attack but missed!"); //Bot also misses if the enemy is already dead
                            return false;
                        }
                    }
                    return attacked; //Always false

                case Turn:

                    int turnOffset = arguments[0];
                    if (Math.abs(turnOffset) > 1) {
                        //System.out.println("Bot tried to turn too much");
                        return false;
                    }

                    if (turnOffset == 0) {
                        //System.out.println("Bot didn't turn at all");
                        return false;
                    }

                    return bot.turn(turnOffset);

                case Move:

                    //A bot can only move forward and backward, not left and right
                    //So you only need 1 argument (1 = forwards, -1 = backwards)
                    Point rotated = bot.rotate(0/*arguments[0]*/, arguments[0], bot.getDirection());

                    int xOffset = rotated.x;
                    int yOffset = rotated.y;

                    int newX = x + xOffset;
                    int newY = y + yOffset;

                    //TODo: If the bot is stuck in an obstacle you should remove the obstacle instead...

                    if (xOffset == 0 && yOffset == 0) {
                        //System.out.println("Bot didn't move at all");
                        return false;
                    }

                    if (Math.abs(xOffset) + Math.abs(yOffset) > 1) {
                        //System.out.println("Bot tried to move too much");
                        return false;
                    }

                    if (!vm.insideBounds(newX, newY)) {
                        //System.out.println("Bot tried to move out of bounds");
                        return false;
                    }

                    if (vm.field[newX][newY] != 0) {
                        //System.out.println("Bot tried to move into an obstacle");
                        return false;
                    }

                    Robot botAt = vm.getBotAt(newX, newY);
                    if (botAt != null) {
                        if (!botAt.isDead()) {
                            //System.out.println("Bot tried to move into another bot!");
                            return false;
                        }
                        //else
                        //    System.out.println("Bot stepped on the corpse of another bot");
                    }

                    return bot.move(xOffset, yOffset);

                default:
                    System.out.println("Bot did nothing");
                    return false;
            }
        }
    }


}
