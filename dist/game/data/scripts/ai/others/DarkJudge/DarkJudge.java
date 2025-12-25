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
package ai.others.DarkJudge;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * Dark Judge AI.
 * @author St3eT, Mobius
 */
public class DarkJudge extends Script
{
	// NPC
	private static final int DARK_JUDGE = 30981;
	
	// Item
	private static final int SCROLL_SIN_EATER = 82965;
	
	private DarkJudge()
	{
		addStartNpc(DARK_JUDGE);
		addTalkId(DARK_JUDGE);
		addFirstTalkId(DARK_JUDGE);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "30981.html":
			case "30981-03.html":
			{
				htmltext = event;
				break;
			}
			case "weakenBreath":
			{
				if (player.getShilensBreathDebuffLevel() >= 3)
				{
					player.setShilensBreathDebuffLevel(2);
					htmltext = "30981-01.html";
				}
				else
				{
					htmltext = "30981-02.html";
				}
				break;
			}
			case "trade":
			{
				if ((player.getAdena() < 1000000))
				{
					player.sendPacket(SystemMessageId.NOT_ENOUGH_ADENA_2);
					return null;
				}
				
				if (getQuestItemsCount(player, SCROLL_SIN_EATER) > 0)
				{
					player.sendPacket(SystemMessageId.NO_MORE_ITEMS_CAN_BE_REGISTERED);
					return null;
				}
				
				if (player.getReputation() < 0)
				{
					takeItems(player, Inventory.ADENA_ID, 1000000);
					giveItems(player, SCROLL_SIN_EATER, 1);
					return null;
				}
				
				htmltext = "30981-03.html";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if ((player.getLevel() < 85) || (player.getReputation() >= 0))
		{
			return "30981-04.html";
		}
		
		return "30981.html";
	}
	
	public static void main(String[] args)
	{
		new DarkJudge();
	}
}
