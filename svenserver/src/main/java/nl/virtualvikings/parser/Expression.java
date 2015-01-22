package nl.virtualvikings.parser;

public interface Expression {
	int result(byte[] stats);
	String toString();
}
