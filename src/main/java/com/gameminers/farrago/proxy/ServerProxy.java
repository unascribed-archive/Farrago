package com.gameminers.farrago.proxy;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.world.World;

import com.gameminers.farrago.Masses;
import com.gameminers.farrago.entity.EntityRifleProjectile;
import com.gameminers.farrago.enums.RifleMode;

import cpw.mods.fml.common.registry.GameData;

public class ServerProxy implements Proxy {

	@Override public void postInit() {
		for (Object o : GameData.getItemRegistry()) {
			if (o instanceof Item) { // should always be true, but just to be sure
				Item i = (Item) o;
				try {
					Masses.calculateMass(i, 0, 32767);
				} catch (StackOverflowError error) {
					continue;
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Dazed and confused, but trying to continue");
					continue;
				}
			}
		}
		Masses.bake();
	}
	@Override public void init() {}
	@Override public void preInit() {}
	@Override public void spawnRifleParticle(RifleMode mode, EntityRifleProjectile proj) {}
	@Override public void stopSounds() {}
	@Override public void glowRandomDisplayTick(World world, int x, int y, int z,Random rand) {}

}
