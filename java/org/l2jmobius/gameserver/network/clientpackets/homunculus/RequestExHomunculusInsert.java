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
package org.l2jmobius.gameserver.network.clientpackets.homunculus;

import org.l2jmobius.gameserver.data.xml.HomunculusCreationData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusHPSPVP;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusInsertResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusBirthInfo;

/**
 * @author Mobius
 */
public class RequestExHomunculusInsert extends ClientPacket
{
	private int _type;
	
	@Override
	protected void readImpl()
	{
		_type = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		int hpPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_HP_POINTS, 0);
		int spPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_SP_POINTS, 0);
		int vpPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_VP_POINTS, 0);
		switch (_type)
		{
			case 0:
			{
				if ((player.getCurrentHp() > HomunculusCreationData.getInstance().getDefaultTemplate().getHPFeeCountByUse()) && (hpPoints < HomunculusCreationData.getInstance().getDefaultTemplate().getHPFeeCount()))
				{
					int newHp = (int) (player.getCurrentHp()) - HomunculusCreationData.getInstance().getDefaultTemplate().getHPFeeCountByUse();
					player.setCurrentHp(newHp, true);
					hpPoints += 1;
					player.getVariables().set(PlayerVariables.HOMUNCULUS_HP_POINTS, hpPoints);
				}
				else
				{
					return;
				}
				break;
			}
			case 1:
			{
				if ((player.getSp() >= HomunculusCreationData.getInstance().getDefaultTemplate().getSPFeeCountByUse()) && (spPoints < HomunculusCreationData.getInstance().getDefaultTemplate().getSPFeeCount()))
				{
					player.setSp(player.getSp() - HomunculusCreationData.getInstance().getDefaultTemplate().getSPFeeCountByUse());
					spPoints += 1;
					player.getVariables().set(PlayerVariables.HOMUNCULUS_SP_POINTS, spPoints);
				}
				else
				{
					return;
				}
				break;
			}
			case 2:
			{
				if ((player.getVitalityPoints() >= HomunculusCreationData.getInstance().getDefaultTemplate().getVPFeeByUse()) && (vpPoints < HomunculusCreationData.getInstance().getDefaultTemplate().getVPFeeCount()))
				{
					int newVitality = player.getVitalityPoints() - HomunculusCreationData.getInstance().getDefaultTemplate().getVPFeeByUse();
					player.setVitalityPoints(newVitality, true);
					vpPoints += 1;
					player.getVariables().set(PlayerVariables.HOMUNCULUS_VP_POINTS, vpPoints);
				}
				else
				{
					return;
				}
				break;
			}
		}
		
		player.getHomunculusList().refreshStats(true);
		
		player.sendPacket(new ExShowHomunculusBirthInfo(player));
		player.sendPacket(new ExHomunculusHPSPVP(player));
		player.sendPacket(new ExHomunculusInsertResult(_type));
	}
}
