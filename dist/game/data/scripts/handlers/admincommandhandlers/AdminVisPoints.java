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
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.UserInfo;

public class AdminVisPoints implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_vispoints",
	};
	
	@Override
	public boolean onCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		if (!actualCommand.equals("admin_vispoints"))
		{
			return false;
		}
		
		if (!st.hasMoreTokens())
		{
			activeChar.sendSysMessage("Usage: //vispoints set/add/remove <target> <amount>");
			return true;
		}
		
		final String action = st.nextToken();
		if (!st.hasMoreTokens())
		{
			activeChar.sendSysMessage("Usage: //vispoints set/add/remove <target> <amount>");
			return true;
		}
		
		Player target = null;
		final String firstParam = st.nextToken();
		int amount;
		if (st.hasMoreTokens())
		{
			target = World.getInstance().getPlayer(firstParam);
			if (target == null)
			{
				activeChar.sendSysMessage("Target player not found.");
				return true;
			}
			
			try
			{
				amount = Integer.parseInt(st.nextToken());
			}
			catch (Exception e)
			{
				activeChar.sendSysMessage("Invalid amount.");
				return true;
			}
		}
		else
		{
			target = getTarget(activeChar);
			if (target == null)
			{
				activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
				return true;
			}
			
			try
			{
				amount = Integer.parseInt(firstParam);
			}
			catch (Exception e)
			{
				activeChar.sendSysMessage("Invalid amount.");
				return true;
			}
		}
		
		switch (action)
		{
			case "set":
			{
				if (amount < 0)
				{
					amount = 0;
				}
				
				target.setVirtualPoints(amount);
				notifyChange(activeChar, target, "set", amount);
				break;
			}
			case "add":
			{
				if (amount < 0)
				{
					activeChar.sendSysMessage("Amount must be positive.");
					return true;
				}
				
				target.addVirtualPoints(amount);
				notifyChange(activeChar, target, "added", amount);
				break;
			}
			case "remove":
			{
				if (amount < 0)
				{
					activeChar.sendSysMessage("Amount must be positive.");
					return true;
				}
				
				target.removeVirtualPoints(amount);
				notifyChange(activeChar, target, "removed", amount);
				break;
			}
			default:
			{
				activeChar.sendSysMessage("Usage: //vispoints set/add/remove <target> <amount>");
				return true;
			}
		}
		
		return true;
	}
	
	private void notifyChange(Player activeChar, Player target, String action, int amount)
	{
		target.sendMessage("Admin " + action + " " + amount + " VIS point(s). Current total: " + target.getVirtualPoints() + ".");
		activeChar.sendSysMessage("You " + action + " " + amount + " VIS point(s) for " + target.getName() + ". Total: " + target.getVirtualPoints() + ".");
		target.sendPacket(new UserInfo(target));
	}
	
	private Player getTarget(Player activeChar)
	{
		final WorldObject target = activeChar.getTarget();
		final Player targetPlayer = target != null ? target.asPlayer() : null;
		return targetPlayer != null ? targetPlayer : activeChar;
	}
	
	@Override
	public String[] getCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
