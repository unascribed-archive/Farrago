package com.gameminers.farrago;

import gminers.kitchensink.Strings;

public enum RifleMode {
	RIFLE(1.75f, 1, 0xFF0000),
	BAZOOKA(1.1f, 1, 0xFF0000),
	SCATTER(1.0f, 1, 0xFF0000),
	
	BLAZE(1.8f, 6, 0xFFAA00),
	
	MINING(1.2f, 3, 0x00FFFF),
	PRECISION_MINING(2.0f, 3, 0x00FFFF),
	
	EXPLOSIVE(0.8f, 4, 0xAAAAAA),
	GLOW(2.0f, 2, 0xFFFF00),
	
	TELEPORT(0.5f, 5, 0xFF00FF);
	private final float chargeSpeed;
	private final String abbrev;
	private final String displayName;
	private final int cellType;
	private final int color;
	private RifleMode(float chargeSpeed, int cellType, int color) {
		this.chargeSpeed = chargeSpeed;
		this.cellType = cellType;
		this.color = color;
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
}
