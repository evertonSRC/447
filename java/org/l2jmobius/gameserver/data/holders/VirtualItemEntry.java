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
package org.l2jmobius.gameserver.data.holders;

/**
 * @author Mobius
 */
public class VirtualItemEntry
{
	private final int _indexMain;
	private final int _indexSub;
	private final long _slotIdClient;
	private final int _virtualSlotId;
	private final String _slotAlias;
	private final int _itemId;
	private final int _enchant;
	private final int _costVISPoint;
	
	public VirtualItemEntry(int indexMain, int indexSub, long slotIdClient, int virtualSlotId, String slotAlias, int itemId, int enchant, int costVISPoint)
	{
		_indexMain = indexMain;
		_indexSub = indexSub;
		_slotIdClient = slotIdClient;
		_virtualSlotId = virtualSlotId;
		_slotAlias = slotAlias;
		_itemId = itemId;
		_enchant = enchant;
		_costVISPoint = costVISPoint;
	}
	
	public int getIndexMain()
	{
		return _indexMain;
	}
	
	public int getIndexSub()
	{
		return _indexSub;
	}
	
	public long getSlotIdClient()
	{
		return _slotIdClient;
	}
	
	public int getVirtualSlotId()
	{
		return _virtualSlotId;
	}
	
	public String getSlotAlias()
	{
		return _slotAlias;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getEnchant()
	{
		return _enchant;
	}
	
	public int getCostVISPoint()
	{
		return _costVISPoint;
	}
}
