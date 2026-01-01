/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.l2jmobius.gameserver.data.xml;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.config.IllusoryEquipmentConfig;
import org.l2jmobius.gameserver.config.ServerConfig;
import org.l2jmobius.gameserver.data.holders.VirtualItemEntry;
import org.l2jmobius.gameserver.data.holders.VirtualItemGroup;
import org.l2jmobius.gameserver.model.actor.enums.player.VirtualSlot;

/**
 * @author Mobius
 */
public class VirtualItemData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(VirtualItemData.class.getName());
	
	private static final Map<Integer, VirtualItemGroup> VIRTUAL_ITEMS = new HashMap<>();
	private static final Map<String, Integer> VIRTUAL_SLOT_IDS = new HashMap<>();
	
	static
	{
		for (VirtualSlot slot : VirtualSlot.values())
		{
			registerVirtualSlot(slot.getAlias(), slot.getId());
		}
	}
	
	protected VirtualItemData()
	{
		if (IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_ENABLED)
		{
			load();
		}
	}
	
	@Override
	public void load()
	{
		VIRTUAL_ITEMS.clear();
		
		if (IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_ENABLED)
		{
			parseDatapackFile("data/VirtualItemData.xml");
		}
		
		if (!VIRTUAL_ITEMS.isEmpty())
		{
			int entryCount = 0;
			for (VirtualItemGroup group : VIRTUAL_ITEMS.values())
			{
				entryCount += group.getEntries().size();
			}
			
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + VIRTUAL_ITEMS.size() + " virtual item groups.");
			if (ServerConfig.VIRTUAL_ITEM_DEBUG)
			{
				LOGGER.info(getClass().getSimpleName() + ": Loaded " + entryCount + " virtual item entries.");
			}
		}
		else
		{
			LOGGER.info(getClass().getSimpleName() + ": System is disabled.");
		}
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		int entryCount = 0;
		for (Node n = document.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("virtualItem".equalsIgnoreCase(d.getNodeName()))
					{
						final NamedNodeMap attrs = d.getAttributes();
						final Integer indexMain = parseRequiredInteger(attrs, "indexMain", "virtualItem");
						if (indexMain == null)
						{
							continue;
						}
						
						final VirtualItemGroup group = VIRTUAL_ITEMS.computeIfAbsent(indexMain, VirtualItemGroup::new);
						for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling())
						{
							if (!"virtualItemStat".equalsIgnoreCase(b.getNodeName()))
							{
								continue;
							}
							
							final NamedNodeMap itemAttrs = b.getAttributes();
							final SlotMapping slotMapping = parseSlot(itemAttrs, indexMain);
							if (slotMapping == null)
							{
								continue;
							}
							
							final Integer indexSub = parseRequiredInteger(itemAttrs, "indexSub", "virtualItemStat(indexMain=" + indexMain + ")");
							final Integer itemId = parseRequiredInteger(itemAttrs, "itemId", "virtualItemStat(indexMain=" + indexMain + ")");
							final Integer enchant = parseRequiredInteger(itemAttrs, "enchant", "virtualItemStat(indexMain=" + indexMain + ")");
							final Integer costVISPoint = parseRequiredInteger(itemAttrs, "costVISPoint", "virtualItemStat(indexMain=" + indexMain + ")");
							if ((indexSub == null) || (itemId == null) || (enchant == null) || (costVISPoint == null))
							{
								continue;
							}
							
							if (costVISPoint < 0)
							{
								LOGGER.warning(getClass().getSimpleName() + ": Negative costVISPoint for indexMain=" + indexMain + " indexSub=" + indexSub + ", skipping.");
								continue;
							}
							
							if (itemId <= 0)
							{
								LOGGER.warning(getClass().getSimpleName() + ": Invalid itemId for indexMain=" + indexMain + " indexSub=" + indexSub + ", skipping.");
								continue;
							}
							
							if (group.hasEntry(indexSub))
							{
								LOGGER.warning(getClass().getSimpleName() + ": Duplicate virtualItemStat for indexMain=" + indexMain + " indexSub=" + indexSub + ", skipping.");
								continue;
							}
							
							final VirtualItemEntry entry = new VirtualItemEntry(indexMain, indexSub, slotMapping.slotIdClient, slotMapping.virtualSlotId, slotMapping.slotAlias, itemId, enchant, costVISPoint);
							group.addEntry(entry);
							entryCount++;
						}
					}
				}
			}
		}
		
		if (ServerConfig.VIRTUAL_ITEM_DEBUG)
		{
			LOGGER.info(getClass().getSimpleName() + ": Parsed " + entryCount + " virtual item entries from " + file.getName() + ".");
		}
	}
	
	public VirtualItemGroup getVirtualItemGroup(int mainIndex)
	{
		return VIRTUAL_ITEMS.get(mainIndex);
	}
	
	public VirtualItemEntry getVirtualItemEntry(int mainIndex, int indexSub)
	{
		final VirtualItemGroup group = VIRTUAL_ITEMS.get(mainIndex);
		return group != null ? group.getEntry(indexSub) : null;
	}
	
	public Collection<VirtualItemGroup> getVirtualItemGroups()
	{
		return Collections.unmodifiableCollection(VIRTUAL_ITEMS.values());
	}
	
	public static VirtualItemData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final VirtualItemData INSTANCE = new VirtualItemData();
	}
	
	private static void registerVirtualSlot(String alias, int slotId)
	{
		VIRTUAL_SLOT_IDS.put(alias.toLowerCase(Locale.ROOT), slotId);
	}
	
	private static SlotMapping parseSlot(NamedNodeMap attributes, int indexMain)
	{
		final Node slotNode = attributes.getNamedItem("slot");
		if (slotNode == null)
		{
			LOGGER.warning(VirtualItemData.class.getSimpleName() + ": Missing slot attribute for indexMain=" + indexMain + ".");
			return null;
		}
		
		final String slotValue = slotNode.getNodeValue().trim();
		if (slotValue.isEmpty())
		{
			LOGGER.warning(VirtualItemData.class.getSimpleName() + ": Empty slot value for indexMain=" + indexMain + ".");
			return null;
		}
		
		try
		{
			return new SlotMapping(Long.parseLong(slotValue), 0, null);
		}
		catch (NumberFormatException e)
		{
			final String alias = slotValue.toLowerCase(Locale.ROOT);
			final Integer virtualSlotId = VIRTUAL_SLOT_IDS.get(alias);
			if (virtualSlotId == null)
			{
				LOGGER.warning(VirtualItemData.class.getSimpleName() + ": Unknown slot alias '" + slotValue + "' for indexMain=" + indexMain + ".");
				return null;
			}
			
			return new SlotMapping(0L, virtualSlotId, alias);
		}
	}
	
	private static Integer parseRequiredInteger(NamedNodeMap attributes, String name, String context)
	{
		final Node node = attributes.getNamedItem(name);
		if (node == null)
		{
			LOGGER.warning(VirtualItemData.class.getSimpleName() + ": Missing " + name + " in " + context + ".");
			return null;
		}
		
		try
		{
			return Integer.decode(node.getNodeValue());
		}
		catch (NumberFormatException e)
		{
			LOGGER.warning(VirtualItemData.class.getSimpleName() + ": Invalid " + name + " value '" + node.getNodeValue() + "' in " + context + ".");
			return null;
		}
	}
	
	private static class SlotMapping
	{
		private final long slotIdClient;
		private final int virtualSlotId;
		private final String slotAlias;
		
		private SlotMapping(long slotIdClient, int virtualSlotId, String slotAlias)
		{
			this.slotIdClient = slotIdClient;
			this.virtualSlotId = virtualSlotId;
			this.slotAlias = slotAlias;
		}
	}
}
