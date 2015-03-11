package com.gameminers.farrago.client.encyclopedia;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.selector.ItemSelector;
import com.gameminers.farrago.selector.NullSelector;
import com.gameminers.farrago.selector.Selector;
import com.google.common.collect.Lists;


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
					Selector selector = parseDef(node.getAttributes().getNamedItem("def").getNodeValue());
					String body = node.getTextContent().replaceAll("^\n", "").replace("\n", " ").replace("\t", "");
					entries.add(new EncyclopediaEntry(selector, body));
				} else if ("ignore".equals(node.getNodeName())) {
					Selector selector = parseDef(node.getAttributes().getNamedItem("def").getNodeValue());
					entries.add(new EncyclopediaEntry(selector, null));
				}
			}
		} catch (Exception e) {
			FarragoMod.log.error("Failed to load encyclopedia", e);
		}
	}

	private Selector parseDef(String def) throws NBTException {
		int meta = 32767;
		NBTTagCompound tag = null;
		boolean lenientTag = def.endsWith("}?");
		if (def.contains("{") && def.contains("}")) {
			String mojangson = def.substring(def.indexOf('{'), def.indexOf('}')+1);
			tag = (NBTTagCompound) JsonToNBT.func_150315_a(mojangson);
			def = def.substring(0, def.indexOf('{'));
		}
		if (def.contains("@")) {
			meta = Integer.parseInt(def.substring(def.indexOf('@')+1));
			def = def.substring(0, def.indexOf('@'));
		}
		ItemStack stack = new ItemStack((Item) Item.itemRegistry.getObject(def), 1, meta);
		if (stack.getItem() == null) return new NullSelector();
		stack.setTagCompound(tag);
		return new ItemSelector(stack, lenientTag);
	}

}
