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
package ai.bosses.QueenAnt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.xml.NpcData;
import org.l2jmobius.gameserver.managers.DatabaseSpawnManager;
import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.managers.GrandBossManager;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.Spawn;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.GrandBoss;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;
import org.l2jmobius.gameserver.model.groups.CommandChannel;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.enums.SkillFinishType;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.model.zone.type.ArenaZone;

/**
 * @author Notorion
 */
public class QueenAnt extends Script
{
	// NPCs
	private static final int QUEEN_ANT = 29381;
	private static final int INVISIBLE_NPC = 18919;
	private static final int NPC_LIFETIME = 9000;
	private static final int REQUIRED_CC_MEMBERS = 14;
	
	// Skills
	private static final SkillHolder AREA_SKILL = new SkillHolder(33918, 1);
	private static final SkillHolder COMMON_SKILL_1 = new SkillHolder(33915, 1);
	private static final SkillHolder COMMON_SKILL_2 = new SkillHolder(33916, 1);
	private static final SkillHolder INITIAL_SKILL = new SkillHolder(33917, 1);
	private static final SkillHolder LIMIT_BARRIER = new SkillHolder(29515, 1);
	
	// Barrier
	private static final int BARRIER_DURATION_MILLIS = 600000; // 10 minutes.
	private static final int HIT_COUNT = 2000; // 2000 Number of attacks needed to destroy the barrier.
	private static final int HIT_COUNT_RENEW = 500; // 500 hits in 60 seconds to continue without the barrier, not confirmed.
	private static final int RENEW_DURATION_MILLIS = 600000; // 60 seconds of vulnerability, Not confirmed.
	// Locations
	private static final Location GLUDIO_LOCATION = new Location(-14608, 123920, -3123);
	private static final Location SPAWN_LOCATION = new Location(-7848, 183389, -3624);
	
	// Zone
	private static final ArenaZone ARENA_ZONE = ZoneManager.getInstance().getZoneByName("Queen_Ants_Lair", ArenaZone.class);
	
	// Misc
	private static GrandBoss _spawnedMainBoss;
	private boolean _barrierActivated = false;
	private boolean _bossInCombat = false;
	private boolean _hp85Reached = false;
	private boolean _isDebuffImmunityActive = false;
	private boolean _vulnerablePhase = false;
	private final AtomicBoolean _canUseSkill = new AtomicBoolean(true);
	private final AtomicBoolean _isUsingAreaSkill = new AtomicBoolean(false);
	private long _lastAttackTime = 0;
	private final Map<Npc, Integer> _queenAntHits = new ConcurrentHashMap<>();
	
	private QueenAnt()
	{
		addAttackId(QUEEN_ANT);
		addSpawnId(QUEEN_ANT);
		addKillId(QUEEN_ANT);
		addAggroRangeEnterId(QUEEN_ANT);
		
		initializeRespawn();
		startQuestTimer("check_arena", 5000, null, null, true);
	}
	
	private void initializeRespawn()
	{
		try
		{
			final int status = GrandBossManager.getInstance().getStatus(QUEEN_ANT);
			if (status == 0)
			{
				spawnQueenAnt();
			}
			else if (status == 1)
			{
				scheduleNextRespawn();
			}
			else
			{
				scheduleNextRespawn();
			}
		}
		catch (Exception e)
		{
			LOGGER.severe("Queen Ant: Error during initialization: " + e.getMessage());
		}
	}
	
	private void scheduleNextRespawn()
	{
		final long currentTime = System.currentTimeMillis();
		final Calendar nextRespawn = getNextRespawnTime();
		final long delay = nextRespawn.getTimeInMillis() - currentTime;
		
		LOGGER.info("Queen Ant: Next respawn scheduled for " + nextRespawn.getTime() + " in " + delay + "ms");
		
		ThreadPool.schedule(() ->
		{
			if (GrandBossManager.getInstance().getStatus(QUEEN_ANT) == 1)
			{
				spawnQueenAnt();
			}
		}, delay);
	}
	
	private void spawnQueenAnt()
	{
		try
		{
			if ((_spawnedMainBoss != null) && !_spawnedMainBoss.isDead())
			{
				return;
			}
			
			final NpcTemplate template = NpcData.getInstance().getTemplate(QUEEN_ANT);
			final Spawn spawn = new Spawn(template);
			spawn.setXYZ(SPAWN_LOCATION);
			spawn.setHeading(0);
			spawn.setRespawnDelay(0);
			
			final Npc boss = DatabaseSpawnManager.getInstance().addNewSpawn(spawn, false);
			_spawnedMainBoss = (GrandBoss) boss;
			GrandBossManager.getInstance().setStatus(QUEEN_ANT, 0);
			LOGGER.info("Queen Ant: Boss spawned successfully at " + SPAWN_LOCATION);
			boss.setRandomWalking(false);
			boss.setRandomAnimation(false);
		}
		catch (Exception e)
		{
			LOGGER.severe("Queen Ant: Error spawning boss: " + e.getMessage());
		}
	}
	
	// Return of The Queen Ant - Monday - Tuesday is in 9pm
	// Hero’s Tome time respawn 10/2022 Queen Ant Monday 9pm
	// Shinemaker - Monday 8pm
	private Calendar getNextRespawnTime()
	{
		final Calendar nextRespawn = Calendar.getInstance();
		
		// Spawn Queen Ant Monday 8pm Night*
		nextRespawn.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		nextRespawn.set(Calendar.HOUR_OF_DAY, 20);
		nextRespawn.set(Calendar.MINUTE, 0);
		nextRespawn.set(Calendar.SECOND, 0);
		if (nextRespawn.getTimeInMillis() < System.currentTimeMillis())
		{
			nextRespawn.add(Calendar.WEEK_OF_YEAR, 1);
		}
		
		return nextRespawn;
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		startQuestTimer("checkCombatStatus", 1000, npc, null, true);
	}
	
	@Override
	public void onAggroRangeEnter(Npc npc, Player player, boolean isSummon)
	{
		_bossInCombat = true;
		checkCombatStatus(npc);
		if (_hp85Reached && (npc.getCurrentHp() < (npc.getMaxHp() * 0.85)))
		{
			activateSpecialMechanics(npc);
		}
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		if (!_barrierActivated)
		{
			_barrierActivated = true;
			LIMIT_BARRIER.getSkill().applyEffects(npc, npc);
			npc.setInvul(true);
			startQuestTimer("remove_barrier", BARRIER_DURATION_MILLIS, npc, null);
			_queenAntHits.put(npc, 0);
		}
		
		if (_vulnerablePhase)
		{
			final int hits = _queenAntHits.getOrDefault(npc, 0) + 1;
			_queenAntHits.put(npc, hits);
			
			if (hits >= HIT_COUNT_RENEW)
			{
				cancelQuestTimer("activate_barrier", npc, null);
				startQuestTimer("activate_barrier", RENEW_DURATION_MILLIS, npc, null);
				_queenAntHits.put(npc, 0);
			}
		}
		else
		{
			final int hits = _queenAntHits.getOrDefault(npc, 0) + 1;
			_queenAntHits.put(npc, hits);
			
			if (hits >= HIT_COUNT)
			{
				npc.stopSkillEffects(LIMIT_BARRIER.getSkill());
				npc.setInvul(false);
				cancelQuestTimer("remove_barrier", npc, null);
				_vulnerablePhase = true;
				startQuestTimer("activate_barrier", RENEW_DURATION_MILLIS, npc, null);
				_queenAntHits.put(npc, 0);
			}
		}
		
		if (!ARENA_ZONE.isInsideZone(attacker))
		{
			attacker.teleToLocation(GLUDIO_LOCATION, false);
			return;
		}
		
		_lastAttackTime = System.currentTimeMillis();
		_bossInCombat = true;
		
		if (!_hp85Reached && (npc.getCurrentHp() < (npc.getMaxHp() * 0.85)))
		{
			_hp85Reached = true;
			activateSpecialMechanics(npc);
		}
		
		if (isPlayerInValidCommandChannel(attacker))
		{
			final CommandChannel cc = attacker.getParty().getCommandChannel();
			for (Player member : cc.getMembers())
			{
				if (member.getLevel() < 110)
				{
					attacker.teleToLocation(GLUDIO_LOCATION, false);
					return;
				}
			}
		}
		else if (attacker.getLevel() < 110)
		{
			attacker.teleToLocation(GLUDIO_LOCATION, false);
			return;
		}
		
		if (!isPlayerInValidCommandChannel(attacker))
		{
			attacker.teleToLocation(GLUDIO_LOCATION, false);
		}
	}
	
	private boolean isPlayerInValidCommandChannel(Player player)
	{
		final Party party = player.getParty();
		if (party == null)
		{
			return false;
		}
		
		final CommandChannel cc = party.getCommandChannel();
		if ((cc == null) || (cc.getMemberCount() < REQUIRED_CC_MEMBERS))
		{
			return false;
		}
		
		return true;
	}
	
	private void activateSpecialMechanics(Npc npc)
	{
		startQuestTimer("useAreaSkill", 1000, npc, null);
		startQuestTimer("repeatSpecialMechanics", 30000, npc, null, true);
	}
	
	private void useAreaSkill(Npc npc)
	{
		if (!_canUseSkill.get() || (npc == null) || npc.isDead() || !_bossInCombat)
		{
			return;
		}
		
		_isUsingAreaSkill.set(true);
		
		ThreadPool.schedule(() ->
		{
			if (!npc.isDead() && _bossInCombat)
			{
				_isDebuffImmunityActive = true;
				cancelDebuffs(npc);
				
				ThreadPool.schedule(() -> _isDebuffImmunityActive = false, 7000);
			}
		}, 1000);
		
		ThreadPool.schedule(() ->
		{
			if (_bossInCombat)
			{
				npc.disableSkill(COMMON_SKILL_1.getSkill(), 7000);
				npc.disableSkill(COMMON_SKILL_2.getSkill(), 7000);
				
				ThreadPool.schedule(() ->
				{
					if (!npc.isDead() && _bossInCombat)
					{
						_isUsingAreaSkill.set(false);
					}
				}, 6000);
			}
		}, 1000);
		
		npc.enableAllSkills();
		
		final Location bossLocation = npc.getLocation();
		final List<Npc> spawnedNpcs = new ArrayList<>();
		
		ThreadPool.schedule(() ->
		{
			if (!npc.isDead() && _bossInCombat)
			{
				npc.doCast(INITIAL_SKILL.getSkill());
			}
			
			for (int i = 0; i < 10; i++)
			{
				if (!_bossInCombat)
				{
					break;
				}
				
				final int offsetX = getRandom(-1000, 1000);
				final int offsetY = getRandom(-900, 1000);
				final Location targetLocation = new Location(bossLocation.getX() + offsetX, bossLocation.getY() + offsetY, bossLocation.getZ());
				final Npc invisibleNpc = addSpawn(INVISIBLE_NPC, targetLocation, false, NPC_LIFETIME);
				if (invisibleNpc != null)
				{
					spawnedNpcs.add(invisibleNpc);
					
					ThreadPool.schedule(() ->
					{
						if (!invisibleNpc.isDead() && SkillCaster.checkUseConditions(invisibleNpc, INITIAL_SKILL.getSkill()))
						{
							SkillCaster.triggerCast(invisibleNpc, invisibleNpc, INITIAL_SKILL.getSkill());
						}
					}, 1000);
					
					ThreadPool.schedule(() ->
					{
						if (!invisibleNpc.isDead() && SkillCaster.checkUseConditions(invisibleNpc, AREA_SKILL.getSkill()))
						{
							SkillCaster.triggerCast(invisibleNpc, invisibleNpc, AREA_SKILL.getSkill());
						}
					}, 4000);
				}
			}
		}, 4000);
	}
	
	private void cancelDebuffs(Npc npc)
	{
		if ((npc == null) || npc.isDead() || !_isDebuffImmunityActive)
		{
			return;
		}
		
		npc.getEffectList().getEffects().stream().filter(effect -> isDebuff(effect.getSkill())).forEach(effect -> npc.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, effect.getSkill()));
	}
	
	private boolean isDebuff(Skill skill)
	{
		return (skill != null) && skill.isDebuff();
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if ((npc == null) || (npc.getId() != QUEEN_ANT))
		{
			return null;
		}
		
		switch (event)
		{
			case "queen_ant_barrier_start":
			{
				break;
			}
			case "activate_barrier":
			{
				_barrierActivated = true;
				LIMIT_BARRIER.getSkill().applyEffects(npc, npc);
				npc.setInvul(true);
				_vulnerablePhase = false;
				startQuestTimer("remove_barrier", BARRIER_DURATION_MILLIS, npc, null);
				_queenAntHits.put(npc, 0);
				break;
			}
			case "remove_barrier":
			{
				_barrierActivated = false;
				npc.stopSkillEffects(LIMIT_BARRIER.getSkill());
				npc.setInvul(false);
				_queenAntHits.put(npc, 0);
				break;
			}
			case "check_arena":
			{
				if ((_spawnedMainBoss != null) && !_spawnedMainBoss.isDead())
				{
					checkBossInArena(_spawnedMainBoss);
				}
				break;
			}
			case "useAreaSkill":
			{
				useAreaSkill(npc);
				break;
			}
			case "repeatSpecialMechanics":
			{
				if (!npc.isDead() && _bossInCombat)
				{
					useAreaSkill(npc);
					startQuestTimer("repeatSpecialMechanics", 30000, npc, null, true);
				}
				else
				{
					cancelQuestTimer("repeatSpecialMechanics", npc, null);
				}
				break;
			}
			case "checkCombatStatus":
			{
				checkCombatStatus(npc);
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	private void checkCombatStatus(Npc npc)
	{
		if (((System.currentTimeMillis() - _lastAttackTime) > 10000))
		{
			_bossInCombat = false;
			_hp85Reached = false;
			cancelQuestTimer("repeatSpecialMechanics", npc, null);
		}
		else
		{
			_bossInCombat = true;
		}
	}
	
	private void checkBossInArena(Npc npc)
	{
		if ((npc == null) || npc.isDead())
		{
			return;
		}
		
		if (!ARENA_ZONE.isInsideZone(npc))
		{
			npc.teleToLocation(SPAWN_LOCATION, false);
			npc.setCurrentHp(npc.getMaxHp());
			npc.setCurrentMp(npc.getMaxMp());
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc == _spawnedMainBoss)
		{
			cancelQuestTimer("repeatSpecialMechanics", npc, null);
			cancelQuestTimer("checkCombatStatus", npc, null);
			cancelQuestTimers("check_arena");
			GrandBossManager.getInstance().setStatus(QUEEN_ANT, 1);
			_spawnedMainBoss = null;
			_hp85Reached = false;
			_bossInCombat = false;
			
			final long currentTime = System.currentTimeMillis();
			GlobalVariablesManager.getInstance().set("QUEEN_ANT_LAST_DEATH_TIME", currentTime);
			LOGGER.info("Queen Ant: Boss killed. Last death time recorded: " + currentTime + " / " + new java.util.Date(currentTime));
		}
	}
	
	public static void main(String[] args)
	{
		new QueenAnt();
	}
}
