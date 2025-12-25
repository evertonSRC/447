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
package org.l2jmobius.gameserver.network.clientpackets;

/**
 * @author NviX
 */
public class RequestAutoUse extends ClientPacket
{
	@Override
	protected void readImpl()
	{
		// final int unk1 = readByte(); // C - true. This is summary amount of next data received.
		// PacketLogger.info("received packet RequestAutoUse with unk1:" + unk1);
		// final int unk2 = readByte();
		// PacketLogger.info("and unk2: " + unk2);
		// final int unk3 = readByte(); // Can target mobs, that attacked by other players?
		// PacketLogger.info("and unk3: " + unk3);
		// final int unk4 = readByte(); // Auto pickup?
		// PacketLogger.info("and unk4: " + unk4);
		// final int unk5 = readByte();
		// PacketLogger.info("and unk5: " + unk5);
		// final int unk6 = readByte();
		// PacketLogger.info("and unk6: " + unk6);
		// final int unk7 = readByte(); // short range :1; long: 0
		// PacketLogger.info("and unk7: " + unk7);
		// final int unk8 = readByte(); // received 51 when logged in game...
		// PacketLogger.info("and unk8: " + unk8);
		// final int unk9 = readByte();
		// PacketLogger.info("and unk9: " + unk9);
		// final int unk10 = readByte();
		// PacketLogger.info("and unk10: " + unk10);
		// final int unk11 = readByte();
		// PacketLogger.info("and unk11: " + unk11);
		// final int unk12 = readByte(); // enable/ disable?
		// PacketLogger.info("and unk12: " + unk12);
	}
	
	@Override
	protected void runImpl()
	{
	}
}
