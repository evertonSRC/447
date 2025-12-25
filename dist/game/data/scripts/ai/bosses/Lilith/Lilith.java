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
package ai.bosses.Lilith;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.config.GrandBossConfig;
import org.l2jmobius.gameserver.managers.GrandBossManager;
import org.l2jmobius.gameserver.managers.MapRegionManager;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.Spawn;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.TeleportWhereType;
import org.l2jmobius.gameserver.model.actor.instance.GrandBoss;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.script.QuestTimer;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.AbnormalType;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.util.ArrayUtil;

/**
 * Lilith AI
 * @author LasTravel, NviX
 * @URL http://boards.lineage2.com/showpost.php?p=3386784&postcount=6<br>
 * @video https://www.youtube.com/watch?v=H3MuIwUjjD4
 */
public class Lilith extends Script
{
	// Status
	private static final int ALIVE = 0;
	private static final int FIGHTING = 1;
	private static final int DEAD = 2;
	
	// NPCs
	private static final int LILITH = 29336;
	private static final int EXIST_CUBIC = 31124;
	private static final int LILITH_CUBIC = 31118;
	// @formatter:off
	private static final int[] LILITH_MINIONS = {29337, 29338, 29339};
	// @formatter:on
	private static final int[] ALL_MOBS =
	{
		LILITH,
		LILITH_MINIONS[0],
		LILITH_MINIONS[1],
	};
	
	// Misc
	private static final Location ENTER_LILITH_LOC = new Location(184449, -9032, -5499);
	private static final ZoneType BOSS_ZONE = ZoneManager.getInstance().getZoneById(12005);
	private static final ZoneType PRE_LILITH_ZONE = ZoneManager.getInstance().getZoneById(12006);
	
	// Others
	private static long _lastAction;
	private static Npc _lilithBoss;
	private GrandBoss _tempLilith = null;
	
	public Lilith()
	{
		addTalkId(EXIST_CUBIC, LILITH_CUBIC);
		addStartNpc(EXIST_CUBIC, LILITH_CUBIC);
		addFirstTalkId(EXIST_CUBIC, LILITH_CUBIC);
		addAttackId(ALL_MOBS);
		addKillId(ALL_MOBS);
		addSkillSeeId(ALL_MOBS);
		
		// Unlock
		final StatSet info = GrandBossManager.getInstance().getStatSet(LILITH);
		final int status = GrandBossManager.getInstance().getStatus(LILITH);
		if (status == DEAD)
		{
			final long time = info.getLong("respawn_time") - System.currentTimeMillis();
			if (time > 0)
			{
				startQuestTimer("unlock_lilith", time, null, null);
			}
			else
			{
				_tempLilith = (GrandBoss) addSpawn(LILITH, -126920, -234182, -15563, 0, false, 0);
				GrandBossManager.getInstance().addBoss(_tempLilith);
				GrandBossManager.getInstance().setStatus(LILITH, ALIVE);
			}
		}
		else
		{
			_tempLilith = (GrandBoss) addSpawn(LILITH, -126920, -234182, -15563, 0, false, 0);
			GrandBossManager.getInstance().addBoss(_tempLilith);
			GrandBossManager.getInstance().setStatus(LILITH, ALIVE);
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "unlock_lilith":
			{
				_tempLilith = (GrandBoss) addSpawn(LILITH, -126920, -234182, -15563, 0, false, 0);
				GrandBossManager.getInstance().addBoss(_tempLilith);
				GrandBossManager.getInstance().setStatus(LILITH, ALIVE);
				break;
			}
			case "check_activity_task":
			{
				if ((_lastAction + 900000) < System.currentTimeMillis())
				{
					GrandBossManager.getInstance().setStatus(LILITH, ALIVE);
					for (Creature creature : BOSS_ZONE.getCharactersInside())
					{
						if (creature != null)
						{
							if (creature.isNpc())
							{
								creature.deleteMe();
							}
							else if (creature.isPlayer())
							{
								creature.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(creature, TeleportWhereType.TOWN));
							}
						}
					}
					
					startQuestTimer("end_lilith", 2000, null, null);
				}
				else
				{
					startQuestTimer("check_activity_task", 60000, null, null);
				}
				break;
			}
			case "cancel_timers":
			{
				QuestTimer activityTimer = getQuestTimer("check_activity_task", null, null);
				if (activityTimer != null)
				{
					activityTimer.cancel();
				}
				
				QuestTimer forceEnd = getQuestTimer("end_lilith", null, null);
				if (forceEnd != null)
				{
					forceEnd.cancel();
				}
				break;
			}
			case "end_lilith":
			{
				notifyEvent("cancel_timers", null, null);
				if (_lilithBoss != null)
				{
					_lilithBoss.deleteMe();
				}
				
				BOSS_ZONE.oustAllPlayers();
				PRE_LILITH_ZONE.oustAllPlayers();
				if (GrandBossManager.getInstance().getStatus(LILITH) != DEAD)
				{
					GrandBossManager.getInstance().setStatus(LILITH, ALIVE);
				}
				break;
			}
			case "exist":
			{
				player.teleToLocation(TeleportWhereType.TOWN);
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final int _lilithStatus = GrandBossManager.getInstance().getStatus(LILITH);
		if ((npc.getId() == LILITH_CUBIC) && (_lilithStatus > ALIVE))
		{
			return "31118-01.html";
		}
		
		if (!player.isInParty())
		{
			final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
			packet.setHtml(getHtm(player, "31118-02.html"));
			packet.replace("%min%", Integer.toString(GrandBossConfig.LILITH_MIN_PLAYERS));
			player.sendPacket(packet);
			return null;
		}
		
		final Party party = player.getParty();
		final boolean isInCC = party.isInCommandChannel();
		final List<Player> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
		final boolean isPartyLeader = (isInCC) ? party.getCommandChannel().isLeader(player) : party.isLeader(player);
		if (!isPartyLeader)
		{
			return "31118-03.html";
		}
		
		if ((members.size() < GrandBossConfig.LILITH_MIN_PLAYERS) || (members.size() > GrandBossConfig.LILITH_MAX_PLAYERS))
		{
			final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
			packet.setHtml(getHtm(player, "31118-02.html"));
			packet.replace("%min%", Integer.toString(GrandBossConfig.LILITH_MIN_PLAYERS));
			player.sendPacket(packet);
			return null;
		}
		
		for (Player member : members)
		{
			if (member.getLevel() < GrandBossConfig.LILITH_MIN_PLAYER_LEVEL)
			{
				final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
				packet.setHtml(getHtm(player, "31118-04.html"));
				packet.replace("%minLevel%", Integer.toString(GrandBossConfig.LILITH_MIN_PLAYER_LEVEL));
				player.sendPacket(packet);
				return null;
			}
		}
		
		for (Player member : members)
		{
			if (member.isInsideRadius3D(npc, 1000) && (npc.getId() == LILITH_CUBIC))
			{
				member.teleToLocation(ENTER_LILITH_LOC, true);
			}
		}
		
		if ((_lilithStatus == ALIVE) && (npc.getId() == LILITH_CUBIC))
		{
			GrandBossManager.getInstance().setStatus(LILITH, FIGHTING);
			
			// Spawn the rb
			_lilithBoss = addSpawn(LILITH, 185062, -9605, -5499, 15640, false, 0);
			GrandBossManager.getInstance().addBoss((GrandBoss) _lilithBoss);
			_lastAction = System.currentTimeMillis();
			startQuestTimer("check_activity_task", 60000, null, null, true);
			startQuestTimer("end_lilith", 60 * 60000, null, null); // 1h
		}
		
		return super.onTalk(npc, player);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isPet)
	{
		_lastAction = System.currentTimeMillis();
		if (npc.isMinion() || npc.isRaid()) // Lilith and minions
		{
			// Anti BUGGERS
			if (!BOSS_ZONE.isInsideZone(attacker)) // Character attacking out of zone
			{
				attacker.doDie(null);
			}
			
			if (!BOSS_ZONE.isInsideZone(npc)) // Npc moved out of the zone
			{
				Spawn spawn = npc.getSpawn();
				if (spawn != null)
				{
					npc.teleToLocation(spawn.getX(), spawn.getY(), spawn.getZ());
				}
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isPet)
	{
		if (npc.getId() == LILITH)
		{
			notifyEvent("cancel_timers", null, null);
			addSpawn(EXIST_CUBIC, 185062, -9605, -5499, 15640, false, 900000); // 15min
			
			GrandBossManager.getInstance().setStatus(LILITH, DEAD);
			final long respawnTime = getRespawnTime();
			final StatSet info = GrandBossManager.getInstance().getStatSet(LILITH);
			info.set("respawn_time", System.currentTimeMillis() + respawnTime);
			GrandBossManager.getInstance().setStatSet(LILITH, info);
			
			startQuestTimer("unlock_lilith", respawnTime, null, null);
			startQuestTimer("end_lilith", 900000, null, null);
		}
	}
	
	@Override
	public void onSkillSee(Npc npc, Player caster, Skill skill, Collection<WorldObject> targets, boolean isPet)
	{
		if (ArrayUtil.contains(LILITH_MINIONS, npc.getId()) && getRandomBoolean())
		{
			if (skill.getAbnormalType() == AbnormalType.HP_RECOVER)
			{
				if (!npc.isCastingNow() && (npc.getTarget() != npc) && (npc.getTarget() != caster) && (npc.getTarget() != _lilithBoss))
				{
					npc.asAttackable().clearAggroList();
					npc.setTarget(caster);
					npc.asAttackable().addDamageHate(caster, 500, 99999);
					npc.getAI().setIntention(Intention.ATTACK, caster);
				}
			}
		}
	}
	
	private int getRespawnTime()
	{
		return (int) calcReuseFromDays(0, 21, Calendar.THURSDAY, 0, 14, Calendar.SATURDAY);
	}
	
	private long calcReuseFromDays(int day1Minute, int day1Hour, int day1Day, int day2Minute, int day2Hour, int day2Day)
	{
		Calendar now = Calendar.getInstance();
		Calendar day1 = (Calendar) now.clone();
		day1.set(Calendar.MINUTE, day1Minute);
		day1.set(Calendar.HOUR_OF_DAY, day1Hour);
		day1.set(Calendar.DAY_OF_WEEK, day1Day);
		
		Calendar day2 = (Calendar) day1.clone();
		day2.set(Calendar.MINUTE, day2Minute);
		day2.set(Calendar.HOUR_OF_DAY, day2Hour);
		day2.set(Calendar.DAY_OF_WEEK, day2Day);
		
		if (now.after(day1))
		{
			day1.add(Calendar.WEEK_OF_MONTH, 1);
		}
		
		if (now.after(day2))
		{
			day2.add(Calendar.WEEK_OF_MONTH, 1);
		}
		
		Calendar reenter = day1;
		if (day2.before(day1))
		{
			reenter = day2;
		}
		
		return reenter.getTimeInMillis() - System.currentTimeMillis();
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + ".html";
	}
	
	public static void main(String[] args)
	{
		new Lilith();
	}
}
