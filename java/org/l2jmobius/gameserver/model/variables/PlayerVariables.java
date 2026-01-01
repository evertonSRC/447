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
package org.l2jmobius.gameserver.model.variables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.threads.ThreadPool;

/**
 * @author UnAfraid, Mobius
 */
public class PlayerVariables extends AbstractVariables
{
	private static final Logger LOGGER = Logger.getLogger(PlayerVariables.class.getName());
	
	// SQL Queries.
	private static final String SELECT_QUERY = "SELECT * FROM character_variables WHERE charId = ?";
	private static final String DELETE_QUERY = "DELETE FROM character_variables WHERE charId = ? AND var = ?";
	private static final String DELETE_ALL_QUERY = "DELETE FROM character_variables WHERE charId = ?";
	private static final String INSERT_QUERY = "INSERT INTO character_variables (charId, var, val) VALUES (?, ?, ?)";
	private static final String UPDATE_QUERY = "UPDATE character_variables SET val = ? WHERE charId = ? AND var = ?";
	
	// Asynchronous persistence.
	private static final long SAVE_INTERVAL = 60000; // 1 minute.
	private static final boolean ASYNC_SAVE_ENABLED = true;
	
	// Public variables.
	public static final String INSTANCE_ORIGIN = "INSTANCE_ORIGIN";
	public static final String INSTANCE_RESTORE = "INSTANCE_RESTORE";
	public static final String RESTORE_LOCATION = "RESTORE_LOCATION";
	public static final String INTRO_VIDEO = "INTRO_VIDEO";
	public static final String HAIR_ACCESSORY_VARIABLE_NAME = "HAIR_ACCESSORY_ENABLED";
	public static final String WORLD_CHAT_VARIABLE_NAME = "WORLD_CHAT_USED";
	public static final String VITALITY_ITEMS_USED_VARIABLE_NAME = "VITALITY_ITEMS_USED";
	public static final String UI_KEY_MAPPING = "UI_KEY_MAPPING";
	public static final String CLIENT_SETTINGS = "CLIENT_SETTINGS";
	public static final String ATTENDANCE_DATE = "ATTENDANCE_DATE";
	public static final String ATTENDANCE_INDEX = "ATTENDANCE_INDEX";
	public static final String DAILY_MISSION_COUNT = "DAILY_MISSION_COUNT";
	public static final String DAILY_MISSION_ONE_TIME = "DAILY_MISSION_ONE_TIME";
	public static final String CEREMONY_OF_CHAOS_SCORE = "CEREMONY_OF_CHAOS_SCORE";
	public static final String CEREMONY_OF_CHAOS_MARKS = "CEREMONY_OF_CHAOS_MARKS";
	public static final String ABILITY_PRESET = "ABILITY_PRESET";
	public static final String ABILITY_POINTS_MAIN_CLASS_SKILLS_A = "ABILITY_POINTS_MAIN_CLASS_SKILLS_A";
	public static final String ABILITY_POINTS_MAIN_CLASS_SKILLS_B = "ABILITY_POINTS_MAIN_CLASS_SKILLS_B";
	public static final String ABILITY_POINTS_DUAL_CLASS_SKILLS_A = "ABILITY_POINTS_DUA:_CLASS_SKILLS_A";
	public static final String ABILITY_POINTS_DUAL_CLASS_SKILLS_B = "ABILITY_POINTS_DUAL_CLASS_SKILLS_B";
	public static final String ABILITY_POINTS_USED_MAIN_CLASS_A = "ABILITY_POINTS_USED_MAIN_CLASS_A";
	public static final String ABILITY_POINTS_USED_MAIN_CLASS_B = "ABILITY_POINTS_USED_MAIN_CLASS_B";
	public static final String ABILITY_POINTS_USED_DUAL_CLASS_A = "ABILITY_POINTS_USED_DUAL_CLASS_A";
	public static final String ABILITY_POINTS_USED_DUAL_CLASS_B = "ABILITY_POINTS_USED_DUAL_CLASS_B";
	public static final String REVELATION_SKILL_1_MAIN_CLASS = "RevelationSkill1";
	public static final String REVELATION_SKILL_2_MAIN_CLASS = "RevelationSkill2";
	public static final String REVELATION_SKILL_1_DUAL_CLASS = "DualclassRevelationSkill1";
	public static final String REVELATION_SKILL_2_DUAL_CLASS = "DualclassRevelationSkill2";
	public static final String FORTUNE_TELLING_VARIABLE = "FortuneTelling";
	public static final String FORTUNE_TELLING_BLACK_CAT_VARIABLE = "FortuneTellingBlackCat";
	public static final String DELUSION_RETURN = "DELUSION_RETURN";
	public static final String CLAN_CONTRIBUTION = "CLAN_CONTRIBUTION";
	public static final String CLAN_CONTRIBUTION_TOTAL = "CLAN_CONTRIBUTION_TOTAL";
	public static final String CLAN_CONTRIBUTION_REWARDED = "CLAN_CONTRIBUTION_REWARDED";
	public static final String CLAN_CONTRIBUTION_REWARDED_COUNT = "CLAN_CONTRIBUTION_REWARDED_COUNT";
	public static final String CLAN_CONTRIBUTION_PREVIOUS = "CLAN_CONTRIBUTION_PREVIOUS";
	public static final String CLAN_CONTRIBUTION_TOTAL_PREVIOUS = "CLAN_CONTRIBUTION_TOTAL_PREVIOUS";
	public static final String BALTHUS_REWARD = "BALTHUS_REWARD";
	public static final String AUTO_USE_SETTINGS = "AUTO_USE_SETTINGS";
	public static final String AUTO_USE_SHORTCUTS = "AUTO_USE_SHORTCUTS";
	public static final String LAST_HUNTING_ZONE_ID = "LAST_HUNTING_ZONE_ID";
	public static final String HUNTING_ZONE_ENTRY = "HUNTING_ZONE_ENTRY_";
	public static final String HUNTING_ZONE_TIME = "HUNTING_ZONE_TIME_";
	public static final String HUNTING_ZONE_REMAIN_REFILL = "HUNTING_ZONE_REMAIN_REFILL_";
	public static final String IS_LEGEND = "IS_LEGEND";
	public static final String FAVORITE_TELEPORTS = "FAVORITE_TELEPORTS";
	public static final String HOMUNCULUS_HP_POINTS = "HOMUNCULUS_HP_POINTS";
	public static final String HOMUNCULUS_SP_POINTS = "HOMUNCULUS_SP_POINTS";
	public static final String HOMUNCULUS_VP_POINTS = "HOMUNCULUS_VP_POINTS";
	public static final String HOMUNCULUS_CREATION_TIME = "HOMUNCULUS_CREATION_TIME";
	public static final String HOMUNCULUS_UPGRADE_POINTS = "HOMUNCULUS_UPGRADE_POINTS";
	public static final String HOMUNCULUS_KILLED_MOBS = "HOMUNCULUS_KILLED_MOBS";
	public static final String HOMUNCULUS_USED_KILL_CONVERT = "HOMUNCULUS_USED_KILL_CONVERT";
	public static final String HOMUNCULUS_USED_RESET_KILLS = "HOMUNCULUS_USED_RESET_KILLS";
	public static final String HOMUNCULUS_USED_VP_POINTS = "HOMUNCULUS_USED_VP_POINTS";
	public static final String HOMUNCULUS_USED_VP_CONVERT = "HOMUNCULUS_USED_VP_CONVERT";
	public static final String HOMUNCULUS_USED_RESET_VP = "HOMUNCULUS_USED_RESET_VP";
	public static final String HOMUNCULUS_OPENED_SLOT_COUNT = "HOMUNCULUS_OPENED_SLOT_COUNT";
	public static final String HERO_BOOK_PROGRESS = "HERO_BOOK_PROGRESS";
	public static final String PRISON_WAIT_TIME = "PRISON_WAIT_TIME";
	public static final String PRISON_2_POINTS = "PRISON_2_POINTS";
	public static final String PRISON_3_POINTS = "PRISON_3_POINTS";
	public static final String CONQUEST_ORIGIN = "CONQUEST_ORIGIN";
	public static final String CONQUEST_NAME = "CONQUEST_NAME";
	public static final String CONQUEST_INTRO = "CONQUEST_INTRO";
	public static final String CONQUEST_ATTACK_POINTS = "CONQUEST_ATTACK_POINTS";
	public static final String CONQUEST_LIFE_POINTS = "CONQUEST_LIFE_POINTS";
	public static final String CONQUEST_PERSONAL_POINTS = "CONQUEST_PERSONAL_POINTS";
	public static final String CONQUEST_REWARDS_RECEIVED = "CONQUEST_REWARDS_RECEIVED";
	public static final String CONQUEST_SACRED_FIRE_SLOT_COUNT = "CONQUEST_SACRED_FIRE_SLOT_COUNT";
	public static final String CONQUEST_ABILITY_FIRE_SOURCE_LEVEL = "CONQUEST_ABILITY_FIRE_SOURCE_LEVEL";
	public static final String CONQUEST_ABILITY_FIRE_SOURCE_EXP = "CONQUEST_ABILITY_FIRE_SOURCE_EXP";
	public static final String CONQUEST_ABILITY_LIFE_SOURCE_LEVEL = "CONQUEST_ABILITY_LIFE_SOURCE_LEVEL";
	public static final String CONQUEST_ABILITY_LIFE_SOURCE_EXP = "CONQUEST_ABILITY_LIFE_SOURCE_EXP";
	public static final String CONQUEST_ABILITY_LIFE_SOURCE_RESET = "CONQUEST_ABILITY_LIFE_SOURCE_RESET";
	public static final String CONQUEST_ABILITY_LIFE_SOURCE_UPGRADES = "CONQUEST_ABILITY_LIFE_SOURCE_UPGRADES";
	public static final String CONQUEST_ABILITY_FLAME_SPARK_LEVEL = "CONQUEST_ABILITY_FLAME_SPARK_LEVEL";
	public static final String CONQUEST_ABILITY_FLAME_SPARK_EXP = "CONQUEST_ABILITY_FLAME_SPARK_EXP";
	public static final String CONQUEST_ABILITY_FLAME_SPARK_RESET = "CONQUEST_ABILITY_FLAME_SPARK_RESET";
	public static final String CONQUEST_ABILITY_FLAME_SPARK_UPGRADES = "CONQUEST_ABILITY_FLAME_SPARK_UPGRADES";
	public static final String CONQUEST_ABILITY_FIRE_TOTEM_LEVEL = "CONQUEST_ABILITY_FIRE_TOTEM_LEVEL";
	public static final String CONQUEST_ABILITY_FIRE_TOTEM_EXP = "CONQUEST_ABILITY_FIRE_TOTEM_EXP";
	public static final String CONQUEST_ABILITY_FIRE_TOTEM_RESET = "CONQUEST_ABILITY_FIRE_TOTEM_RESET";
	public static final String CONQUEST_ABILITY_FIRE_TOTEM_UPGRADES = "CONQUEST_ABILITY_FIRE_TOTEM_UPGRADES";
	public static final String CONQUEST_ABILITY_BATTLE_SOUL_LEVEL = "CONQUEST_ABILITY_BATTLE_SOUL_LEVEL";
	public static final String CONQUEST_ABILITY_BATTLE_SOUL_EXP = "CONQUEST_ABILITY_BATTLE_SOUL_EXP";
	public static final String CONQUEST_ABILITY_BATTLE_SOUL_RESET = "CONQUEST_ABILITY_BATTLE_SOUL_RESET";
	public static final String CONQUEST_ABILITY_BATTLE_SOUL_UPGRADES = "CONQUEST_ABILITY_BATTLE_SOUL_UPGRADES";
	public static final String ACTIVE_RELIC = "ACTIVE_RELIC";
	
	// Private variables.
	private final AtomicBoolean _scheduledSave = new AtomicBoolean(false);
	private final int _objectId;
	
	public PlayerVariables(int objectId)
	{
		_objectId = objectId;
		restoreMe();
	}
	
	public boolean restoreMe()
	{
		clearChangeTracking();
		
		// Restore previous variables.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement st = con.prepareStatement(SELECT_QUERY))
		{
			st.setInt(1, _objectId);
			try (ResultSet rset = st.executeQuery())
			{
				while (rset.next())
				{
					set(rset.getString("var"), rset.getString("val"), false);
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not restore variables for: " + _objectId, e);
			return false;
		}
		finally
		{
			compareAndSetChanges(true, false);
		}
		
		return true;
	}
	
	public boolean storeMe()
	{
		// No changes, nothing to store.
		if (!hasChanges())
		{
			return false;
		}
		
		// If async saving is enabled and not already scheduled, schedule a save.
		if (ASYNC_SAVE_ENABLED && !_scheduledSave.get())
		{
			_scheduledSave.set(true);
			ThreadPool.schedule(() ->
			{
				_scheduledSave.set(false);
				saveNow();
			}, SAVE_INTERVAL);
			return true;
		}
		
		return saveNow();
	}
	
	/**
	 * Force an immediate save of the variables.
	 * @return true if successful, false otherwise.
	 */
	public boolean saveNow()
	{
		if (!hasChanges())
		{
			return false;
		}
		
		// FIXME: May store after server shutdown.
		// If async is enabled, offload to ThreadPool.
		// if (ASYNC_SAVE_ENABLED)
		// {
		// ThreadPool.execute(this::saveNowSync);
		// return true;
		// }
		
		return saveNowSync();
	}
	
	/**
	 * Synchronous implementation of variable saving with optimized database operations.
	 * @return true if successful, false otherwise.
	 */
	private boolean saveNowSync()
	{
		_saveLock.lock();
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			// Process deletions.
			if (!_deleted.isEmpty())
			{
				try (PreparedStatement st = con.prepareStatement(DELETE_QUERY))
				{
					for (String name : _deleted)
					{
						st.setInt(1, _objectId);
						st.setString(2, name);
						st.addBatch();
					}
					
					st.executeBatch();
				}
			}
			
			// Process additions.
			if (!_added.isEmpty())
			{
				try (PreparedStatement st = con.prepareStatement(INSERT_QUERY))
				{
					for (String name : _added)
					{
						final Object value = getSet().get(name);
						if (value != null)
						{
							st.setInt(1, _objectId);
							st.setString(2, name);
							st.setString(3, String.valueOf(value));
							st.addBatch();
						}
					}
					
					st.executeBatch();
				}
			}
			
			// Process modifications.
			if (!_modified.isEmpty())
			{
				try (PreparedStatement st = con.prepareStatement(UPDATE_QUERY))
				{
					for (String name : _modified)
					{
						final Object value = getSet().get(name);
						if (value != null)
						{
							st.setString(1, String.valueOf(value));
							st.setInt(2, _objectId);
							st.setString(3, name);
							st.addBatch();
						}
					}
					
					st.executeBatch();
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not update variables for: " + _objectId, e);
			_saveLock.unlock();
			return false;
		}
		finally
		{
			clearChangeTracking();
			compareAndSetChanges(true, false);
			_saveLock.unlock();
		}
		
		return true;
	}
	
	public boolean deleteMe()
	{
		_saveLock.lock();
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement st = con.prepareStatement(DELETE_ALL_QUERY))
		{
			st.setInt(1, _objectId);
			st.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not delete variables for: " + _objectId, e);
			_saveLock.unlock();
			return false;
		}
		
		// Clear all variables.
		getSet().clear();
		clearChangeTracking();
		
		_saveLock.unlock();
		return true;
	}
}
