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
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExHomunculusPointInfo extends ServerPacket
{
	private final Player _player;
	private final PlayerVariables _variables;
	
	public ExHomunculusPointInfo(Player player)
	{
		_player = player;
		_variables = player.getVariables();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_HOMUNCULUS_POINT_INFO.writeId(this, buffer);
		buffer.writeInt(_variables.getInt(PlayerVariables.HOMUNCULUS_UPGRADE_POINTS, 0)); // points
		buffer.writeInt(_variables.getInt(PlayerVariables.HOMUNCULUS_KILLED_MOBS, 0)); // killed mobs
		buffer.writeInt(_variables.getInt(PlayerVariables.HOMUNCULUS_USED_KILL_CONVERT, 0)); // consumed basic kill points
		buffer.writeInt(_variables.getInt(PlayerVariables.HOMUNCULUS_USED_RESET_KILLS, 0)); // consumed reset kill points?
		buffer.writeInt(_variables.getInt(PlayerVariables.HOMUNCULUS_USED_VP_POINTS, 0)); // vp points
		buffer.writeInt(_variables.getInt(PlayerVariables.HOMUNCULUS_USED_VP_CONVERT, 0)); // consumed basic vp points
		buffer.writeInt(_variables.getInt(PlayerVariables.HOMUNCULUS_USED_RESET_VP, 0)); // consumed reset vp points
		buffer.writeInt(_player.getAvailableHomunculusSlotCount()); // 306
	}
}
