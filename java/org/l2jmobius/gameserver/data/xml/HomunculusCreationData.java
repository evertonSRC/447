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
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.homunculus.HomunculusCreationTemplate;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;

/**
 * @author Index
 */
public class HomunculusCreationData implements IXmlReader
{
	private static final List<HomunculusCreationTemplate> TEMPLATES = new ArrayList<>();
	
	private HomunculusCreationTemplate _defaultTemplate;
	
	protected HomunculusCreationData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		TEMPLATES.clear();
		_defaultTemplate = null;
		parseDatapackFile("data/HomunculusCreationData.xml");
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
					if ("homunculusCreation".equalsIgnoreCase(d.getNodeName()))
					{
						attrs = d.getAttributes();
						set = new StatSet();
						for (int i = 0; i < attrs.getLength(); i++)
						{
							att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						final int slotId = set.getInt("slotId", 0);
						final Boolean isEnabled = set.getBoolean("isEnabled", false);
						final int grade = set.getInt("grade", 0);
						final Boolean isEvent = set.getBoolean("event", false);
						List<ItemHolder> itemFees = Collections.emptyList();
						Integer[] hpFee = new Integer[2];
						Long[] spFee = new Long[2];
						Integer[] vpFee = new Integer[2];
						long time = 0;
						List<Double[]> chances = Collections.emptyList();
						for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling())
						{
							if ("itemFees".equalsIgnoreCase(b.getNodeName()))
							{
								itemFees = getItemList(b);
							}
							else if ("hpFee".equalsIgnoreCase(b.getNodeName()))
							{
								hpFee[0] = Integer.parseInt(b.getAttributes().getNamedItem("count").getNodeValue());
								hpFee[1] = Integer.parseInt(b.getAttributes().getNamedItem("byUse").getNodeValue());
							}
							else if ("spFee".equalsIgnoreCase(b.getNodeName()))
							{
								spFee[0] = Long.parseLong(b.getAttributes().getNamedItem("count").getNodeValue());
								spFee[1] = Long.parseLong(b.getAttributes().getNamedItem("byUse").getNodeValue());
							}
							else if ("vpFee".equalsIgnoreCase(b.getNodeName()))
							{
								vpFee[0] = Integer.parseInt(b.getAttributes().getNamedItem("count").getNodeValue());
								vpFee[1] = Integer.parseInt(b.getAttributes().getNamedItem("byUse").getNodeValue());
							}
							else if ("time".equalsIgnoreCase(b.getNodeName()))
							{
								time = Long.parseLong(b.getAttributes().getNamedItem("count").getNodeValue());
							}
							else if ("chance".equalsIgnoreCase(b.getNodeName()))
							{
								chances = getChanceList(b);
							}
						}
						
						final HomunculusCreationTemplate template = new HomunculusCreationTemplate(slotId, isEnabled, grade, isEvent, itemFees, hpFee, spFee, vpFee, time, chances);
						TEMPLATES.add(template);
						if (_defaultTemplate == null)
						{
							_defaultTemplate = template;
						}
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
	
	private List<Double[]> getChanceList(Node c)
	{
		final List<Double[]> chanceList = new ArrayList<>();
		for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
		{
			if ("homunculus".equalsIgnoreCase(b.getNodeName()))
			{
				final Double[] feeArray = new Double[2];
				feeArray[0] = Double.parseDouble(b.getAttributes().getNamedItem("id").getNodeValue());
				feeArray[1] = Double.parseDouble(b.getAttributes().getNamedItem("creationChance").getNodeValue());
				chanceList.add(feeArray);
			}
		}
		
		return chanceList;
	}
	
	/**
	 * Retrieves the default {@link HomunculusCreationTemplate}.
	 * @return the default {@link HomunculusCreationTemplate}, or {@code null} if no default template is set
	 */
	public HomunculusCreationTemplate getDefaultTemplate()
	{
		return _defaultTemplate;
	}
	
	/**
	 * Retrieves the {@link HomunculusCreationTemplate} that matches the specified item ID.
	 * @param itemId the ID of the item used to find the associated template
	 * @return the {@link HomunculusCreationTemplate} that includes the specified item ID, or {@code null} if no matching template is found
	 */
	public HomunculusCreationTemplate getTemplateByItemId(int itemId)
	{
		for (HomunculusCreationTemplate template : TEMPLATES)
		{
			if (template.isInstanceHaveCoupon(itemId))
			{
				return template;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a collection of all available {@link HomunculusCreationTemplate} instances.
	 * @return a {@link Collection} containing all homunculus creation templates
	 */
	public Collection<HomunculusCreationTemplate> getTemplates()
	{
		return TEMPLATES;
	}
	
	public static HomunculusCreationData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HomunculusCreationData INSTANCE = new HomunculusCreationData();
	}
}
