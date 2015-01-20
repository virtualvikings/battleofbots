package nl.virtualvikings.battleofbots;

import java.awt.*;

public class Robot {

    private final State state;

    public boolean isDead() {
        return getHealth() <= 0;
    }

    public int getHealth() {
        return state.getHealth();
    }

    public Point getPosition() {
        return state.getPosition();
    }

    public void hurt(int damage) {
        state.health -= damage;
    }

    public byte getDirection() {
        return state.getDirection();
    }

    public State copyState() throws CloneNotSupportedException {
        return (State) state.clone();
    }

    public static class State implements Cloneable {
        private Point position;
        private byte direction;
        private int health;

        public byte getDirection() {
            return direction;
        }

        public Point getPosition() {
            return position;
        }

        public int getHealth() {
            return health;
        }

        public State(Point point, byte dir, int hp) {
            position = point;
            direction = dir; //0 is down, 3 is to the right, 2 is up, 1 is left
            health = hp;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            super.clone(); //Not a deep clone, won't clone position!
            return new State((Point) position.clone(), direction, health);
        }
    }

    public Robot(Point point, byte dir, int hp) {
        state = new State(point, dir, hp);
    }

    public boolean executeCommand(Machine vm, Machine.Command command) {

        System.out.println(" HP = " + getHealth());
        if (isDead()) {
            System.out.println("Bot is dead and cannot execute any commands!");
            return false;
        }

        return command.execute(this, vm); //Makes Command call move/attack
    }

    public boolean move(int xOffset, int yOffset) {
        //gets passed absolute movement
        state.position.translate(xOffset, yOffset);
        System.out.println("Bot moved to x=" + state.position.x + ", y=" + state.position.y);

        return true; //TODO return false if out of fuel or something
    }

    public Point rotate(int xOffset, int yOffset, byte rotation) {

        if (rotation < 0)
            throw new IllegalArgumentException("Rotation is negative!");

        while (rotation > 0) {
            int tempX = xOffset;
            int tempY = yOffset;
            xOffset = -tempY;
            yOffset = tempX;
            rotation--;
        }

        return new Point(xOffset, yOffset);
    }

    public boolean attack() {
        return true; //TODO return false if we can't attack somehow (no ammo or something)
    }

    public boolean turn(int turnOffset) {

        byte direction = state.direction;
        direction += turnOffset;

        byte maxRotation = 4;
        while (direction < 0)
            direction += maxRotation; //Wrap left
        direction %= maxRotation; //Wrap right

        state.direction = direction;

        String dirStr = "";
        switch (direction) {
            case 0: dirStr = "Down"; break;
            case 1: dirStr = "Left"; break;
            case 2: dirStr = "Up"; break;
            case 3: dirStr = "Right"; break;
        }
        System.out.println("Bot rotated to " + dirStr);

        return true; //TODO return false if out of fuel or something
    }
}
