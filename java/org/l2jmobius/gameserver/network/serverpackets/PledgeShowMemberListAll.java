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
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.Collection;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.config.ServerConfig;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanMember;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class PledgeShowMemberListAll extends ServerPacket
{
	private final Clan _clan;
	private final String _name;
	private final String _leaderName;
	private final Collection<ClanMember> _members;
	
	private PledgeShowMemberListAll(Clan clan, boolean isSubPledge)
	{
		_clan = clan;
		_leaderName = clan.getLeaderName();
		_name = clan.getName();
		_members = _clan.getMembers();
	}
	
	public static void sendAllTo(Player player)
	{
		final Clan clan = player.getClan();
		player.sendPacket(new PledgeShowMemberListAll(clan, true));
		for (ClanMember member : clan.getMembers())
		{
			if (member.getPledgeType() != Clan.PLEDGE_CLASS_COMMON)
			{
				player.sendPacket(new PledgeShowMemberListUpdate(member));
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PLEDGE_SHOW_MEMBER_LIST_ALL.writeId(this, buffer);
		buffer.writeInt(0); // _isSubPledge
		buffer.writeInt(_clan.getId());
		buffer.writeInt(ServerConfig.SERVER_ID);
		buffer.writeInt(0);
		buffer.writeString(_name);
		buffer.writeString(_leaderName);
		buffer.writeInt(_clan.getCrestId()); // crest id .. is used again
		buffer.writeInt(_clan.getLevel());
		buffer.writeInt(_clan.getCastleId());
		buffer.writeInt(0);
		buffer.writeInt(_clan.getHideoutId());
		buffer.writeInt(_clan.getFortId());
		buffer.writeInt(_clan.getRank());
		buffer.writeInt(_clan.getReputationScore());
		buffer.writeInt(0); // 0
		buffer.writeInt(0); // 0
		buffer.writeInt(_clan.getAllyId());
		buffer.writeString(_clan.getAllyName());
		buffer.writeInt(_clan.getAllyCrestId());
		buffer.writeInt(_clan.isAtWar()); // new c3
		buffer.writeInt(0); // Territory castle ID
		buffer.writeInt(_members.size());
		for (ClanMember m : _members)
		{
			buffer.writeString(m.getName());
			buffer.writeInt(m.getLevel());
			buffer.writeInt(m.getClassId());
			buffer.writeInt(0); // sex
			buffer.writeInt(0); // race
			buffer.writeInt(m.isOnline() ? m.getObjectId() : 0); // objectId = online 0 = offline
			buffer.writeInt(0);
			buffer.writeByte(0);
		}
	}
}
