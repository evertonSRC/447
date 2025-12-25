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
package instances.ThroneOfHeroes;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author CostyKiller
 */
public class ThroneOfHeroesMaryReed extends InstanceScript
{
	// NPCs
	private static final int ROIENTAL = 34571;
	
	// Monsters
	private static final int MARY_REED = 26267;
	private static final int MARY_REED_MINION_ZAKEN = 26255;
	
	// Throne's Treasure Chest Mary Reed
	private static final int TREASURE_CHEST = 26456;
	
	// Misc
	private static final int TEMPLATE_ID = 308;
	
	// NPC dialogs
	private static final NpcStringId[] ZAKEN_MESSAGES =
	{
		NpcStringId.MARY_REED_THEY_ARE_JUST_INSECTS_HAHA_SHOW_THEM_YOUR_TRUE_POWER,
		NpcStringId.MARY_REED_THESE_BRATS_ARE_PRETTY_STRONG_I_SHALL_REST_UP_A_BIT,
		NpcStringId.MARY_REED_THESE_BRATS_ARE_PRETTY_STRONG_WATCH_OUT,
		NpcStringId.MARY_REED_REVEALS_HER_TRUE_POWER
	};
	
	public ThroneOfHeroesMaryReed()
	{
		super(TEMPLATE_ID);
		addInstanceCreatedId(TEMPLATE_ID);
		addStartNpc(ROIENTAL);
		addTalkId(ROIENTAL);
		addAttackId(MARY_REED, MARY_REED_MINION_ZAKEN);
		addKillId(MARY_REED);
	}
	
	@Override
	public void onInstanceCreated(Instance activeInstance, Player player)
	{
		activeInstance.setStatus(0);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "enterInstance":
			{
				enterInstance(player, npc, TEMPLATE_ID);
				startQuestTimer("ANNOUNCE_RAID_START", 10000, null, player);
				break;
			}
			case "reenterInstance":
			{
				final Instance activeInstance = getPlayerInstance(player);
				if (isInInstance(activeInstance))
				{
					enterInstance(player, npc, activeInstance.getTemplateId());
				}
				break;
			}
			case "ANNOUNCE_RAID_START":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.THE_CHALLENGE_FOR_THE_THRONE_OF_HEROES_MARY_REED_WILL_BEGIN_SHORTLY_PLEASE_GET_READY_HEROES, ExShowScreenMessage.TOP_CENTER, 5000, true);
					startQuestTimer("ANNOUNCE_RAID_PREP", 15000, null, player);
				}
				break;
			}
			case "ANNOUNCE_RAID_PREP":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.THE_BATTLE_WITH_MARY_REED_STARTS_IN_10_SEC, ExShowScreenMessage.TOP_CENTER, 5000, true);
					startQuestTimer("ANNOUNCE_5", 10000, null, player);
				}
				break;
			}
			case "ANNOUNCE_5":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.FIVE_SECONDS, ExShowScreenMessage.TOP_CENTER, 1000, true);
					startQuestTimer("ANNOUNCE_4", 1000, null, player);
				}
				break;
			}
			case "ANNOUNCE_4":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.FOUR_SECONDS, ExShowScreenMessage.TOP_CENTER, 1000, true);
					startQuestTimer("ANNOUNCE_3", 1000, null, player);
				}
				break;
			}
			case "ANNOUNCE_3":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.THREE_SECONDS_2, ExShowScreenMessage.TOP_CENTER, 1000, true);
					startQuestTimer("ANNOUNCE_2", 1000, null, player);
				}
				break;
			}
			case "ANNOUNCE_2":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.TWO_SECONDS_2, ExShowScreenMessage.TOP_CENTER, 1000, true);
					startQuestTimer("ANNOUNCE_1", 1000, null, player);
				}
				break;
			}
			case "ANNOUNCE_1":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.ONE_SECOND_2, ExShowScreenMessage.TOP_CENTER, 1000, true);
					startQuestTimer("SPAWN_MARY_REED", 1000, null, player);
				}
				break;
			}
			case "SPAWN_MARY_REED":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.MARY_REED_GREETS_HEROES, ExShowScreenMessage.TOP_CENTER, 5000, true);
					world.spawnGroup("MARY_REED");
				}
				break;
			}
			case "ANNOUNCE_MARY_REED_SPAWNS_ZAKEN":
			{
				final Instance world = npc.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.MARY_REED_SUMMONS_ZAKEN_ZAKEN_WILL_BE_HERE_VERY_SOON, ExShowScreenMessage.TOP_CENTER, 5000, true);
					startQuestTimer("MARY_REED_SPAWNS_ZAKEN", 10000, npc, null);
				}
				break;
			}
			case "MARY_REED_SPAWNS_ZAKEN":
			{
				final Instance world = npc.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.ZAKEN_YOUR_TIME_HAS_COME_KILL_THEM_ALL, ExShowScreenMessage.TOP_CENTER, 5000, true);
					world.spawnGroup("MARY_REED_MINION_ZAKEN");
				}
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			final int hpPer = npc.getCurrentHpPercent();
			if (npc.getId() == MARY_REED)
			{
				if ((hpPer <= 50) && world.isStatus(0))
				{
					startQuestTimer("ANNOUNCE_MARY_REED_SPAWNS_ZAKEN", 10000, npc, null);
					world.setStatus(1);
				}
				else if ((hpPer <= 30) && world.isStatus(1))
				{
					showOnScreenMsg(world, NpcStringId.DO_YOU_THINK_YOU_CAN_EVADE_MY_STRIKE_TAKE_THAT, ExShowScreenMessage.TOP_CENTER, 5000, true);
					world.setStatus(2);
				}
			}
			else if ((npc.getId() == MARY_REED_MINION_ZAKEN) && world.isStatus(2))
			{
				showOnScreenMsg(world, ZAKEN_MESSAGES[getRandom(4)], ExShowScreenMessage.TOP_CENTER, 5000, true);
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (npc.getId() == MARY_REED)
			{
				// Despawn minions and stop timer
				cancelQuestTimer("ANNOUNCE_MARY_REED_SPAWNS_ZAKEN", npc, killer);
				cancelQuestTimer("MARY_REED_SPAWNS_ZAKEN", npc, killer);
				world.getAliveNpcs(MARY_REED_MINION_ZAKEN).forEach(beast -> beast.doDie(null));
				
				// Spawn treasure chests
				boolean eightCCMembersOrMore = ((killer.getCommandChannel() != null) && (killer.getCommandChannel().getMemberCount() >= 8));
				if (killer.isGM() || eightCCMembersOrMore)
				{
					addSpawn(TREASURE_CHEST, killer.getX() + getRandom(-150, 150), killer.getY() + getRandom(-150, 150), killer.getZ() + 20, 0, false, 0, true, world.getId());
					addSpawn(TREASURE_CHEST, killer.getX() + getRandom(-150, 150), killer.getY() + getRandom(-150, 150), killer.getZ() + 20, 0, false, 0, true, world.getId());
				}
				else
				{
					addSpawn(TREASURE_CHEST, killer.getX() + getRandom(-150, 150), killer.getY() + getRandom(-150, 150), killer.getZ() + 20, 0, false, 0, true, world.getId());
				}
				
				// Finish instance
				world.finishInstance(2);
				if (!killer.isGM())
				{
					// Set clan variable
					killer.getClan().getVariables().set("TOH_MARYREED_DONE", System.currentTimeMillis());
					killer.getClan().getVariables().storeMe();
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new ThroneOfHeroesMaryReed();
	}
}
