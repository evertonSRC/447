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
package ai.others.Servitors;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * Death Gate AI.
 * @author Sdw, Mobius
 */
public class GateOfUnlimitedSummoning extends Script
{
	// NPCs
	private static final Map<Integer, Integer> DEATH_GATE = new HashMap<>();
	static
	{
		DEATH_GATE.put(14927, 1); // Death Gate
		DEATH_GATE.put(15200, 2); // Death Gate
		DEATH_GATE.put(15201, 3); // Death Gate
		DEATH_GATE.put(15202, 4); // Death Gate
		DEATH_GATE.put(15218, 5); // Death Gate
		DEATH_GATE.put(15219, 6); // Death Gate
		DEATH_GATE.put(15264, 7); // Death Gate
		DEATH_GATE.put(15265, 8); // Death Gate
	}
	
	// Skills
	private static final int GATE_ROOT = 11289;
	private static final int GATE_VORTEX = 11291;
	
	private GateOfUnlimitedSummoning()
	{
		addSpawnId(DEATH_GATE.keySet());
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		final Creature summoner = npc.getSummoner();
		if ((summoner != null) && summoner.isPlayer())
		{
			getTimers().addTimer("SKILL_CAST_SLOW", 1000, npc, null);
			getTimers().addTimer("SKILL_CAST_DAMAGE", 2000, npc, null);
			getTimers().addTimer("END_OF_LIFE", 30000, npc, null);
		}
	}
	
	@Override
	public void onTimerEvent(String event, StatSet params, Npc npc, Player player)
	{
		switch (event)
		{
			case "SKILL_CAST_SLOW":
			{
				final int skillLevel = DEATH_GATE.get(npc.getId());
				if (skillLevel > 0)
				{
					final Skill skill = SkillData.getInstance().getSkill(GATE_ROOT, skillLevel);
					if (skill != null)
					{
						npc.doCast(skill);
					}
				}
				
				getTimers().addTimer("SKILL_CAST_SLOW", 3000, npc, null);
				break;
			}
			case "SKILL_CAST_DAMAGE":
			{
				final Skill skill = SkillData.getInstance().getSkill(GATE_VORTEX, 1);
				if (skill != null)
				{
					npc.doCast(skill);
				}
				
				getTimers().addTimer("SKILL_CAST_DAMAGE", 2000, npc, null);
				break;
			}
			case "END_OF_LIFE":
			{
				getTimers().cancelTimer("SKILL_CAST_SLOW", npc, null);
				getTimers().cancelTimer("SKILL_CAST_DAMAGE", npc, null);
				npc.deleteMe();
				break;
			}
		}
	}
	
	public static void main(String[] args)
	{
		new GateOfUnlimitedSummoning();
	}
}
