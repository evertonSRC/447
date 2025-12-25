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
package org.l2jmobius.gameserver.model.script;

/**
 * @author Mobius
 */
public enum Faction
{
	BLACKBIRD_CLAN(1, 200, 1200, 3200, 6200, 11200, 19200, 30200),
	MOTHER_TREE_GUARDIANS(2, 100, 1000, 2800, 5500, 10000, 17200, 27100),
	GIANT_TRACKERS(3, 200, 1350, 3650, 7100, 12850, 22050, 34700),
	UNWORLDLY_VISITORS(4, 100, 1200, 3400, 6700, 12200, 21000, 33100),
	KINGDOM_ROYAL_GUARDS(5, 100, 900, 2500, 4900, 8100, 12100, 16900, 22500, 29700, 38500, 48900),
	FISHING_GUILD(6, 100, 7300, 18100, 32500, 53500, 78700, 106700),
	HUNTERS_GUILD(7, 200, 4000, 9600, 19900, 32500, 47200, 64000),
	ADVENTURE_GUILD(8, 200, 4000, 9600, 19900, 32500, 47200, 64000);
	
	private int _id;
	private int[] _points;
	
	private Faction(int id, int... points)
	{
		_id = id;
		_points = points;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getLevelCount()
	{
		return _points.length;
	}
	
	public int getPointsOfLevel(int level)
	{
		if (level < 0)
		{
			return 0;
		}
		
		if (level > (_points.length - 1))
		{
			return _points[_points.length - 1];
		}
		
		return _points[level];
	}
}
