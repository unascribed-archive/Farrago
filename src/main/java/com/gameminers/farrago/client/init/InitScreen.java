package com.gameminers.farrago.client.init;

import gminers.glasspane.GlassPane;
import gminers.glasspane.HorzAlignment;
import gminers.glasspane.component.PaneImage;
import gminers.glasspane.component.progress.PaneProgressRing;
import gminers.glasspane.component.text.PaneLabel;
import gminers.glasspane.event.PaneDisplayEvent;
import gminers.glasspane.event.WinchEvent;
import gminers.glasspane.listener.PaneEventHandler;
import gminers.glasspane.shadowbox.ImageTileShadowbox;
import gminers.kitchensink.RandomPool;

import java.util.Random;

import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.mutable.MutableBoolean;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InitScreen {
	private static final String[] prefixes = { "Initializing", "Actuating", "Separating", "Differentiating", "Reversing", "Preparing", "Digitizing", "Crunching", "Invoking", "Computing", "Programming", "Animating", "Exciting", "Energizing", "Inspiring", "Propelling", "Impelling", "Weakening", "Strengthening", "Comprehending", "Discerning", "Contrasting", "Marking", "Interpreting", "Associating", "Linking", "Depolarizing", "Executing", "Slashing", "Deleting", "Purging", "Clearing", "Extrapolating", "Randomizing", "Supersampling", "Subsampling", "Composing", "Chaining", "Cleaning", "Disinfecting", "Infecting", "Mining", "Shrinking", "Growing", "Augmenting", "Exaggerating", "Elaborating", "Reflecting", "Redirecting", "Dodging", "Throwing", "Implying", "Losing", "Winning", "Verifying", "Trusting", "Utilizing", "Clicking", "Priming", "Approaching", "Dropping", "Injecting", "Leveraging", "Synergizing", "Destroying" };
	private static final String[] devices = { "Einstein buffers", "the neutron flow", "the transmundane", "Aardvark caches", "octet arrays", "nybble caches", "SQL databases", "antimatter cannons", "something highly imaginative", "anti-brony cannons", "bash scripts", "HTTP servers", "tires", "defused bombs", "internal websites", "localhost accelerators", "musical compositions", "security audits", "arbitrary executables from an untrusted source", "unknown storage units", "millisecond traps", "electronic widgets", "timey-wimey detectors", "TARDIS arrays", "Shizunite pylons", "dummied out items", "accidentally left-in debug features", "mod-based explosion octets", "Rin in a bin", "sysadmin pagers", "the lights", "otherworldly creatures", "high resolution displays", "high-end graphics cards", "Dogecoins", "the moon", "colorful gems", "shiny gems", "ugly gems", "remote-sourced object files", "yellow tulips", "disconnected consoles", "vanilla features", "overpowered items", "the office towers", "central processing units", "FTP servers", "Minecraftians", "feeders of beasts", "aqueous accumulators", "Xbox Ones", "PS4s", "viruses", "derpy sentences", "Twitter followers", "the polarity", "the polarity of the neutron flow", "low resolution displays", "smartphones", "tablets", "nutmeg stations", "hydrographic network-broadcasters", "ectoplasmic changers", "transportation beams", "aqueous mines", "analytical defenders", "subterranean enthrallers", "geological radios", "ghostly radios", "cancellation rulers", "luminous sectors", "synthetic cutters", "aquatic slicers", "crypto bombs", "expansive venoms", "utility dirt compasses", "stealth shifters", "dormant chunk caches", "mods", "modification drivers", "sea-vitality explosives", "glint cloakers", "computer cyclones", "shrieking guardian alterers", "psychic cure-counterers", "radioactive cremators", "vitalizing gilded masters", "shroud concealers", "pollution strikers", "possessions", "the answer to the question", "exceptions", "untrusted executables", "unverified certificates", "expired milk", "stale cookies", "games", "The Game", "hexagons", "more hexagons", "lots of hexagons", "too many hexagons", "infinite hexagons", "cookies", "more cookies", "lots of cookies", "too many cookies", "infinite cookies", "eternal cookies", "Grandmatriarchs", "demonic cookies", "Cookieverse portals", "antimatter condensers", "Pokémon", "disintegrators", "unmanned spacecraft", "ghost cars", "ghosts", "mysterious boats", "nirvana", "melodies", "the bass", "explosive cats", "hovercraft", "DeLoreans", "roads", "the feels", "arrythmia", "Redditors", "Tumblr users", "the stock market", "synergy", "synergistic impulse engines", "warp reactors", "plasma coils", "antimatter injectors", "the captain's chair", "Romulans", "Klingons", "Tesla coils", "Edison impellers", "impulse manifolds" };
	public static void init() {
		final PaneProgressRing ring = new PaneProgressRing();
		ring.setOutlined(true);
		ring.setX(-1);
		ring.setY(-1);
		ring.setAutoResize(true);
		ring.setRelativeWidthOffset(2);
		ring.setRelativeHeightOffset(2);

		final PaneImage image = new PaneImage(new ResourceLocation("farrago", "textures/misc/box.png"));
		image.setWidth(140);
		image.setHeight(140);

		final PaneLabel verLabel = new PaneLabel("Farrago");
		verLabel.setColor(0x55AAFF);
		verLabel.setOutlined(true);
		verLabel.setShadow(false);
		verLabel.setAlignmentX(HorzAlignment.MIDDLE);
		verLabel.setAutoResizeWidth(true);
		verLabel.setRelativeWidth(1.0);

		final PaneLabel derpLabel = new PaneLabel("Initializing Einstein buffers...");
		derpLabel.setAlignmentX(HorzAlignment.MIDDLE);
		derpLabel.setColor(0xCCCCCC);
		derpLabel.setAutoResizeWidth(true);
		derpLabel.setRelativeWidth(1.0);

		final PaneLabel lessDerpLabel = new PaneLabel("(Preparing to initialize)");
		lessDerpLabel.setAlignmentX(HorzAlignment.MIDDLE);
		lessDerpLabel.setColor(0xCCCCCC);
		lessDerpLabel.setSmall(true);
		lessDerpLabel.setAutoResizeWidth(true);
		lessDerpLabel.setRelativeWidth(1.0);

		final MutableBoolean go = new MutableBoolean(true);
		final InitThread initThread = new InitThread(lessDerpLabel, ring);
		final GlassPane takeoverPane = new GlassPane() {
			{
				add(ring, image, derpLabel, verLabel, lessDerpLabel);
				final ImageTileShadowbox shadowbox = new ImageTileShadowbox(new ResourceLocation(
						"textures/blocks/wool_colored_white.png"));
				shadowbox.setTransparentWhenInWorld(false);
				shadowbox.setDarkened(true);
				setShadowbox(shadowbox);
			}

			@PaneEventHandler
			public void onWinch(final WinchEvent e) {
				image.setX((e.getNewWidth() / 2) - 64);
				image.setY((e.getNewHeight() / 2) - 96);
				verLabel.setY((e.getNewHeight() / 2) + 48);
				derpLabel.setY((e.getNewHeight() / 2) + 76);
				lessDerpLabel.setY((e.getNewHeight() / 2) + 88);
			}

			@PaneEventHandler
			public void onDisplay(final PaneDisplayEvent e) {
				initThread.start();
			}
		};
		new Thread("Derp Label Derper") {
			{
				setDaemon(true);
			}

			@Override
			public void run() {
				while (go.booleanValue()) {
					derpLabel.setText(getRandomLoadingMessage());
					for (int i = 0; i < 4; i++) {
						derpLabel.setText(derpLabel.getText() + ".");
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
		takeoverPane.takeover();
		go.setValue(false);
	}
	private static final Random rand = RandomPool.createRandomFacade();
	public static String getRandomLoadingMessage() {
		return prefixes[rand.nextInt(prefixes.length)] + " " + devices[rand.nextInt(devices.length)];
	}
}
