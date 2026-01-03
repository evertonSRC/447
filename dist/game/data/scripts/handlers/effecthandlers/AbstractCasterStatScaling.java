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

import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.effects.SkillScalingEffect;
import org.l2jmobius.gameserver.model.stats.Stat;
import org.l2jmobius.gameserver.util.MathUtil;

/**
 * @author Mobius
 */
public abstract class AbstractCasterStatScaling extends AbstractEffect implements SkillScalingEffect
{
	private static final int MAX_PERCENT = 1000;
	
	private final Stat _stat;
	private final double _percent;
	
	protected AbstractCasterStatScaling(StatSet params, Stat stat)
	{
		_stat = stat;
		final double percent = params.getDouble("value", params.getDouble("amount", 0));
		_percent = MathUtil.clamp(percent, 0, MAX_PERCENT);
		
		if (params.contains("power"))
		{
			throw new IllegalArgumentException(getClass().getSimpleName() + " should use value instead of power.");
		}
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public int getScalingBonus(Creature effector)
	{
		if (effector == null)
		{
			return 0;
		}
		
		final double statValue = effector.getStat().getValue(_stat);
		final double bonus = Math.floor(statValue * (_percent / 100d));
		return (int) MathUtil.clamp((long) bonus, 0, Integer.MAX_VALUE);
	}
}
