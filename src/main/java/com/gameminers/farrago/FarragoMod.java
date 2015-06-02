package com.gameminers.farrago;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.chunk.Chunk;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gameminers.farrago.block.BlockCollector;
import com.gameminers.farrago.block.BlockCombustor;
import com.gameminers.farrago.block.BlockFarragoStairs;
import com.gameminers.farrago.block.BlockGlow;
import com.gameminers.farrago.block.BlockLightPipe;
import com.gameminers.farrago.block.BlockMachine;
import com.gameminers.farrago.block.BlockOre;
import com.gameminers.farrago.block.BlockResource;
import com.gameminers.farrago.block.BlockScrapper;
import com.gameminers.farrago.block.item.ItemBlockWithCustomName;
import com.gameminers.farrago.entity.EntityBlunderbussProjectile;
import com.gameminers.farrago.entity.EntityKahurProjectile;
import com.gameminers.farrago.entity.EntityRifleProjectile;
import com.gameminers.farrago.enums.WoodColor;
import com.gameminers.farrago.gen.XenotimeGenerator;
import com.gameminers.farrago.gen.YttriumGenerator;
import com.gameminers.farrago.item.ItemFondue;
import com.gameminers.farrago.item.ItemUndefined;
import com.gameminers.farrago.item.ItemUtilityBelt;
import com.gameminers.farrago.item.chromatic.ItemChromaticArmor;
import com.gameminers.farrago.item.chromatic.ItemChromaticAxe;
import com.gameminers.farrago.item.chromatic.ItemChromaticHoe;
import com.gameminers.farrago.item.chromatic.ItemChromaticPickaxe;
import com.gameminers.farrago.item.chromatic.ItemChromaticSpade;
import com.gameminers.farrago.item.chromatic.ItemChromaticSword;
import com.gameminers.farrago.item.gun.ItemBlunderbuss;
import com.gameminers.farrago.item.gun.ItemKahur;
import com.gameminers.farrago.item.gun.ItemMinigun;
import com.gameminers.farrago.item.gun.ItemRifle;
import com.gameminers.farrago.item.modular.ItemModularChestplate;
import com.gameminers.farrago.item.resource.ItemApocite;
import com.gameminers.farrago.item.resource.ItemCell;
import com.gameminers.farrago.item.resource.ItemCrafting;
import com.gameminers.farrago.item.resource.ItemDust;
import com.gameminers.farrago.item.resource.ItemIngot;
import com.gameminers.farrago.item.resource.ItemMinigunCell;
import com.gameminers.farrago.item.resource.ItemRubble;
import com.gameminers.farrago.item.resource.ItemVividOrb;
import com.gameminers.farrago.network.ChangeSelectedHotbarHandler;
import com.gameminers.farrago.network.ChangeSelectedHotbarMessage;
import com.gameminers.farrago.network.FarragoGuiHandler;
import com.gameminers.farrago.network.LockSlotHandler;
import com.gameminers.farrago.network.LockSlotMessage;
import com.gameminers.farrago.network.ModifyRifleModeHandler;
import com.gameminers.farrago.network.ModifyRifleModeMessage;
import com.gameminers.farrago.network.RenameHotbarHandler;
import com.gameminers.farrago.network.RenameHotbarMessage;
import com.gameminers.farrago.network.SpawnBeltBreakParticleHandler;
import com.gameminers.farrago.network.SpawnBeltBreakParticleMessage;
import com.gameminers.farrago.proxy.Proxy;
import com.gameminers.farrago.recipes.RecipeChromatic;
import com.gameminers.farrago.recipes.RecipesVividOrbDyes;
import com.gameminers.farrago.selector.ItemSelector;
import com.gameminers.farrago.selector.NullSelector;
import com.gameminers.farrago.selector.OreSelector;
import com.gameminers.farrago.selector.Selector;
import com.gameminers.farrago.tileentity.TileEntityCellFiller;
import com.gameminers.farrago.tileentity.TileEntityCombustor;
import com.gameminers.farrago.tileentity.TileEntityScrapper;
import com.gameminers.farrago.tileentity.TileEntityTicker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;

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
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(name="Farrago",modid="farrago",dependencies="required-after:KitchenSink;after:GlassPane",version="@VERSION@",acceptedMinecraftVersions="@MCVERSION@")
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
	public static BlockCollector COLLECTOR;
	public static BlockLightPipe LIGHT_PIPE;
	public static BlockFarragoStairs XENOTIME_STAIRS;
	
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
	public static ItemMinigun MINIGUN;
	public static ItemMinigunCell MINIGUN_CELL;
	public static ItemUndefined UNDEFINED;
	public static ItemModularChestplate MODULAR_CHESTPLATE;
	public static ItemCrafting CRAFTING;
	public static ItemUtilityBelt UTILITY_BELT;
	
	public static ItemChromaticPickaxe CHROMATIC_PICKAXE;
	public static ItemChromaticAxe CHROMATIC_AXE;
	public static ItemChromaticSword CHROMATIC_SWORD;
	public static ItemChromaticSpade CHROMATIC_SHOVEL;
	public static ItemChromaticHoe CHROMATIC_HOE;
	
	public static ItemChromaticArmor CHROMATIC_HELMET;
	public static ItemChromaticArmor CHROMATIC_CHESTPLATE;
	public static ItemChromaticArmor CHROMATIC_LEGGINGS;
	public static ItemChromaticArmor CHROMATIC_BOOTS;
	
	
	public static SimpleNetworkWrapper CHANNEL;
	
	public static Map<Long, List<IRecipe>> recipes = new HashMap<Long, List<IRecipe>>();
	
	public static String brand = null;
	public static boolean showBrand = true;
	
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
	public static Map<Selector, String> disabled = Maps.newHashMap();
	public static Config config;
	public static int lightPipeRenderType;
	public static List<Material> materials = Lists.newArrayList();
	public static Map<String, Material> monikerLookup = Maps.newHashMap();
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		proxy.preInit();
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new FarragoGuiHandler());
		CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("Farrago");
		CHANNEL.registerMessage(ModifyRifleModeHandler.class, ModifyRifleModeMessage.class, 0, Side.SERVER);
		CHANNEL.registerMessage(ChangeSelectedHotbarHandler.class, ChangeSelectedHotbarMessage.class, 1, Side.SERVER);
		CHANNEL.registerMessage(RenameHotbarHandler.class, RenameHotbarMessage.class, 2, Side.SERVER);
		CHANNEL.registerMessage(LockSlotHandler.class, LockSlotMessage.class, 3, Side.SERVER);
		
		CHANNEL.registerMessage(SpawnBeltBreakParticleHandler.class, SpawnBeltBreakParticleMessage.class, 0, Side.CLIENT);
		
		COMBUSTOR = new BlockCombustor();
		SCRAPPER = new BlockScrapper();
		ORE = new BlockOre();
		GLOW = new BlockGlow();
		RESOURCE = new BlockResource();
		MACHINE = new BlockMachine();
		COLLECTOR = new BlockCollector();
		LIGHT_PIPE = new BlockLightPipe();
		XENOTIME_STAIRS = new BlockFarragoStairs(ORE, 2);
		
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
		MINIGUN = new ItemMinigun();
		MINIGUN_CELL = new ItemMinigunCell();
		UNDEFINED = new ItemUndefined();
		MODULAR_CHESTPLATE = new ItemModularChestplate();
		CRAFTING = new ItemCrafting();
		UTILITY_BELT = new ItemUtilityBelt();
		
		CHROMATIC_PICKAXE = new ItemChromaticPickaxe();
		CHROMATIC_AXE = new ItemChromaticAxe().setUnlocalizedName("chromatic_axe");
		CHROMATIC_SWORD = new ItemChromaticSword().setUnlocalizedName("chromatic_sword");
		CHROMATIC_SHOVEL = new ItemChromaticSpade().setUnlocalizedName("chromatic_shovel");
		CHROMATIC_HOE = new ItemChromaticHoe().setUnlocalizedName("chromatic_hoe");
		
		CHROMATIC_HELMET = new ItemChromaticArmor(0).setTextureName("farrago:chromatic_helmet").setUnlocalizedName("chromatic_helmet");
		CHROMATIC_CHESTPLATE = new ItemChromaticArmor(1).setTextureName("farrago:chromatic_chestplate").setUnlocalizedName("chromatic_chestplate");
		CHROMATIC_LEGGINGS = new ItemChromaticArmor(2).setTextureName("farrago:chromatic_leggings").setUnlocalizedName("chromatic_leggings");
		CHROMATIC_BOOTS = new ItemChromaticArmor(3).setTextureName("farrago:chromatic_boots").setUnlocalizedName("chromatic_boots");
		
		ConfigList li = config.getList("materials");
		for (ConfigValue cv : li) {
			Config obj = ((ConfigObject) cv).toConfig();
			Material mat = new Material(obj);
			materials.add(mat);
			if (mat.moniker != null) {
				monikerLookup.put(mat.moniker, mat);
			}
		}
		
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
		GameRegistry.registerTileEntity(TileEntityTicker.class, "FarragoTicker");
		
		GameRegistry.registerBlock(GLOW, null, "glow");
		GameRegistry.registerBlock(COMBUSTOR, "combustor");
		GameRegistry.registerBlock(SCRAPPER, "scrapper");
		GameRegistry.registerBlock(MACHINE, ItemBlockWithCustomName.class, "machine");
		GameRegistry.registerBlock(ORE, ItemBlockWithCustomName.class, "watashi");
		GameRegistry.registerBlock(RESOURCE, ItemBlockWithCustomName.class, "resource");
		GameRegistry.registerBlock(COLLECTOR, ItemBlockWithCustomName.class, "collector");
		GameRegistry.registerBlock(LIGHT_PIPE, "lightPipe");
		GameRegistry.registerBlock(XENOTIME_STAIRS, "xenotimeStairs");
		
		
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
		GameRegistry.registerItem(MINIGUN, "minigun");
		GameRegistry.registerItem(MINIGUN_CELL, "minigunCell");
		if (config.getBoolean("eegg.undefined")) {
			GameRegistry.registerItem(UNDEFINED, "undefined");
		}
		GameRegistry.registerItem(MODULAR_CHESTPLATE, "modularChestplate");
		GameRegistry.registerItem(CRAFTING, "crafting");
		GameRegistry.registerItem(UTILITY_BELT, "utilityBelt");
		
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
		if (config.getBoolean("chromatics.orb.spawnInDungeons")) {
			for (String s : new String[] {ChestGenHooks.DUNGEON_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST, ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.MINESHAFT_CORRIDOR}) {
				for (int color : new int[] {0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0xFF00FF, 0x00FFFF}) {
					ItemStack orb = new ItemStack(VIVID_ORB);
					VIVID_ORB.setColor(orb, color);
					ChestGenHooks.addItem(s, new WeightedRandomChestContent(orb, 0, 2, 25));
				}
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
		GameRegistry.addSmelting(new ItemStack(ORE, 1, 1), new ItemStack(APOCITE), 0);
		if (config.getBoolean("worldGen.yttriumOre.generate")) {
			GameRegistry.registerWorldGenerator(yttrGen = new YttriumGenerator(), 5);
		}
		if (config.getBoolean("worldGen.xenotime.generate")) {
			GameRegistry.registerWorldGenerator(xenoGen = new XenotimeGenerator(), 4);
		}
		
		RecipeSorter.register("farrago:chromatic_shaped", RecipeChromatic.class, Category.SHAPED, "before:minecraft:shapeless");
		RecipeSorter.register("farrago:vivid_orb_dyes", RecipesVividOrbDyes.class, Category.SHAPELESS, "after:minecraft:shapeless");
		
		OreDictionary.registerOre("dyeRed", new ItemStack(DUST, 1, 5));
		OreDictionary.registerOre("gemEnderPearl", Items.ender_pearl);
		OreDictionary.registerOre("gemNetherStar", Items.nether_star);
		OreDictionary.registerOre("gemApocite", APOCITE);
		OreDictionary.registerOre("gemApociteLenient", new ItemStack(APOCITE, 1, OreDictionary.WILDCARD_VALUE));
		
		RESOURCE.registerRecipes();
		
		if (config.getBoolean("eegg.allowEaterOfWorldsCrafting")) {
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
		}
		
		if (config.getBoolean("fondue.cider.craftable")) {
			GameRegistry.addRecipe(new ItemStack(FONDUE, 1, 3),
					"A",
					"C",
					'A', Items.apple,
					'C', CAQUELON
					);
		} else {
			disabled.put(new ItemSelector(new ItemStack(FONDUE, 1, 3)), config.getString("fondue.cider.disableReason"));
		}
		
		if (config.getBoolean("fondue.cheese.craftable")) {
			GameRegistry.addRecipe(new ItemStack(FONDUE, 1, 0),
					"M",
					"C",
					'M', Items.milk_bucket,
					'C', CAQUELON
					);
		} else {
			disabled.put(new ItemSelector(new ItemStack(FONDUE, 1, 0)), config.getString("fondue.cheese.disableReason"));
		}
		
		if (config.getBoolean("fondue.chinese.craftable")) {
			GameRegistry.addRecipe(new ItemStack(FONDUE, 1, 1),
					"cWB",
					" Cw",
					'W', Items.water_bucket,
					'w', Items.wheat,
					'c', Items.chicken,
					'B', Items.beef,
					'C', CAQUELON
					);
		} else {
			disabled.put(new ItemSelector(new ItemStack(FONDUE, 1, 1)), config.getString("fondue.chinese.disableReason"));
		}
		
		if (config.getBoolean("fondue.chocolate.craftable")) {
			GameRegistry.addRecipe(new ItemStack(FONDUE, 1, 2),
					"ccc",
					" C ",
					'c', new ItemStack(Items.dye, 1, 3),
					'C', CAQUELON
					);
		} else {
			disabled.put(new ItemSelector(new ItemStack(FONDUE, 1, 2)), config.getString("fondue.chocolate.disableReason"));
		}
		
		if (config.getBoolean("misc.caquelon.craftable")) {
			GameRegistry.addRecipe(new ShapedOreRecipe(CAQUELON, 
					"IBI",
					" I ",
					'I', "ingotIron",
					'B', Items.bucket
					));
		} else {
			disabled.put(new ItemSelector(CAQUELON), config.getString("misc.caquelon.disableReason"));
		}
		
		if (config.getBoolean("machines.combustor.craftable")) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MACHINE, 1, 0),
					"III",
					"IBI",
					"IGI",
					'I', "ingotYttrium",
					'B', Blocks.iron_bars,
					'G', Items.gunpowder
					));
		} else {
			disabled.put(new ItemSelector(new ItemStack(MACHINE, 1, 0)), config.getString("machines.combustor.disableReason"));
		}
		
		if (config.getBoolean("machines.scrapper.craftable")) {
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
		} else {
			disabled.put(new ItemSelector(new ItemStack(MACHINE, 1, 1)), config.getString("machines.scrapper.disableReason"));
		}
		
		if (config.getBoolean("blunderbuss.craftable")) {
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
		} else {
			disabled.put(new ItemSelector(BLUNDERBUSS), config.getString("blunderbuss.disableReason"));
		}
		
		if (config.getBoolean("rifle.craftable")) {
			GameRegistry.addRecipe(new ShapedOreRecipe(RIFLE, 
					"D  ",
					" C ",
					" BC",
					'D', "gemApocite",
					'C', "ingotYttriumCopper",
					'B', Items.blaze_rod
					));
		} else {
			disabled.put(new ItemSelector(RIFLE), config.getString("rifle.disableReason"));
		}
		
		if (config.getBoolean("minigun.craftable")) {
			GameRegistry.addRecipe(new ShapedOreRecipe(MINIGUN, 
					"I  ",
					" B ",
					" bD",
					'B', "blockYttriumCopper",
					'I', "ingotYttriumCopper",
					'D', new ItemStack(MINIGUN_CELL, 1, MINIGUN_CELL.getCapacity()+1),
					'b', Items.blaze_rod
					));
		} else {
			disabled.put(new ItemSelector(MINIGUN), config.getString("minigun.disableReason"));
		}
		if (config.getBoolean("minigun.drum.craftable")) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MINIGUN_CELL, 1, MINIGUN_CELL.getCapacity()+1), 
					"ICI",
					"CBC",
					"ICI",
					'B', "blockYttriumCopper",
					'I', "ingotYttriumCopper",
					'C', new ItemStack(CELL, 1, 0)
					));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MINIGUN_CELL, 1, 0),
					new ItemStack(MINIGUN_CELL, 1, MINIGUN_CELL.getCapacity()+1),
					"blockRedstone", "blockRedstone",
					"blockCopper", "blockCopper"
					));
		} else {
			disabled.put(new ItemSelector(MINIGUN_CELL), config.getString("minigun.drum.disableReason"));
		}
		if (!config.getBoolean("chromatics.disallowCrafting")) {
			if (config.getBoolean("chromatics.pickaxe.craftable")) {
				GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_PICKAXE,
						"III",
						" / ",
						" V ",
						'I', "ingotYttrium",
						'V', VIVID_ORB,
						'/', "stickWood"
						));
			} else {
				disabled.put(new ItemSelector(CHROMATIC_PICKAXE), config.getString("chromatics.pickaxe.disableReason"));
			}
			ItemStack diaChromaticPick = new ItemStack(CHROMATIC_PICKAXE);
			diaChromaticPick.setTagCompound(new NBTTagCompound());
			diaChromaticPick.getTagCompound().setBoolean("Diamond", true);
			if (config.getBoolean("chromatics.diamondTippedPickaxe.craftable")) {
				GameRegistry.addRecipe(new RecipeChromatic(diaChromaticPick,
						"III",
						"D/D",
						" V ",
						'I', "ingotYttrium",
						'V', VIVID_ORB,
						'/', "stickWood",
						'D', "gemDiamond"
						));
			} else {
				disabled.put(new ItemSelector(diaChromaticPick), config.getString("chromatics.diamondTippedPickaxe.disableReason"));
			}
			if (config.getBoolean("chromatics.axe.craftable")) {
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
			} else {
				disabled.put(new ItemSelector(CHROMATIC_AXE), config.getString("chromatics.axe.disableReason"));
			}
			if (config.getBoolean("chromatics.sword.craftable")) {
				GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_SWORD,
						" I ",
						" I ",
						" V ",
						'I', "ingotYttrium",
						'V', VIVID_ORB
						));
			} else {
				disabled.put(new ItemSelector(CHROMATIC_SWORD), config.getString("chromatics.sword.disableReason"));
			}
			if (config.getBoolean("chromatics.shovel.craftable")) {
				GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_SHOVEL,
						" I ",
						" / ",
						" V ",
						'I', "ingotYttrium",
						'V', VIVID_ORB,
						'/', "stickWood"
						));
			} else {
				disabled.put(new ItemSelector(CHROMATIC_SHOVEL), config.getString("chromatics.shovel.disableReason"));
			}
			if (config.getBoolean("chromatics.hoe.craftable")) {
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
			} else {
				disabled.put(new ItemSelector(CHROMATIC_HOE), config.getString("chromatics.hoe.disableReason"));
			}
			if (config.getBoolean("chromatics.helmet.craftable")) {
				GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_HELMET,
						"IVI",
						"I I",
						'I', "ingotYttrium",
						'V', VIVID_ORB
						));
			} else {
				disabled.put(new ItemSelector(CHROMATIC_HELMET), config.getString("chromatics.helmet.disableReason"));
			}
			if (config.getBoolean("chromatics.chestplate.craftable")) {
				GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_CHESTPLATE,
						"I I",
						"IVI",
						"III",
						'I', "ingotYttrium",
						'V', VIVID_ORB
						));
			} else {
				disabled.put(new ItemSelector(CHROMATIC_CHESTPLATE), config.getString("chromatics.chestplate.disableReason"));
			}
			if (config.getBoolean("chromatics.leggings.craftable")) {
				GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_LEGGINGS,
						"IVI",
						"I I",
						"I I",
						'I', "ingotYttrium",
						'V', VIVID_ORB
						));
			} else {
				disabled.put(new ItemSelector(CHROMATIC_LEGGINGS), config.getString("chromatics.leggings.disableReason"));
			}
			if (config.getBoolean("chromatics.boots.craftable")) {
				GameRegistry.addRecipe(new RecipeChromatic(CHROMATIC_BOOTS,
						"I I",
						"IVI",
						'I', "ingotYttrium",
						'V', VIVID_ORB
						));
			} else {
				disabled.put(new ItemSelector(CHROMATIC_BOOTS), config.getString("chromatics.boots.disableReason"));
			}
			if (config.getBoolean("chromatics.orb.craftable")) {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(VIVID_ORB),
						"QIQ",
						"IEI",
						"QIQ",
						'E', "gemEnderPearl",
						'I', "ingotIron",
						'Q', "gemQuartz"
						));
			} else {
				disabled.put(new ItemSelector(VIVID_ORB), config.getString("chromatics.orb.disableReason"));
			}
		} else {
			String reason = config.getString("chromatics.disallowReason");
			disabled.put(new ItemSelector(CHROMATIC_PICKAXE), reason);
			disabled.put(new ItemSelector(CHROMATIC_AXE), reason);
			disabled.put(new ItemSelector(CHROMATIC_SWORD), reason);
			disabled.put(new ItemSelector(CHROMATIC_SHOVEL), reason);
			disabled.put(new ItemSelector(CHROMATIC_HOE), reason);
			disabled.put(new ItemSelector(CHROMATIC_HELMET), reason);
			disabled.put(new ItemSelector(CHROMATIC_CHESTPLATE), reason);
			disabled.put(new ItemSelector(CHROMATIC_LEGGINGS), reason);
			disabled.put(new ItemSelector(CHROMATIC_BOOTS), reason);
			disabled.put(new ItemSelector(VIVID_ORB), reason);
		}
		if (config.getBoolean("resources.apocite.decay")) {
			ItemStack stableApocite = new ItemStack(APOCITE, 1, 0);
			stableApocite.setTagCompound(new NBTTagCompound());
			stableApocite.getTagCompound().setBoolean("Stable", true);
			GameRegistry.addRecipe(new ShapelessOreRecipe(stableApocite, "gemApocite", "dustEnderPearl", "dustEmerald", "dustRedstone"));
		}
		if (config.getBoolean("rifle.magazine.craftable")) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CELL, 4, 0), 
					"YGY",
					"YGY",
					"YCY",
					'Y', "ingotYttrium",
					'C', "ingotYttriumCopper",
					'G', "paneGlass"
					));
		} else {
			disabled.put(new ItemSelector(new ItemStack(CELL, 1, 0)), config.getString("rifle.magazine.disableReason"));
		}
		if (config.getBoolean("rifle.magazine.highVelocity.craftable")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 1, 1),  new ItemStack(CELL, 1, 0), "dustCopper", "dustRedstone"));
		} else {
			disabled.put(new ItemSelector(new ItemStack(CELL, 1, 1)), config.getString("rifle.magazine.highVelocity.disableReason"));
		}
		
		if (config.getBoolean("rifle.magazine.luminescent.craftable")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 1, 2),  new ItemStack(CELL, 1, 0), "dustYttrium", "dustGlowstone"));
		} else {
			disabled.put(new ItemSelector(new ItemStack(CELL, 1, 2)), config.getString("rifle.magazine.luminescent.disableReason"));
		}
		
		if (config.getBoolean("rifle.magazine.mining.craftable")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 2, 3),  new ItemStack(CELL, 1, 0), new ItemStack(CELL, 1, 0), "dustGold", "dustGold", "dustDiamond"));
		} else {
			disabled.put(new ItemSelector(new ItemStack(CELL, 1, 3)), config.getString("rifle.magazine.mining.disableReason"));
		}
		
		if (config.getBoolean("rifle.magazine.explosive.craftable")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 1, 4),  new ItemStack(CELL, 1, 0), "dustIron", Items.gunpowder));
		} else {
			disabled.put(new ItemSelector(new ItemStack(CELL, 1, 4)), config.getString("rifle.magazine.explosive.disableReason"));
		}
		
		if (config.getBoolean("rifle.magazine.teleportation.craftable")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 1, 5),  new ItemStack(CELL, 1, 0), "dustEnderPearl", "dustEmerald"));
		} else {
			disabled.put(new ItemSelector(new ItemStack(CELL, 1, 5)), config.getString("rifle.magazine.teleportation.disableReason"));
		}
		
		if (config.getBoolean("rifle.magazine.incendiary.craftable")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CELL, 1, 6),  new ItemStack(CELL, 1, 0), "dustCopper", "dustRedstone", Items.blaze_powder));
		} else {
			disabled.put(new ItemSelector(new ItemStack(CELL, 1, 6)), config.getString("rifle.magazine.incendiary.disableReason"));
		}
		
		GameRegistry.addShapedRecipe(new ItemStack(XENOTIME_STAIRS, 4),
				"X  ",
				"XX ",
				"XXX",
				'X', new ItemStack(ORE, 1, 2)
			);
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(DUST, 2, 6), "dustCopper", "dustYttrium"));
		
		if (config.getBoolean("kahur.craftable")) {
			for (WoodColor body : WoodColor.values()) {
				for (WoodColor drum : WoodColor.values()) {
					for (Material mat : materials) {
						if (!mat.validForKahur) continue;
						if (mat.ingot.getRepresentation() == null) continue;
						ItemStack kahur = new ItemStack(KAHUR);
						NBTTagCompound tag = new NBTTagCompound();
						tag.setString("KahurBodyMaterial", body.name());
						tag.setString("KahurDrumMaterial", drum.name());
						tag.setString("KahurPumpName", mat.name);
						tag.setInteger("KahurDurability", mat.kahurDurability);
						tag.setInteger("KahurPumpColor", mat.color);
						tag.setBoolean("KahurDeterministic", mat.kahurDeterministic);
						if (mat.kahurSpecial != null) {
							tag.setString("KahurAbility", mat.kahurSpecial.name());
						}
						tag.setBoolean("KahurCanPickUpMobs", mat.kahurMobs);
						kahur.setTagCompound(tag);
						GameRegistry.addRecipe(new ShapedOreRecipe(kahur,
								"B  ",
								"PD ",
								" /B",
								'B', new ItemStack(Blocks.planks, 1, body.ordinal()),
								'D', new ItemStack(Blocks.planks, 1, drum.ordinal()),
								'P', mat.ingot.getRepresentation(),
								'/', "stickWood"));
						GameRegistry.addRecipe(new ShapedOreRecipe(kahur,
								"  B",
								" DP",
								"B/ ",
								'B', new ItemStack(Blocks.planks, 1, body.ordinal()),
								'D', new ItemStack(Blocks.planks, 1, drum.ordinal()),
								'P', mat.ingot.getRepresentation(),
								'/', "stickWood"));
					}
				}
			}
		} else {
			disabled.put(new ItemSelector(KAHUR), config.getString("kahur.disableReason"));
		}
		if (config.getBoolean("utilityBelt.craftable")) {
			for (Material mat : materials) {
				if (!mat.validForBelt) continue;
				if (mat.block.getRepresentation() == null) continue;
				ItemStack stack = UTILITY_BELT.setExtraRows(new ItemStack(UTILITY_BELT, 1, 0), mat.beltRows);
				stack.getTagCompound().setTag("display", new NBTTagCompound());
				stack.getTagCompound().getCompoundTag("display").setInteger("color", mat.color);
				stack.setStackDisplayName("\u00A7f"+mat.name+" Utility Belt");
				GameRegistry.addRecipe(new ShapedOreRecipe(stack,
						"LLL",
						"CMC",
						'L', Items.leather,
						'C', Blocks.chest,
						'M', mat.block.getRepresentation()));
			}
		} else {
			disabled.put(new ItemSelector(UTILITY_BELT), config.getString("utilityBelt.disableReason"));
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
		if (!config.getBoolean("fondue.cyberCider.craftable")) return;
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
		proxy.tooltip(e);
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
	/**
	 * A selector must start with either a fully qualified block or item name
	 * (e.g. 'minecraft:stone', 'farrago:rifle'), a hash ("#"), a dollar sign ("$"),
	 * or be the string "x".
	 * <p>
	 * "x" indicates to return a {@link NullSelector}.
	 * <p>
	 * For a hash, the string is expected to be an {@link OreDictionary} name
	 * prefixed with a hash symbol. If the name has an | at the end, another
	 * selector string will be parsed as a fallback if no items are registered
	 * under that name. So, "#ingotYttrium" will match an yttrium ingot, if it
	 * exists, or nothing. "#ingotYttrium|farrago:ingot@0" will match an yttrium
	 * ingot using the ore dictionary if possible, or will fall back to a static
	 * reference to Farrago's yttrium ingot. These can be chained, so if you
	 * really wanted, you could do something like "#ingotYellorium|#ingotUranium|
	 * #ingotPlutonium|#gemEnderPearl|minecraft:ender_pearl". An {@link OreSelector}
	 * will be returned.
	 * <p>
	 * For a fully qualified block or item name, an @ sign is
	 * a separator between the item's name and it's required damage value. If
	 * the damage value is omitted, all damage values are accepted. A value of
	 * 32767 also acts as a wildcard. If the given block or item name is not found,
	 * the selector returned will be a {@link NullSelector}, which matches nothing.
	 * If the name is found, an {@link ItemSelector} will be returned.
	 * Finally, a Mojangson NBT definition can be enclosed in curly braces at the
	 * end to require certain NBT tags to be present. As with damage values, if
	 * omitted, any values are accepted. If trailed by a '?', then any extra tags
	 * on the item are ignored. So, {Foo:Bar} matches {Foo:Bar} but not
	 * {Foo:Bar,Baz:Quux}, but {Foo:Bar}? will match both.
	 * 
	 * ('Mojangson' here means the format used by command blocks.)
	 * <p>
	 * For a dollar sign, the given class will be loaded and
	 * instanciated with the default constructor. An unknown {@link Selector}
	 * subclass will be returned.
	 * @param def A string matching the format described above
	 * @return A newly created Selector matching the format
	 * @throws IllegalArgumentException if the format string is invalid
	 */
	public static Selector parseSelector(String def) {
		try {
			if (def.equals("x")) {
				return new NullSelector();
			}
			if (def.startsWith("#")) {
				if (def.contains("|")) {
					int idx = def.indexOf('|');
					return new OreSelector(def.substring(1, idx), parseSelector(def.substring(idx+1)));
				} else {
					return new OreSelector(def.substring(1));
				}
			}
			if (def.startsWith("$")) {
				return (Selector) Class.forName(def.substring(1)).newInstance();
			}
			int meta = 32767;
			NBTTagCompound tag = null;
			boolean lenientTag = def.endsWith("}?");
			if (def.contains("{") && def.contains("}")) {
				String mojangson = def.substring(def.indexOf('{'), def.indexOf('}')+1);
				tag = (NBTTagCompound) JsonToNBT.func_150315_a(mojangson);
				def = def.substring(0, def.indexOf('{'));
			}
			if (def.contains("@")) {
				meta = Integer.parseInt(def.substring(def.indexOf('@')+1));
				def = def.substring(0, def.indexOf('@'));
			}
			ItemStack stack = new ItemStack((Item) Item.itemRegistry.getObject(def), 1, meta);
			if (stack.getItem() == null) return new NullSelector();
			stack.setTagCompound(tag);
			return new ItemSelector(stack, lenientTag);
		} catch (Exception e) {
			throw new IllegalArgumentException("Selector string is invalid: "+def, e);
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

	public static int getPassThruCost(String key) {
		return getPassThruCost(config, key);
	}

	public static int getPassThruCost(Config config, String key) {
		ConfigValue val = config.getValue(key);
		return val.valueType() == ConfigValueType.STRING ? -1 : ((Number)val.unwrapped()).intValue();
	}

	/*
	 * Hold on, before you berate me for this awful hack, I can explain!
	 * 
	 * PlayerDestroyItemEvent doesn't fire for armor. I looked through all code
	 * related to armor breaking, and the only external method that's called that
	 * tons of other things don't also call that is reasonable is setDamage.
	 * 
	 * setDamage doesn't pass in an Entity, so I scan every player on the server...
	 * Please submit an issue if there's a new (better) way to do it!
	 */
	public static void doBreakUtilityBelt(ItemStack belt, List<EntityPlayer> players) {
		if (!FarragoMod.config.getBoolean("utilityBelt.dropItemsOnBreak")) return;
		for (EntityPlayer p : players) {
			for (ItemStack s : p.inventory.armorInventory) {
				if (s == belt) {
					_breakUtilityBelt(p, s);
					return;
				}
			}
			for (ItemStack s : p.inventory.mainInventory) {
				if (s == belt) {
					_breakUtilityBelt(p, s);
					return;
				}
			}
		}
	}
	private static void _breakUtilityBelt(EntityPlayer p, ItemStack belt) {
		p.worldObj.playSoundAtEntity(p, "farrago:belt_break", 0.8f, 1.0f);
		CHANNEL.sendToAllAround(new SpawnBeltBreakParticleMessage(p.getEntityId()), new TargetPoint(p.dimension, p.posX, p.posY, p.posZ, 64));
		for (ItemStack s : FarragoMod.UTILITY_BELT.getCompleteContents(belt)) {
			p.entityDropItem(s, 0.2f);
		}
	}
}
