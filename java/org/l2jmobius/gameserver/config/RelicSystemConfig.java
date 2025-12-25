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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.l2jmobius.commons.util.ConfigReader;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;

/**
 * This class loads all the relic system related configurations.
 * @author Mobius
 */
public class RelicSystemConfig
{
	private static final Logger LOGGER = Logger.getLogger(RelicSystemConfig.class.getName());
	
	// File
	private static final String RELIC_SYSTEM_CONFIG_FILE = "./config/RelicSystem.ini";
	
	// Constants
	public static boolean RELIC_SYSTEM_ENABLED;
	public static boolean RELIC_SYSTEM_DEBUG_ENABLED;
	public static int RELIC_SUMMON_COMMON_COUPON_CHANCE_NO_GRADE;
	public static int RELIC_SUMMON_COMMON_COUPON_CHANCE_D_GRADE;
	public static int RELIC_SUMMON_COMMON_COUPON_CHANCE_C_GRADE;
	public static int RELIC_SUMMON_SHINING_COUPON_CHANCE_NO_GRADE;
	public static int RELIC_SUMMON_SHINING_COUPON_CHANCE_D_GRADE;
	public static int RELIC_SUMMON_SHINING_COUPON_CHANCE_C_GRADE;
	public static int RELIC_SUMMON_SHINING_COUPON_CHANCE_B_GRADE;
	public static int RELIC_SUMMON_C_TICKET_CHANCE_D_GRADE;
	public static int RELIC_SUMMON_C_TICKET_CHANCE_C_GRADE;
	public static int RELIC_SUMMON_B_TICKET_CHANCE_C_GRADE;
	public static int RELIC_SUMMON_B_TICKET_CHANCE_B_GRADE;
	public static int RELIC_SUMMON_A_TICKET_CHANCE_B_GRADE;
	public static int RELIC_SUMMON_A_TICKET_CHANCE_A_GRADE;
	public static int RELIC_SUMMON_CHANCE_SHINING_D_GRADE;
	public static int RELIC_SUMMON_CHANCE_SHINING_C_GRADE;
	public static int RELIC_SUMMON_CHANCE_SHINING_B_GRADE;
	public static boolean RELIC_SUMMON_ANNOUNCE;
	public static boolean RELIC_COMPOUND_ANNOUNCE;
	public static boolean RELIC_ANNOUNCE_ONLY_A_B_GRADE;
	public static int RELIC_ENHANCEMENT_CHANCE_1_INGREDIENT;
	public static int RELIC_ENHANCEMENT_CHANCE_2_INGREDIENTS;
	public static int RELIC_ENHANCEMENT_CHANCE_3_INGREDIENTS;
	public static int RELIC_ENHANCEMENT_CHANCE_4_INGREDIENTS;
	public static List<ItemHolder> RELIC_ENHANCEMENT_FEE_NO_GRADE;
	public static List<ItemHolder> RELIC_ENHANCEMENT_FEE_D_GRADE;
	public static List<ItemHolder> RELIC_ENHANCEMENT_FEE_C_GRADE;
	public static List<ItemHolder> RELIC_ENHANCEMENT_FEE_B_GRADE;
	public static List<ItemHolder> RELIC_ENHANCEMENT_FEE_A_GRADE;
	public static int RELIC_COMPOUND_NO_GRADE_INGREDIENTS_CHANCE_NO_GRADE;
	public static int RELIC_COMPOUND_NO_GRADE_INGREDIENTS_CHANCE_D_GRADE;
	public static int RELIC_COMPOUND_NO_GRADE_INGREDIENTS_CHANCE_SHINING_D_GRADE;
	public static int RELIC_COMPOUND_D_GRADE_INGREDIENTS_CHANCE_D_GRADE;
	public static int RELIC_COMPOUND_D_GRADE_INGREDIENTS_CHANCE_SHINING_D_GRADE;
	public static int RELIC_COMPOUND_D_GRADE_INGREDIENTS_CHANCE_C_GRADE;
	public static int RELIC_COMPOUND_D_GRADE_INGREDIENTS_CHANCE_SHINING_C_GRADE;
	public static int RELIC_COMPOUND_C_GRADE_INGREDIENTS_CHANCE_C_GRADE;
	public static int RELIC_COMPOUND_C_GRADE_INGREDIENTS_CHANCE_SHINING_C_GRADE;
	public static int RELIC_COMPOUND_C_GRADE_INGREDIENTS_CHANCE_B_GRADE;
	public static int RELIC_COMPOUND_C_GRADE_INGREDIENTS_CHANCE_SHINING_B_GRADE;
	public static int RELIC_COMPOUND_B_GRADE_INGREDIENTS_CHANCE_B_GRADE;
	public static int RELIC_COMPOUND_B_GRADE_INGREDIENTS_CHANCE_SHINING_B_GRADE;
	public static int RELIC_COMPOUND_B_GRADE_INGREDIENTS_CHANCE_A_GRADE;
	public static List<ItemHolder> RELIC_COMPOUND_FEE_NO_GRADE;
	public static List<ItemHolder> RELIC_COMPOUND_FEE_D_GRADE;
	public static List<ItemHolder> RELIC_COMPOUND_FEE_C_GRADE;
	public static List<ItemHolder> RELIC_COMPOUND_FEE_B_GRADE;
	public static List<ItemHolder> RELIC_COMPOUND_FAILURE_ITEM_C_GRADE;
	public static List<ItemHolder> RELIC_COMPOUND_FAILURE_ITEM_B_GRADE;
	public static int RELIC_UNCONFIRMED_LIST_LIMIT;
	public static int RELIC_UNCONFIRMED_TIME_LIMIT;
	public static int RELIC_REPLACE_ATTEMPTS_B_GRADE;
	public static int RELIC_REPLACE_ATTEMPTS_A_GRADE;
	public static List<ItemHolder> RELIC_REPLACE_ATTEMPTS_FEES_B_GRADE;
	public static List<ItemHolder> RELIC_REPLACE_ATTEMPTS_FEES_A_GRADE;
	public static int RELIC_REPLACE_ATTEMPTS_CHANCE_COMMON_B_GRADE;
	public static int RELIC_REPLACE_ATTEMPTS_CHANCE_SHINING_B_GRADE;
	public static int RELIC_REPLACE_ATTEMPTS_CHANCE_COMMON_A_GRADE;
	public static List<Integer> NO_GRADE_COMMON_RELICS = new ArrayList<>();
	public static List<Integer> D_GRADE_COMMON_RELICS = new ArrayList<>();
	public static List<Integer> D_GRADE_SHINING_RELICS = new ArrayList<>();
	public static List<Integer> C_GRADE_COMMON_RELICS = new ArrayList<>();
	public static List<Integer> C_GRADE_SHINING_RELICS = new ArrayList<>();
	public static List<Integer> B_GRADE_COMMON_RELICS = new ArrayList<>();
	public static List<Integer> B_GRADE_SHINING_RELICS = new ArrayList<>();
	public static List<Integer> A_GRADE_COMMON_RELICS = new ArrayList<>();
	public static Set<Integer> ELEVEN_SUMMON_COUNT_COUPONS = new HashSet<>();
	public static Set<Integer> RELIC_SUMMON_COUPONS = new HashSet<>();
	public static Set<Integer> SHINING_RELIC_SUMMON_COUPONS = new HashSet<>();
	public static Set<Integer> C_GRADE_RELIC_SUMMON_COUPONS = new HashSet<>();
	public static Set<Integer> B_GRADE_RELIC_SUMMON_COUPONS = new HashSet<>();
	public static Set<Integer> A_GRADE_RELIC_SUMMON_COUPONS = new HashSet<>();
	public static Set<Integer> C_GRADE_RELIC_TICKETS = new HashSet<>();
	public static Set<Integer> B_GRADE_RELIC_TICKETS = new HashSet<>();
	public static Set<Integer> A_GRADE_RELIC_TICKETS = new HashSet<>();
	
	public static void load()
	{
		final ConfigReader config = new ConfigReader(RELIC_SYSTEM_CONFIG_FILE);
		RELIC_SYSTEM_ENABLED = config.getBoolean("RelicSystemEnabled", true);
		RELIC_SYSTEM_DEBUG_ENABLED = config.getBoolean("RelicSystemDebugEnabled", false);
		RELIC_SUMMON_COMMON_COUPON_CHANCE_NO_GRADE = config.getInt("RelicSummonCommonCouponChanceNoGrade", 50);
		RELIC_SUMMON_COMMON_COUPON_CHANCE_D_GRADE = config.getInt("RelicSummonCommonCouponChanceDGrade", 30);
		RELIC_SUMMON_COMMON_COUPON_CHANCE_C_GRADE = config.getInt("RelicSummonCommonCouponChanceCGrade", 20);
		RELIC_SUMMON_SHINING_COUPON_CHANCE_NO_GRADE = config.getInt("RelicSummonShiningCouponChanceNoGrade", 50);
		RELIC_SUMMON_SHINING_COUPON_CHANCE_D_GRADE = config.getInt("RelicSummonShiningCouponChanceDGrade", 30);
		RELIC_SUMMON_SHINING_COUPON_CHANCE_C_GRADE = config.getInt("RelicSummonShiningCouponChanceCGrade", 20);
		RELIC_SUMMON_SHINING_COUPON_CHANCE_B_GRADE = config.getInt("RelicSummonShiningCouponChanceBGrade", 10);
		RELIC_SUMMON_C_TICKET_CHANCE_D_GRADE = config.getInt("RelicSummonCTicketChanceDGrade", 80);
		RELIC_SUMMON_C_TICKET_CHANCE_C_GRADE = config.getInt("RelicSummonCTicketChanceCGrade", 20);
		RELIC_SUMMON_B_TICKET_CHANCE_C_GRADE = config.getInt("RelicSummonBTicketChanceCGrade", 85);
		RELIC_SUMMON_B_TICKET_CHANCE_B_GRADE = config.getInt("RelicSummonBTicketChanceBGrade", 15);
		RELIC_SUMMON_A_TICKET_CHANCE_B_GRADE = config.getInt("RelicSummonATicketChanceBGrade", 90);
		RELIC_SUMMON_A_TICKET_CHANCE_A_GRADE = config.getInt("RelicSummonATicketChanceAGrade", 10);
		RELIC_SUMMON_CHANCE_SHINING_D_GRADE = config.getInt("RelicSummonChanceShiningDGrade", 60);
		RELIC_SUMMON_CHANCE_SHINING_C_GRADE = config.getInt("RelicSummonChanceShiningCGrade", 40);
		RELIC_SUMMON_CHANCE_SHINING_B_GRADE = config.getInt("RelicSummonChanceShiningBGrade", 20);
		RELIC_SUMMON_ANNOUNCE = config.getBoolean("RelicSummonAnnounce", true);
		RELIC_COMPOUND_ANNOUNCE = config.getBoolean("RelicCompoundAnnounce", true);
		RELIC_ANNOUNCE_ONLY_A_B_GRADE = config.getBoolean("RelicAnnounceOnlyABGrade", true);
		RELIC_ENHANCEMENT_CHANCE_1_INGREDIENT = config.getInt("RelicEnhancementChance1Ingredient", 15);
		RELIC_ENHANCEMENT_CHANCE_2_INGREDIENTS = config.getInt("RelicEnhancementChance2Ingredients", 35);
		RELIC_ENHANCEMENT_CHANCE_3_INGREDIENTS = config.getInt("RelicEnhancementChance3Ingredients", 60);
		RELIC_ENHANCEMENT_CHANCE_4_INGREDIENTS = config.getInt("RelicEnhancementChance4Ingredients", 100);
		RELIC_ENHANCEMENT_FEE_NO_GRADE = parseItemsList(config.getString("RelicEnhancementFeeNoGrade", "57,100000"));
		RELIC_ENHANCEMENT_FEE_D_GRADE = parseItemsList(config.getString("RelicEnhancementFeeDGrade", "57,1000000"));
		RELIC_ENHANCEMENT_FEE_C_GRADE = parseItemsList(config.getString("RelicEnhancementFeeCGrade", "57,30000000"));
		RELIC_ENHANCEMENT_FEE_B_GRADE = parseItemsList(config.getString("RelicEnhancementFeeBGrade", "57,300000000"));
		RELIC_ENHANCEMENT_FEE_A_GRADE = parseItemsList(config.getString("RelicEnhancementFeeAGrade", "57,1000000000"));
		RELIC_COMPOUND_NO_GRADE_INGREDIENTS_CHANCE_NO_GRADE = config.getInt("RelicCompoundNoGradeIngredientsChanceNoGrade", 55);
		RELIC_COMPOUND_NO_GRADE_INGREDIENTS_CHANCE_D_GRADE = config.getInt("RelicCompoundNoGradeIngredientsChanceDGrade", 42);
		RELIC_COMPOUND_NO_GRADE_INGREDIENTS_CHANCE_SHINING_D_GRADE = config.getInt("RelicCompoundNoGradeIngredientsChanceShiningDGrade", 3);
		RELIC_COMPOUND_D_GRADE_INGREDIENTS_CHANCE_D_GRADE = config.getInt("RelicCompoundDGradeIngredientsChanceDGrade", 75);
		RELIC_COMPOUND_D_GRADE_INGREDIENTS_CHANCE_SHINING_D_GRADE = config.getInt("RelicCompoundDGradeIngredientsChanceShiningDGrade", 5);
		RELIC_COMPOUND_D_GRADE_INGREDIENTS_CHANCE_C_GRADE = config.getInt("RelicCompoundDGradeIngredientsChanceCGrade", 18);
		RELIC_COMPOUND_D_GRADE_INGREDIENTS_CHANCE_SHINING_C_GRADE = config.getInt("RelicCompoundDGradeIngredientsChanceShiningCGrade", 2);
		RELIC_COMPOUND_C_GRADE_INGREDIENTS_CHANCE_C_GRADE = config.getInt("RelicCompoundCGradeIngredientsChanceCGrade", 75);
		RELIC_COMPOUND_C_GRADE_INGREDIENTS_CHANCE_SHINING_C_GRADE = config.getInt("RelicCompoundCGradeIngredientsChanceShiningCGrade", 7);
		RELIC_COMPOUND_C_GRADE_INGREDIENTS_CHANCE_B_GRADE = config.getInt("RelicCompoundCGradeIngredientsChanceBGrade", 12);
		RELIC_COMPOUND_C_GRADE_INGREDIENTS_CHANCE_SHINING_B_GRADE = config.getInt("RelicCompoundCGradeIngredientsChanceShiningBGrade", 6);
		RELIC_COMPOUND_B_GRADE_INGREDIENTS_CHANCE_B_GRADE = config.getInt("RelicCompoundBGradeIngredientsChanceBGrade", 59);
		RELIC_COMPOUND_B_GRADE_INGREDIENTS_CHANCE_SHINING_B_GRADE = config.getInt("RelicCompoundBGradeIngredientsChanceShiningBGrade", 30);
		RELIC_COMPOUND_B_GRADE_INGREDIENTS_CHANCE_A_GRADE = config.getInt("RelicCompoundBGradeIngredientsChanceAGrade", 11);
		RELIC_COMPOUND_FEE_NO_GRADE = parseItemsList(config.getString("RelicCompoundFeeNoGrade", "57,30000"));
		RELIC_COMPOUND_FEE_D_GRADE = parseItemsList(config.getString("RelicCompoundFeeDGrade", "57,300000"));
		RELIC_COMPOUND_FEE_C_GRADE = parseItemsList(config.getString("RelicCompoundFeeCGrade", "57,1000000"));
		RELIC_COMPOUND_FEE_B_GRADE = parseItemsList(config.getString("RelicCompoundFeeBGrade", "57,100000000"));
		RELIC_COMPOUND_FAILURE_ITEM_C_GRADE = parseItemsList(config.getString("RelicCompoundFailureItemCGrade", "83011,1"));
		RELIC_COMPOUND_FAILURE_ITEM_B_GRADE = parseItemsList(config.getString("RelicCompoundFailureItemBGrade", "83012,1"));
		RELIC_UNCONFIRMED_LIST_LIMIT = config.getInt("RelicUnconfirmedListLimit", 100);
		RELIC_UNCONFIRMED_TIME_LIMIT = config.getInt("RelicUnconfirmedTimeLimit", 7);
		RELIC_REPLACE_ATTEMPTS_B_GRADE = config.getInt("RelicReplaceAttemptsBGrade", 5);
		RELIC_REPLACE_ATTEMPTS_A_GRADE = config.getInt("RelicReplaceAttemptsAGrade", 5);
		RELIC_REPLACE_ATTEMPTS_FEES_B_GRADE = parseItemsList(config.getString("RelicReplaceAttemptsFeesBGrade", "48472,3000;48472,15000;48472,27000;48472,39000;48472,51000"));
		RELIC_REPLACE_ATTEMPTS_FEES_A_GRADE = parseItemsList(config.getString("RelicReplaceAttemptsFeesAGrade", "48472,30000;48472,150000;48472,270000;48472,390000;48472,510000"));
		RELIC_REPLACE_ATTEMPTS_CHANCE_COMMON_B_GRADE = config.getInt("RelicReplaceAttemptsChanceCommonBGrade", 60);
		RELIC_REPLACE_ATTEMPTS_CHANCE_SHINING_B_GRADE = config.getInt("RelicReplaceAttemptsChanceShiningBGrade", 40);
		RELIC_REPLACE_ATTEMPTS_CHANCE_COMMON_A_GRADE = config.getInt("RelicReplaceAttemptsChanceCommonAGrade", 10);
		NO_GRADE_COMMON_RELICS.clear();
		final String noGradeCommonRelics = config.getString("NoGradeCommonRelics", "").trim();
		if (!noGradeCommonRelics.isEmpty())
		{
			for (String s : noGradeCommonRelics.split(","))
			{
				NO_GRADE_COMMON_RELICS.add(Integer.parseInt(s.trim()));
			}
		}
		D_GRADE_COMMON_RELICS.clear();
		final String dGradeCommonRelics = config.getString("DGradeCommonRelics", "").trim();
		if (!dGradeCommonRelics.isEmpty())
		{
			for (String s : dGradeCommonRelics.split(","))
			{
				D_GRADE_COMMON_RELICS.add(Integer.parseInt(s.trim()));
			}
		}
		D_GRADE_SHINING_RELICS.clear();
		final String dGradeShiningRelics = config.getString("DGradeShiningRelics", "").trim();
		if (!dGradeShiningRelics.isEmpty())
		{
			for (String s : dGradeShiningRelics.split(","))
			{
				D_GRADE_SHINING_RELICS.add(Integer.parseInt(s.trim()));
			}
		}
		C_GRADE_COMMON_RELICS.clear();
		final String cGradeCommonRelics = config.getString("CGradeCommonRelics", "").trim();
		if (!cGradeCommonRelics.isEmpty())
		{
			for (String s : cGradeCommonRelics.split(","))
			{
				C_GRADE_COMMON_RELICS.add(Integer.parseInt(s.trim()));
			}
		}
		C_GRADE_SHINING_RELICS.clear();
		final String cGradeShiningRelics = config.getString("CGradeShiningRelics", "").trim();
		if (!cGradeShiningRelics.isEmpty())
		{
			for (String s : cGradeShiningRelics.split(","))
			{
				C_GRADE_SHINING_RELICS.add(Integer.parseInt(s.trim()));
			}
		}
		B_GRADE_COMMON_RELICS.clear();
		final String bGradeCommonRelics = config.getString("BGradeCommonRelics", "").trim();
		if (!bGradeCommonRelics.isEmpty())
		{
			for (String s : bGradeCommonRelics.split(","))
			{
				B_GRADE_COMMON_RELICS.add(Integer.parseInt(s.trim()));
			}
		}
		B_GRADE_SHINING_RELICS.clear();
		final String bGradeShiningRelics = config.getString("BGradeShiningRelics", "").trim();
		if (!bGradeShiningRelics.isEmpty())
		{
			for (String s : bGradeShiningRelics.split(","))
			{
				B_GRADE_SHINING_RELICS.add(Integer.parseInt(s.trim()));
			}
		}
		A_GRADE_COMMON_RELICS.clear();
		final String aGradeCommonRelics = config.getString("AGradeCommonRelics", "").trim();
		if (!aGradeCommonRelics.isEmpty())
		{
			for (String s : aGradeCommonRelics.split(","))
			{
				A_GRADE_COMMON_RELICS.add(Integer.parseInt(s.trim()));
			}
		}
		ELEVEN_SUMMON_COUNT_COUPONS.clear();
		final String elevenSummonCountCoupons = config.getString("ElevenSummonCountCoupons", "").trim();
		if (!elevenSummonCountCoupons.isEmpty())
		{
			for (String s : elevenSummonCountCoupons.split(","))
			{
				ELEVEN_SUMMON_COUNT_COUPONS.add(Integer.parseInt(s.trim()));
			}
		}
		RELIC_SUMMON_COUPONS.clear();
		final String relicSummonCoupons = config.getString("RelicSummonCoupons", "").trim();
		if (!relicSummonCoupons.isEmpty())
		{
			for (String s : relicSummonCoupons.split(","))
			{
				RELIC_SUMMON_COUPONS.add(Integer.parseInt(s.trim()));
			}
		}
		SHINING_RELIC_SUMMON_COUPONS.clear();
		final String shiningRelicSummonCoupons = config.getString("ShiningRelicSummonCoupons", "").trim();
		if (!shiningRelicSummonCoupons.isEmpty())
		{
			for (String s : shiningRelicSummonCoupons.split(","))
			{
				SHINING_RELIC_SUMMON_COUPONS.add(Integer.parseInt(s.trim()));
			}
		}
		C_GRADE_RELIC_SUMMON_COUPONS.clear();
		final String cGradeRelicSummonCoupons = config.getString("CGradeRelicSummonCoupons", "").trim();
		if (!cGradeRelicSummonCoupons.isEmpty())
		{
			for (String s : cGradeRelicSummonCoupons.split(","))
			{
				C_GRADE_RELIC_SUMMON_COUPONS.add(Integer.parseInt(s.trim()));
			}
		}
		B_GRADE_RELIC_SUMMON_COUPONS.clear();
		final String bGradeRelicSummonCoupons = config.getString("BGradeRelicSummonCoupons", "").trim();
		if (!bGradeRelicSummonCoupons.isEmpty())
		{
			for (String s : bGradeRelicSummonCoupons.split(","))
			{
				B_GRADE_RELIC_SUMMON_COUPONS.add(Integer.parseInt(s.trim()));
			}
		}
		A_GRADE_RELIC_SUMMON_COUPONS.clear();
		final String aGradeRelicSummonCoupons = config.getString("AGradeRelicSummonCoupons", "").trim();
		if (!aGradeRelicSummonCoupons.isEmpty())
		{
			for (String s : aGradeRelicSummonCoupons.split(","))
			{
				A_GRADE_RELIC_SUMMON_COUPONS.add(Integer.parseInt(s.trim()));
			}
		}
		C_GRADE_RELIC_TICKETS.clear();
		final String cGradeRelicTickets = config.getString("CGradeRelicTickets", "").trim();
		if (!cGradeRelicTickets.isEmpty())
		{
			for (String s : cGradeRelicTickets.split(","))
			{
				C_GRADE_RELIC_TICKETS.add(Integer.parseInt(s.trim()));
			}
		}
		B_GRADE_RELIC_TICKETS.clear();
		final String bGradeRelicTickets = config.getString("BGradeRelicTickets", "").trim();
		if (!bGradeRelicTickets.isEmpty())
		{
			for (String s : bGradeRelicTickets.split(","))
			{
				B_GRADE_RELIC_TICKETS.add(Integer.parseInt(s.trim()));
			}
		}
		A_GRADE_RELIC_TICKETS.clear();
		final String aGradeRelicTickets = config.getString("AGradeRelicTickets", "").trim();
		if (!aGradeRelicTickets.isEmpty())
		{
			for (String s : aGradeRelicTickets.split(","))
			{
				A_GRADE_RELIC_TICKETS.add(Integer.parseInt(s.trim()));
			}
		}
	}
	
	/**
	 * Parse a config value from its string representation to a two-dimensional int array.<br>
	 * The format of the value to be parsed should be as follows: "item1Id,item1Amount;item2Id,item2Amount;...itemNId,itemNAmount".
	 * @param line the value of the parameter to parse
	 * @return the parsed list or {@code null} if nothing was parsed
	 */
	private static List<ItemHolder> parseItemsList(String line)
	{
		if (line.isEmpty())
		{
			return Collections.emptyList();
		}
		
		final String[] propertySplit = line.split(";");
		if (line.equalsIgnoreCase("none") || (propertySplit.length == 0))
		{
			return Collections.emptyList();
		}
		
		String[] valueSplit;
		final List<ItemHolder> result = new ArrayList<>(propertySplit.length);
		for (String value : propertySplit)
		{
			valueSplit = value.split(",");
			if (valueSplit.length != 2)
			{
				LOGGER.warning("parseItemsList[RelicSystemConfig.load()]: invalid entry -> " + valueSplit[0] + ", should be itemId,itemNumber. Skipping to the next entry in the list.");
				continue;
			}
			
			int itemId = -1;
			try
			{
				itemId = Integer.parseInt(valueSplit[0]);
			}
			catch (NumberFormatException e)
			{
				LOGGER.warning("parseItemsList[RelicSystemConfig.load()]: invalid itemId -> " + valueSplit[0] + ", value must be an integer. Skipping to the next entry in the list.");
				continue;
			}
			
			int count = -1;
			try
			{
				count = Integer.parseInt(valueSplit[1]);
			}
			catch (NumberFormatException e)
			{
				LOGGER.warning("parseItemsList[RelicSystemConfig.load()]: invalid item number -> " + valueSplit[1] + ", value must be an integer. Skipping to the next entry in the list.");
				continue;
			}
			
			if ((itemId > 0) && (count > 0))
			{
				result.add(new ItemHolder(itemId, count));
			}
		}
		
		return result;
	}
}
