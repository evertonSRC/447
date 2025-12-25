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
package org.l2jmobius.gameserver.network.clientpackets.ability;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.SkillTreeData;
import org.l2jmobius.gameserver.model.SkillLearn;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.ability.ExAcquireAPSkillList;

/**
 * @author Mobius
 */
public class RequestAcquireAbilityList extends ClientPacket
{
	private Map<Integer, SkillHolder> _skills = new LinkedHashMap<>();
	
	@Override
	protected void readImpl()
	{
		readInt(); // Total size
		for (int i = 0; i < 3; i++) // Tree size.
		{
			final int size = readInt();
			for (int j = 0; j < size; j++)
			{
				final SkillHolder holder = new SkillHolder(readInt(), readInt());
				if (holder.getSkillLevel() < 1)
				{
					_skills = null;
					PacketLogger.warning("Player is trying to learn skill " + holder + " by sending packet with level 0!");
					return;
				}
				
				if (_skills.putIfAbsent(holder.getSkillId(), holder) != null)
				{
					_skills = null;
					PacketLogger.warning("Player is trying to send two times one skill " + holder + " to learn!");
					return;
				}
			}
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_skills == null)
		{
			PacketLogger.warning("Player " + player + " tried to exploit RequestAcquireAbilityList!");
			return;
		}
		
		if (player.isSubClassActive() && !player.isDualClassActive())
		{
			return;
		}
		
		if ((player.getAbilityPoints() <= 0) || (player.getAbilityPoints() == player.getAbilityPointsUsed()))
		{
			PacketLogger.warning(player + " is trying to learn ability without ability points!");
			return;
		}
		
		if (player.getLevel() < 85)
		{
			player.sendPacket(SystemMessageId.REACH_LV_85_TO_USE);
			return;
		}
		else if (!player.isAwakenedClass())
		{
			player.sendPacket(SystemMessageId.ONLY_AWAKENED_CHARACTERS_OF_LV_85_OR_ABOVE_CAN_BE_ACTIVATED);
			return;
		}
		else if (player.isInOlympiadMode())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_OR_RESET_ABILITY_POINTS_WHILE_PARTICIPATING_IN_THE_OLYMPIAD_OR_CEREMONY_OF_CHAOS);
			return;
		}
		else if (player.isOnEvent())
		{
			player.sendMessage("You cannot use or reset Ability Points while participating in an event.");
			return;
		}
		
		int spentPoints = 0;
		final List<SkillLearn> skillsToLearn = new ArrayList<>(_skills.size());
		for (SkillHolder holder : _skills.values())
		{
			final SkillLearn learn = SkillTreeData.getInstance().getAbilitySkill(holder.getSkillId(), holder.getSkillLevel());
			if (learn == null)
			{
				PacketLogger.warning("SkillLearn " + holder.getSkillId() + " (" + holder.getSkillLevel() + ") not found!");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				break;
			}
			
			final Skill skill = holder.getSkill();
			if (skill == null)
			{
				PacketLogger.warning("Skill " + holder.getSkillId() + " (" + holder.getSkillLevel() + ") not found!");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				break;
			}
			
			spentPoints++;
			skillsToLearn.add(learn);
		}
		
		if ((player.getAbilityPoints() - player.getAbilityPointsUsed()) < spentPoints)
		{
			PacketLogger.warning(player + " is trying to learn ability without ability points!");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.setAbilityPointsUsed(spentPoints, false);
		
		// Sort the skills by their tree id -> row -> column
		skillsToLearn.sort(Comparator.comparingInt(SkillLearn::getTreeId).thenComparing(SkillLearn::getRow).thenComparing(SkillLearn::getColumn));
		
		final StringBuilder learnedSkillsInfo = new StringBuilder();
		for (SkillLearn learn : skillsToLearn)
		{
			final Skill skill = SkillData.getInstance().getSkill(learn.getSkillId(), learn.getSkillLevel());
			player.addSkill(skill, false);
			
			// Append the learned skill's ID and level to the string
			learnedSkillsInfo.append(skill.getId()).append("-").append(learn.getSkillLevel()).append(",");
		}
		
		// Set the player's variable with the learned skills' information.
		if (player.getAbilityPreset() == 0)
		{
			if (player.isDualClassActive())
			{
				player.getVariables().set(PlayerVariables.ABILITY_POINTS_DUAL_CLASS_SKILLS_A, learnedSkillsInfo.toString());
			}
			else
			{
				player.getVariables().set(PlayerVariables.ABILITY_POINTS_MAIN_CLASS_SKILLS_A, learnedSkillsInfo.toString());
			}
		}
		else
		{
			if (player.isDualClassActive())
			{
				player.getVariables().set(PlayerVariables.ABILITY_POINTS_DUAL_CLASS_SKILLS_B, learnedSkillsInfo.toString());
			}
			else
			{
				player.getVariables().set(PlayerVariables.ABILITY_POINTS_MAIN_CLASS_SKILLS_B, learnedSkillsInfo.toString());
			}
		}
		
		player.sendPacket(new ExAcquireAPSkillList(player));
		
		ThreadPool.schedule(() ->
		{
			player.sendSkillList();
			player.getStat().recalculateStats(false);
			player.broadcastUserInfo();
		}, 100);
	}
}
