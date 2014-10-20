package com.gameminers.farrago;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gameminers.farrago.block.BlockCombustor;
import com.gameminers.farrago.item.ItemFondue;
import com.gameminers.farrago.kahur.KahurIota;
import com.gameminers.farrago.tileentity.TileEntityCombustor;
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

@Mod(name="Farrago",modid="farrago",dependencies="required-after:KitchenSink;after:GlassPane",version="0.6")
public class FarragoMod {
	private static final List<Iota> subMods = Lists.newArrayList();
	private static final Logger log = LogManager.getLogger("Farrago");
	@SidedProxy(clientSide="com.gameminers.farrago.ClientProxy", serverSide="com.gameminers.farrago.ServerProxy")
	public static Proxy proxy;
	@Instance("farrago")
	public static FarragoMod inst;
	public static BlockCombustor COMBUSTOR;
	public static Item CAQUELON;
	public static ItemFondue FONDUE;
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		subMods.add(new KahurIota());
	}
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new FarragoGuiHandler());
		COMBUSTOR = new BlockCombustor();
		CAQUELON = new Item().setTextureName("farrago:caquelon").setMaxStackSize(1).setUnlocalizedName("caquelon");
		FONDUE = new ItemFondue();
		GameRegistry.registerTileEntity(TileEntityCombustor.class, "FarragoCombustor");
		GameRegistry.registerBlock(COMBUSTOR, "combustor");
		GameRegistry.registerItem(CAQUELON, "caquelon");
		GameRegistry.registerItem(FONDUE, "fondue");
		GameRegistry.addRecipe(new ItemStack(FONDUE, 1, 0),
				"M",
				"C",
				'M', Items.milk_bucket,
				'C', CAQUELON
				);
		GameRegistry.addRecipe(new ItemStack(FONDUE, 1, 1),
				"cWB",
				" Cw",
				'W', Items.water_bucket,
				'w', Items.wheat,
				'c', Items.chicken,
				'B', Items.beef,
				'C', CAQUELON
				);
		GameRegistry.addRecipe(new ItemStack(FONDUE, 1, 2),
				"ccc",
				" C ",
				'c', new ItemStack(Items.dye, 1, 3),
				'C', CAQUELON
				);
		GameRegistry.addRecipe(new ShapedOreRecipe(CAQUELON, 
				"IBI",
				" I ",
				'I', "ingotIron",
				'B', Items.bucket
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(COMBUSTOR, 
				"III",
				"IBI",
				"IGI",
				'I', "ingotIron",
				'B', Blocks.iron_bars,
				'G', Items.gunpowder
				));
		for (Iota iota : subMods) {
			iota.init();
		}
		proxy.init();
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
