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
package instances.FortressOfTheDead;

import org.l2jmobius.gameserver.managers.ScriptManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.enums.Movie;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.classchange.ExClassChangeSetAlarm;

import quests.Q10023_ProphesiedOne.Q10023_ProphesiedOne;
import quests.Q10123_ProphesiedOne.Q10123_ProphesiedOne;
import quests.Q10223_ProphesiedOne.Q10223_ProphesiedOne;
import quests.Q10323_ProphesiedOne.Q10323_ProphesiedOne;

/**
 * Fortress of the Dead instance zone.
 * @author Gladicek, Mobius
 */
public class FortressOfTheDead extends InstanceScript
{
	// NPCs
	private static final int VAMPIRIC_SOLDIER = 19567;
	private static final int VON_HELLMAN = 19566;
	private static final int MYSTERIOUS_WIZARD = 31522;
	private static final int KAIN_VAN_HALTER = 33979;
	
	// Location
	private static final Location VON_HELLMAN_LOC = new Location(57963, -28676, 568, 49980);
	private static final Location MYSTERIOUS_WIZARD_LOC = new Location(57982, -28645, 568);
	private static final Location KAIN_VAN_HALTER_LOC = new Location(57963, -28676, 568, 49980);
	
	// Misc
	private static final int TEMPLATE_ID = 254;
	
	public FortressOfTheDead()
	{
		super(TEMPLATE_ID);
		addStartNpc(KAIN_VAN_HALTER);
		addFirstTalkId(KAIN_VAN_HALTER);
		addTalkId(KAIN_VAN_HALTER, MYSTERIOUS_WIZARD);
		addKillId(VAMPIRIC_SOLDIER, VON_HELLMAN);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		if (event.equals("enterInstance"))
		{
			final QuestState questState = getQuestState(player);
			if ((questState != null) && !player.isInInstance())
			{
				enterInstance(player, npc, TEMPLATE_ID);
			}
		}
		else
		{
			final Instance world = npc.getInstanceWorld();
			if (isInInstance(world))
			{
				switch (event)
				{
					case "33979-01.html":
					case "33979-02.html":
					case "33979-03.html":
					case "33979-04.html":
					case "33979-05.html":
					case "33979-06.html":
					case "33979-07.html":
					case "33979-08.html":
					case "33979-09.html":
					case "33979-10.html":
					{
						htmltext = event;
						break;
					}
					case "exitInstance":
					{
						world.finishInstance(0);
						break;
					}
					case "vampire_dead":
					{
						addSpawn(VON_HELLMAN, VON_HELLMAN_LOC, false, 0, false, world.getId());
						break;
					}
					case "hellman_dead":
					{
						addSpawn(KAIN_VAN_HALTER, KAIN_VAN_HALTER_LOC, false, 0, false, world.getId());
						break;
					}
					case "spawnWizard":
					{
						if (npc.getInstanceWorld().getNpc(MYSTERIOUS_WIZARD) != null)
						{
							return null;
						}
						
						showOnScreenMsg(player, NpcStringId.TALK_TO_THE_MYSTERIOUS_WIZARD, ExShowScreenMessage.TOP_CENTER, 5000);
						final Npc wizzard = addSpawn(MYSTERIOUS_WIZARD, MYSTERIOUS_WIZARD_LOC, true, 0, false, world.getId());
						wizzard.setSummoner(player);
						wizzard.setTitle(player.getAppearance().getVisibleName());
						wizzard.broadcastInfo();
						htmltext = "33979-11.html";
						break;
					}
					case "endCinematic":
					{
						final QuestState questState = getQuestState(player);
						if (questState != null)
						{
							questState.setCond(QuestCondType.DONE);
							ScriptManager.getInstance().getScript(questState.getQuestName()).notifyEvent("COMPLETE", null, player);
							player.sendPacket(ExClassChangeSetAlarm.STATIC_PACKET);
						}
						
						world.getNpc(KAIN_VAN_HALTER).deleteMe();
						world.getNpc(MYSTERIOUS_WIZARD).deleteMe();
						playMovie(player, Movie.ERT_QUEST_B);
						startQuestTimer("exitInstance", 25000, npc, player);
						break;
					}
				}
			}
		}
		
		return htmltext;
	}
	
	@Override
	public void onKill(Npc npc, Player player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (npc.getId() == VAMPIRIC_SOLDIER)
			{
				if (world.getAliveNpcCount(VAMPIRIC_SOLDIER) == 0)
				{
					startQuestTimer("vampire_dead", 180, npc, player);
				}
			}
			else if (npc.getId() == VON_HELLMAN)
			{
				npc.deleteMe();
				playMovie(player, Movie.ERT_QUEST_A);
				startQuestTimer("hellman_dead", 8000, npc, player);
			}
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	
	{
		return "33979.html";
	}
	
	private QuestState getQuestState(Player player)
	{
		QuestState questState = player.getQuestState(Q10023_ProphesiedOne.class.getSimpleName());
		if (questState != null)
		{
			return questState.isStarted() ? questState : null;
		}
		
		questState = player.getQuestState(Q10123_ProphesiedOne.class.getSimpleName());
		if (questState != null)
		{
			return questState.isStarted() ? questState : null;
		}
		
		questState = player.getQuestState(Q10223_ProphesiedOne.class.getSimpleName());
		if (questState != null)
		{
			return questState.isStarted() ? questState : null;
		}
		
		questState = player.getQuestState(Q10323_ProphesiedOne.class.getSimpleName());
		if (questState != null)
		{
			return questState.isStarted() ? questState : null;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new FortressOfTheDead();
	}
}
