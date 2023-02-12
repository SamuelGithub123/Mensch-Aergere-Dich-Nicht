package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This is the class to configure and start a game
 * 
 * You may add public and private attributes and/or methods if needed.
 *
 * See task description for all explanations and restrictions!
 */
public class PinguGame {
	private final boolean renderBW;
	private final Random random;
	private final BufferedReader reader;
	private final int renderDelay;

	private Board board;

	// my attributes ******************************************************************
	private Pingu pingu1;
	private Pingu pingu2;
	private Pingu pingu3;
	private Pingu pingu4;

	static int rollDiceCount = 0;


	public PinguGame() {
		this(false);
	}

	public PinguGame(boolean renderBW) {
		this(renderBW, Long.MIN_VALUE, 500);
	}

	public PinguGame(boolean renderBW, long randomSeed, int renderDelay) {
		this.renderBW = renderBW;
		if (randomSeed == Long.MIN_VALUE) {
			random = new Random();
		} else {
			random = new Random(randomSeed);
		}
		reader = new BufferedReader(new InputStreamReader(System.in));
		this.renderDelay = renderDelay;
		board = new Board();
	}

	public void play() {

		// Willkommen zum Spiel
		printString(1);  // Willkommen zu "Pingu ärgere dich nicht"!
		printString(2);   // Wie viele Pinguine wollen spielen? Bitte eine Zahl von 0 bis 4 eingeben!

		int numberOfPlayers = readInt();

		while (numberOfPlayers < 0 || numberOfPlayers > 4) {

			numberOfPlayers = readInt();
		}

		printString(3, numberOfPlayers);  // Starte Spiel mit 4 "echten" und 0 KI Pinguinen


		if (numberOfPlayers == 0) {
			pingu1 = new Pingu(true, 1);
			pingu2 = new Pingu(true, 2);
			pingu3 = new Pingu(true, 3);
			pingu4 = new Pingu(true, 4);
		}
		if (numberOfPlayers == 1) {
			pingu1 = new Pingu(false, 1);
			pingu2 = new Pingu(true, 2);
			pingu3 = new Pingu(true, 3);
			pingu4 = new Pingu(true, 4);
		}
		if (numberOfPlayers == 2) {
			pingu1 = new Pingu(false, 1);
			pingu2 = new Pingu(false, 2);
			pingu3 = new Pingu(true, 3);
			pingu4 = new Pingu(true, 4);
		}
		if (numberOfPlayers == 3) {
			pingu1 = new Pingu(false, 1);
			pingu2 = new Pingu(false, 2);
			pingu3 = new Pingu(false, 3);
			pingu4 = new Pingu(true, 4);
		}
		if (numberOfPlayers == 4) {
			pingu1 = new Pingu(false, 1);
			pingu2 = new Pingu(false, 2);
			pingu3 = new Pingu(false, 3);
			pingu4 = new Pingu(false, 4);

		}

		Pingu player = pingu1;
		boolean again6 = false;
		boolean goal6 = false;

		render();

		// Spiel beginnt *********************************************************************************

		while (true) {

			boolean allFiguresAtHome = true;
			List<Integer> figuresAtHome = new ArrayList<>();
			boolean has6 = false;
			Board.Figure[][] figures = board.getFigures();

			if (!again6) {
				printString(4, player.getId());  // Pinguin ist am Zug
			}

			// check whether all figures are at home
			for (int i = 1; i < 4; i++) {
				if (!board.isFigureAtHome(player.getId(), i) && !figures[player.getId() - 1][i - 1].isReachedGoal()) {
					allFiguresAtHome = false;
				}
				else {
					if (!figures[player.getId() - 1][i - 1].isReachedGoal()) {
						figuresAtHome.add(i);
					}
				}
			}

			// all figures are at home *******************************************************************************

			if (allFiguresAtHome) {
				if (goal6) {
					int number = rollDice();
					if (number != 6) {
						printString(5, player.getId(), number); // Pinguin hat eine Zahl gewürfelt
						printString(11, player.getId(), number); // Keine Figur kann mit einer Zahl bewegt werden
						if (player == pingu1) {
							player = pingu2;
						} else if (player == pingu2) {
							player = pingu3;
						} else if (player == pingu3) {
							player = pingu4;
						} else if (player == pingu4) {
							player = pingu1;
						}
						render();
						goal6 = false;
						again6 = false;
						continue;
					}
					else {
						printString(5, player.getId(), number);   // Pinguin hat eine Zahl gewürfelt
						if (figuresAtHome.size() == 3) {
							int firstFigure = figuresAtHome.get(0);
							int secondFigure = figuresAtHome.get(1);
							int thirdFigure = figuresAtHome.get(2);
							printString(10, firstFigure, secondFigure, thirdFigure);
						}
						else if (figuresAtHome.size() == 2) {
							int firstFigure = figuresAtHome.get(0);
							int secondFigure = figuresAtHome.get(1);
							printString(10, firstFigure, secondFigure);
						}
						else if (figuresAtHome.size() == 1) {
							int firstFigure = figuresAtHome.get(0);
							printString(10, firstFigure);
						}


						// place figure in startPosition
						int figureID;

						if (!player.isKI()) {
							figureID = readInt();
							while (!figuresAtHome.contains(figureID)) {

								figureID = readInt();
							}
						}
						else {
							figureID = figuresAtHome.get(0);
							printString(6, figureID);

						}

						int startPosition = Board.getPlayerStartPosition(player.getId());

						figures = board.getFigures();

						int[] boardCurrent = board.getBoardFields();
						boardCurrent[startPosition % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
						figures[player.getId() - 1][figureID - 1].setPosition(startPosition % 32);


						figures[player.getId() - 1][figureID -1].setHome(false);

						// Schmeißen einer Figur beim Verlassen des Hauses
						schmeissenHausB(player, startPosition % 32, figures);

						again6 = false;
						goal6 = false;
						render();

						// nochmal Würfeln
						printString(16, player.getId()); // Pinguin muss das Startfeld räumen
						number = rollDice();
						printString(5, player.getId(), number); // Pinguin hat eine Zahl gewürfelt

						boardCurrent = board.getBoardFields();

						boardCurrent[(startPosition + number) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
						figures[player.getId() - 1][figureID - 1].setPosition((startPosition + number) % 32);

						boardCurrent[startPosition % 32] = Board.EMPTY;

						// Schmeissen einer Figur nach Verlassen des Hauses
						schmeissenHaus(player, (startPosition + number) % 32, figures);

						render();

						if (player == pingu1) {
							player = pingu2;
						}

						else if (player == pingu2) {
							player = pingu3;
						}

						else if (player == pingu3) {
							player = pingu4;
						}

						else if (player == pingu4) {
							player = pingu1;
						}

						continue;
					}
				}

				printString(12, player.getId()); // Pinguin hat keine Figur auf dem Feld

				//dreimal würfeln
				for (int i = 0; i < 3; i++) {
					int number = rollDice();
					printString(5, player.getId(), number); // Pinguin hat eine Zahl gewürfelt

					// 6 gewürfelt
					if (number == 6) {

						has6 = true;

						if (figuresAtHome.size() == 3) {
							int firstFigure = figuresAtHome.get(0);
							int secondFigure = figuresAtHome.get(1);
							int thirdFigure = figuresAtHome.get(2);
							printString(14, firstFigure, secondFigure, thirdFigure); // Welche Figur möchtest du aufs Spielfeld ziehen?
						}
						else if (figuresAtHome.size() == 2) {
							int firstFigure = figuresAtHome.get(0);
							int secondFigure = figuresAtHome.get(1);
							printString(14, firstFigure, secondFigure); // Welche Figur möchtest du aufs Spielfeld ziehen?
						}
						else if (figuresAtHome.size() == 1) {
							int firstFigure = figuresAtHome.get(0);
							printString(14, firstFigure); // Welche Figur möchtest du aufs Spielfeld ziehen?
						}


						// place figure in startPosition
						int figureID;

						if (!player.isKI()) {
							figureID = readInt();
							while (!figuresAtHome.contains(figureID)) {

								figureID = readInt();
							}
						}
						else {
							figureID = figuresAtHome.get(0);
							printString(6, figureID);

						}

						int startPosition = Board.getPlayerStartPosition(player.getId());

						figures = board.getFigures();

						int[] boardCurrent = board.getBoardFields();
						boardCurrent[startPosition % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
						figures[player.getId() - 1][figureID - 1].setPosition(startPosition % 32);


						figures[player.getId() - 1][figureID -1].setHome(false);

						// Schmeißen einer Figur beim Verlassen des Hauses
						schmeissenHausB(player, startPosition % 32, figures);

						render();

						// nochmal Würfeln
						printString(16, player.getId()); // Pinguin muss das Startfeld räumen
						number = rollDice();
						printString(5, player.getId(), number); // Pinguin hat eine Zahl gewürfelt

						boardCurrent = board.getBoardFields();

						boardCurrent[(startPosition + number) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
						figures[player.getId() - 1][figureID - 1].setPosition((startPosition + number) % 32);

						boardCurrent[startPosition % 32] = Board.EMPTY;

						// Schmeissen einer Figur nach Verlassen des Hauses
						schmeissenHaus(player, (startPosition + number) % 32, figures);

						again6 = false;

						render();

						break;
					}
				}

				// keine 6 gewürfelt
				if (!has6) {
					printString(13); // Schade, keine 6. Mehr Glück nächste Runde!
					render();
				}
			}


			// nicht alle Figuren im Home ********************************************************************

			else {
				int number = rollDice();
				printString(5, player.getId(), number);   // Pinguin hat eine Zahl gewürfelt
				boolean hasPlayed = false;
				figures = board.getFigures();
				int[] boardCurrent = board.getBoardFields();


				// Figur erreicht gleich das Zielfeld
				for (int i = 1; i < 4; i++) {
					if (!figures[player.getId() - 1][i - 1].isReachedGoal()) {
						int currentPosition = board.getFigurePosition(player.getId(), i);
						// System.out.println(currentPosition);
						if (hasReachGoal(player, i, number, figures)) {
							printString(7, i);  // Eine der folgenden Figuren kann das Ziel erreichen
							if (!player.isKI()) {
								int figureID = readInt();
								while (figureID != i) {

									figureID = readInt();
								}
							} else {
								int figureID = i;
								printString(6, figureID);
							}

							figures = board.getFigures();
							figures[player.getId() - 1][i - 1].setReachedGoal(true);
							boardCurrent[currentPosition % 32] = Board.EMPTY;

							// Beim Erreichen des Ziels Figur schmeissen
							schmeissenGoal(player, Board.getPlayerGoalPosition(player.getId()), figures, boardCurrent);

							hasPlayed = true;
							render();
							if (number == 6) {
								goal6 = true;
								again6 = true;
							}
							else {
								again6 = false;
							}

							break;
						}
					}
				}

				// Schmeissen
				boolean result;
				List<Integer> schmeissenList = new ArrayList<>();
				List<Integer> figuresAtHome2 = new ArrayList<>();

				for (int i = 1; i < 4; i++) {
					if (!figures[player.getId() - 1][i - 1].isReachedGoal()) {
						result = schmeissenHausBPruefen(player, Board.getPlayerStartPosition(player.getId()), figures);
						if (result && board.isFigureAtHome(player.getId(), i) && number == 6) {
							figuresAtHome2.add(i);
						}
						if (schmeissenHausPruefen(player, (board.getFigurePosition(player.getId(), i) + number) % 32, figures, i, new ArrayList<>())) {
							schmeissenList.add(i);
						}
					}

				}

				if (!figuresAtHome2.isEmpty() && schmeissenHausBPruefen(player, Board.getPlayerStartPosition(player.getId()), figures) || !schmeissenList.isEmpty()) {
					int figureID;

					int firstFigure;
					int secondFigure;
					int firstFigure2;
					int secondFigure2;
					//System.out.println(schmeissenList.get(0));
					//System.out.println(figuresAtHome2.size());
					//System.out.println(schmeissenList.size());

					// drei
					if (figuresAtHome2.size() + schmeissenList.size() == 3) {
						printString(9, 1, 2, 3);
					}

					// zwei Figuren im Home
					if (figuresAtHome2.size() == 2 && schmeissenList.size() == 0) {
						firstFigure = figuresAtHome.get(0);
						secondFigure = figuresAtHome.get(1);
						printString(9, firstFigure, secondFigure);
					}

					// eine Figur im Home
					if (figuresAtHome2.size() == 1 && schmeissenList.size() == 0) {
						firstFigure = figuresAtHome.get(0);
						printString(9, firstFigure);
					}

					// Schmeissen zwei Figuren
					if (figuresAtHome2.size() == 0 && schmeissenList.size() == 2) {
						firstFigure2 = schmeissenList.get(0);
						secondFigure2 = schmeissenList.get(1);
						printString(9, firstFigure2, secondFigure2);
					}

					// Schmeissen eine Figur
					if (figuresAtHome2.size() == 0 && schmeissenList.size() == 1) {
						firstFigure2 = schmeissenList.get(0);
						printString(9, firstFigure2);
					}

					if (!player.isKI()) {
						figureID = readInt();
						while (!figuresAtHome2.contains(figureID) && !schmeissenList.contains(figureID)) {

							figureID = readInt();
						}
					} else {
						if (!figuresAtHome2.isEmpty()) {
							figureID = figuresAtHome2.get(0);
							printString(6, figureID);
						}
						else {
							figureID = schmeissenList.get(0);
							printString(6, figureID);
						}
					}

					int startPosition = Board.getPlayerStartPosition(player.getId());
					figures = board.getFigures();
					boardCurrent = board.getBoardFields();

					if (figures[player.getId() - 1][figureID - 1].isHome()) {
						schmeissenHausB(player, startPosition, figures);
						boardCurrent[startPosition % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
						figures[player.getId() - 1][figureID - 1].setPosition(startPosition % 32);
						figures[player.getId() - 1][figureID - 1].setHome(false);

						// nochmal Würfeln
						render();
						printString(16, player.getId()); // Pinguin muss das Startfeld räumen

						number = rollDice();
						printString(5, player.getId(), number); // Pinguin hat eine Zahl gewürfelt
						boardCurrent = board.getBoardFields();

						boardCurrent[(startPosition + number) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
						figures[player.getId() - 1][figureID - 1].setPosition((startPosition + number) % 32);

						boardCurrent[startPosition % 32] = Board.EMPTY;

						// Schmeissen einer Figur nach Verlassen des Hauses
						schmeissenHaus(player, (startPosition + number) % 32, figures);
					}

					else {
						int currentPosition = board.getFigurePosition(player.getId(), figureID);
						schmeissenHaus(player, (currentPosition + number) % 32, figures);
						boardCurrent = board.getBoardFields();
						boardCurrent[currentPosition % 32] = Board.EMPTY;
						boardCurrent[(currentPosition + number) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
						figures[player.getId() - 1][figureID - 1].setPosition((currentPosition + number) % 32);

					}
					render();
					again6 = false;

					hasPlayed = true;
				}

				if (!hasPlayed) {

					List<Integer> hasReachedGoal = new ArrayList<>();

					for (int i = 1; i < 4; i++) {
						if (figures[player.getId() - 1][i - 1].isReachedGoal()) {
							hasReachedGoal.add(i);
						}
					}


					// 6 gewürfelt
					if (number == 6) {

						int figureID = 0;

						// bereits im Ziel erreichte Figur nicht mehr verfügbar

						if (!hasReachedGoal.contains(1) && !hasReachedGoal.contains(2) && !hasReachedGoal.contains(3)) {
							printString(10, 1, 2, 3); // Eine der folgenden Figuren kann bewegt werden

							if (!player.isKI()) {
								figureID = readInt();
								while (figureID != 1 && figureID != 2 && figureID != 3) {

									figureID = readInt();
								}
							}
							else {
								figureID = 1;
								printString(6, 1);
							}
						}

						else if (!hasReachedGoal.contains(1) && !hasReachedGoal.contains(2)) {
							printString(10, 1, 2); // Eine der folgenden Figuren kann bewegt werden

							if (!player.isKI()) {
								figureID = readInt();
								while (figureID != 1 && figureID != 2) {

									figureID = readInt();
								}
							}
							else {
								figureID = 1;
								printString(6, 1);
							}
						}

						else if (!hasReachedGoal.contains(1) && !hasReachedGoal.contains(3)) {
							printString(10, 1, 3); // Eine der folgenden Figuren kann bewegt werden
							if (!player.isKI()) {
								figureID = readInt();
								while (figureID != 1 && figureID != 3) {

									figureID = readInt();
								}
							}
							else {
								figureID = 1;
								printString(6, 1);
							}
						}
						else if (!hasReachedGoal.contains(2) && !hasReachedGoal.contains(3)) {
							printString(10, 2, 3);
							if (!player.isKI()) {
								figureID = readInt();
								while (figureID != 2 && figureID != 3) {

									figureID = readInt();
								}
							}
							else {
								figureID = 2;
								printString(6, 2);
							}
						}

						else if (!hasReachedGoal.contains(1)) {
							printString(10, 1);

							if (!player.isKI()) {
								figureID = readInt();
								while (figureID != 1) {

									figureID = readInt();
								}
							}
							else {
								figureID = 1;
								printString(6, 1);
							}
						}

						else if (!hasReachedGoal.contains(2)) {
							printString(10, 2);

							if (!player.isKI()) {
								figureID = readInt();
								while (figureID != 2) {

									figureID = readInt();
								}
							}
							else {
								figureID = 2;
								printString(6, 2);
							}
						}

						else if (!hasReachedGoal.contains(3)) {
							printString(10, 3);

							if (!player.isKI()) {
								figureID = readInt();
								while (figureID != 3) {

									figureID = readInt();
								}
							}
							else {
								figureID = 3;
								printString(6, 3);
							}
						}


						// figure is at home ************************************************************************

						if (board.isFigureAtHome(player.getId(), figureID)) {
							int startPosition = Board.getPlayerStartPosition(player.getId());
							boardCurrent = board.getBoardFields();
							boardCurrent[startPosition % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
							figures[player.getId() - 1][figureID - 1].setPosition(startPosition % 32);
							figures = board.getFigures();

							figures[player.getId() - 1][figureID -1].setHome(false);


							// Schmeißen einer Figur beim Verlassen des Hauses
							schmeissenHausB(player, startPosition % 32, figures);

							render();

							// nochmal Würfeln
							printString(16, player.getId()); // Pinguin muss das Startfeld räumen
							number = rollDice();
							printString(5, player.getId(), number); // Pinguin hat eine Zahl gewürfelt

							schmeissenList = new ArrayList<>();

							// Feld belegt
							if (number == 1 && feldBelegt(player, (startPosition + number) % 32, figures, figureID)) {
								if (feldBelegt(player, (startPosition + number + 1) % 32, figures, figureID)) {
									boardCurrent[startPosition % 32] = Board.EMPTY;
									boardCurrent[(startPosition + number + 2) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
									figures[player.getId() - 1][figureID - 1].setPosition((startPosition + number + 2) % 32);
									if (schmeissenHausPruefen(player, (startPosition + number + 2) % 32, figures, figureID, schmeissenList)) {

										printString(18, number + 2); // Feld bereits belegt, Figur schlagen
										printString(21, schmeissenList.get(0), schmeissenList.get(1));
									}
									else {
										printString(19, number + 2); // Feld bereits belegt, Felder mehr gegangen
									}
								}
								else {
									boardCurrent[startPosition % 32] = Board.EMPTY;
									boardCurrent[(startPosition + number + 1) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
									figures[player.getId() - 1][figureID - 1].setPosition((startPosition + number + 1) % 32);
									if (schmeissenHausPruefen(player, (startPosition + number + 1) % 32, figures, figureID, schmeissenList)) {
										printString(18, number + 1);
										printString(21, schmeissenList.get(0), schmeissenList.get(1));

									}
									else {
										printString(19, number + 1);
									}
								}
							}

							else if (number == 2 && feldBelegt(player, (startPosition + number) % 32, figures, figureID)) {
								if (feldBelegt(player, (startPosition + number - 1) % 32, figures, figureID)) {
									boardCurrent[startPosition % 32] = Board.EMPTY;
									boardCurrent[(startPosition + number + 1) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
									figures[player.getId() - 1][figureID - 1].setPosition((startPosition + number + 1) % 32);
									if (schmeissenHausPruefen(player, (startPosition + number + 1) % 32, figures, figureID, schmeissenList)) {
										printString(18, number + 1);
										printString(21, schmeissenList.get(0), schmeissenList.get(1));

									}
									else {
										printString(19, number + 1);
									}
								}
								else {
									boardCurrent[startPosition % 32] = Board.EMPTY;
									boardCurrent[(startPosition + number - 1) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
									figures[player.getId() - 1][figureID - 1].setPosition((startPosition + number - 1) % 32);
									if (schmeissenHausPruefen(player, (startPosition + number - 1) % 32, figures, figureID, schmeissenList)) {
										printString(18, number - 1);
										printString(21, schmeissenList.get(0), schmeissenList.get(1));

									}
									else {
										printString(17, number - 1);
									}
								}
							}

							else if (number >= 3 && feldBelegt(player, (startPosition + number) % 32, figures, figureID)) {
								if (feldBelegt(player, (startPosition + number - 1) % 32, figures, figureID)) {
									boardCurrent[startPosition % 32] = Board.EMPTY;
									boardCurrent[(startPosition + number - 2) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
									figures[player.getId() - 1][figureID - 1].setPosition((startPosition + number - 2) % 32);
									if (schmeissenHausPruefen(player, (startPosition + number - 2) % 32, figures, figureID, schmeissenList)) {
										printString(18, number - 2);
										printString(21, schmeissenList.get(0), schmeissenList.get(1));

									}
									else {
										printString(17, number - 2);
									}
								}
								else {
									boardCurrent[startPosition % 32] = Board.EMPTY;
									boardCurrent[(startPosition + number - 1) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
									figures[player.getId() - 1][figureID - 1].setPosition((startPosition + number - 1) % 32);
									if (schmeissenHausPruefen(player, (startPosition + number - 1) % 32, figures, figureID, schmeissenList)) {
										printString(18, number - 1);
										printString(21, schmeissenList.get(0), schmeissenList.get(1));

									}
									else {
										printString(17, number - 1);
									}
								}
							}

							if (!feldBelegt(player, (startPosition + number) % 32, figures, figureID)) {
								boardCurrent[startPosition % 32] = Board.EMPTY;
								boardCurrent = board.getBoardFields();
								boardCurrent[(startPosition + number) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
								figures[player.getId() - 1][figureID - 1].setPosition((startPosition + number) % 32);
							}

							render();

							again6 = false;
						}

						// figure is not at home *****************************************************************************

						else {
							boardCurrent = board.getBoardFields();
							figures = board.getFigures();
							int currentPosition = board.getFigurePosition(player.getId(), figureID);

							// Feld belegt
							if (feldBelegt(player, (currentPosition + number) % 32, figures, figureID)) {
								if (feldBelegt(player, (currentPosition + number - 1) % 32, figures, figureID)) {
									boardCurrent[(currentPosition)] = Board.EMPTY;
									boardCurrent[(currentPosition + number - 2) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
									figures[player.getId() - 1][figureID - 1].setPosition((currentPosition + number - 2) % 32);
									if (schmeissenHaus(player, (currentPosition + number - 2) % 32, figures)) {
										printString(18, number - 2);
									}
									else {
										printString(17, number - 2);
									}
								}
								else {
									boardCurrent[(currentPosition)] = Board.EMPTY;
									boardCurrent[(currentPosition + number - 1) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
									figures[player.getId() - 1][figureID - 1].setPosition((currentPosition + number - 1) % 32);
									if (schmeissenHaus(player, (currentPosition + number - 1) % 32, figures)) {
										printString(18, number - 1);
									}
									else {
										printString(17, number - 1);
									}
								}
							}

							else {
								boardCurrent[currentPosition % 32] = Board.EMPTY;
								boardCurrent[(currentPosition + number) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
								figures[player.getId() - 1][figureID - 1].setPosition((currentPosition + number) % 32);

							}
							render();

							again6 = true;
						}
					}

					// keine 6 gewürfelt **********************************************************************************************

					else {

						List<Integer> figuresNotAtHome = new ArrayList<>();

						// checken ob Figur zuhause sind
						for (int i = 1; i < 4; i++) {
							int currentPosition = board.getFigurePosition(player.getId(), i);
							if (!board.isFigureAtHome(player.getId(), i) && !figures[player.getId() - 1][i - 1].isReachedGoal() && !feldBelegt(player, currentPosition + number, figures, i)) {
								figuresNotAtHome.add(i);
							}
						}

						int firstFigure;
						int secondFigure;

						// zwei Figuren im Home
						if (figuresNotAtHome.size() == 2) {
							firstFigure = figuresNotAtHome.get(0);
							secondFigure = figuresNotAtHome.get(1);
							printString(10, firstFigure, secondFigure);
						}

						// eine Figur im Home
						if (figuresNotAtHome.size() == 1) {
							firstFigure = figuresNotAtHome.get(0);
							printString(10, firstFigure);
						}

						int figureID;

						if (!player.isKI()) {
							figureID = readInt();
							while (!figuresNotAtHome.contains(figureID)) {

								figureID = readInt();
							}
						}
						else {
							figureID = figuresNotAtHome.get(0);
							printString(6, figureID);
						}

						boardCurrent = board.getBoardFields();
						figures = board.getFigures();
						int currentPosition = board.getFigurePosition(player.getId(), figureID);

						// Feld belegt
						if (feldBelegt(player, (currentPosition + number) % 32, figures, figureID)) {
							if (feldBelegt(player, (currentPosition + number - 1) % 32, figures, figureID) && (currentPosition + number - 1) % 32 != currentPosition % 32) {
								boardCurrent[(currentPosition)] = Board.EMPTY;
								boardCurrent[(currentPosition + number - 2) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
								figures[player.getId() - 1][figureID - 1].setPosition((currentPosition + number - 2) % 32);
								if (schmeissenHaus(player, (currentPosition + number - 2) % 32, figures)) {
									printString(18, number - 2);
								}
								else {
									printString(17, number - 2);
								}
							}
							else {
								boardCurrent[(currentPosition)] = Board.EMPTY;
								boardCurrent[(currentPosition + number - 1) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
								figures[player.getId() - 1][figureID - 1].setPosition((currentPosition + number - 1) % 32);
								if (schmeissenHaus(player, (currentPosition + number - 1) % 32, figures)) {
									printString(18, number - 1);
								}
								else {
									printString(17, number - 1);
								}
							}
						}

						else {
							boardCurrent[(currentPosition + number) % 32] = BoardUtility.getBoardValue(player.getId(), figureID);
							figures[player.getId() - 1][figureID - 1].setPosition((currentPosition + number) % 32);
							boardCurrent[currentPosition % 32] = Board.EMPTY;
						}
						again6 = false;
						render();
					}
				}
			}

			if (won(player)) {
				printString(22, player.getId());
				printString(23);
				int result = readInt();
				while (result != 0 && result != 1) {

					result = readInt();

				}
				if (result == 1) {
					play();
				} else {
					return;
				}
			}

			// Spieler wechseln *****************************************************************************************

			if (!again6) {
				if (player == pingu1) {
					player = pingu2;
				}

				else if (player == pingu2) {
					player = pingu3;
				}

				else if (player == pingu3) {
					player = pingu4;
				}

				else if (player == pingu4) {
					player = pingu1;
				}
			}


		}

	}

	// my helper methods ********************************************************************************+

	private boolean schmeissenHausB(Pingu player, int startPosition, Board.Figure[][] figures) {

		if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 1) && !board.isFigureAtHome(pingu1.getId(), 1) && !board.isFigureAtGoal(pingu1.getId(), 1)) {
			figures[pingu1.getId() - 1][0].setHome(true);
			printString(15, 1, pingu1.getId());
			return true;
		}
		else if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 2) && !board.isFigureAtHome(pingu1.getId(), 2) && !board.isFigureAtGoal(pingu1.getId(), 2)) {
			figures[pingu1.getId() - 1][1].setHome(true);
			printString(15, 2, pingu1.getId());
			return true;
		}
		else if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 3) && !board.isFigureAtHome(pingu1.getId(), 3) && !board.isFigureAtGoal(pingu1.getId(), 3)) {
			figures[pingu1.getId() - 1][2].setHome(true);
			printString(15, 3, pingu1.getId());
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 1) && !board.isFigureAtHome(pingu2.getId(), 1) && !board.isFigureAtGoal(pingu2.getId(), 1)) {
			figures[pingu2.getId() - 1][0].setHome(true);
			printString(15, 1, pingu2.getId());
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 2) && !board.isFigureAtHome(pingu2.getId(), 2) && !board.isFigureAtGoal(pingu2.getId(), 2)) {
			figures[pingu2.getId() - 1][1].setHome(true);
			printString(15, 2, pingu2.getId());
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 3) && !board.isFigureAtHome(pingu2.getId(), 3) && !board.isFigureAtGoal(pingu2.getId(), 3)) {
			figures[pingu2.getId() - 1][2].setHome(true);
			printString(15, 3, pingu2.getId());
			return true;
		}
		else if (player != pingu3 && board.getBoardAt(startPosition % 32) == board.getFigurePosition(pingu3.getId(), 1) && !board.isFigureAtHome(pingu3.getId(), 1) && !board.isFigureAtGoal(pingu3.getId(), 1)) {
			figures[pingu3.getId() - 1][0].setHome(true);
			printString(15, 1, pingu3.getId());
			return true;
		}
		else if (player != pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 2) && !board.isFigureAtHome(pingu3.getId(), 2) && !board.isFigureAtGoal(pingu3.getId(), 2)) {
			figures[pingu3.getId() - 1][1].setHome(true);
			printString(15, 2, pingu3.getId());
			return true;
		}
		else if (player != pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 3) && !board.isFigureAtHome(pingu3.getId(), 3) && !board.isFigureAtGoal(pingu3.getId(), 3)) {
			figures[pingu3.getId() - 1][2].setHome(true);
			printString(15, 3, pingu3.getId());
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 1) && !board.isFigureAtHome(pingu4.getId(), 1) && !board.isFigureAtGoal(pingu4.getId(), 1)) {
			figures[pingu4.getId() - 1][0].setHome(true);
			printString(15, 1, pingu4.getId());
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 2) && !board.isFigureAtHome(pingu4.getId(), 2) && !board.isFigureAtGoal(pingu4.getId(), 2)) {
			figures[pingu4.getId() - 1][1].setHome(true);
			printString(15, 2, pingu4.getId());
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 3) && !board.isFigureAtHome(pingu1.getId(), 3) && !board.isFigureAtGoal(pingu4.getId(), 3)) {
			figures[pingu4.getId() - 1][2].setHome(true);
			printString(15, 3, pingu4.getId());
			return true;
		}
		return false;
	}

	private boolean schmeissenHausBPruefen(Pingu player, int startPosition, Board.Figure[][] figures) {

		if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 1) && !board.isFigureAtHome(pingu1.getId(), 1) && !board.isFigureAtGoal(pingu1.getId(), 1)) {

			return true;
		}
		else if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 2) && !board.isFigureAtHome(pingu1.getId(), 2) && !board.isFigureAtGoal(pingu1.getId(), 2)) {
			return true;
		}
		else if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 3) && !board.isFigureAtHome(pingu1.getId(), 3) && !board.isFigureAtGoal(pingu1.getId(), 3)) {
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 1) && !board.isFigureAtHome(pingu2.getId(), 1) && !board.isFigureAtGoal(pingu2.getId(), 1)) {
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 2) && !board.isFigureAtHome(pingu2.getId(), 2) && !board.isFigureAtGoal(pingu2.getId(), 2)) {
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 3) && !board.isFigureAtHome(pingu2.getId(), 3) && !board.isFigureAtGoal(pingu2.getId(), 3)) {
			return true;
		}
		else if (player != pingu3 && board.getBoardAt(startPosition % 32) == board.getFigurePosition(pingu3.getId(), 1) && !board.isFigureAtHome(pingu3.getId(), 1) && !board.isFigureAtGoal(pingu3.getId(), 1)) {
			return true;
		}
		else if (player != pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 2) && !board.isFigureAtHome(pingu3.getId(), 2) && !board.isFigureAtGoal(pingu3.getId(), 2)) {
			return true;
		}
		else if (player != pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 3) && !board.isFigureAtHome(pingu3.getId(), 3) && !board.isFigureAtGoal(pingu3.getId(), 3)) {
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 1) && !board.isFigureAtHome(pingu4.getId(), 1) && !board.isFigureAtGoal(pingu4.getId(), 1)) {
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 2) && !board.isFigureAtHome(pingu4.getId(), 2) && !board.isFigureAtGoal(pingu4.getId(), 2)) {
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 3) && !board.isFigureAtHome(pingu1.getId(), 3) && !board.isFigureAtGoal(pingu4.getId(), 3)) {
			return true;
		}
		return false;
	}

	private boolean schmeissenHaus(Pingu player, int startPosition, Board.Figure[][] figures) {

		if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 1) && !board.isFigureAtHome(pingu1.getId(), 1) && !board.isFigureAtGoal(pingu1.getId(), 1)) {
			figures[pingu1.getId() - 1][0].setHome(true);
			printString(21, 1, pingu1.getId()); // Figur von Pinguin wurde geschlagen
			return true;
		}
		else if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 2) && !board.isFigureAtHome(pingu1.getId(), 2) && !board.isFigureAtGoal(pingu1.getId(), 2)) {
			figures[pingu1.getId() - 1][1].setHome(true);
			printString(21, 2, pingu1.getId());
			return true;
		}
		else if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 3) && !board.isFigureAtHome(pingu1.getId(), 3) && !board.isFigureAtGoal(pingu1.getId(), 3)) {
			figures[pingu1.getId() - 1][2].setHome(true);
			printString(21, 3, pingu1.getId());
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 1) && !board.isFigureAtHome(pingu2.getId(), 1) && !board.isFigureAtGoal(pingu2.getId(), 1)) {
			figures[pingu2.getId() - 1][0].setHome(true);
			printString(21, 1, pingu2.getId());
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 2) && !board.isFigureAtHome(pingu2.getId(), 2) && !board.isFigureAtGoal(pingu2.getId(), 2)) {
			figures[pingu2.getId() - 1][1].setHome(true);
			printString(21, 2, pingu2.getId());
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 3) && !board.isFigureAtHome(pingu2.getId(), 3) && !board.isFigureAtGoal(pingu2.getId(), 3)) {
			figures[pingu2.getId() - 1][2].setHome(true);
			printString(21, 3, pingu2.getId());
			return true;
		}
		else if (player != pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 1) && !board.isFigureAtHome(pingu3.getId(), 1) && !board.isFigureAtGoal(pingu3.getId(), 1)) {
			figures[pingu3.getId() - 1][0].setHome(true);
			printString(21, 1, pingu3.getId());
			return true;
		}
		else if (player != pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 2) && !board.isFigureAtHome(pingu3.getId(), 2) && !board.isFigureAtGoal(pingu3.getId(), 2)) {
			figures[pingu3.getId() - 1][1].setHome(true);
			printString(21, 2, pingu3.getId());
			return true;
		}
		else if (player != pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 3) && !board.isFigureAtHome(pingu3.getId(), 3) && !board.isFigureAtGoal(pingu3.getId(), 3)) {
			figures[pingu3.getId() - 1][2].setHome(true);
			printString(21, 3, pingu3.getId());
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 1) && !board.isFigureAtHome(pingu4.getId(), 1) && !board.isFigureAtGoal(pingu4.getId(), 1)) {
			figures[pingu4.getId() - 1][0].setHome(true);
			printString(21, 1, pingu4.getId());
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 2) && !board.isFigureAtHome(pingu4.getId(), 2) && !board.isFigureAtGoal(pingu4.getId(), 2)) {
			figures[pingu4.getId() - 1][1].setHome(true);
			printString(21, 2, pingu4.getId());
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 3) && !board.isFigureAtHome(pingu4.getId(), 3) && !board.isFigureAtGoal(pingu4.getId(), 3)) {
			figures[pingu4.getId() - 1][2].setHome(true);
			printString(21, 3, pingu4.getId());
			return true;
		}
		return false;
	}

	private boolean schmeissenHausPruefen(Pingu player, int startPosition, Board.Figure[][] figures, int figureID, List<Integer> list) {

		if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 1) && !board.isFigureAtHome(pingu1.getId(), 1) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu1.getId(), 1)) {
			list.add(1);
			list.add(pingu1.getId());
			return true;
		}
		else if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 2) && !board.isFigureAtHome(pingu1.getId(), 2) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu1.getId(), 2)) {
			list.add(2);
			list.add(pingu1.getId());
			return true;
		}
		else if (player != pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 3) && !board.isFigureAtHome(pingu1.getId(), 3) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu1.getId(), 3)) {
			list.add(3);
			list.add(pingu1.getId());
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 1) && !board.isFigureAtHome(pingu2.getId(), 1) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu2.getId(), 1)) {
			list.add(1);
			list.add(pingu2.getId());
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 2) && !board.isFigureAtHome(pingu2.getId(), 2) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu2.getId(), 2)) {
			list.add(2);
			list.add(pingu2.getId());
			return true;
		}
		else if (player != pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 3) && !board.isFigureAtHome(pingu2.getId(), 3) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu2.getId(), 3)) {
			list.add(3);
			list.add(pingu2.getId());
			return true;
		}
		else if (player != pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 1) && !board.isFigureAtHome(pingu3.getId(), 1) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu3.getId(), 1)) {
			list.add(1);
			list.add(pingu3.getId());
			return true;
		}
		else if (player != pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 2) && !board.isFigureAtHome(pingu3.getId(), 2) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu3.getId(), 2)) {
			list.add(2);
			list.add(pingu3.getId());
			return true;
		}
		else if (player != pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 3) && !board.isFigureAtHome(pingu3.getId(), 3) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu3.getId(), 3)) {
			list.add(3);
			list.add(pingu3.getId());
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 1) && !board.isFigureAtHome(pingu4.getId(), 1) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu4.getId(), 1)) {
			list.add(1);
			list.add(pingu4.getId());
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 2) && !board.isFigureAtHome(pingu4.getId(), 2) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu4.getId(), 2)) {
			list.add(2);
			list.add(pingu4.getId());
			return true;
		}
		else if (player != pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 3) && !board.isFigureAtHome(pingu4.getId(), 3) && !board.isFigureAtHome(player.getId(), figureID) && !board.isFigureAtGoal(pingu4.getId(), 3)) {
			list.add(3);
			list.add(pingu4.getId());
			return true;
		}
		return false;
	}
	private boolean feldBelegt(Pingu player, int startPosition, Board.Figure[][] figures, int figureID) {
		if (player == pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 1) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][0].isHome() && !board.isFigureAtGoal(player.getId(), 1)) {
			return true;
		}
		else if (player == pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 2) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][1].isHome() && !board.isFigureAtGoal(player.getId(), 2)) {
			return true;
		}
		else if (player == pingu1 && startPosition % 32 == board.getFigurePosition(pingu1.getId(), 3) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][2].isHome() && !board.isFigureAtGoal(player.getId(), 3)) {
			return true;
		}
		else if (player == pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 1) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][0].isHome() && !board.isFigureAtGoal(player.getId(), 1)) {
			return true;
		}
		else if (player == pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 2) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][1].isHome() && !board.isFigureAtGoal(player.getId(), 2)) {
			return true;
		}
		else if (player == pingu2 && startPosition % 32 == board.getFigurePosition(pingu2.getId(), 3) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][2].isHome() && !board.isFigureAtGoal(player.getId(), 3)) {
			return true;
		}
		else if (player == pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 1) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][0].isHome() && !board.isFigureAtGoal(player.getId(), 1)) {
			return true;
		}
		else if (player == pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 2) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][1].isHome() && !board.isFigureAtGoal(player.getId(), 2)) {
			return true;
		}
		else if (player == pingu3 && startPosition % 32 == board.getFigurePosition(pingu3.getId(), 3) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][2].isHome() && !board.isFigureAtGoal(player.getId(), 3)) {
			return true;
		}
		else if (player == pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 1) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][0].isHome() && !board.isFigureAtGoal(player.getId(), 1)) {
			return true;
		}
		else if (player == pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 2) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][1].isHome() && !board.isFigureAtGoal(player.getId(), 2)) {
			return true;
		}
		else if (player == pingu4 && startPosition % 32 == board.getFigurePosition(pingu4.getId(), 3) && !figures[player.getId() - 1][figureID - 1].isHome() && !figures[player.getId() - 1][2].isHome() && !board.isFigureAtGoal(player.getId(), 3)) {
			return true;
		}
		return false;
	}

	private boolean schmeissenGoal(Pingu player, int endPosition, Board.Figure[][] figures, int[] currentBoard) {

		if (player != pingu1 && endPosition == board.getFigurePosition(pingu1.getId(), 1)) {
			currentBoard[endPosition] = Board.EMPTY;
			figures[pingu1.getId() - 1][0].setHome(true);
			printString(8, 1, pingu1.getId()); // Figur von Pinguin wurde geschlagen
			return true;
		}
		else if (player != pingu1 && endPosition == board.getFigurePosition(pingu1.getId(), 2)) {
			figures[pingu1.getId() - 1][1].setHome(true);
			currentBoard[endPosition] = Board.EMPTY;
			printString(8, 2, pingu1.getId());
			return true;
		}
		else if (player != pingu1 && endPosition == board.getFigurePosition(pingu1.getId(), 3)) {
			figures[pingu1.getId() - 1][2].setHome(true);
			currentBoard[endPosition] = Board.EMPTY;
			printString(8, 3, pingu1.getId());
			return true;
		}
		else if (player != pingu2 && endPosition == board.getFigurePosition(pingu2.getId(), 1)) {
			figures[pingu2.getId() - 1][0].setHome(true);
			currentBoard[endPosition] = Board.EMPTY;
			printString(8, 1, pingu2.getId());
			return true;
		}
		else if (player != pingu2 && endPosition == board.getFigurePosition(pingu2.getId(), 2)) {
			figures[pingu2.getId() - 1][1].setHome(true);
			currentBoard[endPosition] = Board.EMPTY;
			printString(8, 2, pingu2.getId());
			return true;
		}
		else if (player != pingu2 && endPosition == board.getFigurePosition(pingu2.getId(), 3)) {
			figures[pingu2.getId() - 1][2].setHome(true);
			currentBoard[endPosition] = Board.EMPTY;
			printString(8, 3, pingu2.getId());
			return true;
		}
		else if (player != pingu3 && endPosition == board.getFigurePosition(pingu3.getId(), 1)) {
			figures[pingu3.getId() - 1][0].setHome(true);
			currentBoard[endPosition] = Board.EMPTY;
			printString(8, 1, pingu3.getId());
			return true;
		}
		else if (player != pingu3 && endPosition == board.getFigurePosition(pingu3.getId(), 2)) {
			figures[pingu3.getId() - 1][1].setHome(true);
			currentBoard[endPosition] = Board.EMPTY;
			printString(8, 2, pingu3.getId());
			return true;
		}
		else if (player != pingu3 && endPosition == board.getFigurePosition(pingu3.getId(), 3)) {
			figures[pingu3.getId() - 1][2].setHome(true);
			currentBoard[endPosition] = Board.EMPTY;
			printString(8, 3, pingu3.getId());
			return true;
		}
		else if (player != pingu4 && endPosition == board.getFigurePosition(pingu4.getId(), 1)) {
			figures[pingu4.getId() - 1][0].setHome(true);
			currentBoard[endPosition] = Board.EMPTY;
			printString(8, 1, pingu4.getId());
			return true;
		}
		else if (player != pingu4 && endPosition == board.getFigurePosition(pingu4.getId(), 2)) {
			figures[pingu4.getId() - 1][1].setHome(true);
			currentBoard[endPosition] = Board.EMPTY;
			printString(8, 2, pingu4.getId());
			return true;
		}
		else if (player != pingu4 && endPosition == board.getFigurePosition(pingu4.getId(), 3)) {
			figures[pingu4.getId() - 1][2].setHome(true);
			currentBoard[endPosition] = Board.EMPTY;
			printString(8, 3, pingu4.getId());
			return true;
		}
		return false;
	}

	private boolean hasReachGoal(Pingu player, int figure, int number, Board.Figure[][] figures) {
		if ((board.getFigurePosition(player.getId(), figure) + number) >= Board.getPlayerGoalPosition(player.getId()) && (board.getFigurePosition(player.getId(), figure) % 32 < Board.getPlayerGoalPosition(player.getId())) && !figures[player.getId() - 1][figure - 1].isHome()) {
			return true;
		}
		return false;
	}

	private boolean won(Pingu player) {
		Board.Figure[][] figures = board.getFigures();
		for (int i = 1; i < 4; i++) {
			if (!figures[player.getId() - 1][i - 1].isReachedGoal()) {
				return false;
			}
		}
		return true;

	}

	// ********************************************************************************************************************

	// here are some helper methods to help you with the implementation:

	/**
	 * reads a single integer from console and prints the lines required by the task description
	 * @return the number or Integer.MIN_VALUE if input is invalid
	 */
	private int readInt() {
		System.out.print("> ");
		try {
			String in = reader.readLine();
			return Integer.parseInt(in);
		} catch (IOException e) {
			System.out.println("An Exception occured: " + e.getLocalizedMessage());
			System.exit(1);
		} catch (NumberFormatException n) {
			System.out.println("Keine gültige Zahl!");
			return Integer.MIN_VALUE;
		}
		return Integer.MIN_VALUE;
	}

	/**
	 * rolls a dice
	 * @return an integer in [1,6]
	 */
	/*protected int rollDice() {
		// do not change method (will be overridden for testing)
		return random.nextInt(6) + 1;
	}*/

	protected int rollDice() {
		if (rollDiceCount == 0) {
			rollDiceCount++;
			return 6;
		}
		return 2;
	}

	/**
	 * prints the board using the selected render mode and waits for
	 * renderDelay milliseconds if its value is larger than 0
	 */
	private void render() {
		if (renderBW) {
			BoardUtility.printBoardBW(board);
		} else {
			BoardUtility.printBoard(board);
		}
		if (renderDelay > 0) {
			try {
				Thread.sleep(renderDelay);
			} catch (InterruptedException e) {
				System.out.println("Eine Exception ist aufgetreten: " + e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Prints the String defined by id (see task description)
	 * @param id the Strings id
	 * @param params the required parameters for the string in order of appearance 
	 * 		(for outputs with variable number of figures the order is sorted automatically)
	 */
	private static void printString(int id, int... params) {
		switch (id) {
		case 1:
			// #1: start new Game
			System.out.println("Willkommen zu \"Pingu ärgere dich nicht\"!");
			break;
		case 2:
			// #2: input request for number of players
			System.out.println("Wie viele Pinguine wollen spielen?");
			System.out.println("Bitte eine Zahl von 0 (nur KI) bis 4 eingeben!");
			break;
		case 3:
			// #3: game start with selected number of players
			System.out
					.println("Starte Spiel mit " + params[0] + " \"echten\" und " + (4 - params[0]) + " KI Pinguinen.");
			break;
		case 4:
			// #4: first line of a single turn
			System.out.println("Pinguin " + params[0] + " ist am Zug.");
			break;
		case 5:
			// #5: dice roll
			System.out.println("Pinguin " + params[0] + " hat eine " + params[1] + " gewürfelt.");
			break;
		case 6:
			// #6: AI chooses figure
			System.out.println("KI wählt Figur " + params[0] + ".");
			break;
		case 7:
			// #7: figures can reach goal
			System.out.print("Eine der folgenden Figuren kann das Ziel erreichen (bitte auswählen): ");
			Arrays.sort(params);
			for (int i = 0; i < params.length; i++) {
				System.out.print(params[i]);
				if (i != params.length - 1) {
					System.out.print(", ");
				}
			}
			System.out.println();
			break;
		case 8:
			// #8: throw figure while reaching goal
			System.out.println("Beim Erreichen des Ziels wurde Figur " + params[0] + " des Pinguins " + params[1]
					+ " geschlagen.");
			break;
		case 9:
			// #9: figure can throw
			System.out.print("Eine der folgenden Figuren kann eine gegnerische Figur schlagen (bitte auswählen): ");
			Arrays.sort(params);
			for (int i = 0; i < params.length; i++) {
				System.out.print(params[i]);
				if (i != params.length - 1) {
					System.out.print(", ");
				}
			}
			System.out.println();
			break;
		case 10:
			// #10: figure can move
			System.out.print("Eine der folgenden Figuren kann bewegt werden (bitte auswählen): ");
			Arrays.sort(params);
			for (int i = 0; i < params.length; i++) {
				System.out.print(params[i]);
				if (i != params.length - 1) {
					System.out.print(", ");
				}
			}
			System.out.println();
			break;
		case 11:
			// #11: no move possible
			System.out.println(
					"Keine Figur von Pinguin " + params[0] + " kann mit einer " + params[1] + " bewegt werden.");
			break;
		case 12:
			// #12: no figure on field
			System.out.println("Pinguin " + params[0]
					+ " hat keine Figur auf dem Feld und braucht eine 6. Er darf bis zu 3-mal würfeln.");
			break;
		case 13:
			// #13: no 6 this round
			System.out.println("Schade, keine 6. Mehr Glück nächste Runde!");
			break;
		case 14:
			// #14: choose figure to move to field
			System.out.print("Welche Figur möchtest du aufs Spielfeld ziehen? Bitte wählen: ");
			Arrays.sort(params);
			for (int i = 0; i < params.length; i++) {
				System.out.print(params[i]);
				if (i != params.length - 1) {
					System.out.print(", ");
				}
			}
			System.out.println();
			break;
		case 15:
			// #15: throw while moving to field
			System.out.println("Die Figur " + params[0] + " von Pinguin " + params[1]
					+ " wurde beim Verlassen des Hauses geschlagen.");
			break;
		case 16:
			// #16: clear start field
			System.out.println("Pinguin " + params[0] + " muss das Startfeld räumen.");
			break;
		case 17:
			// #17: clear start field - special case: move less
			System.out.println("Feld bereits belegt, es kann/können nur " + params[0] + " Feld(er) gegangen werden.");
			break;
		case 18:
			// #18: clear start field - special case: move less + throw
			System.out.println("Feld bereits belegt, es kann/können nur " + params[0]
					+ " Feld(er) gegangen werden und dabei wird eine andere Figur geschlagen.");
			break;
		case 19:
			// #19: clear start field - special case: move more
			System.out.println("Feld bereits belegt, es dürfen sogar " + params[0] + " Felder gegangen werden.");
			break;
		case 20:
			// #20: clear start field - special case: move more + throw
			System.out.println("Feld bereits belegt, es dürfen sogar " + params[0]
					+ " Felder gegangen werden und dabei wird eine andere Figur geschlagen.");
			break;
		case 21:
			// #21: figure thrown
			System.out.println("Figur " + params[0] + " von Pinguin " + params[1] + " wurde geschlagen.");
			break;
		case 22:
			// #22: player won
			System.out.println("Herzlichen Glückwunsch Pinguin " + params[0] + ", du hast gewonnen!!!");
			break;
		case 23:
			// #23: restart game
			System.out.println("Soll ein neues Spiel gestartet werden? 1 - Ja, 0 - Nein");
			break;
		default:
			System.out.println("Unkown id!");
			break;
		}
	}

	public static void main(String[] args) {
		// default options (you may change them for testing or playing)
		boolean bw = false;
		int seed = 420;
		int delay = 5000;
		// very basic args check
		if (args.length >= 1) {
			bw = Boolean.parseBoolean(args[0]);
		}
		if (args.length >= 2) {
			seed = Integer.parseInt(args[1]);
		}
		if (args.length >= 3) {
			delay = Integer.parseInt(args[2]);
		}
		// init game and start playing
		PinguGame p = new PinguGame(bw, seed, delay);
		p.play();
	}
}
