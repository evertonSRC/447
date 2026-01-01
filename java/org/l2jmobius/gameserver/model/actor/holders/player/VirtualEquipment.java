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
package org.l2jmobius.gameserver.model.actor.holders.player;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.l2jmobius.gameserver.model.actor.enums.player.VirtualSlot;

/**
 * @author Mobius
 */
public class VirtualEquipment
{
	private final Map<VirtualSlot, VirtualEquippedItem> _items = new EnumMap<>(VirtualSlot.class);
	
	public Map<VirtualSlot, VirtualEquippedItem> getItems()
	{
		return Collections.unmodifiableMap(_items);
	}
	
	public VirtualEquippedItem get(VirtualSlot slot)
	{
		return _items.get(slot);
	}
	
	public void set(VirtualSlot slot, VirtualEquippedItem item)
	{
		if ((slot == null) || (item == null))
		{
			return;
		}
		
		_items.put(slot, item);
	}
	
	public void remove(VirtualSlot slot)
	{
		_items.remove(slot);
	}
	
	public boolean isSlotOccupied(VirtualSlot slot)
	{
		return (slot != null) && _items.containsKey(slot);
	}
	
	public int size()
	{
		return _items.size();
	}
	
	public boolean isEmpty()
	{
		return _items.isEmpty();
	}
	
	public void clear()
	{
		_items.clear();
	}
	
	public void putAll(Map<VirtualSlot, VirtualEquippedItem> items)
	{
		_items.putAll(items);
	}
}
