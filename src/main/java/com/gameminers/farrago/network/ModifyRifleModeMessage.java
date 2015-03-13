package com.gameminers.farrago.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ModifyRifleModeMessage implements IMessage {
	private boolean set;
	private int value;
	public ModifyRifleModeMessage() {} // Required constructor for Forge
	public ModifyRifleModeMessage(boolean set, int value) {
		this.set = set;
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isSet() {
		return set;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		set = buf.readBoolean();
		value = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(set);
		buf.writeByte(value);
	}

}
