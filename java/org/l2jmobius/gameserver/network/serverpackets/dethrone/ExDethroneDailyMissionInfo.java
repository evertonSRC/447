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
package org.l2jmobius.gameserver.network.serverpackets.dethrone;

import java.util.Collection;
import java.util.Collections;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.xml.DailyMissionDataConquest;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.DailyMissionDataHolder;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExDethroneDailyMissionInfo extends ServerPacket
{
	private final Player _player;
	private final Collection<DailyMissionDataHolder> _rewards;
	
	public ExDethroneDailyMissionInfo(Player player)
	{
		_player = player;
		_rewards = DailyMissionDataConquest.getInstance().getDailyMissionData(player);
	}
	
	public ExDethroneDailyMissionInfo(Player player, DailyMissionDataHolder holder)
	{
		_player = player;
		_rewards = Collections.singletonList(holder);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		if (!DailyMissionDataConquest.getInstance().isAvailable() || (_player.getClan() == null))
		{
			return;
		}
		
		ServerPackets.EX_DETHRONE_DAILY_MISSION_INFO.writeId(this, buffer);
		buffer.writeInt(_rewards.size()); // how many missions have
		for (DailyMissionDataHolder reward : _rewards)
		{
			int progress = reward.getProgress(_player);
			int status = reward.getStatus(_player);
			if (status == 2) // daily mission status
			{
				status = 0; // in progress status
			}
			else if (status == 1) // daily mission status
			{
				status = 2; // mission complete status
			}
			
			buffer.writeInt(reward.getId()); // mission name (only can use is 1)
			buffer.writeInt(progress); // current item count/300
			buffer.writeByte(status); // 1 - receive, 0 - no receive
		}
	}
}
