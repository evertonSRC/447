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
package org.l2jmobius.gameserver.network.enums;

import java.util.function.Function;

import org.l2jmobius.gameserver.model.actor.Creature;

/**
 * @author UnAfraid, Mobius
 */
public enum StatusUpdateType
{
	LEVEL(0x01, Creature::getLevel),
	EXP(0x02, creature -> (int) creature.getStat().getExp()),
	STR(0x03, Creature::getSTR),
	DEX(0x04, Creature::getDEX),
	CON(0x05, Creature::getCON),
	INT(0x06, Creature::getINT),
	WIT(0x07, Creature::getWIT),
	MEN(0x08, Creature::getMEN),
	
	CUR_HP(0x09, creature -> (int) creature.getCurrentHp()),
	MAX_HP(0x0A, Creature::getMaxHp),
	CUR_MP(0x0B, creature -> (int) creature.getCurrentMp()),
	MAX_MP(0x0C, Creature::getMaxMp),
	CUR_LOAD(0x0E, Creature::getCurrentLoad),
	
	P_ATK(0x11, Creature::getPAtk),
	ATK_SPD(0x12, Creature::getPAtkSpd),
	P_DEF(0x13, Creature::getPDef),
	EVASION(0x14, Creature::getEvasionRate),
	ACCURACY(0x15, Creature::getAccuracy),
	CRITICAL(0x16, creature -> (int) creature.getCriticalDmg(1)),
	M_ATK(0x17, Creature::getMAtk),
	CAST_SPD(0x18, Creature::getMAtkSpd),
	M_DEF(0x19, Creature::getMDef),
	PVP_FLAG(0x1A, creature -> (int) creature.getPvpFlag()),
	REPUTATION(0x1B, creature -> creature.isPlayer() ? creature.asPlayer().getReputation() : 0),
	
	CUR_CP(0x21, creature -> (int) creature.getCurrentCp()),
	MAX_CP(0x22, Creature::getMaxCp);
	
	private final int _clientId;
	private final Function<Creature, Integer> _valueSupplier;
	
	StatusUpdateType(int clientId, Function<Creature, Integer> valueSupplier)
	{
		_clientId = clientId;
		_valueSupplier = valueSupplier;
	}
	
	public int getClientId()
	{
		return _clientId;
	}
	
	public int getValue(Creature creature)
	{
		return _valueSupplier.apply(creature).intValue();
	}
}
