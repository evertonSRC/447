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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.config.IllusoryEquipmentConfig;
import org.l2jmobius.gameserver.data.holders.VirtualItemHolder;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.item.ItemTemplate;

/**
 * @author CostyKiller
 */
public class VirtualItemData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(VirtualItemData.class.getName());
	
	private static final Map<Integer, Map<Integer, VirtualItemHolder>> VIRTUAL_ITEMS = new HashMap<>();
	
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
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + VIRTUAL_ITEMS.size() + " virtual items.");
		}
		else
		{
			LOGGER.info(getClass().getSimpleName() + ": System is disabled.");
		}
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		for (Node n = document.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("virtualItem".equalsIgnoreCase(d.getNodeName()))
					{
						NamedNodeMap attrs = d.getAttributes();
						Node att;
						final StatSet set = new StatSet();
						for (int i = 0; i < attrs.getLength(); i++)
						{
							att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						final int indexMain = parseInteger(attrs, "indexMain");
						for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling())
						{
							attrs = b.getAttributes();
							if ("virtualItemStat".equalsIgnoreCase(b.getNodeName()))
							{
								final long slot = parseLong(attrs, "slot");
								final int indexSub = parseInteger(attrs, "indexSub");
								final int itemId = parseInteger(attrs, "itemId");
								final int enchant = parseInteger(attrs, "enchant");
								final int costVISPoint = parseInteger(attrs, "costVISPoint");
								
								final ItemTemplate itemTemplate = ItemData.getInstance().getTemplate(itemId);
								if (itemTemplate != null)
								{
									final VirtualItemHolder template = new VirtualItemHolder(indexMain, slot, indexSub, itemId, enchant, costVISPoint);
									VIRTUAL_ITEMS.computeIfAbsent(indexMain, key -> new HashMap<>()).put(indexSub, template);
								}
								else
								{
									LOGGER.warning(getClass().getSimpleName() + ": Could not find item template for id " + itemId);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Retrieves the virtual item ID associated with a specified virtual item index.
	 * @param mainIndex the unique index of the virtual item to retrieve the item ID for
	 * @return the virtual item ID, or {@code 0} if no virtual item is associated with the specified index
	 */
	public VirtualItemHolder getVirtualItem(int mainIndex, int subIndex)
	{
		final Map<Integer, VirtualItemHolder> subItems = VIRTUAL_ITEMS.get(mainIndex);
		if (subItems == null)
		{
			return null;
		}
		
		return subItems.get(subIndex);
	}
	
	/**
	 * Retrieves the virtual item ID associated with a specified virtual item index.
	 * @param mainIndex the unique index of the virtual item to retrieve the item ID for
	 * @return the virtual item ID, or {@code 0} if no virtual item is associated with the specified index
	 */
	public VirtualItemHolder getVirtualItemByItem(int itemId, int enchant, long slot)
	{
		for (Map<Integer, VirtualItemHolder> subItems : VIRTUAL_ITEMS.values())
		{
			for (VirtualItemHolder holder : subItems.values())
			{
				if ((holder.getItemId() == itemId) && (holder.getEnchant() == enchant) && (holder.getSlot() == slot))
				{
					return holder;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the virtual item enchant level associated with a specified virtual item ID.
	 * @param mainIndex the unique ID of the virtual item to retrieve the enchant level for
	 * @return the enchant level of the virtual item, or {@code 0} if no virtual item is associated with the specified ID
	 */
	/**
	 * Retrieves a collection of all available virtual items data.
	 * @return a collection of {@code RelicDataHolder} objects representing all virtual items
	 */
	public Collection<VirtualItemHolder> getVirtualItems()
	{
		return VIRTUAL_ITEMS.values().stream().map(Map::values).flatMap(Collection::stream).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	public Collection<VirtualItemHolder> getVirtualItems(int mainIndex)
	{
		final Map<Integer, VirtualItemHolder> subItems = VIRTUAL_ITEMS.get(mainIndex);
		if (subItems == null)
		{
			return null;
		}
		
		return subItems.values();
	}
	
	public static VirtualItemData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final VirtualItemData INSTANCE = new VirtualItemData();
	}
}
