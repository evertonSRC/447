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
package ai.areas.Rune.Roiental;

import org.l2jmobius.gameserver.managers.InstanceManager;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanAccess;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * Roiental AI.
 * @author CostyKiller
 */
public class Roiental extends Script
{
	// NPCs
	private static final int ROIENTAL = 34571;
	
	// Misc
	private static final int TOH_GB_TEMPLATE_ID = 307; // Throne of Heroes - Goldberg
	private static final int TOH_MR_TEMPLATE_ID = 308; // Throne of Heroes - Mary Reed
	private static final int TOH_TA_TEMPLATE_ID = 309; // Throne of Heroes - Tauti
	private static final int MIN_LVL = 110;
	private static final int CLAN_MIN_LVL_GB = 7;
	private static final int CLAN_MIN_LVL_MR = 10;
	private static final int CLAN_MIN_LVL_TA = 13;
	
	private Roiental()
	{
		addStartNpc(ROIENTAL);
		addFirstTalkId(ROIENTAL);
		addTalkId(ROIENTAL);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final Clan clan = player.getClan();
		if (event.equals("enterGoldberg"))
		{
			if (player.getLevel() < MIN_LVL)
			{
				htmltext = "Roiental-NoLevel.html";
			}
			else if (clan == null)
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_3);
			}
			else if ((clan.getLevel() < CLAN_MIN_LVL_GB))
			{
				htmltext = "Roiental-03a.html";
			}
			else if (clan.getVariables().hasVariable("TOH_GOLDBERG_DONE"))
			{
				htmltext = "Roiental-AlreadyDone.html";
			}
			else if (!player.hasAccess(ClanAccess.THRONE_OF_HEROES))
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			}
			else
			{
				htmltext = "Roiental-01a.html";
			}
		}
		
		if (event.equals("enterMaryReed"))
		{
			if (player.getLevel() < MIN_LVL)
			{
				htmltext = "Roiental-NoLevel.html";
			}
			else if (clan == null)
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_3);
			}
			else if ((clan.getLevel() < CLAN_MIN_LVL_MR))
			{
				htmltext = "Roiental-03b.html";
			}
			else if (clan.getVariables().hasVariable("TOH_MARYREED_DONE"))
			{
				htmltext = "Roiental-AlreadyDone.html";
			}
			else if (!player.hasAccess(ClanAccess.THRONE_OF_HEROES))
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			}
			else
			{
				htmltext = "Roiental-01b.html";
			}
		}
		
		if (event.equals("enterTauti"))
		{
			if (player.getLevel() < MIN_LVL)
			{
				htmltext = "Roiental-NoLevel.html";
			}
			else if (clan == null)
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_3);
			}
			else if ((clan.getLevel() < CLAN_MIN_LVL_TA))
			{
				htmltext = "Roiental-03c.html";
			}
			else if (clan.getVariables().hasVariable("TOH_TAUTI_DONE"))
			{
				htmltext = "Roiental-AlreadyDone.html";
			}
			else if (!player.hasAccess(ClanAccess.THRONE_OF_HEROES))
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			}
			else
			{
				htmltext = "Roiental-01c.html";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		final Instance instance = InstanceManager.getInstance().getPlayerInstance(player, false);
		if ((instance != null) && ((instance.getTemplateId() == TOH_GB_TEMPLATE_ID)))
		{
			htmltext = "Roiental-02a.html";
		}
		else if ((instance != null) && ((instance.getTemplateId() == TOH_MR_TEMPLATE_ID)))
		{
			htmltext = "Roiental-02b.html";
		}
		else if ((instance != null) && ((instance.getTemplateId() == TOH_TA_TEMPLATE_ID)))
		{
			htmltext = "Roiental-02c.html";
		}
		else
		{
			htmltext = "Roiental-01.html";
		}
		
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Roiental();
	}
}
