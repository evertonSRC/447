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
package org.l2jmobius.gameserver.network.serverpackets.homunculus;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.model.homunculus.HomunculusList;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExShowHomunculusList extends ServerPacket
{
	private final int _slotCount;
	private final HomunculusList _homunculusList;
	
	public ExShowHomunculusList(Player player)
	{
		_slotCount = player.getAvailableHomunculusSlotCount();
		_homunculusList = player.getHomunculusList();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_HOMUNCULUS_LIST.writeId(this, buffer);
		int counter = 0;
		buffer.writeInt(_slotCount);
		for (int i = 0; i <= _slotCount; i++)
		{
			if (_homunculusList.get(i) != null)
			{
				final Homunculus homunculus = _homunculusList.get(i);
				buffer.writeInt(counter); // slot
				buffer.writeInt(homunculus.getId()); // homunculus id
				buffer.writeInt(homunculus.getType());
				buffer.writeByte(homunculus.isActive());
				buffer.writeInt(homunculus.getTemplate().getBasicSkillId());
				buffer.writeInt(homunculus.getSkillLevel1() > 0 ? homunculus.getTemplate().getSkillId1() : 0);
				buffer.writeInt(homunculus.getSkillLevel2() > 0 ? homunculus.getTemplate().getSkillId2() : 0);
				buffer.writeInt(homunculus.getSkillLevel3() > 0 ? homunculus.getTemplate().getSkillId3() : 0);
				buffer.writeInt(homunculus.getSkillLevel4() > 0 ? homunculus.getTemplate().getSkillId4() : 0);
				buffer.writeInt(homunculus.getSkillLevel5() > 0 ? homunculus.getTemplate().getSkillId5() : 0);
				buffer.writeInt(homunculus.getTemplate().getBasicSkillLevel());
				buffer.writeInt(homunculus.getSkillLevel1());
				buffer.writeInt(homunculus.getSkillLevel2());
				buffer.writeInt(homunculus.getSkillLevel3());
				buffer.writeInt(homunculus.getSkillLevel4());
				buffer.writeInt(homunculus.getSkillLevel5());
				buffer.writeInt(homunculus.getLevel());
				buffer.writeInt(homunculus.getExp());
				buffer.writeInt(homunculus.getHp());
				buffer.writeInt(homunculus.getAtk());
				buffer.writeInt(homunculus.getDef());
				buffer.writeInt(homunculus.getCritRate());
			}
			else
			{
				buffer.writeInt(counter); // slot
				buffer.writeInt(0); // homunculus id
				buffer.writeInt(0);
				buffer.writeByte(0);
				buffer.writeInt(0);
				for (int j = 1; j <= 5; j++)
				{
					buffer.writeInt(0);
				}
				
				buffer.writeInt(0);
				for (int j = 1; j <= 5; j++)
				{
					buffer.writeInt(0);
				}
				
				buffer.writeInt(0); // Level
				buffer.writeInt(0); // HP
				buffer.writeInt(0); // HP
				buffer.writeInt(0); // Attack
				buffer.writeInt(0); // Defence
				buffer.writeInt(0); // Critical
			}
			
			counter++;
		}
	}
}
