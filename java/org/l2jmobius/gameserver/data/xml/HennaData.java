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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.enums.player.PlayerClass;
import org.l2jmobius.gameserver.model.item.Henna;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * @author Zoey76, Mobius
 */
public class HennaData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(HennaData.class.getName());
	
	private final Map<Integer, Henna> _hennaList = new HashMap<>();
	
	protected HennaData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_hennaList.clear();
		parseDatapackFile("data/stats/hennaList.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _hennaList.size() + " henna data.");
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		for (Node n = document.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equals(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("henna".equals(d.getNodeName()))
					{
						parseHenna(d);
					}
				}
			}
		}
	}
	
	/**
	 * Parses the henna.
	 * @param d the d
	 */
	private void parseHenna(Node d)
	{
		final StatSet set = new StatSet();
		final List<PlayerClass> wearClassIds = new ArrayList<>();
		final List<Skill> skills = new ArrayList<>();
		NamedNodeMap attrs = d.getAttributes();
		Node attr;
		for (int i = 0; i < attrs.getLength(); i++)
		{
			attr = attrs.item(i);
			set.set(attr.getNodeName(), attr.getNodeValue());
		}
		
		for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
		{
			final String name = c.getNodeName();
			attrs = c.getAttributes();
			switch (name)
			{
				case "stats":
				{
					for (int i = 0; i < attrs.getLength(); i++)
					{
						attr = attrs.item(i);
						set.set(attr.getNodeName(), attr.getNodeValue());
					}
					break;
				}
				case "wear":
				{
					attr = attrs.getNamedItem("count");
					set.set("wear_count", attr.getNodeValue());
					attr = attrs.getNamedItem("fee");
					set.set("wear_fee", attr.getNodeValue());
					break;
				}
				case "cancel":
				{
					attr = attrs.getNamedItem("count");
					set.set("cancel_count", attr.getNodeValue());
					attr = attrs.getNamedItem("fee");
					set.set("cancel_fee", attr.getNodeValue());
					break;
				}
				case "duration":
				{
					attr = attrs.getNamedItem("time"); // in minutes
					set.set("duration", attr.getNodeValue());
					break;
				}
				case "skill":
				{
					skills.add(SkillData.getInstance().getSkill(parseInteger(attrs, "id"), parseInteger(attrs, "level")));
					break;
				}
				case "classId":
				{
					wearClassIds.add(PlayerClass.getPlayerClass(Integer.parseInt(c.getTextContent())));
					break;
				}
			}
		}
		
		final Henna henna = new Henna(set);
		henna.setSkills(skills);
		henna.setWearClassIds(wearClassIds);
		_hennaList.put(henna.getDyeId(), henna);
	}
	
	/**
	 * Retrieves the henna (dye) associated with the specified ID.
	 * @param id the ID of the dye.
	 * @return the {@link Henna} with the specified ID, or {@code null} if not found.
	 */
	public Henna getHenna(int id)
	{
		return _hennaList.get(id);
	}
	
	/**
	 * Retrieves a list of all dyes allowed for the specified class ID.
	 * @param playerClass the {@link PlayerClass} of the player.
	 * @return a list of {@link Henna} objects allowed for the specified class ID.
	 */
	public List<Henna> getHennaList(PlayerClass playerClass)
	{
		final List<Henna> list = new ArrayList<>();
		for (Henna henna : _hennaList.values())
		{
			if (henna.isAllowedClass(playerClass))
			{
				list.add(henna);
			}
		}
		
		return list;
	}
	
	public static HennaData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HennaData INSTANCE = new HennaData();
	}
}
