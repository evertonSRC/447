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
package org.l2jmobius.gameserver.network.clientpackets.pledgeV2;

import java.util.Collection;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.xml.DailyMissionData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.DailyMissionDataHolder;
import org.l2jmobius.gameserver.model.actor.request.RewardRequest;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.pledgeV2.ExPledgeMissionInfo;
import org.l2jmobius.gameserver.network.serverpackets.pledgeV2.ExPledgeMissionRewardCount;

/**
 * @author Mobius
 */
public class RequestExPledgeMissionReward extends ClientPacket
{
	private int _id;
	
	@Override
	protected void readImpl()
	{
		_id = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		if (!getClient().getFloodProtectors().canPerformPlayerAction())
		{
			return;
		}
		
		final Player player = getPlayer();
		if ((player == null) || (player.getClan() == null))
		{
			return;
		}
		
		if (player.hasRequest(RewardRequest.class))
		{
			return;
		}
		
		player.addRequest(new RewardRequest(player));
		
		final Collection<DailyMissionDataHolder> reward = DailyMissionData.getInstance().getDailyMissionData(_id);
		if ((reward != null) && !reward.isEmpty())
		{
			for (DailyMissionDataHolder holder : reward)
			{
				if (holder.isDisplayable(player) && (!holder.isOneTime() || !player.getVariables().getIntegerList(PlayerVariables.DAILY_MISSION_ONE_TIME).contains(_id)))
				{
					holder.requestReward(player);
					player.sendPacket(new ExPledgeMissionInfo(player, holder));
				}
			}
			
			player.sendPacket(new ExPledgeMissionRewardCount(player));
		}
		
		ThreadPool.schedule(() -> player.removeRequest(RewardRequest.class), 300);
	}
}
