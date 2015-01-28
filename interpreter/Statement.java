package nl.davidlieffijn.battleofbots.interpreter;

public interface Statement {
	/**
	 * 
	 * @param stats array of the bot's current stats, in order:
	 * <ul>
	 * <li>DIRECTION</li>
	 * <li>DIRECTION_E</li>
	 * <li>HP</li>
	 * <li>RANDOM</li>
	 * <li>TURNS</li>
	 * <li>X</li>
	 * <li>Y</li>
	 * <li>VIEW_L</li>
	 * <li>VIEW_LF</li>
	 * <li>VIEW_F</li>
	 * <li>VIEW_RF</li>
	 * <li>VIEW_R</li>
	 * </ul>
	 * @return the resulting action
	 */
	String result(int[] stats);
	String toString();
}
