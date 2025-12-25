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
package ai.others.AdventurersGuide;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;

/**
 * Adventurers Guide AI.
 * @author St3eT, Mobius
 */
public class AdventurersGuide extends Script
{
	// NPC
	private static final int[] ADVENTURERS_GUIDE =
	{
		32327,
		33950,
	};
	
	// Items
	private static final int ADENA = 57;
	private static final int GEMSTONE_R = 19440;
	
	// Skills
	private static final SkillHolder BLESS_PROTECTION = new SkillHolder(5182, 1); // Blessing of Protection
	private static final SkillHolder FANTASIA = new SkillHolder(32840, 1); // Fantasia Harmony - Adventurer
	private static final SkillHolder[] GROUP_BUFFS =
	{
		new SkillHolder(34243, 1), // Musician's Melody (Adventurer)
		new SkillHolder(34254, 1), // Sonate Performance (Adventurer)
	};
	private static final SkillHolder[] DONATE_BUFFS =
	{
		new SkillHolder(34243, 3), // Musician's Melody (Adventurer)
		new SkillHolder(34254, 1), // Sonate Performance (Adventurer)
	};
	
	// Misc
	private static final int MAX_LEVEL_BUFFS = 99;
	private static final int MIN_LEVEL_PROTECTION = 40;
	
	private AdventurersGuide()
	{
		addStartNpc(ADVENTURERS_GUIDE);
		addTalkId(ADVENTURERS_GUIDE);
		addFirstTalkId(ADVENTURERS_GUIDE);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "guide-01.html":
			case "guide-02.html":
			case "guide-03.html":
			case "guide-04.html":
			case "guide-05.html":
			case "guide-06.html":
			case "guide-07.html":
			case "guide-08.html":
			{
				htmltext = event;
				break;
			}
			case "index":
			{
				htmltext = npc.getId() + ".html";
				break;
			}
			case "weakenBreath":
			{
				if (player.getShilensBreathDebuffLevel() < 3)
				{
					htmltext = "guide-noBreath.html";
					break;
				}
				
				player.setShilensBreathDebuffLevel(2);
				htmltext = "guide-cleanedBreath.html";
				break;
			}
			case "fantasia":
			{
				if (player.getLevel() > MAX_LEVEL_BUFFS)
				{
					return "guide-noBuffs.html";
				}
				
				for (SkillHolder holder : GROUP_BUFFS)
				{
					SkillCaster.triggerCast(npc, player, holder.getSkill());
				}
				
				htmltext = applyBuffs(npc, player, FANTASIA.getSkill());
				break;
			}
			case "fantasia_donate_adena":
			{
				if (getQuestItemsCount(player, ADENA) >= 3000000)
				{
					takeItems(player, ADENA, 3000000);
					for (SkillHolder holder : DONATE_BUFFS)
					{
						SkillCaster.triggerCast(npc, player, holder.getSkill());
					}
					
					htmltext = applyBuffs(npc, player, FANTASIA.getSkill());
				}
				else
				{
					htmltext = "guide-noItems.html";
				}
				break;
			}
			case "fantasia_donate_gemstones":
			{
				if (getQuestItemsCount(player, GEMSTONE_R) >= 5)
				{
					takeItems(player, GEMSTONE_R, 5);
					for (SkillHolder holder : DONATE_BUFFS)
					{
						SkillCaster.triggerCast(npc, player, holder.getSkill());
					}
					
					htmltext = applyBuffs(npc, player, FANTASIA.getSkill());
				}
				else
				{
					htmltext = "guide-noItems.html";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	private String applyBuffs(Npc npc, Player player, Skill skill)
	{
		for (SkillHolder holder : GROUP_BUFFS)
		{
			SkillCaster.triggerCast(npc, player, holder.getSkill());
		}
		
		SkillCaster.triggerCast(npc, player, skill);
		if ((player.getLevel() < MIN_LEVEL_PROTECTION) && (player.getPlayerClass().level() <= 1))
		{
			SkillCaster.triggerCast(npc, player, BLESS_PROTECTION.getSkill());
		}
		
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + ".html";
	}
	
	public static void main(String[] args)
	{
		new AdventurersGuide();
	}
}
