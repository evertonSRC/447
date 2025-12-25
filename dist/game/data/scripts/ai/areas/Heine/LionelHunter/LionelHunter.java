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
package ai.areas.Heine.LionelHunter;

import org.l2jmobius.gameserver.data.xml.MultisellData;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.Script;

import quests.Q10514_NobleMaterial4.Q10514_NobleMaterial4;
import quests.Q10515_NobleMaterial4.Q10515_NobleMaterial4;
import quests.Q10516_NobleMaterial4.Q10516_NobleMaterial4;
import quests.Q10517_NobleMaterial4.Q10517_NobleMaterial4;

/**
 * Lionel Hunter AI.
 * @author Stayway, CostyKiller
 */
public class LionelHunter extends Script
{
	// NPC
	private static final int NPC_LIONEL = 33907;
	
	// Multisell
	private static final int SHIELD_SIGIL = 3390708;
	
	private LionelHunter()
	{
		addStartNpc(NPC_LIONEL);
		addTalkId(NPC_LIONEL);
		addFirstTalkId(NPC_LIONEL);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33907.html":
			case "33907-02.html":
			case "33907-01.html":
			case "33907-03.html":
			{
				htmltext = event;
				break;
			}
			case "ExaltedShield":
			{
				if (player.getNobleLevel() > 0)
				{
					final QuestState qs = player.getQuestState(Q10514_NobleMaterial4.class.getSimpleName());
					final QuestState qs1 = player.getQuestState(Q10515_NobleMaterial4.class.getSimpleName());
					final QuestState qs2 = player.getQuestState(Q10516_NobleMaterial4.class.getSimpleName());
					final QuestState qs3 = player.getQuestState(Q10517_NobleMaterial4.class.getSimpleName());
					if (((qs != null) && qs.isCompleted()) || ((qs1 != null) && qs1.isCompleted()) || ((qs2 != null) && qs2.isCompleted()) || ((qs3 != null) && qs3.isCompleted()))
					{
						MultisellData.getInstance().separateAndSend(SHIELD_SIGIL, player, null, false);
						break;
					}
				}
				
				htmltext = "noreq.html";
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + ".html";
	}
	
	public static void main(String[] args)
	{
		new LionelHunter();
	}
}
