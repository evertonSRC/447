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
package ai.areas.AteliaRefinery.Leona;

import java.util.List;

import org.l2jmobius.gameserver.config.GrandBossConfig;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.managers.GrandBossManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Liamxroy
 */
public class Leona extends Script
{
	// NPCs
	private static final int LEONA = 34426;
	private static final int ETINA_RAID = 29318;
	
	// Location
	private static final Location ENTER_LOC = new Location(-245778, 181088, 2860);
	private static final Location REFINERY = new Location(-59328, 52624, -8608);
	
	public Leona()
	{
		addFirstTalkId(LEONA);
		addTalkId(LEONA);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equals("teleport"))
		{
			final int status = GrandBossManager.getInstance().getStatus(ETINA_RAID);
			if (player.isGM())
			{
				player.teleToLocation(ENTER_LOC, true);
				GrandBossManager.getInstance().setStatus(ETINA_RAID, 1);
			}
			else
			{
				if (status == 1)
				{
					return "34426-1.html";
				}
				
				if (status == 2)
				{
					return "34426-2.html";
				}
				
				if (!player.isInParty())
				{
					return "34426-3.html";
				}
				
				final Party party = player.getParty();
				final boolean isInCC = party.isInCommandChannel();
				final List<Player> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
				final boolean isPartyLeader = (isInCC) ? party.getCommandChannel().isLeader(player) : party.isLeader(player);
				if (!isPartyLeader)
				{
					return "34426-3.html";
				}
				
				if ((members.size() < GrandBossConfig.ETINA_MIN_PLAYERS) || (members.size() > GrandBossConfig.ETINA_MAX_PLAYERS))
				{
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(player, "34426-4.html"));
					packet.replace("%min%", Integer.toString(GrandBossConfig.ETINA_MIN_PLAYERS));
					packet.replace("%max%", Integer.toString(GrandBossConfig.ETINA_MAX_PLAYERS));
					player.sendPacket(packet);
					return null;
				}
				
				for (Player member : members)
				{
					if (member.getLevel() < GrandBossConfig.ETINA_MIN_PLAYER_LEVEL)
					{
						final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
						packet.setHtml(getHtm(player, "34426-5.html"));
						packet.replace("%minlvl%", Integer.toString(GrandBossConfig.ETINA_MIN_PLAYER_LEVEL));
						player.sendPacket(packet);
						return null;
					}
				}
				
				for (Player member : members)
				{
					if (member.isInsideRadius3D(npc, PlayerConfig.ALT_PARTY_RANGE))
					{
						member.teleToLocation(ENTER_LOC, false);
						GrandBossManager.getInstance().setStatus(ETINA_RAID, 1);
					}
				}
			}
		}
		else if (event.equals("tp_inner"))
		{
			player.teleToLocation(REFINERY, true);
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		// final QuestState qs = player.getQuestState(Q10890_SaviorsPathHallOfEtina.class.getSimpleName());
		String htmltext = null;
		
		// if (((qs != null) && qs.isCompleted()))
		// {
		// htmltext = "34426-0.html";
		// }
		// else
		// {
		htmltext = "34426.html";
		
		// }
		
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Leona();
	}
}
