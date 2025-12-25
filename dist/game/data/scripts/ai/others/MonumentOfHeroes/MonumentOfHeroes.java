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
package ai.others.MonumentOfHeroes;

import java.util.List;

import org.l2jmobius.gameserver.config.OlympiadConfig;
import org.l2jmobius.gameserver.data.xml.ClassListData;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.olympiad.Hero;
import org.l2jmobius.gameserver.model.olympiad.Olympiad;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ExHeroList;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;

/**
 * Monument of Heroes AI.
 * @author St3eT, Mobius
 */
public class MonumentOfHeroes extends Script
{
	// NPCs
	private static final int[] MONUMENTS =
	{
		31690,
		31769,
		31770,
		31771,
		31772,
	};
	
	// Items
	private static final int HERO_CLOAK = 30372;
	private static final int GLORIOUS_CLOAK = 30373;
	private static final int WINGS_OF_DESTINY_CIRCLET = 6842;
	private static final int HERO_RUNE = 48551;
	private static final int[] WEAPONS =
	{
		30392, // Infinity Shaper (dagger)
		30393, // Infinity Cutter (1-H Sword)
		30394, // Infinity Slasher (2-H Sword)
		30395, // Infinity Avenger (1-H Blunt Weapon)
		30396, // Infinity Fighter (Fist)
		30397, // Infinity Stormer (Polearm)
		30398, // Infinity Thrower (bow)
		30399, // Infinity Shooter (crossbow)
		30400, // Infinity Buster (magic sword)
		30401, // Infinity Caster (magic blunt weapon)
		30402, // Infinity Retributer (two-handed magic blunt weapon)
		30403, // Infinity Dual Sword (Dual Swords)
		30404, // Infinity Dual Dagger (Dual Daggers)
		30405, // Infinity Dual Blunt Weapon (Dual Blunt Weapon)
	};
	private static final int[] WEAPONS_LEGEND =
	{
		48554, // Legend's Infinity Shaper (dagger)
		48555, // Legend's Infinity Cutter (1-H Sword)
		48556, // Legend's Infinity Slasher (2-H Sword)
		48557, // Legend's Infinity Avenger (1-H Blunt Weapon)
		48558, // Legend's Infinity Fighter (Fist)
		48559, // Legend's Infinity Stormer (Polearm)
		48560, // Legend's Infinity Thrower (bow)
		48561, // Legend's Infinity Guardian (crossbow)
		48562, // Legend's Infinity Buster (magic sword)
		48563, // Legend's Infinity Caster (magic blunt weapon)
		48564, // Legend's Infinity Retributer (two-handed magic blunt weapon)
		48565, // Legend's Infinity Dual Sword (Dual Swords)
		48566, // Legend's Infinity Dual Dagger (Dual Daggers)
		48567, // Legend's Infinity Dual Blunt Weapon (Dual Blunt Weapon)
	};
	
	private MonumentOfHeroes()
	{
		if (OlympiadConfig.OLYMPIAD_ENABLED)
		{
			addStartNpc(MONUMENTS);
			addFirstTalkId(MONUMENTS);
			addTalkId(MONUMENTS);
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "MonumentOfHeroes-reward.html":
			{
				htmltext = event;
				break;
			}
			case "index":
			{
				htmltext = onFirstTalk(npc, player);
				break;
			}
			case "heroList":
			{
				player.sendPacket(new ExHeroList());
				break;
			}
			case "receiveCloak":
			{
				final int olympiadRank = getOlympiadRank(player);
				if (olympiadRank == 1)
				{
					if (!hasAtLeastOneQuestItem(player, HERO_CLOAK, GLORIOUS_CLOAK))
					{
						if (player.isInventoryUnder80(false))
						{
							giveItems(player, HERO_CLOAK, 1);
						}
						else
						{
							player.sendPacket(SystemMessageId.NOT_ENOUGH_SPACE_IN_THE_INVENTORY_UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
						}
					}
					else
					{
						htmltext = "MonumentOfHeroes-cloakHave.html";
					}
				}
				else if ((olympiadRank == 2) || (olympiadRank == 3))
				{
					if (!hasAtLeastOneQuestItem(player, HERO_CLOAK, GLORIOUS_CLOAK))
					{
						if (player.isInventoryUnder80(false))
						{
							giveItems(player, GLORIOUS_CLOAK, 1);
						}
						else
						{
							player.sendPacket(SystemMessageId.NOT_ENOUGH_SPACE_IN_THE_INVENTORY_UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
						}
					}
					else
					{
						htmltext = "MonumentOfHeroes-cloakHave.html";
					}
				}
				else
				{
					htmltext = "MonumentOfHeroes-cloakNo.html";
				}
				break;
			}
			case "heroWeapon":
			{
				if (player.isLegend())
				{
					if (player.isInventoryUnder80(false))
					{
						htmltext = hasAtLeastOneQuestItem(player, WEAPONS_LEGEND) || hasAtLeastOneQuestItem(player, WEAPONS) ? "MonumentOfHeroes-weaponHave.html" : "MonumentOfHeroes-weaponListLegend.html";
					}
					else
					{
						player.sendPacket(SystemMessageId.NOT_ENOUGH_SPACE_IN_THE_INVENTORY_UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
					}
				}
				else if (Hero.getInstance().isHero(player.getObjectId()))
				{
					if (player.isInventoryUnder80(false))
					{
						htmltext = hasAtLeastOneQuestItem(player, WEAPONS) ? "MonumentOfHeroes-weaponHave.html" : "MonumentOfHeroes-weaponList.html";
					}
					else
					{
						player.sendPacket(SystemMessageId.NOT_ENOUGH_SPACE_IN_THE_INVENTORY_UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
					}
				}
				else
				{
					htmltext = "MonumentOfHeroes-weaponNo.html";
				}
				break;
			}
			case "heroCirclet":
			{
				if (Hero.getInstance().isHero(player.getObjectId()))
				{
					if (hasQuestItems(player, WINGS_OF_DESTINY_CIRCLET))
					{
						htmltext = "MonumentOfHeroes-circletHave.html";
					}
					else if (!player.isInventoryUnder80(false))
					{
						player.sendPacket(SystemMessageId.NOT_ENOUGH_SPACE_IN_THE_INVENTORY_UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
					}
					else
					{
						giveItems(player, WINGS_OF_DESTINY_CIRCLET, 1);
					}
				}
				else
				{
					htmltext = "MonumentOfHeroes-circletNo.html";
				}
				break;
			}
			case "heroRune":
			{
				if (Hero.getInstance().isHero(player.getObjectId()))
				{
					if (hasQuestItems(player, HERO_RUNE))
					{
						htmltext = "MonumentOfHeroes-runeHave.html";
					}
					else if (!player.isInventoryUnder80(false))
					{
						player.sendPacket(SystemMessageId.NOT_ENOUGH_SPACE_IN_THE_INVENTORY_UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
					}
					else
					{
						giveItems(player, HERO_RUNE, 1);
					}
				}
				else
				{
					htmltext = "MonumentOfHeroes-runeNo.html";
				}
				break;
			}
			case "heroCertification":
			{
				if (Hero.getInstance().isUnclaimedHero(player.getObjectId()))
				{
					htmltext = "MonumentOfHeroes-heroCertification.html";
				}
				else if (Hero.getInstance().isHero(player.getObjectId()))
				{
					htmltext = "MonumentOfHeroes-heroCertificationAlready.html";
				}
				else
				{
					htmltext = "MonumentOfHeroes-heroCertificationNo.html";
				}
				break;
			}
			case "heroConfirm":
			{
				if (Hero.getInstance().isUnclaimedHero(player.getObjectId()))
				{
					if (!player.isSubClassActive())
					{
						if (player.getLevel() >= 85)
						{
							Hero.getInstance().claimHero(player);
							showOnScreenMsg(player, "Congratulations, " + player.getName() + "! You have become the Hero of " + ClassListData.getInstance().getClass(player.getPlayerClass()).getClassName(), 10000);
							player.broadcastPacket(new PlaySound(1, "ns01_f", 0, 0, 0, 0, 0));
							htmltext = "MonumentOfHeroes-heroCertificationsDone.html";
						}
						else
						{
							htmltext = "MonumentOfHeroes-heroCertificationLevel.html";
						}
					}
					else
					{
						htmltext = "MonumentOfHeroes-heroCertificationSub.html";
					}
				}
				else
				{
					htmltext = "MonumentOfHeroes-heroCertificationNo.html";
				}
				break;
			}
			case "give_30392": // Infinity Shaper (dagger)
			case "give_30393": // Infinity Cutter (1-H Sword)
			case "give_30394": // Infinity Slasher (2-H Sword)
			case "give_30395": // Infinity Avenger (1-H Blunt Weapon)
			case "give_30396": // Infinity Fighter (Fist)
			case "give_30397": // Infinity Stormer (Polearm)
			case "give_30398": // Infinity Thrower (bow)
			case "give_30399": // Infinity Shooter (crossbow)
			case "give_30400": // Infinity Buster (magic sword)
			case "give_30401": // Infinity Caster (magic blunt weapon)
			case "give_30402": // Infinity Retributer (two-handed magic blunt weapon)
			case "give_30403": // Infinity Dual Sword (Dual Swords)
			case "give_30404": // Infinity Dual Dagger (Dual Daggers)
			case "give_30405": // Infinity Dual Blunt Weapon (Dual Blunt Weapon)
			{
				final int weaponId = Integer.parseInt(event.replace("give_", ""));
				giveItems(player, weaponId, 1);
				break;
			}
			case "give_48554": // Infinity Shaper (dagger)
			case "give_48555": // Infinity Cutter (1-H Sword)
			case "give_48556": // Infinity Slasher (2-H Sword)
			case "give_48557": // Infinity Avenger (1-H Blunt Weapon)
			case "give_48558": // Infinity Fighter (Fist)
			case "give_48559": // Infinity Stormer (Polearm)
			case "give_48560": // Infinity Thrower (bow)
			case "give_48561": // Infinity Shooter (crossbow)
			case "give_48562": // Infinity Buster (magic sword)
			case "give_48563": // Infinity Caster (magic blunt weapon)
			case "give_48564": // Infinity Retributer (two-handed magic blunt weapon)
			case "give_48565": // Infinity Dual Sword (Dual Swords)
			case "give_48566": // Infinity Dual Dagger (Dual Daggers)
			case "give_48567": // Infinity Dual Blunt Weapon (Dual Blunt Weapon)
			{
				final int weaponId = Integer.parseInt(event.replace("give_", ""));
				giveItems(player, weaponId, 1);
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return player.getNobleLevel() > 0 ? "MonumentOfHeroes-noblesse.html" : "MonumentOfHeroes-noNoblesse.html";
	}
	
	private int getOlympiadRank(Player player)
	{
		final List<String> names = Olympiad.getInstance().getClassLeaderBoard(player.getPlayerClass().getId());
		try
		{
			for (int i = 1; i <= 3; i++)
			{
				if (names.get(i - 1).equals(player.getName()))
				{
					return i;
				}
			}
		}
		catch (Exception e)
		{
			return -1;
		}
		
		return -1;
	}
	
	public static void main(String[] args)
	{
		new MonumentOfHeroes();
	}
}
