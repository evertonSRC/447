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
package org.l2jmobius.gameserver.config;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.util.ConfigReader;
import org.l2jmobius.commons.util.StringUtil;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;

/**
 * This class loads all the Conquest related configurations.
 * @author Mobius
 */
public class ConquestConfig
{
	// File
	private static final String CONQUEST_CONFIG_FILE = "./config/Conquest.ini";
	
	// Constants
	public static boolean CONQUEST_SYSTEM_ENABLED;
	public static boolean CONQUEST_SCHEDULE_ENABLED;
	public static int CONQUEST_CURRENT_CYCLE;
	public static long CONQUEST_SEASON_END;
	public static String CONQUEST_RESET_DAY;
	public static String CONQUEST_RESET_TIME;
	public static List<Integer> CONQUEST_AVAILABLE_DAYS1;
	public static String CONQUEST_START_HOUR1;
	public static String CONQUEST_END_HOUR1;
	public static String CONQUEST_START_HOUR2;
	public static String CONQUEST_END_HOUR2;
	public static List<Integer> CONQUEST_AVAILABLE_DAYS2;
	public static String CONQUEST_START_HOUR3;
	public static String CONQUEST_END_HOUR3;
	public static boolean CONQUEST_TELEPORTS_FOR_ALL;
	public static int CONQUEST_TELEPORT_REQUIRED_LEVEL;
	public static int CONQUEST_RATE_PERSONAL_POINTS;
	public static int CONQUEST_RATE_SERVER_POINTS;
	public static int CONQUEST_RATE_SERVER_SOUL_ORBS;
	public static int CONQUEST_RATE_ZONE_POINTS;
	public static int CONQUEST_RATE_BLOODY_COINS;
	public static int CONQUEST_ATTACK_POINTS;
	public static int CONQUEST_LIFE_POINTS;
	public static boolean CONQUEST_PVP_ZONE;
	public static int CONQUEST_PERSONAL_REWARD_MIN_POINTS;
	public static int CONQUEST_SERVER_REWARD_MIN_POINTS;
	public static List<ItemHolder> CONQUEST_REWARDS_RANK_1 = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_REWARDS_RANK_2 = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_REWARDS_RANK_3 = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_REWARDS_RANK_4 = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_REWARDS_RANK_5 = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_REWARDS_RANK_6 = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_REWARDS_RANK_7 = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_REWARDS_RANK_8 = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_REWARDS_RANK_9 = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_REWARDS_RANK_10 = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_REWARDS_RANK_PARTICIPANT = new ArrayList<>();
	public static int CONQUEST_MAX_SACRED_FIRE_SLOTS_COUNT;
	public static int CONQUEST_SACRED_FIRE_REWARD_SERVER_POINTS;
	public static int CONQUEST_SACRED_FIRE_REWARD_PERSONAL_POINTS;
	public static int CONQUEST_SACRED_FIRE_REWARD_FIRE_SOURCE_POINTS;
	public static List<ItemHolder> CONQUEST_SACRED_FIRE_REWARDS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_UPGRADES_RESET_REQUIRED_ITEMS = new ArrayList<>();
	public static int CONQUEST_ABILITY_FIRE_SOURCE_EXP_AMOUNT;
	public static int CONQUEST_ABILITY_FIRE_SOURCE_UPGRADE_CHANCE;
	public static int CONQUEST_ABILITY_LIFE_SOURCE_REQUIRED_HP_POINTS;
	public static List<ItemHolder> CONQUEST_ABILITY_LIFE_SOURCE_REQUIRED_ITEMS = new ArrayList<>();
	public static int CONQUEST_ABILITY_LIFE_SOURCE_EXP_AMOUNT;
	public static int CONQUEST_ABILITY_LIFE_SOURCE_UPGRADE_CHANCE;
	public static int CONQUEST_ABILITY_LIFE_SOURCE_REWARD_CHANCE;
	public static List<ItemHolder> CONQUEST_ABILITY_LIFE_SOURCE_REWARDS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_FLAME_SPARK_REQUIRED_ITEMS = new ArrayList<>();
	public static int CONQUEST_ABILITY_FLAME_SPARK_EXP_AMOUNT;
	public static int CONQUEST_ABILITY_FLAME_SPARK_UPGRADE_CHANCE;
	public static int CONQUEST_ABILITY_FLAME_SPARK_REWARD_CHANCE;
	public static List<ItemHolder> CONQUEST_ABILITY_FLAME_SPARK_REWARDS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_FIRE_TOTEM_REQUIRED_ITEMS = new ArrayList<>();
	public static int CONQUEST_ABILITY_FIRE_TOTEM_EXP_AMOUNT;
	public static int CONQUEST_ABILITY_FIRE_TOTEM_UPGRADE_CHANCE;
	public static int CONQUEST_ABILITY_FIRE_TOTEM_REWARD_CHANCE;
	public static List<ItemHolder> CONQUEST_ABILITY_FIRE_TOTEM_REWARDS = new ArrayList<>();
	public static int CONQUEST_ABILITY_BATTLE_SOUL_REQUIRED_SP_POINTS;
	public static List<ItemHolder> CONQUEST_ABILITY_BATTLE_SOUL_REQUIRED_ITEMS = new ArrayList<>();
	public static int CONQUEST_ABILITY_BATTLE_SOUL_EXP_AMOUNT;
	public static int CONQUEST_ABILITY_BATTLE_SOUL_UPGRADE_CHANCE;
	public static int CONQUEST_ABILITY_BATTLE_SOUL_REWARD_CHANCE;
	public static List<ItemHolder> CONQUEST_ABILITY_BATTLE_SOUL_REWARDS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_UPGRADE_LEVEL_1_REQUIRED_ITEMS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_UPGRADE_LEVEL_2_REQUIRED_ITEMS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_UPGRADE_LEVEL_3_REQUIRED_ITEMS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_UPGRADE_LEVEL_4_REQUIRED_ITEMS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_UPGRADE_LEVEL_5_REQUIRED_ITEMS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_UPGRADE_LEVEL_6_REQUIRED_ITEMS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_UPGRADE_LEVEL_7_REQUIRED_ITEMS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_UPGRADE_LEVEL_8_REQUIRED_ITEMS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_UPGRADE_LEVEL_9_REQUIRED_ITEMS = new ArrayList<>();
	public static List<ItemHolder> CONQUEST_ABILITY_UPGRADE_LEVEL_10_REQUIRED_ITEMS = new ArrayList<>();
	
	public static void load()
	{
		final ConfigReader config = new ConfigReader(CONQUEST_CONFIG_FILE);
		CONQUEST_SYSTEM_ENABLED = config.getBoolean("ConquestSystemEnabled", true);
		CONQUEST_SCHEDULE_ENABLED = config.getBoolean("ConquestScheduleEnabled", true);
		CONQUEST_CURRENT_CYCLE = config.getInt("ConquestCurrentCycle", 1);
		CONQUEST_SEASON_END = config.getLong("ConquestSeasonEnd", 0);
		CONQUEST_RESET_DAY = config.getString("ConquestResetDay", "01");
		CONQUEST_RESET_TIME = config.getString("ConquestResetTime", "00:00");
		CONQUEST_AVAILABLE_DAYS1 = new ArrayList<>();
		for (String day : config.getString("ConquestAvailableDays1", "").trim().split(","))
		{
			if (StringUtil.isNumeric(day))
			{
				CONQUEST_AVAILABLE_DAYS1.add(Integer.parseInt(day));
			}
		}
		CONQUEST_START_HOUR1 = config.getString("ConquestStartHour1", "10:00");
		CONQUEST_END_HOUR1 = config.getString("ConquestEndHour1", "12:00");
		CONQUEST_START_HOUR2 = config.getString("ConquestStartHour2", "22:00");
		CONQUEST_END_HOUR2 = config.getString("ConquestEndHour2", "00:00");
		CONQUEST_AVAILABLE_DAYS2 = new ArrayList<>();
		for (String day : config.getString("ConquestAvailableDays2", "").trim().split(","))
		{
			if (StringUtil.isNumeric(day))
			{
				CONQUEST_AVAILABLE_DAYS2.add(Integer.parseInt(day));
			}
		}
		CONQUEST_START_HOUR3 = config.getString("ConquestStartHour3", "20:00");
		CONQUEST_END_HOUR3 = config.getString("ConquestEndHour3", "01:00");
		CONQUEST_TELEPORTS_FOR_ALL = config.getBoolean("ConquestTeleportsForAll", false);
		CONQUEST_TELEPORT_REQUIRED_LEVEL = config.getInt("ConquestTeleportRequiredLevel", 110);
		CONQUEST_RATE_PERSONAL_POINTS = config.getInt("ConquestRatePersonalPoints", 1);
		CONQUEST_RATE_SERVER_POINTS = config.getInt("ConquestRateServerPoints", 1);
		CONQUEST_RATE_SERVER_SOUL_ORBS = config.getInt("ConquestRateServerSoulOrbs", 100);
		CONQUEST_RATE_ZONE_POINTS = config.getInt("ConquestRateZonePoints", 1);
		CONQUEST_RATE_BLOODY_COINS = config.getInt("ConquestRateBloodyCoins", 1);
		CONQUEST_ATTACK_POINTS = config.getInt("ConquestCharacterAttackPoints", 100);
		CONQUEST_LIFE_POINTS = config.getInt("ConquestCharacterLifePoints", 20);
		CONQUEST_PVP_ZONE = config.getBoolean("ConquestIsPvpZone", false);
		CONQUEST_PERSONAL_REWARD_MIN_POINTS = config.getInt("ConquestPersonalRewardMinPoints", 1000);
		CONQUEST_SERVER_REWARD_MIN_POINTS = config.getInt("ConquestServerRewardMinPoints", 1);
		CONQUEST_REWARDS_RANK_1.clear();
		for (String s : config.getString("ConquestRewardsRank1", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_REWARDS_RANK_1.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_REWARDS_RANK_2.clear();
		for (String s : config.getString("ConquestRewardsRank2", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_REWARDS_RANK_2.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_REWARDS_RANK_3.clear();
		for (String s : config.getString("ConquestRewardsRank3", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_REWARDS_RANK_3.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_REWARDS_RANK_4.clear();
		for (String s : config.getString("ConquestRewardsRank4", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_REWARDS_RANK_4.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_REWARDS_RANK_5.clear();
		for (String s : config.getString("ConquestRewardsRank5", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_REWARDS_RANK_5.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_REWARDS_RANK_6.clear();
		for (String s : config.getString("ConquestRewardsRank6", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_REWARDS_RANK_6.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_REWARDS_RANK_7.clear();
		for (String s : config.getString("ConquestRewardsRank7", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_REWARDS_RANK_7.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_REWARDS_RANK_8.clear();
		for (String s : config.getString("ConquestRewardsRank8", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_REWARDS_RANK_8.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_REWARDS_RANK_9.clear();
		for (String s : config.getString("ConquestRewardsRank9", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_REWARDS_RANK_9.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_REWARDS_RANK_10.clear();
		for (String s : config.getString("ConquestRewardsRank10", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_REWARDS_RANK_10.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_REWARDS_RANK_PARTICIPANT.clear();
		for (String s : config.getString("ConquestRewardsRankParticipant", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_REWARDS_RANK_PARTICIPANT.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_MAX_SACRED_FIRE_SLOTS_COUNT = config.getInt("ConquestMaxSacredFireSlotsCount", 3);
		CONQUEST_SACRED_FIRE_REWARD_SERVER_POINTS = config.getInt("ConquestSacredFireRewardServerPoints", 2976);
		CONQUEST_SACRED_FIRE_REWARD_PERSONAL_POINTS = config.getInt("ConquestSacredFireRewardPersonalPoints", 2976);
		CONQUEST_SACRED_FIRE_REWARD_FIRE_SOURCE_POINTS = config.getInt("ConquestSacredFireRewardFireSourcePoints", 32);
		CONQUEST_SACRED_FIRE_REWARDS.clear();
		for (String s : config.getString("ConquestSacredFireRewards", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_SACRED_FIRE_REWARDS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_UPGRADES_RESET_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityUpgradesResetRequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_UPGRADES_RESET_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_FIRE_SOURCE_EXP_AMOUNT = config.getInt("ConquestAbilityFireSourceExpAmount", 100);
		CONQUEST_ABILITY_FIRE_SOURCE_UPGRADE_CHANCE = config.getInt("ConquestAbilityFireSourceUpgradeChance", 70);
		CONQUEST_ABILITY_LIFE_SOURCE_REQUIRED_HP_POINTS = config.getInt("ConquestAbilityLifeSourceRequiredHpPoints", 1000);
		CONQUEST_ABILITY_LIFE_SOURCE_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityLifeSourceRequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_LIFE_SOURCE_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_LIFE_SOURCE_EXP_AMOUNT = config.getInt("ConquestAbilityLifeSourceExpAmount", 100);
		CONQUEST_ABILITY_LIFE_SOURCE_UPGRADE_CHANCE = config.getInt("ConquestAbilityLifeSourceUpgradeChance", 70);
		CONQUEST_ABILITY_LIFE_SOURCE_REWARD_CHANCE = config.getInt("ConquestAbilityLifeSourceRewardChance", 30);
		CONQUEST_ABILITY_LIFE_SOURCE_REWARDS.clear();
		for (String s : config.getString("ConquestAbilityLifeSourceRewards", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_LIFE_SOURCE_REWARDS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_FLAME_SPARK_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityFlameSparkRequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_FLAME_SPARK_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_FLAME_SPARK_EXP_AMOUNT = config.getInt("ConquestAbilityFlameSparkExpAmount", 100);
		CONQUEST_ABILITY_FLAME_SPARK_UPGRADE_CHANCE = config.getInt("ConquestAbilityFlameSparkUpgradeChance", 70);
		CONQUEST_ABILITY_FLAME_SPARK_REWARD_CHANCE = config.getInt("ConquestAbilityFlameSparkRewardChance", 30);
		CONQUEST_ABILITY_FLAME_SPARK_REWARDS.clear();
		for (String s : config.getString("ConquestAbilityFlameSparkRewards", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_FLAME_SPARK_REWARDS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_FIRE_TOTEM_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityFireTotemRequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_FIRE_TOTEM_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_FIRE_TOTEM_EXP_AMOUNT = config.getInt("ConquestAbilityFireTotemExpAmount", 100);
		CONQUEST_ABILITY_FIRE_TOTEM_UPGRADE_CHANCE = config.getInt("ConquestAbilityFireTotemUpgradeChance", 70);
		CONQUEST_ABILITY_FIRE_TOTEM_REWARD_CHANCE = config.getInt("ConquestAbilityFireTotemRewardChance", 30);
		CONQUEST_ABILITY_FIRE_TOTEM_REWARDS.clear();
		for (String s : config.getString("ConquestAbilityFireTotemRewards", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_FIRE_TOTEM_REWARDS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_BATTLE_SOUL_REQUIRED_SP_POINTS = config.getInt("ConquestAbilityBattleSoulRequiredSpPoints", 10000);
		CONQUEST_ABILITY_BATTLE_SOUL_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityBattleSoulRequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_BATTLE_SOUL_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_BATTLE_SOUL_EXP_AMOUNT = config.getInt("ConquestAbilityBattleSoulExpAmount", 100);
		CONQUEST_ABILITY_BATTLE_SOUL_UPGRADE_CHANCE = config.getInt("ConquestAbilityBattleSoulUpgradeChance", 70);
		CONQUEST_ABILITY_BATTLE_SOUL_REWARD_CHANCE = config.getInt("ConquestAbilityBattleSoulRewardChance", 30);
		CONQUEST_ABILITY_BATTLE_SOUL_REWARDS.clear();
		for (String s : config.getString("ConquestAbilityBattleSoulRewards", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_BATTLE_SOUL_REWARDS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_UPGRADE_LEVEL_1_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityUpgradeLevel1RequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_UPGRADE_LEVEL_1_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_UPGRADE_LEVEL_2_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityUpgradeLevel2RequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_UPGRADE_LEVEL_2_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_UPGRADE_LEVEL_3_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityUpgradeLevel3RequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_UPGRADE_LEVEL_3_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_UPGRADE_LEVEL_4_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityUpgradeLevel4RequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_UPGRADE_LEVEL_4_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_UPGRADE_LEVEL_5_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityUpgradeLevel5RequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_UPGRADE_LEVEL_5_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_UPGRADE_LEVEL_6_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityUpgradeLevel6RequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_UPGRADE_LEVEL_6_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_UPGRADE_LEVEL_7_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityUpgradeLevel7RequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_UPGRADE_LEVEL_7_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_UPGRADE_LEVEL_8_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityUpgradeLevel8RequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_UPGRADE_LEVEL_8_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_UPGRADE_LEVEL_9_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityUpgradeLevel9RequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_UPGRADE_LEVEL_9_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
		CONQUEST_ABILITY_UPGRADE_LEVEL_10_REQUIRED_ITEMS.clear();
		for (String s : config.getString("ConquestAbilityUpgradeLevel10RequiredItems", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			CONQUEST_ABILITY_UPGRADE_LEVEL_10_REQUIRED_ITEMS.add(new ItemHolder(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1])));
		}
	}
}
