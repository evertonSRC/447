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

import java.util.StringTokenizer;

import org.l2jmobius.gameserver.handler.IAdminCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.virtual.VirtualSlot;

public class AdminVirtualEquip implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_visequip",
		"admin_visunequip",
	};
	
	@Override
	public boolean onCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		
		switch (actualCommand)
		{
			case "admin_visequip":
			{
				if (st.countTokens() < 2)
				{
					activeChar.sendSysMessage("Usage: //visequip <indexMain> <indexSub>");
					return true;
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
					activeChar.sendSysMessage("Invalid index values.");
					return true;
				}
				
				if (!activeChar.equipVirtualFromCatalog(indexMain, indexSub))
				{
					activeChar.sendSysMessage("Failed to equip virtual item.");
				}
				else
				{
					activeChar.sendSysMessage("Equipped virtual item from catalog.");
				}
				return true;
			}
			case "admin_visunequip":
			{
				if (!st.hasMoreTokens())
				{
					activeChar.sendSysMessage("Usage: //visunequip <slot>");
					return true;
				}
				
				final VirtualSlot slot = VirtualSlot.fromClientSlot(st.nextToken());
				if (slot == null)
				{
					activeChar.sendSysMessage("Invalid virtual slot.");
					return true;
				}
				
				if (!activeChar.unequipVirtual(slot))
				{
					activeChar.sendSysMessage("No virtual item equipped in that slot.");
				}
				else
				{
					activeChar.sendSysMessage("Unequipped virtual item.");
				}
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String[] getCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
