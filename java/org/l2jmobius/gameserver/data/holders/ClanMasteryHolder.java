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
package org.l2jmobius.gameserver.data.holders;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * @author Mobius
 */
public class ClanMasteryHolder
{
	private final int _id;
	private final List<Skill> _skills = new ArrayList<>();
	private final int _clanLevel;
	private final int _clanReputation;
	private final int _previousMastery;
	private final int _previousMasteryAlt;
	
	public ClanMasteryHolder(int id, Skill skill1, Skill skill2, Skill skill3, Skill skill4, int clanLevel, int clanReputation, int previousMastery, int previousMasteryAlt)
	{
		_id = id;
		_clanLevel = clanLevel;
		_clanReputation = clanReputation;
		_previousMastery = previousMastery;
		_previousMasteryAlt = previousMasteryAlt;
		_skills.add(skill1);
		if (skill2 != null)
		{
			_skills.add(skill2);
		}
		
		if (skill3 != null)
		{
			_skills.add(skill3);
		}
		
		if (skill4 != null)
		{
			_skills.add(skill4);
		}
	}
	
	public int getId()
	{
		return _id;
	}
	
	public List<Skill> getSkills()
	{
		return _skills;
	}
	
	public int getClanLevel()
	{
		return _clanLevel;
	}
	
	public int getClanReputation()
	{
		return _clanReputation;
	}
	
	public int getPreviousMastery()
	{
		return _previousMastery;
	}
	
	public int getPreviousMasteryAlt()
	{
		return _previousMasteryAlt;
	}
}
