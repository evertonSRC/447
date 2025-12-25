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
import org.l2jmobius.gameserver.data.xml.HomunculusCreationData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.HomunculusCreationTemplate;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExShowHomunculusBirthInfo extends ServerPacket
{
	private final int _hpPoints;
	private final int _spPoints;
	private final int _vpPoints;
	private final int _homunculusCreateTime;
	private final int _feeHpPoints;
	private final int _feeSpPoints;
	private final int _feeVpPoints;
	
	public ExShowHomunculusBirthInfo(Player player)
	{
		final PlayerVariables variables = player.getVariables();
		_hpPoints = variables.getInt(PlayerVariables.HOMUNCULUS_HP_POINTS, 0);
		_spPoints = variables.getInt(PlayerVariables.HOMUNCULUS_SP_POINTS, 0);
		_vpPoints = variables.getInt(PlayerVariables.HOMUNCULUS_VP_POINTS, 0);
		_homunculusCreateTime = (int) (variables.getLong(PlayerVariables.HOMUNCULUS_CREATION_TIME, 0) / 1000);
		
		final HomunculusCreationTemplate template = HomunculusCreationData.getInstance().getDefaultTemplate();
		_feeHpPoints = template.getHPFeeCount();
		_feeSpPoints = (int) template.getSPFeeCount();
		_feeVpPoints = template.getVPFeeCount();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_HOMUNCULUS_BIRTH_INFO.writeId(this, buffer);
		int creationStage = 0;
		if (_homunculusCreateTime > 0)
		{
			if (((System.currentTimeMillis() / 1000) >= _homunculusCreateTime) && (_hpPoints == _feeHpPoints) && (_spPoints == _feeSpPoints) && (_vpPoints == _feeVpPoints))
			{
				creationStage = 2;
			}
			else
			{
				creationStage = 1;
			}
		}
		
		buffer.writeInt(creationStage); // in creation process (0: can create, 1: in process, 2: can awake
		buffer.writeInt(_hpPoints); // hp points
		buffer.writeInt(_spPoints); // sp points
		buffer.writeInt(_vpPoints); // vp points
		buffer.writeLong(_homunculusCreateTime); // finish time
		// buffer.writeInt(0); // JP = 0. ?
	}
}
