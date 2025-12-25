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
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.homunculus.HomunculusTemplate;

/**
 * @author Mobius
 */
public class HomunculusData implements IXmlReader
{
	private static final Map<Integer, HomunculusTemplate> TEMPLATES = new HashMap<>();
	
	protected HomunculusData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		TEMPLATES.clear();
		parseDatapackFile("data/HomunculusData.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + TEMPLATES.size() + " templates.");
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		forEach(document, "list", listNode -> forEach(listNode, "homunculus", homunculusNode ->
		{
			final StatSet set = new StatSet(parseAttributes(homunculusNode));
			final int id = set.getInt("id");
			TEMPLATES.put(id, new HomunculusTemplate(id, set.getInt("type"), set.getInt("basicSkillId"), set.getInt("basicSkillLevel"), set.getInt("skillId1"), set.getInt("skillId2"), set.getInt("skillId3"), set.getInt("skillId4"), set.getInt("skillId5"), set.getInt("hpLevel1"), set.getInt("atkLevel1"), set.getInt("defLevel1"), set.getInt("expToLevel2"), set.getInt("hpLevel2"), set.getInt("atkLevel2"), set.getInt("defLevel2"), set.getInt("expToLevel3"), set.getInt("hpLevel3"), set.getInt("atkLevel3"), set.getInt("defLevel3"), set.getInt("expToLevel4"), set.getInt("hpLevel4"), set.getInt("atkLevel4"), set.getInt("defLevel4"), set.getInt("expToLevel5"), set.getInt("hpLevel5"), set.getInt("atkLevel5"), set.getInt("defLevel5"), set.getInt("expToLevel6"), set.getInt("critRate")));
		}));
	}
	
	/**
	 * Retrieves the {@link HomunculusTemplate} associated with the specified ID.
	 * @param id the unique identifier of the homunculus template
	 * @return the {@link HomunculusTemplate} corresponding to the given ID, or {@code null} if no template with the specified ID exists
	 */
	public HomunculusTemplate getTemplate(int id)
	{
		return TEMPLATES.get(id);
	}
	
	public static HomunculusData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HomunculusData INSTANCE = new HomunculusData();
	}
}
