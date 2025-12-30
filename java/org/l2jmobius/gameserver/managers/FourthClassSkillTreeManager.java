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
import org.l2jmobius.gameserver.data.enums.CategoryType;
import org.l2jmobius.gameserver.data.xml.CategoryData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.SkillTreeData;
import org.l2jmobius.gameserver.model.SkillLearn;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.PlayerClass;
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
			final int points = countLearnedPoints(player, playerClass, entry.getTreeId(), learned);
			if (points < pointsRequired)
			{
				return LearnValidation.fail("Você precisa de " + pointsRequired + " pontos nesta árvore.");
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

	private int countLearnedPoints(Player player, PlayerClass playerClass, int treeId, Map<Integer, Integer> learned)
	{
		int points = 0;
		for (SkillLearn learn : SkillTreeData.getInstance().getFourthClassSkillTree(playerClass).values())
		{
			if (learn.getTreeId() != treeId)
			{
				continue;
			}

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
