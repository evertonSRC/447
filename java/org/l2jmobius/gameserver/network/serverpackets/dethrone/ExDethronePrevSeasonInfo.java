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

import java.util.Map;
import java.util.Map.Entry;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.config.ConquestConfig;
import org.l2jmobius.gameserver.config.ServerConfig;
import org.l2jmobius.gameserver.managers.RankManager;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller, Mobius
 */
public class ExDethronePrevSeasonInfo extends ServerPacket
{
	private final Player _player;
	private final Long _previousSeasonServerPoints;
	private final Long _previousSeasonServerSoulOrbs;
	private int _rewardRank = 0;
	private long _personalPoints = 0;
	private int _rank = 0;
	private int _rankPercent = 0;
	private String _conquerorName = "";
	
	public ExDethronePrevSeasonInfo(Player player, Long prevServerPoints, Long prevServerSoulOrbs)
	{
		_player = player;
		_previousSeasonServerPoints = prevServerPoints;
		_previousSeasonServerSoulOrbs = prevServerSoulOrbs;
		
		final RankManager manager = RankManager.getInstance();
		final Map<Integer, StatSet> previousConquestPlayerList = manager.getPreviousConquestRankList();
		if (!previousConquestPlayerList.isEmpty())
		{
			for (Entry<Integer, StatSet> entry : previousConquestPlayerList.entrySet())
			{
				if (entry.getValue().getInt("charId") == _player.getObjectId())
				{
					_rank = entry.getKey();
					_personalPoints = entry.getValue().getLong("conquestPersonalPoints");
					break;
				}
			}
			
			_conquerorName = previousConquestPlayerList.get(1).getString("conquest_name");
			_rankPercent = previousConquestPlayerList.size();
		}
		
		// Rank percent formula.
		if (_personalPoints > ConquestConfig.CONQUEST_PERSONAL_REWARD_MIN_POINTS)
		{
			final double rankPercent = ((_rank * 100) / (previousConquestPlayerList.size()));
			if ((rankPercent > 0) && (rankPercent < 5))
			{
				_rewardRank = 1;
			}
			else if ((rankPercent > 5) && (rankPercent < 10))
			{
				_rewardRank = 2;
			}
			else if ((rankPercent > 10) && (rankPercent < 20))
			{
				_rewardRank = 3;
			}
			else if ((rankPercent > 20) && (rankPercent < 30))
			{
				_rewardRank = 4;
			}
			else if ((rankPercent > 30) && (rankPercent < 40))
			{
				_rewardRank = 5;
			}
			else if ((rankPercent > 40) && (rankPercent < 50))
			{
				_rewardRank = 6;
			}
			else if ((rankPercent > 50) && (rankPercent < 60))
			{
				_rewardRank = 7;
			}
			else if ((rankPercent > 60) && (rankPercent < 70))
			{
				_rewardRank = 8;
			}
			else if ((rankPercent > 70) && (rankPercent < 80))
			{
				_rewardRank = 9;
			}
			else if (rankPercent > 80)
			{
				_rewardRank = 10;
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_DETHRONE_PREV_SEASON_INFO.writeId(this, buffer);
		
		// Previous Results
		// Terr. Owner
		buffer.writeSizedString(_conquerorName); // sConquerorName
		buffer.writeInt(_rank); // My Rank
		buffer.writeInt(_rankPercent); // My Rank percent
		
		buffer.writeInt(ServerConfig.SERVER_ID); // Conqueror Server
		buffer.writeInt(1); // total rankers only 1 server atm
		buffer.writeInt(ServerConfig.SERVER_ID); // server id
		buffer.writeLong(_previousSeasonServerPoints); // Server points
		
		buffer.writeLong(_previousSeasonServerSoulOrbs); // Total Soul Orbs
		buffer.writeLong(_previousSeasonServerPoints); // Server points
		
		// Personal Reward.
		switch (_rewardRank)
		{
			case 1:
			{
				buffer.writeInt(ConquestConfig.CONQUEST_REWARDS_RANK_1.size());
				for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_1)
				{
					buffer.writeInt(reward.getId());
					buffer.writeLong(reward.getCount());
				}
				break;
			}
			case 2:
			{
				buffer.writeInt(ConquestConfig.CONQUEST_REWARDS_RANK_2.size());
				for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_2)
				{
					buffer.writeInt(reward.getId());
					buffer.writeLong(reward.getCount());
				}
				break;
			}
			case 3:
			{
				buffer.writeInt(ConquestConfig.CONQUEST_REWARDS_RANK_3.size());
				for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_3)
				{
					buffer.writeInt(reward.getId());
					buffer.writeLong(reward.getCount());
				}
				break;
			}
			case 4:
			{
				buffer.writeInt(ConquestConfig.CONQUEST_REWARDS_RANK_4.size());
				for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_4)
				{
					buffer.writeInt(reward.getId());
					buffer.writeLong(reward.getCount());
				}
				break;
			}
			case 5:
			{
				buffer.writeInt(ConquestConfig.CONQUEST_REWARDS_RANK_5.size());
				for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_5)
				{
					buffer.writeInt(reward.getId());
					buffer.writeLong(reward.getCount());
				}
				break;
			}
			case 6:
			{
				buffer.writeInt(ConquestConfig.CONQUEST_REWARDS_RANK_6.size());
				for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_6)
				{
					buffer.writeInt(reward.getId());
					buffer.writeLong(reward.getCount());
				}
				break;
			}
			case 7:
			{
				buffer.writeInt(ConquestConfig.CONQUEST_REWARDS_RANK_7.size());
				for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_7)
				{
					buffer.writeInt(reward.getId());
					buffer.writeLong(reward.getCount());
				}
				break;
			}
			case 8:
			{
				buffer.writeInt(ConquestConfig.CONQUEST_REWARDS_RANK_8.size());
				for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_8)
				{
					buffer.writeInt(reward.getId());
					buffer.writeLong(reward.getCount());
				}
				break;
			}
			case 9:
			{
				buffer.writeInt(ConquestConfig.CONQUEST_REWARDS_RANK_9.size());
				for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_9)
				{
					buffer.writeInt(reward.getId());
					buffer.writeLong(reward.getCount());
				}
				break;
			}
			case 10:
			{
				buffer.writeInt(ConquestConfig.CONQUEST_REWARDS_RANK_10.size());
				for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_10)
				{
					buffer.writeInt(reward.getId());
					buffer.writeLong(reward.getCount());
				}
				break;
			}
			default:
			{
				buffer.writeInt(0); // 0 - No reward available.
				break;
			}
		}
		
		// Conqueror Server Reward.
		if (_personalPoints > ConquestConfig.CONQUEST_SERVER_REWARD_MIN_POINTS)
		{
			buffer.writeInt(ConquestConfig.CONQUEST_REWARDS_RANK_PARTICIPANT.size());
			for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_PARTICIPANT)
			{
				buffer.writeInt(reward.getId());
				buffer.writeLong(reward.getCount());
			}
		}
		else
		{
			buffer.writeInt(0); // 0 - No reward available.
		}
		
		// Conquest personal reward ranking only available if player has more than 1 personal points.
		// Conquest server reward only available if player has more than 1k personal points.
		buffer.writeByte(_personalPoints > 1); // Reward button available (grey false - green true).
	}
}
