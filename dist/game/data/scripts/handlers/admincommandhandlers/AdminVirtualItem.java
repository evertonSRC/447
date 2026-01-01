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
package handlers.admincommandhandlers;

import java.util.Map;
import java.util.StringTokenizer;

import org.l2jmobius.gameserver.handler.IAdminCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.VirtualSlot;
import org.l2jmobius.gameserver.model.actor.holders.player.VirtualEquippedItem;
import org.l2jmobius.gameserver.model.virtual.VirtualItemService;
import org.l2jmobius.gameserver.model.virtual.VirtualItemService.VirtualItemResult;

/**
 * @author Mobius
 */
public class AdminVirtualItem implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_virtualitem"
	};
	
	@Override
	public boolean onCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken(); // Skip actual command.
		
		if (!st.hasMoreTokens())
		{
			sendUsage(activeChar);
			return false;
		}
		
		final String subCommand = st.nextToken().toLowerCase();
		switch (subCommand)
		{
			case "list":
			{
				listEquipment(activeChar);
				return true;
			}
			case "equip":
			{
				if (st.countTokens() < 2)
				{
					sendUsage(activeChar);
					return false;
				}
				
				final int indexMain;
				final int indexSub;
				try
				{
					indexMain = Integer.parseInt(st.nextToken());
					indexSub = Integer.parseInt(st.nextToken());
				}
				catch (NumberFormatException e)
				{
					sendUsage(activeChar);
					return false;
				}
				
				long slotRequested = 0;
				if (st.hasMoreTokens())
				{
					final String slotValue = st.nextToken();
					final VirtualSlot parsedSlot = VirtualSlot.parseSlot(slotValue);
					if (parsedSlot != null)
					{
						slotRequested = parsedSlot.getId();
					}
					else
					{
						try
						{
							slotRequested = Long.parseLong(slotValue);
						}
						catch (NumberFormatException e)
						{
							activeChar.sendSysMessage("Slot inválido: " + slotValue + ".");
							return false;
						}
					}
				}
				
				final VirtualItemResult result = VirtualItemService.equipVirtualItem(activeChar, indexMain, indexSub, slotRequested);
				activeChar.sendSysMessage(result.getMessage());
				return result.isSuccess();
			}
			case "unequip":
			{
				if (!st.hasMoreTokens())
				{
					sendUsage(activeChar);
					return false;
				}
				
				final String slotValue = st.nextToken();
				final VirtualSlot slot = VirtualSlot.parseSlot(slotValue);
				if (slot == null)
				{
					activeChar.sendSysMessage("Slot inválido: " + slotValue + ".");
					return false;
				}
				
				final VirtualItemResult result = VirtualItemService.unequipVirtualItem(activeChar, slot);
				activeChar.sendSysMessage(result.getMessage());
				return result.isSuccess();
			}
		}
		
		sendUsage(activeChar);
		return false;
	}
	
	private void listEquipment(Player player)
	{
		final Map<VirtualSlot, VirtualEquippedItem> equipment = player.getVirtualEquipment().getItems();
		if (equipment.isEmpty())
		{
			player.sendSysMessage("Nenhum item virtual equipado.");
			return;
		}
		
		player.sendSysMessage("Equipamento virtual:");
		for (VirtualSlot slot : VirtualSlot.values())
		{
			final VirtualEquippedItem item = equipment.get(slot);
			if (item != null)
			{
				player.sendSysMessage(slot.getAlias() + " -> itemId=" + item.getItemId() + " enchant=" + item.getEnchant() + " indexMain=" + item.getIndexMain() + " indexSub=" + item.getIndexSub());
			}
		}
	}
	
	private void sendUsage(Player player)
	{
		player.sendSysMessage("Uso: //virtualitem list");
		player.sendSysMessage("Uso: //virtualitem equip <indexMain> <indexSub> [slotAlias|slotId]");
		player.sendSysMessage("Uso: //virtualitem unequip <slotAlias|slotId>");
	}
	
	@Override
	public String[] getCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
