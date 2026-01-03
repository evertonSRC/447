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
import java.util.logging.Level;

import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.effects.DebuffShieldEffect;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.BuffInfo;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * Debuff Shield effect implementation.
 * @author Mobius
 */
public class DebuffShieldCast extends AbstractEffect implements DebuffShieldEffect
{
	private static final String ICON_SKILL_PREFIX = "icon.skill";
	
	private final int _maxCharges;
	private final boolean _excludeDots;
	private final AtomicInteger _charges = new AtomicInteger();
	private final String[] _iconsByCharge;
	
	public DebuffShieldCast(StatSet params)
	{
		_maxCharges = params.getInt("maxCharges", params.getInt("charges", 3));
		_excludeDots = params.getBoolean("excludeDots", true);
		_iconsByCharge = new String[_maxCharges + 1];
		for (int i = 1; i <= _maxCharges; i++)
		{
			final String icon = params.getString("iconCharges" + i, null);
			if ((icon != null) && !icon.trim().isEmpty())
			{
				_iconsByCharge[i] = icon.trim();
			}
		}
		
		if (LOGGER.isLoggable(Level.FINE))
		{
			final StringBuilder builder = new StringBuilder();
			builder.append("Loaded DebuffShieldCast icons: maxCharges=").append(_maxCharges);
			for (int i = 1; i <= _maxCharges; i++)
			{
				builder.append(", icon[").append(i).append("]=").append(_iconsByCharge[i]);
			}
			LOGGER.fine(builder.toString());
		}
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		// Charge lifecycle: initialization happens on effect start.
		_charges.set(_maxCharges);
		if (effected != null)
		{
			final BuffInfo info = effected.getEffectList().getBuffInfoBySkillId(skill.getId());
			updateIcon(info, _maxCharges);
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		// Charge lifecycle: clear on exit.
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
		// Charge lifecycle: consumption happens here (atomic).
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
	
	@Override
	public void updateIcon(BuffInfo info, int currentCharges)
	{
		if ((info == null) || (currentCharges <= 0))
		{
			return;
		}
		
		final String icon = getIconForCharges(currentCharges);
		boolean updated = false;
		if (icon == null)
		{
			if (info.hasDisplaySkillOverride())
			{
				info.clearDisplaySkillOverride();
				updated = true;
			}
		}
		else
		{
			final Skill displaySkill = resolveDisplaySkill(info.getSkill(), icon);
			if (displaySkill != null)
			{
				if (!info.hasDisplaySkillOverride() || (info.getDisplaySkillId() != displaySkill.getDisplayId()) || (info.getDisplaySkillLevel() != displaySkill.getDisplayLevel()) || (info.getDisplaySkillSubLevel() != displaySkill.getSubLevel()))
				{
					info.setDisplaySkillOverride(displaySkill.getDisplayId(), displaySkill.getDisplayLevel(), displaySkill.getSubLevel());
					updated = true;
				}
			}
			else if (info.hasDisplaySkillOverride())
			{
				info.clearDisplaySkillOverride();
				updated = true;
			}
		}
		
		if (updated)
		{
			info.getEffected().getEffectList().updateEffectIcons(false);
		}
	}
	
	private String getIconForCharges(int currentCharges)
	{
		if ((currentCharges < 1) || (currentCharges >= _iconsByCharge.length))
		{
			return null;
		}
		return _iconsByCharge[currentCharges];
	}
	
	private Skill resolveDisplaySkill(Skill baseSkill, String icon)
	{
		final SkillData data = SkillData.getInstance();
		final int maxLevel = data.getMaxLevel(baseSkill.getId());
		for (int level = 1; level <= maxLevel; level++)
		{
			final Skill candidate = data.getSkill(baseSkill.getId(), level, baseSkill.getSubLevel());
			if ((candidate != null) && icon.equals(candidate.getIcon()))
			{
				return candidate;
			}
		}
		
		final Integer iconSkillId = parseIconSkillId(icon);
		if (iconSkillId == null)
		{
			return null;
		}
		
		return data.getSkill(iconSkillId, 1);
	}
	
	private Integer parseIconSkillId(String icon)
	{
		if ((icon == null) || !icon.startsWith(ICON_SKILL_PREFIX))
		{
			return null;
		}
		
		final String suffix = icon.substring(ICON_SKILL_PREFIX.length());
		if (suffix.isEmpty())
		{
			return null;
		}
		
		final StringBuilder digits = new StringBuilder();
		for (int i = 0; i < suffix.length(); i++)
		{
			final char ch = suffix.charAt(i);
			if (!Character.isDigit(ch))
			{
				break;
			}
			digits.append(ch);
		}
		
		if (digits.length() == 0)
		{
			return null;
		}
		
		try
		{
			return Integer.parseInt(digits.toString());
		}
		catch (NumberFormatException ex)
		{
			return null;
		}
	}
}
