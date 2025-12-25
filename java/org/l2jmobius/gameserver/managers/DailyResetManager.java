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
package org.l2jmobius.gameserver.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.time.TimeUtil;
import org.l2jmobius.gameserver.config.AttendanceRewardsConfig;
import org.l2jmobius.gameserver.config.GeneralConfig;
import org.l2jmobius.gameserver.config.HuntPassConfig;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.config.TrainingCampConfig;
import org.l2jmobius.gameserver.config.WorldExchangeConfig;
import org.l2jmobius.gameserver.data.holders.LimitShopProductHolder;
import org.l2jmobius.gameserver.data.holders.TimedHuntingZoneHolder;
import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.data.xml.LimitShopCraftData;
import org.l2jmobius.gameserver.data.xml.LimitShopData;
import org.l2jmobius.gameserver.data.xml.MableGameData;
import org.l2jmobius.gameserver.data.xml.PrimeShopData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.TimedHuntingZoneData;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.SubClassHolder;
import org.l2jmobius.gameserver.model.actor.stat.PlayerStat;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanMember;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.holders.OnDailyReset;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.olympiad.Olympiad;
import org.l2jmobius.gameserver.model.primeshop.PrimeShopGroup;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.variables.AccountVariables;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2jmobius.gameserver.network.serverpackets.ExWorldChatCnt;

/**
 * @author Mobius
 */
public class DailyResetManager
{
	private static final Logger LOGGER = Logger.getLogger(DailyResetManager.class.getName());
	
	private static final Set<Integer> RESET_SKILLS = new HashSet<>();
	static
	{
		RESET_SKILLS.add(2510); // Wondrous Cubic
		RESET_SKILLS.add(22180); // Wondrous Cubic - 1 time use
	}
	public static final Set<Integer> RESET_ITEMS = new HashSet<>();
	static
	{
		RESET_ITEMS.add(47387); // Balthus Knights Supply Items
		RESET_ITEMS.add(60011); // Festival Fairy's Good Luck Bag
	}
	public static final Set<String> RESET_CLAN_INSTANCES = new HashSet<>();
	static
	{
		RESET_CLAN_INSTANCES.add("TOH_GOLDBERG_DONE"); // Goldberg Instance
		RESET_CLAN_INSTANCES.add("TOH_MARYREED_DONE"); // MaryReed Instance
		RESET_CLAN_INSTANCES.add("TOH_TAUTI_DONE"); // Tauti Instance
	}
	
	protected DailyResetManager()
	{
		// Schedule reset everyday at 6:30.
		final long nextResetTime = TimeUtil.getNextTime(6, 30).getTimeInMillis();
		final long currentTime = System.currentTimeMillis();
		
		// Get today's 6:30 AM timestamp.
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 6);
		calendar.set(Calendar.MINUTE, 30);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		final long currentResetTime = calendar.getTimeInMillis();
		
		// Check if 24 hours have passed since the last daily reset.
		if ((currentTime < currentResetTime) || (GlobalVariablesManager.getInstance().getLong(GlobalVariablesManager.DAILY_TASK_RESET, 0) > currentResetTime))
		{
			LOGGER.info(getClass().getSimpleName() + ": Next schedule at " + TimeUtil.getDateTimeString(nextResetTime) + ".");
		}
		else
		{
			LOGGER.info(getClass().getSimpleName() + ": Daily task will run now.");
			onReset();
		}
		
		// Daily reset task.
		final long startDelay = Math.max(0, nextResetTime - currentTime);
		ThreadPool.scheduleAtFixedRate(this::onReset, startDelay, 86400000); // 86400000 = 1 day
		
		// Global save task.
		ThreadPool.scheduleAtFixedRate(this::onSave, 1800000, 1800000); // 1800000 = 30 minutes
	}
	
	private void onReset()
	{
		LOGGER.info("Starting reset of daily tasks...");
		
		// Store last reset time.
		GlobalVariablesManager.getInstance().set(GlobalVariablesManager.DAILY_TASK_RESET, System.currentTimeMillis());
		
		// Wednesday weekly tasks.
		final Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
		{
			clanLeaderApply();
			resetClanContribution();
			resetDailyMissionRewards();
			resetTimedHuntingZonesWeekly();
			resetVitalityWeekly();
			resetPrivateStoreHistory();
			resetWeeklyLimitShopData();
			resetThroneOfHeroesWeekly();
		}
		else // All days, except Wednesday.
		{
			resetVitalityDaily();
		}
		
		if (HuntPassConfig.ENABLE_HUNT_PASS && (calendar.get(Calendar.DAY_OF_MONTH) == HuntPassConfig.HUNT_PASS_PERIOD))
		{
			resetHuntPass();
		}
		
		if (calendar.get(Calendar.DAY_OF_MONTH) == 1)
		{
			resetCeremonyOfChaos();
			resetMonthlyLimitShopData();
		}
		
		// Daily tasks.
		resetAttendanceRewards();
		resetDailySkills();
		resetDailyItems();
		resetDailyPrimeShopData();
		resetDailyLimitShopData();
		resetHomunculusResetPoints();
		resetRecommends();
		resetTimedHuntingZones();
		resetTrainingCamp();
		resetWorldChatPoints();
		checkWeekSwap();
		
		// Trigger daily reset event.
		if (EventDispatcher.getInstance().hasListener(EventType.ON_DAILY_RESET))
		{
			EventDispatcher.getInstance().notifyEvent(new OnDailyReset());
		}
		
		// Store player variables.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getVariables().storeMe();
			player.getAccountVariables().storeMe();
		}
		
		LOGGER.info("Daily tasks reset completed.");
	}
	
	private void checkWeekSwap()
	{
		final long nextEvenWeekSwap = GlobalVariablesManager.getInstance().getLong(GlobalVariablesManager.NEXT_EVEN_WEEK_SWAP, 0);
		if (nextEvenWeekSwap < System.currentTimeMillis())
		{
			final boolean isEvenWeek = GlobalVariablesManager.getInstance().getBoolean(GlobalVariablesManager.IS_EVEN_WEEK, true);
			GlobalVariablesManager.getInstance().set(GlobalVariablesManager.IS_EVEN_WEEK, !isEvenWeek);
			final Calendar calendar = TimeUtil.getNextDayTime(Calendar.WEDNESDAY, 6, 25);
			GlobalVariablesManager.getInstance().set(GlobalVariablesManager.NEXT_EVEN_WEEK_SWAP, calendar.getTimeInMillis());
		}
	}
	
	private void onSave()
	{
		GlobalVariablesManager.getInstance().storeMe();
		
		if (WorldExchangeConfig.WORLD_EXCHANGE_LAZY_UPDATE)
		{
			WorldExchangeManager.getInstance().storeMe();
		}
		
		if (Olympiad.getInstance().inCompPeriod())
		{
			Olympiad.getInstance().saveOlympiadStatus();
			LOGGER.info("Olympiad System: Data updated.");
		}
		
		MableGameData.getInstance().save();
	}
	
	private void clanLeaderApply()
	{
		for (Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getNewLeaderId() != 0)
			{
				final ClanMember member = clan.getClanMember(clan.getNewLeaderId());
				if (member == null)
				{
					continue;
				}
				
				clan.setNewLeader(member);
			}
		}
		
		LOGGER.info("Clan leaders have been updated.");
	}
	
	private void resetVitalityDaily()
	{
		if (!PlayerConfig.ENABLE_VITALITY)
		{
			return;
		}
		
		int vitality = PlayerStat.MAX_VITALITY_POINTS / 4;
		for (Player player : World.getInstance().getPlayers())
		{
			final int VP = player.getVitalityPoints();
			player.setVitalityPoints(VP + vitality, false);
			for (SubClassHolder subclass : player.getSubClasses().values())
			{
				final int VPS = subclass.getVitalityPoints();
				subclass.setVitalityPoints(VPS + vitality);
			}
		}
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement st = con.prepareStatement("UPDATE character_subclasses SET vitality_points = IF(vitality_points = ?, vitality_points, vitality_points + ?)"))
			{
				st.setInt(1, PlayerStat.MAX_VITALITY_POINTS);
				st.setInt(2, PlayerStat.MAX_VITALITY_POINTS / 4);
				st.execute();
			}
			
			try (PreparedStatement st = con.prepareStatement("UPDATE characters SET vitality_points = IF(vitality_points = ?, vitality_points, vitality_points + ?)"))
			{
				st.setInt(1, PlayerStat.MAX_VITALITY_POINTS);
				st.setInt(2, PlayerStat.MAX_VITALITY_POINTS / 4);
				st.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error while updating vitality", e);
		}
		
		LOGGER.info("Daily vitality added successfully.");
	}
	
	private void resetVitalityWeekly()
	{
		if (!PlayerConfig.ENABLE_VITALITY)
		{
			return;
		}
		
		for (Player player : World.getInstance().getPlayers())
		{
			player.setVitalityPoints(PlayerStat.MAX_VITALITY_POINTS, false);
			for (SubClassHolder subclass : player.getSubClasses().values())
			{
				subclass.setVitalityPoints(PlayerStat.MAX_VITALITY_POINTS);
			}
		}
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement st = con.prepareStatement("UPDATE character_subclasses SET vitality_points = ?"))
			{
				st.setInt(1, PlayerStat.MAX_VITALITY_POINTS);
				st.execute();
			}
			
			try (PreparedStatement st = con.prepareStatement("UPDATE characters SET vitality_points = ?"))
			{
				st.setInt(1, PlayerStat.MAX_VITALITY_POINTS);
				st.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error while updating vitality", e);
		}
		
		LOGGER.info("Vitality points have been reset.");
	}
	
	private void resetDailySkills()
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection())
		{
			for (int skillId : RESET_SKILLS)
			{
				try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills_save WHERE skill_id=? AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
				{
					ps.setInt(1, skillId);
					ps.execute();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not reset daily skill reuse: ", e);
		}
		
		// Update data for online players.
		// final Set<Player> updates = new HashSet<>();
		for (int skillId : RESET_SKILLS)
		{
			final Skill skill = SkillData.getInstance().getSkill(skillId, 1 /* No known need for more levels */);
			if (skill != null)
			{
				for (Player player : World.getInstance().getPlayers())
				{
					if (player.hasSkillReuse(skill.getReuseHashCode()))
					{
						player.removeTimeStamp(skill);
						// updates.add(player);
					}
				}
			}
		}
		
		// for (Player player : updates)
		// {
		// player.sendSkillList();
		// }
		
		LOGGER.info("Daily skill reuse cleaned.");
	}
	
	private void resetDailyItems()
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection())
		{
			for (int itemId : RESET_ITEMS)
			{
				try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_item_reuse_save WHERE itemId=? AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
				{
					ps.setInt(1, itemId);
					ps.execute();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not reset daily item reuse: ", e);
		}
		
		// Update data for online players.
		boolean update;
		for (Player player : World.getInstance().getPlayers())
		{
			update = false;
			for (int itemId : RESET_ITEMS)
			{
				for (Item item : player.getInventory().getAllItemsByItemId(itemId))
				{
					player.getItemReuseTimeStamps().remove(item.getObjectId());
					update = true;
				}
			}
			
			if (update)
			{
				player.sendItemList();
			}
		}
		
		LOGGER.info("Daily item reuse cleaned.");
	}
	
	private void resetWorldChatPoints()
	{
		if (!GeneralConfig.ENABLE_WORLD_CHAT)
		{
			return;
		}
		
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE character_variables SET val = ? WHERE var = ? AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
		{
			ps.setInt(1, 0);
			ps.setString(2, PlayerVariables.WORLD_CHAT_VARIABLE_NAME);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not reset daily world chat points: ", e);
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.setWorldChatUsed(0);
			player.sendPacket(new ExWorldChatCnt(player));
		}
		
		LOGGER.info("Daily world chat points have been reset.");
	}
	
	private void resetRecommends()
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("UPDATE character_reco_bonus SET rec_left = ?, rec_have = 0 WHERE rec_have <= 20"))
			{
				ps.setInt(1, 0); // Rec left = 0
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("UPDATE character_reco_bonus SET rec_left = ?, rec_have = GREATEST(rec_have - 20,0) WHERE rec_have > 20"))
			{
				ps.setInt(1, 0); // Rec left = 0
				ps.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not reset Recommendations System: ", e);
		}
		
		for (Player player : World.getInstance().getPlayers())
		{
			player.setRecomLeft(0);
			player.setRecomHave(player.getRecomHave() - 20);
			player.sendPacket(new ExVoteSystemInfo(player));
			player.broadcastUserInfo();
		}
	}
	
	private void resetTrainingCamp()
	{
		if (TrainingCampConfig.TRAINING_CAMP_ENABLE)
		{
			// Update data for offline players.
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ? AND account_name NOT IN (SELECT account_name FROM characters WHERE online = 1)"))
			{
				ps.setString(1, "TRAINING_CAMP_DURATION");
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Could not reset Training Camp: ", e);
			}
			
			// Update data for online players.
			for (Player player : World.getInstance().getPlayers())
			{
				player.resetTraingCampDuration();
			}
			
			LOGGER.info("Training Camp durations have been reset.");
		}
	}
	
	private void resetDailyMissionRewards()
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var = ? AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
		{
			ps.setString(1, PlayerVariables.DAILY_MISSION_COUNT);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not reset Clan Mission Rewards: ", e);
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getVariables().remove(PlayerVariables.DAILY_MISSION_COUNT);
		}
		
		LOGGER.info("Clan Mission Rewards have been reset.");
	}
	
	private void resetClanContribution()
	{
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getVariables().set(PlayerVariables.CLAN_CONTRIBUTION_PREVIOUS, player.getClanContribution());
			player.getVariables().set(PlayerVariables.CLAN_CONTRIBUTION_TOTAL_PREVIOUS, player.getClanContributionTotal());
			player.getVariables().remove(PlayerVariables.CLAN_CONTRIBUTION);
		}
		
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE character_variables SET var = 'CLAN_CONTRIBUTION_PREVIOUS' WHERE `var` = 'CLAN_CONTRIBUTION' AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
		{
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not reset Clan contributions: ", e);
		}
		
		LOGGER.info("Clan contributions have been reset.");
	}
	
	private void resetThroneOfHeroesWeekly()
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection())
		{
			for (String clanInstances : RESET_CLAN_INSTANCES)
			{
				try (PreparedStatement ps = con.prepareStatement("DELETE FROM clan_variables WHERE var=?"))
				{
					ps.setString(1, clanInstances);
					ps.execute();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Could not reset Throne of Heroes entries: " + e);
		}
		
		// Update data for online players.
		final Set<Clan> clans = new HashSet<>();
		for (Player player : World.getInstance().getPlayers())
		{
			final Clan clan = player.getClan();
			if (clan != null)
			{
				clans.add(clan);
			}
		}
		
		for (Clan clan : clans)
		{
			clan.getVariables().remove("TOH_GOLDBERG_DONE");
			clan.getVariables().remove("TOH_MARYREED_DONE");
			clan.getVariables().remove("TOH_TAUTI_DONE");
		}
		
		LOGGER.info("Throne of Heroes entries have been reset.");
	}
	
	private void resetTimedHuntingZones()
	{
		for (TimedHuntingZoneHolder holder : TimedHuntingZoneData.getInstance().getAllHuntingZones())
		{
			if (holder.isWeekly())
			{
				continue;
			}
			
			// Update data for offline players.
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var IN (?, ?, ?) AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
			{
				ps.setString(1, PlayerVariables.HUNTING_ZONE_ENTRY + holder.getZoneId());
				ps.setString(2, PlayerVariables.HUNTING_ZONE_TIME + holder.getZoneId());
				ps.setString(3, PlayerVariables.HUNTING_ZONE_REMAIN_REFILL + holder.getZoneId());
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Could not reset Special Hunting Zones: ", e);
			}
			
			// Update data for online players.
			for (Player player : World.getInstance().getPlayers())
			{
				player.getVariables().remove(PlayerVariables.HUNTING_ZONE_ENTRY + holder.getZoneId());
				player.getVariables().remove(PlayerVariables.HUNTING_ZONE_TIME + holder.getZoneId());
				player.getVariables().remove(PlayerVariables.HUNTING_ZONE_REMAIN_REFILL + holder.getZoneId());
			}
		}
		
		LOGGER.info("Special Hunting Zones have been reset.");
	}
	
	private void resetTimedHuntingZonesWeekly()
	{
		for (TimedHuntingZoneHolder holder : TimedHuntingZoneData.getInstance().getAllHuntingZones())
		{
			if (!holder.isWeekly())
			{
				continue;
			}
			
			// Update data for offline players.
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var IN (?, ?, ?) AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
			{
				ps.setString(1, PlayerVariables.HUNTING_ZONE_ENTRY + holder.getZoneId());
				ps.setString(2, PlayerVariables.HUNTING_ZONE_TIME + holder.getZoneId());
				ps.setString(3, PlayerVariables.HUNTING_ZONE_REMAIN_REFILL + holder.getZoneId());
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Could not reset Weekly Special Hunting Zones: ", e);
			}
			
			// Update data for online players.
			for (Player player : World.getInstance().getPlayers())
			{
				player.getVariables().remove(PlayerVariables.HUNTING_ZONE_ENTRY + holder.getZoneId());
				player.getVariables().remove(PlayerVariables.HUNTING_ZONE_TIME + holder.getZoneId());
				player.getVariables().remove(PlayerVariables.HUNTING_ZONE_REMAIN_REFILL + holder.getZoneId());
			}
		}
		
		LOGGER.info("Weekly Special Hunting Zones have been reset.");
	}
	
	private void resetHomunculusResetPoints()
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("UPDATE character_variables SET val = ? WHERE var IN (?, ?, ?, ?) AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
			{
				ps.setInt(1, 0);
				ps.setString(2, PlayerVariables.HOMUNCULUS_USED_RESET_VP);
				ps.setString(3, PlayerVariables.HOMUNCULUS_USED_VP_CONVERT);
				ps.setString(4, PlayerVariables.HOMUNCULUS_USED_RESET_KILLS);
				ps.setString(5, PlayerVariables.HOMUNCULUS_USED_KILL_CONVERT);
				ps.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Could not reset Homunculus Reset Points: " + e);
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_RESET_VP, 0);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_VP_CONVERT, 0);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_RESET_KILLS, 0);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_USED_KILL_CONVERT, 0);
		}
		
		LOGGER.info("Homunculus Reset Points have been reset.");
	}
	
	private void resetAttendanceRewards()
	{
		if (AttendanceRewardsConfig.ATTENDANCE_REWARDS_SHARE_ACCOUNT)
		{
			// Update data for offline players.
			try (Connection con = DatabaseFactory.getConnection())
			{
				try (PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ? AND account_name NOT IN (SELECT account_name FROM characters WHERE online = 1)"))
				{
					ps.setString(1, PlayerVariables.ATTENDANCE_DATE);
					ps.execute();
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Could not reset Attendance Rewards: " + e);
			}
			
			// Update data for online players.
			for (Player player : World.getInstance().getPlayers())
			{
				player.getAccountVariables().remove(PlayerVariables.ATTENDANCE_DATE);
			}
			
			LOGGER.info("Account shared Attendance Rewards have been reset.");
		}
		else
		{
			// Update data for offline players.
			try (Connection con = DatabaseFactory.getConnection())
			{
				try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var = ? AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
				{
					ps.setString(1, PlayerVariables.ATTENDANCE_DATE);
					ps.execute();
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Could not reset Attendance Rewards: " + e);
			}
			
			// Update data for online players.
			for (Player player : World.getInstance().getPlayers())
			{
				player.getVariables().remove(PlayerVariables.ATTENDANCE_DATE);
			}
			
			LOGGER.info("Attendance Rewards have been reset.");
		}
	}
	
	private void resetDailyPrimeShopData()
	{
		for (PrimeShopGroup holder : PrimeShopData.getInstance().getPrimeItems().values())
		{
			// Update data for offline players.
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ? AND account_name NOT IN (SELECT account_name FROM characters WHERE online = 1)"))
			{
				ps.setString(1, AccountVariables.PRIME_SHOP_PRODUCT_DAILY_COUNT + holder.getBrId());
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Could not reset PrimeShopData: " + e);
			}
			
			// Update data for online players.
			for (Player player : World.getInstance().getPlayers())
			{
				player.getAccountVariables().remove(AccountVariables.PRIME_SHOP_PRODUCT_DAILY_COUNT + holder.getBrId());
			}
		}
		
		LOGGER.info("PrimeShopData have been reset.");
	}
	
	private void resetDailyLimitShopData()
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ? AND account_name NOT IN (SELECT account_name FROM characters WHERE online = 1)"))
		{
			for (LimitShopProductHolder holder : LimitShopData.getInstance().getProducts())
			{
				ps.setString(1, AccountVariables.LCOIN_SHOP_PRODUCT_DAILY_COUNT + holder.getProductionId());
				ps.executeUpdate();
			}
			
			for (LimitShopProductHolder holder : LimitShopCraftData.getInstance().getProducts())
			{
				ps.setString(1, AccountVariables.LCOIN_SHOP_PRODUCT_DAILY_COUNT + holder.getProductionId());
				ps.executeUpdate();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Could not reset LimitShopData: " + e);
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			for (LimitShopProductHolder holder : LimitShopData.getInstance().getProducts())
			{
				player.getAccountVariables().remove(AccountVariables.LCOIN_SHOP_PRODUCT_DAILY_COUNT + holder.getProductionId());
			}
			
			for (LimitShopProductHolder holder : LimitShopCraftData.getInstance().getProducts())
			{
				player.getAccountVariables().remove(AccountVariables.LCOIN_SHOP_PRODUCT_DAILY_COUNT + holder.getProductionId());
			}
		}
		
		LOGGER.info("Daily LimitShopData have been reset.");
	}
	
	private void resetWeeklyLimitShopData()
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ? AND account_name NOT IN (SELECT account_name FROM characters WHERE online = 1)"))
		{
			for (LimitShopProductHolder holder : LimitShopData.getInstance().getProducts())
			{
				ps.setString(1, AccountVariables.LCOIN_SHOP_PRODUCT_WEEKLY_COUNT + holder.getProductionId());
				ps.executeUpdate();
			}
			
			for (LimitShopProductHolder holder : LimitShopCraftData.getInstance().getProducts())
			{
				ps.setString(1, AccountVariables.LCOIN_SHOP_PRODUCT_WEEKLY_COUNT + holder.getProductionId());
				ps.executeUpdate();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Could not reset LimitShopData: " + e);
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			for (LimitShopProductHolder holder : LimitShopData.getInstance().getProducts())
			{
				player.getAccountVariables().remove(AccountVariables.LCOIN_SHOP_PRODUCT_WEEKLY_COUNT + holder.getProductionId());
			}
			
			for (LimitShopProductHolder holder : LimitShopCraftData.getInstance().getProducts())
			{
				player.getAccountVariables().remove(AccountVariables.LCOIN_SHOP_PRODUCT_WEEKLY_COUNT + holder.getProductionId());
			}
		}
		
		LOGGER.info("Weekly LimitShopData have been reset.");
	}
	
	private void resetMonthlyLimitShopData()
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ? AND account_name NOT IN (SELECT account_name FROM characters WHERE online = 1)"))
		{
			for (LimitShopProductHolder holder : LimitShopData.getInstance().getProducts())
			{
				ps.setString(1, AccountVariables.LCOIN_SHOP_PRODUCT_MONTHLY_COUNT + holder.getProductionId());
				ps.executeUpdate();
			}
			
			for (LimitShopProductHolder holder : LimitShopCraftData.getInstance().getProducts())
			{
				ps.setString(1, AccountVariables.LCOIN_SHOP_PRODUCT_MONTHLY_COUNT + holder.getProductionId());
				ps.executeUpdate();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Could not reset LimitShopData: " + e);
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			for (LimitShopProductHolder holder : LimitShopData.getInstance().getProducts())
			{
				player.getAccountVariables().remove(AccountVariables.LCOIN_SHOP_PRODUCT_MONTHLY_COUNT + holder.getProductionId());
			}
			
			for (LimitShopProductHolder holder : LimitShopCraftData.getInstance().getProducts())
			{
				player.getAccountVariables().remove(AccountVariables.LCOIN_SHOP_PRODUCT_MONTHLY_COUNT + holder.getProductionId());
			}
		}
		
		LOGGER.info("Monthly LimitShopData have been reset.");
	}
	
	private void resetCeremonyOfChaos()
	{
		// Set monthly true hero.
		GlobalVariablesManager.getInstance().set(GlobalVariablesManager.COC_TRUE_HERO, GlobalVariablesManager.getInstance().getInt(GlobalVariablesManager.COC_TOP_MEMBER, 0));
		GlobalVariablesManager.getInstance().set(GlobalVariablesManager.COC_TRUE_HERO_REWARDED, false);
		
		// Reset monthly winner.
		GlobalVariablesManager.getInstance().set(GlobalVariablesManager.COC_TOP_MARKS, 0);
		GlobalVariablesManager.getInstance().set(GlobalVariablesManager.COC_TOP_MEMBER, 0);
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var = ? AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
		{
			ps.setString(1, PlayerVariables.CEREMONY_OF_CHAOS_MARKS);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.severe(getClass().getSimpleName() + ": Could not reset Ceremony Of Chaos victories: " + e);
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getVariables().remove(PlayerVariables.CEREMONY_OF_CHAOS_MARKS);
		}
		
		LOGGER.info("Ceremony of Chaos variables have been reset.");
	}
	
	private void resetHuntPass()
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM huntpass"))
		{
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Could not delete entries from hunt pass: " + e);
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getHuntPass().resetHuntPass();
		}
		
		LOGGER.info("HuntPassData have been reset.");
	}
	
	public void resetPrivateStoreHistory()
	{
		try
		{
			PrivateStoreHistoryManager.getInstance().reset();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Could not reset private store history! " + e);
		}
		
		LOGGER.info("Private store history records have been reset.");
	}
	
	public static DailyResetManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DailyResetManager INSTANCE = new DailyResetManager();
	}
}
