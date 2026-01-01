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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.config.IllusoryEquipmentConfig;
import org.l2jmobius.gameserver.data.holders.VirtualItemHolder;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * @author CostyKiller
 */
public class VirtualItemData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(VirtualItemData.class.getName());
	
	private static final Map<Integer, Map<Integer, VirtualItemHolder>> VIRTUAL_ITEMS = new HashMap<>();
	private static final Set<Integer> MAIN_INDEXES = new TreeSet<>();
	
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
		MAIN_INDEXES.clear();
		
		if (IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_ENABLED)
		{
			parseDatapackFile("data/VirtualItemData.xml");
		}
		
		if (!VIRTUAL_ITEMS.isEmpty())
		{
			final int totalItems = VIRTUAL_ITEMS.values().stream().mapToInt(Map::size).sum();
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + totalItems + " virtual items across " + MAIN_INDEXES.size() + " main indexes.");
			if (IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DEBUG_ENABLED)
			{
				final List<VirtualItemHolder> samples = new ArrayList<>();
				for (Map<Integer, VirtualItemHolder> subItems : VIRTUAL_ITEMS.values())
				{
					for (VirtualItemHolder holder : subItems.values())
					{
						samples.add(holder);
						if (samples.size() >= 3)
						{
							break;
						}
					}
					if (samples.size() >= 3)
					{
						break;
					}
				}
				
				for (VirtualItemHolder holder : samples)
				{
					LOGGER.info(getClass().getSimpleName() + ": Example indexMain=" + holder.getIndexMain() + " indexSub=" + holder.getIndexSub() + " itemId=" + holder.getItemId());
				}
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
		for (Node n = document.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("virtualItem".equalsIgnoreCase(d.getNodeName()))
					{
						NamedNodeMap attrs = d.getAttributes();
						
						final int indexMain = parseInteger(attrs, "indexMain");
						MAIN_INDEXES.add(indexMain);
						for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling())
						{
							if ("virtualItemStat".equalsIgnoreCase(b.getNodeName()))
							{
								attrs = b.getAttributes();
								final long slot = parseLong(attrs, "slot");
								final int indexSub = parseInteger(attrs, "indexSub");
								final int itemId = parseInteger(attrs, "itemId");
								final int enchant = parseInteger(attrs, "enchant");
								final int costVISPoint = parseInteger(attrs, "costVISPoint");
								
								final ItemTemplate itemTemplate = ItemData.getInstance().getTemplate(itemId);
								final Skill skillTemplate = SkillData.getInstance().getSkill(itemId, enchant);
								if ((itemTemplate == null) && (skillTemplate == null))
								{
									LOGGER.warning(getClass().getSimpleName() + ": Could not find item or skill template for id " + itemId + " (indexMain=" + indexMain + ", indexSub=" + indexSub + ").");
									continue;
								}
								
								if (VIRTUAL_ITEMS.computeIfAbsent(indexMain, key -> new HashMap<>()).containsKey(indexSub))
								{
									LOGGER.warning(getClass().getSimpleName() + ": Duplicate virtual item indexMain=" + indexMain + " indexSub=" + indexSub + " found in " + file.getName());
									continue;
								}
								
								final VirtualItemHolder template = new VirtualItemHolder(indexMain, slot, indexSub, itemId, enchant, costVISPoint);
								VIRTUAL_ITEMS.get(indexMain).put(indexSub, template);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Retrieves the virtual item holder associated with a specified virtual item index pair.
	 * @param mainIndex the unique main index of the virtual item
	 * @param subIndex the unique sub index of the virtual item
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
	 * @param subIndex the unique sub index of the virtual item to retrieve the item ID for
	 * @return the virtual item ID, or {@code 0} if no virtual item is associated with the specified index
	 */
	public int getVirtualItemId(int mainIndex, int subIndex)
	{
		final VirtualItemHolder holder = getVirtualItem(mainIndex, subIndex);
		return holder != null ? holder.getItemId() : 0;
	}
	
	/**
	 * Retrieves the virtual item enchant level associated with a specified virtual item ID.
	 * @param mainIndex the unique ID of the virtual item to retrieve the enchant level for
	 * @param subIndex the unique sub index of the virtual item to retrieve the enchant level for
	 * @return the enchant level of the virtual item, or {@code 0} if no virtual item is associated with the specified ID
	 */
	public int getVirtualItemEnchant(int mainIndex, int subIndex)
	{
		final VirtualItemHolder holder = getVirtualItem(mainIndex, subIndex);
		return holder != null ? holder.getEnchant() : 0;
	}
	
	/**
	 * Retrieves a collection of all available virtual items data.
	 * @return a collection of {@code RelicDataHolder} objects representing all virtual items
	 */
	public Collection<VirtualItemHolder> getVirtualItems()
	{
		final List<VirtualItemHolder> results = new ArrayList<>();
		for (Map<Integer, VirtualItemHolder> subItems : VIRTUAL_ITEMS.values())
		{
			results.addAll(subItems.values());
		}
		return results;
	}
	
	/**
	 * Retrieves all available virtual items for a main index.
	 * @param mainIndex the main index
	 * @return a collection of virtual items
	 */
	public Collection<VirtualItemHolder> getVirtualItems(int mainIndex)
	{
		final Map<Integer, VirtualItemHolder> subItems = VIRTUAL_ITEMS.get(mainIndex);
		if (subItems == null)
		{
			return Collections.emptyList();
		}
		return subItems.values();
	}
	
	/**
	 * Retrieves all main indexes that have virtual items defined.
	 * @return a set of main indexes
	 */
	public Set<Integer> getMainIndexes()
	{
		return MAIN_INDEXES;
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
