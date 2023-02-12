package game;

import java.util.Arrays;

public class Board {
	public static final int EMPTY = 0;
	private static final int[] START_POSITIONS = { 0, 8, 16, 24 };
	private static final int[] GOAL_POSITIONS = { 31, 7, 15, 23 };

	private int[] boardFields;
	private Figure[][] figures;

	public Board() {
		boardFields = new int[32];
		Arrays.fill(boardFields, EMPTY);
		figures = new Figure[4][3];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				figures[i][j] = new Figure(j);
			}
		}
		boardFields[1] = BoardUtility.getBoardValue(1,3);
		figures[0][2].position = 1;
		figures[0][2].isHome = false;
		boardFields[2] = BoardUtility.getBoardValue(1,2);
		figures[0][1].position = 2;
		figures[0][1].isHome = false;
	}

	/**
	 * returns the boards current value at given position
	 * @param position the position
	 * @return the boards value at position
	 */
	public int getBoardAt(int position) {
		if (position < 0 || position >= boardFields.length) {
			throw new IllegalArgumentException("Invalid value for parameter position: " + position);
		}
		return boardFields[position];
	}

	/**
	 * returns the board position of figure from player
	 * @param player the player [1,4]
	 * @param figure the figure [1,3]
	 * @return the figures position
	 */
	public int getFigurePosition(int player, int figure) {
		if (player < 1 || player > 4) {
			throw new IllegalArgumentException("Invalid value for parameter player: " + player);
		}
		if (figure < 1 || figure > 3) {
			throw new IllegalArgumentException("Invalid value for parameter figure: " + figure);
		}
		return figures[player - 1][figure - 1].position;
	}

	/**
	 * returns whether or not the figure of player is at home
	 * @param player the player [1,4]
	 * @param figure the figure [1,3]
	 * @return whether or not the figure is at home
	 */
	public boolean isFigureAtHome(int player, int figure) {
		if (player < 1 || player > 4) {
			throw new IllegalArgumentException("Invalid value for parameter player: " + player);
		}
		if (figure < 1 || figure > 3) {
			throw new IllegalArgumentException("Invalid value for parameter figure: " + figure);
		}
		return figures[player - 1][figure - 1].isHome;
	}

	/**
	 * returns whether or not the figure of player reached to goal
	 * @param player the player [1,4]
	 * @param figure the figure [1,3]
	 * @return whether or not the figure is at home
	 */
	public boolean isFigureAtGoal(int player, int figure) {
		if (player < 1 || player > 4) {
			throw new IllegalArgumentException("Invalid value for parameter player: " + player);
		}
		if (figure < 1 || figure > 3) {
			throw new IllegalArgumentException("Invalid value for parameter figure: " + figure);
		}
		return figures[player - 1][figure - 1].reachedGoal;
	}

	/**
	 * returns players start position
	 * @param player the player [1,4]
	 * @return the players start position
	 */
	public static int getPlayerStartPosition(int player) {
		if (player <= 0 || player > 4) {
			throw new IllegalArgumentException("Invalid value for parameter player: " + player);
		}
		return START_POSITIONS[player - 1];
	}

	/**
	 * returns players goal position
	 * @param player the player [1,4]
	 * @return the players goal position
	 */
	public static int getPlayerGoalPosition(int player) {
		if (player <= 0 || player > 4) {
			throw new IllegalArgumentException("Invalid value for parameter player: " + player);
		}
		return GOAL_POSITIONS[player - 1];
	}

	public int[] getBoardFields() {
		return boardFields;
	}

	public void setBoardFields(int[] boardFields) {
		this.boardFields = boardFields;
	}

	public Figure[][] getFigures() {
		return figures;
	}

	public void setFigures(Figure[][] figures) {
		this.figures = figures;
	}

	/**
	 * private class to store information about a figure
	 */
	public static class Figure {
		boolean isHome;
		boolean reachedGoal;
		int position;

		Figure(int homePosition) {
			position = homePosition;
			isHome = true;
			reachedGoal = false;
		}

		public boolean isHome() {
			return isHome;
		}

		public void setHome(boolean home) {
			isHome = home;
		}

		public boolean isReachedGoal() {
			return reachedGoal;
		}

		public void setReachedGoal(boolean reachedGoal) {
			this.reachedGoal = reachedGoal;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}
	}
}
