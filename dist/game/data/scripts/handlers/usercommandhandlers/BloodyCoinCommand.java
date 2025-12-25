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
package handlers.usercommandhandlers;

import org.l2jmobius.gameserver.config.ConquestConfig;
import org.l2jmobius.gameserver.handler.IUserCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * @author CostyKiller
 */
public class BloodyCoinCommand implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		180
	};
	
	@Override
	public boolean onCommand(int id, Player player)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		if (player.getLevel() < ConquestConfig.CONQUEST_TELEPORT_REQUIRED_LEVEL) // Retail level is 110.
		{
			player.sendPacket(SystemMessageId.THE_BLOODY_COIN_SYSTEM_IS_AVAILABLE_TO_CHARACTERS_OF_LV_110);
		}
		else
		{
			final int attackPoints = player.getVariables().getInt(PlayerVariables.CONQUEST_ATTACK_POINTS, ConquestConfig.CONQUEST_ATTACK_POINTS);
			final int lifePoints = player.getVariables().getInt(PlayerVariables.CONQUEST_LIFE_POINTS, ConquestConfig.CONQUEST_LIFE_POINTS);
			
			player.sendPacket(SystemMessageId.BLOODY_COINS_INFO);
			
			SystemMessage sm1 = new SystemMessage(SystemMessageId.ATTACK_POINTS_S1);
			sm1.addString(Integer.toString(attackPoints));
			player.sendPacket(sm1);
			
			SystemMessage sm2 = new SystemMessage(SystemMessageId.VITALITY_S1);
			sm2.addString(Integer.toString(lifePoints));
			player.sendPacket(sm2);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public int[] getCommandList()
	{
		return COMMAND_IDS;
	}
}
