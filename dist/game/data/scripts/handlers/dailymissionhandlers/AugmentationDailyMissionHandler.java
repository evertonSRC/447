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
package handlers.dailymissionhandlers;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.gameserver.handler.AbstractDailyMissionHandler;
import org.l2jmobius.gameserver.model.VariationInstance;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.DailyMissionStatus;
import org.l2jmobius.gameserver.model.actor.holders.player.DailyMissionDataHolder;
import org.l2jmobius.gameserver.model.actor.holders.player.DailyMissionPlayerEntry;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerAugment;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;

/**
 * @author CostyKiller
 */
public class AugmentationDailyMissionHandler extends AbstractDailyMissionHandler
{
	private final int _missionId;
	private final int _amount;
	private final int _minLevel;
	private final int _maxLevel;
	private final Set<Integer> _mineralIds = new HashSet<>();
	
	public AugmentationDailyMissionHandler(DailyMissionDataHolder holder)
	{
		super(holder);
		_missionId = holder.getId();
		_amount = holder.getRequiredCompletions();
		_minLevel = holder.getParams().getInt("minLevel", 0);
		_maxLevel = holder.getParams().getInt("maxLevel", Integer.MAX_VALUE);
		final String mineralIds = holder.getParams().getString("mineralIds", "");
		if (!mineralIds.isEmpty())
		{
			for (String s : mineralIds.split(","))
			{
				final int id = Integer.parseInt(s);
				if (!_mineralIds.contains(id))
				{
					_mineralIds.add(id);
				}
			}
		}
	}
	
	@Override
	public void init()
	{
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_AUGMENT, (OnPlayerAugment event) -> onPlayerAugment(event), this));
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
	
	private void onPlayerAugment(OnPlayerAugment event)
	{
		final Player player = event.getPlayer();
		if ((player.getLevel() < _minLevel) || (player.getLevel() > _maxLevel))
		{
			return;
		}
		
		// Only missions with specific augment stones
		if ((_missionId == 3055) || (_missionId == 3056) || (_missionId == 3057))
		{
			// Check if used item has been augmented with specified stones
			final VariationInstance augmentation = event.getItem().getAugmentation();
			if (augmentation != null)
			{
				for (int mineralId : _mineralIds)
				{
					if ((augmentation.getMineralId() == mineralId) && player.getInventory().getItemByItemId(event.getItem().getId()).isAugmented())
					{
						processPlayerProgress(player);
					}
				}
			}
		}
		else if (_mineralIds.isEmpty())
		{
			processPlayerProgress(player);
		}
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
}
