package com.gameminers.farrago.client.pane;

import gminers.glasspane.GlassPane;
import gminers.glasspane.HorzAlignment;
import gminers.glasspane.component.text.PaneLabel;
import gminers.glasspane.event.PaneOverlayEvent;
import gminers.glasspane.listener.PaneEventHandler;

import com.gameminers.farrago.FarragoMod;

public class PaneBranding extends GlassPane {
	private PaneLabel brand = new PaneLabel();
	public PaneBranding() {
		brand.setAutoResizeWidth(true);
		brand.setAutoPositionY(true);
		brand.setAlignmentX(HorzAlignment.MIDDLE);
		brand.setRelativeY(0.25);
		brand.setRelativeYOffset(32);
		add(brand);
	}
	@PaneEventHandler
	public void onOverlay(PaneOverlayEvent e) {
		brand.setText(FarragoMod.brand);
	}
}
