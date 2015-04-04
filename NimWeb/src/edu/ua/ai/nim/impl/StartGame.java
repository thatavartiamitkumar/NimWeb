package edu.ua.ai.nim.impl;

import java.util.Scanner;

import edu.ua.ai.nim.util.GameBuilder;
import edu.ua.ai.nim.util.State;

/**
 * 
 * @author MaheshBabu
 *
 */
public class StartGame {

	public static void main(String[] args) {
		try {
			Scanner scanner = new Scanner(System.in);
			GameBuilder gameBuilder = new GameBuilder();
			int maxRemovableLimit = 3;

			boolean play = true;
			while (play) {
				// Configuring the game i.e., Number of stacks and number of
				// tokens on each stack
				System.out.println("How many stacks would you like to have:");
				int numberOfStacks = scanner.nextInt();

				int[] stacks = new int[numberOfStacks];

				// If there are multiple stacks
				if (1 < numberOfStacks) {
					System.out
							.println("Would you like to have equal number of tokens in each pile?(Y/N) :");
					if (scanner.next().equalsIgnoreCase("Y")) {
						int numberOfTokens = 0;
						System.out
								.println("How many tokens would you like to have(at least 1) in each stack? :");
						numberOfTokens = scanner.nextInt();
						while (numberOfTokens == 0) {
							System.out
									.println("You should have at least one token in a stack. Please enter another value.");
							numberOfTokens = scanner.nextInt();
						}
						for (int i = 0; i < stacks.length; i++) {
							stacks[i] = numberOfTokens;
						}
					} else {
						for (int i = 0; i < stacks.length; i++) {
							System.out
									.println("How many tokens would you like to have(at least 1) in Stack-"
											+ (i + 1) + " ? :");
							stacks[i] = scanner.nextInt();
							while (stacks[i] == 0) {
								System.out
										.println("You should have at least one token in a stack. Please enter another value.");
								stacks[i] = scanner.nextInt();
							}
						}
					}
				} else {
					System.out
							.println("How many tokens would you like to have? :");
					int numberOfTokens = scanner.nextInt();
					while (numberOfTokens == 0) {
						System.out
								.println("You should have at least one token in a stack. Please enter another value.");
						numberOfTokens = scanner.nextInt();
					}
					stacks[0] = numberOfTokens;
				}

				// starting the game
				char currentPlayer = 'C';
				System.out.println("Would you like to play first?(Y/N) : ");
				if (scanner.next().equalsIgnoreCase("Y")) {
					currentPlayer = 'U';
				}

				System.out
						.println("\n\n###################################################################################");
				System.out
						.println("###################################################################################");

				// Building the state space
				State stateSpace = gameBuilder.buildStateSpace(stacks,
						currentPlayer, 0, 0);

				int tokensRemaining = gameBuilder.getTokensCount(stateSpace
						.getStacks());
				State currentGameState = stateSpace;

				// Game starts
				while (tokensRemaining != 0) {
					State nextState = null;
					int[] currentStacks = currentGameState.getStacks();
					if (currentPlayer == 'U') {

						int stackChoosen = 1;

						// If there are more than 1 stack then asking the user
						// to enter the stack number
						if (currentStacks.length > 1) {
							System.out
									.println("Which stack would you like to remove the tokens from? : ");
							// Accept and validate the stack number
							stackChoosen = scanner.nextInt();

							boolean invalidStack = (0 == stackChoosen)
									|| (stackChoosen > currentStacks.length)
									|| (0 == currentStacks[stackChoosen - 1]);
							while (invalidStack) {
								// Checking if the stack chosen is available
								if (0 == stackChoosen
										|| stackChoosen > currentStacks.length)
									System.out
											.println("Stack number must be less than or equal to "
													+ currentStacks.length
													+ " and greater than 0. Please choose another.");

								// Checking if any tokens are left in the stack
								// chosen. If no tokens are left, asking user to
								// select
								// another stack
								else if (0 == currentStacks[stackChoosen - 1])
									System.out
											.println("There are no tokens left in stack"
													+ stackChoosen
													+ ". Please choose another.");

								stackChoosen = scanner.nextInt();

								// Checking if the stack is valid
								invalidStack = (stackChoosen > currentStacks.length)
										|| (0 == currentStacks[stackChoosen - 1]);
							}
						}
						System.out
								.println("How many tokens would you like to remove? : ");
						// Accept and validate the tokens number
						int tokensRemoved = scanner.nextInt();

						while ((0 == tokensRemoved)
								|| (tokensRemoved > currentStacks[stackChoosen - 1])
								|| (tokensRemoved > maxRemovableLimit)) {
							if (0 == tokensRemoved)
								System.out
										.println("You should remove at least 1 token. Enter again.");
							else if (tokensRemoved > maxRemovableLimit)
								System.out
										.println("You can remove a maximum of "
												+ maxRemovableLimit
												+ " tokens only.");
							else
								System.out.println("You only have "
										+ currentStacks[stackChoosen - 1]
										+ " in stack " + stackChoosen
										+ ". Enter again.");
							tokensRemoved = scanner.nextInt();
						}

						// Determining the state after user makes a move. We
						// find the state possible when we remove so many token
						// from the stack chosen.
						for (int i = 0; i < currentGameState.getPossibleMoves()
								.size(); i++) {
							State possibleMove = currentGameState
									.getPossibleMoves().get(i);
							if (possibleMove.getStackChoosen() == (stackChoosen - 1)
									&& possibleMove.getTokensRemoved() == tokensRemoved) {
								nextState = possibleMove;
								break;
							}
						}

						System.out.println("Your Move:: \t Stack: "
								+ stackChoosen + "\t Tokens Removed: "
								+ tokensRemoved);
						System.out.println("Board state after your move:");
						for (int i = 0; i < nextState.getStacks().length; i++) {
							System.out.print("Stack" + (i + 1) + ": ");
							for (int j = 0; j < nextState.getStacks()[i]; j++) {
								System.out.print(" * ");
							}
							System.out.println("  --  ("+nextState.getStacks()[i]+")");
						}

						// setting currentPlayer to C(Computer)
						currentPlayer = 'C';
					} else {
						// Finding the best move to go the next state
						nextState = gameBuilder
								.computeMinimax(currentGameState)
								.getBestState();
						System.out.println("My Move:: \t Stack: "
								+ (nextState.getStackChoosen() + 1)
								+ "\t Tokens Removed: "
								+ nextState.getTokensRemoved());
						System.out.println("Board state after my move:");
						for (int i = 0; i < nextState.getStacks().length; i++) {
							System.out.print("Stack" + (i + 1) + ": ");							
							for (int j = 0; j < nextState.getStacks()[i]; j++) {
								System.out.print(" * ");
							}

							System.out.println("  --  ("+nextState.getStacks()[i]+")");
						}

						// setting currentPlayer to U(User)
						currentPlayer = 'U';
					}

					System.out
							.println("--------------------------------------------");
					currentGameState = nextState;
					// checking the number of tokens left on the board
					tokensRemaining = gameBuilder
							.getTokensCount(currentGameState.getStacks());
				}

				// Determining the winner
				if (currentGameState.getPlayer() == 'C')
					System.out.println("\n\n\n\t\tComputer Wins!!");
				else
					System.out.println("\n\n\n\t\tYou Win!!");

				// Checking if the user wants to play another game
				System.out.println("\n\n\nWant to play again? (Y/N): ");
				play = (scanner.next().equalsIgnoreCase("y"));
			}
			scanner.close();
		} catch (Exception exception) {
			System.out.println("Unexpected error occurred. Please try again.\n"
					+ exception.getMessage());
			exception.printStackTrace();
		}
	}

}
