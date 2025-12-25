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
package ai.others;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.OnDailyReset;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * @author Mobius
 */
public class WaterDragonEliteSupplyDrops extends Script
{
	// Monsters
	private static final int[] MONSTERS =
	{
		24598, // Water Dragon's Elite Raider
		24599, // Water Dragon's Elite Swordsman
		24600, // Water Dragon's Elite Wyrm
		24603, // Water Dragon's Pikeman
		24604, // Water Dragon's Swordsman
		24605, // Weakened Krotania
		24957, // Fafurion's Praetorian Knight
		24958, // Fafurion's Praetorian Pikeman
		24959, // Fafurion's Praetorian Swordsman
		24960, // Fafurion's Praetorian Water Dragon
		24992, // Fafurion's Praetorian Invader
		24993, // Fafurion's Elite Praetorian Soldier
		24994, // Fafurion's Praetorian Shaman
		24995, // Fafurion's Praetorian Archer
	};
	
	// Item
	private static final int WATER_DRAGON_ELITE_SUPPLIES = 81758;
	
	// Misc
	private static final String WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR = "WATER_DRAGON_SUPPLIES_DROP_COUNT";
	private static final int PLAYER_LEVEL = 99;
	private static final int DROP_DAILY = 100;
	private static final int DROP_MIN = 1;
	private static final int DROP_MAX = 1;
	private static final double CHANCE = 7.2;
	
	private WaterDragonEliteSupplyDrops()
	{
		addKillId(MONSTERS);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Player player = getRandomPartyMember(killer);
		if ((player.getLevel() >= PLAYER_LEVEL) && (getRandom(100) < CHANCE) && ((player.getParty() == null) || ((player.getParty() != null) && player.isInsideRadius3D(npc, PlayerConfig.ALT_PARTY_RANGE))))
		{
			final int count = player.getVariables().getInt(WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR, 0);
			if (count < DROP_DAILY)
			{
				player.getVariables().set(WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR, count + 1);
				giveItems(player, WATER_DRAGON_ELITE_SUPPLIES, getRandom(DROP_MIN, DROP_MAX));
			}
			else
			{
				if (count == DROP_DAILY)
				{
					player.getVariables().set(WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR, count + 1);
					player.sendPacket(SystemMessageId.YOU_EXCEEDED_THE_LIMIT_AND_CANNOT_COMPLETE_THE_TASK);
				}
				
				player.sendMessage("You obtained all available Water Dragon's Elite Supplies for this day!");
			}
		}
	}
	
	@RegisterEvent(EventType.ON_DAILY_RESET)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void onDailyReset(OnDailyReset event)
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var = ? AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
		{
			ps.setString(1, WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Could not reset variables: " + e.getMessage());
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getVariables().remove(WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR);
		}
		
		LOGGER.info(getClass().getSimpleName() + " has been reset.");
	}
	
	public static void main(String[] args)
	{
		new WaterDragonEliteSupplyDrops();
	}
}
