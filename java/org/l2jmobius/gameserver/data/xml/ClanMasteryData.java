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
import java.util.logging.Logger;

import org.w3c.dom.Document;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.data.holders.ClanMasteryHolder;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * @author Mobius
 */
public class ClanMasteryData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(ClanMasteryData.class.getName());
	
	private final Map<Integer, ClanMasteryHolder> _clanMasteryData = new HashMap<>();
	
	protected ClanMasteryData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_clanMasteryData.clear();
		parseDatapackFile("data/ClanMasteryData.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _clanMasteryData.size() + " clan masteries.");
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		forEach(document, "list", listNode -> forEach(listNode, "clan", clanNode ->
		{
			final StatSet set = new StatSet(parseAttributes(clanNode));
			final int id = set.getInt("mastery");
			final int skill1Id = set.getInt("skill1Id");
			final int skill1Level = set.getInt("skill1Level");
			final Skill skill1 = SkillData.getInstance().getSkill(skill1Id, skill1Level);
			if (skill1 == null)
			{
				LOGGER.info(getClass().getSimpleName() + ": Could not create clan mastery, skill id " + skill1Id + " with level " + skill1Level + " does not exist.");
				return;
			}
			
			final int skill2Id = set.getInt("skill2Id", 0);
			final int skill2Level = set.getInt("skill2Level", 0);
			Skill skill2 = null;
			if (skill2Id > 0)
			{
				skill2 = SkillData.getInstance().getSkill(skill2Id, skill2Level);
				if (skill2 == null)
				{
					LOGGER.info(getClass().getSimpleName() + ": Could not create clan mastery, skill id " + skill2Id + " with level " + skill2Level + " does not exist.");
					return;
				}
			}
			
			final int skill3Id = set.getInt("skill3Id", 0);
			final int skill3Level = set.getInt("skill3Level", 0);
			Skill skill3 = null;
			if (skill3Id > 0)
			{
				skill3 = SkillData.getInstance().getSkill(skill3Id, skill3Level);
				if (skill3 == null)
				{
					LOGGER.info(getClass().getSimpleName() + ": Could not create clan mastery, skill id " + skill3Id + " with level " + skill3Level + " does not exist.");
					return;
				}
			}
			
			final int skill4Id = set.getInt("skill4Id", 0);
			final int skill4Level = set.getInt("skill4Level", 0);
			Skill skill4 = null;
			if (skill4Id > 0)
			{
				skill4 = SkillData.getInstance().getSkill(skill4Id, skill4Level);
				if (skill4 == null)
				{
					LOGGER.info(getClass().getSimpleName() + ": Could not create clan mastery, skill id " + skill4Id + " with level " + skill4Level + " does not exist.");
					return;
				}
			}
			
			final int clanLevel = set.getInt("clanLevel");
			final int clanReputation = set.getInt("clanReputation");
			final int previousMastery = set.getInt("previousMastery", 0);
			final int previousMasteryAlt = set.getInt("previousMasteryAlt", 0);
			_clanMasteryData.put(id, new ClanMasteryHolder(id, skill1, skill2, skill3, skill4, clanLevel, clanReputation, previousMastery, previousMasteryAlt));
		}));
	}
	
	public Collection<ClanMasteryHolder> getMasteries()
	{
		return _clanMasteryData.values();
	}
	
	public ClanMasteryHolder getClanMastery(int id)
	{
		return _clanMasteryData.get(id);
	}
	
	public static ClanMasteryData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanMasteryData INSTANCE = new ClanMasteryData();
	}
}
