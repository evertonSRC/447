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

import java.util.Map.Entry;
import java.util.Set;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.config.ServerConfig;
import org.l2jmobius.gameserver.managers.RankManager;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller, Mobius
 */
public class ExDethroneInfo extends ServerPacket
{
	private final String _playerName;
	private final int _attackPoint;
	private final int _life;
	private final long _personalDethronePoint;
	private final long _serverDethronePoint;
	private final long _topServerDethronePoint;
	private final int _rank;
	private final int _rankSize;
	private final String _topRankerName;
	private final int _previousRankSize;
	private int _previousRank = 0;
	private long _prevPersonalPoints = 0;
	
	public ExDethroneInfo(Player player, String playerName, int attackPoint, int life, long personalDethronePoint, long serverDethronePoint)
	{
		_playerName = playerName;
		_attackPoint = attackPoint;
		_life = life;
		_personalDethronePoint = personalDethronePoint;
		_serverDethronePoint = serverDethronePoint;
		_topServerDethronePoint = serverDethronePoint;
		
		final RankManager manager = RankManager.getInstance();
		_rank = manager.getPlayerConquestGlobalRank(player);
		_rankSize = manager.getCurrentConquestRankList().size();
		_topRankerName = manager.getConquestTopRankName();
		
		final Set<Entry<Integer, StatSet>> previousRankList = manager.getPreviousConquestRankList().entrySet();
		for (Entry<Integer, StatSet> entry : previousRankList)
		{
			final StatSet info = entry.getValue();
			if (info.getInt("charId") == player.getObjectId())
			{
				_previousRank = entry.getKey();
				_prevPersonalPoints = info.getLong("conquestPersonalPoints");
				break;
			}
		}
		
		_previousRankSize = previousRankList.size();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_DETHRONE_INFO.writeId(this, buffer);
		
		buffer.writeSizedString(_playerName);
		
		buffer.writeInt(_attackPoint); // nAttackPoint 100
		buffer.writeInt(_life); // nLife 20
		
		buffer.writeInt(_rank); // nRank
		buffer.writeInt(_rankSize); // rank percents
		buffer.writeLong(_personalDethronePoint); // nPersonalDethronePoint
		
		buffer.writeInt(_previousRank); // nPrevRank
		buffer.writeInt(_previousRankSize); // rank percents
		buffer.writeLong(_prevPersonalPoints); // nPrevDethronePoint
		
		buffer.writeInt(1); // nServerRank
		buffer.writeLong(_serverDethronePoint); // nServerDethronePoint
		
		// Terr. Owner
		buffer.writeInt(ServerConfig.SERVER_ID); // nConquerorWorldID (Server Id of the conqueror player)
		buffer.writeSizedString(_topRankerName); // sTopRankerName; // conquest char name
		
		// Conqueror Server
		buffer.writeInt(ServerConfig.SERVER_ID); // nOccupyingServerWorldID
		
		// Conquest Status
		// set from SeasonInfo Packet
		
		// Rank 1
		buffer.writeInt(ServerConfig.SERVER_ID); // nTopRankerWorldID
		buffer.writeSizedString(_topRankerName); // sTopRankerName; // conquest char name
		
		buffer.writeInt(ServerConfig.SERVER_ID); // Leading Server nTopServerWorldID
		buffer.writeLong(_topServerDethronePoint); // Server Points nTopServerDethronePoint
	}
}
