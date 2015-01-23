package nl.davidlieffijn.battleofbots.interpreter;

public interface Statement {
	String result(int[] stats);
	String toString();
}
