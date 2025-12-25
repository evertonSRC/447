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
package ai.others.EinhasadStore;

import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.data.xml.MultisellData;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerBypass;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.network.serverpackets.ExPremiumManagerShowHtml;

/**
 * @author Index
 */
public class EinhasadStore extends Script
{
	// NPC
	private static final int MERCHANT = 34487;
	
	// Multisells
	private static final int JEWELS_STONE = 34487001;
	private static final int ACCESSORIES = 34487002;
	private static final int SCROLLS = 34487003;
	private static final int ENHANCEMENT = 34487004;
	private static final int OTHER = 34487005;
	private static final int CLOAK = 34487006;
	
	// Others
	private static final String COMMAND_BYPASS = "Quest EinhasadStore ";
	
	private EinhasadStore()
	{
		addStartNpc(MERCHANT);
		addFirstTalkId(MERCHANT);
		addTalkId(MERCHANT);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = null;
		switch (event)
		{
			
			case "back":
			{
				player.sendPacket(new ExPremiumManagerShowHtml(HtmCache.getInstance().getHtm(player, "data/scripts/ai/others/EinhasadStore/34487.html")));
				break;
			}
			// Bypass
			case "Chat_Jewell_Stones":
			{
				MultisellData.getInstance().separateAndSend(JEWELS_STONE, player, null, false);
				break;
			}
			case "Chat_Accessories":
			{
				MultisellData.getInstance().separateAndSend(ACCESSORIES, player, null, false);
				break;
			}
			case "Chat_Scrolls":
			{
				MultisellData.getInstance().separateAndSend(SCROLLS, player, null, false);
				break;
			}
			case "Chat_Enhancement":
			{
				MultisellData.getInstance().separateAndSend(ENHANCEMENT, player, null, false);
				break;
			}
			case "Chat_Others":
			{
				MultisellData.getInstance().separateAndSend(OTHER, player, null, false);
				break;
			}
			case "Chat_Cloak":
			{
				MultisellData.getInstance().separateAndSend(CLOAK, player, null, false);
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		player.sendPacket(new ExPremiumManagerShowHtml(HtmCache.getInstance().getHtm(player, "data/scripts/ai/others/EinhasadStore/34487.html")));
		return null;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerBypass(OnPlayerBypass event)
	{
		final Player player = event.getPlayer();
		if (event.getCommand().startsWith(COMMAND_BYPASS))
		{
			notifyEvent(event.getCommand().replace(COMMAND_BYPASS, ""), null, player);
		}
	}
	
	public static void main(String[] args)
	{
		new EinhasadStore();
	}
}
