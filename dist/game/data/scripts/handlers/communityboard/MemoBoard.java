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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.data.xml.ClassListData;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.handler.CommunityBoardHandler;
import org.l2jmobius.gameserver.handler.IWriteBoardHandler;
import org.l2jmobius.gameserver.managers.FourthClassSkillTreeManager;
import org.l2jmobius.gameserver.managers.FourthClassSkillTreeManager.FourthClassSkillEntry;
import org.l2jmobius.gameserver.managers.FourthClassSkillTreeManager.LearnResult;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
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
		final boolean isDual = player.isDualClassActive();
		final String mode = isDual ? "Dual Class" : "Main Class";
		final String className = ClassListData.getInstance().getClass(classId) != null ? ClassListData.getInstance().getClass(classId).getClassName() : String.valueOf(classId);
		final Map<Integer, Integer> learned = manager.getLearnedSkills(player.getObjectId(), classId, isDual);
		final List<FourthClassSkillEntry> skills = new ArrayList<>(manager.loadSkillTreeForClass(classId));
		skills.sort(Comparator.comparingInt(FourthClassSkillEntry::getGetLevel).thenComparingInt(FourthClassSkillEntry::getSkillId));

		final String html = buildPage(player, className, mode, buildSkillRows(player, skills, learned), message, success);
		CommunityBoardHandler.getInstance().addBypass(player, "Fourth Class Skill Tree", "_bbsmemo");
		CommunityBoardHandler.separateAndSend(html, player);
	}

	private String buildPage(Player player, String className, String mode, String skillRows, String message, boolean success)
	{
		String html = HtmCache.getInstance().getHtm(player, HTML_PATH);
		html = html.replace("%class_name%", className);
		html = html.replace("%mode%", mode);
		html = html.replace("%skills%", skillRows);
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

	private String buildSkillRows(Player player, List<FourthClassSkillEntry> skills, Map<Integer, Integer> learned)
	{
		if (skills.isEmpty())
		{
			return "<tr><td width=755 align=center>Sem skills disponíveis para esta classe.</td></tr>";
		}

		final StringBuilder sb = new StringBuilder();
		for (FourthClassSkillEntry entry : skills)
		{
			final int skillId = entry.getSkillId();
			final int skillLevel = entry.getSkillLevel();
			final boolean learnedSkill = learned.getOrDefault(skillId, 0) >= skillLevel || (player.getKnownSkill(skillId) != null && player.getKnownSkill(skillId).getLevel() >= skillLevel);
			final String cost = buildCost(entry.getItems());
			final String action = learnedSkill ? "Aprendida" : "<button value=\"Aprender\" action=\"bypass -h _bbsmemo;learn;" + skillId + ";" + skillLevel + "\" back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\" width=80 height=20>";
			final String name = entry.getSkillName().isEmpty() ? ("Skill " + skillId) : entry.getSkillName();

			sb.append("<tr>");
			sb.append("<td width=300>").append(name).append(" (").append(skillId).append("/").append(skillLevel).append(")</td>");
			sb.append("<td width=80 align=center>").append(entry.getGetLevel()).append("</td>");
			sb.append("<td width=275>").append(cost).append("</td>");
			sb.append("<td width=100 align=center>").append(action).append("</td>");
			sb.append("</tr>");
			sb.append("<tr><td colspan=4><img src=\"L2UI.squaregray\" width=\"755\" height=\"1\"></td></tr>");
		}

		return sb.toString();
	}

	private String buildCost(List<ItemHolder> items)
	{
		if (items.isEmpty())
		{
			return "Grátis";
		}

		final StringBuilder sb = new StringBuilder();
		for (ItemHolder item : items)
		{
			final ItemTemplate template = ItemData.getInstance().getTemplate(item.getId());
			final String name = template != null ? template.getName() : "Item " + item.getId();
			if (sb.length() > 0)
			{
				sb.append("<br1>");
			}

			sb.append(name).append(" (").append(item.getId()).append(") x").append(FormatUtil.formatAdena(item.getCount()));
		}

		return sb.toString();
	}
}
