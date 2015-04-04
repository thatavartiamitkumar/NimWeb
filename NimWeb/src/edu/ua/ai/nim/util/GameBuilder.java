package edu.ua.ai.nim.util;

import java.io.Serializable;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Method to build the state space for a given configuration of the board
	 * 
	 * @param stacks
	 * @param player
	 * @return
	 */
	public State buildStateSpace(int stacks[], char player, int stackChoosen,
			int tokensRemoved) {
		// Creating a new state with the given configuration of the stacks and
		// the player to make the move
		State state = new State();
		state.setStacks(stacks);
		state.setPlayer(player);
		state.setStackChoosen(stackChoosen);
		state.setTokensRemoved(tokensRemoved);

		// Finding the tokens left on the board
		int remainingTokens = getTokensCount(state.getStacks());

		// If there are any tokens left, then generating the possible moves for
		// them
		if (remainingTokens > 0) {
			state.setPossibleMoves(new ArrayList<State>());
			for (int i = 0; i < stacks.length; i++) {

				// Number of Tokens in the ith stack
				int stackTokens = stacks[i];

				// If number of tokens in the stack is more than 1
				if (stackTokens >= 1) {
					int[] possibleStack = stacks.clone();
					possibleStack[i] = stackTokens - 1;
					state.getPossibleMoves().add(
							buildStateSpace(possibleStack,
									(player == 'C') ? 'U' : 'C', i, 1));
				}

				// If number of tokens in the stack is more than 2
				if (stackTokens >= 2) {
					int[] possibleStack = stacks.clone();
					possibleStack[i] = stackTokens - 2;
					state.getPossibleMoves().add(
							buildStateSpace(possibleStack,
									(player == 'C') ? 'U' : 'C', i, 2));
				}

				// If number of tokens in the stack is more than 3
				if (stackTokens >= 3) {
					int[] possibleStack = stacks.clone();
					possibleStack[i] = stackTokens - 3;
					state.getPossibleMoves().add(
							buildStateSpace(possibleStack,
									(player == 'C') ? 'U' : 'C', i, 3));
				}

			}
		}

		return state;
	}

	

	/**
	 * Method to calculate the sum of number of tokens available on all the
	 * stacks
	 * 
	 * @param stacks
	 * @return
	 */
	public int getTokensCount(int stacks[]) {
		int sum = 0;

		if (stacks.length > 0) {
			for (int i = 0; i < stacks.length; i++) {
				sum += stacks[i];
			}
		}

		return sum;
	}

	/**
	 * Method to perform the minimax algorithm using alpha-beta pruning.
	 * 
	 * @param state
	 * @return
	 */
	public State computeMinimax(State state) {
		int minValue = 1;
		int maxValue = -1;

		// Terminal state i.e., no more tokens left on the board
		if (null == state.getPossibleMoves()) {
			// If it's computer's turn but no tokens are there, then computer
			// wins, since user has removed the last token(s). In such a case,
			// return 1 else -1.
			state.setHeuristicValue(state.getPlayer() == 'C' ? 1 : -1);
			return state;
		}
		// If it's computer's turn to make a move
		else if (state.getPlayer() == 'C') {
			for (int i = 0; i < state.getPossibleMoves().size(); i++) {
				State bestState = computeMinimax(state.getPossibleMoves()
						.get(i));
				maxValue = Math.max(maxValue, bestState.getHeuristicValue());
				// Pruning the rest of the childs(possible moves)
				if (1 == maxValue) {
					state.setBestState(bestState);
					break;
				}
				state.setBestState(bestState);
			}
			state.setHeuristicValue(maxValue);
			return state;
		}
		// If it's user's turn to make a move
		else {
			for (int i = 0; i < state.getPossibleMoves().size(); i++) {
				State bestState = computeMinimax(state.getPossibleMoves()
						.get(i));
				minValue = Math.min(minValue, bestState.getHeuristicValue());
				// Pruning the rest of the childs(possible moves)
				if (-1 == minValue) {
					state.setBestState(bestState);
					break;
				}
				state.setBestState(bestState);
			}
			state.setHeuristicValue(minValue);
			return state;
		}

	}

	/**
	 * Method to determine the state in the state space after user makes a move
	 * 
	 * @param stackChoosen
	 * @param tokensRemoved
	 * @param currentGameState
	 * @return
	 */
	public State userMove(int stackChoosen, int tokensRemoved,
			State currentGameState) {
		State nextState = null;

		// Determining the state after user makes a move. We
		// find the state possible when we remove so many token
		// from the stack chosen.
		for (int i = 0; i < currentGameState.getPossibleMoves().size(); i++) {
			State possibleMove = currentGameState.getPossibleMoves().get(i);
			if (possibleMove.getStackChoosen() == (stackChoosen - 1)
					&& possibleMove.getTokensRemoved() == tokensRemoved) {
				nextState = possibleMove;
				break;
			}
		}

		return nextState;
	}

	/**
	 * Method to determine the best possible move, which a computer can make as
	 * per the current state of the game
	 * 
	 * @param currentGameState
	 * @return
	 */
	public State computerMove(State currentGameState) {
		// Finding the best move to go the next state
		State nextState = computeMinimax(currentGameState).getBestState();

		System.out.println("My Move:: \t Stack: "
				+ (nextState.getStackChoosen() + 1) + "\t Tokens Removed: "
				+ nextState.getTokensRemoved());

		return nextState;
	}
}
