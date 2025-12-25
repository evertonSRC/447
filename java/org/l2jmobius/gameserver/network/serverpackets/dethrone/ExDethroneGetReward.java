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
import org.l2jmobius.gameserver.managers.RankManager;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller, Mobius
 */
public class ExDethroneGetReward extends ServerPacket
{
	private final boolean _rewarded;
	private final Map<Integer, StatSet> _previousConquestPlayerList;
	
	public ExDethroneGetReward(Player player, boolean rewarded)
	{
		_rewarded = rewarded;
		_previousConquestPlayerList = RankManager.getInstance().getPreviousConquestRankList();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_DETHRONE_INFO.writeId(this, buffer);
	}
	
	@Override
	public void runImpl(Player player)
	{
		// Check rank for previous season.
		int rank = 0;
		long personalPoints = 0;
		if (!_previousConquestPlayerList.isEmpty())
		{
			for (Entry<Integer, StatSet> entry : _previousConquestPlayerList.entrySet())
			{
				if (entry.getValue().getInt("charId") == player.getObjectId())
				{
					rank = entry.getKey();
					personalPoints = entry.getValue().getLong("conquestPersonalPoints");
					break;
				}
			}
		}
		
		if (_rewarded)
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_RECEIVED_THE_REWARD);
		}
		else if (personalPoints > ConquestConfig.CONQUEST_PERSONAL_REWARD_MIN_POINTS) // Personal Reward.
		{
			// Rank percent formula.
			int rewardRank = 0;
			double rankPercent = ((rank * 100) / (RankManager.getInstance().getPreviousConquestRankList().size()));
			
			if ((rankPercent > 0) && (rankPercent < 5))
			{
				rewardRank = 1;
			}
			else if ((rankPercent > 5) && (rankPercent < 10))
			{
				rewardRank = 2;
			}
			else if ((rankPercent > 10) && (rankPercent < 20))
			{
				rewardRank = 3;
			}
			else if ((rankPercent > 20) && (rankPercent < 30))
			{
				rewardRank = 4;
			}
			else if ((rankPercent > 30) && (rankPercent < 40))
			{
				rewardRank = 5;
			}
			else if ((rankPercent > 40) && (rankPercent < 50))
			{
				rewardRank = 6;
			}
			else if ((rankPercent > 50) && (rankPercent < 60))
			{
				rewardRank = 7;
			}
			else if ((rankPercent > 60) && (rankPercent < 70))
			{
				rewardRank = 8;
			}
			else if ((rankPercent > 70) && (rankPercent < 80))
			{
				rewardRank = 9;
			}
			else if (rankPercent > 80)
			{
				rewardRank = 10;
			}
			
			switch (rewardRank)
			{
				case 1:
				{
					for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_1)
					{
						player.addItem(ItemProcessType.REWARD, reward.getId(), reward.getCount(), player, true);
					}
					break;
				}
				case 2:
				{
					for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_2)
					{
						player.addItem(ItemProcessType.REWARD, reward.getId(), reward.getCount(), player, true);
					}
					break;
				}
				case 3:
				{
					for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_3)
					{
						player.addItem(ItemProcessType.REWARD, reward.getId(), reward.getCount(), player, true);
					}
					break;
				}
				case 4:
				{
					for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_4)
					{
						player.addItem(ItemProcessType.REWARD, reward.getId(), reward.getCount(), player, true);
					}
					break;
				}
				case 5:
				{
					for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_5)
					{
						player.addItem(ItemProcessType.REWARD, reward.getId(), reward.getCount(), player, true);
					}
					break;
				}
				case 6:
				{
					for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_6)
					{
						player.addItem(ItemProcessType.REWARD, reward.getId(), reward.getCount(), player, true);
					}
					break;
				}
				case 7:
				{
					for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_7)
					{
						player.addItem(ItemProcessType.REWARD, reward.getId(), reward.getCount(), player, true);
					}
					break;
				}
				case 8:
				{
					for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_8)
					{
						player.addItem(ItemProcessType.REWARD, reward.getId(), reward.getCount(), player, true);
					}
					break;
				}
				case 9:
				{
					for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_9)
					{
						player.addItem(ItemProcessType.REWARD, reward.getId(), reward.getCount(), player, true);
					}
					break;
				}
				case 10:
				{
					for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_10)
					{
						player.addItem(ItemProcessType.REWARD, reward.getId(), reward.getCount(), player, true);
					}
					break;
				}
			}
			
			// Conqueror Server Reward.
			if (personalPoints > ConquestConfig.CONQUEST_SERVER_REWARD_MIN_POINTS)
			{
				for (ItemHolder reward : ConquestConfig.CONQUEST_REWARDS_RANK_PARTICIPANT)
				{
					player.addItem(ItemProcessType.REWARD, reward.getId(), reward.getCount(), player, true);
				}
			}
			
			player.getVariables().set(PlayerVariables.CONQUEST_REWARDS_RECEIVED, true);
		}
	}
}
