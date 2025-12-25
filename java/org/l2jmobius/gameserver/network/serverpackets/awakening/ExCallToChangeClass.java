/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.serverpackets.awakening;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExCallToChangeClass extends ServerPacket
{
	private final int _classId;
	private final boolean _showMessage;
	
	public ExCallToChangeClass(int classId, boolean showMessage)
	{
		_classId = classId;
		_showMessage = showMessage;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_CALL_TO_CHANGE_CLASS.writeId(this, buffer);
		buffer.writeInt(_classId);
		buffer.writeInt(_showMessage ? 1 : 0);
		buffer.writeInt(1); // Force - 0 you have to do it; 1 it's optional
	}
}
