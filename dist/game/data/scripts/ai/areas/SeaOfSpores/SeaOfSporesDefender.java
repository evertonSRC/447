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
package ai.areas.SeaOfSpores;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.enums.ChatType;

/**
 * @author Notorion
 */
public class SeaOfSporesDefender extends Script
{
	// NPCs
	private static final int FUNGUS_DEFENDER = 24649;
	private static final int FUNGUS = 24650;
	
	// Misc
	private static final Map<Npc, Long> TALK_COOLDOWN = new ConcurrentHashMap<>();
	private static final long COOLDOWN_TIME = 60000; // 1 minute
	
	private SeaOfSporesDefender()
	{
		addSpawnId(FUNGUS_DEFENDER);
		addAttackId(FUNGUS_DEFENDER, FUNGUS);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		if (npc.getId() == FUNGUS_DEFENDER)
		{
			npc.setInvul(true);
			
			final int count = getRandom(3, 6);
			final int radius = 63;
			for (int i = 0; i < count; i++)
			{
				final double angle = Math.toRadians((360.0 / count) * i);
				final int x = npc.getX() + (int) (radius * Math.cos(angle));
				final int y = npc.getY() + (int) (radius * Math.sin(angle));
				addSpawn(FUNGUS, x, y, npc.getZ(), 0, false, 0);
			}
			
			for (Npc minion : World.getInstance().getVisibleObjectsInRange(npc, Npc.class, 100))
			{
				if (minion.getId() == FUNGUS)
				{
					minion.setInvul(true);
				}
			}
		}
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if ((npc.getId() == FUNGUS_DEFENDER) || (npc.getId() == FUNGUS))
		{
			npc.setCurrentHp(npc.getMaxHp());
			if ((npc.getId() == FUNGUS_DEFENDER) && npc.isInCombat())
			{
				final long currentTime = System.currentTimeMillis();
				final long lastTalkTime = TALK_COOLDOWN.getOrDefault(npc, 0L);
				if ((currentTime - lastTalkTime) >= COOLDOWN_TIME)
				{
					TALK_COOLDOWN.put(npc, currentTime);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_WON_T_KILL_ME_THESE_SPORES_MAKE_ME_IMMORTAL);
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new SeaOfSporesDefender();
	}
}
