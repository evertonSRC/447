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
package org.l2jmobius.gameserver.network.clientpackets.classchange;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.enums.CategoryType;
import org.l2jmobius.gameserver.managers.ScriptManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.classchange.ExClassChangeSetAlarm;

/**
 * @author Mobius
 */
public class ExRequestClassChangeVerifying extends ClientPacket
{
	// @formatter:off
	private static final int[] FIRST_CLASS_QUESTS = {10007, 10207, 10307}; // Path of Destiny - Beginning
	private static final int[] SECOND_CLASS_QUESTS = {10015, 10215, 10315}; // Path of Destiny - Proving
	private static final int[] THIRD_CLASS_QUESTS = {10024, 10124, 10224, 10324}; // Path of Destiny - Conviction
	private static final int[] FOURTH_CLASS_QUESTS = {10036, 10236, 10336}; // Path of Destiny - Overcome
	// @formatter:on
	
	private int _classId;
	
	@Override
	protected void readImpl()
	{
		_classId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_classId != player.getPlayerClass().getId())
		{
			return;
		}
		
		if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
		{
			return;
		}
		
		if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
		{
			if (!fourthClassCheck(player))
			{
				return;
			}
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
		{
			if (!thirdClassCheck(player))
			{
				return;
			}
		}
		else if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP))
		{
			if (!secondClassCheck(player))
			{
				return;
			}
		}
		else if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP))
		{
			if (!firstClassCheck(player))
			{
				return;
			}
		}
		
		player.sendPacket(ExClassChangeSetAlarm.STATIC_PACKET);
	}
	
	private boolean firstClassCheck(Player player)
	{
		if (PlayerConfig.DISABLE_TUTORIAL)
		{
			return true;
		}
		
		return isAnyQuestStarted(player, FIRST_CLASS_QUESTS);
	}
	
	private boolean secondClassCheck(Player player)
	{
		if (PlayerConfig.DISABLE_TUTORIAL)
		{
			return true;
		}
		
		return isAnyQuestStarted(player, SECOND_CLASS_QUESTS);
	}
	
	private boolean thirdClassCheck(Player player)
	{
		if (PlayerConfig.DISABLE_TUTORIAL)
		{
			return true;
		}
		
		return isAnyQuestStarted(player, THIRD_CLASS_QUESTS);
	}
	
	private boolean fourthClassCheck(Player player)
	{
		if (PlayerConfig.DISABLE_TUTORIAL)
		{
			return true;
		}
		
		return isAnyQuestStarted(player, FOURTH_CLASS_QUESTS);
	}
	
	/**
	 * Checks if any quest from the given array is started for the player.
	 * @param player the player to check
	 * @param questIds array of quest IDs to check
	 * @return true if any quest is started, false otherwise
	 */
	private boolean isAnyQuestStarted(Player player, int[] questIds)
	{
		for (int questId : questIds)
		{
			final Quest quest = ScriptManager.getInstance().getQuest(questId);
			if (quest != null)
			{
				final QuestState qs = player.getQuestState(quest.getName());
				if ((qs != null) && qs.isStarted())
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
