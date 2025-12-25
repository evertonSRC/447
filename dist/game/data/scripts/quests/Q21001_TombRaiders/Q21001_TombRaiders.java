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
package quests.Q21001_TombRaiders;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.xml.TimedHuntingZoneData;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestDialogType;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.QuestType;
import org.l2jmobius.gameserver.model.script.State;
import org.l2jmobius.gameserver.model.script.newquestdata.NewQuest;
import org.l2jmobius.gameserver.model.script.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestDialog;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestNotification;

/**
 * @author CostyKiller
 */
public class Q21001_TombRaiders extends Quest
{
	private static final int QUEST_ID = 21001;
	
	// Monsters
	private static final int[] MONSTERS =
	{
		24580, // Tomb Guardian
		24581, // Tomb Raider
		24582, // Tomb Patrol
		24583, // Tomb Soultaker
		24584, // Tomb Watcher
	};
	
	// Zone
	private static final int SESSION_ZONE_ID = 12; // Session Zone - Imperial Tomb
	
	public Q21001_TombRaiders()
	{
		super(QUEST_ID);
		addInstanceEnterId(TimedHuntingZoneData.getInstance().getHuntingZone(SESSION_ZONE_ID).getInstanceId());
		addKillId(MONSTERS);
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
				if (!questState.isStarted() || questState.isNowAvailable())
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
					questState.exitQuest(QuestType.DAILY, true);
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
			if (questState.isCond(QuestCondType.NONE))
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.START));
			}
			else if (questState.isCond(QuestCondType.DONE))
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.END));
			}
		}
		
		npc.showChatWindow(player);
		return null;
	}
	
	@Override
	public void onInstanceEnter(Player player, Instance instance)
	{
		final QuestState questState = getQuestState(player, true);
		if (questState != null)
		{
			if (questState.isCreated())
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.ACCEPT));
			}
			else if (!questState.isStarted() && questState.isNowAvailable())
			{
				questState.setState(State.CREATED);
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.ACCEPT));
			}
		}
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
