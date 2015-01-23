package nl.davidlieffijn.battleofbots.interpreter;

public interface Expression {
	int result(int[] stats);
	String toString();
}
