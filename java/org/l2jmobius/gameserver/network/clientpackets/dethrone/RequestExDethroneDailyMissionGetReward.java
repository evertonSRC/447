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
package org.l2jmobius.gameserver.network.clientpackets.dethrone;

import java.util.Collection;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.xml.DailyMissionDataConquest;
import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.DailyMissionDataHolder;
import org.l2jmobius.gameserver.model.actor.request.RewardRequest;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.dethrone.ExDethroneDailyMissionComplete;
import org.l2jmobius.gameserver.network.serverpackets.dethrone.ExDethroneDailyMissionGetReward;
import org.l2jmobius.gameserver.network.serverpackets.dethrone.ExDethroneDailyMissionInfo;

/**
 * @author CostyKiller
 */
public class RequestExDethroneDailyMissionGetReward extends ClientPacket
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
		
		final Collection<DailyMissionDataHolder> reward = DailyMissionDataConquest.getInstance().getDailyMissionData(_id);
		if ((reward != null) && !reward.isEmpty())
		{
			for (DailyMissionDataHolder holder : reward)
			{
				if (holder.isDisplayable(player))
				{
					holder.requestReward(player);
					player.sendPacket(new ExDethroneDailyMissionInfo(player, holder));
				}
				
				player.sendPacket(new ExDethroneDailyMissionComplete(true));
				
				final int personalDethronePoint = 250; // reward
				final int serverDethronePoint = 500; // reward
				final long currentPersonalDethronePoints = player.getVariables().getLong(PlayerVariables.CONQUEST_PERSONAL_POINTS, 0);
				final long currentServerDethronePoints = GlobalVariablesManager.getInstance().getLong("CONQUEST_SERVER_POINTS", 0L);
				player.sendPacket(new ExDethroneDailyMissionGetReward(_id, true, personalDethronePoint, serverDethronePoint));
				player.getVariables().set(PlayerVariables.CONQUEST_PERSONAL_POINTS, currentPersonalDethronePoints + personalDethronePoint);
				GlobalVariablesManager.getInstance().set("CONQUEST_SERVER_POINTS", currentServerDethronePoints + serverDethronePoint);
			}
		}
		
		ThreadPool.schedule(() -> player.removeRequest(RewardRequest.class), 300);
	}
}
