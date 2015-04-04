package edu.ua.ai.nim.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import edu.ua.ai.nim.util.GameBuilder;
import edu.ua.ai.nim.util.Stack;
import edu.ua.ai.nim.util.State;
import edu.ua.ai.nim.util.Token;

@ManagedBean
@SessionScoped
public class GameBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private int stackChoosen;
	private int tokensRemoved;
	private List<Stack> gameConfiguration;

	private GameBuilder gameBuilder;
	private State stateSpace;
	private int[] stacks;
	private char player;
	private boolean gameMode;
	private String computerMoveLog;
	private boolean gameOn;
	private boolean userWins;

	// Configuration related
	private List<Stack> stackTokensList;
	private int numberOfStacks;
	private boolean renderTokenChoice;
	private int equalTokens;
	private int tokensInEachStack;
	private boolean renderMultipleStacks;

	public GameBean() {
		gameBuilder = new GameBuilder();
		gameOn = true;

		// setting up default configuration
		defaultConfiguration();

		// Building the stacks array
		buildStacks();

		// Building configuration list
		buildGameConfiguration();
	}

	public void defaultConfiguration() {
		numberOfStacks = 3;
		equalTokens = 1;
		renderTokenChoice = true;
		tokensInEachStack = 3;
		renderMultipleStacks = false;

		buildStackTokensList();
	}

	private void buildStackTokensList() {
		stackTokensList = new ArrayList<Stack>();
		for (int i = 0; i < numberOfStacks; i++) {
			Stack tempStack = new Stack();
			tempStack.setStackNumber(i + 1);
			tempStack.setTokensCount(tokensInEachStack);
			stackTokensList.add(tempStack);
		}
	}

	public void buildStacks() {
		stacks = new int[stackTokensList.size()];
		for (int i = 0; i < stackTokensList.size(); i++) {
			stacks[i] = stackTokensList.get(i).getTokensCount();
		}

	}

	private void buildGameConfiguration() {
		gameConfiguration = new ArrayList<Stack>();
		for (int i = 0; i < stacks.length; i++) {
			Stack tempStack = new Stack();
			tempStack.setStackNumber(i);
			tempStack.setTokensCount(stacks[i]);
			for (int j = 0; j < stacks[i]; j++) {
				Token tempToken = new Token();
				tempStack.getTokens().add(tempToken);
			}
			gameConfiguration.add(tempStack);
		}
	}

	public String userPlaysFirst() {
		player = 'U';
		// Building the state space
		stateSpace = gameBuilder.buildStateSpace(stacks, player, 0, 0);

		System.out.println("User plays first");

		gameMode = true;
		return "";
	}

	public String machinePlaysFirst() {
		player = 'C';
		// Building the state space
		stateSpace = gameBuilder.buildStateSpace(stacks, player, 0, 0);

		System.out.println("Machine plays first");

		gameMode = true;
		computerMove();

		return "";
	}

	public String restartGame() {
		gameMode = false;
		gameOn = true;

		getNewGameState();

		return "game.xhtml";
	}

	public String quitGame() {
		System.out.println("In quit game method");
		gameMode = false;
		gameOn = true;

		getNewGameState();

		return "startPage.xhtml";
	}

	private void getNewGameState() {
		buildStacks();
		buildGameConfiguration();

		stackChoosen = 0;
		tokensRemoved = 0;
		computerMoveLog = "";
	}

	public String userMove() {
		System.out.println("In user move");
		FacesMessage errorMessage = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (numberOfStacks == 1) {
			stackChoosen = 1;
		}
		// Validating the Stack chosen
		if (0 == stackChoosen || stackChoosen > stacks.length) {
			errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Enter a valid stack number", "Invalid Stack Number");
			facesContext.addMessage("InvalidStackNumber", errorMessage);

			return "";
		} else if (0 == stacks[stackChoosen - 1]) {
			errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"No tokens left in the stack" + stackChoosen, "Empty Stack");
			facesContext.addMessage("EmptyStack", errorMessage);

			return "";
		}

		// Validating the tokens removed
		if (0 == tokensRemoved) {
			errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"You should remove at least 1 token", "No tokens removed");
			facesContext.addMessage("noTokensRemoved", errorMessage);

			return "";
		} else if (tokensRemoved > 3) {
			errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"You can remove maximum of only 3 tokens only",
					"More tokens removed");
			facesContext.addMessage("moreTokensRemoved", errorMessage);

			return "";
		} else if (tokensRemoved > stacks[stackChoosen - 1]) {
			errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"You have only " + stacks[stackChoosen - 1]
							+ " tokens in stack" + (stackChoosen),
					"Invalid tokens removed");
			facesContext.addMessage("invalidTokensRemoved", errorMessage);

			return "";
		}

		// updating the state space
		stateSpace = gameBuilder.userMove(stackChoosen, tokensRemoved,
				stateSpace);
		// updating the stacks
		stacks = stateSpace.getStacks();
		// update the board display
		updateConfiguration(stateSpace);

		// Checking if the game is over
		int tokensRemaining = gameBuilder
				.getTokensCount(stateSpace.getStacks());
		if (0 == tokensRemaining) {
			// Game over -- computer wins
			gameOn = false;
			userWins = false;
			return "";
		}

		// Making a computer move
		computerMove();

		return "";
	}

	private void updateConfiguration(State currentState) {
		Stack stack = gameConfiguration.get(currentState.getStackChoosen());

		for (int i = 0, j = 0; i < currentState.getTokensRemoved()
				&& j < stack.getTokens().size(); j++) {
			if (!stack.getTokens().get(j).isRemoved()) {
				stack.getTokens().get(j).setRemoved(true);
				i++;
			}
		}

	}

	public String computerMove() {
		// Making the computer move
		stateSpace = gameBuilder.computerMove(stateSpace);
		stacks = stateSpace.getStacks();
		// update the board display
		updateConfiguration(stateSpace);

		// Updating the computer moves log
		String log = (getComputerMoveLog() == null ? "" : getComputerMoveLog())
				+ "\nStack: " + (stateSpace.getStackChoosen() + 1)
				+ "\t Tokens Removed: " + stateSpace.getTokensRemoved();

		setComputerMoveLog(log);

		// Checking if the game is over
		int tokensRemaining = gameBuilder
				.getTokensCount(stateSpace.getStacks());
		if (0 == tokensRemaining) {
			// Game over -- User wins
			gameOn = false;
			userWins = true;
			return "";
		}

		return "";
	}

	public void stacksCountListener() {
		System.out.println("in Ajax listner");
		if (numberOfStacks > 10) {
			numberOfStacks = 3;
			FacesContext.getCurrentInstance().addMessage(
					"InvalidNoOfStacks",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"You can have 1/2/3 stacks only",
							"Invalid No.of Stacks"));
			return;
		}

		if (numberOfStacks > 1) {
			renderTokenChoice = true;
			buildStackTokensList();
		} else {
			renderTokenChoice = false;
			renderMultipleStacks = false;
		}

	}

	public void tokensChoiceListener() {
		System.out.println("In Tokens choice listener");
		if (1 == equalTokens)
			renderMultipleStacks = false;
		else {
			renderMultipleStacks = true;
			buildStackTokensList();
		}
	}

	public String changeConfiguration() {
		System.out.println("Apply");

		/*if (true) {*/
			if (1 == equalTokens || numberOfStacks == 1) {
				stackTokensList = new ArrayList<Stack>();
				for (int i = 0; i < numberOfStacks; i++) {
					Stack tempStack = new Stack();
					tempStack.setStackNumber(i + 1);
					tempStack.setTokensCount(tokensInEachStack);
					stackTokensList.add(tempStack);
				}
			}

			buildStacks();
			buildGameConfiguration();

			return "startPage.xhtml";
		/*}else {
			return "";
		}*/
	}

	public boolean validateUpdateConfig() {
		FacesMessage errorMessage = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		boolean isValid = true;

		if (numberOfStacks == 1
				&& (tokensInEachStack < 5 || tokensInEachStack > 20)) {

			errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Enter tokens in 5 to 20 range", "Out of range tokens");
			facesContext.addMessage("outOfRangeTokens", errorMessage);

			isValid = false;

		} else if (numberOfStacks == 2) {
			if (equalTokens == 1
					&& (tokensInEachStack == 0 || tokensInEachStack > 7)) {

				errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Enter tokens in 1 to 7 range in each stack",
						"Out of range tokens");
				facesContext.addMessage("outOfRangeTokens", errorMessage);

				isValid = false;

			} else {
				for (int i = 0; i < stackTokensList.size(); i++) {
					if (stackTokensList.get(i).getTokensCount() == 0
							|| stackTokensList.get(i).getTokensCount() > 7) {

						errorMessage = new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Enter tokens in 1 to 7 range in stack " + (i+1),
								"Out of range tokens");
						facesContext.addMessage("outOfRangeTokens",
								errorMessage);

						isValid = false;

					}
				}
			}
		} else if (numberOfStacks == 3) {

			if (equalTokens == 1
					&& (tokensInEachStack == 0 || tokensInEachStack > 4)) {

				errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Enter tokens in 1 to 4 range in each stack",
						"Out of range tokens");
				facesContext.addMessage("outOfRangeTokens", errorMessage);

				isValid = false;

			} else {
				for (int i = 0; i < stackTokensList.size(); i++) {
					if (stackTokensList.get(i).getTokensCount() == 0
							|| stackTokensList.get(i).getTokensCount() > 4) {

						errorMessage = new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Enter tokens in 1 to 4 range in stack " + (i+1),
								"Out of range tokens");
						facesContext.addMessage("outOfRangeTokens",
								errorMessage);

						isValid = false;

					}
				}
			}

		}

		return isValid;

	}

	public String revertConfiguration() {
		System.out.println("Revert");

		numberOfStacks = stacks.length;
		stackTokensList = new ArrayList<Stack>();
		for (int i = 0; i < stacks.length; i++) {
			Stack tempStack = new Stack();
			tempStack.setStackNumber(i + 1);
			tempStack.setTokensCount(stacks[i]);
			stackTokensList.add(tempStack);
		}

		if (stackTokensList.size() == 1)
			renderTokenChoice = false;
		else
			renderTokenChoice = true;

		if (sameLengthStacks()) {
			renderMultipleStacks = false;
			equalTokens = 1;
			tokensInEachStack = stackTokensList.get(0).getTokensCount();
		}

		return "startPage.xhtml";
	}

	public String resetDefaultConfiguration() {
		System.out.println("Reset");

		defaultConfiguration();
		buildStacks();
		buildGameConfiguration();

		return "startPage.xhtml";
	}

	private boolean sameLengthStacks() {
		int tokenCount = stackTokensList.get(0).getTokensCount();
		for (int i = 1; i < stackTokensList.size(); i++) {
			if (tokenCount != stackTokensList.get(i).getTokensCount())
				return false;
		}
		return true;
	}

	public int getStackChoosen() {
		return stackChoosen;
	}

	public void setStackChoosen(int stackChoosen) {
		this.stackChoosen = stackChoosen;
	}

	public int getTokensRemoved() {
		return tokensRemoved;
	}

	public void setTokensRemoved(int tokensRemoved) {
		this.tokensRemoved = tokensRemoved;
	}

	public List<Stack> getGameConfiguration() {
		return gameConfiguration;
	}

	public void setGameConfiguration(List<Stack> gameConfiguration) {
		this.gameConfiguration = gameConfiguration;
	}

	public int getNumberOfStacks() {
		return numberOfStacks;
	}

	public void setNumberOfStacks(int numberOfStacks) {
		this.numberOfStacks = numberOfStacks;
	}

	public int getEqualTokens() {
		return equalTokens;
	}

	public void setEqualTokens(int equalTokens) {
		this.equalTokens = equalTokens;
	}

	public boolean isRenderTokenChoice() {
		return renderTokenChoice;
	}

	public void setRenderTokenChoice(boolean renderTokenChoice) {
		this.renderTokenChoice = renderTokenChoice;
	}

	public State getStateSpace() {
		return stateSpace;
	}

	public void setStateSpace(State stateSpace) {
		this.stateSpace = stateSpace;
	}

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

	public boolean isGameMode() {
		return gameMode;
	}

	public void setGameMode(boolean gameMode) {
		this.gameMode = gameMode;
	}

	public String getComputerMoveLog() {
		return computerMoveLog;
	}

	public void setComputerMoveLog(String computerMoveLog) {
		this.computerMoveLog = computerMoveLog;
	}

	public boolean isGameOn() {
		return gameOn;
	}

	public void setGameOn(boolean gameOn) {
		this.gameOn = gameOn;
	}

	public boolean isUserWins() {
		return userWins;
	}

	public void setUserWins(boolean userWins) {
		this.userWins = userWins;
	}

	public List<Stack> getStackTokensList() {
		return stackTokensList;
	}

	public void setStackTokensList(List<Stack> stackTokensList) {
		this.stackTokensList = stackTokensList;
	}

	public int getTokensInEachStack() {
		return tokensInEachStack;
	}

	public void setTokensInEachStack(int tokensInEachStack) {
		this.tokensInEachStack = tokensInEachStack;
	}

	public boolean isRenderMultipleStacks() {
		return renderMultipleStacks;
	}

	public void setRenderMultipleStacks(boolean renderMultipleStacks) {
		this.renderMultipleStacks = renderMultipleStacks;
	}

}
