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
import org.l2jmobius.gameserver.model.actor.Player;

/**
 * @author Mobius
 */
public class AdminVisPoints implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_addvispoints",
		"admin_setvispoints",
		"admin_getvispoints"
	};
	
	@Override
	public boolean onCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		
		switch (actualCommand)
		{
			case "admin_addvispoints":
			{
				if (st.countTokens() < 2)
				{
					sendUsage(activeChar);
					return false;
				}
				
				final String playerName = st.nextToken();
				final Player target = World.getInstance().getPlayer(playerName);
				if (target == null)
				{
					activeChar.sendSysMessage("Player não encontrado: " + playerName + ".");
					return false;
				}
				
				final int amount = parseAmount(activeChar, st);
				if (amount < 0)
				{
					return false;
				}
				
				final int newPoints = target.addVirtualPoints(amount);
				target.sendMessage("Admin adicionou " + amount + " VISPoints. Total: " + newPoints + ".");
				activeChar.sendSysMessage("VISPoints de " + target.getName() + " agora: " + newPoints + ".");
				return true;
			}
			case "admin_setvispoints":
			{
				if (st.countTokens() < 2)
				{
					sendUsage(activeChar);
					return false;
				}
				
				final String playerName = st.nextToken();
				final Player target = World.getInstance().getPlayer(playerName);
				if (target == null)
				{
					activeChar.sendSysMessage("Player não encontrado: " + playerName + ".");
					return false;
				}
				
				final int amount = parseAmount(activeChar, st);
				if (amount < 0)
				{
					return false;
				}
				
				target.setVirtualPoints(amount);
				target.sendMessage("Admin definiu seus VISPoints para " + target.getVirtualPoints() + ".");
				activeChar.sendSysMessage("VISPoints de " + target.getName() + " agora: " + target.getVirtualPoints() + ".");
				return true;
			}
			case "admin_getvispoints":
			{
				if (!st.hasMoreTokens())
				{
					sendUsage(activeChar);
					return false;
				}
				
				final String playerName = st.nextToken();
				final Player target = World.getInstance().getPlayer(playerName);
				if (target == null)
				{
					activeChar.sendSysMessage("Player não encontrado: " + playerName + ".");
					return false;
				}
				
				activeChar.sendSysMessage("VISPoints de " + target.getName() + ": " + target.getVirtualPoints() + ".");
				return true;
			}
		}
		
		sendUsage(activeChar);
		return false;
	}
	
	private int parseAmount(Player activeChar, StringTokenizer st)
	{
		try
		{
			return Integer.parseInt(st.nextToken());
		}
		catch (NumberFormatException e)
		{
			activeChar.sendSysMessage("Valor inválido.");
			return -1;
		}
	}
	
	private void sendUsage(Player activeChar)
	{
		activeChar.sendSysMessage("Uso: //addvispoints <player> <amount>");
		activeChar.sendSysMessage("Uso: //setvispoints <player> <amount>");
		activeChar.sendSysMessage("Uso: //getvispoints <player>");
	}
	
	@Override
	public String[] getCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
