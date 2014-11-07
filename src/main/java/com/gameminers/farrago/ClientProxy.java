package com.gameminers.farrago;

import net.minecraft.client.gui.GuiIngame;

import com.gameminers.farrago.kahur.client.InitScreen;
import com.gameminers.farrago.pane.PaneOrbGlow;


public class ClientProxy implements Proxy {

	@Override
	public void postInit() {
		for (Iota sub : FarragoMod.getSubMods()) {
			sub.clientPostInit();
		}
		InitScreen.init();
	}

	@Override
	public void init() {
		//new PaneVanityArmor().autoOverlay(GuiInventory.class);
		new PaneOrbGlow().autoOverlay(GuiIngame.class);
	}

}
