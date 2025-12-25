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
public class AetherDrops extends Script
{
	// Monsters
	private static final int[] MONSTERS =
	{
		// Blazing Swamp
		23488, // Magma Apophis
		23496, // Magma Ifrit
		// Abandoned Coal Mines
		24577, // Black Hammer Artisan
		24578, // Black Hammer Collector
		24579, // Black Hammer Protector
		// Cemetery Lv.118
		24843, // Royal Guard
		24844, // Royal Guard Captain
		24845, // Royal Field Officer
		24846, // Commander of Operations
		24847, // Elite Wizard
		24848, // Wizard Captain
		// Fields of Massacre Lv.116
		24488, // Doom Archer
		24489, // Doom Scout
		24490, // Doom Soldier
		24487, // Graveyard Predator
		24486, // Dismal Pole
		24491, // Doom Knight
		// Silent Valley 106
		24506, // Silence Witch
		24507, // Silence Preacle
		24508, // Silence Warrior
		24509, // Silence Slave
		24510, // Silence Hannibal
		// Ivory Tower Crater Lv.106
		24421, // Stone Gargoyle
		24422, // Stone Golem
		24423, // Monster Eye
		24424, // Gargoyle Hunter
		24425, // Steel Golem
		24426, // Stone Cube
		// Enchanted Valley Lv.104
		23567, // Nymph Rose
		23568, // Nymph Lily
		23569, // Nymph Lily
		23570, // Nymph Tulip
		23571, // Nymph Tulip
		23572, // Nymph Cosmos
		23573, // Nymph Cosmos
		23578, // Nymph Guardian
		23581, // Apherus
		// Forest of Mirrors Lv.110
		24461, // Forest Ghost
		24462, // Bewildered Expedition Member
		24463, // Bewildered Patrol
		24464, // Bewildered Dwarf Adventurer
		24465, // Forest Evil Spirit
		24466, // Demonic Mirror
		// Desert Query Lv.102
		23811, // Cantera Tanya
		23812, // Cantera Deathmoz
		23813, // Cantera Floxis
		23814, // Cantera Belika
		23815, // Cantera Bridget
		// Beleth's Magic Circle Lv.102
		23354, // Decay Hannibal
		23355, // Armor Beast
		23356, // Klein Soldier
		23357, // Disorder Warrior
		23360, // Bizuard
		23361, // Mutated Fly
		// Phantasmal Ridge Lv.104
		24511, // Lunatikan
		24512, // Garion Neti
		24513, // Desert Wendigo
		24514, // Koraza
		24515, // Kandiloth
		// Wasteland Lv.118
		24500, // Sand Golem
		24501, // Centaur Fighter
		24502, // Centaur Marksman
		24503, // Centaur Wizard
		24504, // Centaur Warlord
		24505, // Earth Elemental Lord
		// Tanor Canyon Lv. 108
		24587, // Tanor Silenos
		20936, // Tanor Silenos
		20937, // Tanor Silenos Soldier
		20938, // Tanor Silenos Scout
		20939, // Tanor Silenos Warrior
		20942, // Nightmare Guide
		20943, // Nightmare Watchman
		// Alligator Island Lv.108
		24373, // Dailaon Lad
		24376, // Nos Lad
		24377, // Swamp Tribe
		24378, // Swamp Alligator
		24379, // Swamp Warrior
		// Field of Silence Lv.110
		24517, // Kropiora
		24520, // Krotania
		24521, // Krophy
		24522, // Spiz Krphy
		24523, // Krotany
		// Field of Whispers Lv.112
		24304, // Groz Kropiora
		24305, // Groz Krotania
		24306, // Groz Krophy
		24307, // Groz Krotany
		24308, // Groz Water Drake
		// Isle of Prayer Lv.112
		24445, // Lizardman Rouge
		24446, // Island Guard
		24447, // Niasis
		24448, // Lizardman Archer
		24449, // Lizardman Warrior
		24450, // Lizardman Wizard
		24451, // Lizardman Defender
		// Fafurion Temple Lv.124
		24318, // Temple Guard Captain
		24321, // Temple Patrol Guard
		24322, // Temple Knight Recruit
		24323, // Temple Guard
		24324, // Temple Guardian Warrior
		24325, // Temple Wizard
		24326, // Temple Guardian Wizard
		24329, // Starving Water Dragon
		// Superion Fortress Lv.102
		23774, // Delta Bathus
		23775, // Delta Krakos
		23776, // Delta Kshana
		23777, // Royal Templar
		23778, // Royal Shooter
		23779, // Royal Wizard
		23780, // Royal Templar Colonel
		23781, // Royal Sharpshooter
		23782, // Royal Archmage
		// Breka's Stronghold Lv.114
		24415, // Breka Orc Scout
		24416, // Breka Orc Scout Captain
		24417, // Breka Orc Archer
		24418, // Breka Orc Shaman
		24419, // Breka Orc Slaughterer
		24420, // Breka Orc prefect
		// Swamp of Screams Lv.116
		24570, // Dire Stakato Drone
		24571, // Dire Stakato Berserker
		24572, // Dire Stakato Shaman
		24573, // Dire Stakato Witch
		// Sel Mahum Training Grounds Lv.114
		24492, // Sel Mahum Soldier
		24493, // Sel Mahum Squad Leader
		24494, // Sel Mahum Warrior
		24495, // Keltron
		// Plains of the Lizardman Lv.114
		24496, // Tanta Lizardman Warrior
		24497, // Tanta Lizardman Archer
		24498, // Tanta Lizardman Wizard
		24499, // Priest Uguros
		// Varka Silenos Barracks Lv.114
		24636, // Varka Silenos Magus
		24637, // Varka Silenos Shaman
		24638, // Varka Silenos Footman
		24639, // Varka Silenos Seargeant
		24640, // Varka Silenos Officer
		// Ketra Orc Barracks Lv.112
		24631, // Ketra Orc Shaman
		24632, // Ketra Orc Prophet
		24633, // Ketra Orc Warrior
		24634, // Ketra Orc Lieutenant
		24635, // Battalion Commander
		// Wall of Argos Lv.116
		24606, // Captive Antelope
		24607, // Captive Bandersnatch
		24608, // Captive Buffalo
		24609, // Captive Grendel
		24610, // Eye of Watchman
		24611, // Elder Homunculus
		// Neutral Zone Lv.110
		24641, // Tel Mahum Wizard
		24642, // Tel Mahum Legionary
		24643, // Tel Mahum Footman
		24644, // Tel Mahum Lieutenant
		// Sea of Spores Lv.116
		24621, // Laikel
		24622, // Harane
		24623, // Lesatanas
		24624, // Arbor
		// Cruma Marshlands Lv.122
		24930, // Black Demon Knight
		24931, // Black Demon Warrior
		24932, // Black Demon Scout
		24933, // Black Demon Wizard
		// Frozen Labyrinth Lv.122
		24934, // Frozen Soldier
		24935, // Frozen Defender
		24936, // Ice Knight
		24937, // Glacier Golem
		24938, // Ice Fairy
		// Sel Mahum Base Lv.124
		24961, // Sel Mahum Footman
		24962, // Sel Mahum Elite Soldier
		24963, // Sel Mahum Shaman
		24964, // Sel Mahum Wizard
		// Dragon Valley West Lv.120
		24664, // Graveyard Death Lich
		24665, // Graveyard Death Berserker
		24666, // Graveyard Death Soldier
		24667, // Graveyard Death Knight
		// Dragon Valley East Lv.122
		24669, // Dragon Officer
		24670, // Dragon Beast
		24671, // Dragon Centurion
		24672, // Elite Dragon Guard
		// Shadow Of The Mother Tree Lv.120
		24965, // Creeper Rampike
		24966, // Fila Aprias
		24967, // Flush Teasle
		24968, // Treant Blossom
		24969, // Arsos Butterfly
		// Execution Grounds Lv.126
		24673, // Zombie Orc
		24674, // Zombie Dark Elf
		24675, // Zombie Dwarf
		24676, // Schnabel Stalker
		24677, // Henker Hacker
		24678, // Schnabel Doctor
		24679, // Henker Anatomist
	};
	
	// Item
	private static final int AETHER = 81215;
	
	// Misc
	private static final String AETHER_DROP_COUNT_VAR = "AETHER_DROP_COUNT";
	private static final int PLAYER_LEVEL = 85;
	private static final int DROP_DAILY = 120;
	private static final int DROP_MIN = 1;
	private static final int DROP_MAX = 1;
	private static final double CHANCE = 1.5;
	
	private AetherDrops()
	{
		addKillId(MONSTERS);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Player player = getRandomPartyMember(killer);
		if ((player.getLevel() >= PLAYER_LEVEL) && (getRandom(100) < CHANCE) && ((player.getParty() == null) || ((player.getParty() != null) && player.isInsideRadius3D(npc, PlayerConfig.ALT_PARTY_RANGE))))
		{
			final int count = player.getVariables().getInt(AETHER_DROP_COUNT_VAR, 0);
			if (count < DROP_DAILY)
			{
				player.getVariables().set(AETHER_DROP_COUNT_VAR, count + 1);
				giveItems(player, AETHER, getRandom(DROP_MIN, DROP_MAX));
			}
			else
			{
				if (count == DROP_DAILY)
				{
					player.getVariables().set(AETHER_DROP_COUNT_VAR, count + 1);
					player.sendPacket(SystemMessageId.YOU_EXCEEDED_THE_LIMIT_AND_CANNOT_COMPLETE_THE_TASK);
				}
				
				player.sendMessage("You obtained all available Aether for this day!");
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
			ps.setString(1, AETHER_DROP_COUNT_VAR);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Could not reset Aether drop count: " + e.getMessage());
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getVariables().remove(AETHER_DROP_COUNT_VAR);
		}
		
		LOGGER.info(getClass().getSimpleName() + " drop count has been reset.");
	}
	
	public static void main(String[] args)
	{
		new AetherDrops();
	}
}
