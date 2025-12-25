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
package instances.EtisVanEtinaSolo;

import org.l2jmobius.gameserver.geoengine.GeoEngine;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.FriendlyNpc;
import org.l2jmobius.gameserver.model.actor.instance.Monster;
import org.l2jmobius.gameserver.model.events.holders.actor.creature.OnCreatureAttacked;
import org.l2jmobius.gameserver.model.events.holders.instance.OnInstanceStatusChange;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.util.ArrayUtil;

/**
 * Etina Helper Devianne AI.
 * @author CostyKiller
 */
public class EtinaHelperDevianne extends Script
{
	// NPCs
	private static final int ETINA_HELPER_DEVIANNE = 34472;
	private static final int[] NOT_ATK_NPCS =
	{
		33798, // Common Decoration - Barricade
		19677, // Seal of Gnosis
		19678, // Seal of Strife
		19679, // Seal of Avarice
		19680, // Seal of Punishment
		19681, // Seal of Awakening
		19682, // Seal of Calamity
		19683, // Seal of Destruction
	};
	private static final int[] ETINA_HELPERS =
	{
		34471, // Leona Blackbird
		34472, // Devianne
		34473, // Elikia
		34474, // Sporcha
		34475, // Aliber
	};
	
	// Misc
	private static final int[] ETINA_SOLO_INSTANCES =
	{
		292, // Fall of Etina (Solo)
		293, // Fall of Etina (Solo)
	};
	
	private EtinaHelperDevianne()
	{
		addSpellFinishedId(ETINA_HELPER_DEVIANNE);
		addCreatureSeeId(ETINA_HELPER_DEVIANNE);
		setCreatureAttackedId(this::onCreatureAttacked, ETINA_HELPER_DEVIANNE);
		setInstanceStatusChangeId(this::onInstanceStatusChange, ETINA_SOLO_INSTANCES);
	}
	
	@Override
	public void onTimerEvent(String event, StatSet params, Npc npc, Player player)
	{
		final Instance instance = npc.getInstanceWorld();
		if ((instance != null) && event.equals("CHECK_ACTION"))
		{
			final StatSet npcVars = npc.getVariables();
			final Player plr = npcVars.getObject("PLAYER_OBJECT", Player.class);
			if (plr != null)
			{
				final double distance = npc.calculateDistance2D(plr);
				if (!npc.isAttackingNow() && !npc.isMoving() && (distance > 250))
				{
					final Location loc = new Location(plr.getX(), plr.getY(), plr.getZ() + 50);
					final Location randLoc = new Location(loc.getX() + getRandom(-100, 100), loc.getY() + getRandom(-100, 100), loc.getZ());
					if (distance > 600)
					{
						npc.teleToLocation(loc);
					}
					else
					{
						npc.setRunning();
					}
					
					addMoveToDesire(npc, randLoc, 23);
					((FriendlyNpc) npc).setCanReturnToSpawnPoint(false);
				}
				else if (!npc.isInCombat() || !npc.isAttackingNow() || (npc.getTarget() == null))
				{
					WorldObject target = npc.getTarget();
					if (target == null)
					{
						npc.setTarget(getRandomEntry(World.getInstance().getVisibleObjectsInRange(npc, Monster.class, 2500)));
					}
					
					if ((target != null) && !target.isInvul() && target.isTargetable() && GeoEngine.getInstance().canSeeTarget(npc, target) && !ArrayUtil.contains(NOT_ATK_NPCS, target.getId()) && !ArrayUtil.contains(ETINA_HELPERS, target.getId()))
					{
						npc.setInvul(true);
						npc.setRunning();
						((FriendlyNpc) npc).setCanReturnToSpawnPoint(false);
						addAttackDesire(npc, target.asCreature());
						// npc.reduceCurrentHp(1, monster, null);
					}
				}
			}
		}
	}
	
	public void onCreatureAttacked(OnCreatureAttacked event)
	{
		final FriendlyNpc npc = (FriendlyNpc) event.getTarget();
		if ((npc != null) && !npc.isInCombat())
		{
			final Instance instance = npc.getInstanceWorld();
			if ((instance != null) && !event.getAttacker().isPlayable() && (getRandom(50) < 5) && !ArrayUtil.contains(ETINA_HELPERS, event.getAttacker().getId()))
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.BEHOLD_THE_POWER_OF_THE_IVORY_TOWER_WIZARDS);
			}
		}
	}
	
	public void onInstanceStatusChange(OnInstanceStatusChange event)
	{
		final int status = event.getStatus();
		if ((status == 1) || (status == 2) || (status == 3))
		{
			final Instance instance = event.getWorld();
			instance.getAliveNpcs(ETINA_HELPER_DEVIANNE).forEach(etinaHelperDevianne -> getTimers().addRepeatingTimer("CHECK_ACTION", 3000, etinaHelperDevianne, null));
		}
	}
	
	@Override
	public void onCreatureSee(Npc npc, Creature creature)
	{
		if (creature.isPlayer())
		{
			npc.getVariables().set("PLAYER_OBJECT", creature.asPlayer());
		}
	}
	
	public static void main(String[] args)
	{
		new EtinaHelperDevianne();
	}
}
