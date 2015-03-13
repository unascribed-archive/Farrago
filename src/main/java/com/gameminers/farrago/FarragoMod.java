package com.gameminers.farrago;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.gameminers.farrago.block.BlockCombustor;
import com.gameminers.farrago.block.BlockGlow;
import com.gameminers.farrago.block.BlockMachine;
import com.gameminers.farrago.block.BlockOre;
import com.gameminers.farrago.block.BlockResource;
import com.gameminers.farrago.block.BlockScrapper;
import com.gameminers.farrago.block.item.ItemBlockWithCustomName;
import com.gameminers.farrago.client.encyclopedia.Encyclopedia;
import com.gameminers.farrago.entity.EntityBlunderbussProjectile;
import com.gameminers.farrago.entity.EntityKahurProjectile;
import com.gameminers.farrago.entity.EntityRifleProjectile;
import com.gameminers.farrago.enums.MineralColor;
import com.gameminers.farrago.enums.WoodColor;
import com.gameminers.farrago.gen.XenotimeGenerator;
import com.gameminers.farrago.gen.YttriumGenerator;
import com.gameminers.farrago.item.ItemFondue;
import com.gameminers.farrago.item.chromatic.Chromatics;
import com.gameminers.farrago.item.chromatic.ItemChromaticArmor;
import com.gameminers.farrago.item.chromatic.ItemChromaticAxe;
import com.gameminers.farrago.item.chromatic.ItemChromaticHoe;
import com.gameminers.farrago.item.chromatic.ItemChromaticPickaxe;
import com.gameminers.farrago.item.chromatic.ItemChromaticSpade;
import com.gameminers.farrago.item.chromatic.ItemChromaticSword;
import com.gameminers.farrago.item.resource.ItemApocite;
import com.gameminers.farrago.item.resource.ItemCell;
import com.gameminers.farrago.item.resource.ItemDust;
import com.gameminers.farrago.item.resource.ItemIngot;
import com.gameminers.farrago.item.resource.ItemRubble;
import com.gameminers.farrago.item.tool.ItemBlunderbuss;
import com.gameminers.farrago.item.tool.ItemKahur;
import com.gameminers.farrago.item.tool.ItemRifle;
import com.gameminers.farrago.item.tool.ItemVividOrb;
import com.gameminers.farrago.network.FarragoGuiHandler;
import com.gameminers.farrago.network.ModifyRifleModeHandler;
import com.gameminers.farrago.network.ModifyRifleModeMessage;
import com.gameminers.farrago.proxy.Proxy;
import com.gameminers.farrago.recipes.RecipeChromatic;
import com.gameminers.farrago.recipes.RecipesVividOrbDyes;
import com.gameminers.farrago.tileentity.TileEntityCellFiller;
import com.gameminers.farrago.tileentity.TileEntityCombustor;
import com.gameminers.farrago.tileentity.TileEntityScrapper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(name="Farrago",modid="farrago",dependencies="required-after:KitchenSink;after:GlassPane",version="1.0")
public class FarragoMod {
	public static final Logger log = LogManager.getLogger("Farrago");
	@SidedProxy(clientSide="com.gameminers.farrago.proxy.ClientProxy", serverSide="com.gameminers.farrago.proxy.ServerProxy")
	public static Proxy proxy;
	@Instance("farrago")
	public static FarragoMod inst;
	
	@Deprecated
	public static BlockCombustor COMBUSTOR;
	@Deprecated
	public static BlockScrapper SCRAPPER;
	public static BlockOre ORE;
	public static BlockResource RESOURCE;
	public static BlockGlow GLOW;
	public static BlockMachine MACHINE;
	
	public static ItemVividOrb VIVID_ORB;
	public static Item CAQUELON;
	public static ItemCell CELL;
	public static ItemRubble RUBBLE;
	public static ItemDust DUST;
	public static ItemIngot INGOT;
	public static ItemBlunderbuss BLUNDERBUSS;
	public static ItemFondue FONDUE;
	public static ItemRifle RIFLE;
	public static ItemKahur KAHUR;
	public static ItemApocite APOCITE;
	
	public static ItemChromaticPickaxe CHROMATIC_PICKAXE;
	public static ItemChromaticAxe CHROMATIC_AXE;
	public static ItemChromaticSword CHROMATIC_SWORD;
	public static ItemChromaticSpade CHROMATIC_SHOVEL;
	public static ItemChromaticHoe CHROMATIC_HOE;
	
	public static ItemChromaticArmor CHROMATIC_HELMET;
	public static ItemChromaticArmor CHROMATIC_CHESTPLATE;
	public static ItemChromaticArmor CHROMATIC_LEGGINGS;
	public static ItemChromaticArmor CHROMATIC_BOOTS;
	
	public static SimpleNetworkWrapper RIFLE_MODE_CHANNEL;
	
	public static Map<Long, List<IRecipe>> recipes = new HashMap<Long, List<IRecipe>>();
	public static String brand;
	public static boolean copperlessEnvironment;
	public static CreativeTabs creativeTab = new CreativeTabs("farrago") {
		private ItemStack iconItemStack;
		@Override
		public ItemStack getIconItemStack() {
			if (iconItemStack == null) {
				iconItemStack = new ItemStack(MACHINE, 1, 0);
			}
			return iconItemStack;
		}
		@Override
		public Item getTabIconItem() {
			return null;
		}
	};
	public static boolean scoped;
	public static int scopeTicks;
	private YttriumGenerator yttrGen;
	private XenotimeGenerator xenoGen;
	
	private boolean linuxNag = true;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		proxy.preInit();
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new FarragoGuiHandler());
		RIFLE_MODE_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("Farrago");
		RIFLE_MODE_CHANNEL.registerMessage(ModifyRifleModeHandler.class, ModifyRifleModeMessage.class, 0, Side.SERVER);
		
		COMBUSTOR = new BlockCombustor();
		SCRAPPER = new BlockScrapper();
		ORE = new BlockOre();
		GLOW = new BlockGlow();
		RESOURCE = new BlockResource();
		MACHINE = new BlockMachine();
		
		CAQUELON = new Item().setTextureName("farrago:caquelon").setMaxStackSize(1).setUnlocalizedName("caquelon").setCreativeTab(creativeTab);
		VIVID_ORB = new ItemVividOrb();
		RUBBLE = new ItemRubble();
		BLUNDERBUSS = new ItemBlunderbuss();
		DUST = new ItemDust();
		INGOT = new ItemIngot();
		FONDUE = new ItemFondue();
		RIFLE = new ItemRifle();
		CELL = new ItemCell();
		KAHUR = new ItemKahur();
		APOCITE = new ItemApocite();
		
		CHROMATIC_PICKAXE = new ItemChromaticPickaxe();
		CHROMATIC_AXE = new ItemChromaticAxe().setUnlocalizedName("chromatic_axe");
		CHROMATIC_SWORD = new ItemChromaticSword().setUnlocalizedName("chromatic_sword");
		CHROMATIC_SHOVEL = new ItemChromaticSpade().setUnlocalizedName("chromatic_shovel");
		CHROMATIC_HOE = new ItemChromaticHoe().setUnlocalizedName("chromatic_hoe");
		
		CHROMATIC_HELMET = new ItemChromaticArmor(0).setTextureName("farrago:chromatic_helmet").setUnlocalizedName("chromatic_helmet");
		CHROMATIC_CHESTPLATE = new ItemChromaticArmor(1).setTextureName("farrago:chromatic_chestplate").setUnlocalizedName("chromatic_chestplate");
		CHROMATIC_LEGGINGS = new ItemChromaticArmor(2).setTextureName("farrago:chromatic_leggings").setUnlocalizedName("chromatic_leggings");
		CHROMATIC_BOOTS = new ItemChromaticArmor(3).setTextureName("farrago:chromatic_boots").setUnlocalizedName("chromatic_boots");
		
		
		GameRegistry.registerFuelHandler(new IFuelHandler() {
			private Random rand = new Random();
			@Override
			public int getBurnTime(ItemStack fuel) {
				if (fuel != null && fuel.getItem() == RUBBLE && fuel.getItemDamage() < 5) {
					if (fuel.stackSize/3 == rand.nextInt(16)) {
						fuel.setItemDamage(5);
					}
					return rand.nextInt(30)+5;
				}
				return 0;
			}
		});
		GameRegistry.registerTileEntity(TileEntityCombustor.class, "FarragoCombustor");
		GameRegistry.registerTileEntity(TileEntityScrapper.class, "FarragoScrapper");
		GameRegistry.registerTileEntity(TileEntityCellFiller.class, "FarragoCellFiller");
		
		GameRegistry.registerBlock(GLOW, null, "glow");
		GameRegistry.registerBlock(COMBUSTOR, "combustor");
		GameRegistry.registerBlock(SCRAPPER, "scrapper");
		GameRegistry.registerBlock(MACHINE, ItemBlockWithCustomName.class, "machine");
		GameRegistry.registerBlock(ORE, ItemBlockWithCustomName.class, "watashi");
		GameRegistry.registerBlock(RESOURCE, ItemBlockWithCustomName.class, "resource");
		
		
		GameRegistry.registerItem(BLUNDERBUSS, "blunderbuss");
		GameRegistry.registerItem(CAQUELON, "caquelon");
		GameRegistry.registerItem(RUBBLE, "rubble");
		GameRegistry.registerItem(DUST, "dust");
		GameRegistry.registerItem(INGOT, "ingot");
		GameRegistry.registerItem(FONDUE, "fondue");
		GameRegistry.registerItem(VIVID_ORB, "vividOrb");
		GameRegistry.registerItem(CELL, "cell");
		GameRegistry.registerItem(RIFLE, "rifle");
		GameRegistry.registerItem(KAHUR, "kahur");
		GameRegistry.registerItem(APOCITE, "apocite");
		
		GameRegistry.registerItem(CHROMATIC_PICKAXE, "chromaticPickaxe");
		GameRegistry.registerItem(CHROMATIC_AXE, "chromaticAxe");
		GameRegistry.registerItem(CHROMATIC_SWORD, "chromaticSword");
		GameRegistry.registerItem(CHROMATIC_SHOVEL, "chromaticShovel");
		GameRegistry.registerItem(CHROMATIC_HOE, "chromaticHoe");
		
		GameRegistry.registerItem(CHROMATIC_HELMET, "chromaticHelmet");
		GameRegistry.registerItem(CHROMATIC_CHESTPLATE, "chromaticChestplate");
		GameRegistry.registerItem(CHROMATIC_LEGGINGS, "chromaticLeggings");
		GameRegistry.registerItem(CHROMATIC_BOOTS, "chromaticBoots");
		
		EntityRegistry.registerModEntity(EntityKahurProjectile.class, "kahurShot", 0, this, 64, 12, true);
		EntityRegistry.registerModEntity(EntityRifleProjectile.class, "rifleShot", 1, this, 64, 12, true);
		EntityRegistry.registerModEntity(EntityBlunderbussProjectile.class, "blunderbussShot", 2, this, 64, 12, true);
		ORE.registerOres();
		DUST.registerOres();
		INGOT.registerOres();
		RESOURCE.registerOres();
		for (String s : new String[] {ChestGenHooks.DUNGEON_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST, ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.MINESHAFT_CORRIDOR}) {
			for (int color : new int[] {0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0xFF00FF, 0x00FFFF}) {
				ItemStack orb = new ItemStack(VIVID_ORB);
				VIVID_ORB.setColor(orb, color);
				ChestGenHooks.addItem(s, new WeightedRandomChestContent(orb, 0, 2, 25));
			}
		}
		GameRegistry.addRecipe(new RecipesVividOrbDyes());
		GameRegistry.addSmelting(new ItemStack(DUST, 1, 0), new ItemStack(Items.iron_ingot), 0);
		GameRegistry.addSmelting(new ItemStack(DUST, 1, 1), new ItemStack(Items.gold_ingot), 0);
		GameRegistry.addSmelting(new ItemStack(DUST, 1, 2), new ItemStack(Items.emerald), 0);
		GameRegistry.addSmelting(new ItemStack(DUST, 1, 3), new ItemStack(Items.diamond), 0);
		GameRegistry.addSmelting(new ItemStack(DUST, 1, 5), new ItemStack(INGOT, 1, 0), 0);
		GameRegistry.addSmelting(new ItemStack(DUST, 1, 6), new ItemStack(INGOT, 1, 1), 0);
		GameRegistry.addSmelting(new ItemStack(DUST, 1, 7), new ItemStack(INGOT, 1, 2), 0);
		GameRegistry.addSmelting(new ItemStack(DUST, 1, 8), new ItemStack(Items.ender_pearl), 0);
		GameRegistry.addSmelting(new ItemStack(ORE, 1, 0), new ItemStack(INGOT, 1, 0), 0);
		GameRegistry.registerWorldGenerator(yttrGen = new YttriumGenerator(), 5);
		GameRegistry.registerWorldGenerator(xenoGen = new XenotimeGenerator(), 4);
		
		RecipeSorter.register("farrago:chromatic_shaped", RecipeChromatic.class, Category.SHAPED, "before:minecraft:shapeless");
		RecipeSorter.register("farrago:vivid_orb_dyes", RecipesVividOrbDyes.class, Category.SHAPELESS, "after:minecraft:shapeless");
		
		OreDictionary.registerOre("dyeRed", new ItemStack(DUST, 1, 5));
		OreDictionary.registerOre("gemEnderPearl", Items.ender_pearl);
		OreDictionary.registerOre("gemNetherStar", Items.nether_star);
		OreDictionary.registerOre("gemApocite", APOCITE);
		
		RESOURCE.registerRecipes();
		
		ItemStack eow = new ItemStack(CHROMATIC_SWORD);
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList ench = new NBTTagList();
		{
	        NBTTagCompound enchCompound = new NBTTagCompound();
	        enchCompound.setShort("id", (short)Enchantment.sharpness.effectId);
	        enchCompound.setShort("lvl", (short)2149);
	        ench.appendTag(enchCompound);
		}
		{
	        NBTTagCompound enchCompound = new NBTTagCompound();
	        enchCompound.setShort("id", (short)Enchantment.looting.effectId);
	        enchCompound.setShort("lvl", (short)10);
	        ench.appendTag(enchCompound);
		}
		{
	        NBTTagCompound enchCompound = new NBTTagCompound();
	        enchCompound.setShort("id", (short)Enchantment.knockback.effectId);
	        enchCompound.setShort("lvl", (short)12);
	        ench.appendTag(enchCompound);
		}
        compound.setTag("ench", ench);
		compound.setBoolean("Unbreakable", true);
		eow.setTagCompound(compound);
		Chromatics.setColor(eow, 0xFF0000);
		eow.setStackDisplayName("\u00A7cEater of Worlds");
		GameRegistry.addShapelessRecipe(new ItemStack(MACHINE, 1, 0), COMBUSTOR);
		GameRegistry.addShapelessRecipe(new ItemStack(MACHINE, 1, 1), SCRAPPER);
		GameRegistry.addRecipe(new RecipeChromatic(eow,
				"DVD",
				"+y+",
				"d+d",
				'D', new ItemStack(DUST, 1, 4),
				'y', "blockYttriumCopper",
				'd', "blockDiamond",
				'+', "blockNetherStar",
				'V', VIVID_ORB
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(VIVID_ORB),
				"QIQ",
				"IEI",
				"QIQ",
				'E', "gemEnderPearl",
				'I', "ingotIron",
				'Q', "gemQuartz"
				));
		GameRegistry.addRecipe(new ItemStack(FONDUE, 1, 3),
				"A",
				"C",
				'A', Items.apple,
				'C', CAQUELON
				);
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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MACHINE, 1, 0),
				"III",
				"IBI",
				"IGI",
				'I', "ingotYttrium",
				'B', Blocks.iron_bars,
				'G', Items.gunpowder
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MACHINE, 1, 1),
				"III",
				"QPQ",
				"BDB",
				'I', "ingotYttrium",
				'Q', "gemQuartz",
				'B', "blockYttrium",
				'D', "gemDiamond",
				'P', "ingotYttriumCopper"
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(BLUNDERBUSS, 
				" I ",
				"IYG",
				" BC",
				'I', "ingotIron",
				'Y', "ingotYttrium",
				'C', "ingotYttriumCopper",
				'G', Items.gunpowder,
				'B', Items.blaze_rod
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(RIFLE, 
				"D  ",
				" C ",
				" BC",
				'D', "gemApocite",
				'C', "ingotYttriumCopper",
				'B', Items.blaze_rod
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CELL, 4, 0), 
				"YGY",
				"YGY",
				"YCY",
				'Y', "ingotYttrium",
				'C', "ingotYttriumCopper",
				'G', "paneGlass"
				));
		GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_PICKAXE,
				"III",
				" / ",
				" V ",
				'I', "ingotYttrium",
				'V', VIVID_ORB,
				'/', "stickWood"
				));
		ItemStack diaChromaticPick = new ItemStack(CHROMATIC_PICKAXE);
		diaChromaticPick.setTagCompound(new NBTTagCompound());
		diaChromaticPick.getTagCompound().setBoolean("Diamond", true);
		GameRegistry.addRecipe(new RecipeChromatic(diaChromaticPick,
				"III",
				"D/D",
				" V ",
				'I', "ingotYttrium",
				'V', VIVID_ORB,
				'/', "stickWood",
				'D', "gemDiamond"
				));
		GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_AXE,
				"II ",
				"I/ ",
				" V ",
				'I', "ingotYttrium",
				'V', VIVID_ORB,
				'/', "stickWood"
				));
		GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_AXE,
				" II",
				" /I",
				" V ",
				'I', "ingotYttrium",
				'V', VIVID_ORB,
				'/', "stickWood"
				));
		GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_SWORD,
				" I ",
				" I ",
				" V ",
				'I', "ingotYttrium",
				'V', VIVID_ORB
				));
		GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_SHOVEL,
				" I ",
				" / ",
				" V ",
				'I', "ingotYttrium",
				'V', VIVID_ORB,
				'/', "stickWood"
				));
		GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_HOE,
				"II ",
				" / ",
				" V ",
				'I', "ingotYttrium",
				'V', VIVID_ORB,
				'/', "stickWood"
				));
		GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_HOE,
				" II",
				" / ",
				" V ",
				'I', "ingotYttrium",
				'V', VIVID_ORB,
				'/', "stickWood"
				));
		GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_HELMET,
				"IVI",
				"I I",
				'I', "ingotYttrium",
				'V', VIVID_ORB
				));
		GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_CHESTPLATE,
				"I I",
				"IVI",
				"III",
				'I', "ingotYttrium",
				'V', VIVID_ORB
				));
		GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_LEGGINGS,
				"IVI",
				"I I",
				"I I",
				'I', "ingotYttrium",
				'V', VIVID_ORB
				));
		GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_BOOTS,
				"I I",
				"IVI",
				'I', "ingotYttrium",
				'V', VIVID_ORB
				));
		ItemStack stableApocite = new ItemStack(APOCITE, 1, 0);
		stableApocite.setTagCompound(new NBTTagCompound());
		stableApocite.getTagCompound().setBoolean("Stable", true);
		GameRegistry.addRecipe(new ShapelessOreRecipe(stableApocite, "gemApocite", "dustEnderPearl", "dustEmerald", "dustRedstone"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 1, 1),  new ItemStack(CELL, 1, 0), "dustCopper", "dustRedstone"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 1, 2),  new ItemStack(CELL, 1, 0), "dustYttrium", "dustGlowstone"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 2, 3),  new ItemStack(CELL, 1, 0), new ItemStack(CELL, 1, 0), "dustGold", "dustGold", "dustDiamond"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 1, 4),  new ItemStack(CELL, 1, 0), "dustIron", Items.gunpowder));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 1, 5),  new ItemStack(CELL, 1, 0), "dustEnderPearl", "dustEmerald"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 1, 6),  new ItemStack(CELL, 1, 0), "dustCopper", "dustRedstone", Items.blaze_powder));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(DUST, 2, 6), "dustCopper", "dustYttrium"));
		for (WoodColor body : WoodColor.values()) {
			for (WoodColor drum : WoodColor.values()) {
				for (MineralColor pump : MineralColor.values()) {
					if (pump.getSelector().getRepresentation() == null) continue;
					ItemStack kahur = new ItemStack(KAHUR);
					NBTTagCompound tag = new NBTTagCompound();
					tag.setString("KahurBodyMaterial", body.name());
					tag.setString("KahurDrumMaterial", drum.name());
					tag.setString("KahurPumpMaterial", pump.name());
					kahur.setTagCompound(tag);
					GameRegistry.addRecipe(new ShapedOreRecipe(kahur,
							"B  ",
							"PD ",
							" /B",
							'B', new ItemStack(Blocks.planks, 1, body.ordinal()),
							'D', new ItemStack(Blocks.planks, 1, drum.ordinal()),
							'P', pump.getSelector().getRepresentation(),
							'/', "stickWood"));
					GameRegistry.addRecipe(new ShapedOreRecipe(kahur,
							"  B",
							" DP",
							"B/ ",
							'B', new ItemStack(Blocks.planks, 1, body.ordinal()),
							'D', new ItemStack(Blocks.planks, 1, drum.ordinal()),
							'P', pump.getSelector().getRepresentation(),
							'/', "stickWood"));
				}
			}
		}
		for (IRecipe recipe : (List<IRecipe>)CraftingManager.getInstance().getRecipeList()) {
			if (recipe == null) continue;
			ItemStack out = recipe.getRecipeOutput();
			if (out == null) continue;
			List<IRecipe> list;
			Long hash = hashItemStack(out);
			if (recipes.containsKey(hash)) {
				list = recipes.get(hash);
			} else {
				list = new ArrayList<IRecipe>();
				recipes.put(hash, list);
			}
			list.add(recipe);
		}
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
		proxy.init();
	}
	@SubscribeEvent
	public void onLightning(EntityStruckByLightningEvent e) {
		if (e.entity.worldObj.isRemote) return;
		if (e.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.entity;
			boolean sting = false;
			for (ItemStack is : player.inventory.mainInventory) {
				if (is == null) continue;
				if (is.getItem() == FONDUE) {
					if (is.getItemDamage() == 3) {
						is.setItemDamage(4);
						sting = true;
					}
				}
			}
			if (sting) {
				player.worldObj.playSoundAtEntity(player, "farrago:cyber_sting", 0.5f, 1.0f);
			}
		}
	}
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e) {
		if (e.phase == Phase.START) {
			if (scoped) {
				if (Minecraft.getMinecraft().thePlayer == null) {
					scoped = false;
					return;
				}
				if (Minecraft.getMinecraft().thePlayer.getHeldItem() == null) {
					scoped = false;
					return;
				}
				if (Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() != RIFLE) {
					scoped = false;
					return;
				}
				scopeTicks++;
			} else {
				scopeTicks = 0;
			}
		}
	}
	@SubscribeEvent
	public void onFov(FOVUpdateEvent e) {
		if (scoped) {
			e.newfov = 0.1f;
		}
	}
	@SubscribeEvent
	public void onKeyboardInput(KeyInputEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer != null) {
			if (mc.thePlayer.isSneaking()) {
				if (mc.thePlayer.getHeldItem() != null) {
					ItemStack held = mc.thePlayer.getHeldItem();
					if (held.getItem() == FarragoMod.RIFLE) {
						// on Linux, Shift+2 and Shift+6 do not work. This is an LWJGL bug.
						// This is a QWERTY-only workaround.
						if (SystemUtils.IS_OS_LINUX) {
							if (linuxNag) {
								log.warn("We are running on Linux. Due to a bug in LWJGL, Shift+2 and Shift+6 do not work "+
											"properly. Activating workaround. This may cause strange issues and is only "+
											"confirmed to work with QWERTY keyboards. This message is only shown once.");
								linuxNag = false;
							}
							if (Keyboard.getEventCharacter() == '@') {
								while (mc.gameSettings.keyBindsHotbar[1].isPressed()) {}
								RIFLE_MODE_CHANNEL.sendToServer(new ModifyRifleModeMessage(true, 1));
								return;
							}
							if (Keyboard.getEventCharacter() == '^') {
								while (mc.gameSettings.keyBindsHotbar[5].isPressed()) {}
								RIFLE_MODE_CHANNEL.sendToServer(new ModifyRifleModeMessage(true, 5));
								return;
							}
						}
						for (int i = 0; i < 9; i++) {
							if (mc.gameSettings.keyBindsHotbar[i].isPressed()) {
								while (mc.gameSettings.keyBindsHotbar[i].isPressed()) {} // drain pressTicks to zero to suppress vanilla behavior
								RIFLE_MODE_CHANNEL.sendToServer(new ModifyRifleModeMessage(true, i));
							}
						}
						return;
					}
				}
			}
		}
	}
	@SubscribeEvent
	public void onMouseInput(MouseInputEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer != null) {
			int dWheel = Mouse.getEventDWheel();
			mc.thePlayer.inventory.changeCurrentItem(dWheel*-1);
			if (dWheel != 0) {
				if (mc.thePlayer.isSneaking()) {
					if (mc.thePlayer.getHeldItem() != null) {
						ItemStack held = mc.thePlayer.getHeldItem();
						if (held.getItem() == FarragoMod.RIFLE) {
							if (dWheel > 0) {
								dWheel = 1;
							}
							if (dWheel < 0) {
								dWheel = -1;
							}
							RIFLE_MODE_CHANNEL.sendToServer(new ModifyRifleModeMessage(false, dWheel*-1));
							return;
						}
					}
				}
			}
			mc.thePlayer.inventory.changeCurrentItem(dWheel);
		}
	}
	private Deque<GenData> chunksToGen = new ArrayDeque<GenData>();
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e) {
		if (e.showAdvancedItemTooltips) {
			float massF = Masses.getMass(e.itemStack);
			String massS = (massF <= 0 ? "\u00A7cERROR\u00A79" : ""+massF);
			float magic = Masses.getMagic(e.itemStack);
			if (magic > 0) {
				e.toolTip.add("\u00A79"+magic+" Kahur Magic");
			}
			e.toolTip.add("\u00A79"+massS+" Kahur Mass");
		}
		if (e.itemStack.getItem() == Item.getItemFromBlock(COMBUSTOR) || e.itemStack.getItem() == Item.getItemFromBlock(SCRAPPER)) {
			e.toolTip.add("\u00A74DEPRECATED. Place in a crafting window to update.");
			e.toolTip.add("\u00A7cIF THIS ITEM IS NOT UPDATED IT WILL BE LOST IN FARRAGO 1.1");
		}
		Encyclopedia.process(e.itemStack, e.entityPlayer, e.toolTip, e.showAdvancedItemTooltips);
	}
	public void onDataLoad(ChunkDataEvent.Load e) {
		if (!e.getChunk().isTerrainPopulated) return;
		String gen = e.getData().getString("farrago:RetroGenKey");
		if (gen == null || gen.isEmpty()) {
			chunksToGen.addLast(new GenData(e.getChunk(), 0));
			chunksToGen.addLast(new GenData(e.getChunk(), 1));
		} else if ("yttrium".equals(gen)) {
			chunksToGen.addLast(new GenData(e.getChunk(), 1));
		} else if ("xenotime".equals(gen)) {
			// future
		}
	}
	@SubscribeEvent
	public void onDataSave(ChunkDataEvent.Save e) {
		e.getData().setString("farrago:RetroGenKey", "xenotime");
	}
	@SubscribeEvent
	public void onTick(ServerTickEvent e) {
		if (e.phase == Phase.END) {
			if (!chunksToGen.isEmpty()) {
				GenData data = chunksToGen.pop();
				Chunk chunk = data.chunk;
				if (data.level == 0) {
					yttrGen.generate(chunk.worldObj.rand, chunk.xPosition, chunk.zPosition, chunk.worldObj, null, null);
				} else if (data.level == 1) {
					xenoGen.generate(chunk.worldObj.rand, chunk.xPosition, chunk.zPosition, chunk.worldObj, null, null);
				}
				chunk.setChunkModified();
				log.info("Retrogenerating "+chunk.xPosition+", "+chunk.zPosition);
			}
		}
	}
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		proxy.postInit();
		if (OreDictionary.getOres("ingotCopper").size() <= 1) {
			copperlessEnvironment = true;
			log.warn("We are running in a copperless environment; enabling fallback copper dust drops from Yttrium ore");
		}
	}
	public static long hashItemStack(ItemStack toHash) {
		if (toHash == null) return 0;
		long hash = 0;
		hash |= (toHash.getItemDamage() & Short.MAX_VALUE) << Short.SIZE;
		hash |= (Item.getIdFromItem(toHash.getItem()) & Short.MAX_VALUE);
		if (toHash.hasTagCompound()) {
			hash |= (toHash.getTagCompound().hashCode() << 32);
		}
		return hash;
	}
}
