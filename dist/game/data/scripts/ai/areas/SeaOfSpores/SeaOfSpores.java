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

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.util.ArrayUtil;

/**
 * @author NviX
 */
public class SeaOfSpores extends Script
{
	// Mobs
	private static final int[] GROUP_MOBS =
	{
		24227, // Keros
		24228, // Falena
		24229, // Atrofa
		24230, // Nuba
		24231 // Torfedo
	};
	private static final int[] SOLO_MOBS =
	{
		24234, // Lesatanas
		24235, // Arbor
		24236, // Tergus
		24237, // Skeletus
		24238, // Atrofine
	};
	
	// Special Mobs
	private static final int ARANEA = 24226;
	private static final int ARIMA = 24232;
	private static final int HARANE = 24233;
	private static final int ARIMUS = 24239;
	
	// Skills
	private static final SkillHolder ABSORB_ENERGY1 = new SkillHolder(32483, 1);
	private static final SkillHolder ABSORB_ENERGY2 = new SkillHolder(32481, 1);
	private static final SkillHolder REFINED_ENERGY = new SkillHolder(32485, 1);
	
	private SeaOfSpores()
	{
		addAttackId(ARIMA, ARIMUS);
		addKillId(SOLO_MOBS);
		addKillId(GROUP_MOBS);
		addKillId(ARIMA, ARIMUS);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if ((npc.getCurrentHp() <= (npc.getMaxHp() * 0.7)) && !npc.isCastingNow())
		{
			if (npc.getId() == ARIMA)
			{
				npc.doCast(ABSORB_ENERGY2.getSkill());
			}
			else if (npc.getId() == ARIMUS)
			{
				npc.doCast(ABSORB_ENERGY1.getSkill());
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (ArrayUtil.contains(SOLO_MOBS, npc.getId()) && (getRandom(1000) < 2))
		{
			addSpawn(ARIMUS, npc, false, 300000);
		}
		else if (ArrayUtil.contains(GROUP_MOBS, npc.getId()) && (getRandom(1000) < 2))
		{
			addSpawn(ARIMA, npc, false, 300000);
		}
		else if (npc.getId() == ARIMA)
		{
			List<Player> members = new ArrayList<>();
			if (killer.getParty() != null)
			{
				members = killer.getParty().getMembers();
			}
			else
			{
				members.add(killer);
			}
			
			for (Player member : members)
			{
				member.doCast(REFINED_ENERGY.getSkill());
			}
			
			if (getRandom(1000) < 2)
			{
				addSpawn(ARANEA, npc, false, 300000);
				addSpawn(HARANE, npc, false, 300000);
			}
		}
		else if (npc.getId() == ARIMUS)
		{
			List<Player> members = new ArrayList<>();
			if (killer.getParty() != null)
			{
				members = killer.getParty().getMembers();
			}
			else
			{
				members.add(killer);
			}
			
			for (Player member : members)
			{
				member.doCast(REFINED_ENERGY.getSkill());
			}
		}
	}
	
	public static void main(String[] args)
	{
		new SeaOfSpores();
	}
}
