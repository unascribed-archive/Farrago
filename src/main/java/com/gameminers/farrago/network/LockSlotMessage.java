package com.gameminers.farrago.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class LockSlotMessage implements IMessage {
	private byte slot;
	public LockSlotMessage() {} // Required constructor for Forge
	public LockSlotMessage(int slot) {
		this.slot = (byte)slot;
	}

	public byte getSlot() {
		return slot;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		slot = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(slot);
	}

}
