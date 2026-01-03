/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.model.effects;

import java.util.List;

import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.skill.EffectScope;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.util.MathUtil;

/**
 * Utility class for caster stat scaling.
 * @author Mobius
 */
public final class SkillScaling
{
	private SkillScaling()
	{
	}
	
	public static int calculateBonus(Creature effector, Skill skill)
	{
		if ((effector == null) || (skill == null))
		{
			return 0;
		}
		
		long total = 0;
		for (EffectScope scope : EffectScope.values())
		{
			final List<AbstractEffect> effects = skill.getEffects(scope);
			if (effects == null)
			{
				continue;
			}
			
			for (AbstractEffect effect : effects)
			{
				if (effect instanceof SkillScalingEffect scalingEffect)
				{
					total += scalingEffect.getScalingBonus(effector);
				}
			}
		}
		
		return (int) MathUtil.clamp(total, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
}
