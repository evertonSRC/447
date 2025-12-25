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
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.zone.type.ScriptZone;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.prison.ExPrisonUserInfo;

/**
 * @author Fakee
 */
public class RequestPrisonUserInfo extends ClientPacket
{
	private static final ScriptZone PRISON_ZONE_1 = ZoneManager.getInstance().getZoneById(26010, ScriptZone.class);
	private static final ScriptZone PRISON_ZONE_2 = ZoneManager.getInstance().getZoneById(26011, ScriptZone.class);
	private static final ScriptZone PRISON_ZONE_3 = ZoneManager.getInstance().getZoneById(26012, ScriptZone.class);
	
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
		
		if (PRISON_ZONE_1.isCharacterInZone(player))
		{
			player.sendPacket(new ExPrisonUserInfo(player, 1));
		}
		else if (PRISON_ZONE_2.isCharacterInZone(player))
		{
			player.sendPacket(new ExPrisonUserInfo(player, 2));
		}
		else if (PRISON_ZONE_3.isCharacterInZone(player))
		{
			player.sendPacket(new ExPrisonUserInfo(player, 3));
		}
	}
}
