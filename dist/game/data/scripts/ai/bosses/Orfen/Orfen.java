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
package ai.bosses.Orfen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.managers.GrandBossManager;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.GrandBoss;
import org.l2jmobius.gameserver.model.groups.CommandChannel;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.model.zone.type.ArenaZone;
import org.l2jmobius.gameserver.model.zone.type.EffectZone;
import org.l2jmobius.gameserver.model.zone.type.ScriptZone;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.OnEventTrigger;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * Orfen's AI
 * @author Notorion
 */
public class Orfen extends Script
{
	// NPCs
	private static final int ORFEN = 29325;
	private static final int ARIMA = 29326; // Minios
	private static final int TORFEDO = 29328; // Monster
	private static final int HARANA = 29329; // Monster
	
	// ID Zone and Emitter
	private static final int FOG_TRIGGER_ZONE_ID = 12013;
	private static final int FOG_EMITTER_ID = 21187770;
	private static final String FOG_VARIABLE = "ORFEN_FOG_ACTIVE";
	
	// Zone and Effect
	private static final EffectZone FOG_EFFECT_ZONE = ZoneManager.getInstance().getZoneByName("spore_orfen_poison_zone", EffectZone.class);
	private static final ArenaZone ARENA_ZONE = ZoneManager.getInstance().getZoneByName("Orfen_Arima_Monster_pvp", ArenaZone.class);
	private static final ScriptZone FOG_SCRIPT_ZONE = ZoneManager.getInstance().getZoneById(FOG_TRIGGER_ZONE_ID, ScriptZone.class);
	
	// Requirement Constants
	private static final int ORFEN_MIN_LEVEL = 110;
	
	/**
	 * Minimum members in Command Channel to enter the zone.<br>
	 * Official setting is typically 21 (3 full parties) or 35 depending on update.<br>
	 * Set to 21 to allow access for mid-sized servers while maintaining raid structure.
	 */
	private static final int ORFEN_REQUIRED_CC_MEMBERS = 21;
	
	// Button to deactivate "false" and activate "true" the Party and CC requirement
	private static final boolean REQUIRE_CC_CHECK = true;
	
	// Speech and Voices
	private static final NpcStringId FIRST_ATTACK_MESSAGE = NpcStringId.YOU_RE_ALL_FOOLS;
	private static final NpcStringId CUSTOM_DEATH_MESSAGE = NpcStringId.I_WILL_CONDEMN_YOU_ALL_TO_HELL;
	private static final NpcStringId FOG_ACTIVATED_MESSAGE = NpcStringId.AN_ENRAGED_ORFEN_LIFTS_A_CLOUD_OF_HALLUCINATION_DUST_FROM_THE_GROUND;
	private static final NpcStringId HP_75_MESSAGE = NpcStringId.FOOLS_WHO_CANNOT_SEE_THE_FUTURE;
	private static final NpcStringId HP_60_MESSAGE = NpcStringId.YOU_LL_REGRET_COMPARING_ME_TO_THAT_PATHETIC_LITHRA;
	private static final NpcStringId HP_50_MESSAGE = NpcStringId.I_LL_SHOW_YOU_THE_POWER_OF_MY_RAGE;
	
	// Sounds
	private static final String FIRST_ATTACK_VOICE = "Npcdialog1.orfen_ep50_1129_battle_1";
	private static final String DEATH_VOICE = "Npcdialog1.orfen_ep50_1129_battle_5";
	private static final String HP_75_VOICE = "Npcdialog1.orfen_ep50_1129_battle_6";
	private static final String HP_60_VOICE = "Npcdialog1.orfen_ep50_1129_battle_2";
	private static final String HP_50_VOICE = "Npcdialog1.orfen_ep50_1129_battle_3";
	
	// Spawn Orfen and teleport out
	private static final Location SPAWN_LOCATION = new Location(43728, 17220, -4342);
	private static final Location TELEPORT_OUT_LOCATION = new Location(64151, 26094, -3763);
	
	// Status
	private static final int ALIVE = 0;
	private static final int DEAD = 1;
	
	// Respawn Constants
	private static final int SPAWN_DAY = Calendar.THURSDAY;
	private static final int SPAWN_HOUR = 21;
	private static final int SPAWN_MINUTE = 00;
	
	// Skills Orfen
	private static final SkillHolder ORFEN_SLASHER = new SkillHolder(32486, 1); // Orfen Slasher
	private static final SkillHolder ORFEN_FATAL_SLASHER = new SkillHolder(32487, 1); // Orfen Fatal Slasher
	private static final SkillHolder ORFEN_ENERGY_SCATTER = new SkillHolder(32488, 1); // Orfen Energy Scatter
	private static final SkillHolder ORFEN_FURY_ENERGY_WAVE = new SkillHolder(32489, 1); // Orfen Fury Energy Wave
	
	// Barrier Skills
	private static final SkillHolder ORFEN_BOSS_PROTECTION = new SkillHolder(29518, 1); // Boss' Protection
	private static final SkillHolder ORFEN_BARRIER = new SkillHolder(29515, 1); // Boss Power
	
	// Orfen Buff - HP 50%
	private static final int SKILL_ID_BUFF_50HP = 32495; // Orfen's Rage
	private static final int SKILL_LEVEL_BUFF_50HP = 1;
	private static final SkillHolder ORFEN_50HP_BUFF = new SkillHolder(SKILL_ID_BUFF_50HP, SKILL_LEVEL_BUFF_50HP);
	
	// Arima
	private static final int ARIMA_SHROUD_ID = 32507; // Barrier Visual Skill - Shroud of the Sea of Spores
	private static final int ARIMA_SHROUD_START_LEVEL = 4;
	private static final int ARIMA_SHROUD_MAX_LEVEL = 4; // Maximum level for regression
	
	/**
	 * Arima Shroud Hits Configuration.<br>
	 * <b>Note on Sampling:</b> The values defined below are divided by 10 because the script uses a sampling optimization (processing only 1 in every 10 attacks).<br>
	 * <i>Formula: Real Hits Required = Config Value * 10.</i>
	 */
	private static final Map<Integer, Integer> ARIMA_HITS_PER_LEVEL = Map.of( //
		4, 250, // Level 4: Requires approx. 2500 real hits.
		3, 200, // Level 3: Requires approx. 2000 real hits.
		2, 180, // Level 2: Requires approx. 1800 real hits.
		1, 140 // Level 1: Requires approx. 1400 real hits.
	);
	
	/** Time window (in ms) for players to break the current shield level before hits are reset. */
	private static final long ARIMA_DEGRADE_TIMER_MS = 120000; // 2 minutes (120.000 ms)
	
	/** Time (in ms) for the shield to naturally regenerate one level if left alone. */
	private static final long ARIMA_REGRESS_TIMER_MS = 3600000; // 60 minutes
	
	// Arena spawn monster
	private static final int MIN_X = 41753;
	private static final int MAX_X = 45693;
	private static final int MIN_Y = 14657;
	private static final int MAX_Y = 18904;
	private static final int SPAWN_Z = -4429;
	
	// Barrier
	// Hits to break down barriers
	private static final int ORFEN_BARRIER_HIT_COUNT = 2000; // Hits to break 29518 (Boss' Protection)
	private static final int ORFEN_BARRIER_HIT_COUNT_2 = 2000; // Hits to break 29515 (Barrier)
	private static final long ORFEN_BARRIER_TIMEOUT_MS = 600000; // 10 minutos (Timeout do 29515)
	
	// Vulnerable Window
	private static final int ORFEN_BARRIER_VULN_MIN_MS = 50000; // 50 sec
	private static final int ORFEN_BARRIER_VULN_MAX_MS = 70000; // 70 sec
	
	// State Variables
	private final Set<Npc> _minions = ConcurrentHashMap.newKeySet();
	private GrandBoss _orfenInstance = null;
	private boolean _arimaActive = false;
	private boolean _arimaOnCooldown = false;
	private boolean _firstWaveDone = false;
	private boolean _fogWaveAt50HP = false;
	private long _lastAttackTime = 0;
	private final Map<Npc, Integer> _orfenHits = new ConcurrentHashMap<>();
	private boolean _orfenVulnerable = false;
	private boolean _arimaLockedIn = false;
	
	// Barrier State Flags
	private boolean _orfenHasBossProtection = false;
	private boolean _orfenHasTimedBarrier = false;
	private long _lastBarrierHitTime = 0;
	
	// Arima Shroud State Variables
	private final Map<Npc, Integer> _arimaShroudLevel = new ConcurrentHashMap<>();
	private final Map<Npc, Integer> _arimaHitCount = new ConcurrentHashMap<>();
	private final Map<Npc, Long> _arimaDegradeTimerStart = new ConcurrentHashMap<>();
	private final Map<Npc, Long> _arimaRegressTimerRemain = new ConcurrentHashMap<>();
	private final Map<Npc, Long> _arimaRegressTimerStart = new ConcurrentHashMap<>();
	
	private boolean _firstAttackSpeechDone = false;
	private boolean _hp75SpeechDone = false;
	private boolean _hp60SpeechDone = false;
	
	private Orfen()
	{
		addAttackId(ORFEN, ARIMA, TORFEDO, HARANA);
		addKillId(ORFEN, ARIMA, TORFEDO, HARANA);
		addFactionCallId(ORFEN, ARIMA, TORFEDO, HARANA);
		addEnterZoneId(FOG_TRIGGER_ZONE_ID);
		addAttackId(ORFEN, ARIMA);
		addAggroRangeEnterId(ORFEN);
		addSpawnId(ARIMA);
		
		if (FOG_SCRIPT_ZONE == null)
		{
			LOGGER.severe("Orfen AI: ScriptZone (ID: " + FOG_TRIGGER_ZONE_ID + ") not found!");
		}
		
		if (ARENA_ZONE == null)
		{
			LOGGER.severe("Orfen AI: Zone 'Orfen_Arima_Monster_pvp' not found!");
		}
		
		if (FOG_EFFECT_ZONE == null)
		{
			LOGGER.severe("Orfen AI: Effect Zone 'spore_orfen_poison_zone' not found!");
		}
		
		initializeRespawn();
	}
	
	private void initializeRespawn()
	{
		try
		{
			final int status = GrandBossManager.getInstance().getStatus(ORFEN);
			if (status == ALIVE) // Status 0
			{
				spawnOrfen();
			}
			else if (status == DEAD) // Status 1
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
			LOGGER.severe("Orfen: Error during initialization: " + e.getMessage());
		}
	}
	
	private void scheduleNextRespawn()
	{
		final long currentTime = System.currentTimeMillis();
		final Calendar nextRespawn = getNextRespawnTime();
		final long delay = nextRespawn.getTimeInMillis() - currentTime;
		
		LOGGER.info("Orfen: Next respawn scheduled for " + nextRespawn.getTime() + " in " + delay + "ms");
		
		ThreadPool.schedule(() ->
		{
			if (GrandBossManager.getInstance().getStatus(ORFEN) == DEAD)
			{
				spawnOrfen();
			}
		}, delay);
	}
	
	private void spawnOrfen()
	{
		try
		{
			if ((_orfenInstance != null) && !_orfenInstance.isDead())
			{
				LOGGER.info("Orfen: Attempted to spawn Orfen but she is already alive.");
				return;
			}
			
			_orfenInstance = null;
			
			final Npc bossNpc = addSpawn(ORFEN, SPAWN_LOCATION, false, 0);
			
			if (bossNpc == null)
			{
				LOGGER.severe("Orfen: Failed to spawn boss via addSpawn()");
				return;
			}
			
			final GrandBoss orfen = (GrandBoss) bossNpc;
			GrandBossManager.getInstance().setStatus(ORFEN, ALIVE);
			orfen.setRandomWalking(false);
			orfen.setRandomAnimation(false);
			_orfenInstance = orfen;
			spawnBoss(orfen);
			cancelQuestTimer("DISTANCE_CHECK", orfen, null);
			startQuestTimer("DISTANCE_CHECK", 5000, orfen, null, true);
			
			LOGGER.info("Orfen: Boss spawned successfully at " + SPAWN_LOCATION + " with ObjectId: " + orfen.getObjectId());
		}
		catch (Exception e)
		{
			LOGGER.severe("Orfen: Error spawning boss: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private Calendar getNextRespawnTime()
	{
		final Calendar nextRespawn = Calendar.getInstance();
		
		// Orfen Spawn Thursday 21:00
		nextRespawn.set(Calendar.DAY_OF_WEEK, SPAWN_DAY);
		nextRespawn.set(Calendar.HOUR_OF_DAY, SPAWN_HOUR);
		nextRespawn.set(Calendar.MINUTE, SPAWN_MINUTE);
		nextRespawn.set(Calendar.SECOND, 0);
		nextRespawn.set(Calendar.MILLISECOND, 0);
		
		if (nextRespawn.getTimeInMillis() < System.currentTimeMillis())
		{
			nextRespawn.add(Calendar.WEEK_OF_YEAR, 1);
		}
		
		return nextRespawn;
	}
	
	public void spawnBoss(GrandBoss npc)
	{
		GrandBossManager.getInstance().addBoss(npc);
		npc.broadcastPacket(new PlaySound(1, "BS01_A", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
		GlobalVariablesManager.getInstance().set(FOG_VARIABLE, false);
		
		if (FOG_EFFECT_ZONE != null)
		{
			FOG_EFFECT_ZONE.setEnabled(false);
		}
		
		_firstWaveDone = false;
		_fogWaveAt50HP = false;
		_arimaActive = false;
		_arimaOnCooldown = false;
		_arimaLockedIn = false;
		_lastAttackTime = 0;
		_minions.clear();
		
		_firstAttackSpeechDone = false;
		_hp75SpeechDone = false;
		_hp60SpeechDone = false;
		
		_arimaShroudLevel.clear();
		_arimaHitCount.clear();
		_arimaDegradeTimerStart.clear();
		_arimaRegressTimerRemain.clear();
		_arimaRegressTimerStart.clear();
		
		_orfenVulnerable = false;
		_orfenHits.clear();
		_lastBarrierHitTime = 0;
		
		Skill protectionSkill = ORFEN_BOSS_PROTECTION.getSkill();
		if (protectionSkill != null)
		{
			protectionSkill.applyEffects(npc, npc);
			npc.setInvul(true);
			_orfenHasBossProtection = true;
			_orfenHasTimedBarrier = false;
			LOGGER.info("Orfen AI: Barrier 'Boss Protection' (29517) ACTIVATED at spawn.");
		}
		else
		{
			LOGGER.warning("Orfen AI: Skill 29517 (Boss Protection) not found at spawn!");
			npc.setInvul(false);
			_orfenHasBossProtection = false;
			_orfenHasTimedBarrier = false;
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.startsWith("SHROUD_REGRESS_CHECK_"))
		{
			if ((npc != null) && !npc.isDead() && _arimaShroudLevel.containsKey(npc))
			{
				levelUpShroud(npc);
			}
			
			return null;
		}
		
		switch (event)
		{
			case "ORFEN_BARRIER_TIMEOUT_10MIN":
			{
				if ((npc != null) && _orfenHasTimedBarrier && !_orfenVulnerable)
				{
					// Remove 29515
					if (ORFEN_BARRIER.getSkill() != null)
					{
						npc.stopSkillEffects(ORFEN_BARRIER.getSkill());
					}
					
					// Apply 29517
					if (ORFEN_BOSS_PROTECTION.getSkill() != null)
					{
						ORFEN_BOSS_PROTECTION.getSkill().applyEffects(npc, npc);
						_orfenHasBossProtection = true;
						_orfenHasTimedBarrier = false;
						_orfenHits.put(npc, 0);
						_lastBarrierHitTime = 0;
					}
				}
				break;
			}
			case "ORFEN_BARRIER_REAPPLY_TIMER":
			{
				if ((npc != null) && _orfenVulnerable)
				{
					_orfenVulnerable = false;
					npc.setInvul(true);
					
					if (ORFEN_BARRIER.getSkill() != null)
					{
						ORFEN_BARRIER.getSkill().applyEffects(npc, npc);
						_orfenHasBossProtection = false;
						_orfenHasTimedBarrier = true;
						_orfenHits.put(npc, 0);
						_lastBarrierHitTime = 0;
						
						startQuestTimer("ORFEN_BARRIER_TIMEOUT_10MIN", ORFEN_BARRIER_TIMEOUT_MS, npc, null);
					}
				}
				break;
			}
			case "REAPPLY_BARRIER_VISUAL":
			{
				if ((npc != null) && (npc.getId() == ORFEN) && !npc.isDead())
				{
					if (_orfenHasBossProtection)
					{
						Skill protectionSkill = ORFEN_BOSS_PROTECTION.getSkill();
						if ((protectionSkill != null) && (npc.getEffectList().getBuffInfoBySkillId(protectionSkill.getId()) == null))
						{
							protectionSkill.applyEffects(npc, npc);
						}
					}
					else if (_orfenHasTimedBarrier)
					{
						Skill barrierSkill = ORFEN_BARRIER.getSkill();
						if ((barrierSkill != null) && (npc.getEffectList().getBuffInfoBySkillId(barrierSkill.getId()) == null))
						{
							barrierSkill.applyEffects(npc, npc);
						}
					}
				}
				break;
			}
			case "DISTANCE_CHECK":
			{
				if ((npc == null) || npc.isDead())
				{
					cancelQuestTimers("DISTANCE_CHECK");
				}
				else if (npc.calculateDistance2D(npc.getSpawn()) > 3000)
				{
					LOGGER.info("Orfen AI: Maximum distance (3000) reached. Returning to spawn.");
					npc.teleToLocation(SPAWN_LOCATION, true);
					if (npc.isAttackable())
					{
						npc.asAttackable().clearAggroList();
					}
				}
				break;
			}
			case "ACTIVATE_FOG":
			{
				activateFog();
				break;
			}
			case "REAPPLY_SHROUD":
			{
				if ((npc != null) && (npc.getId() == ARIMA) && !npc.isDead())
				{
					final int level = _arimaShroudLevel.getOrDefault(npc, 0);
					if (level > 0)
					{
						if (npc.getEffectList().getBuffInfoBySkillId(ARIMA_SHROUD_ID) != null)
						{
							npc.stopSkillEffects(SkillData.getInstance().getSkill(ARIMA_SHROUD_ID, 1));
						}
						
						Skill shroudVisual = SkillData.getInstance().getSkill(ARIMA_SHROUD_ID, level);
						if (shroudVisual != null)
						{
							shroudVisual.applyEffects(npc, npc);
							LOGGER.info("Orfen AI: Visual shield Lv." + level + " applied to Arima [" + npc.getObjectId() + "]");
						}
					}
				}
				break;
			}
			case "REAPPLY_SHROUD_FOR_PLAYER":
			{
				if ((npc != null) && (npc.getId() == ARIMA) && !npc.isDead())
				{
					final int level = _arimaShroudLevel.getOrDefault(npc, 0);
					if ((level > 0) && (npc.getEffectList().getBuffInfoBySkillId(ARIMA_SHROUD_ID) == null))
					{
						if (npc.getEffectList().getBuffInfoBySkillId(ARIMA_SHROUD_ID) != null)
						{
							npc.stopSkillEffects(SkillData.getInstance().getSkill(ARIMA_SHROUD_ID, 1));
						}
						
						Skill shroudVisual = SkillData.getInstance().getSkill(ARIMA_SHROUD_ID, level);
						if (shroudVisual != null)
						{
							shroudVisual.applyEffects(npc, npc);
						}
					}
				}
				break;
			}
			case "arima_check_timer":
			{
				if ((_orfenInstance == null) && (npc != null))
				{
					_orfenInstance = (GrandBoss) npc;
				}
				
				if ((npc == null) || npc.isDead() || !_arimaActive)
				{
					cancelQuestTimer("arima_check_timer", npc, null);
					break;
				}
				
				if (_arimaLockedIn)
				{
					cancelQuestTimer("arima_check_timer", npc, null);
					break;
				}
				
				boolean timeout;
				if ((ARENA_ZONE != null) && ARENA_ZONE.getPlayersInside().isEmpty())
				{
					timeout = (System.currentTimeMillis() - _lastAttackTime) > 180000;
				}
				else
				{
					timeout = (System.currentTimeMillis() - _lastAttackTime) > 180000;
					if (!timeout)
					{
						_lastAttackTime = System.currentTimeMillis();
					}
				}
				
				if (timeout)
				{
					LOGGER.info("Orfen AI: Arima Check: Timeout (3 min) reached. Deleting Arimas.");
					despawnMinionsById(true, ARIMA);
					_arimaActive = false;
					cancelQuestTimer("arima_check_timer", npc, null);
					_arimaOnCooldown = true;
					startQuestTimer("arima_respawn_cooldown", 60000, npc, null);
				}
				break;
			}
			case "arima_respawn_cooldown":
			{
				_arimaOnCooldown = false;
				break;
			}
			case "spawn_wave_and_fog":
			{
				if ((npc != null) && !npc.isDead() && !_firstWaveDone)
				{
					LOGGER.info("Orfen AI: 1-minute timer reached. Spawning monsters and activating fog.");
					_firstWaveDone = true;
					spawnTorfedoAndHarana(npc);
				}
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAggroRangeEnter(Npc npc, Player player, boolean isSummon)
	{
		if (_arimaActive)
		{
			_lastAttackTime = System.currentTimeMillis();
		}
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		final int level = _arimaShroudLevel.getOrDefault(npc, 0);
		if (level > 0)
		{
			startQuestTimer("REAPPLY_SHROUD", 100, npc, null);
		}
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		final int npcId = npc.getId();
		if (npcId == ORFEN)
		{
			if (ARENA_ZONE == null)
			{
				LOGGER.warning("Orfen: ARENA_ZONE is null in onAttack.");
				return;
			}
			
			if (!ARENA_ZONE.isInsideZone(attacker))
			{
				attacker.teleToLocation(TELEPORT_OUT_LOCATION, false);
				return;
			}
			
			if (REQUIRE_CC_CHECK)
			{
				if (isPlayerInValidCommandChannel(attacker))
				{
					final CommandChannel cc = attacker.getParty().getCommandChannel();
					for (Player member : cc.getMembers())
					{
						if (member.getLevel() < ORFEN_MIN_LEVEL)
						{
							attacker.teleToLocation(TELEPORT_OUT_LOCATION, false);
							return;
						}
					}
				}
				else if (attacker.getLevel() < ORFEN_MIN_LEVEL)
				{
					attacker.teleToLocation(TELEPORT_OUT_LOCATION, false);
					return;
				}
				
				if (!isPlayerInValidCommandChannel(attacker))
				{
					attacker.teleToLocation(TELEPORT_OUT_LOCATION, false);
					return;
				}
			}
			
			_lastAttackTime = System.currentTimeMillis();
			
			if (!_firstAttackSpeechDone)
			{
				_firstAttackSpeechDone = true;
				
				broadcastPacketToZone(new PlaySound(3, FIRST_ATTACK_VOICE, 0, 0, 0, 0, 0));
				npc.broadcastSay(ChatType.NPC_GENERAL, FIRST_ATTACK_MESSAGE);
			}
			
			if (!_arimaActive && !_arimaOnCooldown)
			{
				_arimaActive = true;
				
				final boolean hasExistingArimas = _minions.stream().anyMatch(m -> (m != null) && !m.isDead() && (m.getId() == ARIMA));
				if (hasExistingArimas)
				{
					for (Npc minion : _minions)
					{
						if ((minion != null) && !minion.isDead() && (minion.getId() == ARIMA))
						{
							minion.setTarget(attacker);
							minion.getAI().setIntention(Intention.ATTACK, attacker);
						}
					}
				}
				else
				{
					spawnArima(npc);
				}
				
				startQuestTimer("arima_check_timer", 10000, npc, null, true);
				
				if (!_firstWaveDone)
				{
					startQuestTimer("spawn_wave_and_fog", 60000, npc, null);
				}
			}
			
			if (!_arimaActive && _firstWaveDone && !_arimaLockedIn)
			{
				_arimaActive = true;
				_arimaLockedIn = true;
				
				final boolean hasExistingArimas = _minions.stream().anyMatch(m -> (m != null) && !m.isDead() && (m.getId() == ARIMA));
				if (hasExistingArimas)
				{
					for (Npc minion : _minions)
					{
						if ((minion != null) && !minion.isDead() && (minion.getId() == ARIMA) && (npc.getTarget() != null) && npc.getTarget().isPlayer())
						{
							minion.setTarget(npc.getTarget());
							minion.getAI().setIntention(Intention.ATTACK, npc.getTarget());
						}
					}
				}
				else
				{
					spawnArima(npc);
				}
			}
			
			if (!_orfenVulnerable)
			{
				if ((System.currentTimeMillis() - _lastBarrierHitTime) > 60000)
				{
					_orfenHits.put(npc, 0);
				}
				
				_lastBarrierHitTime = System.currentTimeMillis();
				
				final int hits = _orfenHits.merge(npc, 1, Integer::sum);
				if (_orfenHasBossProtection)
				{
					if (hits >= ORFEN_BARRIER_HIT_COUNT)
					{
						
						if (ORFEN_BOSS_PROTECTION.getSkill() != null)
						{
							npc.stopSkillEffects(ORFEN_BOSS_PROTECTION.getSkill());
						}
						
						if (ORFEN_BARRIER.getSkill() != null)
						{
							ORFEN_BARRIER.getSkill().applyEffects(npc, npc);
						}
						
						_orfenHasBossProtection = false;
						_orfenHasTimedBarrier = true;
						_orfenHits.put(npc, 0);
						_lastBarrierHitTime = 0;
						
						startQuestTimer("ORFEN_BARRIER_TIMEOUT_10MIN", ORFEN_BARRIER_TIMEOUT_MS, npc, null);
					}
				}
				else if (_orfenHasTimedBarrier)
				{
					if (hits >= ORFEN_BARRIER_HIT_COUNT_2)
					{
						
						if (ORFEN_BARRIER.getSkill() != null)
						{
							npc.stopSkillEffects(ORFEN_BARRIER.getSkill());
						}
						
						_orfenHasTimedBarrier = false;
						_orfenVulnerable = true;
						npc.setInvul(false);
						_orfenHits.put(npc, 0);
						_lastBarrierHitTime = 0;
						
						cancelQuestTimer("ORFEN_BARRIER_TIMEOUT_10MIN", npc, null);
						
						startQuestTimer("ORFEN_BARRIER_REAPPLY_TIMER", getRandom(ORFEN_BARRIER_VULN_MIN_MS, ORFEN_BARRIER_VULN_MAX_MS), npc, null);
					}
				}
			}
			
			final double hpPercent = npc.getCurrentHpPercent();
			if ((hpPercent <= 75) && (hpPercent > 70) && !_hp75SpeechDone)
			{
				_hp75SpeechDone = true;
				
				broadcastPacketToZone(new PlaySound(3, HP_75_VOICE, 0, 0, 0, 0, 0));
				npc.broadcastSay(ChatType.NPC_GENERAL, HP_75_MESSAGE);
			}
			
			if ((hpPercent <= 65) && (hpPercent > 60) && !_hp60SpeechDone)
			{
				_hp60SpeechDone = true;
				
				broadcastPacketToZone(new PlaySound(3, HP_60_VOICE, 0, 0, 0, 0, 0));
				npc.broadcastSay(ChatType.NPC_GENERAL, HP_60_MESSAGE);
			}
			
			if ((hpPercent <= 50) && !_fogWaveAt50HP)
			{
				_fogWaveAt50HP = true;
				
				if (hpPercent > 30)
				{
					
					final SkillHolder buff50 = ORFEN_50HP_BUFF;
					final Skill buffSkill = buff50.getSkill();
					if (buffSkill != null)
					{
						buffSkill.applyEffects(npc, npc);
						LOGGER.info("Orfen AI: 50% HP reached, applying self-buff (Skill ID: " + buff50.getSkillId() + ").");
					}
					
					if (ARENA_ZONE != null)
					{
						for (Player p : ARENA_ZONE.getPlayersInside())
						{
							if (p != null)
							{
								p.sendPacket(new PlaySound(3, HP_50_VOICE, 0, 0, 0, 0, 0));
							}
						}
					}
					
					npc.broadcastSay(ChatType.NPC_GENERAL, HP_50_MESSAGE);
				}
				
				final boolean hadOldMinions = _minions.stream().anyMatch(m -> (m != null) && !m.isDead() && ((m.getId() == TORFEDO) || (m.getId() == HARANA)));
				if (hadOldMinions)
				{
					despawnMinionsById(true, TORFEDO, HARANA);
				}
				
				spawnTorfedoAndHarana(npc);
			}
			
			if (manageSkills(npc) && npc.isInsideRadius2D(attacker, 1000) && _orfenVulnerable)
			{
				
				final int chance = getRandom(1000);
				SkillHolder skillToUse = null;
				
				if (chance < 100) // 10% (0-99)
				{
					skillToUse = ORFEN_SLASHER;
				}
				else if (chance < 370) // 27% (100-369)
				{
					skillToUse = ORFEN_FATAL_SLASHER;
				}
				else if (chance < 780) // 41% (370-779)
				{
					skillToUse = ORFEN_ENERGY_SCATTER;
				}
				else if (chance < 1000) // 22% (780-999)
				{
					skillToUse = ORFEN_FURY_ENERGY_WAVE;
				}
				
				if (skillToUse != null)
				{
					final Skill skill = skillToUse.getSkill();
					if (skill != null)
					{
						npc.setTarget(attacker);
						npc.doCast(skill);
					}
				}
			}
		}
		else if (npcId == ARIMA)
		{
			if (_orfenInstance != null)
			{
				_lastAttackTime = System.currentTimeMillis();
			}
			
			if (getRandom(10) != 0)
			{
				return;
			}
			
			final int currentLevel = _arimaShroudLevel.getOrDefault(npc, 0);
			if (currentLevel > 0)
			{
				int hits = _arimaHitCount.getOrDefault(npc, 0) + 1;
				long degradeStartTime = _arimaDegradeTimerStart.getOrDefault(npc, 0L);
				if (degradeStartTime == 0L)
				{
					degradeStartTime = System.currentTimeMillis();
					_arimaDegradeTimerStart.put(npc, degradeStartTime);
				}
				
				if ((System.currentTimeMillis() - degradeStartTime) > ARIMA_DEGRADE_TIMER_MS)
				{
					hits = 1;
					_arimaDegradeTimerStart.put(npc, System.currentTimeMillis());
				}
				
				final int hitsNeeded = ARIMA_HITS_PER_LEVEL.getOrDefault(currentLevel, 10);
				if (hits >= hitsNeeded)
				{
					levelDownShroud(npc);
				}
				else
				{
					_arimaHitCount.put(npc, hits);
				}
			}
		}
	}
	
	private boolean manageSkills(Npc npc)
	{
		if (npc.isCastingNow())
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case ORFEN:
			{
				broadcastPacketToZone(new PlaySound(3, DEATH_VOICE, 0, 0, 0, 0, 0));
				npc.broadcastSay(ChatType.NPC_GENERAL, CUSTOM_DEATH_MESSAGE);
				npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
				GrandBossManager.getInstance().setStatus(ORFEN, DEAD);
				_orfenInstance = null;
				final long currentTime = System.currentTimeMillis();
				GlobalVariablesManager.getInstance().set("ORFEN_LAST_DEATH_TIME", currentTime);
				LOGGER.info("Orfen: Boss killed. Last death time recorded: " + currentTime + " / " + new java.util.Date(currentTime));
				
				scheduleNextRespawn();
				deactivateFog();
				despawnAllMinions();
				
				cancelQuestTimer("arima_check_timer", npc, null);
				cancelQuestTimer("arima_respawn_cooldown", npc, null);
				cancelQuestTimer("spawn_wave_and_fog", npc, null);
				cancelQuestTimers("DISTANCE_CHECK");
				cancelQuestTimer("ORFEN_BARRIER_TIMEOUT_10MIN", npc, null);
				cancelQuestTimer("ORFEN_BARRIER_REAPPLY_TIMER", npc, null);
				
				_arimaActive = false;
				_arimaOnCooldown = false;
				_firstWaveDone = false;
				_fogWaveAt50HP = false;
				_arimaLockedIn = false;
				_lastAttackTime = 0;
				_firstAttackSpeechDone = false;
				_hp75SpeechDone = false;
				_hp60SpeechDone = false;
				_orfenVulnerable = false;
				_orfenHasBossProtection = false;
				_orfenHasTimedBarrier = false;
				_orfenHits.clear();
				_lastBarrierHitTime = 0;
				break;
			}
			case ARIMA:
			{
				_minions.remove(npc);
				_arimaShroudLevel.remove(npc);
				_arimaHitCount.remove(npc);
				_arimaDegradeTimerStart.remove(npc);
				_arimaRegressTimerRemain.remove(npc);
				_arimaRegressTimerStart.remove(npc);
				
				cancelQuestTimer("SHROUD_REGRESS_CHECK_" + npc.getObjectId(), npc, null);
				
				if (_arimaActive && !_arimaLockedIn && _minions.stream().noneMatch(m -> (m != null) && !m.isDead() && (m.getId() == ARIMA)))
				{
					_arimaActive = false;
					if (_orfenInstance != null)
					{
						cancelQuestTimer("arima_check_timer", _orfenInstance, null);
						if (!_firstWaveDone)
						{
							_firstWaveDone = true;
							spawnTorfedoAndHarana(_orfenInstance);
							cancelQuestTimer("spawn_wave_and_fog", _orfenInstance, null);
						}
						
						_arimaOnCooldown = true;
						startQuestTimer("arima_respawn_cooldown", 60000, _orfenInstance, null);
					}
				}
				break;
			}
			case TORFEDO:
			case HARANA:
			{
				_minions.remove(npc);
				
				final boolean minionsRemaining = _minions.stream().anyMatch(m -> (m != null) && !m.isDead() && ((m.getId() == TORFEDO) || (m.getId() == HARANA)));
				if (!minionsRemaining && (_firstWaveDone || _fogWaveAt50HP))
				{
					deactivateFog();
				}
				break;
			}
		}
	}
	
	private void spawnArima(Npc npc)
	{
		if (npc == null)
		{
			return;
		}
		
		for (int i = 0; i < 4; i++)
		{
			try
			{
				final Location loc = getRandomLocationInArena();
				final Npc minion = addSpawn(ARIMA, loc, false, 0);
				_minions.add(minion);
				
				setArimaShroudLevel(minion, ARIMA_SHROUD_START_LEVEL);
				
				long remainingTime = _arimaRegressTimerRemain.getOrDefault(minion, ARIMA_REGRESS_TIMER_MS);
				if (remainingTime <= 0)
				{
					remainingTime = ARIMA_REGRESS_TIMER_MS;
				}
				
				_arimaRegressTimerRemain.remove(minion);
				_arimaRegressTimerStart.put(minion, System.currentTimeMillis() - (ARIMA_REGRESS_TIMER_MS - remainingTime));
				startQuestTimer("SHROUD_REGRESS_CHECK_" + minion.getObjectId(), remainingTime, minion, null);
				
				if ((npc.getTarget() != null) && npc.getTarget().isPlayer())
				{
					minion.setTarget(npc.getTarget());
					minion.getAI().setIntention(Intention.ATTACK, npc.getTarget());
				}
			}
			catch (Exception e)
			{
				LOGGER.warning("Orfen: Fail Spawn Arima: " + e.getMessage());
			}
		}
	}
	
	private void spawnTorfedoAndHarana(Npc npc)
	{
		if (npc == null)
		{
			return;
		}
		
		for (int i = 0; i < 20; i++)
		{
			try
			{
				final Location loc = getRandomLocationInArena();
				final Npc mob = addSpawn(TORFEDO, loc, false, 0);
				_minions.add(mob);
				
				if ((npc.getTarget() != null) && npc.getTarget().isPlayer())
				{
					mob.setTarget(npc.getTarget());
					mob.getAI().setIntention(Intention.ATTACK, npc.getTarget());
				}
			}
			catch (Exception e)
			{
				LOGGER.warning("Orfen: Fail spawn TORFEDO: " + e.getMessage());
			}
		}
		
		for (int i = 0; i < 20; i++)
		{
			try
			{
				final Location loc = getRandomLocationInArena();
				final Npc mob = addSpawn(HARANA, loc, false, 0);
				_minions.add(mob);
				
				if ((npc.getTarget() != null) && npc.getTarget().isPlayer())
				{
					mob.setTarget(npc.getTarget());
					mob.getAI().setIntention(Intention.ATTACK, npc.getTarget());
				}
			}
			catch (Exception e)
			{
				LOGGER.warning("Orfen: Fail spawn HARANA: " + e.getMessage());
			}
		}
		
		startQuestTimer("ACTIVATE_FOG", 500, npc, null);
	}
	
	private void despawnMinionsById(boolean delete, int... npcIds)
	{
		final List<Integer> idsToDespawn = new ArrayList<>();
		for (int id : npcIds)
		{
			idsToDespawn.add(Integer.valueOf(id));
		}
		
		final Iterator<Npc> iterator = _minions.iterator();
		while (iterator.hasNext())
		{
			final Npc minion = iterator.next();
			if ((minion != null) && idsToDespawn.contains(Integer.valueOf(minion.getId())))
			{
				if (delete && (minion.getId() == ARIMA))
				{
					cleanArimaData(minion);
				}
				
				if (delete)
				{
					minion.deleteMe();
					iterator.remove();
				}
				else
				{
					minion.getAI().setIntention(Intention.ACTIVE);
					if (minion.isAttackable())
					{
						minion.asAttackable().clearAggroList();
					}
				}
			}
		}
	}
	
	private void despawnAllMinions()
	{
		final Iterator<Npc> iterator = _minions.iterator();
		while (iterator.hasNext())
		{
			final Npc minion = iterator.next();
			if (minion != null)
			{
				if (minion.getId() == ARIMA)
				{
					cleanArimaData(minion);
				}
				
				minion.deleteMe();
			}
			
			iterator.remove();
		}
		
		_minions.clear();
		_arimaShroudLevel.clear();
		_arimaHitCount.clear();
		_arimaDegradeTimerStart.clear();
		_arimaRegressTimerRemain.clear();
	}
	
	private void cleanArimaData(Npc arima)
	{
		if (arima == null)
		{
			return;
		}
		
		final long regressStartTime = _arimaRegressTimerStart.getOrDefault(arima, 0L);
		if (regressStartTime > 0)
		{
			long remainingTime = ARIMA_REGRESS_TIMER_MS - (System.currentTimeMillis() - regressStartTime);
			if (remainingTime < 0)
			{
				remainingTime = 0;
			}
			
			_arimaRegressTimerRemain.put(arima, remainingTime);
		}
		
		cancelQuestTimer("SHROUD_REGRESS_CHECK_" + arima.getObjectId(), arima, null);
		
		_arimaShroudLevel.remove(arima);
		_arimaHitCount.remove(arima);
		_arimaDegradeTimerStart.remove(arima);
		_arimaRegressTimerStart.remove(arima);
	}
	
	private Location getRandomLocationInArena()
	{
		if (ARENA_ZONE == null)
		{
			LOGGER.warning("Orfen: ARENA_ZONE null.");
			return SPAWN_LOCATION;
		}
		
		int x = 100;
		int y = 100;
		int maxTries = 100;
		do
		{
			x = getRandom(MIN_X, MAX_X);
			y = getRandom(MIN_Y, MAX_Y);
			maxTries--;
		}
		while (!ARENA_ZONE.isInsideZone(x, y, SPAWN_Z) && (maxTries > 0));
		
		if (maxTries <= 0)
		{
			return SPAWN_LOCATION;
		}
		
		return new Location(x, y, SPAWN_Z);
	}
	
	/**
	 * Applies the Shield Level (Visual + Stats) to an Arima.
	 * @param npc The Arima
	 * @param level The shield level (1-4). If 0, removes everything.
	 */
	private void setArimaShroudLevel(Npc npc, int level)
	{
		
		if (npc.getEffectList().getBuffInfoBySkillId(ARIMA_SHROUD_ID) != null)
		{
			npc.stopSkillEffects(SkillData.getInstance().getSkill(ARIMA_SHROUD_ID, 1));
		}
		
		_arimaShroudLevel.put(npc, level);
		
		if (level > 0)
		{
			final Skill newShroudVisual = SkillData.getInstance().getSkill(ARIMA_SHROUD_ID, level);
			if (newShroudVisual != null)
			{
				newShroudVisual.applyEffects(npc, npc);
			}
			
			npc.setInvul(true);
			
			_arimaHitCount.put(npc, 0);
			_arimaDegradeTimerStart.remove(npc);
		}
		else // level == 0 (Broken)
		{
			npc.setInvul(false);
		}
	}
	
	/**
	 * Called by onAttack when enough hits are dealt. Lowers the shield level of the Arima.
	 * @param npc The Arima whose shield will be downgraded
	 */
	private void levelDownShroud(Npc npc)
	{
		if ((npc == null) || !_arimaShroudLevel.containsKey(npc))
		{
			return;
		}
		
		final int currentLevel = _arimaShroudLevel.get(npc);
		if (currentLevel <= 0)
		{
			return;
		}
		
		final int newLevel = currentLevel - 1;
		cancelQuestTimer("SHROUD_REGRESS_CHECK_" + npc.getObjectId(), npc, null);
		_arimaRegressTimerStart.remove(npc);
		setArimaShroudLevel(npc, newLevel);
		if (newLevel > 0)
		{
			_arimaRegressTimerStart.put(npc, System.currentTimeMillis());
			startQuestTimer("SHROUD_REGRESS_CHECK_" + npc.getObjectId(), ARIMA_REGRESS_TIMER_MS, npc, null);
		}
	}
	
	/**
	 * Called by the "SHROUD_REGRESS_CHECK_" timer (60 minutes). Raises the shield level of the Arima.
	 * @param npc The Arima whose shield will be regenerated
	 */
	private void levelUpShroud(Npc npc)
	{
		if ((npc == null) || !_arimaShroudLevel.containsKey(npc) || _arimaLockedIn)
		{
			return;
		}
		
		final int currentLevel = _arimaShroudLevel.get(npc);
		if (currentLevel == ARIMA_SHROUD_MAX_LEVEL)
		{
			setArimaShroudLevel(npc, ARIMA_SHROUD_MAX_LEVEL);
			
			_arimaRegressTimerStart.put(npc, System.currentTimeMillis());
			startQuestTimer("SHROUD_REGRESS_CHECK_" + npc.getObjectId(), ARIMA_REGRESS_TIMER_MS, npc, null);
			return;
		}
		
		final int newLevel = currentLevel + 1;
		setArimaShroudLevel(npc, newLevel);
		
		_arimaRegressTimerStart.put(npc, System.currentTimeMillis());
		startQuestTimer("SHROUD_REGRESS_CHECK_" + npc.getObjectId(), ARIMA_REGRESS_TIMER_MS, npc, null);
	}
	
	private void activateFog()
	{
		if (!GlobalVariablesManager.getInstance().getBoolean(FOG_VARIABLE, false))
		{
			GlobalVariablesManager.getInstance().set(FOG_VARIABLE, true);
			
			if (FOG_EFFECT_ZONE != null)
			{
				FOG_EFFECT_ZONE.setEnabled(true);
			}
			
			broadcastFogPacket(true);
		}
	}
	
	private void deactivateFog()
	{
		if (GlobalVariablesManager.getInstance().getBoolean(FOG_VARIABLE, false))
		{
			GlobalVariablesManager.getInstance().set(FOG_VARIABLE, false);
			
			if (FOG_EFFECT_ZONE != null)
			{
				FOG_EFFECT_ZONE.setEnabled(false);
			}
			
			broadcastFogPacket(false);
		}
	}
	
	private void broadcastFogPacket(boolean state)
	{
		final ScriptZone fogZone = ZoneManager.getInstance().getZoneById(FOG_TRIGGER_ZONE_ID, ScriptZone.class);
		if (fogZone == null)
		{
			return;
		}
		
		final OnEventTrigger fogEmitterPacket = new OnEventTrigger(FOG_EMITTER_ID, state);
		final ExShowScreenMessage screenMessagePacket = state ? new ExShowScreenMessage(FOG_ACTIVATED_MESSAGE, ExShowScreenMessage.TOP_CENTER, 7000, true) : null;
		for (Creature creature : fogZone.getCharactersInside())
		{
			if ((creature != null) && creature.isPlayer())
			{
				creature.sendPacket(fogEmitterPacket);
				
				if (state)
				{
					creature.sendPacket(screenMessagePacket);
				}
			}
		}
	}
	
	private void broadcastPacketToZone(ServerPacket packet)
	{
		if (ARENA_ZONE == null)
		{
			return;
		}
		
		for (Player player : ARENA_ZONE.getPlayersInside())
		{
			if (player != null)
			{
				player.sendPacket(packet);
			}
		}
	}
	
	@Override
	public void onEnterZone(Creature creature, ZoneType zone)
	{
		if ((zone.getId() == FOG_TRIGGER_ZONE_ID) && creature.isPlayer())
		{
			if (GlobalVariablesManager.getInstance().getBoolean(FOG_VARIABLE, false))
			{
				creature.sendPacket(new OnEventTrigger(FOG_EMITTER_ID, true));
			}
			
			for (Npc minion : _minions)
			{
				if ((minion != null) && !minion.isDead() && (minion.getId() == ARIMA))
				{
					final int level = _arimaShroudLevel.getOrDefault(minion, 0);
					if ((level > 0) && (minion.getEffectList().getBuffInfoBySkillId(ARIMA_SHROUD_ID) == null))
					{
						startQuestTimer("REAPPLY_SHROUD_FOR_PLAYER", 500, minion, creature.asPlayer());
					}
				}
			}
			
			if ((_orfenInstance != null) && !_orfenInstance.isDead())
			{
				if (_orfenHasBossProtection && (_orfenInstance.getEffectList().getBuffInfoBySkillId(ORFEN_BOSS_PROTECTION.getSkillId()) == null))
				{
					startQuestTimer("REAPPLY_BARRIER_VISUAL", 500, _orfenInstance, null);
				}
				else if (_orfenHasTimedBarrier && (_orfenInstance.getEffectList().getBuffInfoBySkillId(ORFEN_BARRIER.getSkillId()) == null))
				{
					startQuestTimer("REAPPLY_BARRIER_VISUAL", 500, _orfenInstance, null);
				}
			}
		}
		
		super.onEnterZone(creature, zone);
	}
	
	private boolean isPlayerInValidCommandChannel(Player player)
	{
		final Party party = player.getParty();
		if (party == null)
		{
			return false;
		}
		
		final CommandChannel cc = party.getCommandChannel();
		if ((cc == null) || (cc.getMemberCount() < ORFEN_REQUIRED_CC_MEMBERS))
		{
			return false;
		}
		
		return true;
	}
	
	public static void main(String[] args)
	{
		new Orfen();
	}
}
