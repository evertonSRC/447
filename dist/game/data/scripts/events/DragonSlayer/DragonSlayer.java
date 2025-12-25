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
package events.DragonSlayer;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.script.LongTimeEvent;

/**
 * Dragon Slayer event AI.
 * @author CostyKiller
 */
public class DragonSlayer extends LongTimeEvent
{
	// NPC
	private static final int LUNA = 34419;
	
	// Items
	private static final int MYSTERIOUS_DRAGON_SLAYER = 48494;
	private static final ItemHolder REWARD_7_ID_AND_QTY = new ItemHolder(29759, 2); // Wondrous Shard
	private static final ItemHolder REWARD_8_ID_AND_QTY = new ItemHolder(36515, 1); // Elcyum
	private static final ItemHolder REWARD_9_ID_AND_QTY = new ItemHolder(48910, 1); // Shillien's Soul Crystal Box
	private static final ItemHolder REWARD_10_ID_AND_QTY = new ItemHolder(80636, 2); // Artifact Crystal
	private static final ItemHolder REWARD_11_ID_AND_QTY = new ItemHolder(80449, 1); // Royal Black Save Ticket
	private static final ItemHolder REWARD_12_ID_AND_QTY = new ItemHolder(29469, 1); // Shiny Jewel Energy
	private static final ItemHolder REWARD_13_ID_AND_QTY = new ItemHolder(28595, 1); // Scroll: Enchant Ancient Cloak
	private static final ItemHolder REWARD_14_ID_AND_QTY = new ItemHolder(80932, 1); // Greater Zodiac Agathion's Book of Growth
	private static final ItemHolder REWARD_15_ID_AND_QTY = new ItemHolder(81770, 1); // Shining Energy of Protection
	private static final ItemHolder REWARD_16_ID_AND_QTY = new ItemHolder(81135, 1); // Dragon Weapon Augmenting Stone
	private static final ItemHolder REWARD_17_ID_AND_QTY = new ItemHolder(28766, 1); // Stable Scroll: Enchant Legendary Cloak
	private static final ItemHolder REWARD_18_ID_AND_QTY = new ItemHolder(80931, 1); // Greater Zodiac Agathion Cube
	private static final ItemHolder REWARD_19_ID_AND_QTY = new ItemHolder(47821, 1); // Sayha's Talisman Pack Lv. 10
	private static final ItemHolder REWARD_20_ID_AND_QTY = new ItemHolder(48839, 1); // Ultimate Luxurious Jewelry Box Lv. 5
	private static final ItemHolder REWARD_21_ID_AND_QTY = new ItemHolder(48667, 1); // Mid-grade Dragon Weapon Pack
	private static final ItemHolder REWARD_22_ID_AND_QTY = new ItemHolder(80663, 1); // High-grade Dragon Weapon Pack
	private static final ItemHolder REWARD_23_ID_AND_QTY = new ItemHolder(29907, 1); // Top-grade Dragon Weapon Pack
	
	private DragonSlayer()
	{
		addStartNpc(LUNA);
		addFirstTalkId(LUNA);
		addTalkId(LUNA);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		if (event.equals("receive_reward"))
		{
			final int enchantLevel = getEnchantLevel(player, MYSTERIOUS_DRAGON_SLAYER);
			if ((getItemEquipped(player, Inventory.PAPERDOLL_RHAND) == MYSTERIOUS_DRAGON_SLAYER) && (enchantLevel > 6))
			{
				takeItems(player, MYSTERIOUS_DRAGON_SLAYER, 1);
				switch (enchantLevel)
				{
					case 7:
					{
						giveItems(player, REWARD_7_ID_AND_QTY);
						break;
					}
					case 8:
					{
						giveItems(player, REWARD_8_ID_AND_QTY);
						break;
					}
					case 9:
					{
						giveItems(player, REWARD_9_ID_AND_QTY);
						break;
					}
					case 10:
					{
						giveItems(player, REWARD_10_ID_AND_QTY);
						break;
					}
					case 11:
					{
						giveItems(player, REWARD_11_ID_AND_QTY);
						break;
					}
					case 12:
					{
						giveItems(player, REWARD_12_ID_AND_QTY);
						break;
					}
					case 13:
					{
						giveItems(player, REWARD_13_ID_AND_QTY);
						break;
					}
					case 14:
					{
						giveItems(player, REWARD_14_ID_AND_QTY);
						break;
					}
					case 15:
					{
						giveItems(player, REWARD_15_ID_AND_QTY);
						break;
					}
					case 16:
					{
						giveItems(player, REWARD_16_ID_AND_QTY);
						break;
					}
					case 17:
					{
						giveItems(player, REWARD_17_ID_AND_QTY);
						break;
					}
					case 18:
					{
						giveItems(player, REWARD_18_ID_AND_QTY);
						break;
					}
					case 19:
					{
						giveItems(player, REWARD_19_ID_AND_QTY);
						break;
					}
					case 20:
					{
						giveItems(player, REWARD_20_ID_AND_QTY);
						break;
					}
					case 21:
					{
						giveItems(player, REWARD_21_ID_AND_QTY);
						break;
					}
					case 22:
					{
						giveItems(player, REWARD_22_ID_AND_QTY);
						break;
					}
					case 23:
					{
						giveItems(player, REWARD_23_ID_AND_QTY);
						break;
					}
					default:
					{
						if (getEnchantLevel(player, MYSTERIOUS_DRAGON_SLAYER) > 23)
						{
							giveItems(player, REWARD_23_ID_AND_QTY);
						}
						break;
					}
				}
				
				htmltext = "34419-rewardok.htm";
			}
			else
			{
				htmltext = "34419-rewardnosword.htm";
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
		new DragonSlayer();
	}
}
