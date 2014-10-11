package com.gameminers.farrago;

import com.gameminers.farrago.kahur.client.InitScreen;


public class ClientProxy implements Proxy {

	@Override
	public void postInit() {
		for (Iota sub : FarragoMod.getSubMods()) {
			sub.clientPostInit();
		}
		InitScreen.init();
	}

	@Override
	public void init() {
	}

}
