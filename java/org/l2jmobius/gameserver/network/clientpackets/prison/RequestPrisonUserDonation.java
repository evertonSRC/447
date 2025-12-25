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
package org.l2jmobius.gameserver.network.clientpackets.prison;

import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.zone.type.ScriptZone;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.prison.ExPrisonUserDonation;

/**
 * @author Fakee
 */
public class RequestPrisonUserDonation extends ClientPacket
{
	private static final ScriptZone PRISON_ZONE_1 = ZoneManager.getInstance().getZoneById(26010, ScriptZone.class);
	private static final ScriptZone PRISON_ZONE_2 = ZoneManager.getInstance().getZoneById(26011, ScriptZone.class);
	private static final ScriptZone PRISON_ZONE_3 = ZoneManager.getInstance().getZoneById(26012, ScriptZone.class);
	private static final Location EXIT_LOCATION1 = new Location(61072, -43395, -2992);
	private static final Location EXIT_LOCATION2 = new Location(59317, -43502, -2992);
	private static final Location EXIT_LOCATION3 = new Location(60026, -44630, -2992);
	private static final long PRISON_ZONE_1_DONATION = 1000000000;
	private static final long PRISON_ZONE_2_DONATION = 1500000000;
	private static final long PRISON_ZONE_3_DONATION = 2000000000;
	
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (PRISON_ZONE_3.isCharacterInZone(player))
		{
			if (!player.reduceAdena(ItemProcessType.FEE, PRISON_ZONE_3_DONATION, player, true))
			{
				player.sendPacket(new ExPrisonUserDonation(false));
			}
			else
			{
				player.getVariables().set(PlayerVariables.PRISON_WAIT_TIME, 0);
				player.setReputation(0);
				player.sendPacket(new ExPrisonUserDonation(true));
				player.teleToLocation(EXIT_LOCATION3);
			}
		}
		else if (PRISON_ZONE_2.isCharacterInZone(player))
		{
			if (!player.reduceAdena(ItemProcessType.FEE, PRISON_ZONE_2_DONATION, player, true))
			{
				player.sendPacket(new ExPrisonUserDonation(false));
			}
			else
			{
				player.getVariables().set(PlayerVariables.PRISON_WAIT_TIME, 0);
				player.setReputation(0);
				player.sendPacket(new ExPrisonUserDonation(true));
				player.teleToLocation(EXIT_LOCATION2);
			}
		}
		else if (PRISON_ZONE_1.isCharacterInZone(player))
		{
			if (!player.reduceAdena(ItemProcessType.FEE, PRISON_ZONE_1_DONATION, player, true))
			{
				player.sendPacket(new ExPrisonUserDonation(false));
			}
			else
			{
				player.getVariables().set(PlayerVariables.PRISON_WAIT_TIME, 0);
				player.setReputation(0);
				player.sendPacket(new ExPrisonUserDonation(true));
				player.teleToLocation(EXIT_LOCATION1);
			}
		}
	}
}
