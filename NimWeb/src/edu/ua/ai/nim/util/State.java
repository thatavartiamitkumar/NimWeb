package edu.ua.ai.nim.util;

import java.io.Serializable;
import java.util.List;

public class State implements Serializable {

	private static final long serialVersionUID = 1L;

	// Stack configuration of the current state
	private int stacks[];
	// Player who has to make a move
	private char player;
	// Minimax value assigned to the state(if computed)
	private int heuristicValue;
	// Tokens removed from the previous state to get to this state
	private int tokensRemoved;
	// Stack from which the tokens have been removed
	private int stackChoosen;
	// Possible moves from this state
	private List<State> possibleMoves;
	// Best state to move to from this state
	private State bestState;

	public int[] getStacks() {
		return stacks;
	}

	public void setStacks(int[] stacks) {
		this.stacks = stacks;
	}

	public char getPlayer() {
		return player;
	}

	public void setPlayer(char player) {
		this.player = player;
	}

	public List<State> getPossibleMoves() {
		return possibleMoves;
	}

	public void setPossibleMoves(List<State> possibleMoves) {
		this.possibleMoves = possibleMoves;
	}

	public int getHeuristicValue() {
		return heuristicValue;
	}

	public void setHeuristicValue(int heuristicValue) {
		this.heuristicValue = heuristicValue;
	}

	public int getTokensRemoved() {
		return tokensRemoved;
	}

	public void setTokensRemoved(int tokensRemoved) {
		this.tokensRemoved = tokensRemoved;
	}

	public int getStackChoosen() {
		return stackChoosen;
	}

	public void setStackChoosen(int stackChoosen) {
		this.stackChoosen = stackChoosen;
	}

	public State getBestState() {
		return bestState;
	}

	public void setBestState(State bestState) {
		this.bestState = bestState;
	}

}
