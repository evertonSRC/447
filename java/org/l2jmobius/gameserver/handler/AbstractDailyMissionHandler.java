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
package org.l2jmobius.gameserver.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.DailyMissionStatus;
import org.l2jmobius.gameserver.model.actor.holders.player.DailyMissionDataHolder;
import org.l2jmobius.gameserver.model.actor.holders.player.DailyMissionPlayerEntry;
import org.l2jmobius.gameserver.model.events.ListenersContainer;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.enums.SpecialItemType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Sdw, NasSeKa
 */
public abstract class AbstractDailyMissionHandler extends ListenersContainer
{
	protected Logger LOGGER = Logger.getLogger(getClass().getName());
	
	private final Map<Integer, DailyMissionPlayerEntry> _entries = new ConcurrentHashMap<>();
	private final DailyMissionDataHolder _holder;
	
	protected AbstractDailyMissionHandler(DailyMissionDataHolder holder)
	{
		_holder = holder;
		init();
	}
	
	public DailyMissionDataHolder getHolder()
	{
		return _holder;
	}
	
	public abstract boolean isAvailable(Player player);
	
	public boolean isLevelUpMission()
	{
		return false;
	}
	
	public abstract void init();
	
	public int getStatus(Player player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		return entry != null ? entry.getStatus().getClientId() : DailyMissionStatus.NOT_AVAILABLE.getClientId();
	}
	
	public int getProgress(Player player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		return entry != null ? entry.getProgress() : 0;
	}
	
	public boolean isRecentlyCompleted(Player player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		return (entry != null) && entry.isRecentlyCompleted();
	}
	
	public boolean requestReward(Player player)
	{
		if (isAvailable(player) || isLevelUpMission())
		{
			giveRewards(player);
			
			final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), true);
			if (!_holder.isOneTime())
			{
				int doneDailyMissions = player.getVariables().getInt(PlayerVariables.DAILY_MISSION_COUNT, 0);
				player.getVariables().set(PlayerVariables.DAILY_MISSION_COUNT, doneDailyMissions + 1);
				entry.setStatus(DailyMissionStatus.NOT_AVAILABLE);
				entry.setProgress(0);
			}
			else
			{
				entry.setStatus(DailyMissionStatus.COMPLETED);
				final List<Integer> missions = player.getVariables().getIntegerList(PlayerVariables.DAILY_MISSION_ONE_TIME);
				missions.add(_holder.getId());
				player.getVariables().setIntegerList(PlayerVariables.DAILY_MISSION_ONE_TIME, missions);
			}
			
			entry.setLastCompleted(System.currentTimeMillis());
			entry.setRecentlyCompleted(true);
			storePlayerEntry(entry);
			
			return true;
		}
		
		return false;
	}
	
	protected void giveRewards(Player player)
	{
		for (ItemHolder reward : _holder.getRewards())
		{
			if (reward.getId() == SpecialItemType.CLAN_REPUTATION.getClientId())
			{
				player.getClan().addReputationScore((int) reward.getCount());
				player.increaseClanContribution((int) reward.getCount());
				final SystemMessage sm = new SystemMessage(SystemMessageId.CLAN_REPUTATION_POINTS_S1_3);
				sm.addLong((int) reward.getCount());
				player.sendPacket(sm);
			}
			else if (reward.getId() == SpecialItemType.FAME.getClientId())
			{
				player.setFame(player.getFame() + (int) reward.getCount());
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_ACQUIRED_S1_INDIVIDUAL_FAME);
				sm.addLong((int) reward.getCount());
				player.sendPacket(sm);
				player.broadcastUserInfo();
			}
			else
			{
				player.addItem(ItemProcessType.REWARD, reward, player, true);
			}
		}
	}
	
	protected void storePlayerEntry(DailyMissionPlayerEntry entry)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("REPLACE INTO character_daily_rewards (charId, rewardId, status, progress, lastCompleted) VALUES (?, ?, ?, ?, ?)"))
		{
			ps.setInt(1, entry.getObjectId());
			ps.setInt(2, entry.getRewardId());
			ps.setInt(3, entry.getStatus().getClientId());
			ps.setInt(4, entry.getProgress());
			ps.setLong(5, entry.getLastCompleted());
			ps.execute();
			
			// Cache if not exists
			_entries.computeIfAbsent(entry.getObjectId(), _ -> entry);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error while saving reward " + entry.getRewardId() + " for player: " + entry.getObjectId() + " in database: ", e);
		}
	}
	
	protected DailyMissionPlayerEntry getPlayerEntry(int objectId, boolean createIfNone)
	{
		DailyMissionPlayerEntry entry = _entries.get(objectId);
		if (entry != null)
		{
			return entry;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM character_daily_rewards WHERE charId = ? AND rewardId = ?"))
		{
			ps.setInt(1, objectId);
			ps.setInt(2, _holder.getId());
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					entry = new DailyMissionPlayerEntry(rs.getInt("charId"), rs.getInt("rewardId"), rs.getInt("status"), rs.getInt("progress"), rs.getLong("lastCompleted"));
					_entries.put(objectId, entry);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error while loading reward " + _holder.getId() + " for player: " + objectId + " in database: ", e);
		}
		
		if ((entry == null) && createIfNone)
		{
			entry = new DailyMissionPlayerEntry(objectId, _holder.getId());
			_entries.put(objectId, entry);
		}
		
		return entry;
	}
}
