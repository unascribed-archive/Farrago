package com.unascribed.farrago.client.pane;

import com.unascribed.farrago.FarragoMod;
import com.unascribed.farrago.network.RenameHotbarMessage;

import gminers.glasspane.GlassPane;
import gminers.glasspane.component.button.PaneButton;
import gminers.glasspane.component.text.PaneTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class PaneRenameHotbar extends GlassPane {
	public PaneRenameHotbar(int idx, ItemStack stack) {
		setRevertAllowed(true);
		final PaneTextField field = new PaneTextField(FarragoMod.UTILITY_BELT.getRowName(stack, idx));
		field.setAutoPosition(true);
		field.setRelativeX(0.5);
		field.setRelativeXOffset(-field.getWidth()/2);
		field.setRelativeYOffset(-field.getHeight()/2);
		field.setRelativeY(0.5);
		PaneButton done = new PaneButton("Done");
		done.registerActivationListener(new Runnable() {
			@Override
			public void run() {
				FarragoMod.CHANNEL.sendToServer(new RenameHotbarMessage(field.getText()));
				Minecraft.getMinecraft().displayGuiScreen(null);
			}
		});
		done.setAutoPosition(true);
		done.setRelativeX(0.5);
		done.setRelativeXOffset(-done.getWidth()/2);
		done.setRelativeYOffset((-field.getHeight()/2)+60);
		done.setRelativeY(0.5);
		add(field, done);
		setFocusedComponent(field);
	}
}
