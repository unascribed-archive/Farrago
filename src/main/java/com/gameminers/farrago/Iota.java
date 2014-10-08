package com.gameminers.farrago;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface Iota {
	void init();
	void postInit();
	@SideOnly(Side.CLIENT)
	void clientPostInit();
	@SideOnly(Side.SERVER)
	void serverPostInit();
}
