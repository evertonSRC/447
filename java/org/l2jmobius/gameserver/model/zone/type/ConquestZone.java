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
package org.l2jmobius.gameserver.model.zone.type;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.config.ConquestConfig;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.appearance.PlayerAppearance;
import org.l2jmobius.gameserver.model.actor.enums.creature.InstanceType;
import org.l2jmobius.gameserver.model.actor.enums.player.TeleportWhereType;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.enums.SkillFinishType;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.serverpackets.EtcStatusUpdate;

/**
 * @author Liamxroy
 */
public class ConquestZone extends ZoneType
{
	int _chance;
	private int _initialDelay;
	private int _reuse;
	protected boolean _bypassConditions;
	private boolean _isShowDangerIcon;
	private boolean _removeEffectsOnExit;
	protected Map<Integer, Integer> _skills;
	protected Future<?> _task;
	protected Future<?> _teleportToTownTask;
	
	public ConquestZone(int id)
	{
		super(id);
		_chance = 100;
		_initialDelay = 0;
		_reuse = 30000;
		setTargetType(InstanceType.Playable); // default only playable
		_bypassConditions = false;
		_isShowDangerIcon = false;
		_removeEffectsOnExit = true;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		switch (name)
		{
			case "chance":
			{
				_chance = Integer.parseInt(value);
				break;
			}
			case "initialDelay":
			{
				_initialDelay = Integer.parseInt(value);
				break;
			}
			case "reuse":
			{
				_reuse = Integer.parseInt(value);
				break;
			}
			case "bypassSkillConditions":
			{
				_bypassConditions = Boolean.parseBoolean(value);
				break;
			}
			case "maxDynamicSkillCount":
			{
				_skills = new ConcurrentHashMap<>(Integer.parseInt(value));
				break;
			}
			case "showDangerIcon":
			{
				_isShowDangerIcon = Boolean.parseBoolean(value);
				break;
			}
			case "skillIdLvl":
			{
				final String[] propertySplit = value.split(";");
				_skills = new ConcurrentHashMap<>(propertySplit.length);
				for (String skill : propertySplit)
				{
					final String[] skillSplit = skill.split("-");
					if (skillSplit.length != 2)
					{
						LOGGER.warning(getClass().getSimpleName() + ": invalid config property -> skillsIdLvl \"" + skill + "\"");
					}
					else
					{
						try
						{
							_skills.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
						}
						catch (NumberFormatException nfe)
						{
							if (!skill.isEmpty())
							{
								LOGGER.warning(getClass().getSimpleName() + ": invalid config property -> skillsIdLvl \"" + skillSplit[0] + "\"" + skillSplit[1]);
							}
						}
					}
				}
				break;
			}
			case "removeEffectsOnExit":
			{
				_removeEffectsOnExit = Boolean.parseBoolean(value);
				break;
			}
			default:
			{
				super.setParameter(name, value);
			}
		}
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (_skills != null)
		{
			Future<?> task = _task;
			if (task == null)
			{
				synchronized (this)
				{
					task = _task;
					if (task == null)
					{
						_task = task = ThreadPool.scheduleAtFixedRate(new ApplySkill(), _initialDelay, _reuse);
					}
				}
			}
		}
		
		if (creature.isPlayer())
		{
			final Player player = creature.asPlayer();
			player.setInsideZone(ZoneId.CONQUEST, true);
			
			// Prevent db reading conquest name error.
			final String conquestName = player.getConquestName();
			if (conquestName == null)
			{
				player.teleToLocation(TeleportWhereType.TOWN);
				onExit(player);
				player.sendMessage("You must choose a conquest name before going there!");
				return;
			}
			
			// Change player information.
			final PlayerAppearance app = player.getAppearance();
			app.setVisibleName(conquestName);
			app.setVisibleTitle("");
			app.setVisibleClanData(0, 0, 0, 0, 0);
			
			if (ConquestConfig.CONQUEST_PVP_ZONE)
			{
				player.setInsideZone(ZoneId.PVP, true);
			}
			
			if (_isShowDangerIcon)
			{
				player.setInsideZone(ZoneId.DANGER_AREA, true);
				player.sendPacket(new EtcStatusUpdate(player));
			}
			
			// Teleport player to Town if still in zone and not conquest period.
			if (!GlobalVariablesManager.getInstance().hasVariable("CONQUEST_AVAILABLE") || (GlobalVariablesManager.getInstance().hasVariable("CONQUEST_AVAILABLE") && !GlobalVariablesManager.getInstance().getBoolean("CONQUEST_AVAILABLE", false)))
			{
				if (!player.isGM())
				{
					final PlayerVariables vars = player.getVariables();
					Location location = new Location(147458, 26903, -2206); // Aden center if no location stored
					if (vars.contains(PlayerVariables.CONQUEST_ORIGIN))
					{
						final int[] loc = vars.getIntArray(PlayerVariables.CONQUEST_ORIGIN, ";");
						if ((loc != null) && (loc.length == 3))
						{
							location = new Location(loc[0], loc[1], loc[2]);
						}
						
						player.teleToLocation(location);
						vars.remove(PlayerVariables.CONQUEST_ORIGIN);
					}
					else
					{
						player.teleToLocation(location);
					}
					
					onExit(player);
				}
			}
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if (creature.isPlayer())
		{
			final Player player = creature.asPlayer();
			player.setInsideZone(ZoneId.CONQUEST, false);
			
			// Restore player information.
			final PlayerAppearance app = player.getAppearance();
			app.setVisibleName(null);
			app.setVisibleTitle(null);
			app.setVisibleClanData(-1, -1, -1, -1, -1);
			
			if (ConquestConfig.CONQUEST_PVP_ZONE)
			{
				player.setInsideZone(ZoneId.PVP, false);
			}
			
			if (_isShowDangerIcon)
			{
				player.setInsideZone(ZoneId.DANGER_AREA, false);
				if (!player.isInsideZone(ZoneId.DANGER_AREA))
				{
					player.sendPacket(new EtcStatusUpdate(player));
				}
			}
			
			if (_removeEffectsOnExit && (_skills != null))
			{
				for (Entry<Integer, Integer> e : _skills.entrySet())
				{
					final Skill skill = SkillData.getInstance().getSkill(e.getKey(), e.getValue());
					if ((skill != null) && player.isAffectedBySkill(skill.getId()))
					{
						player.stopSkillEffects(SkillFinishType.REMOVED, skill.getId());
					}
				}
			}
		}
		
		if (getCharactersInside().isEmpty() && (_task != null))
		{
			_task.cancel(true);
			_task = null;
		}
	}
	
	@Override
	public void onPlayerLogoutInside(Player player)
	{
		if (player != null)
		{
			player.setInsideZone(ZoneId.CONQUEST, false);
			
			// Restore player information.
			final PlayerAppearance appearance = player.getAppearance();
			appearance.setVisibleName(null);
			appearance.setVisibleTitle(null);
			appearance.setVisibleClanData(-1, -1, -1, -1, -1);
			
			if (ConquestConfig.CONQUEST_PVP_ZONE)
			{
				player.setInsideZone(ZoneId.PVP, false);
			}
			
			if (_isShowDangerIcon)
			{
				player.setInsideZone(ZoneId.DANGER_AREA, false);
				if (!player.isInsideZone(ZoneId.DANGER_AREA))
				{
					player.sendPacket(new EtcStatusUpdate(player));
				}
			}
			
			if (_removeEffectsOnExit && (_skills != null))
			{
				for (int skillId : _skills.keySet())
				{
					if (player.isAffectedBySkill(skillId))
					{
						player.stopSkillEffects(SkillFinishType.REMOVED, skillId);
					}
				}
			}
		}
		
		if (getCharactersInside().isEmpty() && (_task != null))
		{
			_task.cancel(true);
			_task = null;
		}
	}
	
	public int getChance()
	{
		return _chance;
	}
	
	public void addSkill(int skillId, int skillLevel)
	{
		if (skillLevel < 1) // remove skill
		{
			removeSkill(skillId);
			return;
		}
		
		if (_skills == null)
		{
			synchronized (this)
			{
				if (_skills == null)
				{
					_skills = new ConcurrentHashMap<>(3);
				}
			}
		}
		
		_skills.put(skillId, skillLevel);
	}
	
	public void removeSkill(int skillId)
	{
		if (_skills != null)
		{
			_skills.remove(skillId);
		}
	}
	
	public void clearSkills()
	{
		if (_skills != null)
		{
			_skills.clear();
		}
	}
	
	public int getSkillLevel(int skillId)
	{
		if ((_skills == null) || !_skills.containsKey(skillId))
		{
			return 0;
		}
		
		return _skills.get(skillId);
	}
	
	private class ApplySkill implements Runnable
	{
		protected ApplySkill()
		{
			if (_skills == null)
			{
				throw new IllegalStateException("No skills defined.");
			}
		}
		
		@Override
		public void run()
		{
			if (getCharactersInside().isEmpty())
			{
				if (_task != null)
				{
					_task.cancel(false);
					_task = null;
				}
				return;
			}
			
			if (!isEnabled())
			{
				return;
			}
			
			for (Creature character : getCharactersInside())
			{
				if ((character != null) && character.isPlayer() && !character.isDead() && (Rnd.get(100) < _chance))
				{
					for (Entry<Integer, Integer> e : _skills.entrySet())
					{
						final Skill skill = SkillData.getInstance().getSkill(e.getKey().intValue(), e.getValue().intValue());
						if ((skill != null) && (_bypassConditions || skill.checkCondition(character, character, false)))
						{
							if (character.getAffectedSkillLevel(skill.getId()) < skill.getLevel())
							{
								skill.activateSkill(character, character);
							}
						}
					}
				}
			}
		}
	}
}
