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

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.data.holders.SymbolSealHolder;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * @author NviX
 */
public class SymbolSealData implements IXmlReader
{
	private final Map<Integer, List<SymbolSealHolder>> _data = new HashMap<>();
	
	protected SymbolSealData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDatapackFile("data/SymbolSealData.xml");
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
					if ("class".equalsIgnoreCase(d.getNodeName()))
					{
						final int classId = parseInteger(d.getAttributes(), "id");
						if (!_data.containsKey(classId))
						{
							_data.put(classId, new ArrayList<>());
						}
						
						for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
						{
							if ("symbol".equalsIgnoreCase(cd.getNodeName()))
							{
								final int symbolId = parseInteger(cd.getAttributes(), "id");
								final int skillId = parseInteger(cd.getAttributes(), "skillId");
								_data.get(classId).add(new SymbolSealHolder(symbolId, SkillData.getInstance().getSkill(skillId, 1)));
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Retrieves a skill associated with a specific class and symbol.
	 * <p>
	 * This method looks up the skill based on the provided {@code classId} and {@code symbolId}. If the specified symbol exists within the data for the given class, the corresponding {@link Skill} is returned. If no matching skill is found, {@code null} is returned.
	 * </p>
	 * @param classId the ID of the class to look up
	 * @param symbolId the ID of the symbol within the class to retrieve the skill for
	 * @return the {@link Skill} associated with the specified class and symbol, or {@code null} if not found
	 */
	public Skill getSkill(int classId, int symbolId)
	{
		final List<SymbolSealHolder> data = _data.get(classId);
		if (data != null)
		{
			final SymbolSealHolder symbol = data.get(symbolId);
			if (symbol != null)
			{
				return symbol.getSkill();
			}
		}
		
		return null;
	}
	
	public static SymbolSealData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SymbolSealData INSTANCE = new SymbolSealData();
	}
}
