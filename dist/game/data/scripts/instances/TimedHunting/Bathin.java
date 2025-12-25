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

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.model.zone.type.ScriptZone;
import org.l2jmobius.gameserver.network.serverpackets.OnEventTrigger;

/**
 * @author NasSeKa
 */
public class Bathin extends Script
{
	// NPC
	private static final int BATHIN = 26481;
	
	// Misc
	private static final int INSTANCE_ID = 1020;
	private static final int DOOR = 15180100;
	private static final int DOOR_EMITTER = 15180102;
	private static final String BATHIN_ALIVE_VAR = "BATHIN_ALIVE";
	private static final ScriptZone BROADCAST_ZONE = ZoneManager.getInstance().getZoneById(26000, ScriptZone.class);
	
	public Bathin()
	{
		addInstanceCreatedId(INSTANCE_ID);
		addEnterZoneId(BROADCAST_ZONE.getId());
		addKillId(BATHIN);
	}
	
	@Override
	public void onInstanceCreated(Instance instance, Player player)
	{
		if ((instance.getAliveNpcCount(BATHIN) == 0) && GlobalVariablesManager.getInstance().getBoolean(BATHIN_ALIVE_VAR, true))
		{
			instance.spawnGroup("bathin");
		}
		
		final long currentTime = System.currentTimeMillis();
		final Calendar calendarBathin = Calendar.getInstance();
		final Calendar calendarSeal = Calendar.getInstance();
		
		calendarBathin.set(Calendar.DAY_OF_WEEK, 7); // Saturday
		calendarBathin.set(Calendar.HOUR_OF_DAY, 23);
		calendarBathin.set(Calendar.MINUTE, 0);
		calendarBathin.set(Calendar.SECOND, 0);
		
		calendarSeal.set(Calendar.DAY_OF_WEEK, 2); // Monday
		calendarSeal.set(Calendar.HOUR_OF_DAY, 12);
		calendarSeal.set(Calendar.MINUTE, 0);
		calendarSeal.set(Calendar.SECOND, 0);
		
		final long calendarTimeBathin = calendarBathin.getTimeInMillis();
		final long startDelay = Math.max(0, calendarTimeBathin - currentTime);
		
		final long calendarTimeSeal = calendarSeal.getTimeInMillis();
		final long startDelayDoor = Math.max(0, calendarTimeSeal - currentTime);
		
		ThreadPool.scheduleAtFixedRate(() ->
		{
			instance.openCloseDoor(DOOR, true);
			
			if ((instance.getAliveNpcCount(BATHIN) == 0))
			{
				instance.spawnGroup("bathin");
			}
			
			for (Player pplayer : instance.getPlayers())
			{
				if (pplayer.isInsideRadius3D(-147664, 13152, -9424, 10000))
				{
					pplayer.sendPacket(new OnEventTrigger(DOOR_EMITTER, true));
				}
			}
			
			GlobalVariablesManager.getInstance().set(BATHIN_ALIVE_VAR, true);
		}, startDelay, 604800000); // 7 days
		
		ThreadPool.scheduleAtFixedRate(() ->
		{
			instance.openCloseDoor(DOOR, false);
			for (Player nearby : instance.getPlayers())
			{
				if (nearby.isInsideRadius3D(-147656, 10993, -9418, 10000))
				{
					nearby.sendPacket(new OnEventTrigger(DOOR_EMITTER, false));
					nearby.teleToLocation(-147643, 21372, -9398, instance);
				}
			}
		}, startDelayDoor, 604800000); // 7 days
		
		instance.openCloseDoor(DOOR, (calendarTimeBathin <= currentTime) && (currentTime <= calendarTimeSeal) && GlobalVariablesManager.getInstance().getBoolean(BATHIN_ALIVE_VAR, true));
	}
	
	@Override
	public void onEnterZone(Creature creature, ZoneType zone)
	{
		if (creature.isPlayer() && creature.isInInstance())
		{
			final Player player = creature.asPlayer();
			if (zone.getId() == BROADCAST_ZONE.getId())
			{
				if (player.getInstanceWorld().getDoor(DOOR).isOpen())
				{
					player.sendPacket(new OnEventTrigger(DOOR_EMITTER, false));
				}
				else
				{
					player.sendPacket(new OnEventTrigger(DOOR_EMITTER, true));
				}
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (killer.isInInstance())
		{
			final Instance instance = killer.getInstanceWorld();
			GlobalVariablesManager.getInstance().set(BATHIN_ALIVE_VAR, false);
			ThreadPool.schedule(() ->
			{
				instance.openCloseDoor(DOOR, false);
				for (Player player : instance.getPlayers())
				{
					if (player.isInsideRadius3D(-147656, 10993, -9418, 2000))
					{
						player.teleToLocation(-147643, 21372, -9398, instance);
					}
				}
			}, 300000);
		}
	}
	
	public static void main(String[] args)
	{
		new Bathin();
	}
}
