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
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusEnchantEXPResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusPointInfo;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusList;

/**
 * @author Mobius
 */
public class RequestExHomunculusEnchantExp extends ClientPacket
{
	private static final int EXP_PER_POINT = 675;
	
	private int _slot;
	
	@Override
	protected void readImpl()
	{
		_slot = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if ((player.getVariables().getInt(PlayerVariables.HOMUNCULUS_UPGRADE_POINTS, 0) == 0) || (player.getHomunculusList().get(_slot).getExp() >= player.getHomunculusList().get(_slot).getTemplate().getExpToLevel6()))
		{
			player.sendPacket(new ExHomunculusEnchantEXPResult(false, false));
		}
		else
		{
			final int points = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_UPGRADE_POINTS, 0) - 1;
			player.getVariables().set(PlayerVariables.HOMUNCULUS_UPGRADE_POINTS, points);
			final Homunculus homunculus = player.getHomunculusList().get(_slot);
			if (homunculus == null)
			{
				player.sendPacket(new ExHomunculusEnchantEXPResult(false, false));
				return;
			}
			
			homunculus.setExp(homunculus.getExp() + EXP_PER_POINT);
			player.sendPacket(new ExHomunculusPointInfo(player));
			switch (homunculus.getLevel())
			{
				case 1:
				{
					if (homunculus.getExp() >= homunculus.getTemplate().getExpToLevel2())
					{
						homunculus.setLevel(2);
						player.getHomunculusList().refreshStats(true);
						player.sendPacket(new ExHomunculusEnchantEXPResult(true, true));
					}
					else
					{
						player.sendPacket(new ExHomunculusEnchantEXPResult(true, false));
					}
					break;
				}
				case 2:
				{
					if (homunculus.getExp() >= homunculus.getTemplate().getExpToLevel3())
					{
						homunculus.setLevel(3);
						player.getHomunculusList().refreshStats(true);
						player.sendPacket(new ExHomunculusEnchantEXPResult(true, true));
					}
					else
					{
						player.sendPacket(new ExHomunculusEnchantEXPResult(true, false));
					}
					break;
				}
				case 3:
				{
					if (homunculus.getExp() >= homunculus.getTemplate().getExpToLevel4())
					{
						homunculus.setLevel(4);
						player.getHomunculusList().refreshStats(true);
						player.sendPacket(new ExHomunculusEnchantEXPResult(true, true));
					}
					else
					{
						player.sendPacket(new ExHomunculusEnchantEXPResult(true, false));
					}
					break;
				}
				case 4:
				{
					if (homunculus.getExp() >= homunculus.getTemplate().getExpToLevel5())
					{
						homunculus.setLevel(5);
						player.getHomunculusList().refreshStats(true);
						player.sendPacket(new ExHomunculusEnchantEXPResult(true, true));
					}
					else
					{
						player.sendPacket(new ExHomunculusEnchantEXPResult(true, false));
					}
					break;
				}
				case 5:
				{
					if ((homunculus.getTemplate().getExpToLevel6() - homunculus.getExp()) < EXP_PER_POINT)
					{
						homunculus.setExp(homunculus.getExp() + (homunculus.getTemplate().getExpToLevel6() - homunculus.getExp()));
						player.sendPacket(new ExHomunculusPointInfo(player));
					}
					
					if (homunculus.getExp() >= homunculus.getTemplate().getExpToLevel6())
					{
						return;
					}
					break;
				}
			}
			
			player.getHomunculusList().update(homunculus);
			player.sendPacket(new ExShowHomunculusList(player));
		}
	}
}
