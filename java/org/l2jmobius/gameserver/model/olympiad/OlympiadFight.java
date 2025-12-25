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
package org.l2jmobius.gameserver.model.olympiad;

/**
 * @author dontknowdontcare
 */
public class OlympiadFight
{
	private final String _opponentName;
	private final int _opponentClassId;
	private final int _winner; // 0 = win, 1 = loss, 2 = draw
	private final int _opponentLevel;
	
	public OlympiadFight(String opponentName, int opponentLevel, int opponentClassId, int winner)
	{
		_opponentName = opponentName;
		_opponentClassId = opponentClassId;
		_winner = winner;
		_opponentLevel = opponentLevel;
	}
	
	public String getOpponentName()
	{
		return _opponentName;
	}
	
	public int getOpponentClassId()
	{
		return _opponentClassId;
	}
	
	public int getWinner()
	{
		return _winner;
	}
	
	public int getOpponentLevel()
	{
		return _opponentLevel;
	}
}
