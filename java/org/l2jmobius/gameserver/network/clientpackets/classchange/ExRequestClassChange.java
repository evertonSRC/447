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
package org.l2jmobius.gameserver.network.clientpackets.classchange;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.enums.CategoryType;
import org.l2jmobius.gameserver.data.xml.CategoryData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.SkillTreeData;
import org.l2jmobius.gameserver.model.SkillLearn;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.creature.Race;
import org.l2jmobius.gameserver.model.actor.enums.player.PlayerClass;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;
import org.l2jmobius.gameserver.network.serverpackets.classchange.ExClassChangeSetAlarm;

/**
 * @author Mobius
 */
public class ExRequestClassChange extends ClientPacket
{
	private int _classId;
	private static final String AWAKE_POWER_REWARDED_VAR = "AWAKE_POWER_REWARDED";
	
	// Reward
	private static final int CHAOS_POMANDER = 37374;
	private static final int VITALITY_MAINTAINING_RUNE = 80712;
	private static final int AWAKE_POWER_EVIS = 40268;
	private static final int AWAKE_POWER_SAYHA = 40269;
	private static final int AWAKE_POWER_SHINE = 82967;
	private static final Map<CategoryType, Integer> AWAKE_POWER = new EnumMap<>(CategoryType.class);
	static
	{
		AWAKE_POWER.put(CategoryType.SHINE_MAKER_ALL_CLASS, 82967);
		AWAKE_POWER.put(CategoryType.SIXTH_SIGEL_GROUP, 32264);
		AWAKE_POWER.put(CategoryType.SIXTH_TIR_GROUP, 32265);
		AWAKE_POWER.put(CategoryType.SIXTH_OTHEL_GROUP, 32266);
		AWAKE_POWER.put(CategoryType.SIXTH_YR_GROUP, 32267);
		AWAKE_POWER.put(CategoryType.SIXTH_FEOH_GROUP, 32268);
		AWAKE_POWER.put(CategoryType.SIXTH_WYNN_GROUP, 32269);
		AWAKE_POWER.put(CategoryType.SIXTH_IS_GROUP, 32270);
		AWAKE_POWER.put(CategoryType.SIXTH_EOLH_GROUP, 32271);
	}
	
	@Override
	protected void readImpl()
	{
		_classId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		// Check if class id is valid.
		boolean canChange = false;
		for (PlayerClass cId : player.getPlayerClass().getNextClasses())
		{
			if (cId.getId() == _classId)
			{
				canChange = true;
				break;
			}
		}
		
		if (!canChange //
			&& (_classId != 170) && (player.getPlayerClass().getId() != 133)) // Female Soul Hound fix.
		{
			PacketLogger.warning(player + " tried to change class from " + player.getPlayerClass() + " to " + PlayerClass.getPlayerClass(_classId) + "!");
			return;
		}
		
		// Check for player proper class group and level.
		canChange = false;
		final int playerLevel = player.getLevel();
		if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (playerLevel >= 18))
		{
			canChange = CategoryData.getInstance().isInCategory(player.getRace() == Race.ERTHEIA ? CategoryType.THIRD_CLASS_GROUP : CategoryType.SECOND_CLASS_GROUP, _classId);
		}
		else if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP) && (playerLevel >= 38))
		{
			canChange = CategoryData.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, _classId);
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (playerLevel >= 76))
		{
			canChange = CategoryData.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, _classId);
		}
		else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (playerLevel >= 85))
		{
			canChange = CategoryData.getInstance().isInCategory(CategoryType.SIXTH_CLASS_GROUP, _classId);
		}
		
		// Change class.
		if (canChange)
		{
			player.setPlayerClass(_classId);
			if (player.isSubClassActive())
			{
				player.getSubClasses().get(player.getClassIndex()).setPlayerClass(player.getActiveClass());
			}
			else
			{
				player.setBaseClass(player.getActiveClass());
			}
			
			if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
			{
				SkillTreeData.getInstance().cleanSkillUponChangeClass(player); // TODO: Move to skill learn method?
				for (SkillLearn skill : SkillTreeData.getInstance().getRaceSkillTree(player.getRace()))
				{
					player.addSkill(SkillData.getInstance().getSkill(skill.getSkillId(), skill.getSkillLevel()), true);
				}
				
				if (!PlayerConfig.DISABLE_TUTORIAL && !player.getVariables().getBoolean(AWAKE_POWER_REWARDED_VAR, false))
				{
					player.addItem(ItemProcessType.REWARD, VITALITY_MAINTAINING_RUNE, 1, player, true);
					player.addItem(ItemProcessType.REWARD, CHAOS_POMANDER, 2, player, true);
					if (player.isInCategory(CategoryType.SHINE_MAKER_ALL_CLASS))
					{
						player.getVariables().set(AWAKE_POWER_REWARDED_VAR, true);
						player.addItem(ItemProcessType.REWARD, AWAKE_POWER_SHINE, 1, player, true);
					}
					else if (player.getRace() == Race.ERTHEIA)
					{
						if (player.getPlayerClass() == PlayerClass.EVISCERATOR)
						{
							player.getVariables().set(AWAKE_POWER_REWARDED_VAR, true);
							player.addItem(ItemProcessType.REWARD, AWAKE_POWER_EVIS, 1, player, true);
						}
						
						if (player.getPlayerClass() == PlayerClass.SAYHA_SEER)
						{
							player.getVariables().set(AWAKE_POWER_REWARDED_VAR, true);
							player.addItem(ItemProcessType.REWARD, AWAKE_POWER_SAYHA, 1, player, true);
						}
					}
					else
					{
						for (Entry<CategoryType, Integer> ent : AWAKE_POWER.entrySet())
						{
							if (player.isInCategory(ent.getKey()))
							{
								player.getVariables().set(AWAKE_POWER_REWARDED_VAR, true);
								player.addItem(ItemProcessType.REWARD, ent.getValue().intValue(), 1, player, true);
								break;
							}
						}
					}
				}
			}
			
			if (PlayerConfig.AUTO_LEARN_SKILLS)
			{
				player.giveAvailableSkills(PlayerConfig.AUTO_LEARN_FS_SKILLS, PlayerConfig.AUTO_LEARN_FP_SKILLS, true, PlayerConfig.AUTO_LEARN_SKILLS_WITHOUT_ITEMS);
			}
			
			player.store(false); // Save player cause if server crashes before this char is saved, he will lose class.
			player.broadcastUserInfo();
			player.sendSkillList();
			player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
			
			if (PlayerConfig.DISABLE_TUTORIAL && !player.isInCategory(CategoryType.SIXTH_CLASS_GROUP) //
				&& ((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) && (playerLevel >= 38)) //
					|| (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (playerLevel >= 76)) //
					|| (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (playerLevel >= 85))))
			{
				player.sendPacket(ExClassChangeSetAlarm.STATIC_PACKET);
			}
		}
	}
}
