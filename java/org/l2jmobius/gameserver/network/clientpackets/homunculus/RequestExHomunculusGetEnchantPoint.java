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
package org.l2jmobius.gameserver.network.clientpackets.homunculus;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.stat.PlayerStat;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusGetEnchantPointResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusHPSPVP;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusPointInfo;

/**
 * @author Mobius
 */
public class RequestExHomunculusGetEnchantPoint extends ClientPacket
{
	private int _type;
	
	@Override
	protected void readImpl()
	{
		_type = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_type == 0) // mobs
		{
			int killedMobs = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_KILLED_MOBS, 0);
			if (killedMobs < 500)
			{
				return;
			}
			
			int usedKillConverts = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_KILL_CONVERT, 0);
			if (usedKillConverts >= 5)
			{
				return;
			}
			
			int upgradePoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_UPGRADE_POINTS, 0) + 1;
			player.getVariables().set(PlayerVariables.HOMUNCULUS_UPGRADE_POINTS, upgradePoints);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_KILLED_MOBS, 0);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_KILL_CONVERT, usedKillConverts + 1);
		}
		else if (_type == 1) // vitality
		{
			int usedVpPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_VP_POINTS, 0);
			if (usedVpPoints < 2)
			{
				return;
			}
			
			int usedVpConverts = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_VP_CONVERT, 0);
			if (usedVpConverts >= 5)
			{
				return;
			}
			
			int upgradePoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_UPGRADE_POINTS, 0) + 1;
			player.getVariables().set(PlayerVariables.HOMUNCULUS_UPGRADE_POINTS, upgradePoints);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_VP_POINTS, 0);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_VP_CONVERT, usedVpConverts + 1);
		}
		else if (_type == 2) // vitality consume
		{
			int usedVpPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_VP_POINTS, 0);
			if (usedVpPoints >= 2)
			{
				return;
			}
			
			if (player.getVitalityPoints() >= (PlayerStat.MAX_VITALITY_POINTS / 4))
			{
				player.setVitalityPoints(player.getVitalityPoints() - (PlayerStat.MAX_VITALITY_POINTS / 4), false);
				player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_VP_POINTS, usedVpPoints + 1);
			}
		}
		
		if (_type == 2)
		{
			player.sendPacket(new ExHomunculusHPSPVP(player));
		}
		
		player.sendPacket(new ExHomunculusPointInfo(player));
		player.sendPacket(new ExHomunculusGetEnchantPointResult(_type));
	}
}
