package com.gameminers.kahur;

import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameData;

public class ServerProxy implements Proxy {

	@Override
	public void init() {
		for (Object o : GameData.getItemRegistry()) {
			if (o instanceof Item) { // should always be true, but just to be sure
				Item i = (Item) o;
				try {
					KahurMod.calculateMass(i, 0, 32767);
				} catch (StackOverflowError error) {
					continue;
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Dazed and confused, but trying to continue");
					continue;
				}
			}
		}
		KahurMod.bake();
	}

}
