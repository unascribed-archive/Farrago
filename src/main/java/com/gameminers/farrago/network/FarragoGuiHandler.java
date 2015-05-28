package com.gameminers.farrago.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.gameminers.farrago.container.ContainerCellFiller;
import com.gameminers.farrago.container.ContainerCombustor;
import com.gameminers.farrago.container.ContainerScrapper;
import com.gameminers.farrago.gui.GuiCellFillerInventory;
import com.gameminers.farrago.gui.GuiCombustorInventory;
import com.gameminers.farrago.gui.GuiScrapperInventory;
import com.gameminers.farrago.tileentity.TileEntityCellFiller;
import com.gameminers.farrago.tileentity.TileEntityCombustor;
import com.gameminers.farrago.tileentity.TileEntityScrapper;

import cpw.mods.fml.common.network.IGuiHandler;

public class FarragoGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == 0) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityCombustor) {
				return new ContainerCombustor(player.inventory, (TileEntityCombustor) te);
			}
		} else if (id == 1) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityScrapper) {
				return new ContainerScrapper(player.inventory, (TileEntityScrapper) te);
			}
		} else if (id == 2) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityCellFiller) {
				return new ContainerCellFiller(player.inventory, (TileEntityCellFiller) te);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == 0) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityCombustor) {
				return new GuiCombustorInventory(player.inventory, (TileEntityCombustor) te);
			}
		} else if (id == 1) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityScrapper) {
				return new GuiScrapperInventory(player.inventory, (TileEntityScrapper) te);
			}
		} else if (id == 2) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityCellFiller) {
				return new GuiCellFillerInventory(player.inventory, (TileEntityCellFiller) te);
			}
		}
		return null;
	}

}
