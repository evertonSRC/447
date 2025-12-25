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
package instances.Kastia100;

import java.util.List;

import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.managers.InstanceManager;
import org.l2jmobius.gameserver.managers.WalkingManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Mobius, Tanatos
 */
public class Kastia100 extends InstanceScript
{
	// NPC
	private static final int KARINIA = 34541;
	private static final int RESEARCHER = 34566;
	private static final int BOSS = 24538;
	
	// Monsters
	private static final int[] MONSTERS =
	{
		24535, // Kastia's Keeper
		24536, // Kastia's Overseer
		24537, // Kastia's Warder
	};
	
	// Item
	private static final ItemHolder KASTIAS_PACK = new ItemHolder(81147, 1);
	
	// Skills
	private static final SkillHolder BOSS_BERSERKER = new SkillHolder(32520, 1);
	
	// Misc
	private static final int TEMPLATE_ID = 298;
	
	public Kastia100()
	{
		super(TEMPLATE_ID);
		addStartNpc(KARINIA);
		addTalkId(KARINIA);
		addSpawnId(BOSS);
		addCreatureSeeId(MONSTERS);
		addCreatureSeeId(BOSS);
		addKillId(BOSS);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "enterInstance":
			{
				// Cannot enter if player finished another Kastia instance.
				final long currentTime = System.currentTimeMillis();
				if ((currentTime < InstanceManager.getInstance().getInstanceTime(player, 299)) //
					|| (currentTime < InstanceManager.getInstance().getInstanceTime(player, 300)) //
					|| (currentTime < InstanceManager.getInstance().getInstanceTime(player, 305)) //
					|| (currentTime < InstanceManager.getInstance().getInstanceTime(player, 306)) //
					|| (currentTime < InstanceManager.getInstance().getInstanceTime(player, 317)) //
					|| (currentTime < InstanceManager.getInstance().getInstanceTime(player, 327)))
				{
					player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_ENTER_AS_C1_IS_IN_ANOTHER_INSTANCE_ZONE).addString(player.getName()));
					return null;
				}
				
				enterInstance(player, npc, TEMPLATE_ID);
				if (player.getInstanceWorld() != null)
				{
					startQuestTimer("check_status", 10000, null, player);
				}
				
				return null;
			}
			case "check_status":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				switch (world.getStatus())
				{
					case 0:
					{
						world.setStatus(1);
						showOnScreenMsg(world, NpcStringId.LV_1_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
						moveMonsters(world.spawnGroup("wave_1"));
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 1:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(2);
							showOnScreenMsg(world, NpcStringId.LV_2_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_2"));
						}
						
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 2:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(3);
							showOnScreenMsg(world, NpcStringId.LV_3_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_3"));
						}
						
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 3:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(4);
							showOnScreenMsg(world, NpcStringId.LV_4_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_4"));
						}
						
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 4:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(5);
							showOnScreenMsg(world, NpcStringId.LV_5_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_5"));
						}
						
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 5:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(6);
							showOnScreenMsg(world, NpcStringId.LV_6_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_6"));
						}
						
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 6:
					{
						if (world.getAliveNpcCount(MONSTERS) == 0)
						{
							world.setStatus(7);
							showOnScreenMsg(world, NpcStringId.LV_7_2, ExShowScreenMessage.TOP_CENTER, 10000, true);
							moveMonsters(world.spawnGroup("wave_7"));
							break;
						}
						
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
				}
				
				return null;
			}
			case "move_to_spawn":
			{
				final Instance world = npc.getInstanceWorld();
				final Location loc = world.getTemplateParameters().getLocation("spawnpoint");
				final Location moveTo = new Location(loc.getX() + getRandom(-100, 100), loc.getY() + getRandom(-100, 100), loc.getZ());
				npc.setRunning();
				addMoveToDesire(npc, moveTo, 4);
				startQuestTimer("start_moving", 5000, npc, null);
				break;
			}
			case "start_moving":
			{
				final Instance world = npc.getInstanceWorld();
				final String selectedRoute = "routetospawn";
				WalkingManager.getInstance().startMoving(npc, world.getTemplateParameters().getString(selectedRoute));
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	private void moveMonsters(List<Npc> monsterList)
	{
		int delay = 500;
		for (Npc monster : monsterList)
		{
			final Instance world = monster.getInstanceWorld();
			if (monster.isAttackable() && (world != null))
			{
				monster.setRandomWalking(false);
				startQuestTimer("move_to_spawn", delay, monster, null);
				monster.asAttackable().setCanReturnToSpawnPoint(false);
			}
		}
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			npc.doCast(BOSS_BERSERKER.getSkill());
		}
	}
	
	@Override
	public void onCreatureSee(Npc npc, Creature player)
	{
		final Instance world = player.getInstanceWorld();
		if ((world != null) && (player.isPlayer()))
		{
			final double distance = npc.calculateDistance2D(player);
			if ((distance < 900))
			{
				WalkingManager.getInstance().cancelMoving(npc);
				npc.asMonster().addDamageHate(player, 0, 1000);
				npc.getAI().setIntention(Intention.ACTIVE);
				addAttackDesire(npc, player);
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			world.getAliveNpcs(MONSTERS).forEach(monster -> monster.deleteMe());
			showOnScreenMsg(world, NpcStringId.YOU_HAVE_SUCCESSFULLY_COMPLETED_THE_KASTIA_S_LABYRINTH_YOU_WILL_BE_TRANSPORTED_TO_THE_SURFACE_SHORTLY_ALSO_YOU_CAN_LEAVE_THIS_PLACE_WITH_THE_HELP_OF_KASTIA_S_RESEARCHER, ExShowScreenMessage.TOP_CENTER, 10000, true);
			addSpawn(RESEARCHER, killer.getLocation(), true, 0, false, world.getId());
			giveItems(killer, KASTIAS_PACK);
			world.finishInstance(3);
		}
	}
	
	public static void main(String[] args)
	{
		new Kastia100();
	}
}
