package edu.ua.ai.nim.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Stack implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Token> tokens;
	private Integer tokensCount;
	private Integer stackNumber;

	public List<Token> getTokens() {
		if (null == tokens)
			tokens = new ArrayList<Token>();

		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	public Integer getTokensCount() {
		return tokensCount;
	}

	public void setTokensCount(Integer tokensCount) {
		this.tokensCount = tokensCount;
	}

	public Integer getStackNumber() {
		return stackNumber;
	}

	public void setStackNumber(Integer stackNumber) {
		this.stackNumber = stackNumber;
	}

}
