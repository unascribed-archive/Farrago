package com.gameminers.farrago.enums;

import gminers.kitchensink.Strings;

public enum RifleMode {
	RIFLE(1.75f, 1, 0xFF0000, true),
	BAZOOKA(1.1f, 1, 0xFF0000, true),
	SCATTER(1.0f, 1, 0xFF0000, true),
	
	BLAZE(1.6f, 6, 0xFFAA00, true),
	
	MINING(1.2f, 3, 0x00FFFF, false),
	PRECISION_MINING(2.0f, 3, 0x00FFFF, false),
	
	EXPLOSIVE(1.5f, 4, 0xAAAAAA, false),
	GLOW(2.0f, 2, 0xFFFF00, true),
	
	TELEPORT(0.5f, 5, 0xFF00FF, true);
	private final float chargeSpeed;
	private final String abbrev;
	private final String displayName;
	private final int cellType;
	private final int color;
	private final boolean isShort;
	private RifleMode(float chargeSpeed, int cellType, int color, boolean isShort) {
		this.chargeSpeed = chargeSpeed;
		this.cellType = cellType;
		this.color = color;
		this.isShort = isShort;
		abbrev = Character.toString(name().charAt(0));
		displayName = Strings.formatTitleCase(name());
	}
	public int getColor() {
		return color;
	}
	public String getAbbreviation() {
		return abbrev;
	}
	public String getDisplayName() {
		return displayName;
	}
	public float getChargeSpeed() {
		return chargeSpeed;
	}
	public int getCellType() {
		return cellType;
	}
	public boolean isShort() {
		return isShort;
	}
}
