package edu.ua.ai.nim.util;

import java.io.Serializable;

public class Token implements Serializable {

	private static final long serialVersionUID = 1L;
	private int tokenId;
	private boolean removed;

	public int getTokenId() {
		return tokenId;
	}

	public void setTokenId(int tokenId) {
		this.tokenId = tokenId;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

}
