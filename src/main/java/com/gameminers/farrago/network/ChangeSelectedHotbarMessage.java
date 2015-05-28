package com.gameminers.farrago.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ChangeSelectedHotbarMessage implements IMessage {
	private boolean direction;
	public ChangeSelectedHotbarMessage() {} // Required constructor for Forge
	public ChangeSelectedHotbarMessage(boolean direction) {
		this.direction = direction;
	}

	public boolean getDirection() {
		return direction;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		direction = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(direction);
	}

}
