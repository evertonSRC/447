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
package custom.FakePlayers;

import org.l2jmobius.gameserver.config.custom.FakePlayersConfig;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.util.ArrayUtil;

/**
 * Town Fake Player walkers that receive buffs from Adventurer NPC.
 * @author Mobius
 */
public class RecieveAdventurerBuffs extends Script
{
	// NPCs
	private static final int[] ADVENTURERS_GUIDE =
	{
		32327,
		33950,
	};
	private static final int[] FAKE_PLAYER_IDS =
	{
		80000
	};
	
	// Skills
	private static final SkillHolder FANTASIA = new SkillHolder(32840, 1); // Fantasia Harmony - Adventurer
	private static final SkillHolder[] GROUP_BUFFS =
	{
		new SkillHolder(34254, 1), // Sonate Performance (Adventurer)
		new SkillHolder(34243, 1), // Musician's Melody (Adventurer)
	};
	
	private RecieveAdventurerBuffs()
	{
		if (FakePlayersConfig.FAKE_PLAYERS_ENABLED)
		{
			addSpawnId(FAKE_PLAYER_IDS);
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.startsWith("AUTOBUFF") && (npc != null) && !npc.isDead())
		{
			if (!npc.isMoving())
			{
				for (Npc nearby : World.getInstance().getVisibleObjectsInRange(npc, Npc.class, 100))
				{
					if (ArrayUtil.contains(ADVENTURERS_GUIDE, nearby.getId()))
					{
						for (SkillHolder holder : GROUP_BUFFS)
						{
							SkillCaster.triggerCast(nearby, npc, holder.getSkill());
						}
						
						if (npc.getTemplate().getFakePlayerInfo().getPlayerClass().isMage())
						{
							SkillCaster.triggerCast(nearby, npc, FANTASIA.getSkill()); // TODO: Merge events.
						}
						else
						{
							SkillCaster.triggerCast(nearby, npc, FANTASIA.getSkill()); // TODO: Merge events.
						}
						break;
					}
				}
			}
			
			startQuestTimer("AUTOBUFF", 30000, npc, null);
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		startQuestTimer("AUTOBUFF", 1000, npc, null);
	}
	
	public static void main(String[] args)
	{
		new RecieveAdventurerBuffs();
	}
}
