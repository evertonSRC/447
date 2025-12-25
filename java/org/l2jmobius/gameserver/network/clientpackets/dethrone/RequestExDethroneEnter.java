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
package org.l2jmobius.gameserver.network.clientpackets.dethrone;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.config.ConquestConfig;
import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.ExShowUsm;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Negrito8
 */
public class RequestExDethroneEnter extends ClientPacket
{
	// First Memory Locations
	private static final Location[] ENTRY_LOCS =
	{
		new Location(-6665, -187363, -4088), // Memory - Elf
		new Location(-10634, -181565, -5144), // Memory - Dark Elf
		new Location(-8725, -182971, -4872), // Memory - Kamael
		new Location(-14589, -179719, -2440), // Memory - Dwarf
		new Location(-16433, -178305, -2368), // Memory - Human
		new Location(-18222, -179191, -2008) // Memory - Orc
	};
	private static final Location CONQUEST_ENTER_LOC = new Location(-16630, -189326, -4005);
	
	@Override
	protected void readImpl()
	{
		// readByte(); // cDummy
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final PlayerVariables variables = player.getVariables();
		if (player.isInsideZone(ZoneId.CONQUEST))
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_WILL_BE_TAKEN_TO_THE_MAIN_SERVER_IN_3_SEC));
			player.stopMove(null);
			
			ThreadPool.schedule(() ->
			{
				Location location = new Location(146783, 25808, -2008); // Aden
				if (variables.contains(PlayerVariables.CONQUEST_ORIGIN))
				{
					final int[] loc = variables.getIntArray(PlayerVariables.CONQUEST_ORIGIN, ";");
					if ((loc != null) && (loc.length == 3))
					{
						location = new Location(loc[0], loc[1], loc[2]);
					}
					
					player.teleToLocation(location);
					variables.remove(PlayerVariables.CONQUEST_ORIGIN);
				}
				else
				{
					player.teleToLocation(location);
				}
			}, 3000);
			return;
		}
		else if (!ConquestConfig.CONQUEST_SYSTEM_ENABLED || (ConquestConfig.CONQUEST_SYSTEM_ENABLED && (GlobalVariablesManager.getInstance().hasVariable("CONQUEST_AVAILABLE") && !GlobalVariablesManager.getInstance().getBoolean("CONQUEST_AVAILABLE", false))))
		{
			player.sendPacket(SystemMessageId.THE_PATH_TO_THE_CONQUEST_WORLD_IS_CLOSED_YOU_CAN_GET_THERE_ON_MONDAYS_TUESDAYS_WEDNESDAYS_AND_THURSDAYS_FROM_10_00_TILL_14_00_AND_FROM_22_00_TILL_00_00_AND_ON_FRIDAYS_SATURDAYS_AND_SUNDAYS_FROM_20_00_TILL_01_00_OF_THE_FOLLOWING_DAY_SERVER_TIME_PVP_IS_DISABLED_FROM_20_00_TILL_22_00_FOR_2_HOURS_BECAUSE_THE_NEW_WORLD_EXPLORATION_IS_UNDER_WAY);
			return;
		}
		else if ((!player.isInsideZone(ZoneId.PEACE) || (player.isInOlympiadMode()) || (player.isFishing()) || (!player.isInventoryUnder80(false))))
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_ENTER_THE_CONQUEST_WORLD_IF_YOU_ARE_REGISTERED_OR_PARTICIPATING_IN_THE_OLYMPIAD_OR_THE_CEREMONY_OF_CHAOS_IF_YOU_HAVE_A_CURSED_SWORD_IF_YOU_ARE_FISHING_DUELING_IF_YOU_ARE_DEAD_IF_YOU_HAVE_OVERWEIGHT_OR_YOUR_INVENTORY_IS_FILLED_UP_FOR_90_OR_MORE));
			return;
		}
		else if (player.hasServitors())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_TELEPORT_TO_THE_WORLD_HUNTING_ZONE_WHILE_YOUR_SERVITOR_IS_SUMMONED));
			return;
		}
		else if (player.isMounted())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_TELEPORT_TO_THE_WORLD_HUNTING_ZONE_WHILE_RIDING_A_MOUNT));
			return;
		}
		else if (player.isCursedWeaponEquipped())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_TELEPORT_TO_THE_WORLD_HUNTING_ZONE_WHILE_OWNING_A_CURSED_WEAPON));
			return;
		}
		
		player.sendPacket(new SystemMessage(SystemMessageId.YOU_WILL_BE_TAKEN_TO_THE_WORLD_HUNTING_ZONE_IN_3_SEC));
		player.stopMove(null);
		
		// Save player location for conquest leave request.
		variables.set(PlayerVariables.CONQUEST_ORIGIN, player.getX() + ";" + player.getY() + ";" + player.getZ());
		
		ThreadPool.schedule(() ->
		{
			if (variables.hasVariable(PlayerVariables.CONQUEST_INTRO))
			{
				variables.remove(PlayerVariables.CONQUEST_INTRO);
				player.sendPacket(ExShowUsm.CONQUEST_INTRO);
				switch (player.getRace())
				{
					case ELF:
					{
						player.teleToLocation(ENTRY_LOCS[0], 0, player.getInstanceWorld());
						break;
					}
					case DARK_ELF:
					{
						player.teleToLocation(ENTRY_LOCS[1], 0, player.getInstanceWorld());
						break;
					}
					case KAMAEL:
					{
						player.teleToLocation(ENTRY_LOCS[2], 0, player.getInstanceWorld());
						break;
					}
					case DWARF:
					{
						player.teleToLocation(ENTRY_LOCS[3], 0, player.getInstanceWorld());
						break;
					}
					case HUMAN:
					case ERTHEIA:
					{
						player.teleToLocation(ENTRY_LOCS[4], 0, player.getInstanceWorld());
						break;
					}
					case ORC:
					{
						player.teleToLocation(ENTRY_LOCS[5], 0, player.getInstanceWorld());
						break;
					}
				}
			}
			else
			{
				player.teleToLocation(CONQUEST_ENTER_LOC);
			}
		}, 3000);
	}
}
