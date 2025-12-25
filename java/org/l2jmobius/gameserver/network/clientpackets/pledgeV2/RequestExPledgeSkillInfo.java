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
public class RequestExPledgeSkillInfo extends ClientPacket
{
	private int _skillId;
	private int _skillLevel;
	
	@Override
	protected void readImpl()
	{
		_skillId = readInt();
		_skillLevel = readInt();
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
		
		int previous = 0;
		switch (_skillId)
		{
			case 19538:
			{
				previous = 4;
				break;
			}
			case 19539:
			{
				previous = 9;
				break;
			}
			case 19540:
			{
				previous = 11;
				break;
			}
			case 19541:
			{
				previous = 14;
				break;
			}
			case 19542:
			{
				previous = 16;
				break;
			}
		}
		
		int time = -1;
		int available = 0;
		final int remainingTime = clan.getMasterySkillRemainingTime(_skillId);
		if (remainingTime > 0)
		{
			time = remainingTime / 1000;
			available = 2;
		}
		else if (clan.hasMastery(previous))
		{
			available = 1;
		}
		
		player.sendPacket(new ExPledgeSkillInfo(_skillId, _skillLevel, time, available));
	}
}
