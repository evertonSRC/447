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
package org.l2jmobius.gameserver.network.clientpackets.pledgeV2;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.pledgeV2.ExPledgeSkillInfo;

/**
 * @author Mobius
 */
public class RequestExPledgeSkillActivate extends ClientPacket
{
	private int _skillId;
	
	@Override
	protected void readImpl()
	{
		_skillId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Clan clan = player.getClan();
		if (clan == null)
		{
			return;
		}
		
		if (player.getObjectId() != clan.getLeaderId())
		{
			player.sendMessage("You do not have enough privileges to take this action.");
			return;
		}
		
		// Check if it can be learned.
		int previous = 0;
		int cost = 0;
		switch (_skillId)
		{
			case 19538:
			{
				previous = 4;
				cost = 40000;
				break;
			}
			case 19539:
			{
				previous = 9;
				cost = 30000;
				break;
			}
			case 19540:
			{
				previous = 11;
				cost = 50000;
				break;
			}
			case 19541:
			{
				previous = 14;
				cost = 30000;
				break;
			}
			case 19542:
			{
				previous = 16;
				cost = 50000;
				break;
			}
		}
		
		if (clan.getReputationScore() < cost)
		{
			player.sendMessage("Your clan reputation is lower than the requirement.");
			return;
		}
		
		if (!clan.hasMastery(previous))
		{
			player.sendMessage("You need to learn the previous mastery.");
			return;
		}
		
		// Check if already enabled.
		if (clan.getMasterySkillRemainingTime(_skillId) > 0)
		{
			clan.removeMasterySkill(_skillId);
			return;
		}
		
		// Learn.
		clan.takeReputationScore(cost);
		clan.addMasterySkill(_skillId);
		player.sendPacket(new ExPledgeSkillInfo(_skillId, 1, 1296000, 2));
	}
}
