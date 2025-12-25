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

import org.l2jmobius.gameserver.data.holders.ClanMasteryHolder;
import org.l2jmobius.gameserver.data.xml.ClanMasteryData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.pledgeV2.ExPledgeMasteryInfo;

/**
 * @author Mobius
 */
public class RequestExPledgeMasterySet extends ClientPacket
{
	private int _masteryId;
	
	@Override
	protected void readImpl()
	{
		_masteryId = readInt();
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
		
		// Check if already enabled.
		if (clan.hasMastery(_masteryId))
		{
			player.sendMessage("This mastery is already available.");
			return;
		}
		
		// Check if it can be learned.
		if (clan.getTotalDevelopmentPoints() <= clan.getUsedDevelopmentPoints())
		{
			player.sendMessage("Your clan develpment points are not sufficient.");
			return;
		}
		
		final ClanMasteryHolder mastery = ClanMasteryData.getInstance().getClanMastery(_masteryId);
		if (clan.getLevel() < mastery.getClanLevel())
		{
			player.sendMessage("Your clan level is lower than the requirement.");
			return;
		}
		
		if (clan.getReputationScore() < mastery.getClanReputation())
		{
			player.sendMessage("Your clan reputation is lower than the requirement.");
			return;
		}
		
		final int previous = mastery.getPreviousMastery();
		final int previousAlt = mastery.getPreviousMasteryAlt();
		if (previousAlt > 0)
		{
			if (!clan.hasMastery(previous) && !clan.hasMastery(previousAlt))
			{
				player.sendMessage("You need to learn a previous mastery.");
				return;
			}
		}
		else if ((previous > 0) && !clan.hasMastery(previous))
		{
			player.sendMessage("You need to learn the previous mastery.");
			return;
		}
		
		// Learn.
		clan.takeReputationScore(mastery.getClanReputation());
		clan.addMastery(mastery.getId());
		clan.setDevelopmentPoints(clan.getUsedDevelopmentPoints() + 1);
		for (Skill skill : mastery.getSkills())
		{
			clan.addNewSkill(skill);
		}
		
		player.sendPacket(new ExPledgeMasteryInfo(player));
	}
}
