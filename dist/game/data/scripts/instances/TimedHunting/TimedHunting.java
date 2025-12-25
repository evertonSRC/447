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
package instances.TimedHunting;

import java.util.Calendar;

import org.l2jmobius.gameserver.data.holders.TimedHuntingZoneHolder;
import org.l2jmobius.gameserver.data.xml.TimedHuntingZoneData;
import org.l2jmobius.gameserver.managers.GrandBossManager;
import org.l2jmobius.gameserver.managers.InstanceManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.util.ArrayUtil;

/**
 * @author Mobius, Tanatos, Notorion
 */
public class TimedHunting extends InstanceScript
{
	// NPCs
	// Fioren's Crystal Prison
	private static final int FIOREN = 34634;
	private static final int RESEARCHERS_TELEPORTER_105 = 34529; // Storm Isle
	private static final int RESEARCHERS_TELEPORTER_110 = 34618; // Isle of Prayer
	private static final int RESEARCHERS_TELEPORTER_115 = 34624; // Alligator Island
	private static final int RESEARCHERS_TELEPORTER_120 = 34682; // Frost Island
	
	// Jamoa's Camp
	private static final int JAMOA = 34635;
	private static final int EXPEDITION_TELEPORTER_105 = 34526; // Primeval Isle
	private static final int EXPEDITION_TELEPORTER_110 = 34626; // Swamp of Screams
	private static final int EXPEDITION_TELEPORTER_115 = 34628; // Blazing Swamp
	private static final int EXPEDITION_TELEPORTER_120 = 34684; // Wasteland
	
	// Pantheon's Museum
	private static final int PANTHEON = 34636;
	private static final int OVERSEER_TELEPORTER_105 = 34524; // Golden Altar
	private static final int OVERSEER_TELEPORTER_110 = 34630; // Mimir's Altar
	private static final int OVERSEER_TELEPORTER_115 = 34632; // Plunderous Plains
	private static final int OVERSEER_TELEPORTER_120 = 34686; // Elven Forest
	
	// Pantheon's Museum Monsters
	private static final int[] PANTHEON_MONSTERS =
	{
		24470,
		24471,
		24472,
		24473,
		24474,
		24475,
		24837,
		24838,
		24839,
		24840,
		24841,
		24842,
		24939,
		24940,
		24941,
		24942,
		24943,
		24944,
		24974,
		24975,
		24976,
		24977,
		24978,
		24979
	};
	
	// Abandoned Coal Mines
	private static final int INVESTIGATORS_TELEPORTER = 34551;
	
	// Imperial Tomb
	private static final int SEARCH_TEAM_TELEPORTER = 34552;
	
	// Ravaged Innadril
	private static final int LIONEL_HUNTER = 34646; // Heine
	private static final int PATROL_TELEPORTER_105 = 34568; // Field of Silence
	private static final int PATROL_TELEPORTER_115 = 34647; // Alligator Beach
	private static final int PATROL_TELEPORTER_120 = 34688; // Alligator Island
	
	// Otherworldly Atelia Refinery
	private static final int ATELIA_REFINERY_TELEPORT_DEVICE = 34583; // Otherworldly Atelia Refinery
	
	// Time Rift
	private static final int GATEKEEPER_ZIGGURAT = 34694; // Time Rift
	private static final int IVORY_TOWER_RESEARCHER = 34695; // Time Rift
	
	// Tower of Insolence
	private static final int TELEPORT_SCOUT = 34549; // Tower of Insolence
	private static final int SPACETEMPORAL_RIFT = 34616; // Teleport to Baium - Tower of Insolence
	private static final int BAIUM_BOSS_ID = 29391; // Baium ID
	
	// Skills
	private static final SkillHolder MORE_ADENA = new SkillHolder(32930, 1);
	
	// Locations
	private static final Location FIELD_OF_SILENCE = new Location(95983, 170989, -3640);
	private static final Location FIELD_OF_WHISPERS = new Location(95981, 210144, -3456);
	private static final Location ALLIGATOR_BEACH = new Location(114572, 202589, -3408);
	private static final Location ALLIGATOR_ISLAND = new Location(121342, 185640, -3587);
	private static final Location BAIUM_TELEPORT_LOC = new Location(113365, 14886, 10053);
	
	// Misc
	private static final int[] TEMPLATE_IDS =
	{
		1001, // Fioren's Crystal Prison
		1006, // Jamoa's Camp
		1007, // Pantheon's Museum
		1011, // Abandoned Coal Mines
		1012, // Imperial Tomb
		1013, // Ravaged Innadril
		1015, // Otherworldly Atelia Refinery
		1016, // Time Rift
		1020, // Tower of Insolence
	};
	
	public TimedHunting()
	{
		super(TEMPLATE_IDS);
		addStartNpc(FIOREN, JAMOA, PANTHEON, INVESTIGATORS_TELEPORTER, SEARCH_TEAM_TELEPORTER, LIONEL_HUNTER, ATELIA_REFINERY_TELEPORT_DEVICE, TELEPORT_SCOUT, GATEKEEPER_ZIGGURAT, IVORY_TOWER_RESEARCHER, SPACETEMPORAL_RIFT);
		addFirstTalkId(FIOREN, JAMOA, PANTHEON, INVESTIGATORS_TELEPORTER, SEARCH_TEAM_TELEPORTER, LIONEL_HUNTER, ATELIA_REFINERY_TELEPORT_DEVICE, TELEPORT_SCOUT, GATEKEEPER_ZIGGURAT, IVORY_TOWER_RESEARCHER, SPACETEMPORAL_RIFT);
		addFirstTalkId(RESEARCHERS_TELEPORTER_105, RESEARCHERS_TELEPORTER_110, RESEARCHERS_TELEPORTER_115, RESEARCHERS_TELEPORTER_120);
		addFirstTalkId(EXPEDITION_TELEPORTER_105, EXPEDITION_TELEPORTER_110, EXPEDITION_TELEPORTER_115, EXPEDITION_TELEPORTER_120);
		addFirstTalkId(OVERSEER_TELEPORTER_105, OVERSEER_TELEPORTER_110, OVERSEER_TELEPORTER_115, OVERSEER_TELEPORTER_120);
		addFirstTalkId(INVESTIGATORS_TELEPORTER);
		addFirstTalkId(SEARCH_TEAM_TELEPORTER);
		addFirstTalkId(PATROL_TELEPORTER_105, PATROL_TELEPORTER_115, PATROL_TELEPORTER_120);
		addFirstTalkId(ATELIA_REFINERY_TELEPORT_DEVICE);
		addFirstTalkId(GATEKEEPER_ZIGGURAT, IVORY_TOWER_RESEARCHER);
		addFirstTalkId(TELEPORT_SCOUT, SPACETEMPORAL_RIFT);
		addTalkId(FIOREN, JAMOA, PANTHEON, INVESTIGATORS_TELEPORTER, SEARCH_TEAM_TELEPORTER, LIONEL_HUNTER, ATELIA_REFINERY_TELEPORT_DEVICE, TELEPORT_SCOUT, GATEKEEPER_ZIGGURAT, IVORY_TOWER_RESEARCHER);
		addTalkId(RESEARCHERS_TELEPORTER_105, RESEARCHERS_TELEPORTER_110, RESEARCHERS_TELEPORTER_115, RESEARCHERS_TELEPORTER_120);
		addTalkId(EXPEDITION_TELEPORTER_105, EXPEDITION_TELEPORTER_110, EXPEDITION_TELEPORTER_115, EXPEDITION_TELEPORTER_120);
		addTalkId(OVERSEER_TELEPORTER_105, OVERSEER_TELEPORTER_110, OVERSEER_TELEPORTER_115, OVERSEER_TELEPORTER_120);
		addTalkId(INVESTIGATORS_TELEPORTER);
		addTalkId(SEARCH_TEAM_TELEPORTER);
		addTalkId(PATROL_TELEPORTER_105, PATROL_TELEPORTER_115, PATROL_TELEPORTER_120);
		addTalkId(ATELIA_REFINERY_TELEPORT_DEVICE);
		addTalkId(GATEKEEPER_ZIGGURAT, IVORY_TOWER_RESEARCHER);
		addTalkId(TELEPORT_SCOUT, SPACETEMPORAL_RIFT);
		addKillId(PANTHEON_MONSTERS);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.startsWith("ENTER"))
		{
			final int zoneId = Integer.parseInt(event.split(" ")[1]);
			final TimedHuntingZoneHolder huntingZone = TimedHuntingZoneData.getInstance().getHuntingZone(zoneId);
			if (huntingZone == null)
			{
				return null;
			}
			
			if (huntingZone.isSoloInstance())
			{
				enterInstance(player, npc, huntingZone.getInstanceId());
			}
			else
			{
				Instance world = null;
				for (Instance instance : InstanceManager.getInstance().getInstances())
				{
					if (instance.getTemplateId() == huntingZone.getInstanceId())
					{
						world = instance;
						break;
					}
				}
				
				if (world == null)
				{
					world = InstanceManager.getInstance().createInstance(huntingZone.getInstanceId(), player);
				}
				
				player.teleToLocation(huntingZone.getEnterLocation(), world);
			}
		}
		else if (event.equals("toFieldOfSilence"))
		{
			player.teleToLocation(FIELD_OF_SILENCE);
		}
		else if (event.equals("toFieldOfWhispers"))
		{
			if (player.getLevel() < 110)
			{
				return "34646-1.html";
			}
			
			player.teleToLocation(FIELD_OF_WHISPERS);
		}
		else if (event.equals("toAlligatorBeach"))
		{
			if (player.getLevel() < 115)
			{
				return "34646-1.html";
			}
			
			player.teleToLocation(ALLIGATOR_BEACH);
		}
		else if (event.equals("toAlligatorIsland"))
		{
			if (player.getLevel() < 120)
			{
				return "34646-1.html";
			}
			
			player.teleToLocation(ALLIGATOR_ISLAND);
		}
		else if (event.equals("BAIUM_ENTER")) // Logic to allow entry into Baium zone.
		{
			// 1. Time Validation
			final Calendar now = Calendar.getInstance();
			final int day = now.get(Calendar.DAY_OF_WEEK);
			final int hour = now.get(Calendar.HOUR_OF_DAY);
			final int minute = now.get(Calendar.MINUTE);
			final int second = now.get(Calendar.SECOND);
			
			boolean isOpen = false;
			final int targetDay = Calendar.WEDNESDAY;
			final int startHour = 22;
			final int startMinute = 0;
			final int endHour = 23;
			final int endMinute = 0;
			
			if (day == targetDay)
			{
				long currentTotalSeconds = (hour * 3600) + (minute * 60) + second;
				long startTotalSeconds = (startHour * 3600) + (startMinute * 60);
				long endTotalSeconds = (endHour * 3600) + (endMinute * 60);
				
				if ((currentTotalSeconds >= startTotalSeconds) && (currentTotalSeconds < endTotalSeconds))
				{
					isOpen = true;
				}
			}
			
			if (!isOpen && !player.isGM())
			{
				return "34616.html";
			}
			
			// 2. Baium Status Validation
			final int status = GrandBossManager.getInstance().getStatus(BAIUM_BOSS_ID);
			if (status == 3) // DEAD
			{
				return "34616.html";
			}
			
			// 3. Castle Validation
			boolean hasCastle = false;
			if (player.getClan() != null)
			{
				final int castleId = player.getClan().getCastleId();
				if ((castleId == 5) || (castleId == 8))
				{
					hasCastle = true;
				}
			}
			
			if (!hasCastle && !player.isGM())
			{
				player.sendMessage("You don't belong here!");
				return null;
			}
			
			// 4. Instance Management (Template ID 1020)
			Instance world = null;
			for (Instance instance : InstanceManager.getInstance().getInstances())
			{
				if (instance.getTemplateId() == 1020)
				{
					world = instance;
					break;
				}
			}
			
			if (world == null)
			{
				world = InstanceManager.getInstance().createInstance(1020, player);
			}
			
			if (world != null)
			{
				player.setInstance(world);
				player.teleToLocation(BAIUM_TELEPORT_LOC, world);
			}
			else
			{
				player.sendMessage("Error creating Baium instance.");
			}
		}
		
		return null;
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isPet)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world) && ArrayUtil.contains(PANTHEON_MONSTERS, npc.getId()) && (getRandom(100) < 5) && (!killer.isAffectedBySkill(MORE_ADENA.getSkillId())))
		{
			MORE_ADENA.getSkill().applyEffects(killer, killer);
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (npc.getId() == SPACETEMPORAL_RIFT)
		{
			return "34616.html";
		}
		
		final Instance instance = npc.getInstanceWorld();
		final String htmltext = null;
		if (isInInstance(instance))
		{
			return npc.getId() + ".html";
		}
		
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new TimedHunting();
	}
}