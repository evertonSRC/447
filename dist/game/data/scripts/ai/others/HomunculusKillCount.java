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
package ai.others;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.actor.npc.OnAttackableKill;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusPointInfo;

/**
 * @author CostyKiller
 */
public class HomunculusKillCount extends Script
{
	private static final int LEVEL_DIFFERENCE = 9;
	
	@RegisterEvent(EventType.ON_ATTACKABLE_KILL)
	@RegisterType(ListenerRegisterType.GLOBAL_MONSTERS)
	public void onAttackableKill(OnAttackableKill event)
	{
		final Creature creature = event.getTarget();
		if ((creature != null) && creature.isMonster())
		{
			final Player player = event.getAttacker().asPlayer();
			if ((player != null) && (Math.abs(player.getLevel() - creature.getLevel()) <= LEVEL_DIFFERENCE))
			{
				if (player.isInParty())
				{
					final Party party = player.getParty();
					for (Player member : party.getMembers())
					{
						if (member.isInsideRadius3D(creature, PlayerConfig.ALT_PARTY_RANGE))
						{
							final int killedMobs = member.getVariables().getInt(PlayerVariables.HOMUNCULUS_KILLED_MOBS, 0);
							if (killedMobs < 500)
							{
								member.getVariables().set(PlayerVariables.HOMUNCULUS_KILLED_MOBS, killedMobs + 1);
								member.sendPacket(new ExHomunculusPointInfo(member));
							}
						}
					}
				}
				else
				{
					final int killedMobs = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_KILLED_MOBS, 0);
					if (killedMobs < 500)
					{
						player.getVariables().set(PlayerVariables.HOMUNCULUS_KILLED_MOBS, killedMobs + 1);
						player.sendPacket(new ExHomunculusPointInfo(player));
					}
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new HomunculusKillCount();
	}
}
