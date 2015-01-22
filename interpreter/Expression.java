package nl.davidlieffijn.battleofbots.interpreter;

public interface Expression {
	int result(byte[] stats);
	String toString();
}
