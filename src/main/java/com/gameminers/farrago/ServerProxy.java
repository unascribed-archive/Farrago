package com.gameminers.farrago;

public class ServerProxy implements Proxy {

	@Override
	public void postInit() {
		for (Iota sub : FarragoMod.getSubMods()) {
			sub.serverPostInit();
		}
	}

	@Override
	public void init() {
		
	}

	@Override
	public void preInit() {
		
	}

}
