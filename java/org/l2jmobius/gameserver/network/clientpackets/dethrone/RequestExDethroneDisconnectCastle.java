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
package org.l2jmobius.gameserver.network.clientpackets.dethrone;

import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.dethrone.ExDethroneDisconnectCastle;
import org.l2jmobius.gameserver.network.serverpackets.dethrone.ExDethroneServerInfo;

/**
 * @author CostyKiller
 */
public class RequestExDethroneDisconnectCastle extends ClientPacket
{
	@Override
	protected void readImpl()
	{
		// _connected = readBoolean(); // cDummy
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!GlobalVariablesManager.getInstance().getBoolean("CONQUEST_CONNECTED", false))
		{
			player.sendMessage("Conquest connection is already inactive.");
		}
		else // Disconnect.
		{
			player.sendMessage("Conquest connection is inactive.");
			
			final long serverPoints = GlobalVariablesManager.getInstance().getLong("CONQUEST_SERVER_POINTS", 0);
			final long serverSoulOrbs = GlobalVariablesManager.getInstance().getLong("CONQUEST_SERVER_SOUL_ORBS", 0L);
			final boolean adenCastleOwner = (player.getClan() != null) && (player.getObjectId() == player.getClan().getLeaderId()) && (player.getClan().getCastleId() == 5); // Aden Castle Id is 5 _bConnected = true
			GlobalVariablesManager.getInstance().set("CONQUEST_CONNECTED", false);
			player.sendPacket(new ExDethroneDisconnectCastle(false));
			player.sendPacket(new ExDethroneServerInfo(serverPoints, serverSoulOrbs, adenCastleOwner, false));
			player.sendPacket(SystemMessageId.THE_CONNECTION_TO_THE_CONQUEST_WORLD_IS_LOST);
		}
	}
}
