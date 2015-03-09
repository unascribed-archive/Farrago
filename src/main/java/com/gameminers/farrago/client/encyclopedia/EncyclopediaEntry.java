package com.gameminers.farrago.client.encyclopedia;

import com.gameminers.farrago.selector.Selector;

public class EncyclopediaEntry {
	private final Selector selector;
	private final String body;
	public EncyclopediaEntry(Selector selector, String body) {
		this.selector = selector;
		this.body = body;
	}
	public boolean isIgnore() {
		return body == null;
	}
	public String getBody() {
		return body;
	}
	public Selector getSelector() {
		return selector;
	}
}
