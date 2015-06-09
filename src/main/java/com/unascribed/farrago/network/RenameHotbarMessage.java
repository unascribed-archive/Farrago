package com.unascribed.farrago.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class RenameHotbarMessage implements IMessage {
	private String name;
	public RenameHotbarMessage() {} // Required constructor for Forge
	public RenameHotbarMessage(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
	}

}
