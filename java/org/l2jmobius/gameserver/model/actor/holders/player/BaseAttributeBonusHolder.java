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
package org.l2jmobius.gameserver.model.actor.holders.player;

import org.l2jmobius.gameserver.model.stats.BaseStat;

public class BaseAttributeBonusHolder
{
	private int _str;
	private int _con;
	private int _dex;
	private int _intValue;
	private int _wit;
	private int _men;
	
	public int getStrBonus()
	{
		return _str;
	}
	
	public int getConBonus()
	{
		return _con;
	}
	
	public int getDexBonus()
	{
		return _dex;
	}
	
	public int getIntBonus()
	{
		return _intValue;
	}
	
	public int getWitBonus()
	{
		return _wit;
	}
	
	public int getMenBonus()
	{
		return _men;
	}
	
	public int getBonus(BaseStat stat)
	{
		switch (stat)
		{
			case STR:
			{
				return _str;
			}
			case CON:
			{
				return _con;
			}
			case DEX:
			{
				return _dex;
			}
			case INT:
			{
				return _intValue;
			}
			case WIT:
			{
				return _wit;
			}
			case MEN:
			{
				return _men;
			}
		}
		
		return 0;
	}
	
	public void addBonus(BaseStat stat, int amount)
	{
		setBonus(stat, getBonus(stat) + amount);
	}
	
	public void setBonus(BaseStat stat, int value)
	{
		switch (stat)
		{
			case STR:
			{
				_str = value;
				break;
			}
			case CON:
			{
				_con = value;
				break;
			}
			case DEX:
			{
				_dex = value;
				break;
			}
			case INT:
			{
				_intValue = value;
				break;
			}
			case WIT:
			{
				_wit = value;
				break;
			}
			case MEN:
			{
				_men = value;
				break;
			}
		}
	}
	
	public int getTotalUsed()
	{
		return _str + _con + _dex + _intValue + _wit + _men;
	}
	
	public void setAll(int str, int con, int dex, int intValue, int wit, int men)
	{
		_str = str;
		_con = con;
		_dex = dex;
		_intValue = intValue;
		_wit = wit;
		_men = men;
	}
	
	public void copyFrom(BaseAttributeBonusHolder other)
	{
		setAll(other.getStrBonus(), other.getConBonus(), other.getDexBonus(), other.getIntBonus(), other.getWitBonus(), other.getMenBonus());
	}
}
