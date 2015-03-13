package com.gameminers.farrago.proxy;

import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.client.effect.EntityRifleFX;
import com.gameminers.farrago.client.encyclopedia.Encyclopedia;
import com.gameminers.farrago.client.init.InitScreen;
import com.gameminers.farrago.client.render.RenderBlunderbussProjectile;
import com.gameminers.farrago.client.render.RenderNull;
import com.gameminers.farrago.client.render.RifleItemRenderer;
import com.gameminers.farrago.entity.EntityBlunderbussProjectile;
import com.gameminers.farrago.entity.EntityRifleProjectile;
import com.gameminers.farrago.enums.RifleMode;
import com.gameminers.farrago.pane.PaneBranding;
import com.gameminers.farrago.pane.PaneOrbGlow;
import com.gameminers.farrago.pane.PaneRifle;
import com.google.common.base.Charsets;

import cpw.mods.fml.client.registry.RenderingRegistry;


public class ClientProxy implements Proxy {

	@Override
	public void postInit() {
		InitScreen.init();
	}

	@Override
	public void init() {
		//new PaneVanityArmor().autoOverlay(GuiInventory.class);
		new PaneOrbGlow().autoOverlay(GuiIngame.class);
		new PaneRifle().autoOverlay(GuiIngame.class);
		if (FarragoMod.brand != null) {
			new PaneBranding().autoOverlay(GuiMainMenu.class);
		}
		RenderingRegistry.registerEntityRenderingHandler(EntityBlunderbussProjectile.class, new RenderBlunderbussProjectile());
		RenderingRegistry.registerEntityRenderingHandler(EntityRifleProjectile.class, new RenderNull());
		MinecraftForgeClient.registerItemRenderer(FarragoMod.RIFLE, new RifleItemRenderer());
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		if (manager instanceof IReloadableResourceManager) {
			((IReloadableResourceManager)manager).registerReloadListener(new Encyclopedia());
		}
	}

	@Override
	public void preInit() {
		File config = new File(Minecraft.getMinecraft().mcDataDir, "config");
		if (config.exists() && config.isDirectory()) {
			File brandFile = new File(config, "farrago-brand.txt");
			if (brandFile.exists()) {
				try {
					FarragoMod.brand = StringEscapeUtils.unescapeJava(FileUtils.readFileToString(brandFile, Charsets.UTF_8));
					FarragoMod.log.info("Brand loaded: "+FarragoMod.brand);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public void spawnRifleParticle(RifleMode mode, EntityRifleProjectile proj) {
		double stepSize = 0.5;
		boolean interpolate = true;
		if (Minecraft.getMinecraft().gameSettings.particleSetting == 1) {
			// Decreased
			stepSize = 1;
		} else if (Minecraft.getMinecraft().gameSettings.particleSetting == 2) {
			// Minimal
			interpolate = false;
		}
		double steps = interpolate ? (int)(distance(proj.lastTickPosX, proj.lastTickPosY, proj.lastTickPosZ, proj.posX, proj.posY, proj.posZ)/stepSize)-1 : 1;
		for (int i = 0; i < steps; i++) {
			double[] pos = interpolate(proj.lastTickPosX, proj.lastTickPosY, proj.lastTickPosZ, proj.posX, proj.posY, proj.posZ, i/steps);
			EntityRifleFX fx = new EntityRifleFX(proj.worldObj, pos[0], pos[1], pos[2], 0, 0, 0);
			fx.motionX = fx.motionY = fx.motionZ = 0;
			float r = ((mode.getColor() >> 16)&0xFF)/255f;
			float g = ((mode.getColor() >> 8)&0xFF)/255f;
			float b = (mode.getColor()&0xFF)/255f;
			fx.setRBGColorF(r, g, b);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private double[] interpolate(double x1, double y1, double z1, double x2, double y2, double z2, double factor) {
		return new double[] { 
				((1.0D - factor) * x1 + factor * x2),
				((1.0D - factor) * y1 + factor * y2),
				((1.0D - factor) * z1 + factor * z2)
		};
	}
	
	private double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double dX = x2 - x1;
        double dY = y2 - y1;
        double dZ = z2 - z1;
        return (double)MathHelper.sqrt_double(dX * dX + dY * dY + dZ * dZ);
	}

	@Override
	public void stopSounds() {
		try {
			Minecraft.getMinecraft().getSoundHandler().stopSounds();
		} catch (ConcurrentModificationException e) {}
	}

	@Override
	public void glowRandomDisplayTick(World world, int x, int y, int z, Random rand) {
		FarragoMod.GLOW.setBlockBoundsBasedOnState(world, x, y, z);
		float x1 = (float) FarragoMod.GLOW.getBlockBoundsMinX();
		float y1 = (float) FarragoMod.GLOW.getBlockBoundsMinY();
		float z1 = (float) FarragoMod.GLOW.getBlockBoundsMinZ();
		float x2 = (float) FarragoMod.GLOW.getBlockBoundsMaxX();
		float y2 = (float) FarragoMod.GLOW.getBlockBoundsMaxY();
		float z2 = (float) FarragoMod.GLOW.getBlockBoundsMaxZ();
		for (int i = 0; i < rand.nextInt(10)+5; i++) {
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityReddustFX(world, x+(x1+(rand.nextFloat()*(x2-x1))),
					y+(y1+(rand.nextFloat()*(y2-y1))), z+(z1+(rand.nextFloat()*(z2-z1))), 0.6f, 1.0f, 1.0f, 0.0f));
		}
	}

	@Override
	public void scope(EntityPlayer player) {
		if (FarragoMod.scopeTicks < 5) return;
		FarragoMod.scoped = !FarragoMod.scoped;
		FarragoMod.scopeTicks = 0;
		if (FarragoMod.scoped) {
			player.playSound("farrago:laser_scope", 1.0f, 1.0f);
		} else {
			player.playSound("farrago:laser_scope", 1.0f, 1.5f);
		}
	}

}
