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
package events.SmashItCompletely;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.OnDailyReset;
import org.l2jmobius.gameserver.model.events.holders.item.OnItemUse;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.script.LongTimeEvent;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.network.serverpackets.NpcSay;
import org.l2jmobius.gameserver.util.Broadcast;

/**
 * @URL https://l2central.info/main/events_and_promos/1444.html
 * @author CostyKiller
 * @apiNote You need to edit client file to add additional seed rewards to clan missions.
 * @TODO Fix autoloot and self destruction skill for melons.
 */
public class SmashItCompletely extends LongTimeEvent
{
	// NPCs
	private static final int DOODOODOO = 34575; // Baby Shark Doo Doo Doo
	private static final int SWEET_WATERMELON = 13608;
	private static final int PRIME_WATERMELON = 13609;
	private static final int LARGE_PRIME_WATERMELON = 13610;
	
	// Items
	private static final int WATERMELON_SEED = 81782;
	private static final int ICE_BOX = 81783;
	
	// Skills
	private static final SkillHolder[] SKILLS =
	{
		new SkillHolder(39714, 2), // Sweet Watermelon
		new SkillHolder(39714, 3), // Prime Watermelon
		new SkillHolder(39714, 4), // Large Prime Watermelon
		new SkillHolder(33927, 1), // Watermelon Burst
	};
	
	// Buffs
	private static final SkillHolder[] BUFFS =
	{
		new SkillHolder(33928, 1), // Dizzy Lv. 1 = 1 hour
		new SkillHolder(33928, 2), // Dizzy Lv. 2 = 2 hours
		new SkillHolder(33928, 3), // Dizzy Lv. 3 = 3 hours
	};
	
	// Misc
	private static final String SMASH_IT_COMPLETELY_VAR = "SMASH_IT_COMPLETELY_SEED_RECEIVED";
	private static final int PLAYER_LEVEL = 105;
	private static final NpcStringId[] DOODOODOO_TEXT =
	{
		NpcStringId.HERE_COMES_BABY_SHARK_DOO_DOO_DOO,
		NpcStringId.HEY_I_WANT_A_WATERMELON_TOO
	};
	
	// Chances for higher quality watermelons
	private static final int LARGE_PRIME_WATERMELON_SUMMON_CHANCE = 15; // Large Watermelon summon chance
	private static final int PRIME_WATERMELON_SUMMON_CHANCE = 40; // Prime Watermelon summon chance
	
	// Chances to get Dizzy buff
	private static final int LARGE_PRIME_WATERMELON_BUFF_CHANCE = 80; // Large Watermelon buff chance
	private static final int PRIME_WATERMELON_BUFF_CHANCE = 60; // Prime Watermelon buff chance
	private static final int SWEET_WATERMELON_BUFF_CHANCE = 40; // Sweet Watermelon buff chance
	
	public SmashItCompletely()
	{
		addStartNpc(DOODOODOO);
		addFirstTalkId(DOODOODOO);
		addTalkId(DOODOODOO);
		addSpawnId(DOODOODOO, SWEET_WATERMELON, PRIME_WATERMELON, LARGE_PRIME_WATERMELON);
		addKillId(SWEET_WATERMELON, PRIME_WATERMELON, LARGE_PRIME_WATERMELON);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34575.htm":
			case "34575-1.htm":
			case "34575-2.htm":
			case "34575-3.htm":
			{
				htmltext = event;
				break;
			}
			case "getSeed":
			{
				if (npc.getId() != DOODOODOO)
				{
					break;
				}
				
				if (player.getLevel() < PLAYER_LEVEL)
				{
					htmltext = "34575-no-level.htm";
					break;
				}
				
				if (player.getAccountVariables().getBoolean(SMASH_IT_COMPLETELY_VAR, false))
				{
					player.sendMessage("This account has already received a seed. An account can receive a seed only once a day.");
					break;
				}
				
				player.getAccountVariables().set(SMASH_IT_COMPLETELY_VAR, true);
				player.getAccountVariables().storeMe();
				giveItems(player, WATERMELON_SEED, 1);
				htmltext = "34575-successful.htm";
				break;
			}
			case "DOODOODOO_SHOUT":
			{
				Broadcast.toKnownPlayersInRadius(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), getRandomEntry(DOODOODOO_TEXT)), 1000);
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
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isPet)
	{
		if (killer.getSummonedNpc(npc.getObjectId()) == null)
		{
			killer.sendMessage("You must grow your own watermelon to get reward.");
			return;
		}
		
		switch (npc.getId())
		{
			case SWEET_WATERMELON:
			{
				if (getRandom(100) < SWEET_WATERMELON_BUFF_CHANCE)
				{
					SkillCaster.triggerCast(killer, killer, BUFFS[0].getSkill());
				}
				break;
			}
			case PRIME_WATERMELON:
			{
				if (getRandom(100) < PRIME_WATERMELON_BUFF_CHANCE)
				{
					SkillCaster.triggerCast(killer, killer, BUFFS[1].getSkill());
				}
				
				if (killer.isInventoryUnder80(false))
				{
					killer.doAutoLoot(npc.asAttackable(), ICE_BOX, 1); // Prime Watermelon Ice Box x1
				}
				else
				{
					killer.sendPacket(SystemMessageId.NOT_ENOUGH_SPACE_IN_THE_INVENTORY_UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
				}
				break;
			}
			case LARGE_PRIME_WATERMELON:
			{
				if (getRandom(100) < LARGE_PRIME_WATERMELON_BUFF_CHANCE)
				{
					SkillCaster.triggerCast(killer, killer, BUFFS[2].getSkill());
				}
				
				if (killer.isInventoryUnder80(false))
				{
					killer.doAutoLoot(npc.asAttackable(), ICE_BOX, 2); // Large Prime Watermelon Ice Box x2
				}
				else
				{
					killer.sendPacket(SystemMessageId.NOT_ENOUGH_SPACE_IN_THE_INVENTORY_UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
				}
				break;
			}
		}
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case DOODOODOO:
			{
				startQuestTimer("DOODOODOO_SHOUT", (5 * 60 * 1000), npc, null, true); // Spam texts every 5 min
				break;
			}
			case SWEET_WATERMELON:
			case PRIME_WATERMELON:
			case LARGE_PRIME_WATERMELON:
			{
				SkillCaster.triggerCast(npc, npc, BUFFS[2].getSkill()); // TODO: FIX death skill.
				break;
			}
		}
	}
	
	@RegisterEvent(EventType.ON_ITEM_USE)
	@RegisterType(ListenerRegisterType.ITEM)
	@Id(81782) // Watermelon Seed
	public void onItemUse(OnItemUse event)
	{
		if (isEventPeriod())
		{
			final Player player = event.getPlayer();
			for (SkillHolder skill : BUFFS)
			{
				if (player.isAffectedBySkill(skill))
				{
					player.sendMessage("You must remove current Dizzy buff effect to be able to grow another watermelon.");
					player.getInventory().addItem(ItemProcessType.REFUND, event.getItem().getId(), 1, player, player);
					return;
				}
			}
			
			final int chance = getRandom(100);
			if (chance < LARGE_PRIME_WATERMELON_SUMMON_CHANCE)
			{
				SkillCaster.triggerCast(player, player, SKILLS[2].getSkill());
			}
			else if (chance < PRIME_WATERMELON_SUMMON_CHANCE)
			{
				SkillCaster.triggerCast(player, player, SKILLS[1].getSkill());
			}
			else
			{
				SkillCaster.triggerCast(player, player, SKILLS[0].getSkill());
			}
		}
	}
	
	@RegisterEvent(EventType.ON_DAILY_RESET)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void onDailyReset(OnDailyReset event)
	{
		if (!isEventPeriod())
		{
			return;
		}
		
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ? AND account_name NOT IN (SELECT account_name FROM characters WHERE online = 1)"))
		{
			ps.setString(1, SMASH_IT_COMPLETELY_VAR);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Could not reset variables: " + e.getMessage());
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getAccountVariables().remove(SMASH_IT_COMPLETELY_VAR);
		}
		
		LOGGER.info(getClass().getSimpleName() + " has been reset.");
	}
	
	public static void main(String[] args)
	{
		new SmashItCompletely();
	}
}
