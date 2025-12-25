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
package ai.others.ValakasTeleporters;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.gameserver.data.xml.DoorData;
import org.l2jmobius.gameserver.managers.GrandBossManager;
import org.l2jmobius.gameserver.managers.ScriptManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.GrandBoss;
import org.l2jmobius.gameserver.model.groups.CommandChannel;
import org.l2jmobius.gameserver.model.script.Script;

import ai.bosses.Valakas.Valakas;

/**
 * Valakas Teleporters AI.
 * @author Notorion
 */
public class ValakasTeleporters extends Script
{
	// NPCs
	private static final int KLEIN = 31540;
	private static final int HEART_OF_VOLCANO = 31385;
	private static final int TELEPORT_CUBIC = 31759;
	private static final int GATEKEEPER_1 = 31384;
	private static final int GATEKEEPER_2 = 31686;
	private static final int GATEKEEPER_3 = 31687;
	
	// Locations
	private static final Location ENTER_HALL_OF_FLAMES = new Location(183813, -115157, -3303);
	private static final Location TELEPORT_INTO_VALAKAS_LAIR = new Location(204328, -111874, 70);
	private static final Location TELEPORT_OUT_OF_VALAKAS_LAIR = new Location(150037, -57720, -2976);
	
	// Boss ID
	private static final int VALAKAS_ID = 29028;
	
	// Status
	private static final int DORMANT = 0;
	private static final int WAITING = 1;
	private static final int FIGHTING = 2;
	private static final int DEAD = 3;
	
	// Configs
	private static final int MIN_CC_MEMBERS = 49;
	private static final int MAX_CC_MEMBERS = 200;
	private static final int CHECK_RADIUS = 800;
	private static final int MIN_LEVEL = 105; // Version Fafurion; 19 february 2019
	
	// Time for Valakas to appear (Minutes)
	private static final int VALAKAS_WAIT_TIME_MINUTES = 20;
	
	// Lock for synchronization
	private static final Object ENTRY_LOCK = new Object();
	
	public ValakasTeleporters()
	{
		super();
		addStartNpc(KLEIN, HEART_OF_VOLCANO, TELEPORT_CUBIC);
		addTalkId(KLEIN, HEART_OF_VOLCANO, TELEPORT_CUBIC, GATEKEEPER_1, GATEKEEPER_2, GATEKEEPER_3);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equals("enter_hall"))
		{
			// Prelude of War: No item check needed. Direct teleport.
			player.teleToLocation(ENTER_HALL_OF_FLAMES);
			return null;
		}
		
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = "";
		
		switch (npc.getId())
		{
			case KLEIN: // 31540
			{
				// Prelude of War: Always allows entry without item check.
				htmltext = "31540-01.htm";
				break;
			}
			case HEART_OF_VOLCANO: // 31385
			{
				return tryEnterLair(npc, player);
			}
			case TELEPORT_CUBIC: // 31759
			{
				player.teleToLocation(TELEPORT_OUT_OF_VALAKAS_LAIR.getX() + getRandom(500), TELEPORT_OUT_OF_VALAKAS_LAIR.getY() + getRandom(500), TELEPORT_OUT_OF_VALAKAS_LAIR.getZ());
				break;
			}
			case GATEKEEPER_1: // 31384
			{
				DoorData.getInstance().getDoor(25140004).openMe();
				break;
			}
			case GATEKEEPER_2: // 31686
			{
				DoorData.getInstance().getDoor(25140005).openMe();
				break;
			}
			case GATEKEEPER_3: // 31687
			{
				DoorData.getInstance().getDoor(25140006).openMe();
				break;
			}
		}
		
		return htmltext;
	}
	
	/**
	 * Strict entry logic for Valakas Lair. Only the first Command Channel that clicks can enter (if status == DORMANT).
	 * @param npc the NPC the player is talking to.
	 * @param player the player attempting to enter (Command Channel Leader).
	 * @return the name of the HTML file to be displayed or null if the teleport occurs.
	 */
	private String tryEnterLair(Npc npc, Player player)
	{
		synchronized (ENTRY_LOCK)
		{
			final int status = GrandBossManager.getInstance().getStatus(VALAKAS_ID);
			if (status == DEAD)
			{
				return "31385-04.htm";
			}
			
			// (WAITING or FIGHTING).
			if ((status == WAITING) || (status == FIGHTING))
			{
				return "31385-04.htm";
			}
			
			if (status != DORMANT)
			{
				return "31385-04.htm";
			}
			
			final CommandChannel cc = player.getCommandChannel();
			if (cc == null)
			{
				return "31385-04.htm";
			}
			
			if (!cc.getLeader().equals(player))
			{
				player.sendMessage("Only the Command Channel Leader can attempt entry.");
				return "31385-04.htm";
			}
			
			final List<Player> validMembers = new ArrayList<>();
			for (Player member : cc.getMembers())
			{
				if ((member == null) || !member.isOnline())
				{
					continue;
				}
				
				if (!member.isInsideRadius3D(npc, CHECK_RADIUS))
				{
					player.sendMessage("Player " + member.getName() + " must come close to the Heart of Volcano to enter.");
					return "31385-04.htm";
				}
				
				if (member.getLevel() < MIN_LEVEL)
				{
					player.sendMessage("Player " + member.getName() + " is too low level (Required: " + MIN_LEVEL + ").");
					return "31385-04.htm";
				}
				
				validMembers.add(member);
			}
			
			if (validMembers.size() < MIN_CC_MEMBERS)
			{
				player.sendMessage("Command Channel needs at least " + MIN_CC_MEMBERS + " members.");
				return "31385-04.htm";
			}
			
			if (validMembers.size() > MAX_CC_MEMBERS)
			{
				player.sendMessage("The maximum number of players exceeded the limit (" + MAX_CC_MEMBERS + ").");
				return "31385-04.htm";
			}
			
			GrandBossManager.getInstance().setStatus(VALAKAS_ID, WAITING);
			
			final Script valakasAi = (Script) ScriptManager.getInstance().getScript(Valakas.class.getSimpleName());
			if (valakasAi != null)
			{
				final GrandBoss valakas = GrandBossManager.getInstance().getBoss(VALAKAS_ID);
				final long waitTimeMillis = VALAKAS_WAIT_TIME_MINUTES * 60000L;
				valakasAi.startQuestTimer("beginning", waitTimeMillis, valakas, null);
			}
			
			for (Player member : validMembers)
			{
				member.teleToLocation(TELEPORT_INTO_VALAKAS_LAIR, true);
			}
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new ValakasTeleporters();
	}
}