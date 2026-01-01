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
package instances.LurinSecretWorkshop;

import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.managers.InstanceManager;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * @author CostyKiller, Tanatos
 */
public class LurinSecretWorkshop extends InstanceScript
{
	// NPC
	private static final int HESED = 33780;
	private static final int GRIMNIR = 8310;
	
	// Bosses
	private static final int[] BOSSES =
	{
		8306, // Lurin Archlord
		8307, // Lurin Archlord
		8308, // Lurin Archlord
		8309, // Lurin Archlord
	};
	
	// Additional Monsters Spawn Chance
	private static final int ADDITIONAL_MONSTER_SPAWN_CHANCE = 7;
	
	// Additional Monsters
	private static final int[] ADDITIONAL_MONSTERS =
	{
		8302, // Golem's Copy
		8303, // Golem's Copy
		8304, // Golem's Copy
		8304, // Golem's Copy
	};
	
	// Monsters 105
	private static final int[] MONSTERS_105 =
	{
		8278, // Artisan
		8282, // Scavenger
		8286, // Dwarven Defender
		8290, // Dwarven Hunter
		8294, // Dwarven Mage
		8298, // Dwarven Berserker
	};
	
	// Monsters 110
	private static final int[] MONSTERS_110 =
	{
		8279, // Artisan
		8283, // Scavenger
		8287, // Dwarven Defender
		8291, // Dwarven Hunter
		8295, // Dwarven Mage
		8299, // Dwarven Berserker
	};
	
	// Monsters 115
	private static final int[] MONSTERS_115 =
	{
		8280, // Artisan
		8284, // Scavenger
		8288, // Dwarven Defender
		8292, // Dwarven Hunter
		8296, // Dwarven Mage
		8300, // Dwarven Berserker
	};
	
	// Monsters 120
	private static final int[] MONSTERS_120 =
	{
		8281, // Artisan
		8285, // Scavenger
		8289, // Dwarven Defender
		8293, // Dwarven Hunter
		8297, // Dwarven Mage
		8301, // Dwarven Berserker
	};
	
	// Misc
	private static final int TEMPLATE_ID = 5000;
	private static final SkillHolder TRANSFORM_SKILL = new SkillHolder(29608, 1); // Grimnir's Siege Golem
	// Instance Status
	private static final int SPAWNING_MONSTERS = 1;
	private static final int FIGHTING_MONSTERS = 2;
	private static final int FIGHTING_BOSS = 3;
	private int _instanceLevel = 0;
	
	public LurinSecretWorkshop()
	{
		super(TEMPLATE_ID);
		addStartNpc(HESED);
		addFirstTalkId(GRIMNIR);
		addTalkId(HESED, GRIMNIR);
		addKillId(BOSSES);
		addSpawnId(MONSTERS_105);
		addSpawnId(MONSTERS_110);
		addSpawnId(MONSTERS_115);
		addSpawnId(MONSTERS_120);
		addSpawnId(ADDITIONAL_MONSTERS);
		addSpawnId(BOSSES);
		addCreatureSeeId(MONSTERS_105);
		addCreatureSeeId(MONSTERS_110);
		addCreatureSeeId(MONSTERS_115);
		addCreatureSeeId(MONSTERS_120);
		addCreatureSeeId(ADDITIONAL_MONSTERS);
		addCreatureSeeId(BOSSES);
		addInstanceLeaveId(TEMPLATE_ID);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final long currentTime = System.currentTimeMillis();
		switch (event)
		{
			case "33780.html":
			case "33780-1.html":
			{
				htmltext = event;
				return htmltext;
			}
			case "enterInstance":
			{
				if (currentTime < InstanceManager.getInstance().getInstanceTime(player, TEMPLATE_ID))
				{
					player.sendPacket(new SystemMessage(SystemMessageId.C1_CANNOT_ENTER_YET).addString(player.getName()));
					htmltext = "condNoEnter.html";
					return htmltext;
				}
				
				enterInstance(player, npc, TEMPLATE_ID);
				break;
			}
			case "exitInstance":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.finishInstance();
				break;
			}
			case "startInstance":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (player.getLevel() <= 109)
				{
					_instanceLevel = 105;
				}
				else if ((player.getLevel() >= 110) && (player.getLevel() <= 114))
				{
					_instanceLevel = 110;
				}
				else if ((player.getLevel() >= 115) && (player.getLevel() <= 119))
				{
					_instanceLevel = 115;
				}
				else if (player.getLevel() >= 120)
				{
					_instanceLevel = 120;
				}
				
				// Dispell transform or mount
				if (player.isTransformed())
				{
					player.untransform();
				}
				
				if (player.isMounted())
				{
					player.dismount();
				}
				
				player.doCast(TRANSFORM_SKILL.getSkill());
				world.setStatus(SPAWNING_MONSTERS);
				startQuestTimer("check_status", 1000, null, player);
				break;
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
					case SPAWNING_MONSTERS:
					{
						if (!world.getParameters().getBoolean("MONSTERS_SPAWNED", false))
						{
							world.setStatus(FIGHTING_MONSTERS);
							world.getParameters().set("MONSTERS_SPAWNED", true);
							player.sendPacket(new ExSendUIEvent(player, false, false, 600, 0, NpcStringId.TIME_LEFT));
							startQuestTimer("spawn_copy", 180 * 1000, null, player); // Copy Golem appears in 3 minutes.
							startQuestTimer("spawn_boss", 420 * 1000, null, player); // Boss Lurin appears in 7 minutes.
							startQuestTimer("end_fight", 600 * 1000, null, player); // End fight timer.
							startQuestTimer("check_status", 1000, null, player);
							switch (_instanceLevel)
							{
								case 105:
								{
									world.spawnGroup("MONSTERS_105_GROUP_1");
									world.spawnGroup("MONSTERS_105_GROUP_2");
									world.spawnGroup("MONSTERS_105_GROUP_3");
									world.spawnGroup("MONSTERS_105_GROUP_4");
									break;
								}
								case 110:
								{
									world.spawnGroup("MONSTERS_110_GROUP_1");
									world.spawnGroup("MONSTERS_110_GROUP_2");
									world.spawnGroup("MONSTERS_110_GROUP_3");
									world.spawnGroup("MONSTERS_110_GROUP_4");
									break;
								}
								case 115:
								{
									world.spawnGroup("MONSTERS_115_GROUP_1");
									world.spawnGroup("MONSTERS_115_GROUP_2");
									world.spawnGroup("MONSTERS_115_GROUP_3");
									world.spawnGroup("MONSTERS_115_GROUP_4");
									break;
								}
								case 120:
								{
									world.spawnGroup("MONSTERS_120_GROUP_1");
									world.spawnGroup("MONSTERS_120_GROUP_2");
									world.spawnGroup("MONSTERS_120_GROUP_3");
									world.spawnGroup("MONSTERS_120_GROUP_4");
									break;
								}
							}
						}
						break;
					}
					case FIGHTING_BOSS:
					{
						if (world.getAliveNpcCount(BOSSES) == 0)
						{
							world.despawnGroup("GRIMNIR_DOOR");
							world.finishInstance(5);
						}
						else
						{
							startQuestTimer("check_status", 1000, null, player);
						}
						break;
					}
				}
				break;
			}
			case "spawn_copy":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (getRandom(100) < ADDITIONAL_MONSTER_SPAWN_CHANCE)
				{
					switch (_instanceLevel)
					{
						case 105:
						{
							world.spawnGroup("ADDITIONAL_MONSTER_105");
							break;
						}
						case 110:
						{
							world.spawnGroup("ADDITIONAL_MONSTER_110");
							break;
						}
						case 115:
						{
							world.spawnGroup("ADDITIONAL_MONSTER_115");
							break;
						}
						case 120:
						{
							world.spawnGroup("ADDITIONAL_MONSTER_120");
							break;
						}
					}
					
					showOnScreenMsg(world, NpcStringId.DESTROY_THE_INVADER, ExShowScreenMessage.TOP_CENTER, 10000, true);
				}
				break;
			}
			case "spawn_boss":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				switch (_instanceLevel)
				{
					case 105:
					{
						world.spawnGroup("BOSS_105");
						break;
					}
					case 110:
					{
						world.spawnGroup("BOSS_110");
						break;
					}
					case 115:
					{
						world.spawnGroup("BOSS_115");
						break;
					}
					case 120:
					{
						world.spawnGroup("BOSS_120");
						break;
					}
				}
				
				showOnScreenMsg(world, NpcStringId.YOU_MADE_ME_ANGRY, ExShowScreenMessage.TOP_CENTER, 10000, true);
				world.setStatus(FIGHTING_BOSS);
				break;
			}
			case "end_fight":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				switch (_instanceLevel)
				{
					case 105:
					{
						world.despawnGroup("MONSTERS_105_GROUP_1");
						world.despawnGroup("MONSTERS_105_GROUP_2");
						world.despawnGroup("MONSTERS_105_GROUP_3");
						world.despawnGroup("MONSTERS_105_GROUP_4");
						break;
					}
					case 110:
					{
						world.despawnGroup("MONSTERS_110_GROUP_1");
						world.despawnGroup("MONSTERS_110_GROUP_2");
						world.despawnGroup("MONSTERS_110_GROUP_3");
						world.despawnGroup("MONSTERS_110_GROUP_4");
						break;
					}
					case 115:
					{
						world.despawnGroup("MONSTERS_115_GROUP_1");
						world.despawnGroup("MONSTERS_115_GROUP_2");
						world.despawnGroup("MONSTERS_115_GROUP_3");
						world.despawnGroup("MONSTERS_115_GROUP_4");
						break;
					}
					case 120:
					{
						world.despawnGroup("MONSTERS_120_GROUP_1");
						world.despawnGroup("MONSTERS_120_GROUP_2");
						world.despawnGroup("MONSTERS_120_GROUP_3");
						world.despawnGroup("MONSTERS_120_GROUP_4");
						break;
					}
				}
				
				world.spawnGroup("GRIMNIR_CENTER");
				world.despawnGroup("GRIMNIR_DOOR");
				if (player.isTransformed())
				{
					player.untransform();
				}
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
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
				npc.asMonster().addDamageHate(player, 0, 1000);
				npc.getAI().setIntention(Intention.ACTIVE);
				addAttackDesire(npc, player);
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isPet)
	{
		final Instance world = killer.getInstanceWorld();
		if (!isInInstance(world))
		{
			return;
		}
		
		// Illusory equipment points are no longer awarded by this instance.
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		if (!isInInstance(world))
		{
			return;
		}
		
		npc.setRandomAnimation(false);
		npc.setWalking();
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			if (world.getStatus() == FIGHTING_BOSS)
			{
				return "8310-exit.html";
			}
			
			if (world.getStatus() == FIGHTING_MONSTERS)
			{
				// Dispell transform or mount
				if (player.isTransformed())
				{
					player.untransform();
				}
				
				if (player.isMounted())
				{
					player.dismount();
				}
				
				player.doCast(TRANSFORM_SKILL.getSkill());
				return "8310-reenter.html";
			}
			
			return "8310-enter.html";
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
		return null;
	}
	
	@Override
	public void onInstanceLeave(Player player, Instance instance)
	{
		if (player.isTransformed())
		{
			player.untransform();
		}
	}
	
	private NpcHtmlMessage getNpcHtmlMessage(Player player, Npc npc, String fileName)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		final String text = getHtm(player, fileName);
		if (text == null)
		{
			LOGGER.info("Cannot find HTML file for " + getClass().getSimpleName() + " Instance: " + fileName);
			return null;
		}
		
		html.setHtml(text);
		return html;
	}
	
	public static void main(String[] args)
	{
		new LurinSecretWorkshop();
	}
}
