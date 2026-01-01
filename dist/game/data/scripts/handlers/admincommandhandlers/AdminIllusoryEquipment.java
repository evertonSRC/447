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

import org.l2jmobius.gameserver.data.holders.VirtualItemHolder;
import org.l2jmobius.gameserver.data.xml.VirtualItemData;
import org.l2jmobius.gameserver.handler.IAdminCommandHandler;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.virtualitem.VirtualItemService;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;

/**
 * @author Mobius
 */
public class AdminIllusoryEquipment implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_set_illusory_points",
		"admin_setillusorypoints",
		"admin_clear_illusory",
		"admin_clearillusory",
		"admin_apply_illusory",
		"admin_applyillusory",
	};
	
	@Override
	public boolean onCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken().toLowerCase();
		
		switch (actualCommand)
		{
			case "admin_set_illusory_points":
			case "admin_setillusorypoints":
			{
				if (st.countTokens() < 1)
				{
					activeChar.sendMessage("Usage: //set_illusory_points <value> [player]");
					return false;
				}
				
				final int points;
				try
				{
					points = Integer.parseInt(st.nextToken());
				}
				catch (NumberFormatException e)
				{
					activeChar.sendMessage("Invalid points value.");
					return false;
				}
				
				Player target = activeChar;
				if (st.hasMoreTokens())
				{
					target = World.getInstance().getPlayer(st.nextToken());
				}
				
				if (target == null)
				{
					activeChar.sendMessage("Target player not found.");
					return false;
				}
				
				target.getVariables().set(PlayerVariables.ILLUSORY_POINTS_ACQUIRED, Math.max(points, 0));
				VirtualItemService.updateUsedPoints(target);
				activeChar.sendMessage("Illusory points set to " + points + " for " + target.getName() + ".");
				return true;
			}
			case "admin_clear_illusory":
			case "admin_clearillusory":
			{
				Player target = activeChar;
				if (st.hasMoreTokens())
				{
					target = World.getInstance().getPlayer(st.nextToken());
				}
				
				if (target == null)
				{
					activeChar.sendMessage("Target player not found.");
					return false;
				}
				
				VirtualItemService.resetSelections(target, true);
				activeChar.sendMessage("Illusory selections cleared for " + target.getName() + ".");
				return true;
			}
			case "admin_apply_illusory":
			case "admin_applyillusory":
			{
				if (st.countTokens() < 2)
				{
					activeChar.sendMessage("Usage: //apply_illusory <indexMain> <indexSub> [player]");
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
					activeChar.sendMessage("Invalid indexes.");
					return false;
				}
				
				Player target = activeChar;
				if (st.hasMoreTokens())
				{
					target = World.getInstance().getPlayer(st.nextToken());
				}
				
				if (target == null)
				{
					activeChar.sendMessage("Target player not found.");
					return false;
				}
				
				final VirtualItemHolder holder = VirtualItemData.getInstance().getVirtualItem(indexMain, indexSub);
				if (holder == null)
				{
					activeChar.sendMessage("Virtual item not found for indexMain=" + indexMain + " indexSub=" + indexSub + ".");
					return false;
				}
				
				final int previousSub = target.getVariables().getInt("ILLUSORY_EQUIPMENT_SELECTION_" + indexMain, 0);
				final VirtualItemHolder previousHolder = previousSub > 0 ? VirtualItemData.getInstance().getVirtualItem(indexMain, previousSub) : null;
				if (!VirtualItemService.applySelection(target, holder, previousHolder, true))
				{
					activeChar.sendMessage("Failed to apply illusory selection.");
					return false;
				}
				
				VirtualItemService.updateUsedPoints(target);
				activeChar.sendMessage("Applied illusory selection to " + target.getName() + ".");
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
