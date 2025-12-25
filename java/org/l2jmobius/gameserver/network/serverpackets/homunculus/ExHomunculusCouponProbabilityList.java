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
import org.l2jmobius.gameserver.data.xml.HomunculusData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.HomunculusCreationTemplate;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Index
 */
public class ExHomunculusCouponProbabilityList extends ServerPacket
{
	private final Player _player;
	private final int _couponId;
	
	public ExHomunculusCouponProbabilityList(Player player, int couponId)
	{
		_player = player;
		_couponId = couponId;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		if (_player == null)
		{
			return;
		}
		
		final HomunculusCreationTemplate creationTemplate = HomunculusCreationData.getInstance().getTemplateByItemId(_couponId);
		if (creationTemplate == null)
		{
			return;
		}
		
		ServerPackets.EX_HOMUNCULUS_COUPON_PROB_LIST.writeId(this, buffer);
		buffer.writeInt(_couponId);
		buffer.writeInt(creationTemplate.getCreationChance().size());
		for (int type = 0; type < 3; type++)
		{
			for (Double[] homunculusChance : creationTemplate.getCreationChance())
			{
				if (HomunculusData.getInstance().getTemplate(homunculusChance[0].intValue()).getType() == type)
				{
					buffer.writeInt(homunculusChance[0].intValue());
					buffer.writeInt((int) (homunculusChance[1] * 1000000));
				}
			}
		}
	}
}
