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
package handlers.communityboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.xml.ClassListData;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.SkillTreeData;
import org.l2jmobius.gameserver.handler.CommunityBoardHandler;
import org.l2jmobius.gameserver.handler.IWriteBoardHandler;
import org.l2jmobius.gameserver.managers.FourthClassSkillTreeManager;
import org.l2jmobius.gameserver.managers.FourthClassSkillTreeManager.FourthClassPoints;
import org.l2jmobius.gameserver.managers.FourthClassSkillTreeManager.LearnResult;
import org.l2jmobius.gameserver.model.SkillLearn;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.PlayerClass;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.util.FormatUtil;

/**
 * Memo board.
 * @author Zoey76
 */
public class MemoBoard implements IWriteBoardHandler
{
	private static final String HTML_PATH = "data/html/CommunityBoard/memo.html";
	private static final String[] COMMANDS =
	{
		"_bbsmemo",
		"_bbstopics"
	};

	@Override
	public String[] getCommandList()
	{
		return COMMANDS;
	}

	@Override
	public boolean onCommand(String command, Player player)
	{
		String message = "";
		boolean success = false;
		if (command.startsWith("_bbsmemo;learn;"))
		{
			final String[] parts = command.split(";");
			if (parts.length >= 4)
			{
				final int skillId = Integer.parseInt(parts[2]);
				final int skillLevel = Integer.parseInt(parts[3]);
				final LearnResult result = FourthClassSkillTreeManager.getInstance().learnSkill(player, skillId, skillLevel);
				message = result.getMessage();
				success = result.isSuccess();
			}
		}
		else if (command.startsWith("_bbsmemo;reset"))
		{
			final LearnResult result = FourthClassSkillTreeManager.getInstance().resetFourthClassSkillTree(player);
			message = result.getMessage();
			success = result.isSuccess();
		}

		showPage(player, message, success);
		return true;
	}

	@Override
	public boolean writeCommunityBoardCommand(Player player, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		// TODO: Implement.
		return false;
	}

	private void showPage(Player player, String message, boolean success)
	{
		final FourthClassSkillTreeManager manager = FourthClassSkillTreeManager.getInstance();
		final int classId = player.getActiveClass();
		final PlayerClass playerClass = PlayerClass.getPlayerClass(classId);
		final String mode = player.isDualClassActive() ? "Dual Class" : "Main Class";
		final String className = ClassListData.getInstance().getClass(classId) != null ? ClassListData.getInstance().getClass(classId).getClassName() : String.valueOf(classId);
		final Map<Integer, Integer> learned = manager.getLearnedSkills(player.getObjectId(), classId);
		final Collection<SkillLearn> skills = playerClass != null ? SkillTreeData.getInstance().getFourthClassSkillTree(playerClass).values() : Collections.emptyList();
		final FourthClassPoints points = manager.getPointsSummary(player, player.isDualClassActive());
		final int cap = Math.max(0, PlayerConfig.FOURTH_CLASS_SKILLTREE_POINTS_CAP);

		final String html = buildPage(player, className, mode, buildPointsLine(points, cap), buildTrees(player, playerClass, skills, learned), message, success);
		CommunityBoardHandler.getInstance().addBypass(player, "Fourth Class Skill Tree", "_bbsmemo");
		CommunityBoardHandler.separateAndSend(html, player);
	}

	private String buildPage(Player player, String className, String mode, String pointsLine, String trees, String message, boolean success)
	{
		String html = HtmCache.getInstance().getHtm(player, HTML_PATH);
		html = html.replace("%class_name%", className);
		html = html.replace("%mode%", mode);
		html = html.replace("%points_line%", pointsLine);
		html = html.replace("%trees%", trees);
		if (message.isEmpty())
		{
			html = html.replace("%message%", "");
		}
		else
		{
			final String color = success ? "00AA00" : "FF6A00";
			html = html.replace("%message%", "<br><center><font color=\"" + color + "\">" + message + "</font></center>");
		}

		return html;
	}

	private String buildPointsLine(FourthClassPoints points, int cap)
	{
		return "Pontos disponíveis: " + points.getAvailable() + " | Pontos usados: " + points.getUsed() + " | Total ganhos/cap: " + points.getEarned() + " / " + cap;
	}

	private String buildTrees(Player player, PlayerClass playerClass, Collection<SkillLearn> skills, Map<Integer, Integer> learned)
	{
		if (skills.isEmpty() || (playerClass == null))
		{
			return "<center>Sem skills disponíveis para esta classe.</center>";
		}

		final Map<Integer, List<SkillLearn>> trees = new TreeMap<>();
		for (SkillLearn entry : skills)
		{
			trees.computeIfAbsent(entry.getTreeId(), key -> new ArrayList<>()).add(entry);
		}

		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Map.Entry<Integer, List<SkillLearn>> entry : trees.entrySet())
		{
			if (!first)
			{
				sb.append("<br>");
			}
			first = false;
			sb.append(buildTreeTable(player, playerClass, entry.getKey(), entry.getValue(), learned));
		}

		return sb.toString();
	}

	private String buildTreeTable(Player player, PlayerClass playerClass, int treeId, List<SkillLearn> entries, Map<Integer, Integer> learned)
	{
		final Map<Integer, List<SkillLearn>> bySkill = new HashMap<>();
		int maxRow = 0;
		int maxColumn = 0;
		for (SkillLearn learn : entries)
		{
			bySkill.computeIfAbsent(learn.getSkillId(), key -> new ArrayList<>()).add(learn);
			maxRow = Math.max(maxRow, learn.getRow());
			maxColumn = Math.max(maxColumn, learn.getColumn());
		}

		final Map<String, SkillCell> cells = new HashMap<>();
		for (Map.Entry<Integer, List<SkillLearn>> entry : bySkill.entrySet())
		{
			final List<SkillLearn> levels = entry.getValue();
			levels.sort(Comparator.comparingInt(SkillLearn::getSkillLevel));
			final int skillId = entry.getKey();
			final int currentLevel = getCurrentLevel(player, learned, skillId);
			final int maxLevel = levels.get(levels.size() - 1).getSkillLevel();
			final int targetLevel = Math.min(currentLevel + 1, maxLevel);
			final SkillLearn targetLearn = levels.stream().filter(level -> level.getSkillLevel() == targetLevel).findFirst().orElse(levels.get(0));
			final String key = targetLearn.getRow() + ":" + targetLearn.getColumn();
			final boolean maxed = currentLevel >= maxLevel;
			final boolean canLearn = !maxed && FourthClassSkillTreeManager.getInstance().canLearnSkill(player, playerClass, targetLearn, learned);
			cells.put(key, new SkillCell(skillId, targetLearn, currentLevel, maxLevel, canLearn));
		}

		final StringBuilder sb = new StringBuilder();
		sb.append("<table border=0 cellspacing=0 cellpadding=4 width=755 bgcolor=333333>");
		sb.append("<tr><td align=center><font color=\"LEVEL\">Árvore ").append(treeId).append("</font></td></tr></table>");
		sb.append("<table border=0 cellspacing=2 cellpadding=2 width=755 bgcolor=111111>");
		for (int row = 1; row <= maxRow; row++)
		{
			sb.append("<tr>");
			for (int column = 1; column <= maxColumn; column++)
			{
				final SkillCell cell = cells.get(row + ":" + column);
				sb.append("<td width=185 height=120 valign=top bgcolor=222222>");
				if (cell != null)
				{
					sb.append(buildSkillCell(player, cell));
				}
				else
				{
					sb.append("&nbsp;");
				}
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	private String buildSkillCell(Player player, SkillCell cell)
	{
		final SkillLearn learn = cell.getTargetLearn();
		final String name = learn.getName().isEmpty() ? ("Skill " + cell.getSkillId()) : learn.getName();
		final StringBuilder sb = new StringBuilder();
		final boolean maxed = cell.getCurrentLevel() >= cell.getMaxLevel();
		final String color = maxed ? "00AA00" : (cell.canLearn() ? "FFFFFF" : "999999");
		sb.append("<font color=\"").append(color).append("\">").append(name).append("</font><br1>");
		sb.append("Lv ").append(Math.min(cell.getCurrentLevel(), cell.getMaxLevel())).append("/").append(cell.getMaxLevel()).append("<br1>");
		sb.append("Req Lv: ").append(learn.getGetLevel()).append("<br1>");
		if (learn.getPointsRequired() > 0)
		{
			sb.append("Pts: ").append(learn.getPointsRequired()).append("<br1>");
		}

		if (!learn.getPreReqSkills().isEmpty())
		{
			sb.append("Req: ").append(buildPrereq(learn.getPreReqSkills())).append("<br1>");
		}

		sb.append("Custo: ").append(buildCost(learn.getRequiredItems())).append("<br1>");

		if (maxed)
		{
			sb.append("<font color=\"00AA00\">Aprendida</font>");
		}
		else if (cell.canLearn())
		{
			sb.append("<button value=\"Aprender\" action=\"bypass -h _bbsmemo;learn;").append(cell.getSkillId()).append(";").append(learn.getSkillLevel()).append("\" back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\" width=80 height=20>");
		}
		else
		{
			sb.append("<font color=\"FF6A00\">Bloqueada</font>");
		}

		return sb.toString();
	}

	private String buildPrereq(Collection<SkillHolder> prereqs)
	{
		final StringBuilder sb = new StringBuilder();
		for (SkillHolder holder : prereqs)
		{
			final Skill skill = SkillData.getInstance().getSkill(holder.getSkillId(), holder.getSkillLevel());
			final String name = skill != null ? skill.getName() : ("Skill " + holder.getSkillId());
			if (sb.length() > 0)
			{
				sb.append(", ");
			}
			sb.append(name).append(" Lv ").append(holder.getSkillLevel());
		}

		return sb.toString();
	}

	private String buildCost(List<List<ItemHolder>> items)
	{
		if (items.isEmpty())
		{
			return "Grátis";
		}

		final StringBuilder sb = new StringBuilder();
		for (List<ItemHolder> group : items)
		{
			if (sb.length() > 0)
			{
				sb.append("<br1>");
			}

			final StringBuilder groupText = new StringBuilder();
			for (ItemHolder item : group)
			{
				final ItemTemplate template = ItemData.getInstance().getTemplate(item.getId());
				final String name = template != null ? template.getName() : "Item " + item.getId();
				if (groupText.length() > 0)
				{
					groupText.append(" ou ");
				}

				groupText.append(name).append(" x").append(FormatUtil.formatAdena(item.getCount()));
			}

			sb.append(groupText);
		}

		return sb.toString();
	}

	private int getCurrentLevel(Player player, Map<Integer, Integer> learned, int skillId)
	{
		final int savedLevel = learned.getOrDefault(skillId, 0);
		final Skill known = player.getKnownSkill(skillId);
		if (known != null)
		{
			return Math.max(savedLevel, known.getLevel());
		}

		return savedLevel;
	}

	private static final class SkillCell
	{
		private final int skillId;
		private final SkillLearn targetLearn;
		private final int currentLevel;
		private final int maxLevel;
		private final boolean canLearn;

		private SkillCell(int skillId, SkillLearn targetLearn, int currentLevel, int maxLevel, boolean canLearn)
		{
			this.skillId = skillId;
			this.targetLearn = targetLearn;
			this.currentLevel = currentLevel;
			this.maxLevel = maxLevel;
			this.canLearn = canLearn;
		}

		private int getSkillId()
		{
			return skillId;
		}

		private SkillLearn getTargetLearn()
		{
			return targetLearn;
		}

		private int getCurrentLevel()
		{
			return currentLevel;
		}

		private int getMaxLevel()
		{
			return maxLevel;
		}

		private boolean canLearn()
		{
			return canLearn;
		}
	}
}
