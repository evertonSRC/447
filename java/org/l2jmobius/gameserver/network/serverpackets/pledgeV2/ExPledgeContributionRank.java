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
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanMember;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExPledgeContributionRank extends ServerPacket
{
	private final Clan _clan;
	private final int _cycle;
	
	public ExPledgeContributionRank(Clan clan, int cycle)
	{
		_clan = clan;
		_cycle = cycle;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		if (_clan == null)
		{
			return;
		}
		
		ServerPackets.EX_PLEDGE_CONTRIBUTION_RANK.writeId(this, buffer);
		buffer.writeByte(_cycle);
		buffer.writeInt(_clan.getMembersCount());
		int order = 1;
		for (ClanMember member : _clan.getMembers())
		{
			if (member.isOnline())
			{
				final Player player = member.getPlayer();
				buffer.writeInt(order++); // Order?
				buffer.writeString(String.format("%1$-" + 24 + "s", player.getName()));
				buffer.writeInt(player.getPledgeType());
				if (_cycle == 1)
				{
					buffer.writeInt(player.getClanContribution());
					buffer.writeInt(player.getClanContributionTotal());
				}
				else if (_cycle == 0)
				{
					buffer.writeInt(player.getClanContributionPrevious());
					buffer.writeInt(player.getClanContributionTotalPrevious());
				}
			}
			else
			{
				buffer.writeInt(order++); // Order?
				buffer.writeString(String.format("%1$-" + 24 + "s", member.getName()));
				buffer.writeInt(member.getPledgeType());
				if (_cycle == 1)
				{
					buffer.writeInt(member.getClanContribution());
					buffer.writeInt(member.getClanContributionTotal());
				}
				else if (_cycle == 0)
				{
					buffer.writeInt(member.getClanContributionPrevious());
					buffer.writeInt(member.getClanContributionTotalPrevious());
				}
			}
		}
	}
}
