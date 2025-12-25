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
package ai.areas.Rune.SwampOfScreams;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

import org.l2jmobius.commons.time.TimeUtil;
import org.l2jmobius.gameserver.data.xml.SpawnData;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.spawns.SpawnGroup;
import org.l2jmobius.gameserver.model.spawns.SpawnTemplate;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.util.ArrayUtil;

/**
 * @author Tanatos
 */
public class SwampOfScreamsSiege extends Script
{
	// NPCs
	private static final int[] SWAMP_MONSTERS =
	{
		24570,
		24571,
		24572,
		24573
	};
	private static final int SWAMP_PETRA = 24574;
	private static final AtomicReference<SpawnTemplate> SPAWN_SWAMP_MONSTERS = new AtomicReference<>();
	
	// Schedule: 12-13 & 19-20
	private static final int[] DAY_TIME =
	{
		12,
		00
	};
	private static final int[] NIGHT_TIME =
	{
		19,
		00
	};
	private static final long DESPAWN_DELAY = 3600000;
	private static boolean _daytime = false;
	
	private SwampOfScreamsSiege()
	{
		addKillId(SWAMP_MONSTERS);
		scheduleDayTime();
		scheduleNightTime();
		LOGGER.info("Swamp of Screams siege starts from 12:00 to 13:00 and from 19:00 to 20:00.");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "day_time_spawn":
			{
				World.getInstance().getPlayers().forEach(p -> showOnScreenMsg(p, NpcStringId.HERALD_S_ANNOUNCEMENT_12_00_13_00_MONSTERS_ARE_SPAWNING_IN_THE_SWAMP_OF_SCREAMS_KILL_THEM_ALL, 2, 10000, true));
				SPAWN_SWAMP_MONSTERS.set(SpawnData.getInstance().getSpawnByName("SwampOfScreamsMonsters"));
				SPAWN_SWAMP_MONSTERS.get().getGroups().forEach(SpawnGroup::spawnAll);
				_daytime = true;
				startQuestTimer("despawn", DESPAWN_DELAY, null, null);
				break;
			}
			case "night_time_spawn":
			{
				World.getInstance().getPlayers().forEach(p -> showOnScreenMsg(p, NpcStringId.HERALD_S_ANNOUNCEMENT_19_00_20_00_MONSTERS_ARE_INVADING_THE_SWAMP_OF_SCREAMS_AT_NIGHT_TIME, 2, 10000, true));
				SPAWN_SWAMP_MONSTERS.set(SpawnData.getInstance().getSpawnByName("SwampOfScreamsMonsters"));
				SPAWN_SWAMP_MONSTERS.get().getGroups().forEach(SpawnGroup::spawnAll);
				_daytime = false;
				startQuestTimer("despawn", DESPAWN_DELAY, null, null);
				break;
			}
			case "despawn":
			{
				if (_daytime)
				{
					World.getInstance().getPlayers().forEach(p -> showOnScreenMsg(p, NpcStringId.HERALD_S_ANNOUNCEMENT_19_00_20_00_MONSTERS_IN_THE_SWAMP_OF_SCREAMS_HAVE_FLED, 2, 10000, true));
					scheduleDayTime();
				}
				else
				{
					World.getInstance().getPlayers().forEach(p -> showOnScreenMsg(p, NpcStringId.HERALD_S_ANNOUNCEMENT_12_00_13_00_MONSTERS_IN_THE_SWAMP_OF_SCREAMS_ARE_DEFEATED, 2, 10000, true));
					scheduleNightTime();
				}
				
				SPAWN_SWAMP_MONSTERS.set(SpawnData.getInstance().getSpawnByName("SwampOfScreamsMonsters"));
				SPAWN_SWAMP_MONSTERS.get().getGroups().forEach(SpawnGroup::despawnAll);
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if ((ArrayUtil.contains(SWAMP_MONSTERS, npc.getId())) && (getRandom(100) < 3))
		{
			addSpawn(SWAMP_PETRA, npc.getLocation(), false, 600000, false);
		}
	}
	
	private void scheduleDayTime()
	{
		final Calendar nextDayTime = TimeUtil.getNextTime(DAY_TIME[0], DAY_TIME[1]);
		startQuestTimer("day_time_spawn", nextDayTime.getTimeInMillis() - System.currentTimeMillis(), null, null);
	}
	
	private void scheduleNightTime()
	{
		final Calendar nextNightTime = TimeUtil.getNextTime(NIGHT_TIME[0], NIGHT_TIME[1]);
		startQuestTimer("night_time_spawn", nextNightTime.getTimeInMillis() - System.currentTimeMillis(), null, null);
	}
	
	public static void main(String[] args)
	{
		new SwampOfScreamsSiege();
	}
}
