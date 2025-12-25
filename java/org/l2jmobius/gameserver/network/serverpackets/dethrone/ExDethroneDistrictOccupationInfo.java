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
package org.l2jmobius.gameserver.network.serverpackets.dethrone;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.config.ServerConfig;
import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExDethroneDistrictOccupationInfo extends ServerPacket
{
	private final int _category;
	
	public ExDethroneDistrictOccupationInfo(int category)
	{
		_category = category;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_DETHRONE_DISTRICT_OCCUPATION_INFO.writeId(this, buffer);
		
		buffer.writeByte(_category);
		
		if (_category == 0) // Water Area
		{
			buffer.writeInt(3); // array size for conquest regions Water Area (currently 3)
			
			// Zone 1 ASA
			buffer.writeInt(1); // zone name id for ASA
			buffer.writeInt(ServerConfig.SERVER_ID); // server id for ASA ZONE CONQUERER
			buffer.writeInt(1); // array size for ASA servers (only one server atm)
			
			buffer.writeInt(1); // server rank 1
			buffer.writeInt(ServerConfig.SERVER_ID); // rank 1 server id
			buffer.writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_ASA_POINTS", 0)); // rank 1 server points
			buffer.writeInt(0);
			
			// buffer.writeInt(2); // server rank 2
			// buffer.writeInt(0); // rank 2 server id
			// buffer.writeInt(0); // rank 2 server points
			// buffer.writeInt(0);
			
			// Zone 2 ANIMA
			buffer.writeInt(2); // zone name id for ANIMA
			buffer.writeInt(ServerConfig.SERVER_ID); // server id for ANIMA ZONE CONQUERER
			buffer.writeInt(1); // array size for ANIMA servers (only one server atm)
			
			buffer.writeInt(1); // server rank 1
			buffer.writeInt(ServerConfig.SERVER_ID); // rank 1 server id
			buffer.writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_ANIMA_POINTS", 0)); // rank 1 server points
			buffer.writeInt(0);
			
			// buffer.writeInt(2); // server rank 2
			// buffer.writeInt(0); // rank 2 server id
			// buffer.writeInt(0); // rank 2 server points
			// buffer.writeInt(0);
			
			// Zone 3 NOX
			buffer.writeInt(3); // zone name id for NOX
			buffer.writeInt(ServerConfig.SERVER_ID); // server id for NOX ZONE CONQUERER
			buffer.writeInt(1); // array size for NOX servers (only one server atm)
			
			buffer.writeInt(1); // server rank 1
			buffer.writeInt(ServerConfig.SERVER_ID); // rank 1 server id
			buffer.writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_NOX_POINTS", 0)); // rank 1 server points
			buffer.writeInt(0);
			
			// buffer.writeInt(2); // server rank 2
			// buffer.writeInt(0); // rank 2 server id
			// buffer.writeInt(0); // rank 2 server points
			// buffer.writeInt(0);
		}
		else // Fire Area
		{
			buffer.writeInt(2); // array size for conquest regions Fire Area (currently 2)
			
			// Zone 4 VITA
			buffer.writeInt(4); // zone name id for VITA
			buffer.writeInt(ServerConfig.SERVER_ID); // server id for VITA ZONE CONQUERER
			buffer.writeInt(1); // array size for VITA servers (only one server atm)
			
			buffer.writeInt(1); // server rank 1
			buffer.writeInt(ServerConfig.SERVER_ID); // rank 1 server id
			buffer.writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_VITA_POINTS", 0)); // rank 1 server points
			buffer.writeInt(0);
			
			// buffer.writeInt(2); // server rank 2
			// buffer.writeInt(0); // rank 2 server id
			// buffer.writeInt(0); // rank 2 server points
			// buffer.writeInt(0);
			
			// Zone 5 IGNIS
			buffer.writeInt(5); // zone name id for IGNIS
			buffer.writeInt(ServerConfig.SERVER_ID); // server id for IGNIS ZONE CONQUERER
			buffer.writeInt(1); // array size for IGNIS servers (only one server atm)
			
			buffer.writeInt(1); // server rank 1
			buffer.writeInt(ServerConfig.SERVER_ID); // rank 1 server id
			buffer.writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_IGNIS_POINTS", 0)); // rank 1 server points
			buffer.writeInt(0);
			
			// buffer.writeInt(2); // server rank 2
			// buffer.writeInt(0); // rank 2 server id
			// buffer.writeInt(0); // rank 2 server points
			// buffer.writeInt(0);
		}
	}
}
