package com.gameminers.kahur;

public enum WoodColor {
	OAK		(0xBC9862, "Oak"),
	BIRCH	(0xD7CB8D, "Birch"),
	SPRUCE	(0x805E36, "Spruce"),
	JUNGLE	(0xB88764, "Jungle"),
	ACACIA	(0xBA683B, "Acacia"),
	BIG_OAK	(0x492F17, "Dark Oak"),
	;
	private final int color;
	private final String friendlyName;
	private WoodColor(int color, String friendlyName) {
		this.color = color;
		this.friendlyName = friendlyName;
	}
	public int getColor() {
		return color;
	}
	public String getFriendlyName() {
		return friendlyName;
	}
}
