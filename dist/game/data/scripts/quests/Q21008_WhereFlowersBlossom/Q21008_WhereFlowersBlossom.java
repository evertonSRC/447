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
package quests.Q21008_WhereFlowersBlossom;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestDialogType;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.newquestdata.NewQuest;
import org.l2jmobius.gameserver.model.script.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestDialog;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestNotification;

/**
 * @author CostyKiller
 */
public class Q21008_WhereFlowersBlossom extends Quest
{
	private static final int QUEST_ID = 21008;
	
	// Monsters
	private static final int[] MONSTERS =
	{
		19818, // Soul Flower - Asa Area 1 (Lv. 116)
		19819, // Soul Flower - Asa Area 2 (Lv. 120)
		19820, // Soul Flower - Asa Area 3 (Lv. 124)
		19824, // Soul Flower - Anima Area 1 (Lv. 116)
		19825, // Soul Flower - Anima Area 2 (Lv. 120)
		19826, // Soul Flower - Anima Area 3 (Lv. 124)
		19821, // Soul Flower - Nox Area 1 (Lv. 116)
		19822, // Soul Flower - Nox Area 2 (Lv. 120)
		19823, // Soul Flower - Nox Area 3 (Lv. 124)
		19840, // Soul Flower - Vita (Lv. 124)
		19841, // Soul Flower - Ignis (Lv. 128)
	};
	
	public Q21008_WhereFlowersBlossom()
	{
		super(QUEST_ID);
		addKillId(MONSTERS);
		setType(2);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "ACCEPT":
			{
				if (!canStartQuest(player))
				{
					break;
				}
				
				final QuestState questState = getQuestState(player, true);
				if (!questState.isStarted() && !questState.isCompleted())
				{
					questState.startQuest();
				}
				break;
			}
			case "COMPLETE":
			{
				final QuestState questState = getQuestState(player, false);
				if (questState == null)
				{
					break;
				}
				
				if (questState.isCond(QuestCondType.DONE) && !questState.isCompleted())
				{
					questState.exitQuest(false, true);
					rewardPlayer(player);
				}
				break;
			}
		}
		
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final QuestState questState = getQuestState(player, false);
		if ((questState != null) && !questState.isCompleted())
		{
			if (questState.isCond(QuestCondType.STARTED))
			{
				if (questState.isStarted())
				{
					questState.setCount(getQuestData().getGoal().getCount());
					questState.setCond(QuestCondType.DONE);
					player.sendPacket(new ExQuestDialog(questState.getQuest().getId(), QuestDialogType.END));
				}
			}
			else if (questState.isCond(QuestCondType.DONE))
			{
				player.sendPacket(new ExQuestDialog(questState.getQuest().getId(), QuestDialogType.END));
			}
		}
		
		npc.showChatWindow(player);
		return null;
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Party party = killer.getParty();
		if (party != null) // Multiple party members.
		{
			
			for (Player member : party.getMembers())
			{
				if (member.calculateDistance3D(npc) < PlayerConfig.ALT_PARTY_RANGE)
				{
					final QuestState questState = getQuestState(member, false);
					if ((questState != null) && questState.isCond(QuestCondType.STARTED))
					{
						final NewQuest data = getQuestData();
						if (data.getGoal().getItemId() > 0)
						{
							final int itemCount = (int) getQuestItemsCount(member, data.getGoal().getItemId());
							if (itemCount < data.getGoal().getCount())
							{
								giveItems(member, data.getGoal().getItemId(), 1);
								final int newItemCount = (int) getQuestItemsCount(member, data.getGoal().getItemId());
								questState.setCount(newItemCount);
							}
						}
						else
						{
							final int currentCount = questState.getCount();
							if (currentCount < data.getGoal().getCount())
							{
								questState.setCount(currentCount + 1);
							}
						}
						
						if (questState.getCount() >= data.getGoal().getCount())
						{
							questState.setCond(QuestCondType.DONE);
							member.sendPacket(new ExQuestNotification(questState));
						}
					}
				}
			}
		}
		else // Single player.
		{
			final QuestState questState = getQuestState(killer, false);
			if ((questState != null) && questState.isCond(QuestCondType.STARTED))
			{
				
				final NewQuest data = getQuestData();
				if (data.getGoal().getItemId() > 0)
				{
					final int itemCount = (int) getQuestItemsCount(killer, data.getGoal().getItemId());
					if (itemCount < data.getGoal().getCount())
					{
						giveItems(killer, data.getGoal().getItemId(), 1);
						final int newItemCount = (int) getQuestItemsCount(killer, data.getGoal().getItemId());
						questState.setCount(newItemCount);
					}
				}
				else
				{
					final int currentCount = questState.getCount();
					if (currentCount < data.getGoal().getCount())
					{
						questState.setCount(currentCount + 1);
					}
				}
				
				if (questState.getCount() >= data.getGoal().getCount())
				{
					questState.setCond(QuestCondType.DONE);
					killer.sendPacket(new ExQuestNotification(questState));
				}
			}
			
		}
	}
}
