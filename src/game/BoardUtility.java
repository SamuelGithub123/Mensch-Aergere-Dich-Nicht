package game;

public class BoardUtility {
	// mask constants
	public static final int PLAYER_BASE = 1000;
	public static final int FIGURE_BASE = 100;

	private static final int HOME = 40;
	private static final int START = 50;
	private static final int GOAL = 60;

	private static final int BACKGROUND = -1;

	// color string constants
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String P1_COLOR = "\u001B[33m"; // yellow
	private static final String P2_COLOR = "\u001B[32m"; // green
	private static final String P3_COLOR = "\u001B[31m"; // red
	private static final String P4_COLOR = "\u001B[34m"; // blue
	private static final String BACKGROUND_COLOR = "\u001B[40m"; //black

	// field string constants
	private static final String HOME_S = "⌂";
	private static final String BACKGROUND_S = " ";
	private static final String GOAL_S = "x";
	private static final String FIELD_S = "o";
	private static final String START_S = "⊚";
	private static final String UNKNOWN_S = "?";

	private static final String FIGURE_1_S = "1";
	private static final String FIGURE_2_S = "2";
	private static final String FIGURE_3_S = "3";

	// fields 0...31
	private static final int[][] BOARD_MASK = {
			{ getPlayerHomeRepresentation(1) + 1 * FIGURE_BASE, getPlayerHomeRepresentation(1) + 2 * FIGURE_BASE,
					BACKGROUND, 6, 7, getPlayerStartRepresentation(2), BACKGROUND,
					getPlayerHomeRepresentation(2) + 1 * FIGURE_BASE,
					getPlayerHomeRepresentation(2) + 2 * FIGURE_BASE },
			{ getPlayerHomeRepresentation(1) + 3 * FIGURE_BASE, BACKGROUND, BACKGROUND, 5,
					getPlayerGoalRepresentation(2) + 1 * FIGURE_BASE, 9, BACKGROUND, BACKGROUND,
					getPlayerHomeRepresentation(2) + 3 * FIGURE_BASE },
			{ BACKGROUND, BACKGROUND, BACKGROUND, 4, getPlayerGoalRepresentation(2) + 2 * FIGURE_BASE, 10, BACKGROUND,
					BACKGROUND, BACKGROUND },
			{ getPlayerStartRepresentation(1), 1, 2, 3, getPlayerGoalRepresentation(2) + 3 * FIGURE_BASE, 11, 12, 13,
					14 },
			{ 31, getPlayerGoalRepresentation(1) + 1 * FIGURE_BASE, getPlayerGoalRepresentation(1) + 2 * FIGURE_BASE,
					getPlayerGoalRepresentation(1) + 3 * FIGURE_BASE, BACKGROUND,
					getPlayerGoalRepresentation(3) + 3 * FIGURE_BASE, getPlayerGoalRepresentation(3) + 2 * FIGURE_BASE,
					getPlayerGoalRepresentation(3) + 1 * FIGURE_BASE, 15 },
			{ 30, 29, 28, 27, getPlayerGoalRepresentation(4) + 3 * FIGURE_BASE, 19, 18, 17,
					getPlayerStartRepresentation(3) },
			{ BACKGROUND, BACKGROUND, BACKGROUND, 26, getPlayerGoalRepresentation(4) + 2 * FIGURE_BASE, 20, BACKGROUND,
					BACKGROUND, BACKGROUND },
			{ getPlayerHomeRepresentation(4) + 1 * FIGURE_BASE, BACKGROUND, BACKGROUND, 25,
					getPlayerGoalRepresentation(4) + 1 * FIGURE_BASE, 21, BACKGROUND, BACKGROUND,
					getPlayerHomeRepresentation(3) + 1 * FIGURE_BASE },
			{ getPlayerHomeRepresentation(4) + 2 * FIGURE_BASE, getPlayerHomeRepresentation(4) + 3 * FIGURE_BASE,
					BACKGROUND, getPlayerStartRepresentation(4), 23, 22, BACKGROUND,
					getPlayerHomeRepresentation(3) + 2 * FIGURE_BASE,
					getPlayerHomeRepresentation(3) + 3 * FIGURE_BASE } };

	private BoardUtility() {
		// do not instantiate utility class
	}

	private static int getPlayerHomeRepresentation(int player) {
		return PLAYER_BASE * player + HOME;
	}

	private static int getPlayerStartRepresentation(int player) {
		return PLAYER_BASE * player + START;
	}

	private static int getPlayerGoalRepresentation(int player) {
		return PLAYER_BASE * player + GOAL;
	}

	private static boolean isMaskHome(int maskValue) {
		return (maskValue / 10) % 10 == HOME / 10;
	}

	private static boolean isMaskPlayerStart(int maskValue) {
		return (maskValue / 10) % 10 == START / 10;
	}

	private static boolean isMaskBackground(int maskValue) {
		return maskValue == BACKGROUND;
	}

	private static boolean isMaskField(int maskValue) {
		return (0 <= maskValue && maskValue <= 31) || isMaskPlayerStart(maskValue);
	}

	private static int getFieldPositionFromMask(int maskValue) {
		if (0 <= maskValue && maskValue <= 31) {
			// normal field
			return maskValue;
		} else {
			// start field
			return (maskValue / PLAYER_BASE - 1) * 8;
		}
	}

	/**
	 * returns the 1-based player ID related to the given value (or 0, if non)
	 * @param maskOrBoardValue the mask or board value
	 * @return 1-based player ID (or 0)
	 */
	public static int getPlayerFromMaskOrBoard(int maskOrBoardValue) {
		return maskOrBoardValue / PLAYER_BASE;
	}

	/**
	 * return the 1-based figure ID related to the given value (or 0, if non)
	 * @param maskOrBoardValue
	 * @return 1-based figure ID (or 0)
	 */
	public static int getFigureFromMaskOrBoard(int maskOrBoardValue) {
		return (maskOrBoardValue / FIGURE_BASE) % 10;
	}

	/**
	 * return the board representation of the given figure for the given player
	 * @param player the player
	 * @param figure the figure
	 * @return the board representation
	 */
	public static int getBoardValue(int player, int figure) {
		return PLAYER_BASE * player + FIGURE_BASE * figure;
	}

	/**
	 * returns the string to represent the given maskValue
	 * @param maskValue the mask value to determine the string
	 * @param board the current board
	 * @param bw whether to print "black and white" (true) or use ANSI color codes (false)
	 * @return the string representation
	 */
	private static String getFieldString(int maskValue, Board board, boolean bw) {
		// background
		if (isMaskBackground(maskValue)) {
			return BACKGROUND_S;
		}

		// field
		if (isMaskField(maskValue)) {
			int boardValue = board.getBoardAt(getFieldPositionFromMask(maskValue));
			if (boardValue != Board.EMPTY) {
				// figure
				String color;
				switch (getPlayerFromMaskOrBoard(boardValue)) {
				case 1:
					color = bw ? "1" : P1_COLOR;
					break;
				case 2:
					color = bw ? "2" : P2_COLOR;
					break;
				case 3:
					color = bw ? "3" : P3_COLOR;
					break;
				case 4:
					color = bw ? "4" : P4_COLOR;
					break;
				default:
					color = bw ? "?" : "";
					break;
				}
				switch (getFigureFromMaskOrBoard(boardValue)) {
				case 1:
					return color + FIGURE_1_S;
				case 2:
					return color + FIGURE_2_S;
				case 3:
					return color + FIGURE_3_S;
				default:
					return color + UNKNOWN_S;
				}
			} else {
				// no figure
				if (isMaskPlayerStart(maskValue)) {
					// start
					switch (getPlayerFromMaskOrBoard(maskValue)) {
					case 1:
						return (bw ? "1" : P1_COLOR) + START_S;
					case 2:
						return (bw ? "2" : P2_COLOR) + START_S;
					case 3:
						return (bw ? "3" : P3_COLOR) + START_S;
					case 4:
						return (bw ? "4" : P4_COLOR) + START_S;
					default:
						return UNKNOWN_S;
					}
				} else {
					// normal field
					return (bw ? " " : "") + FIELD_S;
				}
			}
		}

		// special field: home or goal
		int player = getPlayerFromMaskOrBoard(maskValue);
		int figure = getFigureFromMaskOrBoard(maskValue);

		String color;
		switch (player) {
		case 1:
			color = bw ? "1" : P1_COLOR;
			break;
		case 2:
			color = bw ? "2" : P2_COLOR;
			break;
		case 3:
			color = bw ? "3" : P3_COLOR;
			break;
		case 4:
			color = bw ? "4" : P4_COLOR;
			break;
		default:
			color = bw ? "?" : "";
			break;
		}
		if (isMaskHome(maskValue)) {
			if (board.isFigureAtHome(player, figure)) {
				switch (figure) {
				case 1:
					return color + FIGURE_1_S;
				case 2:
					return color + FIGURE_2_S;
				case 3:
					return color + FIGURE_3_S;
				default:
					return color + UNKNOWN_S;
				}
			} else {
				return color + HOME_S;
			}
		} else {
			// is goal
			if (board.isFigureAtGoal(player, figure)) {
				switch (figure) {
				case 1:
					return color + FIGURE_1_S;
				case 2:
					return color + FIGURE_2_S;
				case 3:
					return color + FIGURE_3_S;
				default:
					return color + UNKNOWN_S;
				}
			} else {
				return color + GOAL_S;
			}
		}
	}

	/**
	 * prints the current board to the console using ANSI color codes
	 * @param board the current board
	 */
	public static void printBoard(Board board) {
		if (board == null) {
			throw new IllegalArgumentException("Cannot print board if board equals null!");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(BACKGROUND_COLOR).append("\t\t\t\t\t\n");
		for (int i = 0; i < BOARD_MASK.length; i++) {
			sb.append("\t");
			for (int j = 0; j < BOARD_MASK[i].length; j++) {
				int maskValue = BOARD_MASK[i][j];
				sb.append(getFieldString(maskValue, board, false)).append(ANSI_RESET).append(BACKGROUND_COLOR)
						.append(" ");
			}
			sb.append("\t\n");
		}
		sb.append("\t\t\t\t").append(ANSI_RESET);
		System.out.println(sb.toString());
	}

	/**
	 * prints the current board without ANSI color codes
	 * @param board
	 */
	public static void printBoardBW(Board board) {
		if (board == null) {
			throw new IllegalArgumentException("Cannot print board if board equals null!");
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < BOARD_MASK.length; i++) {
			for (int j = 0; j < BOARD_MASK[i].length; j++) {
				int maskValue = BOARD_MASK[i][j];
				sb.append(getFieldString(maskValue, board, true)).append("\t");
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
}
