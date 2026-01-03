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
package handlers.effecthandlers;

import java.util.concurrent.atomic.AtomicInteger;

import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.effects.DebuffShieldEffect;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * Debuff Shield effect implementation.
 * @author Mobius
 */
public class DebuffShieldCast extends AbstractEffect implements DebuffShieldEffect
{
	private final int _maxCharges;
	private final boolean _excludeDots;
	private final AtomicInteger _charges = new AtomicInteger();
	
	public DebuffShieldCast(StatSet params)
	{
		_maxCharges = params.getInt("maxCharges", params.getInt("charges", 3));
		_excludeDots = params.getBoolean("excludeDots", true);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		_charges.set(_maxCharges);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		_charges.set(0);
	}
	
	@Override
	public int getMaxCharges()
	{
		return _maxCharges;
	}
	
	@Override
	public int getRemainingCharges()
	{
		return _charges.get();
	}
	
	@Override
	public boolean isExcludeDots()
	{
		return _excludeDots;
	}
	
	@Override
	public int consumeCharge()
	{
		while (true)
		{
			final int current = _charges.get();
			if (current <= 0)
			{
				return -1;
			}
			
			final int next = current - 1;
			if (_charges.compareAndSet(current, next))
			{
				return next;
			}
		}
	}
}
