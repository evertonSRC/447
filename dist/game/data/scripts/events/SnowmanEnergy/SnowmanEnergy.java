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
package events.SnowmanEnergy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.OnDailyReset;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.script.LongTimeEvent;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.util.ArrayUtil;

/**
 * Snowman Energy event AI.
 * @author NasSeKa
 */
public class SnowmanEnergy extends LongTimeEvent
{
	// NPCs
	private static final int SANTA = 33888;
	private static final int BLUE_SNOWMAN = 19809;
	private static final int RED_SNOWMAN = 19810;
	private static final int[] MONSTERS_SOLO =
	{
		// Ivory Tower Crater
		24422, // Stone Golem
		24425, // Steel Golem
		24421, // Stone Gargoyle
		24424, // Gargoyle Hunter
		24426, // Stone Cube
		24423, // Monster Eye
		// Silent Valley
		24506, // Silence Witch
		24508, // Silence Warrior
		24510, // Silence Hannibal
		24509, // Silence Slave
		24507, // Silence Preacle
		// Alligator Island
		24377, // Swamp Tribe
		24378, // Swamp Alligator
		24379, // Swamp Warrior
		24373, // Dailaon Lad
		24376, // Nos Lad
		// Tanor Canyon
		20941, // Tanor Silenos Chieftain
		20939, // Tanor Silenos Warrior
		20937, // Tanor Silenos Soldier
		20942, // Nightmare Guide
		20938, // Tanor Silenos Scout
		20943, // Nightmare Watchman
		24587, // Tanor Silenos
		// Forest of Mirrors
		24466, // Demonic Mirror
		24465, // Forest Evil Spirit
		24461, // Forest Ghost
		24464, // Bewildered Dwarf Adventurer
		24463, // Bewildered Patrol
		24462, // Bewildered Expedition Member
		// Field of Silence
		24523, // Krotany
		24520, // Krotania
		24521, // Krophy
		24522, // Spiz Krophy
		// Isle of Prayer
		24451, // Lizardman Defender
		24449, // Lizardman Warrior
		24448, // Lizardman Archer
		24450, // Lizardmen Wizard
		24447, // Niasis
		24445, // Lizardman Rogue
		24446, // Island Guard
		// Field of Whispers
		24304, // Groz Kropiora
		24305, // Groz Krotania
		24306, // Groz Krophy
		24307, // Groz Krotany
		// Breka's Stronghold
		24420, // Breka Orc Prefect
		24416, // Breka Orc Scout Captain
		24419, // Breka Orc Slaughterer
		24415, // Breka Orc Scout
		24417, // Breka Orc Archer
		24418, // Breka Orc Shaman
		// Sel Mahum Training Grounds
		24492, // Sel Mahum Soldier
		24494, // Sel Mahum Warrior
		24493, // Sel Mahum Squad Leader
		24495, // Keltron
		// Plains of the Lizardmen
		24496, // Tanta Lizardman Warrior
		24498, // Tanta Lizardman Wizard
		24499, // Priest Ugoros
		24497, // Tanta Lizardman Archer
		// Fields of Massacre
		24486, // Dismal Pole
		24487, // Graveyard Predator
		24489, // Doom Scout
		24491, // Doom Knight
		24490, // Doom Soldier
		24488, // Doom Archer
		// Wall of Argos
		24606, // Captive Antelope
		24607, // Captive Bandersnatch
		24608, // Captive Buffalo
		24609, // Captive Grendel
		24610, // Eye of Watchman
		24611, // Elder Homunculus
		// Wasteland
		24501, // Centaur Fighter
		24504, // Centaur Warlord
		24505, // Earth Elemental Lord
		24503, // Centaur Wizard
		24500, // Sand Golem
		24502, // Centaur Marksman
		// Cemetery
		24847, // Elite Wizard
		24843, // Royal Guard
		24844, // Royal Guard Captain
		24848, // Chief Magician
		24845, // Royal Field Officer
		24846, // Commander of Operations
		// Valley of Saints
		24876, // Guide of Splendor
		24877, // Herald of Splendor
		24878, // Believer of Splendor
		24879, // Observer of Splendor
		24880, // Wiseman of Splendor
		// Hot Springs
		24881, // Springs Dwarf Hero
		24882, // Springs Scavenger
		24883, // Springs Dwarf Defender
		24884, // Springs Dwarf Berserker
		24885, // Springs Dwarf Priest
		24886, // Springs Yeti
	};
	private static final int[] MONSTERS_PARTY =
	{
		// Neutral Zone
		24641, // Tel Mahum Wizard
		24642, // Tel Mahum Legionary
		24643, // Tel Mahum Footman
		24644, // Tel Mahum Lieutenant
		// Ketra Orc Outpost
		24631, // Ketra Orc Shaman
		24632, // Ketra Orc Prophet
		24633, // Ketra Orc Warrior
		24634, // Ketra Orc Lieutenant
		24635, // Ketra Orc Battalion Commander
		// Varka Silenos Barracks
		24636, // Varka Silenos Magus
		24637, // Varka Silenos Shaman
		24638, // Varka Silenos Footman
		24639, // Varka Silenos Sergeant
		24640, // Varka Silenos Officer
		// Sea Of Spores
		24226, // Aranea
		24227, // Keros
		24228, // Falena
		24229, // Atrofa
		24230, // Nuba
		24231, // Torfedo
		24234, // Lesatanas
		24235, // Arbor
		24236, // Tergus
		24237, // Skeletus
		24238, // Atrofine
		// Fafurion Temple
		24329, // Starving Water Dragon
		24318, // Temple Guard Captain
		24325, // Temple Wizard
		24324, // Temple Guardian Warrior
		24326, // Temple Guardian Wizard
		24323, // Temple Guard
		24321, // Temple Patrol Guard
		24322, // Temple Knight Recruit
		// Dragon Valley
		24480, // Dragon Legionnaire
		24482, // Dragon Officer
		24481, // Dragon Peltast
		24483, // Dragon Centurion
		24484, // Dragon Elite Guard
		24485, // Behemoth Dragon
		// Beast Farm
		24651, // Red Kookaburra
		24652, // Blue Kookaburra
		24653, // White Cougar
		24654, // Cougar
		24655, // Black Buffalo
		24656, // White Buffalo
		24657, // Grandel
		24658, // Black Grandel
	};
	
	// Item
	private static final int CHRISTMAS_GIFT = 81927;
	
	// Skill
	private static final Skill SNOWMAN_ENERGY = SkillData.getInstance().getSkill(34028, 1);
	
	// Misc
	private static final String SNOWMAN_GIFT_RECIEVED_VAR = "SNOWMAN_GIFT_RECIEVED";
	private static final int PLAYER_LEVEL = 105;
	
	private SnowmanEnergy()
	{
		addKillId(MONSTERS_SOLO);
		addKillId(MONSTERS_PARTY);
		addKillId(BLUE_SNOWMAN);
		addKillId(RED_SNOWMAN);
		addStartNpc(SANTA);
		addFirstTalkId(SANTA);
		addTalkId(SANTA);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		switch (event)
		{
			case "get_gift":
			{
				if (player.getLevel() < PLAYER_LEVEL)
				{
					htmltext = "33888-no-level.htm";
					break;
				}
				
				if (player.getLevel() < PLAYER_LEVEL)
				{
					htmltext = "33888-no-level.htm";
				}
				
				if (!player.getAccountVariables().getBoolean(SNOWMAN_GIFT_RECIEVED_VAR, false))
				{
					giveItems(player, CHRISTMAS_GIFT, 1);
					player.getAccountVariables().set(SNOWMAN_GIFT_RECIEVED_VAR, true);
					htmltext = "33888-successful.htm";
				}
				else
				{
					htmltext = "33888-already-received.htm";
				}
				break;
			}
			case "learn_about":
			{
				htmltext = "33888-info.htm";
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
	public void onKill(Npc npc, Player player, boolean isSummon)
	{
		if (isEventPeriod())
		{
			if (getRandom(100) < 2)
			{
				if (ArrayUtil.contains(MONSTERS_SOLO, npc.getId()))
				{
					addSpawn(RED_SNOWMAN, npc, true, 60000, false, player.getInstanceId());
				}
				else if (ArrayUtil.contains(MONSTERS_PARTY, npc.getId()))
				{
					addSpawn(BLUE_SNOWMAN, npc, true, 60000, false, player.getInstanceId());
				}
			}
			
			if (npc.getId() == RED_SNOWMAN)
			{
				SkillCaster.triggerCast(player, player, SNOWMAN_ENERGY);
				if (getRandom(100) < 30)
				{
					player.addItem(ItemProcessType.REWARD, CHRISTMAS_GIFT, 1, player, true);
				}
			}
			else if (npc.getId() == BLUE_SNOWMAN)
			{
				if (player.getParty() != null)
				{
					final Party party = player.getParty();
					final List<Player> members = party.getMembers();
					for (Player member : members)
					{
						if (member.isInsideRadius3D(npc, PlayerConfig.ALT_PARTY_RANGE))
						{
							SkillCaster.triggerCast(member, member, SNOWMAN_ENERGY);
						}
					}
				}
				else
				{
					SkillCaster.triggerCast(player, player, SNOWMAN_ENERGY);
				}
				
				if (getRandom(100) < 30)
				{
					player.addItem(ItemProcessType.REWARD, CHRISTMAS_GIFT, 1, player, true);
				}
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
			ps.setString(1, SNOWMAN_GIFT_RECIEVED_VAR);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Could not reset variables: " + e.getMessage());
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getAccountVariables().remove(SNOWMAN_GIFT_RECIEVED_VAR);
		}
		
		LOGGER.info(getClass().getSimpleName() + " has been reset.");
	}
	
	public static void main(String[] args)
	{
		new SnowmanEnergy();
	}
}
