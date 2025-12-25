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
package quests.Q21017_FireSmell2;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.conquest.OnConquestFlowerCollect;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestDialogType;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.newquestdata.NewQuest;
import org.l2jmobius.gameserver.model.script.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestDialog;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestNotification;

import quests.Q21018_FireSmell3.Q21018_FireSmell3;

/**
 * @author CostyKiller
 */
public class Q21017_FireSmell2 extends Quest
{
	private static final int QUEST_ID = 21017;
	
	// Flowers
	private static final int LIFE_FLOWER = 34656;
	private static final int POWER_FLOWER = 34657;
	
	// Item
	private static final int BRIGHT_SCARLET_PETAL = 82662;
	
	public Q21017_FireSmell2()
	{
		super(QUEST_ID);
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
					Containers.Global().addListener(new ConsumerEventListener(player, EventType.ON_CONQUEST_FLOWER_COLLECT, (OnConquestFlowerCollect eventListener) -> onConquestFlowerCollect(eventListener), this));
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
					final QuestState nextQuestState = player.getQuestState(Q21018_FireSmell3.class.getSimpleName());
					if (nextQuestState == null)
					{
						player.sendPacket(new ExQuestDialog(21018, QuestDialogType.ACCEPT));
					}
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
	
	@RegisterEvent(EventType.ON_CONQUEST_FLOWER_COLLECT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	@Id(LIFE_FLOWER)
	@Id(POWER_FLOWER)
	private void onConquestFlowerCollect(OnConquestFlowerCollect event)
	{
		final Player player = event.getPlayer();
		if ((event.getNpcId() == LIFE_FLOWER) || (event.getNpcId() == POWER_FLOWER))
		{
			final QuestState questState = getQuestState(player, false);
			if ((questState != null) && questState.isCond(QuestCondType.STARTED))
			{
				final NewQuest data = getQuestData();
				final int itemCount = (int) getQuestItemsCount(player, BRIGHT_SCARLET_PETAL);
				if (itemCount < data.getGoal().getCount())
				{
					giveItems(player, BRIGHT_SCARLET_PETAL, 1);
					final int newItemCount = (int) getQuestItemsCount(player, BRIGHT_SCARLET_PETAL);
					questState.setCount(newItemCount);
				}
				
				if (questState.getCount() >= data.getGoal().getCount())
				{
					questState.setCond(QuestCondType.DONE);
					player.sendPacket(new ExQuestNotification(questState));
				}
			}
		}
	}
}
