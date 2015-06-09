package com.unascribed.farrago.client.pane;

import com.unascribed.farrago.client.tools.ToolBlockRenderer;

import gminers.glasspane.GlassPane;
import gminers.glasspane.component.PaneScrollPanel;
import gminers.glasspane.component.button.PaneButton;
import gminers.glasspane.component.text.PaneLabel;

public class PaneTools extends GlassPane {
	private final PaneScrollPanel panel = new PaneScrollPanel();
	private final GlassPane[] tools = {
		new ToolBlockRenderer()
	};
	public PaneTools() {
		setRevertAllowed(true);
		add(PaneLabel.createTitleLabel("Farrago Tools"), PaneButton.createDoneButton());
		panel.setY(14);
		panel.setAutoResizeWidth(true);
		panel.setAutoResizeHeight(true);
		panel.setRelativeHeightOffset(-52);
		add(panel);
		int y = 5;
		for (final GlassPane t : tools) {
			PaneButton button = new PaneButton(t.getName());
			button.setAutoPositionX(true);
			button.setRelativeX(0.5);
			button.setRelativeXOffset(-100);
			button.setY(y);
			button.registerActivationListener(new Runnable() {
				
				@Override
				public void run() {
					t.show();
				}
			});
			panel.add(button);
			y += 25;
		}
	}
}
