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
package org.l2jmobius.gameserver.network.serverpackets.pledgeV2;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.xml.DailyMissionData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.DailyMissionDataHolder;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExPledgeMissionInfo extends ServerPacket
{
	private final Player _player;
	private final Collection<DailyMissionDataHolder> _rewards;
	
	public ExPledgeMissionInfo(Player player)
	{
		_player = player;
		_rewards = DailyMissionData.getInstance().getDailyMissionData(player);
	}
	
	public ExPledgeMissionInfo(Player player, DailyMissionDataHolder holder)
	{
		_player = player;
		_rewards = Collections.singletonList(holder);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		if (!DailyMissionData.getInstance().isAvailable() || (_player.getClan() == null))
		{
			return;
		}
		
		ServerPackets.EX_PLEDGE_MISSION_INFO.writeId(this, buffer);
		buffer.writeInt(_rewards.size());
		final List<Integer> missions = _player.getVariables().getIntegerList(PlayerVariables.DAILY_MISSION_ONE_TIME);
		for (DailyMissionDataHolder reward : _rewards)
		{
			int progress = reward.getProgress(_player);
			int status = reward.getStatus(_player);
			
			// Client status:
			// 0 - Not displayed.
			// 1 - Locked or not available.
			// 2 - Available.
			// 3 - Completed.
			
			// One time missions that are completed should not be displayed.
			if (missions.contains(reward.getId()))
			{
				status = 0;
			}
			// Missions that don't meet the requirements should be locked.
			else if (((reward.getRequiredMissionCompleteId() != 0) && !missions.contains(reward.getRequiredMissionCompleteId())) || (_player.getLevel() < reward.getParams().getInt("minLevel", 0)))
			{
				status = 1;
			}
			// Level Up Missions.
			else if (reward.isLevelUpMission())
			{
				progress = 1;
				
				// Dualclass and Mainclass missions should not be displayed while you are on the other class.
				if ((_player.isDualClassActive() && reward.isMainClassOnly()) || (!_player.isDualClassActive() && reward.isDualClassOnly()))
				{
					status = 0;
				}
				else
				{
					status = _player.getLevel() >= reward.getParams().getInt("minLevel", 0) ? 3 : 1;
				}
			}
			// Other daily missions.
			else if (!reward.isLevelUpMission())
			{
				if ((reward.getRequiredCompletions() == progress) && (_player.getLevel() >= reward.getParams().getInt("minLevel", 0)))
				{
					status = 3; // Completed.
				}
				else
				{
					status = 2; // Available.
				}
			}
			
			buffer.writeInt(reward.getId());
			buffer.writeInt(progress);
			buffer.writeByte(status);
		}
	}
}
