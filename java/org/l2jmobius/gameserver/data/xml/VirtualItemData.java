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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.virtual.VirtualSlot;
import org.l2jmobius.gameserver.model.item.virtual.VirtualItemGroup;
import org.l2jmobius.gameserver.model.item.virtual.VirtualItemTemplate;

/**
 * @author Mobius
 */
public class VirtualItemData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(VirtualItemData.class.getName());
	private static final String DATA_PATH = "data/VirtualItemData.xml";
	
	private final Map<Integer, VirtualItemGroup> _groups = new HashMap<>();
	private boolean _loaded;
	
	protected VirtualItemData()
	{
		load();
	}
	
	@Override
	public synchronized void load()
	{
		_loaded = false;
		_groups.clear();
		final File file = new File(".", DATA_PATH);
		if (!isValidXmlFile(file))
		{
			LOGGER.warning(getClass().getSimpleName() + ": Missing or invalid " + DATA_PATH + "; virtual item UI data will not be available.");
			return;
		}
		
		parseFile(file);
		if (_loaded)
		{
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + getVirtualItemCount() + " virtual items.");
		}
		else
		{
			LOGGER.warning(getClass().getSimpleName() + ": Failed to load " + DATA_PATH + "; virtual item UI data will not be available.");
		}
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		_loaded = true;
		for (Node n = document.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("virtualItem".equalsIgnoreCase(d.getNodeName()))
					{
						final NamedNodeMap attrs = d.getAttributes();
						final int indexMain = parseInteger(attrs, "indexMain");
						if (indexMain < 0)
						{
							LOGGER.severe(getClass().getSimpleName() + ": Invalid indexMain " + indexMain + " in " + file.getName());
							continue;
						}
						
						final VirtualItemGroup group = _groups.computeIfAbsent(indexMain, VirtualItemGroup::new);
						for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling())
						{
							if (!"virtualItemStat".equalsIgnoreCase(b.getNodeName()))
							{
								continue;
							}
							
							final NamedNodeMap itemAttrs = b.getAttributes();
							try
							{
								final int indexSub = parseInteger(itemAttrs, "indexSub");
								if (indexSub < 0)
								{
									LOGGER.severe(getClass().getSimpleName() + ": Invalid indexSub " + indexSub + " in group " + indexMain);
									continue;
								}
								
								final String slotValue = parseString(itemAttrs, "slot");
								final VirtualSlot slot = parseVirtualSlot(slotValue);
								if (slot == null)
								{
									LOGGER.severe(getClass().getSimpleName() + ": Invalid slot " + slotValue + " for indexMain " + indexMain + " indexSub " + indexSub);
									continue;
								}
								
								final int itemId = parseInteger(itemAttrs, "itemId");
								final ItemTemplate item = ItemData.getInstance().getTemplate(itemId);
								if (item == null)
								{
									LOGGER.severe(getClass().getSimpleName() + ": Item template null for itemId " + itemId + " in indexMain " + indexMain + " indexSub " + indexSub);
									continue;
								}
								
								final int enchant = parseInteger(itemAttrs, "enchant", 0);
								if (enchant < 0)
								{
									LOGGER.severe(getClass().getSimpleName() + ": Invalid enchant " + enchant + " for itemId " + itemId + " in indexMain " + indexMain);
									continue;
								}
								
								final int costVISPoint = parseInteger(itemAttrs, "costVISPoint", 0);
								if (costVISPoint < 0)
								{
									LOGGER.severe(getClass().getSimpleName() + ": Invalid costVISPoint " + costVISPoint + " for itemId " + itemId + " in indexMain " + indexMain);
									continue;
								}
								
								final VirtualItemTemplate template = new VirtualItemTemplate(indexSub, slot, itemId, enchant, costVISPoint);
								if (!group.addTemplate(template))
								{
									LOGGER.severe(getClass().getSimpleName() + ": Duplicate indexSub " + indexSub + " in indexMain " + indexMain);
								}
							}
							catch (Exception e)
							{
								LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Could not parse virtual item in indexMain " + indexMain, e);
							}
						}
					}
				}
			}
		}
	}
	
	private VirtualSlot parseVirtualSlot(String slotValue)
	{
		return VirtualSlot.fromClientSlot(slotValue);
	}
	
	public VirtualItemGroup getByIndexMain(int indexMain)
	{
		return _groups.get(indexMain);
	}

	public Collection<VirtualItemGroup> getGroups()
	{
		return Collections.unmodifiableCollection(_groups.values());
	}
	
	public VirtualItemTemplate getTemplate(VirtualSlot slot, int itemId, int enchant, int costVISPoint)
	{
		for (VirtualItemGroup group : _groups.values())
		{
			for (VirtualItemTemplate template : group.getTemplates())
			{
				if ((template.getSlot() == slot) && (template.getItemId() == itemId) && (template.getEnchant() == enchant) && (template.getCostVISPoint() == costVISPoint))
				{
					return template;
				}
			}
		}
		
		return null;
	}
	
	public VirtualItemTemplate getTemplate(VirtualSlot slot, int itemId, int enchant)
	{
		for (VirtualItemGroup group : _groups.values())
		{
			for (VirtualItemTemplate template : group.getTemplates())
			{
				if ((template.getSlot() == slot) && (template.getItemId() == itemId) && (template.getEnchant() == enchant))
				{
					return template;
				}
			}
		}
		
		return null;
	}
	
	public boolean isLoaded()
	{
		return _loaded;
	}
	
	private int getVirtualItemCount()
	{
		int count = 0;
		for (VirtualItemGroup group : _groups.values())
		{
			count += group.size();
		}
		return count;
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
