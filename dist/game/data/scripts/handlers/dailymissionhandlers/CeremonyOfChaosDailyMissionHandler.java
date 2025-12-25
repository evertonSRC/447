/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.dailymissionhandlers;

import java.util.List;

import org.l2jmobius.gameserver.handler.AbstractDailyMissionHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.DailyMissionStatus;
import org.l2jmobius.gameserver.model.actor.holders.player.DailyMissionDataHolder;
import org.l2jmobius.gameserver.model.actor.holders.player.DailyMissionPlayerEntry;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.holders.ceremonyofchaos.OnCeremonyOfChaosMatchResult;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;

/**
 * @author UnAfraid
 */
public class CeremonyOfChaosDailyMissionHandler extends AbstractDailyMissionHandler
{
	private final int _missionId;
	private final int _minLevel;
	private final int _maxLevel;
	private final int _minClanLevel;
	private final int _minClanMasteryLevel;
	private final int _amount;
	private final boolean _winOnly;
	private final int _requiredMissionCompleteId;
	
	public CeremonyOfChaosDailyMissionHandler(DailyMissionDataHolder holder)
	{
		super(holder);
		_missionId = holder.getId();
		_minLevel = holder.getParams().getInt("minLevel", 0);
		_maxLevel = holder.getParams().getInt("maxLevel", Integer.MAX_VALUE);
		_minClanLevel = holder.getParams().getInt("minClanLevel", 0);
		_minClanMasteryLevel = holder.getParams().getInt("minClanMasteryLevel", 0);
		_amount = holder.getRequiredCompletions();
		_winOnly = holder.getParams().getBoolean("winOnly", false);
		_requiredMissionCompleteId = holder.getRequiredMissionCompleteId();
	}
	
	@Override
	public void init()
	{
		Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_CEREMONY_OF_CHAOS_MATCH_RESULT, (OnCeremonyOfChaosMatchResult event) -> onCeremonyOfChaosMatchResult(event), this));
	}
	
	@Override
	public boolean isAvailable(Player player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		if (entry != null)
		{
			switch (entry.getStatus())
			{
				case NOT_AVAILABLE: // Initial state
				{
					if (entry.getProgress() >= _amount)
					{
						entry.setStatus(DailyMissionStatus.AVAILABLE);
						storePlayerEntry(entry);
					}
					break;
				}
				case AVAILABLE:
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void onCeremonyOfChaosMatchResult(OnCeremonyOfChaosMatchResult event)
	{
		List<Player> members;
		if (_winOnly)
		{
			members = event.getWinners();
		}
		else
		{
			members = event.getMembers();
		}
		
		members.forEach(member ->
		{
			if (checkPlayerLevel(member))
			{
				// Check clan only for specific missions
				if ((_missionId == 2109) || (_missionId == 2110))
				{
					if (checkClan(member))
					{
						if (((_requiredMissionCompleteId != 0) && checkRequiredMission(member)) || (_requiredMissionCompleteId == 0))
						{
							processPlayerProgress(member);
						}
					}
				}
				else
				{
					if (((_requiredMissionCompleteId != 0) && checkRequiredMission(member)) || (_requiredMissionCompleteId == 0))
					{
						processPlayerProgress(member);
					}
				}
			}
		});
	}
	
	private void processPlayerProgress(Player player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), true);
		if (entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE)
		{
			if (entry.increaseProgress() >= _amount)
			{
				entry.setStatus(DailyMissionStatus.AVAILABLE);
			}
			
			storePlayerEntry(entry);
		}
	}
	
	private boolean checkPlayerLevel(Player player)
	{
		if (player == null)
		{
			return false;
		}
		
		return ((player.getLevel() >= _minLevel)) || (player.getLevel() <= _maxLevel);
	}
	
	private boolean checkClan(Player player)
	{
		if (player == null)
		{
			return false;
		}
		
		final Clan clan = player.getClan();
		final int clanMastery = clan.hasMastery(14) ? 14 : clan.hasMastery(15) ? 15 : clan.hasMastery(16) ? 16 : 0;
		return ((clan.getLevel() >= _minClanLevel) && (clanMastery >= _minClanMasteryLevel));
	}
	
	private boolean checkRequiredMission(Player player)
	{
		final DailyMissionPlayerEntry missionEntry = getPlayerEntry(player.getObjectId(), false);
		return (missionEntry != null) && (_requiredMissionCompleteId != 0) && (missionEntry.getRewardId() == _requiredMissionCompleteId) && (getStatus(player) == DailyMissionStatus.COMPLETED.getClientId());
	}
}
