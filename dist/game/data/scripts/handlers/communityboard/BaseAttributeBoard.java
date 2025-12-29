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

import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.sql.BaseAttributeBonusTable;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.handler.CommunityBoardHandler;
import org.l2jmobius.gameserver.handler.IParseBoardHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.BaseAttributeBonusHolder;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.stats.BaseStat;
import org.l2jmobius.gameserver.network.serverpackets.HennaInfo;
import org.l2jmobius.gameserver.util.FormatUtil;

public class BaseAttributeBoard implements IParseBoardHandler
{
	private static final String HTML_PATH = "data/html/CommunityBoard/base_attribute.html";
	
	private static final String[] COMMANDS =
	{
		"_bbsattr"
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
		if (command.startsWith("_bbsattr;"))
		{
			final String[] parts = command.split(";");
			if ((parts.length >= 3) && "add".equalsIgnoreCase(parts[1]))
			{
				message = handleAdd(player, parts[2]);
			}
			else if ((parts.length >= 2) && "reset".equalsIgnoreCase(parts[1]))
			{
				message = handleReset(player);
			}
		}
		
		showPage(player, message);
		return true;
	}
	
	private String handleAdd(Player player, String statName)
	{
		final BaseAttributeBonusHolder bonusHolder = player.getActiveBaseAttributeBonusHolder();
		final int available = player.getAvailableBaseAttributePoints();
		final int used = bonusHolder.getTotalUsed();
		final int remaining = available - used;
		
		if (remaining <= 0)
		{
			return "Você não tem pontos disponíveis.";
		}
		
		if (used > available)
		{
			return "Você excede os pontos disponíveis para seu level atual.";
		}
		
		final BaseStat stat = parseStat(statName);
		if (stat == null)
		{
			return "Atributo inválido.";
		}
		
		bonusHolder.addBonus(stat, 1);
		BaseAttributeBonusTable.getInstance().saveBonuses(player.getObjectId(), player.isDualClassActive(), bonusHolder);
		player.getStat().recalculateStats(false);
		player.sendPacket(new HennaInfo(player));
		player.broadcastUserInfo();
		return "";
	}
	
	private String handleReset(Player player)
	{
		final BaseAttributeBonusHolder bonusHolder = player.getActiveBaseAttributeBonusHolder();
		if (bonusHolder.getTotalUsed() <= 0)
		{
			return "Você não possui bônus para resetar.";
		}
		
		if (PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_ID > 0 && PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_COUNT > 0)
		{
			final long count = player.getInventory().getInventoryItemCount(PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_ID, -1);
			if (count < PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_COUNT)
			{
				return "Você precisa de " + FormatUtil.formatAdena(PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_COUNT) + " " + getResetItemName() + " para resetar.";
			}
			
			if (!player.destroyItemByItemId(ItemProcessType.FEE, PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_ID, PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_COUNT, player, true))
			{
				return "Você não possui o custo necessário para resetar.";
			}
		}
		
		bonusHolder.setAll(0, 0, 0, 0, 0, 0);
		BaseAttributeBonusTable.getInstance().saveBonuses(player.getObjectId(), player.isDualClassActive(), bonusHolder);
		player.getStat().recalculateStats(false);
		player.sendPacket(new HennaInfo(player));
		player.broadcastUserInfo();
		return "Distribuição resetada com sucesso.";
	}
	
	private BaseStat parseStat(String value)
	{
		switch (value.toLowerCase())
		{
			case "str":
			{
				return BaseStat.STR;
			}
			case "con":
			{
				return BaseStat.CON;
			}
			case "dex":
			{
				return BaseStat.DEX;
			}
			case "int":
			{
				return BaseStat.INT;
			}
			case "wit":
			{
				return BaseStat.WIT;
			}
			case "men":
			{
				return BaseStat.MEN;
			}
		}
		
		return null;
	}
	
	private void showPage(Player player, String message)
	{
		final BaseAttributeBonusHolder bonusHolder = player.getActiveBaseAttributeBonusHolder();
		final int available = player.getAvailableBaseAttributePoints();
		final int used = bonusHolder.getTotalUsed();
		final int remaining = Math.max(0, available - used);
		final boolean exceeded = used > available;
		
		String html = HtmCache.getInstance().getHtm(player, HTML_PATH);
		final String mode = player.isDualClassActive() ? "Dual Class" : "Main Class";
		html = html.replace("%mode%", mode);
		html = html.replace("%available_points%", String.valueOf(available));
		html = html.replace("%spent_points%", String.valueOf(used));
		html = html.replace("%remaining_points%", String.valueOf(remaining));
		html = html.replace("%str_bonus%", String.valueOf(bonusHolder.getStrBonus()));
		html = html.replace("%con_bonus%", String.valueOf(bonusHolder.getConBonus()));
		html = html.replace("%dex_bonus%", String.valueOf(bonusHolder.getDexBonus()));
		html = html.replace("%int_bonus%", String.valueOf(bonusHolder.getIntBonus()));
		html = html.replace("%wit_bonus%", String.valueOf(bonusHolder.getWitBonus()));
		html = html.replace("%men_bonus%", String.valueOf(bonusHolder.getMenBonus()));
		html = html.replace("%reset_cost%", getResetCostDescription());
		html = html.replace("%warning%", exceeded ? "<br><center><font color=\"LEVEL\">Você excede os pontos disponíveis para seu level atual.</font></center>" : "");
		html = html.replace("%message%", message.isEmpty() ? "" : "<br><center><font color=\"FF6A00\">" + message + "</font></center>");
		
		CommunityBoardHandler.getInstance().addBypass(player, "Atributos Base", "_bbsattr");
		CommunityBoardHandler.separateAndSend(html, player);
	}
	
	private String getResetCostDescription()
	{
		if (PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_ID <= 0 || PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_COUNT <= 0)
		{
			return "Grátis";
		}
		
		return FormatUtil.formatAdena(PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_COUNT) + " " + getResetItemName();
	}
	
	private String getResetItemName()
	{
		final ItemTemplate item = ItemData.getInstance().getTemplate(PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_ID);
		if (item == null)
		{
			return "Item " + PlayerConfig.BASE_ATTRIBUTE_RESET_ITEM_ID;
		}
		
		return item.getName();
	}
}
