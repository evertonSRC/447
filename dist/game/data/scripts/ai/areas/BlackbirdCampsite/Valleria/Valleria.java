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
package ai.areas.BlackbirdCampsite.Valleria;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Faction;
import org.l2jmobius.gameserver.model.script.Script;

/**
 * Valleria AI.
 * @author CostyKiller
 */
public class Valleria extends Script
{
	// NPC
	private static final int VALLERIA = 34435;
	
	// Items
	private static final int MEDAL = 48516; // Medal of Honor
	private static final int GRAND_MEDAL = 48517; // Grand Medal of Honor
	
	// Misc
	private static final int MEDAL_POINTS = 100;
	private static final int GRAND_MEDAL_POINTS = 1000;
	private static final int MIN_LEVEL = 99;
	
	private Valleria()
	{
		addStartNpc(VALLERIA);
		addFirstTalkId(VALLERIA);
		addTalkId(VALLERIA);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34435.html":
			case "34435-01.html":
			case "34435-02.html":
			case "34435-03.html":
			case "34435-04.html":
			{
				htmltext = event;
				break;
			}
			case "medal_for_blackbird":
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "no_level.html";
				}
				else
				{
					if (hasAtLeastOneQuestItem(player, MEDAL))
					{
						takeItems(player, 1, MEDAL);
						player.addFactionPoints(Faction.BLACKBIRD_CLAN, MEDAL_POINTS);
						htmltext = "success.html";
					}
					else
					{
						htmltext = "no_medal.html";
					}
				}
				break;
			}
			case "medal_for_mother":
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "no_level.html";
				}
				else
				{
					if (hasAtLeastOneQuestItem(player, MEDAL))
					{
						takeItems(player, 1, MEDAL);
						player.addFactionPoints(Faction.MOTHER_TREE_GUARDIANS, MEDAL_POINTS);
						htmltext = "success.html";
					}
					else
					{
						htmltext = "no_medal.html";
					}
				}
				break;
			}
			case "medal_for_giant":
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "no_level.html";
				}
				else
				{
					if (hasAtLeastOneQuestItem(player, MEDAL))
					{
						takeItems(player, 1, MEDAL);
						player.addFactionPoints(Faction.GIANT_TRACKERS, MEDAL_POINTS);
						htmltext = "success.html";
					}
					else
					{
						htmltext = "no_medal.html";
					}
				}
				break;
			}
			case "medal_for_unworldly":
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "no_level.html";
				}
				else
				{
					if (hasAtLeastOneQuestItem(player, MEDAL))
					{
						takeItems(player, 1, MEDAL);
						player.addFactionPoints(Faction.UNWORLDLY_VISITORS, MEDAL_POINTS);
						htmltext = "success.html";
					}
					else
					{
						htmltext = "no_medal.html";
					}
				}
				break;
			}
			case "medal_for_kingdom":
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "no_level.html";
				}
				else
				{
					if (hasAtLeastOneQuestItem(player, MEDAL))
					{
						takeItems(player, 1, MEDAL);
						player.addFactionPoints(Faction.KINGDOM_ROYAL_GUARDS, MEDAL_POINTS);
						htmltext = "success.html";
					}
					else
					{
						htmltext = "no_medal.html";
					}
				}
				break;
			}
			case "grand_medal_for_blackbird":
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "no_level.html";
				}
				else
				{
					if (hasAtLeastOneQuestItem(player, GRAND_MEDAL))
					{
						takeItems(player, 1, GRAND_MEDAL);
						player.addFactionPoints(Faction.BLACKBIRD_CLAN, GRAND_MEDAL_POINTS);
						htmltext = "success.html";
					}
					else
					{
						htmltext = "no_medal.html";
					}
				}
				break;
			}
			case "grand_medal_for_mother":
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "no_level.html";
				}
				else
				{
					if (hasAtLeastOneQuestItem(player, GRAND_MEDAL))
					{
						takeItems(player, 1, GRAND_MEDAL);
						player.addFactionPoints(Faction.MOTHER_TREE_GUARDIANS, GRAND_MEDAL_POINTS);
						htmltext = "success.html";
					}
					else
					{
						htmltext = "no_medal.html";
					}
				}
				break;
			}
			case "grand_medal_for_giant":
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "no_level.html";
				}
				else
				{
					if (hasAtLeastOneQuestItem(player, GRAND_MEDAL))
					{
						takeItems(player, 1, GRAND_MEDAL);
						player.addFactionPoints(Faction.GIANT_TRACKERS, GRAND_MEDAL_POINTS);
						htmltext = "success.html";
					}
					else
					{
						htmltext = "no_medal.html";
					}
				}
				break;
			}
			case "grand_medal_for_unworldly":
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "no_level.html";
				}
				else
				{
					if (hasAtLeastOneQuestItem(player, GRAND_MEDAL))
					{
						takeItems(player, 1, GRAND_MEDAL);
						player.addFactionPoints(Faction.UNWORLDLY_VISITORS, GRAND_MEDAL_POINTS);
						htmltext = "success.html";
					}
					else
					{
						htmltext = "no_medal.html";
					}
				}
				break;
			}
			case "grand_medal_for_kingdom":
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "no_level.html";
				}
				else
				{
					if (hasAtLeastOneQuestItem(player, GRAND_MEDAL))
					{
						takeItems(player, 1, GRAND_MEDAL);
						player.addFactionPoints(Faction.KINGDOM_ROYAL_GUARDS, GRAND_MEDAL_POINTS);
						htmltext = "success.html";
					}
					else
					{
						htmltext = "no_medal.html";
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "34435.html";
	}
	
	public static void main(String[] args)
	{
		new Valleria();
	}
}
