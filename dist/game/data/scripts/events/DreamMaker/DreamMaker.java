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
package events.DreamMaker;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.LongTimeEvent;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;

/**
 * Dream Maker event AI.
 * @author CostyKiller
 */
public class DreamMaker extends LongTimeEvent
{
	// NPC
	private static final int MOO_COW = 34590;
	
	// Items
	private static final int LUCKY_POUCH_GROWTH = 81934; // Lucky Pouch Growth
	private static final int LUCKY_POUCH_PROSPERITY = 81935; // Lucky Pouch Prosperity
	private static final int LUCKY_POUCH_HARMONY = 81936; // Lucky Pouch Harmony
	
	// Buffs
	private static final SkillHolder[] BUFFS =
	{
		new SkillHolder(34032, 1), // Unchallenged Supremacy
		new SkillHolder(34032, 2), // Unchallenged Supremacy
		new SkillHolder(34033, 1), // Red Alert
		new SkillHolder(34033, 2), // Red Alert
		new SkillHolder(34034, 1), // Step Up
		new SkillHolder(34034, 2), // Step Up
		new SkillHolder(34035, 1), // Luck's Kindness
		new SkillHolder(34035, 2), // Luck's Kindness
	};
	
	private DreamMaker()
	{
		addStartNpc(MOO_COW);
		addFirstTalkId(MOO_COW);
		addTalkId(MOO_COW);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		switch (event)
		{
			case "get_gift":
			{
				if (!player.getVariables().getBoolean("DREAM_MAKER_GIFT_RECEIVED", false))
				{
					giveItems(player, getRandom(LUCKY_POUCH_GROWTH, LUCKY_POUCH_HARMONY), 1);
					player.getVariables().set("DREAM_MAKER_GIFT_RECEIVED", true);
					htmltext = "34590-successful.htm";
				}
				else
				{
					htmltext = "34590-already-received.htm";
				}
				break;
			}
			case "g_pouch":
			{
				if (player.getLevel() < 105)
				{
					htmltext = "34590-gift-no-level.htm";
				}
				else if (player.getVariables().getBoolean("DREAM_MAKER_GIFT_EXCHANGED", false))
				{
					htmltext = "34590-already-exchanged.htm";
				}
				else if (hasAtLeastOneQuestItem(player, LUCKY_POUCH_PROSPERITY, LUCKY_POUCH_HARMONY))
				{
					if (hasQuestItems(player, LUCKY_POUCH_PROSPERITY))
					{
						takeItems(player, LUCKY_POUCH_PROSPERITY, 1);
					}
					else if (hasQuestItems(player, LUCKY_POUCH_HARMONY))
					{
						takeItems(player, LUCKY_POUCH_HARMONY, 1);
					}
					
					giveItems(player, LUCKY_POUCH_GROWTH, 1);
					player.getVariables().set("DREAM_MAKER_GIFT_EXCHANGED", true);
					htmltext = "34590-successful.htm";
				}
				else
				{
					htmltext = "34590-no-pouch.htm";
				}
				break;
			}
			case "p_pouch":
			{
				if (player.getLevel() < 105)
				{
					htmltext = "34590-gift-no-level.htm";
				}
				else if (player.getVariables().getBoolean("DREAM_MAKER_GIFT_EXCHANGED", false))
				{
					htmltext = "34590-pouch-exchanged.htm";
				}
				else if (hasAtLeastOneQuestItem(player, LUCKY_POUCH_GROWTH, LUCKY_POUCH_HARMONY))
				{
					if (hasQuestItems(player, LUCKY_POUCH_GROWTH))
					{
						takeItems(player, LUCKY_POUCH_GROWTH, 1);
					}
					else if (hasQuestItems(player, LUCKY_POUCH_HARMONY))
					{
						takeItems(player, LUCKY_POUCH_HARMONY, 1);
					}
					
					giveItems(player, LUCKY_POUCH_PROSPERITY, 1);
					player.getVariables().set("DREAM_MAKER_GIFT_EXCHANGED", true);
					htmltext = "34590-successful.htm";
				}
				else
				{
					htmltext = "34590-no-pouch.htm";
				}
				break;
			}
			case "h_pouch":
			{
				if (player.getLevel() < 105)
				{
					htmltext = "34590-gift-no-level.htm";
				}
				else if (player.getVariables().getBoolean("DREAM_MAKER_GIFT_EXCHANGED", false))
				{
					htmltext = "34590-pouch-exchanged.htm";
				}
				else if (hasAtLeastOneQuestItem(player, LUCKY_POUCH_GROWTH, LUCKY_POUCH_PROSPERITY))
				{
					if (hasQuestItems(player, LUCKY_POUCH_GROWTH))
					{
						takeItems(player, LUCKY_POUCH_GROWTH, 1);
					}
					else if (hasQuestItems(player, LUCKY_POUCH_PROSPERITY))
					{
						takeItems(player, LUCKY_POUCH_PROSPERITY, 1);
					}
					
					giveItems(player, LUCKY_POUCH_HARMONY, 1);
					player.getVariables().set("DREAM_MAKER_GIFT_EXCHANGED", true);
					htmltext = "34590-successful.htm";
				}
				else
				{
					htmltext = "34590-no-pouch.htm";
				}
				break;
			}
			case "get_buff":
			{
				if (player.getLevel() < 105)
				{
					htmltext = "34590-buff-no-level.htm";
				}
				else if ((player.getVariables().getLong("DREAM_MAKER_BUFF_DELAY", 0) > 0) && (player.getVariables().getLong("DREAM_MAKER_BUFF_DELAY", 0) <= (System.currentTimeMillis() + 86400000)))
				{
					htmltext = "34590-buff-already-received.htm";
				}
				else
				{
					SkillCaster.triggerCast(npc, player, getRandomEntry(BUFFS).getSkill());
					player.getVariables().set("DREAM_MAKER_BUFF_DELAY", System.currentTimeMillis());
					htmltext = "34590-buff-received.htm";
				}
				break;
			}
			case "alter_buff":
			{
				if (player.getAdena() < 5000000)
				{
					htmltext = "34590-buff-no-adena.htm";
				}
				else if (player.isAffectedBySkill(BUFFS[0].getSkillId()) || player.isAffectedBySkill(BUFFS[2].getSkillId()) || player.isAffectedBySkill(BUFFS[4].getSkillId()) || player.isAffectedBySkill(BUFFS[6].getSkillId()))
				{
					takeItems(player, 57, 5000000);
					SkillCaster.triggerCast(npc, player, getRandomEntry(BUFFS).getSkill());
					htmltext = "34590-buff-received.htm";
				}
				else
				{
					htmltext = "34590-no-buff.htm";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + ".htm";
	}
	
	public static void main(String[] args)
	{
		new DreamMaker();
	}
}
