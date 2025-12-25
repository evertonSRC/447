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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.homunculus.HomunculusSlotTemplate;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;

/**
 * @author Index
 */
public class HomunculusSlotData implements IXmlReader
{
	private static final Map<Integer, HomunculusSlotTemplate> TEMPLATES = new HashMap<>();
	
	protected HomunculusSlotData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		TEMPLATES.clear();
		parseDatapackFile("data/HomunculusSlotData.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + TEMPLATES.size() + " templates.");
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		StatSet set;
		Node att;
		NamedNodeMap attrs;
		for (Node n = document.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("homunculusSlot".equalsIgnoreCase(d.getNodeName()))
					{
						attrs = d.getAttributes();
						set = new StatSet();
						for (int i = 0; i < attrs.getLength(); i++)
						{
							att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						List<ItemHolder> fee = Collections.emptyList();
						for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
						{
							if ("fee".equalsIgnoreCase(c.getNodeName()))
							{
								fee = getItemList(c);
								break;
							}
						}
						
						final int slotId = set.getInt("slotId");
						TEMPLATES.put(slotId, new HomunculusSlotTemplate(slotId, fee, set.getBoolean("isEnabled", false)));
					}
				}
			}
		}
	}
	
	private List<ItemHolder> getItemList(Node c)
	{
		final List<ItemHolder> items = new ArrayList<>();
		for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
		{
			if ("item".equalsIgnoreCase(b.getNodeName()))
			{
				final int itemId = Integer.parseInt(b.getAttributes().getNamedItem("id").getNodeValue());
				final long itemCount = Long.parseLong(b.getAttributes().getNamedItem("count").getNodeValue());
				items.add(new ItemHolder(itemId, itemCount));
			}
		}
		
		return items;
	}
	
	/**
	 * Retrieves the {@link HomunculusSlotTemplate} associated with the specified slot ID.
	 * @param id the unique identifier of the homunculus slot template
	 * @return the {@link HomunculusSlotTemplate} corresponding to the given ID, or {@code null} if no template with the specified ID exists
	 */
	public HomunculusSlotTemplate getTemplate(int id)
	{
		return TEMPLATES.get(id);
	}
	
	public static HomunculusSlotData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HomunculusSlotData INSTANCE = new HomunculusSlotData();
	}
}
