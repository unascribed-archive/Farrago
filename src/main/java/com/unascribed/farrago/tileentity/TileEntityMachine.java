package com.unascribed.farrago.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityMachine extends TileEntity {
	private int direction;
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("Direction", direction);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Direction", direction);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -255, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		setDirection(pkt.func_148857_g().getInteger("Direction"));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		setDirection(tag.getInteger("Direction"));
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public int getDirection() {
		return direction;
	}
	public abstract boolean isOn();
}
