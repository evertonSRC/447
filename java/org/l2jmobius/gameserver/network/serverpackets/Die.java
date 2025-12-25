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
package org.l2jmobius.gameserver.network.serverpackets;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.managers.CastleManager;
import org.l2jmobius.gameserver.managers.FortManager;
import org.l2jmobius.gameserver.model.SiegeClan;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.siege.Castle;
import org.l2jmobius.gameserver.model.siege.Fort;
import org.l2jmobius.gameserver.model.skill.BuffInfo;
import org.l2jmobius.gameserver.model.skill.CommonSkill;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class Die extends ServerPacket
{
	private final int _objectId;
	private final boolean _isSweepable;
	private int _flags = 1; // To nearest village.
	private int _delayFeather = 0;
	
	public Die(Creature creature)
	{
		_objectId = creature.getObjectId();
		_isSweepable = creature.isAttackable() && creature.isSweepActive();
		if (creature.isPlayer())
		{
			for (BuffInfo effect : creature.getEffectList().getEffects())
			{
				if (effect.getSkill().getId() == CommonSkill.FEATHER_OF_BLESSING.getId())
				{
					_delayFeather = effect.getTime();
					break;
				}
			}
			
			final Player player = creature.asPlayer();
			if (!player.isInTimedHuntingZone())
			{
				final Clan clan = player.getClan();
				boolean isInCastleDefense = false;
				boolean isInFortDefense = false;
				SiegeClan siegeClan = null;
				final Castle castle = CastleManager.getInstance().getCastle(creature);
				final Fort fort = FortManager.getInstance().getFort(creature);
				if ((castle != null) && castle.getSiege().isInProgress())
				{
					siegeClan = castle.getSiege().getAttackerClan(clan);
					isInCastleDefense = (siegeClan == null) && castle.getSiege().checkIsDefender(clan);
				}
				else if ((fort != null) && fort.getSiege().isInProgress())
				{
					siegeClan = fort.getSiege().getAttackerClan(clan);
					isInFortDefense = (siegeClan == null) && fort.getSiege().checkIsDefender(clan);
				}
				
				// ClanHall check.
				if ((clan != null) && (clan.getHideoutId() > 0))
				{
					_flags += 2;
				}
				
				// Castle check.
				if (((clan != null) && (clan.getCastleId() > 0)) || isInCastleDefense)
				{
					_flags += 4;
				}
				
				// Fortress check.
				if (((clan != null) && (clan.getFortId() > 0)) || isInFortDefense)
				{
					_flags += 8;
				}
				
				// Outpost check.
				if (((siegeClan != null) && !isInCastleDefense && !isInFortDefense && !siegeClan.getFlag().isEmpty()))
				{
					_flags += 16;
				}
			}
			else
			{
				_flags = -32388608; // To restart place.
			}
			
			// Feather check.
			if (creature.getAccessLevel().allowFixedRes() || creature.getInventory().haveItemForSelfResurrection())
			{
				_flags += 32;
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.DIE.writeId(this, buffer);
		buffer.writeInt(_objectId);
		buffer.writeLong(_flags);
		buffer.writeInt(_isSweepable);
		buffer.writeInt(_delayFeather); // Feather item time.
		buffer.writeByte(0); // Hide die animation.
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
	}
}
