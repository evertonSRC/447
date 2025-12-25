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
package org.l2jmobius.gameserver.model.homunculus;

import java.util.List;

import org.l2jmobius.gameserver.model.item.holders.ItemHolder;

public class HomunculusCreationTemplate
{
	private final int _slotId;
	private final boolean _isEnabled;
	private final int _grade;
	private final boolean _isEvent;
	private final List<ItemHolder> _itemsFee;
	private final Integer[] _hpFee;
	private final Long[] _spFee;
	private final Integer[] _vpFee;
	private final long _time;
	private final List<Double[]> _createChances;
	
	public HomunculusCreationTemplate(int slotId, boolean isEnabled, int grade, boolean isEvent, List<ItemHolder> itemsFee, Integer[] hpFee, Long[] spFee, Integer[] vpFee, long time, List<Double[]> createChances)
	{
		_slotId = slotId;
		_isEnabled = isEnabled;
		_grade = grade;
		_isEvent = isEvent;
		_itemsFee = itemsFee;
		_hpFee = hpFee;
		_spFee = spFee;
		_vpFee = vpFee;
		_time = time;
		_createChances = createChances;
	}
	
	public int getSlotId()
	{
		return _slotId;
	}
	
	public boolean isEnabled()
	{
		return _isEnabled;
	}
	
	public int getGrade()
	{
		return _grade;
	}
	
	public boolean isEvent()
	{
		return _isEvent;
	}
	
	public List<ItemHolder> getItemFee()
	{
		return _itemsFee;
	}
	
	public boolean haveAnotherFee()
	{
		return !_itemsFee.isEmpty();
	}
	
	public int getHPFeeCountByUse()
	{
		return _hpFee[1];
	}
	
	public int getHPFeeCount()
	{
		return _hpFee[0];
	}
	
	public long getSPFeeCountByUse()
	{
		return _spFee[1];
	}
	
	public long getSPFeeCount()
	{
		return _spFee[0];
	}
	
	public int getVPFeeByUse()
	{
		return _vpFee[1];
	}
	
	public int getVPFeeCount()
	{
		return _vpFee[0];
	}
	
	public double getMaxChance()
	{
		double result = 0;
		for (int i = 0; i < _createChances.size(); i++)
		{
			Double[] chance = _createChances.get(i);
			result = result + chance[1];
		}
		
		return result;
	}
	
	public boolean isInstanceHaveCoupon(int itemId)
	{
		for (ItemHolder humu : _itemsFee)
		{
			if (humu.getId() == itemId)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public long getCreationTime()
	{
		return _time;
	}
	
	public List<Double[]> getCreationChance()
	{
		return _createChances;
	}
}
