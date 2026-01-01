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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mobius
 */
public class VirtualItemGroup
{
	private final int _indexMain;
	private final Map<Integer, VirtualItemEntry> _entries = new HashMap<>();
	
	public VirtualItemGroup(int indexMain)
	{
		_indexMain = indexMain;
	}
	
	public int getIndexMain()
	{
		return _indexMain;
	}
	
	public VirtualItemEntry getEntry(int indexSub)
	{
		return _entries.get(indexSub);
	}
	
	public void addEntry(VirtualItemEntry entry)
	{
		_entries.put(entry.getIndexSub(), entry);
	}
	
	public Collection<VirtualItemEntry> getEntries()
	{
		return Collections.unmodifiableCollection(_entries.values());
	}
	
	public boolean hasEntry(int indexSub)
	{
		return _entries.containsKey(indexSub);
	}
}
