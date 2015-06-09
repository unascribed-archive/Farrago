package com.unascribed.farrago.client.encyclopedia;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.unascribed.farrago.FarragoMod;
import com.unascribed.farrago.selector.Selector;


public class Encyclopedia implements IResourceManagerReloadListener {
	private static final ResourceLocation resloc = new ResourceLocation("farrago", "encyclopedia.xml");
	private static final List<EncyclopediaEntry> entries = Lists.newArrayList();
	private static final StringBuilder builder = new StringBuilder();
	
	private final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	public static void process(ItemStack itemStack, EntityPlayer entityPlayer, List<String> toolTip, boolean showAdvancedItemTooltips) {
		if (itemStack == null || itemStack.getItem() == null) return;
		builder.setLength(0);
		boolean matched = false;
		for (EncyclopediaEntry entry : entries) {
			if (entry.getSelector().itemStackMatches(itemStack)) {
				if (entry.isIgnore()) break;
				matched = true;
				builder.append(entry.getBody());
			}
		}
		if (matched) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
				toolTip.add("\u00A7bInformation");
				toolTip.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(builder.toString(), 200));
			} else {
				toolTip.add("Hold \u00A7b<Shift> \u00A77for more information");
			}
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		entries.clear();
		try {
			IResource res = resourceManager.getResource(resloc);
			InputStream in = res.getInputStream();
			
			DocumentBuilder db = dbFactory.newDocumentBuilder();
			Document doc = db.parse(in);
			
			Node root = null;
			
			NodeList docnl = doc.getChildNodes();
			for (int i = 0; i < docnl.getLength(); i++) {
				Node node = docnl.item(i);
				if ("encyclopedia".equals(node.getNodeName())) {
					root = node;
					break;
				}
			}
			if (root == null) {
				throw new RuntimeException("Couldn't find encyclopedia root tag");
			}
			NodeList rootnl = root.getChildNodes();
			for (int i = 0; i < rootnl.getLength(); i++) {
				Node node = rootnl.item(i);
				if ("entry".equals(node.getNodeName())) {
					Selector selector = FarragoMod.parseSelector(node.getAttributes().getNamedItem("def").getNodeValue());
					String body = node.getTextContent().replaceAll("^\n", "").replace("\n", " ").replace("\t", "");
					entries.add(new EncyclopediaEntry(selector, body));
				} else if ("ignore".equals(node.getNodeName())) {
					Selector selector = FarragoMod.parseSelector(node.getAttributes().getNamedItem("def").getNodeValue());
					entries.add(new EncyclopediaEntry(selector, null));
				}
			}
		} catch (Exception e) {
			FarragoMod.log.error("Failed to load encyclopedia", e);
		}
	}

}
