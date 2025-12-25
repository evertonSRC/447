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

/**
 * This class loads all the feature related configurations.
 * @author Mobius
 */
public class FeatureConfig
{
	// File
	private static final String FEATURE_CONFIG_FILE = "./config/Feature.ini";
	
	// Constants
	public static List<Integer> SIEGE_HOUR_LIST;
	public static int CASTLE_BUY_TAX_NEUTRAL;
	public static int CASTLE_BUY_TAX_LIGHT;
	public static int CASTLE_BUY_TAX_DARK;
	public static int CASTLE_SELL_TAX_NEUTRAL;
	public static int CASTLE_SELL_TAX_LIGHT;
	public static int CASTLE_SELL_TAX_DARK;
	public static long CS_TELE_FEE_RATIO;
	public static int CS_TELE1_FEE;
	public static int CS_TELE2_FEE;
	public static long CS_SUPPORT_FEE_RATIO;
	public static int CS_SUPPORT1_FEE;
	public static int CS_SUPPORT2_FEE;
	public static long CS_MPREG_FEE_RATIO;
	public static int CS_MPREG1_FEE;
	public static int CS_MPREG2_FEE;
	public static long CS_HPREG_FEE_RATIO;
	public static int CS_HPREG1_FEE;
	public static int CS_HPREG2_FEE;
	public static long CS_EXPREG_FEE_RATIO;
	public static int CS_EXPREG1_FEE;
	public static int CS_EXPREG2_FEE;
	public static int OUTER_DOOR_UPGRADE_PRICE2;
	public static int OUTER_DOOR_UPGRADE_PRICE3;
	public static int OUTER_DOOR_UPGRADE_PRICE5;
	public static int INNER_DOOR_UPGRADE_PRICE2;
	public static int INNER_DOOR_UPGRADE_PRICE3;
	public static int INNER_DOOR_UPGRADE_PRICE5;
	public static int WALL_UPGRADE_PRICE2;
	public static int WALL_UPGRADE_PRICE3;
	public static int WALL_UPGRADE_PRICE5;
	public static int TRAP_UPGRADE_PRICE1;
	public static int TRAP_UPGRADE_PRICE2;
	public static int TRAP_UPGRADE_PRICE3;
	public static int TRAP_UPGRADE_PRICE4;
	public static long FS_TELE_FEE_RATIO;
	public static int FS_TELE1_FEE;
	public static int FS_TELE2_FEE;
	public static long FS_SUPPORT_FEE_RATIO;
	public static int FS_SUPPORT1_FEE;
	public static int FS_SUPPORT2_FEE;
	public static long FS_MPREG_FEE_RATIO;
	public static int FS_MPREG1_FEE;
	public static int FS_MPREG2_FEE;
	public static long FS_HPREG_FEE_RATIO;
	public static int FS_HPREG1_FEE;
	public static int FS_HPREG2_FEE;
	public static long FS_EXPREG_FEE_RATIO;
	public static int FS_EXPREG1_FEE;
	public static int FS_EXPREG2_FEE;
	public static int FS_UPDATE_FRQ;
	public static int FS_BLOOD_OATH_COUNT;
	public static int FS_MAX_SUPPLY_LEVEL;
	public static int FS_FEE_FOR_CASTLE;
	public static int FS_MAX_OWN_TIME;
	public static int TAKE_FORT_POINTS;
	public static int LOOSE_FORT_POINTS;
	public static int TAKE_CASTLE_POINTS;
	public static int LOOSE_CASTLE_POINTS;
	public static int CASTLE_DEFENDED_POINTS;
	public static int FESTIVAL_WIN_POINTS;
	public static int HERO_POINTS;
	public static int ROYAL_GUARD_COST;
	public static int KNIGHT_UNIT_COST;
	public static int KNIGHT_REINFORCE_COST;
	public static int CLAN_CONTRIBUTION_REWARD_FOR_RAID;
	public static int CLAN_CONTRIBUTION_REWARD_FOR_ENEMY;
	public static int CLAN_CONTRIBUTION_REQUIRED;
	public static int CLAN_CONTRIBUTION_FAME_REWARD;
	public static int BALLISTA_POINTS;
	public static int BLOODALLIANCE_POINTS;
	public static int BLOODOATH_POINTS;
	public static int KNIGHTSEPAULETTE_POINTS;
	public static int REPUTATION_SCORE_PER_KILL;
	public static int JOIN_ACADEMY_MIN_REP_SCORE;
	public static int JOIN_ACADEMY_MAX_REP_SCORE;
	public static boolean PK_PENALTY_LIST;
	public static int PK_PENALTY_LIST_MINIMUM_COUNT;
	public static boolean ALLOW_WYVERN_ALWAYS;
	public static boolean ALLOW_WYVERN_DURING_SIEGE;
	public static boolean ALLOW_MOUNTS_DURING_SIEGE;
	
	public static void load()
	{
		final ConfigReader config = new ConfigReader(FEATURE_CONFIG_FILE);
		SIEGE_HOUR_LIST = new ArrayList<>();
		for (String hour : config.getString("SiegeHourList", "").split(","))
		{
			if (StringUtil.isNumeric(hour))
			{
				SIEGE_HOUR_LIST.add(Integer.parseInt(hour));
			}
		}
		CASTLE_BUY_TAX_NEUTRAL = config.getInt("BuyTaxForNeutralSide", 15);
		CASTLE_BUY_TAX_LIGHT = config.getInt("BuyTaxForLightSide", 0);
		CASTLE_BUY_TAX_DARK = config.getInt("BuyTaxForDarkSide", 30);
		CASTLE_SELL_TAX_NEUTRAL = config.getInt("SellTaxForNeutralSide", 0);
		CASTLE_SELL_TAX_LIGHT = config.getInt("SellTaxForLightSide", 0);
		CASTLE_SELL_TAX_DARK = config.getInt("SellTaxForDarkSide", 20);
		CS_TELE_FEE_RATIO = config.getLong("CastleTeleportFunctionFeeRatio", 604800000);
		CS_TELE1_FEE = config.getInt("CastleTeleportFunctionFeeLvl1", 1000);
		CS_TELE2_FEE = config.getInt("CastleTeleportFunctionFeeLvl2", 10000);
		CS_SUPPORT_FEE_RATIO = config.getLong("CastleSupportFunctionFeeRatio", 604800000);
		CS_SUPPORT1_FEE = config.getInt("CastleSupportFeeLvl1", 49000);
		CS_SUPPORT2_FEE = config.getInt("CastleSupportFeeLvl2", 120000);
		CS_MPREG_FEE_RATIO = config.getLong("CastleMpRegenerationFunctionFeeRatio", 604800000);
		CS_MPREG1_FEE = config.getInt("CastleMpRegenerationFeeLvl1", 45000);
		CS_MPREG2_FEE = config.getInt("CastleMpRegenerationFeeLvl2", 65000);
		CS_HPREG_FEE_RATIO = config.getLong("CastleHpRegenerationFunctionFeeRatio", 604800000);
		CS_HPREG1_FEE = config.getInt("CastleHpRegenerationFeeLvl1", 12000);
		CS_HPREG2_FEE = config.getInt("CastleHpRegenerationFeeLvl2", 20000);
		CS_EXPREG_FEE_RATIO = config.getLong("CastleExpRegenerationFunctionFeeRatio", 604800000);
		CS_EXPREG1_FEE = config.getInt("CastleExpRegenerationFeeLvl1", 63000);
		CS_EXPREG2_FEE = config.getInt("CastleExpRegenerationFeeLvl2", 70000);
		OUTER_DOOR_UPGRADE_PRICE2 = config.getInt("OuterDoorUpgradePriceLvl2", 3000000);
		OUTER_DOOR_UPGRADE_PRICE3 = config.getInt("OuterDoorUpgradePriceLvl3", 4000000);
		OUTER_DOOR_UPGRADE_PRICE5 = config.getInt("OuterDoorUpgradePriceLvl5", 5000000);
		INNER_DOOR_UPGRADE_PRICE2 = config.getInt("InnerDoorUpgradePriceLvl2", 750000);
		INNER_DOOR_UPGRADE_PRICE3 = config.getInt("InnerDoorUpgradePriceLvl3", 900000);
		INNER_DOOR_UPGRADE_PRICE5 = config.getInt("InnerDoorUpgradePriceLvl5", 1000000);
		WALL_UPGRADE_PRICE2 = config.getInt("WallUpgradePriceLvl2", 1600000);
		WALL_UPGRADE_PRICE3 = config.getInt("WallUpgradePriceLvl3", 1800000);
		WALL_UPGRADE_PRICE5 = config.getInt("WallUpgradePriceLvl5", 2000000);
		TRAP_UPGRADE_PRICE1 = config.getInt("TrapUpgradePriceLvl1", 3000000);
		TRAP_UPGRADE_PRICE2 = config.getInt("TrapUpgradePriceLvl2", 4000000);
		TRAP_UPGRADE_PRICE3 = config.getInt("TrapUpgradePriceLvl3", 5000000);
		TRAP_UPGRADE_PRICE4 = config.getInt("TrapUpgradePriceLvl4", 6000000);
		FS_TELE_FEE_RATIO = config.getLong("FortressTeleportFunctionFeeRatio", 604800000);
		FS_TELE1_FEE = config.getInt("FortressTeleportFunctionFeeLvl1", 1000);
		FS_TELE2_FEE = config.getInt("FortressTeleportFunctionFeeLvl2", 10000);
		FS_SUPPORT_FEE_RATIO = config.getLong("FortressSupportFunctionFeeRatio", 86400000);
		FS_SUPPORT1_FEE = config.getInt("FortressSupportFeeLvl1", 7000);
		FS_SUPPORT2_FEE = config.getInt("FortressSupportFeeLvl2", 17000);
		FS_MPREG_FEE_RATIO = config.getLong("FortressMpRegenerationFunctionFeeRatio", 86400000);
		FS_MPREG1_FEE = config.getInt("FortressMpRegenerationFeeLvl1", 6500);
		FS_MPREG2_FEE = config.getInt("FortressMpRegenerationFeeLvl2", 9300);
		FS_HPREG_FEE_RATIO = config.getLong("FortressHpRegenerationFunctionFeeRatio", 86400000);
		FS_HPREG1_FEE = config.getInt("FortressHpRegenerationFeeLvl1", 2000);
		FS_HPREG2_FEE = config.getInt("FortressHpRegenerationFeeLvl2", 3500);
		FS_EXPREG_FEE_RATIO = config.getLong("FortressExpRegenerationFunctionFeeRatio", 86400000);
		FS_EXPREG1_FEE = config.getInt("FortressExpRegenerationFeeLvl1", 9000);
		FS_EXPREG2_FEE = config.getInt("FortressExpRegenerationFeeLvl2", 10000);
		FS_UPDATE_FRQ = config.getInt("FortressPeriodicUpdateFrequency", 360);
		FS_BLOOD_OATH_COUNT = config.getInt("FortressBloodOathCount", 1);
		FS_MAX_SUPPLY_LEVEL = config.getInt("FortressMaxSupplyLevel", 6);
		FS_FEE_FOR_CASTLE = config.getInt("FortressFeeForCastle", 25000);
		FS_MAX_OWN_TIME = config.getInt("FortressMaximumOwnTime", 168);
		TAKE_FORT_POINTS = config.getInt("TakeFortPoints", 200);
		LOOSE_FORT_POINTS = config.getInt("LooseFortPoints", 0);
		TAKE_CASTLE_POINTS = config.getInt("TakeCastlePoints", 1500);
		LOOSE_CASTLE_POINTS = config.getInt("LooseCastlePoints", 3000);
		CASTLE_DEFENDED_POINTS = config.getInt("CastleDefendedPoints", 750);
		FESTIVAL_WIN_POINTS = config.getInt("FestivalOfDarknessWin", 200);
		HERO_POINTS = config.getInt("HeroPoints", 1000);
		ROYAL_GUARD_COST = config.getInt("CreateRoyalGuardCost", 5000);
		KNIGHT_UNIT_COST = config.getInt("CreateKnightUnitCost", 10000);
		KNIGHT_REINFORCE_COST = config.getInt("ReinforceKnightUnitCost", 5000);
		CLAN_CONTRIBUTION_REWARD_FOR_RAID = config.getInt("ClanContributionRewardForRaid", 1);
		CLAN_CONTRIBUTION_REWARD_FOR_ENEMY = config.getInt("ClanContributionRewardForEnemy", 1);
		CLAN_CONTRIBUTION_REQUIRED = config.getInt("ClanContributionRequired", 100);
		CLAN_CONTRIBUTION_FAME_REWARD = config.getInt("ClanContributionFameReward", 300);
		BALLISTA_POINTS = config.getInt("KillBallistaPoints", 500);
		BLOODALLIANCE_POINTS = config.getInt("BloodAlliancePoints", 500);
		BLOODOATH_POINTS = config.getInt("BloodOathPoints", 200);
		KNIGHTSEPAULETTE_POINTS = config.getInt("KnightsEpaulettePoints", 20);
		REPUTATION_SCORE_PER_KILL = config.getInt("ReputationScorePerKill", 1);
		JOIN_ACADEMY_MIN_REP_SCORE = config.getInt("CompleteAcademyMinPoints", 190);
		JOIN_ACADEMY_MAX_REP_SCORE = config.getInt("CompleteAcademyMaxPoints", 650);
		PK_PENALTY_LIST = config.getBoolean("PkPenaltyList", true);
		PK_PENALTY_LIST_MINIMUM_COUNT = config.getInt("PkPenaltyMinimumCount", 9);
		ALLOW_WYVERN_ALWAYS = config.getBoolean("AllowRideWyvernAlways", false);
		ALLOW_WYVERN_DURING_SIEGE = config.getBoolean("AllowRideWyvernDuringSiege", true);
		ALLOW_MOUNTS_DURING_SIEGE = config.getBoolean("AllowRideMountsDuringSiege", false);
	}
}
