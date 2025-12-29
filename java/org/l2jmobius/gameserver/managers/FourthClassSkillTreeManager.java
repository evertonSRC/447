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

import java.io.File;
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

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.data.enums.CategoryType;
import org.l2jmobius.gameserver.data.xml.CategoryData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * Manages the fourth class skill trees and learned skills.
 */
public class FourthClassSkillTreeManager implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(FourthClassSkillTreeManager.class.getName());
	private static final String DATA_PATH = "data/stats/players/skillTrees/fourthclass_skilltree";
	private static final String LOAD_SKILLS = "SELECT skill_id, skill_level FROM player_fourthclass_skills WHERE player_id=? AND class_id=? AND is_dual=?";
	private static final String SAVE_SKILL = "INSERT INTO player_fourthclass_skills (player_id, class_id, is_dual, skill_id, skill_level, acquired_time) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE skill_level=VALUES(skill_level), acquired_time=VALUES(acquired_time)";

	private final Map<Integer, List<FourthClassSkillEntry>> _skillTrees = new ConcurrentHashMap<>();
	private final Set<Integer> _allSkillIds = ConcurrentHashMap.newKeySet();

	protected FourthClassSkillTreeManager()
	{
		load();
	}

	@Override
	public void load()
	{
		_skillTrees.clear();
		_allSkillIds.clear();
		parseDatapackDirectory(DATA_PATH, false);

		final int totalSkills = _skillTrees.values().stream().mapToInt(List::size).sum();
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + totalSkills + " fourth class skills for " + _skillTrees.size() + " class trees.");

		final Set<Integer> fourthClasses = CategoryData.getInstance().getCategoryByType(CategoryType.FOURTH_CLASS_GROUP);
		if (fourthClasses != null)
		{
			for (int classId : fourthClasses)
			{
				if (!_skillTrees.containsKey(classId))
				{
					LOGGER.warning(getClass().getSimpleName() + ": Missing fourth class skill tree for classId: " + classId);
				}
			}
		}
	}

	@Override
	public void parseDocument(Document document, File file)
	{
		for (Node node = document.getFirstChild(); node != null; node = node.getNextSibling())
		{
			if (!"list".equalsIgnoreCase(node.getNodeName()))
			{
				continue;
			}

			for (Node treeNode = node.getFirstChild(); treeNode != null; treeNode = treeNode.getNextSibling())
			{
				if (!"skillTree".equalsIgnoreCase(treeNode.getNodeName()))
				{
					continue;
				}

				final NamedNodeMap treeAttrs = treeNode.getAttributes();
				final String type = parseString(treeAttrs, "type", "");
				if (!"fourthClassSkillTree".equalsIgnoreCase(type))
				{
					continue;
				}

				final Integer classId = parseInteger(treeAttrs, "classId");
				if (classId == null)
				{
					LOGGER.warning(getClass().getSimpleName() + ": Missing classId in " + file.getName());
					continue;
				}

				final List<FourthClassSkillEntry> entries = _skillTrees.computeIfAbsent(classId, _ -> new ArrayList<>());
				for (Node skillNode = treeNode.getFirstChild(); skillNode != null; skillNode = skillNode.getNextSibling())
				{
					if (!"skill".equalsIgnoreCase(skillNode.getNodeName()))
					{
						continue;
					}

					final NamedNodeMap skillAttrs = skillNode.getAttributes();
					final Integer skillId = parseInteger(skillAttrs, "skillId");
					final Integer skillLevel = parseInteger(skillAttrs, "skillLevel");
					final Integer getLevel = parseInteger(skillAttrs, "getLevel", 1);
					final String skillName = parseString(skillAttrs, "skillName", "");
					if ((skillId == null) || (skillLevel == null))
					{
						LOGGER.warning(getClass().getSimpleName() + ": Invalid skill entry in " + file.getName());
						continue;
					}

					final List<ItemHolder> items = new ArrayList<>();
					for (Node itemNode = skillNode.getFirstChild(); itemNode != null; itemNode = itemNode.getNextSibling())
					{
						if (!"item".equalsIgnoreCase(itemNode.getNodeName()))
						{
							continue;
						}

						final NamedNodeMap itemAttrs = itemNode.getAttributes();
						final Integer itemId = parseInteger(itemAttrs, "id");
						final Long itemCount = parseLong(itemAttrs, "count", 0L);
						if ((itemId == null) || (itemCount == null) || (itemCount <= 0))
						{
							continue;
						}

						items.add(new ItemHolder(itemId, itemCount));
					}

					entries.add(new FourthClassSkillEntry(skillId, skillLevel, getLevel, skillName, items));
					_allSkillIds.add(skillId);
				}
			}
		}
	}

	public List<FourthClassSkillEntry> loadSkillTreeForClass(int classId)
	{
		return _skillTrees.getOrDefault(classId, Collections.emptyList());
	}

	public Map<Integer, Integer> getLearnedSkills(int playerId, int classId, boolean isDual)
	{
		final Map<Integer, Integer> learned = new HashMap<>();
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(LOAD_SKILLS))
		{
			statement.setInt(1, playerId);
			statement.setInt(2, classId);
			statement.setInt(3, isDual ? 1 : 0);
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

	public void applyLearnedSkills(Player player, int classId, boolean isDual)
	{
		final Map<Integer, Integer> learned = getLearnedSkills(player.getObjectId(), classId, isDual);
		if (learned.isEmpty())
		{
			return;
		}

		for (Map.Entry<Integer, Integer> entry : learned.entrySet())
		{
			final int skillId = entry.getKey();
			final int skillLevel = entry.getValue();
			if (getSkillEntry(classId, skillId, skillLevel) == null)
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
		final boolean isDual = player.isDualClassActive();
		final FourthClassSkillEntry entry = getSkillEntry(classId, skillId, skillLevel);
		if (entry == null)
		{
			return LearnResult.fail("Skill não encontrada para a classe atual.");
		}

		if (player.getLevel() < entry.getGetLevel())
		{
			return LearnResult.fail("Você precisa do nível " + entry.getGetLevel() + " para aprender.");
		}

		final Skill known = player.getKnownSkill(skillId);
		if ((known != null) && (known.getLevel() >= skillLevel))
		{
			return LearnResult.fail("Esta skill já foi aprendida.");
		}

		final Map<Integer, Integer> learned = getLearnedSkills(player.getObjectId(), classId, isDual);
		final Integer learnedLevel = learned.get(skillId);
		if ((learnedLevel != null) && (learnedLevel >= skillLevel))
		{
			return LearnResult.fail("Esta skill já foi aprendida.");
		}

		if (!hasAllItems(player, entry.getItems()))
		{
			return LearnResult.fail("Você não possui os itens necessários.");
		}

		final List<ItemHolder> consumed = new ArrayList<>();
		if (!consumeItems(player, entry.getItems(), consumed))
		{
			refundItems(player, consumed);
			return LearnResult.fail("Falha ao consumir os itens necessários.");
		}

		final Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
		if (skill == null)
		{
			refundItems(player, consumed);
			return LearnResult.fail("Skill inválida.");
		}

		player.addSkill(skill, false);
		if (!saveSkill(player, classId, isDual, skillId, skillLevel))
		{
			player.removeSkill(skill, false, true);
			refundItems(player, consumed);
			return LearnResult.fail("Não foi possível salvar a skill.");
		}

		player.sendSkillList();
		return LearnResult.success("Skill aprendida com sucesso.");
	}

	private boolean saveSkill(Player player, int classId, boolean isDual, int skillId, int skillLevel)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(SAVE_SKILL))
		{
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, classId);
			statement.setInt(3, isDual ? 1 : 0);
			statement.setInt(4, skillId);
			statement.setInt(5, skillLevel);
			statement.setLong(6, System.currentTimeMillis());
			statement.execute();
			return true;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not save learned skill for player " + player.getName(), e);
			return false;
		}
	}

	private boolean hasAllItems(Player player, Collection<ItemHolder> items)
	{
		for (ItemHolder item : items)
		{
			final long count = player.getInventory().getInventoryItemCount(item.getId(), -1);
			if (count < item.getCount())
			{
				return false;
			}
		}

		return true;
	}

	private boolean consumeItems(Player player, Collection<ItemHolder> items, Collection<ItemHolder> consumed)
	{
		for (ItemHolder item : items)
		{
			if (!player.destroyItemByItemId(ItemProcessType.FEE, item.getId(), item.getCount(), player, true))
			{
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

	private FourthClassSkillEntry getSkillEntry(int classId, int skillId, int skillLevel)
	{
		for (FourthClassSkillEntry entry : loadSkillTreeForClass(classId))
		{
			if ((entry.getSkillId() == skillId) && (entry.getSkillLevel() == skillLevel))
			{
				return entry;
			}
		}

		return null;
	}

	public static FourthClassSkillTreeManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	public static class FourthClassSkillEntry
	{
		private final int _skillId;
		private final int _skillLevel;
		private final int _getLevel;
		private final String _skillName;
		private final List<ItemHolder> _items;

		public FourthClassSkillEntry(int skillId, int skillLevel, int getLevel, String skillName, List<ItemHolder> items)
		{
			_skillId = skillId;
			_skillLevel = skillLevel;
			_getLevel = getLevel;
			_skillName = skillName;
			_items = Collections.unmodifiableList(items);
		}

		public int getSkillId()
		{
			return _skillId;
		}

		public int getSkillLevel()
		{
			return _skillLevel;
		}

		public int getGetLevel()
		{
			return _getLevel;
		}

		public String getSkillName()
		{
			return _skillName;
		}

		public List<ItemHolder> getItems()
		{
			return _items;
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

	private static class SingletonHolder
	{
		protected static final FourthClassSkillTreeManager INSTANCE = new FourthClassSkillTreeManager();
	}
}
