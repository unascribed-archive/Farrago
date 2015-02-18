package com.gameminers.farrago;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCompressed;
import net.minecraft.block.material.MapColor;
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
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gameminers.farrago.block.BlockCombustor;
import com.gameminers.farrago.block.BlockOre;
import com.gameminers.farrago.block.BlockScrapper;
import com.gameminers.farrago.gen.YttriumGenerator;
import com.gameminers.farrago.item.ItemDust;
import com.gameminers.farrago.item.ItemFondue;
import com.gameminers.farrago.item.ItemIngot;
import com.gameminers.farrago.item.ItemRubble;
import com.gameminers.farrago.item.ItemVividOrb;
import com.gameminers.farrago.kahur.KahurIota;
import com.gameminers.farrago.tileentity.TileEntityCombustor;
import com.gameminers.farrago.tileentity.TileEntityScrapper;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(name="Farrago",modid="farrago",dependencies="required-after:KitchenSink;after:GlassPane",version="0.9")
public class FarragoMod {
	private static final List<Iota> subMods = Lists.newArrayList();
	public static final Logger log = LogManager.getLogger("Farrago");
	@SidedProxy(clientSide="com.gameminers.farrago.ClientProxy", serverSide="com.gameminers.farrago.ServerProxy")
	public static Proxy proxy;
	@Instance("farrago")
	public static FarragoMod inst;
	public static BlockCombustor COMBUSTOR;
	public static BlockScrapper SCRAPPER;
	public static Block NETHER_STAR_BLOCK;
	public static BlockOre ORE;
	
	public static ItemVividOrb VIVID_ORB;
	public static Item CAQUELON;
	public static ItemRubble RUBBLE;
	public static ItemDust DUST;
	public static ItemIngot INGOT;
	public static ItemFondue FONDUE;
	public static Map<Long, List<IRecipe>> recipes = new HashMap<Long, List<IRecipe>>();
	
	public static String brand;
	public static boolean copperlessEnvironment;
	public static CreativeTabs creativeTab = new CreativeTabs("farrago") {
		
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(COMBUSTOR);
		}
	};
	private YttriumGenerator yttrGen;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		proxy.preInit();
		subMods.add(new KahurIota());
	}
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new FarragoGuiHandler());
		COMBUSTOR = new BlockCombustor();
		SCRAPPER = new BlockScrapper();
		ORE = new BlockOre();
		NETHER_STAR_BLOCK = new BlockCompressed(MapColor.adobeColor).setBlockTextureName("farrago:nether_star_block").setHardness(20f).setLightLevel(0.5f).setBlockName("nether_star_block");
		NETHER_STAR_BLOCK.setHarvestLevel("pickaxe", 2);
		CAQUELON = new Item().setTextureName("farrago:caquelon").setMaxStackSize(1).setUnlocalizedName("caquelon");
		VIVID_ORB = new ItemVividOrb();
		RUBBLE = new ItemRubble();
		DUST = new ItemDust();
		INGOT = new ItemIngot();
		FONDUE = new ItemFondue();
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
		GameRegistry.registerBlock(COMBUSTOR, "combustor");
		GameRegistry.registerBlock(SCRAPPER, "scrapper");
		GameRegistry.registerBlock(NETHER_STAR_BLOCK, "netherStarBlock");
		GameRegistry.registerBlock(ORE, "watashi");
		GameRegistry.registerItem(CAQUELON, "caquelon");
		GameRegistry.registerItem(RUBBLE, "rubble");
		GameRegistry.registerItem(DUST, "dust");
		GameRegistry.registerItem(INGOT, "ingot");
		GameRegistry.registerItem(FONDUE, "fondue");
		GameRegistry.registerItem(VIVID_ORB, "vividOrb");
		ORE.registerOres();
		DUST.registerOres();
		INGOT.registerOres();
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
		GameRegistry.addSmelting(new ItemStack(DUST, 1, 7), new ItemStack(INGOT, 1, 3), 0);
		GameRegistry.addSmelting(new ItemStack(ORE, 1, 0), new ItemStack(INGOT, 1, 0), 0);
		GameRegistry.registerWorldGenerator(yttrGen = new YttriumGenerator(), 0);
		GameRegistry.addRecipe(new ItemStack(Items.nether_star, 9),
				"B",
				'B', NETHER_STAR_BLOCK);
		GameRegistry.addRecipe(new ItemStack(Items.nether_star),
				"123",
				"4D5",
				"d6d",
				'1', new ItemStack(RUBBLE, 1, 0),
				'2', new ItemStack(RUBBLE, 1, 1),
				'3', new ItemStack(RUBBLE, 1, 2),
				'4', new ItemStack(RUBBLE, 1, 3),
				'5', new ItemStack(RUBBLE, 1, 4),
				'6', new ItemStack(RUBBLE, 1, 5),
				'D', new ItemStack(DUST, 1, 4),
				'd', new ItemStack(Blocks.diamond_block)
				);
		ItemStack eow = new ItemStack(Items.diamond_sword);
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
		GameRegistry.addRecipe(eow,
				" D ",
				"+d+",
				" + ",
				'D', new ItemStack(DUST, 1, 4),
				'd', new ItemStack(Blocks.diamond_block),
				'+', new ItemStack(NETHER_STAR_BLOCK)
				);
		GameRegistry.addRecipe(new ItemStack(NETHER_STAR_BLOCK),
				"+++",
				"+++",
				"+++",
				'+', Items.nether_star
				);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(VIVID_ORB),
				"WIW",
				"IQI",
				"WIW",
				'W', new ItemStack(Blocks.wool, 1, 0),
				'I', "ingotIron",
				'Q', "blockQuartz"
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
		GameRegistry.addRecipe(new ShapedOreRecipe(COMBUSTOR, 
				"III",
				"IBI",
				"IGI",
				'I', "ingotYttriumSteel",
				'B', Blocks.iron_bars,
				'G', Items.gunpowder
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(SCRAPPER, 
				"III",
				"QPQ",
				"BDB",
				'I', "ingotIron",
				'Q', Items.quartz,
				'B', "blockIron",
				'D', "gemDiamond",
				'P', Blocks.heavy_weighted_pressure_plate
				));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(DUST, 2, 6), "dustCopper", "dustYttrium"));
		OreDictionary.registerOre("dyeRed", new ItemStack(DUST, 1, 5));
		for (Iota iota : subMods) {
			iota.init();
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
	public void onDataSave(ChunkDataEvent.Save e) {
		if (!"yttrium".equals(e.getData().getString("farrago:RetroGenKey"))) {
			log.info("Marking "+e.getChunk().xPosition+", "+e.getChunk().zPosition+" as retrogenerated");
			e.getData().setString("farrago:RetroGenKey", "yttrium");
		}
	}
	private Deque<Chunk> chunksToGen = new ArrayDeque<Chunk>();
	@SubscribeEvent
	public void onDataLoad(ChunkDataEvent.Load e) {
		// TODO: Incremental retrogen
		if (!"yttrium".equals(e.getData().getString("farrago:RetroGenKey"))) {
			chunksToGen.addLast(e.getChunk());
		}
	}
	@SubscribeEvent
	public void onTick(ServerTickEvent e) {
		if (e.phase == Phase.END) {
			if (!chunksToGen.isEmpty()) {
				Chunk chunk = chunksToGen.pop();
				yttrGen.generate(chunk.worldObj.rand, chunk.xPosition, chunk.zPosition, chunk.worldObj, null, null);
				chunk.setChunkModified();
				log.info("Retrogenerating "+chunk.xPosition+", "+chunk.zPosition);
			}
		}
	}
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		for (Iota iota : subMods) {
			iota.postInit();
		}
		proxy.postInit();
		if (OreDictionary.getOres("ingotCopper").size() <= 1) {
			copperlessEnvironment = true;
			log.warn("We are running in a copperless environment; enabling fallback copper dust drops from Yttrium ore");
		}
	}
	@EventHandler
	public void onAvailable(FMLLoadCompleteEvent e) {
		log.info("Farrago load completed without incident; loaded "+subMods.size()+" Iotas");
	}
	public static long hashItemStack(ItemStack toHash) {
		long hash = 0;
		hash |= (toHash.getItemDamage() & Short.MAX_VALUE) << Short.SIZE;
		hash |= (Item.getIdFromItem(toHash.getItem()) & Short.MAX_VALUE);
		if (toHash.hasTagCompound()) {
			hash |= (toHash.getTagCompound().hashCode() << 32);
		}
		return hash;
	}
	protected static List<Iota> getSubMods() {
		return subMods;
	}
}
