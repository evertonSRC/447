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
package quests.Q10015_PathOfDestinyProving;

import org.l2jmobius.gameserver.data.enums.CategoryType;
import org.l2jmobius.gameserver.data.xml.CategoryData;
import org.l2jmobius.gameserver.data.xml.TeleportListData;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLevelChanged;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerProfessionChange;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestDialogType;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.newquestdata.NewQuestLocation;
import org.l2jmobius.gameserver.model.script.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.serverpackets.classchange.ExClassChangeSetAlarm;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestDialog;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestNotification;

import quests.Q10016_ChangedSpirits.Q10016_ChangedSpirits;

/**
 * @author Stayway, Mobius
 */
public class Q10015_PathOfDestinyProving extends Quest
{
	// Class change rewards
	private static final int SS_R = 33780;
	private static final int BSS_R = 33794;
	private static final int BOX_R_HEAVY = 46924;
	private static final int BOX_R_LIGHT = 46925;
	private static final int BOX_R_ROBE = 46926;
	private static final int WEAPON_SWORD_R = 47008;
	private static final int WEAPON_GSWORD_R = 47009;
	private static final int WEAPON_BLUNT_R = 47010;
	private static final int WEAPON_FIST_R = 47011;
	private static final int WEAPON_SPEAR_R = 47012;
	private static final int WEAPON_BOW_R = 47013;
	private static final int WEAPON_DUALDAGGER_R = 47019;
	private static final int WEAPON_STAFF_R = 47017;
	private static final int WEAPON_DUALSWORD_R = 47018;
	private static final int WEAPON_CROSSBOW_R = 47014;
	private static final int WEAPON_BUSTER_R = 47015;
	private static final int WEAPON_CASTER_R = 47016;
	private static final int WEAPON_SIGIL_R = 47037;
	private static final int ORICHALCUM_BOLT_R = 19443;
	private static final int ORICHALCUM_ARROW_R = 18550;
	
	private static final int QUEST_ID = 10015;
	
	public Q10015_PathOfDestinyProving()
	{
		super(QUEST_ID);
		addItemTalkId(82942); // 2nd Class Change Token
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
					giveItems(player, 82942, 1); // 2nd Class Change Token
					
					if (CategoryData.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, player.getPlayerClass().getId()))
					{
						player.sendPacket(ExClassChangeSetAlarm.STATIC_PACKET);
					}
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
					
					final QuestState nextQuestState = player.getQuestState(Q10016_ChangedSpirits.class.getSimpleName());
					if (nextQuestState == null)
					{
						player.sendPacket(new ExQuestDialog(10016, QuestDialogType.ACCEPT));
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
	
	@RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerProfessionChange(OnPlayerProfessionChange event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final QuestState questState = getQuestState(player, false);
		if ((questState != null) && !questState.isCompleted())
		{
			questState.setCount(getQuestData().getGoal().getCount());
			questState.setCond(QuestCondType.DONE);
			player.sendPacket(new ExQuestNotification(questState));
			
			giveItems(player, SS_R, 5000);
			giveItems(player, BSS_R, 5000);
			switch (player.getPlayerClass())
			{
				case WARLORD:
				{
					giveItems(player, BOX_R_HEAVY, 1);
					giveItems(player, WEAPON_SPEAR_R, 1);
					break;
				}
				case GLADIATOR:
				case WARCRYER:
				case PROPHET:
				case BLADEDANCER:
				{
					giveItems(player, BOX_R_HEAVY, 1);
					giveItems(player, WEAPON_DUALSWORD_R, 1);
					break;
				}
				case PALADIN:
				case DARK_AVENGER:
				case DEATH_BERSERKER:
				case TEMPLE_KNIGHT:
				case SWORDSINGER:
				case SHILLIEN_KNIGHT:
				case OVERLORD:
				{
					giveItems(player, BOX_R_HEAVY, 1);
					giveItems(player, WEAPON_SWORD_R, 1);
					giveItems(player, WEAPON_SIGIL_R, 1);
					break;
				}
				case TREASURE_HUNTER:
				case PLAINS_WALKER:
				case ABYSS_WALKER:
				case BOUNTY_HUNTER:
				{
					giveItems(player, BOX_R_LIGHT, 1);
					giveItems(player, WEAPON_DUALDAGGER_R, 1);
					break;
				}
				case HAWKEYE:
				case SILVER_RANGER:
				case PHANTOM_RANGER:
				{
					giveItems(player, BOX_R_LIGHT, 1);
					giveItems(player, WEAPON_BOW_R, 1);
					giveItems(player, ORICHALCUM_ARROW_R, 20000);
					break;
				}
				case SORCERER:
				case NECROMANCER:
				case SPELLSINGER:
				case SPELLHOWLER:
				case MALE_SOULBREAKER:
				case FEMALE_SOULBREAKER:
				{
					giveItems(player, BOX_R_ROBE, 1);
					giveItems(player, WEAPON_BUSTER_R, 1);
					giveItems(player, WEAPON_SIGIL_R, 1);
					break;
				}
				case WARLOCK:
				case ELEMENTAL_SUMMONER:
				case PHANTOM_SUMMONER:
				{
					giveItems(player, BOX_R_LIGHT, 1);
					giveItems(player, WEAPON_STAFF_R, 1);
					break;
				}
				case BISHOP:
				case ELDER:
				case SHILLIEN_ELDER:
				{
					giveItems(player, BOX_R_ROBE, 1);
					giveItems(player, WEAPON_CASTER_R, 1);
					giveItems(player, WEAPON_SIGIL_R, 1);
					break;
				}
				case WARSMITH:
				{
					giveItems(player, BOX_R_HEAVY, 1);
					giveItems(player, WEAPON_BLUNT_R, 1);
					giveItems(player, WEAPON_SIGIL_R, 1);
					break;
				}
				case DESTROYER:
				case BERSERKER:
				{
					giveItems(player, BOX_R_HEAVY, 1);
					giveItems(player, WEAPON_GSWORD_R, 1);
					break;
				}
				case TYRANT:
				{
					giveItems(player, BOX_R_HEAVY, 1);
					giveItems(player, WEAPON_FIST_R, 1);
					break;
				}
				case ARBALESTER:
				{
					giveItems(player, BOX_R_LIGHT, 1);
					giveItems(player, ORICHALCUM_BOLT_R, 20000);
					giveItems(player, WEAPON_CROSSBOW_R, 1);
					break;
				}
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLevelChange(OnPlayerLevelChanged event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final QuestState questState = getQuestState(player, false);
		if (questState == null)
		{
			if (canStartQuest(player))
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.ACCEPT));
			}
		}
		else if (!questState.isCompleted() && (CategoryData.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, player.getPlayerClass().getId())))
		{
			player.sendPacket(ExClassChangeSetAlarm.STATIC_PACKET);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final QuestState questState = getQuestState(player, false);
		if ((questState != null) && !questState.isCompleted() && (CategoryData.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, player.getPlayerClass().getId())))
		{
			player.sendPacket(ExClassChangeSetAlarm.STATIC_PACKET);
		}
	}
}
