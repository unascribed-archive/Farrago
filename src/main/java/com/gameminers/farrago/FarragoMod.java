package com.gameminers.farrago;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gameminers.farrago.block.BlockCombustor;
import com.gameminers.farrago.kahur.KahurIota;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(name="Farrago",modid="farrago",dependencies="required-after:KitchenSink;after:GlassPane",version="${version}")
public class FarragoMod {
	private static final List<Iota> subMods = Lists.newArrayList();
	private static final Logger log = LogManager.getLogger("Farrago");
	@SidedProxy(clientSide="com.gameminers.farrago.ClientProxy", serverSide="com.gameminers.farrago.ServerProxy")
	public static Proxy proxy;
	@Instance("farrago")
	public static FarragoMod inst;
	public static BlockCombustor COMBUSTOR;
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		subMods.add(new KahurIota());
	}
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		for (Iota iota : subMods) {
			iota.init();
		}
		proxy.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new FarragoGuiHandler());
		COMBUSTOR = new BlockCombustor();
		GameRegistry.registerBlock(COMBUSTOR, "combustor");
		GameRegistry.addRecipe(new ShapedOreRecipe(COMBUSTOR, 
				"III",
				"IBI",
				"IGI",
				'I', "ingotIron",
				'B', Blocks.iron_bars,
				'G', Items.gunpowder
				));
		
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
	protected static List<Iota> getSubMods() {
		return subMods;
	}
}
