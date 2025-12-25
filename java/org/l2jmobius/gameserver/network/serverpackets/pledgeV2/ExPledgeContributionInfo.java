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

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExPledgeContributionInfo extends ServerPacket
{
	private final Player _player;
	private final boolean _cycle;
	private final int _previousClaims;
	private final int[] _reputationRequiredTiers =
	{
		2000,
		7000,
		15000,
		30000,
		45000
	};
	private final int[] _fameRewardTiers =
	{
		1300,
		3300,
		5400,
		10300,
		10600
	};
	
	public ExPledgeContributionInfo(Player player, boolean cycle)
	{
		_player = player;
		_cycle = cycle;
		_previousClaims = player.getVariables().getInt(PlayerVariables.CLAN_CONTRIBUTION_REWARDED_COUNT, 0);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		if (_player.getClan() == null)
		{
			return;
		}
		
		ServerPackets.EX_PLEDGE_CONTRIBUTION_INFO.writeId(this, buffer);
		if (_cycle)
		{
			buffer.writeInt(_player.getClanContribution());
		}
		else
		{
			buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CLAN_CONTRIBUTION_PREVIOUS, 0));
		}
		
		buffer.writeInt(_player.getClanContributionTotal());
		if (_previousClaims < 4)
		{
			buffer.writeInt(_reputationRequiredTiers[_previousClaims]);
			buffer.writeInt(-1);
			buffer.writeInt(0);
			buffer.writeInt(_fameRewardTiers[_previousClaims]);
		}
		else
		{
			int reputationRequired = 15000 * (_previousClaims);
			int fameReward = 10600 + ((_previousClaims - 4) * 200);
			buffer.writeInt(reputationRequired);
			buffer.writeInt(-1);
			buffer.writeInt(0);
			buffer.writeInt(fameReward);
		}
	}
}
