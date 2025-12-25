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
package ai.areas.Conquest.Flowers;

import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.holders.conquest.OnConquestFlowerCollect;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * Conquest Flowers AI.<br>
 * Fire Flowers grow only in the central area of Fire Source, while Life Flowers and Power Flowers can be found all over the Fire Area.<br>
 * To collect the Flowers, you will need 1000 HP and 500,000 SP.<br>
 * When you collect the Flowers you might receive the following items with a certain chance: Seed of Fire, Ghost Soul, personal Conquest points, server Conquest points, Fire Source points, Sacred Fire Summon Scroll and Eigis Armor Fragment.<br>
 * When you collect Fire Flowers, you may also receive Divine Flower that is needed to enhance the <Flame Spark> Primordial Fire Source ability.
 * @author CostyKiller
 */
public class Flowers extends Script
{
	// NPCs
	private static final int FIRE_FLOWER = 34655;
	private static final int LIFE_FLOWER = 34656;
	private static final int POWER_FLOWER = 34657;
	
	// Gather flower requirements
	private static final int REQUIRED_HP = 1000;
	private static final int REQUIRED_SP = 500000;
	
	// Rewards
	private static final int PERSONAL_POINTS_AMOUNT = 267;
	private static final int SERVER_POINTS_AMOUNT = 500;
	private static final int SEED_OF_FIRE = 82616;
	private static final int SEALED_GHOST_SOUL_ORB = 82610;
	private static final int SEALED_FIRE_SOURCE = 82658;
	private static final int SACRED_FIRE_SUMMON_SCROLL = 82614;
	private static final int EIGIS_ARMOR_FRAGMENT = 82083;
	private static final int DIVINE_FIRE = 82615;
	private static final int DIVINE_FIRE_CHANCE = 15; // 15%
	
	private Flowers()
	{
		addStartNpc(FIRE_FLOWER, LIFE_FLOWER, POWER_FLOWER);
		addFirstTalkId(FIRE_FLOWER, LIFE_FLOWER, POWER_FLOWER);
		addTalkId(FIRE_FLOWER, LIFE_FLOWER, POWER_FLOWER);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34655.html":
			case "34656.html":
			case "34657.html":
			case "34655-01.html":
			case "34656-01.html":
			case "34657-01.html":
			{
				htmltext = event;
				break;
			}
			case "collectFlower":
			{
				if ((player.getCurrentHp() < REQUIRED_HP) && (player.getSp() < REQUIRED_SP))
				{
					player.sendMessage("You can't collect this flower, the requirements are not met.");
				}
				else
				{
					player.setCurrentHp(player.getCurrentHp() - REQUIRED_HP);
					player.setSp(player.getSp() - REQUIRED_SP);
					
					// Messages
					SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
					sm.addLong(REQUIRED_SP);
					SystemMessage sm2 = new SystemMessage(SystemMessageId.C1_HAS_DRAINED_YOU_OF_S2_HP);
					sm2.addString(npc.getName());
					sm2.addLong(REQUIRED_HP);
					player.sendPacket(sm);
					player.sendPacket(sm2);
					npc.onDecay();
					htmltext = npc.getId() + "-01.html";
					
					// Notify to scripts.
					if (EventDispatcher.getInstance().hasListener(EventType.ON_CONQUEST_FLOWER_COLLECT))
					{
						EventDispatcher.getInstance().notifyEventAsync(new OnConquestFlowerCollect(player, npc.getId()));
					}
					
					if (player.isInventoryUnder90(false))
					{
						final int random = getRandom(100);
						
						// Reward only from Fire Flower
						if ((random < DIVINE_FIRE_CHANCE) && (npc.getId() == FIRE_FLOWER))
						{
							giveItems(player, DIVINE_FIRE, 1);
						}
						
						// Rewards from all flowers
						if (random < 5) // 5% chance
						{
							giveItems(player, SACRED_FIRE_SUMMON_SCROLL, 1);
						}
						else if (random < 10) // 10% chance
						{
							giveItems(player, SEALED_GHOST_SOUL_ORB, 1);
						}
						else if (random < 15) // 15% chance
						{
							giveItems(player, SEALED_FIRE_SOURCE, 1);
							// player.sendPacket(SystemMessageId.YOU_HAVE_RECEIVED_FIRE_SOURCE_POINTS);
						}
						else if (random < 20) // 20% chance
						{
							giveItems(player, SEED_OF_FIRE, 1);
						}
						else if (random < 25) // 25% chance
						{
							giveItems(player, EIGIS_ARMOR_FRAGMENT, 1);
						}
						else if (random < 30) // 30% chance
						{
							GlobalVariablesManager.getInstance().set("CONQUEST_SERVER_POINTS", GlobalVariablesManager.getInstance().getLong("CONQUEST_SERVER_POINTS", 0) + SERVER_POINTS_AMOUNT);
							player.sendPacket(SystemMessageId.YOU_HAVE_RECEIVED_SERVER_CONQUEST_POINTS);
						}
						else
						{
							player.getVariables().set(PlayerVariables.CONQUEST_PERSONAL_POINTS, player.getVariables().getLong(PlayerVariables.CONQUEST_PERSONAL_POINTS, 0) + PERSONAL_POINTS_AMOUNT);
							player.sendPacket(SystemMessageId.YOU_HAVE_RECEIVED_PERSONAL_CONQUEST_POINTS);
						}
					}
					else
					{
						player.sendPacket(SystemMessageId.YOUR_INVENTORY_S_WEIGHT_SLOT_LIMIT_HAS_BEEN_EXCEEDED_SO_YOU_CAN_T_RECEIVE_THE_REWARD_PLEASE_FREE_UP_SOME_SPACE_AND_TRY_AGAIN);
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
		return npc.getId() + ".html";
	}
	
	public static void main(String[] args)
	{
		new Flowers();
	}
}
