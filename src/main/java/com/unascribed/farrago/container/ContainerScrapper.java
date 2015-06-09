package com.unascribed.farrago.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import com.unascribed.farrago.tileentity.TileEntityScrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerScrapper extends Container {
	private TileEntityScrapper tileScrapper;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public ContainerScrapper(InventoryPlayer p_i1812_1_, TileEntityScrapper p_i1812_2_)
    {
        tileScrapper = p_i1812_2_;
        tileScrapper.setContainer(this);
        addSlotToContainer(new Slot(p_i1812_2_, 0, 44, 16));
        addSlotToContainer(new Slot(p_i1812_2_, 1, 44, 52));
        addSlotToContainer(new SlotRestricted(p_i1812_2_, 2, 72, 48));
        
        addSlotToContainer(new SlotRestricted(p_i1812_2_,  3, 98 , 16));
        addSlotToContainer(new SlotRestricted(p_i1812_2_,  4, 116, 16));
        addSlotToContainer(new SlotRestricted(p_i1812_2_,  5, 134, 16));
        addSlotToContainer(new SlotRestricted(p_i1812_2_,  6, 98 , 34));
        addSlotToContainer(new SlotRestricted(p_i1812_2_,  7, 116, 34));
        addSlotToContainer(new SlotRestricted(p_i1812_2_,  8, 134, 34));
        addSlotToContainer(new SlotRestricted(p_i1812_2_,  9, 98 , 52));
        addSlotToContainer(new SlotRestricted(p_i1812_2_, 10, 116, 52));
        addSlotToContainer(new SlotRestricted(p_i1812_2_, 11, 134, 52));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                addSlotToContainer(new Slot(p_i1812_1_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            addSlotToContainer(new Slot(p_i1812_1_, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean mergeItemStack(ItemStack p_75135_1_, int p_75135_2_,
    		int p_75135_3_, boolean p_75135_4_) {
    	return super.mergeItemStack(p_75135_1_, p_75135_2_, p_75135_3_, p_75135_4_);
    }
    
	@Override
	public void addCraftingToCrafters(ICrafting p_75132_1_) {
		super.addCraftingToCrafters(p_75132_1_);
		p_75132_1_.sendProgressBarUpdate(this, 0, tileScrapper.furnaceCookTime);
		p_75132_1_.sendProgressBarUpdate(this, 1, tileScrapper.furnaceBurnTime);
		p_75132_1_.sendProgressBarUpdate(this, 2,
				tileScrapper.currentItemBurnTime);
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) crafters.get(i);

			if (lastCookTime != tileScrapper.furnaceCookTime) {
				icrafting.sendProgressBarUpdate(this, 0,
						tileScrapper.furnaceCookTime);
			}

			if (lastBurnTime != tileScrapper.furnaceBurnTime) {
				icrafting.sendProgressBarUpdate(this, 1,
						tileScrapper.furnaceBurnTime);
			}

			if (lastItemBurnTime != tileScrapper.currentItemBurnTime) {
				icrafting.sendProgressBarUpdate(this, 2,
						tileScrapper.currentItemBurnTime);
			}
		}

		lastCookTime = tileScrapper.furnaceCookTime;
		lastBurnTime = tileScrapper.furnaceBurnTime;
		lastItemBurnTime = tileScrapper.currentItemBurnTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int p_75137_1_, int p_75137_2_) {
		if (p_75137_1_ == 0) {
			tileScrapper.furnaceCookTime = p_75137_2_;
		}

		if (p_75137_1_ == 1) {
			tileScrapper.furnaceBurnTime = p_75137_2_;
		}

		if (p_75137_1_ == 2) {
			tileScrapper.currentItemBurnTime = p_75137_2_;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return tileScrapper.isUseableByPlayer(p_75145_1_);
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or
	 * you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int idx) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(idx);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (idx >= 2 && idx <= 11) {
				if (!mergeItemStack(itemstack1, 12, 39, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (idx != 1 && idx != 0) {
				if (TileEntityFurnace.isItemFuel(itemstack1)) {
					if (!mergeItemStack(itemstack1, 1, 2, false)) {
						return null;
					}
				} else if (!mergeItemStack(itemstack1, 0, 1, false)) {
					return null;
				} else if (idx >= 12 && idx < 30) {
					if (!mergeItemStack(itemstack1, 30, 39, false)) {
						return null;
					}
				} else if (idx >= 30 && idx < 39
						&& !mergeItemStack(itemstack1, 12, 30, false)) {
					return null;
				}
			} else if (!mergeItemStack(itemstack1, 12, 39, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(p_82846_1_, itemstack1);
		}
		if (itemstack != null && itemstack.stackSize == 0) return null;
		return itemstack;
	}

}
