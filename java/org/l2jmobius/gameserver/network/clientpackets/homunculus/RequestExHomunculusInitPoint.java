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
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusInitPointResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusPointInfo;

/**
 * @author Mobius
 */
public class RequestExHomunculusInitPoint extends ClientPacket
{
	private static final int POWERFUL_FISH = 47552;
	private static final int FISH_COUNT = 5;
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
		
		if (_type == 0)
		{
			final int usedResetKills = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_RESET_KILLS, 0);
			final int usedKillConvert = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_KILL_CONVERT, 0);
			final Item fish = player.getInventory().getItemByItemId(POWERFUL_FISH);
			if (((fish == null) || (fish.getCount() < FISH_COUNT)) || ((usedResetKills <= 3) && (usedKillConvert == 0)))
			{
				player.sendPacket(new ExHomunculusInitPointResult(false, _type));
			}
			else if ((usedResetKills <= 3) && (usedKillConvert == 5))
			{
				player.destroyItemByItemId(ItemProcessType.FEE, POWERFUL_FISH, FISH_COUNT, player, true);
				player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_KILL_CONVERT, 0);
				player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_RESET_KILLS, usedResetKills + 1);
				player.sendPacket(new ExHomunculusInitPointResult(true, _type));
				player.sendPacket(new ExHomunculusPointInfo(player));
			}
		}
		else
		{
			final int usedResetVp = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_RESET_VP, 0);
			final int usedVpConvert = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_VP_CONVERT, 0);
			final Item fish = player.getInventory().getItemByItemId(POWERFUL_FISH);
			if (((fish == null) || (fish.getCount() < FISH_COUNT)) || ((usedResetVp <= 3) && (usedVpConvert == 0)))
			{
				player.sendPacket(new ExHomunculusInitPointResult(false, _type));
			}
			else if ((usedResetVp <= 3) && (usedVpConvert == 5))
			{
				player.destroyItemByItemId(ItemProcessType.FEE, POWERFUL_FISH, FISH_COUNT, player, true);
				player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_VP_CONVERT, 0);
				player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_RESET_VP, usedResetVp + 1);
				player.sendPacket(new ExHomunculusInitPointResult(true, _type));
				player.sendPacket(new ExHomunculusPointInfo(player));
			}
		}
	}
}
