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
package quests.Q10518_ExaltedOneWhoFacesTheLimit;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.xml.TeleportListData;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestDialogType;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.newquestdata.NewQuest;
import org.l2jmobius.gameserver.model.script.newquestdata.NewQuestLocation;
import org.l2jmobius.gameserver.model.script.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestDialog;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestNotification;

import quests.Q10519_LevelUpTo101.Q10519_LevelUpTo101;

/**
 * @author Stayway
 */
public class Q10518_ExaltedOneWhoFacesTheLimit extends Quest
{
	private static final int QUEST_ID = 10518;
	
	// Maybe missing Id´s Check in Retail.
	// @formatter:off
	private static final int[] MONSTERS = 
	{
		20936,20937,20938,20939,20940,20941,20942,20943,23354,23355,
		23356,23357,23360,23487,23488,23491,23492,23493,23494,23495,
		23496,23497,23498,23499,23500,23501,23502,23503,23811,23812,
		23813,23814,23815,23834,23835,23836,23837,23838,23839,24305,
		24306,24307,24317,24318,24321,24322,24323,24324,24325,24326,
		24329,24373,24374,24377,24378,24379,24411,24412,24413,24414,
		24415,24416,24417,24418,24419,24421,24422,24423,24424,24425,
		24445,24446,24447,24448,24449,24450,24461,24462,24463,24464,
		24465,24466,24486,24487,24488,24489,24490,24492,24493,24494,
		24495,24496,24497,24498,24501,24502,24503,24504,24506,24507,
		24508,24509,24511,24512,24513,24514,24515,24520,24521,24522,
		24523,24577,24578,24579,24585,24586,24606,24607,24608,24609,
		24610,24611,24621,24622,24623,24624,24631,24632,24633,24634,
		24635,24636,24637,24638,24639,24640,24641,24642,24643,24644,
		24649,24650,24652,24653,24654,24655,24656,24657,24664,24665,
		24666,24667,24673,24674,24675,24676,24677,24678,24679,24843,
		24844,24845,24846,24847,24848,24876,24877,24878,24879,24880,
		24881,24882,24883,24884,24885,24886,24930,24931,24932,24933,
		24934,24935,24937,24938,24961,24962,24963,24964,24965,24966,
		24967,24968,24969,
	};
	// @formatter:on
	public Q10518_ExaltedOneWhoFacesTheLimit()
	{
		super(QUEST_ID);
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
				if (!questState.isStarted() && !questState.isCompleted())
				{
					questState.startQuest();
					giveStoryBuffReward(player);
				}
				break;
			}
			case "TELEPORT":
			{
				QuestState questState = getQuestState(player, false);
				if (questState == null)
				{
					if (!canStartQuest(player))
					{
						break;
					}
					
					questState = getQuestState(player, true);
					
					final NewQuestLocation questLocation = getQuestData().getLocation();
					if (questLocation.getStartLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getStartLocationId()).getLocation();
						if (teleportToQuestLocation(player, location))
						{
							questState.setCond(QuestCondType.ACT);
							sendAcceptDialog(player);
						}
					}
					break;
				}
				
				final NewQuestLocation questLocation = getQuestData().getLocation();
				if (questState.isCond(QuestCondType.STARTED))
				{
					if (questLocation.getQuestLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getQuestLocationId()).getLocation();
						if (teleportToQuestLocation(player, location) && (questLocation.getQuestLocationId() == questLocation.getEndLocationId()))
						{
							questState.setCond(QuestCondType.DONE);
							sendEndDialog(player);
						}
					}
				}
				else if (questState.isCond(QuestCondType.DONE) && !questState.isCompleted())
				{
					if (questLocation.getEndLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getEndLocationId()).getLocation();
						if (teleportToQuestLocation(player, location))
						{
							sendEndDialog(player);
						}
					}
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
					
					final QuestState nextQuestState = player.getQuestState(Q10519_LevelUpTo101.class.getSimpleName());
					if (nextQuestState == null)
					{
						player.sendPacket(new ExQuestDialog(10519, QuestDialogType.ACCEPT));
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
