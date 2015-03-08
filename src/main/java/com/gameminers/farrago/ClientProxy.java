package com.gameminers.farrago;

import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.MinecraftForgeClient;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import com.gameminers.farrago.client.effect.EntityRifleFX;
import com.gameminers.farrago.client.render.RenderBlunderbussProjectile;
import com.gameminers.farrago.client.render.RenderNull;
import com.gameminers.farrago.client.render.RifleItemRenderer;
import com.gameminers.farrago.entity.EntityBlunderbussProjectile;
import com.gameminers.farrago.entity.EntityRifleProjectile;
import com.gameminers.farrago.kahur.client.InitScreen;
import com.gameminers.farrago.pane.PaneBranding;
import com.gameminers.farrago.pane.PaneOrbGlow;
import com.gameminers.farrago.pane.PaneRifle;
import com.google.common.base.Charsets;

import cpw.mods.fml.client.registry.RenderingRegistry;


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
		new PaneRifle().autoOverlay(GuiIngame.class);
		if (FarragoMod.brand != null) {
			new PaneBranding().autoOverlay(GuiMainMenu.class);
		}
		RenderingRegistry.registerEntityRenderingHandler(EntityBlunderbussProjectile.class, new RenderBlunderbussProjectile());
		RenderingRegistry.registerEntityRenderingHandler(EntityRifleProjectile.class, new RenderNull());
		MinecraftForgeClient.registerItemRenderer(FarragoMod.RIFLE, new RifleItemRenderer());
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
		EntityRifleFX fx = new EntityRifleFX(proj.worldObj, proj.posX, proj.posY, proj.posZ, 0, 0, 0);
		fx.motionX = fx.motionY = fx.motionZ = 0;
		float r = ((mode.getColor() >> 16)&0xFF)/255f;
		float g = ((mode.getColor() >> 8)&0xFF)/255f;
		float b = (mode.getColor()&0xFF)/255f;
		fx.setRBGColorF(r, g, b);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public void stopSounds() {
		try {
			Minecraft.getMinecraft().getSoundHandler().stopSounds();
		} catch (ConcurrentModificationException e) {}
	}

}
