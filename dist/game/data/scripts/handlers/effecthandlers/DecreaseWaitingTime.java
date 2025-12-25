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
package handlers.effecthandlers;

import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusBirthInfo;

/**
 * @author CostyKiller
 */
public class DecreaseWaitingTime extends AbstractEffect
{
	private final long _time;
	
	public DecreaseWaitingTime(StatSet params)
	{
		_time = params.getLong("time", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		final Player player = effected.asPlayer();
		if (player == null)
		{
			return;
		}
		
		final long currentTime = System.currentTimeMillis();
		long creationTime = player.getVariables().getLong(PlayerVariables.HOMUNCULUS_CREATION_TIME, 0);
		final long waitTime = 0; // 86400 = 24 Hours
		if (creationTime == 0)
		{
			player.getInventory().addItem(ItemProcessType.REFUND, item.getId(), 1, player, player);
			player.sendMessage("You don't have any Homunculus in progress.");
		}
		else if (((currentTime / 1000) - (creationTime / 1000)) >= waitTime)
		{
			player.getInventory().addItem(ItemProcessType.REFUND, item.getId(), 1, player, player);
			player.sendMessage("You cannot decrease the waiting time anymore.");
		}
		else if (((currentTime / 1000) - (creationTime / 1000)) < waitTime)
		{
			player.getVariables().set(PlayerVariables.HOMUNCULUS_CREATION_TIME, creationTime - (_time));
			player.sendPacket(new ExShowHomunculusBirthInfo(player));
		}
		else
		{
			player.sendMessage("You cannot use this item yet.");
		}
	}
}
