package com.gameminers.farrago;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(name="Farrago",modid="farrago",dependencies="required-after:KitchenSink;after:GlassPane",version="${version}")
public class FarragoMod {
	private static final List<Iota> subMods = Lists.newArrayList();
	private static final Logger log = LogManager.getLogger("Farrago");
	@SidedProxy(clientSide="com.gameminers.farrago.ClientProxy", serverSide="com.gameminers.farrago.ServerProxy")
	public static Proxy proxy;
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		try {
			ClassPath path = ClassPath.from(FarragoMod.class.getClassLoader());
			for (ClassInfo info : path.getTopLevelClassesRecursive("com.gameminers.farrago")) {
				try {
					Class<?> clazz = Class.forName(info.getName(), false, getClass().getClassLoader());
					if (clazz.isAssignableFrom(Iota.class) && clazz != Iota.class) {
						log.info("Found Iota "+info);
						subMods.add((Iota)clazz.newInstance());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		for (Iota iota : subMods) {
			iota.init();
		}
	}
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		for (Iota iota : subMods) {
			iota.postInit();
		}
		proxy.postInit();
	}
	@EventHandler
	public void onAvailable(FMLLoadCompleteEvent e) {
		log.info("Farrago load completed without incident; loaded "+subMods.size()+" Iotas");
	}
	public static List<Iota> getSubMods() {
		return subMods;
	}
}
