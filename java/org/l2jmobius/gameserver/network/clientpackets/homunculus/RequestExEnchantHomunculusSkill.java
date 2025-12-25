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
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExEnchantHomunculusSkillResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusHPSPVP;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusPointInfo;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusList;

/**
 * @author Mobius
 */
public class RequestExEnchantHomunculusSkill extends ClientPacket
{
	private static final int SP_COST = 100000;
	
	private int _slot;
	private int _skillNumber;
	
	@Override
	protected void readImpl()
	{
		readInt();
		_slot = readInt();
		_skillNumber = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.getSp() < SP_COST)
		{
			return;
		}
		
		int points = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_UPGRADE_POINTS, 0);
		if (points < 1)
		{
			player.sendMessage("Not enough upgrade points.");
			return;
		}
		
		player.getVariables().set(PlayerVariables.HOMUNCULUS_UPGRADE_POINTS, points - 1);
		player.setSp(player.getSp() - SP_COST);
		player.sendPacket(new ExEnchantHomunculusSkillResult(player, _slot, _skillNumber));
		player.sendPacket(new ExHomunculusHPSPVP(player));
		player.sendPacket(new ExShowHomunculusList(player));
		player.sendPacket(new ExHomunculusPointInfo(player));
	}
}
