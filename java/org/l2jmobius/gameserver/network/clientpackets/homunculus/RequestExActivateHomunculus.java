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

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExActivateHomunculusResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusList;

/**
 * @author Mobius
 */
public class RequestExActivateHomunculus extends ClientPacket
{
	private int _slot;
	private boolean _activate;
	
	@Override
	protected void readImpl()
	{
		_slot = readInt();
		_activate = readByte() == 1; // enabled?
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final int size = player.getHomunculusList().size();
		if (size == 0)
		{
			return;
		}
		
		final Homunculus homunculus = player.getHomunculusList().get(_slot);
		if (homunculus == null)
		{
			return;
		}
		
		for (int i = 0; i < PlayerConfig.MAX_HOMUNCULUS_COUNT; i++)
		{
			if (size <= i)
			{
				break;
			}
			
			final Homunculus homu = player.getHomunculusList().get(i);
			if (homu == null)
			{
				continue;
			}
			
			if (homu.isActive())
			{
				homu.setActive(false);
				player.getHomunculusList().update(homu);
				player.getHomunculusList().refreshStats(true);
				player.sendPacket(new ExShowHomunculusList(player));
				player.sendPacket(new ExActivateHomunculusResult(false));
			}
		}
		
		if (_activate)
		{
			if (!homunculus.isActive())
			{
				
				homunculus.setActive(true);
				player.getHomunculusList().update(homunculus);
				player.getHomunculusList().refreshStats(true);
				player.sendPacket(new ExShowHomunculusList(player));
				player.sendPacket(new ExActivateHomunculusResult(true));
			}
		}
		else
		{
			if (homunculus.isActive())
			{
				homunculus.setActive(false);
				player.getHomunculusList().update(homunculus);
				player.getHomunculusList().refreshStats(true);
				player.sendPacket(new ExShowHomunculusList(player));
				player.sendPacket(new ExActivateHomunculusResult(false));
			}
		}
	}
}
