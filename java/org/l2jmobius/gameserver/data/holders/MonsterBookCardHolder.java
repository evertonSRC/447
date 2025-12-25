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

import org.l2jmobius.gameserver.model.script.Faction;

/**
 * @author Mobius
 */
public class MonsterBookCardHolder
{
	private final int _id;
	private final int _monsterId;
	private final Faction _faction;
	private final List<MonsterBookRewardHolder> _rewards = new ArrayList<>(4);
	
	public MonsterBookCardHolder(int id, int monsterId, Faction faction)
	{
		_id = id;
		_monsterId = monsterId;
		_faction = faction;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getMonsterId()
	{
		return _monsterId;
	}
	
	public Faction getFaction()
	{
		return _faction;
	}
	
	public MonsterBookRewardHolder getReward(int level)
	{
		return _rewards.get(level);
	}
	
	public void addReward(MonsterBookRewardHolder reward)
	{
		_rewards.add(reward);
	}
}
