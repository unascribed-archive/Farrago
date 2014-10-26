package com.gameminers.farrago.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import com.gameminers.farrago.tileentity.TileEntityScrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerScrapper extends Container {
	private static final class RestrictedSlot extends Slot {
		private RestrictedSlot(IInventory p_i1824_1_, int p_i1824_2_,
				int p_i1824_3_, int p_i1824_4_) {
			super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
		}

		@Override
		public boolean isItemValid(ItemStack p_75214_1_) {
			return false;
		}
	}

	private TileEntityScrapper tileScrapper;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public ContainerScrapper(InventoryPlayer p_i1812_1_, TileEntityScrapper p_i1812_2_)
    {
        this.tileScrapper = p_i1812_2_;
        tileScrapper.setContainer(this);
        this.addSlotToContainer(new Slot(p_i1812_2_, 0, 44, 16));
        this.addSlotToContainer(new Slot(p_i1812_2_, 1, 44, 52));
        this.addSlotToContainer(new RestrictedSlot(p_i1812_2_, 2, 72, 48));
        
        this.addSlotToContainer(new RestrictedSlot(p_i1812_2_,  3, 98 , 16));
        this.addSlotToContainer(new RestrictedSlot(p_i1812_2_,  4, 116, 16));
        this.addSlotToContainer(new RestrictedSlot(p_i1812_2_,  5, 134, 16));
        this.addSlotToContainer(new RestrictedSlot(p_i1812_2_,  6, 98 , 34));
        this.addSlotToContainer(new RestrictedSlot(p_i1812_2_,  7, 116, 34));
        this.addSlotToContainer(new RestrictedSlot(p_i1812_2_,  8, 134, 34));
        this.addSlotToContainer(new RestrictedSlot(p_i1812_2_,  9, 98 , 52));
        this.addSlotToContainer(new RestrictedSlot(p_i1812_2_, 10, 116, 52));
        this.addSlotToContainer(new RestrictedSlot(p_i1812_2_, 11, 134, 52));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(p_i1812_1_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(p_i1812_1_, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean mergeItemStack(ItemStack p_75135_1_, int p_75135_2_,
    		int p_75135_3_, boolean p_75135_4_) {
    	return super.mergeItemStack(p_75135_1_, p_75135_2_, p_75135_3_, p_75135_4_);
    }
    
    public void addCraftingToCrafters(ICrafting p_75132_1_)
    {
        super.addCraftingToCrafters(p_75132_1_);
        p_75132_1_.sendProgressBarUpdate(this, 0, this.tileScrapper.furnaceCookTime);
        p_75132_1_.sendProgressBarUpdate(this, 1, this.tileScrapper.furnaceBurnTime);
        p_75132_1_.sendProgressBarUpdate(this, 2, this.tileScrapper.currentItemBurnTime);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.lastCookTime != this.tileScrapper.furnaceCookTime)
            {
                icrafting.sendProgressBarUpdate(this, 0, this.tileScrapper.furnaceCookTime);
            }

            if (this.lastBurnTime != this.tileScrapper.furnaceBurnTime)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.tileScrapper.furnaceBurnTime);
            }

            if (this.lastItemBurnTime != this.tileScrapper.currentItemBurnTime)
            {
                icrafting.sendProgressBarUpdate(this, 2, this.tileScrapper.currentItemBurnTime);
            }
        }

        this.lastCookTime = this.tileScrapper.furnaceCookTime;
        this.lastBurnTime = this.tileScrapper.furnaceBurnTime;
        this.lastItemBurnTime = this.tileScrapper.currentItemBurnTime;
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int p_75137_1_, int p_75137_2_)
    {
        if (p_75137_1_ == 0)
        {
            this.tileScrapper.furnaceCookTime = p_75137_2_;
        }

        if (p_75137_1_ == 1)
        {
            this.tileScrapper.furnaceBurnTime = p_75137_2_;
        }

        if (p_75137_1_ == 2)
        {
            this.tileScrapper.currentItemBurnTime = p_75137_2_;
        }
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return this.tileScrapper.isUseableByPlayer(p_75145_1_);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (p_82846_2_ >= 2 && p_82846_2_ <= 11)
            {
                if (!this.mergeItemStack(itemstack1, 12, 39, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (p_82846_2_ != 1 && p_82846_2_ != 0)
            {
            	if (TileEntityFurnace.isItemFuel(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 1, 11, false)) {
                        return null;
                    }
                } else if (!this.mergeItemStack(itemstack1, 0, 2, false)) {
                    return null;
                } else if (p_82846_2_ >= 12 && p_82846_2_ < 30) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                        return null;
                    }
                } else if (p_82846_2_ >= 30 && p_82846_2_ < 39 && !this.mergeItemStack(itemstack1, 12, 30, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 12, 39, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(p_82846_1_, itemstack1);
        }

        return itemstack;
    }

}
