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
package org.l2jmobius.gameserver.network.clientpackets.huntingzones;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.holders.TimedHuntingZoneHolder;
import org.l2jmobius.gameserver.data.xml.TimedHuntingZoneData;
import org.l2jmobius.gameserver.managers.InstanceManager;
import org.l2jmobius.gameserver.managers.ScriptManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.olympiad.OlympiadManager;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.huntingzones.TimedHuntingZoneEnter;

/**
 * @author Mobius
 */
public class ExTimedHuntingZoneEnter extends ClientPacket
{
	private int _zoneId;
	
	@Override
	protected void readImpl()
	{
		_zoneId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.isInsideZone(ZoneId.CONQUEST))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TAKE_OTHER_ACTION_WHILE_ENTERING_THE_DIMENSIONAL_SPACE);
			return;
		}
		
		if (!player.isInsideZone(ZoneId.PEACE))
		{
			player.sendPacket(SystemMessageId.YOU_CAN_ENTER_THE_AREA_ONLY_FROM_PEACE_ZONE);
			return;
		}
		
		if (player.isInCombat())
		{
			player.sendMessage("You can only enter in time-limited hunting zones while not in combat.");
			return;
		}
		
		if (player.getReputation() < 0)
		{
			player.sendMessage("You can only enter in time-limited hunting zones when you have positive reputation.");
			return;
		}
		
		if (player.isInDuel())
		{
			player.sendMessage("Cannot use time-limited hunting zones during a duel.");
			return;
		}
		
		if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendPacket(SystemMessageId.SESSION_ZONES_ARE_UNAVAILABLE_WHILE_YOU_ARE_IN_QUEUE_FOR_THE_OLYMPIAD);
			return;
		}
		
		if (player.isRegisteredOnEvent() || (player.getBlockCheckerArena() > -1))
		{
			player.sendMessage("Cannot use time-limited hunting zones while registered on an event.");
			return;
		}
		
		if (player.isInInstance() /* || player.isInTimedHuntingZone() */)
		{
			player.sendMessage("Cannot use time-limited hunting zones while in an instance.");
			return;
		}
		
		final TimedHuntingZoneHolder holder = TimedHuntingZoneData.getInstance().getHuntingZone(_zoneId);
		if (holder == null)
		{
			return;
		}
		
		if ((player.getLevel() < holder.getMinLevel()) || (player.getLevel() > holder.getMaxLevel()))
		{
			player.sendMessage("Your level does not correspond the zone equivalent.");
			return;
		}
		
		final int instanceId = holder.getInstanceId();
		if ((instanceId > 0) && holder.isSoloInstance() && (InstanceManager.getInstance().getInstanceTime(player, instanceId) > System.currentTimeMillis()))
		{
			player.sendMessage("This transcendent instance has not reset yet.");
			return;
		}
		
		if (player.isMounted())
		{
			if (holder.useWorldPrefix())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_TO_THE_WORLD_HUNTING_ZONE_WHILE_RIDING_A_MOUNT);
			}
			else
			{
				player.sendMessage("Cannot use time-limited hunting zones while mounted.");
			}
			return;
		}
		
		if (holder.useWorldPrefix())
		{
			if (player.isCursedWeaponEquipped())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_TO_THE_WORLD_HUNTING_ZONE_WHILE_OWNING_A_CURSED_WEAPON);
				return;
			}
		}
		
		final long currentTime = System.currentTimeMillis();
		long endTime = currentTime + player.getTimedHuntingZoneRemainingTime(_zoneId);
		final long lastEntryTime = player.getVariables().getLong(PlayerVariables.HUNTING_ZONE_ENTRY + _zoneId, 0);
		if ((lastEntryTime + holder.getResetDelay()) < currentTime)
		{
			if (endTime == currentTime)
			{
				endTime += holder.getInitialTime();
			}
			
			player.getVariables().set(PlayerVariables.HUNTING_ZONE_ENTRY + _zoneId, currentTime);
		}
		
		if (endTime > currentTime)
		{
			if (holder.getEntryItemId() == Inventory.ADENA_ID)
			{
				if (player.getAdena() > holder.getEntryFee())
				{
					player.reduceAdena(ItemProcessType.FEE, holder.getEntryFee(), player, true);
				}
				else
				{
					player.sendPacket(SystemMessageId.NOT_ENOUGH_ADENA);
					return;
				}
			}
			else if (!player.destroyItemByItemId(ItemProcessType.FEE, holder.getEntryItemId(), holder.getEntryFee(), player, true))
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}
			
			player.getVariables().set(PlayerVariables.LAST_HUNTING_ZONE_ID, _zoneId);
			player.getVariables().set(PlayerVariables.HUNTING_ZONE_TIME + _zoneId, endTime - currentTime);
			
			if (instanceId == 0)
			{
				if (holder.useWorldPrefix())
				{
					player.sendPacket(SystemMessageId.YOU_WILL_BE_TAKEN_TO_THE_WORLD_HUNTING_ZONE_IN_3_SEC);
					player.stopMove(null);
					ThreadPool.schedule(() -> player.teleToLocation(holder.getEnterLocation()), 3000);
				}
				else
				{
					player.teleToLocation(holder.getEnterLocation());
				}
			}
			else // Instanced zones.
			{
				if (holder.useWorldPrefix())
				{
					player.sendPacket(SystemMessageId.YOU_WILL_BE_TAKEN_TO_THE_WORLD_HUNTING_ZONE_IN_3_SEC);
					player.stopMove(null);
					ThreadPool.schedule(() -> ScriptManager.getInstance().getScript("TimedHunting").notifyEvent("ENTER " + _zoneId, null, player), 3000);
				}
				else
				{
					ScriptManager.getInstance().getScript("TimedHunting").notifyEvent("ENTER " + _zoneId, null, player);
				}
			}
			
			// Send time icon.
			player.sendPacket(new TimedHuntingZoneEnter(player, _zoneId));
		}
		else
		{
			player.sendPacket(SystemMessageId.CURRENTLY_YOU_HAVE_THE_MAX_AMOUNT_OF_TIME_FOR_THE_HUNTING_ZONE_SO_YOU_CANNOT_ADD_ANY_MORE_TIME);
		}
	}
}
