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
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.model.homunculus.HomunculusList;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author nexvill, Mobius
 */
public class ExEnchantHomunculusSkillResult extends ServerPacket
{
	private final int _slot;
	private final int _skillNumber;
	private final int _homunculusNumber;
	private final int _systemNumber;
	private final int _playerNumber;
	private final int _boundLevel;
	private final int _homunculusId;
	
	public ExEnchantHomunculusSkillResult(Player player, int slot, int skillNumber)
	{
		_slot = slot;
		_skillNumber = skillNumber;
		_homunculusNumber = Rnd.get(1, 6);
		_systemNumber = Rnd.get(1, 6);
		_playerNumber = Rnd.get(1, 6);
		if ((_playerNumber == _homunculusNumber) && (_playerNumber == _systemNumber))
		{
			_boundLevel = 3;
		}
		else if ((_playerNumber == _homunculusNumber) || (_playerNumber == _systemNumber) || (_homunculusNumber == _systemNumber))
		{
			_boundLevel = 2;
		}
		else
		{
			_boundLevel = 1;
		}
		
		final HomunculusList homunculusList = player.getHomunculusList();
		final Homunculus homunculus = homunculusList.get(_slot);
		switch (_skillNumber)
		{
			case 1:
			{
				homunculus.setSkillLevel1(_boundLevel);
				break;
			}
			case 2:
			{
				homunculus.setSkillLevel2(_boundLevel);
				break;
			}
			case 3:
			{
				homunculus.setSkillLevel3(_boundLevel);
				break;
			}
			case 4:
			{
				homunculus.setSkillLevel4(_boundLevel);
				break;
			}
			case 5:
			{
				homunculus.setSkillLevel5(_boundLevel);
				break;
			}
		}
		
		_homunculusId = homunculus.getId();
		homunculusList.update(homunculus);
		homunculusList.refreshStats(true);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ENCHANT_HOMUNCULUS_SKILL_RESULT.writeId(this, buffer);
		buffer.writeInt(_boundLevel); // Skill bound level result.
		buffer.writeInt(_homunculusId); // homunculus id? Random value on JP.
		buffer.writeInt(_slot);
		buffer.writeInt(_skillNumber);
		buffer.writeInt(_playerNumber);
		buffer.writeInt(_homunculusNumber);
		buffer.writeInt(_systemNumber);
	}
}
