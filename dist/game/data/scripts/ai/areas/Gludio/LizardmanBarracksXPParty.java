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
package ai.areas.Gludio;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.script.Script;

/**
 * @author Notorion
 */
public class LizardmanBarracksXPParty extends Script
{
	// NPCs
	private static final int[] MONSTER_IDS =
	{
		23834,
		23835,
		23836,
		23837,
		23838,
		23839
	};
	
	// Distance radius for XP bonus Party.
	private static final int BONUS_RADIUS = 1500;
	
	// Maximum XP Bonus (Level 126).
	private static final double MAX_BONUS_PERCENTAGE = 0.25; // Bonus 25%.
	
	private LizardmanBarracksXPParty()
	{
		addKillId(MONSTER_IDS);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Party party = killer.getParty();
		if (party != null)
		{
			for (Player member : party.getMembers())
			{
				if (killer.isInsideRadius3D(member, BONUS_RADIUS) && (member.getLevel() >= 117) && (member.getLevel() <= 131))
				{
					// Add total experience to the player after 1 second.
					final double bonusPercentage = calculateBonusPercentage(member.getLevel());
					final long bonusXp = (long) (npc.getExpReward(member.getLevel()) * bonusPercentage);
					ThreadPool.schedule(() -> member.addExpAndSp(bonusXp, 0), 1000);
				}
			}
		}
	}
	
	private double calculateBonusPercentage(int level)
	{
		if ((level < 117) || (level > 131))
		{
			return 0; // No bonus for out of range levels.
		}
		
		// Sets the percentage proportional to the level.
		switch (level)
		{
			case 117:
			{
				return MAX_BONUS_PERCENTAGE * 0.1;
			}
			case 118:
			case 119:
			case 120:
			{
				return MAX_BONUS_PERCENTAGE * 0.2;
			}
			case 121:
			{
				return MAX_BONUS_PERCENTAGE * 0.4;
			}
			case 122:
			{
				return MAX_BONUS_PERCENTAGE * 0.6;
			}
			case 123:
			{
				return MAX_BONUS_PERCENTAGE * 0.97;
			}
			case 124:
			case 125:
			case 126:
			case 127:
			case 128:
			case 129:
			case 130:
			case 131:
			{
				return MAX_BONUS_PERCENTAGE;
			}
			default:
			{
				return 0;
			}
		}
	}
	
	public static void main(String[] args)
	{
		new LizardmanBarracksXPParty();
	}
}
