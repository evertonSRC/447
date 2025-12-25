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
package org.l2jmobius.gameserver.network.clientpackets.teleports;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.holders.TeleportListHolder;
import org.l2jmobius.gameserver.data.xml.TeleportListData;
import org.l2jmobius.gameserver.managers.CastleManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.siege.Castle;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;

/**
 * @author NviX, Mobius
 */
public class ExRequestTeleport extends ClientPacket
{
	private int _teleportId;
	
	@Override
	protected void readImpl()
	{
		_teleportId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final TeleportListHolder teleport = TeleportListData.getInstance().getTeleport(_teleportId);
		if (teleport == null)
		{
			PacketLogger.warning("No registered teleport location for id: " + _teleportId);
			return;
		}
		
		// Dead characters cannot use teleports.
		if (player.isDead())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_WHILE_DEAD);
			return;
		}
		
		// Players should not be able to teleport if in a special location.
		if ((player.getMovieHolder() != null) || player.isFishing() || player.isInInstance() || player.isOnEvent() || player.isInOlympiadMode() || player.inObserverMode() || player.isInTraingCamp() || player.isInTimedHuntingZone() || player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_RIGHT_NOW);
			return;
		}
		
		// Should not teleport out from Conquest zone.
		if (player.isInsideZone(ZoneId.CONQUEST))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TAKE_OTHER_ACTION_WHILE_ENTERING_THE_DIMENSIONAL_SPACE);
			return;
		}
		
		// Teleport in combat configuration.
		if (!PlayerConfig.TELEPORT_WHILE_PLAYER_IN_COMBAT && (player.isInCombat() || player.isCastingNow()))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_WHILE_IN_COMBAT);
			return;
		}
		
		// Karma related configurations.
		if ((!PlayerConfig.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT || !PlayerConfig.ALT_GAME_KARMA_PLAYER_CAN_USE_GK) && (player.getReputation() < 0))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_RIGHT_NOW);
			return;
		}
		
		// Cannot escape effect.
		if (player.cannotEscape())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_RIGHT_NOW);
			return;
		}
		
		final Location location = teleport.getLocation();
		if (!PlayerConfig.TELEPORT_WHILE_SIEGE_IN_PROGRESS)
		{
			final Castle castle = CastleManager.getInstance().getCastle(location.getX(), location.getY(), location.getZ());
			if ((castle != null) && castle.getSiege().isInProgress())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
				return;
			}
		}
		
		if (player.getLevel() > PlayerConfig.MAX_FREE_TELEPORT_LEVEL)
		{
			int price = teleport.getPrice();
			if (price > 0)
			{
				// Tempfix: Adjust teleport price for client discrepancy.
				price *= 0.99f;
				
				if (player.getAdena() < price)
				{
					player.sendPacket(SystemMessageId.NOT_ENOUGH_ADENA);
					return;
				}
				
				player.reduceAdena(ItemProcessType.FEE, price, player, true);
			}
		}
		
		player.abortCast();
		player.stopMove(null);
		player.teleToLocation(location);
	}
}
