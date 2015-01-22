package nl.davidlieffijn.battleofbots.interpreter;

public interface Statement {
	String result(byte[] stats);
	String toString();
}
