package com.gameminers.farrago;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import com.gameminers.farrago.client.render.RenderBlunderbussProjectile;
import com.gameminers.farrago.entity.EntityBlunderbussProjectile;
import com.gameminers.farrago.kahur.client.InitScreen;
import com.gameminers.farrago.pane.PaneBranding;
import com.gameminers.farrago.pane.PaneOrbGlow;
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
		if (FarragoMod.brand != null) {
			new PaneBranding().autoOverlay(GuiMainMenu.class);
		}
		RenderingRegistry.registerEntityRenderingHandler(EntityBlunderbussProjectile.class, new RenderBlunderbussProjectile());
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

}
