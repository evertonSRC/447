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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class ExDethroneRankingInfo extends ServerPacket
{
	private final int _currentSeason;
	private final int _rankingScope;
	private final List<RankInfo> _rankInfoList;
	
	public ExDethroneRankingInfo(Player player, int currentSeason, int rankingScope)
	{
		_currentSeason = currentSeason;
		_rankingScope = rankingScope;
		
		if (_currentSeason == 1)
		{
			final Map<Integer, StatSet> currentConquestPlayerList = RankManager.getInstance().getCurrentConquestRankList();
			if (_rankingScope == 0)
			{
				// Top-100 for current cycle.
				_rankInfoList = extractRankInfo(currentConquestPlayerList, 100);
			}
			else
			{
				// Personal ranking for current cycle.
				_rankInfoList = extractPersonalRankInfo(currentConquestPlayerList, player);
			}
		}
		else
		{
			final Map<Integer, StatSet> previousConquestPlayerList = RankManager.getInstance().getPreviousConquestRankList();
			if (_rankingScope == 0)
			{
				// Top-100 for previous cycle.
				_rankInfoList = extractRankInfo(previousConquestPlayerList, Integer.MAX_VALUE);
			}
			else
			{
				// Personal ranking for previous cycle.
				_rankInfoList = extractPersonalRankInfo(previousConquestPlayerList, player);
			}
		}
	}
	
	private List<RankInfo> extractRankInfo(Map<Integer, StatSet> playerList, int maxSize)
	{
		final List<RankInfo> rankInfoList = new LinkedList<>();
		int count = Math.min(playerList.size(), maxSize);
		for (Entry<Integer, StatSet> entry : playerList.entrySet())
		{
			if (rankInfoList.size() >= count)
			{
				break;
			}
			
			rankInfoList.add(new RankInfo(entry.getKey(), entry.getValue()));
		}
		
		return rankInfoList;
	}
	
	private List<RankInfo> extractPersonalRankInfo(Map<Integer, StatSet> playerList, Player player)
	{
		final List<RankInfo> rankInfoList = new LinkedList<>();
		for (Entry<Integer, StatSet> entry : playerList.entrySet())
		{
			if (entry.getValue().getInt("charId") == player.getObjectId())
			{
				final int id = entry.getKey();
				final int first = id > 10 ? (id - 9) : 1;
				final int last = Math.min(id + 10, playerList.size());
				for (int id2 = first; id2 <= last; id2++)
				{
					rankInfoList.add(new RankInfo(id2, playerList.get(id2)));
				}
				break;
			}
		}
		
		return rankInfoList;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_DETHRONE_RANKING_INFO.writeId(this, buffer);
		buffer.writeByte(_currentSeason);
		buffer.writeByte(_rankingScope);
		buffer.writeInt(_rankInfoList.size()); // Rank percent?
		buffer.writeInt(_rankInfoList.size());
		for (RankInfo rankInfo : _rankInfoList)
		{
			buffer.writeInt(rankInfo.rank);
			buffer.writeInt(ServerConfig.SERVER_ID);
			buffer.writeSizedString(rankInfo.name);
			buffer.writeLong(rankInfo.points);
		}
	}
	
	private class RankInfo
	{
		final int rank;
		final String name;
		final long points;
		
		RankInfo(int rank, StatSet set)
		{
			this.rank = rank;
			this.name = set.getString("name", "");
			this.points = set.getLong("conquestPersonalPoints", 0);
		}
	}
}
