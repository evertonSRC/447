/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.enums.CategoryType;
import org.l2jmobius.gameserver.data.xml.CategoryData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.SkillTreeData;
import org.l2jmobius.gameserver.model.SkillLearn;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.PlayerClass;
import org.l2jmobius.gameserver.model.actor.holders.player.SubClassHolder;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;

/**
 * Manages the fourth class skill trees and learned skills.
 */
public class FourthClassSkillTreeManager
{
	private static final Logger LOGGER = Logger.getLogger(FourthClassSkillTreeManager.class.getName());
	private static final String LOAD_SKILLS = "SELECT skill_id, skill_level FROM player_fourthclass_skills WHERE player_id=? AND class_id=?";
	private static final String SAVE_SKILL = "INSERT INTO player_fourthclass_skills (player_id, class_id, skill_id, skill_level, acquired_time) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE skill_level=VALUES(skill_level), acquired_time=VALUES(acquired_time)";
	private static final String DELETE_SKILL = "DELETE FROM player_fourthclass_skills WHERE player_id=? AND class_id=? AND skill_id=? AND skill_level=?";
	private static final String DELETE_SKILLS = "DELETE FROM player_fourthclass_skills WHERE player_id=? AND class_id=?";
	private static final String LOAD_POINTS = "SELECT usedPoints, earnedPoints, lastLevelAwarded FROM character_fourthclass_points WHERE charId=? AND isDual=?";
	private static final String SAVE_POINTS = "INSERT INTO character_fourthclass_points (charId, isDual, usedPoints, earnedPoints, lastLevelAwarded, updatedAt) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE usedPoints=VALUES(usedPoints), earnedPoints=VALUES(earnedPoints), lastLevelAwarded=VALUES(lastLevelAwarded), updatedAt=CURRENT_TIMESTAMP";

	private final Set<Integer> _allSkillIds = ConcurrentHashMap.newKeySet();

	protected FourthClassSkillTreeManager()
	{
		load();
	}

	public void load()
	{
		_allSkillIds.clear();

		int totalSkills = 0;
		for (Map.Entry<PlayerClass, Map<Long, SkillLearn>> entry : SkillTreeData.getInstance().getFourthClassSkillTrees().entrySet())
		{
			final Map<Long, SkillLearn> tree = entry.getValue();
			totalSkills += tree.size();
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + tree.size() + " fourth class skills for classId " + entry.getKey().getId() + ".");
			for (SkillLearn learn : tree.values())
			{
				_allSkillIds.add(learn.getSkillId());
			}
		}

		LOGGER.info(getClass().getSimpleName() + ": Loaded " + totalSkills + " fourth class skills for " + SkillTreeData.getInstance().getFourthClassSkillTrees().size() + " class trees.");

		final Set<Integer> fourthClasses = CategoryData.getInstance().getCategoryByType(CategoryType.SIXTH_CLASS_GROUP);
		if (fourthClasses != null)
		{
			for (int classId : fourthClasses)
			{
				final PlayerClass playerClass = PlayerClass.getPlayerClass(classId);
				if ((playerClass == null) || SkillTreeData.getInstance().getFourthClassSkillTree(playerClass).isEmpty())
				{
					LOGGER.warning(getClass().getSimpleName() + ": Missing fourth class skill tree for classId: " + classId);
				}
			}
		}
	}

	public Collection<SkillLearn> loadSkillTreeForClass(int classId)
	{
		final PlayerClass playerClass = PlayerClass.getPlayerClass(classId);
		if (playerClass == null)
		{
			return Collections.emptyList();
		}

		return SkillTreeData.getInstance().getFourthClassSkillTree(playerClass).values();
	}

	public Map<Integer, Integer> getLearnedSkills(int playerId, int classId)
	{
		final Map<Integer, Integer> learned = new HashMap<>();
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(LOAD_SKILLS))
		{
			statement.setInt(1, playerId);
			statement.setInt(2, classId);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					learned.put(rset.getInt("skill_id"), rset.getInt("skill_level"));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not load fourth class skills for player " + playerId, e);
		}

		return learned;
	}

	public void applyLearnedSkills(Player player, int classId)
	{
		final Map<Integer, Integer> learned = getLearnedSkills(player.getObjectId(), classId);
		if (learned.isEmpty())
		{
			return;
		}

		final PlayerClass playerClass = PlayerClass.getPlayerClass(classId);
		if (playerClass == null)
		{
			return;
		}

		for (Map.Entry<Integer, Integer> entry : learned.entrySet())
		{
			final int skillId = entry.getKey();
			final int skillLevel = entry.getValue();
			if (SkillTreeData.getInstance().getFourthClassSkill(playerClass, skillId, skillLevel) == null)
			{
				continue;
			}

			final Skill known = player.getKnownSkill(skillId);
			if ((known != null) && (known.getLevel() >= skillLevel))
			{
				continue;
			}

			final Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
			if (skill == null)
			{
				LOGGER.warning(getClass().getSimpleName() + ": Missing skill data for " + skillId + " level " + skillLevel);
				continue;
			}

			player.addSkill(skill, false);
		}
	}

	public void loadPoints(Player player)
	{
		loadPoints(player, false);
		loadPoints(player, true);
	}

	public FourthClassPoints getPointsSummary(Player player, boolean dual)
	{
		final PointsState points = getPointsState(player, dual);
		final int remainingPoints = Math.max(0, points.getEarned() - points.getUsed());
		return new FourthClassPoints(points.getUsed(), remainingPoints, points.getEarned());
	}

	public void handleLevelUp(Player player, int oldLevel, int newLevel)
	{
		if (newLevel <= oldLevel)
		{
			return;
		}

		if (player.isSubClassActive() && !player.isDualClassActive())
		{
			return;
		}

		final boolean dual = player.isDualClassActive();
		final PointsState points = getPointsState(player, dual);
		final int startLevel = getPointsStartLevel();
		final int cap = getPointsCap();
		final int lastAwarded = Math.max(points.getLastLevelAwarded(), startLevel - 1);
		final int levelsToAward = Math.max(0, newLevel - lastAwarded);
		final int currentEarned = points.getEarned();
		final int updatedEarned = Math.min(cap, currentEarned + levelsToAward);
		final int updatedLastLevel = Math.max(points.getLastLevelAwarded(), newLevel);

		if ((levelsToAward > 0) && (updatedEarned > currentEarned))
		{
			LOGGER.fine(() -> getClass().getSimpleName() + ": " + player.getName() + " gained " + (updatedEarned - currentEarned) + " fourth class points (" + updatedEarned + "/" + cap + ") for level " + newLevel + (dual ? " [dual]" : " [main]") + ".");
		}
		else if ((levelsToAward > 0) && (currentEarned >= cap))
		{
			LOGGER.fine(() -> getClass().getSimpleName() + ": " + player.getName() + " reached fourth class points cap (" + cap + ") at level " + newLevel + (dual ? " [dual]" : " [main]") + ".");
		}

		if ((updatedEarned != currentEarned) || (updatedLastLevel != points.getLastLevelAwarded()))
		{
			savePoints(player, dual, points.getUsed(), updatedEarned, updatedLastLevel);
		}
	}

	public void removeAllFourthClassSkillsFromPlayer(Player player)
	{
		if (_allSkillIds.isEmpty())
		{
			return;
		}

		for (int skillId : _allSkillIds)
		{
			final Skill skill = player.getKnownSkill(skillId);
			if (skill != null)
			{
				player.removeSkill(skill, false, true);
			}
		}
	}

	public LearnResult resetFourthClassSkillTree(Player player)
	{
		final boolean dual = player.isDualClassActive();
		final int classId = player.getActiveClass();
		final PlayerClass playerClass = PlayerClass.getPlayerClass(classId);
		if (playerClass == null)
		{
			return LearnResult.fail("Classe inválida.");
		}

		final int costItemId = PlayerConfig.FOURTH_CLASS_SKILLTREE_RESET_COST_ITEM_ID;
		final long costItemCount = PlayerConfig.FOURTH_CLASS_SKILLTREE_RESET_COST_ITEM_COUNT;
		if ((costItemId > 0) && (costItemCount > 0))
		{
			final long available = player.getInventory().getInventoryItemCount(costItemId, -1);
			if (available < costItemCount)
			{
				return LearnResult.fail("Você não possui itens suficientes para resetar.");
			}
			if (!player.destroyItemByItemId(ItemProcessType.FEE, costItemId, costItemCount, player, true))
			{
				return LearnResult.fail("Falha ao consumir o item de reset.");
			}
		}

		final Map<Integer, Integer> learned = getLearnedSkills(player.getObjectId(), classId);
		for (Map.Entry<Integer, Integer> entry : learned.entrySet())
		{
			if (SkillTreeData.getInstance().getFourthClassSkill(playerClass, entry.getKey(), entry.getValue()) == null)
			{
				continue;
			}

			final Skill known = player.getKnownSkill(entry.getKey());
			if (known != null)
			{
				player.removeSkill(known, false, true);
			}
		}

		if (!deleteAllSkills(player, classId))
		{
			LOGGER.warning(getClass().getSimpleName() + ": Failed to delete fourth class skills from DB for " + player.getName() + ".");
		}

		final PointsState points = getPointsState(player, dual);
		if (!savePoints(player, dual, 0, points.getEarned(), points.getLastLevelAwarded()))
		{
			return LearnResult.fail("Não foi possível salvar o reset de pontos.");
		}

		player.sendSkillList();
		LOGGER.info(getClass().getSimpleName() + ": " + player.getName() + " reset fourth class skill tree points and skills (" + (dual ? "dual" : "main") + ").");
		return LearnResult.success("Skill tree resetada com sucesso.");
	}

	public LearnResult learnSkill(Player player, int skillId, int skillLevel)
	{
		final int classId = player.getActiveClass();
		final PlayerClass playerClass = PlayerClass.getPlayerClass(classId);
		if (playerClass == null)
		{
			return LearnResult.fail("Classe inválida.");
		}

		final SkillLearn entry = SkillTreeData.getInstance().getFourthClassSkill(playerClass, skillId, skillLevel);
		if (entry == null)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Skill " + skillId + " lvl " + skillLevel + " not found for classId " + classId);
			return LearnResult.fail("Skill não encontrada para a classe atual.");
		}

		final Map<Integer, Integer> learned = getLearnedSkills(player.getObjectId(), classId);
		final LearnValidation validation = validateLearn(player, playerClass, entry, learned, true);
		if (!validation.isSuccess())
		{
			LOGGER.warning(getClass().getSimpleName() + ": " + player.getName() + " failed to learn skill " + skillId + " lvl " + skillLevel + " - " + validation.getMessage());
			return LearnResult.fail(validation.getMessage());
		}

		if (!consumeItems(player, validation.getItemsToConsume()))
		{
			return LearnResult.fail("Falha ao consumir os itens necessários.");
		}

		final Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
		if (skill == null)
		{
			refundItems(player, validation.getItemsToConsume());
			return LearnResult.fail("Skill inválida.");
		}

		player.addSkill(skill, false);
		if (!saveSkill(player, classId, skillId, skillLevel))
		{
			player.removeSkill(skill, false, true);
			refundItems(player, validation.getItemsToConsume());
			return LearnResult.fail("Não foi possível salvar a skill.");
		}
		
		final int pointsCost = getSkillPointsCost(entry);
		if (pointsCost > 0)
		{
			final boolean dual = player.isDualClassActive();
			final PointsState points = getPointsState(player, dual);
			final int updatedUsed = points.getUsed() + pointsCost;
			if (!savePoints(player, dual, updatedUsed, points.getEarned(), points.getLastLevelAwarded()))
			{
				player.removeSkill(skill, false, true);
				refundItems(player, validation.getItemsToConsume());
				deleteSkill(player, classId, skillId, skillLevel);
				return LearnResult.fail("Não foi possível salvar os pontos da fourth class.");
			}
			LOGGER.fine(() -> getClass().getSimpleName() + ": " + player.getName() + " spent " + pointsCost + " fourth class points (" + updatedUsed + "/" + points.getEarned() + ") for skill " + skillId + " lvl " + skillLevel + (dual ? " [dual]" : " [main]") + ".");
		}

		player.sendSkillList();
		return LearnResult.success("Skill aprendida com sucesso.");
	}

	public boolean canLearnSkill(Player player, PlayerClass playerClass, SkillLearn entry, Map<Integer, Integer> learned)
	{
		return validateLearn(player, playerClass, entry, learned, true).isSuccess();
	}

	private boolean saveSkill(Player player, int classId, int skillId, int skillLevel)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(SAVE_SKILL))
		{
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, classId);
			statement.setInt(3, skillId);
			statement.setInt(4, skillLevel);
			statement.setLong(5, System.currentTimeMillis());
			statement.execute();
			return true;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not save learned skill for player " + player.getName(), e);
			return false;
		}
	}

	private boolean deleteSkill(Player player, int classId, int skillId, int skillLevel)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_SKILL))
		{
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, classId);
			statement.setInt(3, skillId);
			statement.setInt(4, skillLevel);
			statement.execute();
			return true;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not delete learned skill for player " + player.getName(), e);
			return false;
		}
	}

	private boolean deleteAllSkills(Player player, int classId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_SKILLS))
		{
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, classId);
			statement.execute();
			return true;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not delete fourth class skills for player " + player.getName(), e);
			return false;
		}
	}

	private PointsState getPointsState(Player player, boolean dual)
	{
		if (player.hasFourthClassPointsLoaded(dual))
		{
			return new PointsState(player.getFourthClassUsedPoints(dual), player.getFourthClassEarnedPoints(dual), player.getFourthClassLastLevelAwarded(dual));
		}

		return loadPoints(player, dual);
	}

	private PointsState loadPoints(Player player, boolean dual)
	{
		int usedPoints = 0;
		int earnedPoints = 0;
		int lastLevelAwarded = 0;
		boolean found = false;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(LOAD_POINTS))
		{
			statement.setInt(1, player.getObjectId());
			statement.setBoolean(2, dual);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					found = true;
					usedPoints = rset.getInt("usedPoints");
					earnedPoints = rset.getInt("earnedPoints");
					lastLevelAwarded = rset.getInt("lastLevelAwarded");
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not load fourth class points for player " + player.getName(), e);
		}

		final int cap = getPointsCap();
		final int currentLevel = getCurrentClassLevel(player, dual);
		boolean updated = false;
		if (!found)
		{
			earnedPoints = getInitialEarnedPoints(currentLevel, cap);
			lastLevelAwarded = currentLevel;
			updated = true;
		}
		else
		{
			if (earnedPoints < 0)
			{
				earnedPoints = 0;
				updated = true;
			}
			if (earnedPoints > cap)
			{
				earnedPoints = cap;
				updated = true;
			}
			if (usedPoints < 0)
			{
				usedPoints = 0;
				updated = true;
			}
			if (usedPoints > earnedPoints)
			{
				LOGGER.warning(getClass().getSimpleName() + ": Adjusting fourth class used points for " + player.getName() + " (" + usedPoints + " > " + earnedPoints + ").");
				usedPoints = earnedPoints;
				updated = true;
			}
			if (lastLevelAwarded < 0)
			{
				lastLevelAwarded = currentLevel;
				updated = true;
			}
		}

		if (updated)
		{
			savePoints(player, dual, usedPoints, earnedPoints, lastLevelAwarded);
		}

		player.setFourthClassUsedPoints(dual, usedPoints);
		player.setFourthClassEarnedPoints(dual, earnedPoints);
		player.setFourthClassLastLevelAwarded(dual, lastLevelAwarded);
		return new PointsState(usedPoints, earnedPoints, lastLevelAwarded);
	}

	private boolean savePoints(Player player, boolean dual, int usedPoints, int earnedPoints, int lastLevelAwarded)
	{
		final int cap = getPointsCap();
		final int normalizedEarned = Math.min(cap, Math.max(0, earnedPoints));
		final int normalizedUsed = Math.max(0, Math.min(usedPoints, normalizedEarned));
		final int normalizedLastLevelAwarded = Math.max(0, lastLevelAwarded);
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(SAVE_POINTS))
		{
			statement.setInt(1, player.getObjectId());
			statement.setBoolean(2, dual);
			statement.setInt(3, normalizedUsed);
			statement.setInt(4, normalizedEarned);
			statement.setInt(5, normalizedLastLevelAwarded);
			statement.execute();
			player.setFourthClassUsedPoints(dual, normalizedUsed);
			player.setFourthClassEarnedPoints(dual, normalizedEarned);
			player.setFourthClassLastLevelAwarded(dual, normalizedLastLevelAwarded);
			return true;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not save fourth class points for player " + player.getName(), e);
			return false;
		}
	}

	private int getPointsCap()
	{
		return Math.max(0, PlayerConfig.FOURTH_CLASS_SKILLTREE_POINTS_CAP);
	}

	private int getPointsStartLevel()
	{
		return Math.max(1, PlayerConfig.FOURTH_CLASS_SKILLTREE_POINTS_START_LEVEL);
	}

	private int getInitialEarnedPoints(int level, int cap)
	{
		final int startLevel = getPointsStartLevel();
		final int earned = Math.max(0, level - startLevel + 1);
		return Math.min(cap, earned);
	}

	private int getCurrentClassLevel(Player player, boolean dual)
	{
		if (dual)
		{
			final SubClassHolder dualClass = player.getDualClass();
			return dualClass != null ? dualClass.getLevel() : 0;
		}

		return player.getStat().getBaseLevel();
	}

	private int getSkillPointsCost(SkillLearn entry)
	{
		return Math.max(0, entry.getPointsRequired());
	}

	private LearnValidation validateLearn(Player player, PlayerClass playerClass, SkillLearn entry, Map<Integer, Integer> learned, boolean checkItems)
	{
		final int skillId = entry.getSkillId();
		final int skillLevel = entry.getSkillLevel();
		final int currentLevel = getCurrentLevel(player, learned, skillId);
		if (currentLevel >= skillLevel)
		{
			return LearnValidation.fail("Esta skill já foi aprendida.");
		}

		if ((skillLevel > 1) && (currentLevel != (skillLevel - 1)))
		{
			return LearnValidation.fail("Você precisa aprender o nível anterior desta skill.");
		}

		if (player.getLevel() < entry.getGetLevel())
		{
			return LearnValidation.fail("Você precisa do nível " + entry.getGetLevel() + " para aprender.");
		}

		for (SkillHolder prereq : entry.getPreReqSkills())
		{
			if (player.getSkillLevel(prereq.getSkillId()) < prereq.getSkillLevel())
			{
				return LearnValidation.fail("Pré-requisito não atendido.");
			}
		}

		final int pointsRequired = entry.getPointsRequired();
		if (pointsRequired > 0)
		{
			final int points = countLearnedPoints(player, playerClass, learned);
			if (points < pointsRequired)
			{
				return LearnValidation.fail("Você precisa de " + pointsRequired + " pontos no total.");
			}
		}

		final int pointsCost = getSkillPointsCost(entry);
		if (pointsCost > 0)
		{
			final boolean dual = player.isDualClassActive();
			final FourthClassPoints points = getPointsSummary(player, dual);
			if ((points.getUsed() + pointsCost) > points.getEarned())
			{
				LOGGER.fine(() -> getClass().getSimpleName() + ": " + player.getName() + " lacks fourth class points for skill " + skillId + " lvl " + skillLevel + " (" + points.getAvailable() + "/" + points.getEarned() + ").");
				return LearnValidation.fail("Você não tem pontos suficientes. Restantes: " + points.getAvailable() + " / Total: " + points.getEarned());
			}
		}

		if (checkItems && !entry.getRequiredItems().isEmpty())
		{
			final List<ItemHolder> itemsToConsume = resolveRequiredItems(player, entry.getRequiredItems());
			if (itemsToConsume == null)
			{
				return LearnValidation.fail("Você não possui os itens necessários.");
			}

			return LearnValidation.success(itemsToConsume);
		}

		return LearnValidation.success(Collections.emptyList());
	}

	private int getCurrentLevel(Player player, Map<Integer, Integer> learned, int skillId)
	{
		final int savedLevel = learned.getOrDefault(skillId, 0);
		final Skill knownSkill = player.getKnownSkill(skillId);
		if (knownSkill != null)
		{
			return Math.max(savedLevel, knownSkill.getLevel());
		}

		return savedLevel;
	}

	private int countLearnedPoints(Player player, PlayerClass playerClass, Map<Integer, Integer> learned)
	{
		int points = 0;
		for (SkillLearn learn : SkillTreeData.getInstance().getFourthClassSkillTree(playerClass).values())
		{
			final int learnedLevel = getCurrentLevel(player, learned, learn.getSkillId());
			if (learnedLevel >= learn.getSkillLevel())
			{
				points++;
			}
		}

		return points;
	}

	private List<ItemHolder> resolveRequiredItems(Player player, List<List<ItemHolder>> requiredItems)
	{
		if (requiredItems.isEmpty())
		{
			return Collections.emptyList();
		}

		final Map<Integer, Long> totals = new HashMap<>();
		final List<ItemHolder> chosen = new ArrayList<>();
		for (List<ItemHolder> options : requiredItems)
		{
			ItemHolder selected = null;
			for (ItemHolder option : options)
			{
				final long needed = totals.getOrDefault(option.getId(), 0L) + option.getCount();
				final long available = player.getInventory().getInventoryItemCount(option.getId(), -1);
				if (available >= needed)
				{
					selected = option;
					break;
				}
			}

			if (selected == null)
			{
				return null;
			}

			totals.put(selected.getId(), totals.getOrDefault(selected.getId(), 0L) + selected.getCount());
			chosen.add(selected);
		}

		final List<ItemHolder> aggregated = new ArrayList<>();
		for (Map.Entry<Integer, Long> entry : totals.entrySet())
		{
			aggregated.add(new ItemHolder(entry.getKey(), entry.getValue()));
		}

		return aggregated;
	}

	private boolean consumeItems(Player player, Collection<ItemHolder> items)
	{
		if (items.isEmpty())
		{
			return true;
		}

		final List<ItemHolder> consumed = new ArrayList<>();
		for (ItemHolder item : items)
		{
			if (!player.destroyItemByItemId(ItemProcessType.FEE, item.getId(), item.getCount(), player, true))
			{
				refundItems(player, consumed);
				LOGGER.warning(getClass().getSimpleName() + ": Failed to consume required items for " + player.getName());
				return false;
			}

			consumed.add(item);
		}

		return true;
	}

	private void refundItems(Player player, Collection<ItemHolder> items)
	{
		for (ItemHolder item : items)
		{
			player.addItem(ItemProcessType.FEE, item.getId(), item.getCount(), player, false);
		}
	}

	public static FourthClassSkillTreeManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class PointsState
	{
		private final int _used;
		private final int _earned;
		private final int _lastLevelAwarded;

		private PointsState(int used, int earned, int lastLevelAwarded)
		{
			_used = used;
			_earned = earned;
			_lastLevelAwarded = lastLevelAwarded;
		}

		public int getUsed()
		{
			return _used;
		}

		public int getEarned()
		{
			return _earned;
		}

		public int getLastLevelAwarded()
		{
			return _lastLevelAwarded;
		}
	}

	public static class FourthClassPoints
	{
		private final int _used;
		private final int _available;
		private final int _earned;

		private FourthClassPoints(int used, int available, int earned)
		{
			_used = used;
			_available = available;
			_earned = earned;
		}

		public int getUsed()
		{
			return _used;
		}

		public int getAvailable()
		{
			return _available;
		}

		public int getEarned()
		{
			return _earned;
		}
	}

	public static class LearnResult
	{
		private final boolean _success;
		private final String _message;

		private LearnResult(boolean success, String message)
		{
			_success = success;
			_message = message;
		}

		public boolean isSuccess()
		{
			return _success;
		}

		public String getMessage()
		{
			return _message;
		}

		public static LearnResult success(String message)
		{
			return new LearnResult(true, message);
		}

		public static LearnResult fail(String message)
		{
			return new LearnResult(false, message);
		}
	}

	private static class LearnValidation
	{
		private final boolean _success;
		private final String _message;
		private final List<ItemHolder> _itemsToConsume;

		private LearnValidation(boolean success, String message, List<ItemHolder> itemsToConsume)
		{
			_success = success;
			_message = message;
			_itemsToConsume = itemsToConsume;
		}

		public boolean isSuccess()
		{
			return _success;
		}

		public String getMessage()
		{
			return _message;
		}

		public List<ItemHolder> getItemsToConsume()
		{
			return _itemsToConsume;
		}

		public static LearnValidation success(List<ItemHolder> itemsToConsume)
		{
			return new LearnValidation(true, "", itemsToConsume);
		}

		public static LearnValidation fail(String message)
		{
			return new LearnValidation(false, message, Collections.emptyList());
		}
	}

	private static class SingletonHolder
	{
		protected static final FourthClassSkillTreeManager INSTANCE = new FourthClassSkillTreeManager();
	}
}
