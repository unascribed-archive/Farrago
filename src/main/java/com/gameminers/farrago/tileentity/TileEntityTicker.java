package com.gameminers.farrago.tileentity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTicker extends TileEntity {
	@Override
	public void updateEntity() {
		Block b = worldObj.getBlock(xCoord, yCoord, zCoord);
		if (worldObj.getTotalWorldTime() % b.tickRate(worldObj) == 0) {
			b.updateTick(worldObj, xCoord, yCoord, zCoord, worldObj.rand);
		}
	}
}
