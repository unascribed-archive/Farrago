package com.gameminers.farrago.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.container.ContainerScrapper;
import com.gameminers.farrago.item.resource.ItemRubble;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityScrapper extends TileEntity implements ISidedInventory {
	private static final int[] slotsTop = new int[] { 0 };
	private static final int[] slotsBottom = new int[] { 2, 1 };
	private static final int[] slotsSides = new int[] { 1 };
	private ContainerScrapper container;
	public static int getItemBurnTime(ItemStack p_145952_0_) {
		return TileEntityFurnace.getItemBurnTime(p_145952_0_) / 2;
	}

	private ItemStack[] furnaceItemStacks = new ItemStack[12];
	public int furnaceBurnTime;
    public int currentItemBurnTime;
    public int furnaceCookTime;
    public int operationLength = 400;
	private String inventoryName;

	@Override
	public int getSizeInventory() {
		return this.furnaceItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		return this.furnaceItemStacks[p_70301_1_];
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		if (this.furnaceItemStacks[p_70298_1_] != null) {
			ItemStack itemstack;

			if (this.furnaceItemStacks[p_70298_1_].stackSize <= p_70298_2_) {
				itemstack = this.furnaceItemStacks[p_70298_1_];
				this.furnaceItemStacks[p_70298_1_] = null;
				return itemstack;
			} else {
				itemstack = this.furnaceItemStacks[p_70298_1_]
						.splitStack(p_70298_2_);

				if (this.furnaceItemStacks[p_70298_1_].stackSize == 0) {
					this.furnaceItemStacks[p_70298_1_] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		if (this.furnaceItemStacks[p_70304_1_] != null) {
			ItemStack itemstack = this.furnaceItemStacks[p_70304_1_];
			this.furnaceItemStacks[p_70304_1_] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		this.furnaceItemStacks[p_70299_1_] = p_70299_2_;

		if (p_70299_2_ != null
				&& p_70299_2_.stackSize > this.getInventoryStackLimit()) {
			p_70299_2_.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.inventoryName : "container.scrapper";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.inventoryName != null && this.inventoryName.length() > 0;
	}

	public void setInventoryName(String name) {
		this.inventoryName = name;
	}

	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);
		NBTTagList nbttaglist = p_145839_1_.getTagList("Items", 10);
		this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.furnaceItemStacks.length) {
				this.furnaceItemStacks[b0] = ItemStack
						.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		this.furnaceBurnTime = p_145839_1_.getShort("BurnTime");
		this.furnaceCookTime = p_145839_1_.getShort("CookTime");
		this.currentItemBurnTime = TileEntityScrapper.getItemBurnTime(this.furnaceItemStacks[1]);

		if (p_145839_1_.hasKey("CustomName", 8)) {
			this.inventoryName = p_145839_1_.getString("CustomName");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);
		p_145841_1_.setShort("BurnTime", (short) this.furnaceBurnTime);
		p_145841_1_.setShort("CookTime", (short) this.furnaceCookTime);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.furnaceItemStacks.length; ++i) {
			if (this.furnaceItemStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.furnaceItemStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		p_145841_1_.setTag("Items", nbttaglist);

		if (this.hasCustomInventoryName()) {
			p_145841_1_.setString("CustomName", this.inventoryName);
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int p_145953_1_) {
		canScrap();
		return this.furnaceCookTime * p_145953_1_ / operationLength;
	}

	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int p_145955_1_) {
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = 8;
		}

		return this.furnaceBurnTime * p_145955_1_ / this.currentItemBurnTime;
	}

	public boolean isBurning() {
		return this.furnaceBurnTime > 0;
	}

	@Override
	public void updateEntity() {
		boolean flag = this.furnaceBurnTime > 0;
		boolean flag1 = false;

		if (this.furnaceBurnTime > 0) {
			--this.furnaceBurnTime;
		}
		if (!this.worldObj.isRemote) {
			if (this.furnaceBurnTime != 0 || this.furnaceItemStacks[1] != null
					&& this.furnaceItemStacks[0] != null) {
				if (this.furnaceBurnTime == 0 && this.canScrap()) {
					this.currentItemBurnTime = this.furnaceBurnTime = TileEntityScrapper.getItemBurnTime(this.furnaceItemStacks[1]);

					if (this.furnaceBurnTime > 0) {
						flag1 = true;

						if (this.furnaceItemStacks[1] != null) {
							--this.furnaceItemStacks[1].stackSize;

							if (this.furnaceItemStacks[1].stackSize == 0) {
								this.furnaceItemStacks[1] = furnaceItemStacks[1]
										.getItem().getContainerItem(
												furnaceItemStacks[1]);
							}
						}
					}
				}

				if (this.isBurning() && this.canScrap()) {
					++this.furnaceCookTime;

					if (this.furnaceCookTime >= operationLength) {
						this.furnaceCookTime = 0;
						this.scrapItem();
						flag1 = true;
					}
				} else {
					this.furnaceCookTime = 0;
				}
			}

			if (flag != this.furnaceBurnTime > 0) {
				flag1 = true;
				int newMeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
				if (isBurning()) {
					newMeta = newMeta | 0x8;
				} else {
					newMeta = newMeta & 0x7;
				}
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, newMeta, 3);
			}
		}

		if (flag1) {
			this.markDirty();
		}
	}

	/**
	 * Returns true if the furnace can smelt an item, i.e. has a source item,
	 * destination stack isn't full, etc.
	 */
	private boolean canScrap() {
		if (this.furnaceItemStacks[0] == null) {
			return false;
		} else {
			int result = furnaceItemStacks[2] == null ? 1 : furnaceItemStacks[2].stackSize + 1;
			boolean spaceInOutput = false;
			for (int i = 3; i <= 11; i++) {
				ItemStack slot = getStackInSlot(i);
				if (slot == null || slot.getItem() == null || slot.stackSize == 0) {
					spaceInOutput = true;
					break;
				}
			}
			boolean dustable = false;
			int[] ids = OreDictionary.getOreIDs(furnaceItemStacks[0]);
			for (int id : ids) {
				String nm = OreDictionary.getOreName(id);
				String bare = null;
				if (nm.startsWith("ingot")) {
					bare = nm.substring(5);
				} else if (nm.startsWith("gem")) {
					bare = nm.substring(3);
				}
				if (bare != null) {
					String dnm = "dust"+bare;
					List<ItemStack> dustos = OreDictionary.getOres(dnm);
					if (!dustos.isEmpty()) {
						dustable = true;
						break;
					}
				}
			}
			operationLength = dustable ? 100 : 400;
			if (worldObj.isRemote) return false;
			boolean scrappable = dustable || FarragoMod.recipes.containsKey(FarragoMod.hashItemStack(furnaceItemStacks[0]));
			if (!scrappable) {
				ItemStack copy = furnaceItemStacks[0].copy();
				copy.setTagCompound(null);
				scrappable = FarragoMod.recipes.containsKey(FarragoMod.hashItemStack(copy));
			}
			return scrappable &&
					result <= getInventoryStackLimit()
					&& (furnaceItemStacks[2] == null || result <= this.furnaceItemStacks[2].getMaxStackSize())
					&& spaceInOutput;
		}
	}

	/**
	 * Turn one item from the furnace source stack into the appropriate smelted
	 * item in the furnace result stack
	 */
	public void scrapItem() {
		if (this.canScrap()) {
			ItemStack itemstack = this.furnaceItemStacks[0];

			int rubbleCount = worldObj.rand.nextInt(3) * processRecipes(itemstack, null, null, 0);
			
			if (rubbleCount > 0) {
				if (this.furnaceItemStacks[2] == null) {
					this.furnaceItemStacks[2] = new ItemStack(FarragoMod.RUBBLE, rubbleCount, worldObj.rand.nextInt(ItemRubble.typeCount));
				} else if (this.furnaceItemStacks[2].getItem() == FarragoMod.RUBBLE) {
					this.furnaceItemStacks[2].stackSize = Math.min(64, furnaceItemStacks[2].stackSize+rubbleCount);
				}
			}

			--this.furnaceItemStacks[0].stackSize;

			if (this.furnaceItemStacks[0].stackSize <= 0) {
				this.furnaceItemStacks[0] = null;
			}
			
		}
	}

	
	private int processRecipes(ItemStack itemstack, IRecipe cause, IRecipe previousCause, int depth) {
		if (depth >= 14) return 0;
		
		{
			int[] ids = OreDictionary.getOreIDs(itemstack);
			ItemStack dust = null;
			for (int id : ids) {
				String nm = OreDictionary.getOreName(id);
				String bare = null;
				if (nm.startsWith("ingot")) {
					bare = nm.substring(5);
				} else if (nm.startsWith("gem")) {
					bare = nm.substring(3);
				}
				if (bare != null) {
					String dnm = "dust"+bare;
					List<ItemStack> dustos = OreDictionary.getOres(dnm);
					if (!dustos.isEmpty()) {
						dust = dustos.get(0);
						break;
					}
				}
			}
			if (dust != null) {
				addItem(dust.copy());
				return 0;
			}
		}
		
		List<IRecipe> recipes = FarragoMod.recipes.get(FarragoMod.hashItemStack(itemstack));
		if (recipes == null) {
			ItemStack copy = itemstack.copy();
			copy.setTagCompound(null);
			recipes = FarragoMod.recipes.get(FarragoMod.hashItemStack(copy));
		}
		
		int rubbleCount = 0;
		
		if (recipes != null && !recipes.isEmpty()) {
			List<IRecipe> copy = new ArrayList<IRecipe>(recipes);
			Iterator<IRecipe> iter = copy.iterator();
			while (iter.hasNext()) {
				IRecipe r = iter.next();
				if (r == cause || r == previousCause) continue;
				List<Object> ingredients = new ArrayList<Object>();
				if (r instanceof ShapedRecipes) {
					for (ItemStack is : ((ShapedRecipes) r).recipeItems) {
						ingredients.add(is);
					}
				} else if (r instanceof ShapelessRecipes) {
					ingredients.addAll(((ShapelessRecipes) r).recipeItems);
				} else if (r instanceof ShapedOreRecipe) {
					for (Object o : ((ShapedOreRecipe) r).getInput()) {
						ingredients.add(o);
					}
				} else if (r instanceof ShapelessOreRecipe) {
					ingredients.addAll(((ShapelessOreRecipe) r).getInput());
				} else {
					continue;
				}
				for (Object o : ingredients) {
					if (o == null) continue;
					String[] dictionaryNames;
					ItemStack prototype;
					if (o instanceof String) {
						dictionaryNames = new String[]{(String) o};
						List<ItemStack> ores = OreDictionary.getOres(dictionaryNames[0]);
						prototype = ores == null || ores.isEmpty() ? null : ores.get(0);
					} else if (o instanceof List) {
						List<String> names = new ArrayList<String>();
						List<ItemStack> list = (List<ItemStack>)o;
						for (ItemStack is : list) {
							int[] ids = OreDictionary.getOreIDs(is);
							for (int id : ids) {
								names.add(OreDictionary.getOreName(id));
							}
						}
						prototype = list.isEmpty() ? null : list.get(0);
						dictionaryNames = names.toArray(new String[names.size()]);
					} else if (o instanceof ItemStack) {
						int[] ids = OreDictionary.getOreIDs(prototype = (ItemStack)o);
						String[] names = new String[ids.length];
						for (int i = 0; i < ids.length; i++) {
							names[i] = OreDictionary.getOreName(ids[i]);
						}
						dictionaryNames = names;
					} else if (o instanceof Item) {
						int[] ids = OreDictionary.getOreIDs(prototype = new ItemStack((Item)o));
						String[] names = new String[ids.length];
						for (int i = 0; i < ids.length; i++) {
							names[i] = OreDictionary.getOreName(ids[i]);
						}
						dictionaryNames = names;
					} else if (o instanceof Block) {
						int[] ids = OreDictionary.getOreIDs(prototype = new ItemStack((Block)o));
						String[] names = new String[ids.length];
						for (int i = 0; i < ids.length; i++) {
							names[i] = OreDictionary.getOreName(ids[i]);
						}
						dictionaryNames = names;
					} else {
						continue;
					}
					rubbleCount += processRecipes(prototype, r, cause, depth+1);
					for (String s : dictionaryNames) {
						String material;
						if (s.startsWith("gem")) {
							material = s.substring(3);
						} else if (s.startsWith("ingot")) {
							material = s.substring(5);
						} else {
							continue;
						}
						List<ItemStack> dusts = OreDictionary.getOres("dust"+material);
						if (dusts == null || dusts.isEmpty()) continue;
						if (worldObj.rand.nextInt(r.getRecipeOutput().stackSize*2) == 0) {
							ItemStack stack = dusts.get(0).copy();
							stack.stackSize = 1;
							addItem(stack);
							if (worldObj.rand.nextInt(4000) == 0) {
								addItem(new ItemStack(FarragoMod.DUST, 1, 4));
							}
						} else {
							if (worldObj.rand.nextInt(r.getRecipeOutput().stackSize*2) == 0) {
								rubbleCount++;
							}
						}
					}
				}
			}
		}
		return rubbleCount;
	}

	

	private void addItem(ItemStack itemStack) {
		if (container != null) {
			container.mergeItemStack(itemStack, 3, 12, false);
		} else {
			System.err.println("No container");
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord,
				this.zCoord) != this ? false
				: p_70300_1_.getDistanceSq(this.xCoord + 0.5D,
						this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return p_94041_1_ == 2 ? false
				: (p_94041_1_ == 1 ? TileEntityFurnace.isItemFuel(p_94041_2_) : true);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return p_94128_1_ == 0 ? slotsBottom : (p_94128_1_ == 1 ? slotsTop
				: slotsSides);
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		return this.isItemValidForSlot(p_102007_1_, p_102007_2_);
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		return p_102008_3_ != 0 || p_102008_1_ != 1
				|| p_102008_2_.getItem() == Items.bucket;
	}

	public void setContainer(ContainerScrapper containerScrapper) {
		container = containerScrapper;
	}
}
