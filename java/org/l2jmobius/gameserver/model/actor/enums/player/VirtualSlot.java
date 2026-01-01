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
package org.l2jmobius.gameserver.model.actor.enums.player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.l2jmobius.gameserver.data.holders.VirtualItemEntry;
import org.l2jmobius.gameserver.model.item.enums.BodyPart;

/**
 * @author Mobius
 */
public enum VirtualSlot
{
	R_HAND(1, "r_hand_virtual", BodyPart.R_HAND),
	L_HAND(2, "l_hand_virtual", BodyPart.L_HAND),
	HEAD(3, "head_virtual", BodyPart.HEAD),
	CHEST(4, "chest_virtual", BodyPart.CHEST),
	LEGS(5, "legs_virtual", BodyPart.LEGS),
	GLOVES(6, "gloves_virtual", BodyPart.GLOVES),
	FEET(7, "feet_virtual", BodyPart.FEET),
	BACK(8, "back_virtual", BodyPart.BACK),
	HAIR(9, "hair_virtual", BodyPart.HAIR),
	HAIR2(10, "hair2_virtual", BodyPart.HAIR2),
	FACE(11, "face_virtual", null),
	UNDERWEAR(12, "underwear_virtual", BodyPart.UNDERWEAR),
	BELT(13, "belt_virtual", BodyPart.BELT),
	BROOCH(14, "brooch_virtual", BodyPart.BROOCH),
	AGATHION(15, "agathion_virtual", BodyPart.AGATHION),
	TALISMAN(16, "talisman_virtual", BodyPart.DECO),
	BRACELET(17, "bracelet_virtual", BodyPart.R_BRACELET),
	RING1(18, "ring1_virtual", BodyPart.R_FINGER),
	RING2(19, "ring2_virtual", BodyPart.L_FINGER),
	EAR1(20, "ear1_virtual", BodyPart.R_EAR),
	EAR2(21, "ear2_virtual", BodyPart.L_EAR),
	NECK(22, "neck_virtual", BodyPart.NECK);
	
	private final int _id;
	private final String _alias;
	private final BodyPart _bodyPart;
	
	private static final Map<Integer, VirtualSlot> BY_ID = new HashMap<>();
	private static final Map<String, VirtualSlot> BY_ALIAS = new HashMap<>();
	private static final Map<Long, VirtualSlot> BY_CLIENT_SLOT = new HashMap<>();
	static
	{
		for (VirtualSlot slot : values())
		{
			BY_ID.put(slot._id, slot);
			BY_ALIAS.put(slot._alias, slot);
			if (slot._bodyPart != null)
			{
				BY_CLIENT_SLOT.put(slot._bodyPart.getMask(), slot);
			}
		}
	}
	
	VirtualSlot(int id, String alias, BodyPart bodyPart)
	{
		_id = id;
		_alias = alias;
		_bodyPart = bodyPart;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public String getAlias()
	{
		return _alias;
	}

	public long getClientSlotId()
	{
		return (_bodyPart != null) ? _bodyPart.getMask() : _id;
	}
	
	public static VirtualSlot fromId(int id)
	{
		return BY_ID.get(id);
	}
	
	public static VirtualSlot fromAlias(String alias)
	{
		if (alias == null)
		{
			return null;
		}
		
		return BY_ALIAS.get(alias.toLowerCase(Locale.ROOT));
	}
	
	public static VirtualSlot fromClientSlot(long slotIdClient)
	{
		if (slotIdClient <= 0)
		{
			return null;
		}
		
		if (slotIdClient <= Integer.MAX_VALUE)
		{
			final VirtualSlot slotById = fromId((int) slotIdClient);
			if (slotById != null)
			{
				return slotById;
			}
		}
		
		return BY_CLIENT_SLOT.get(slotIdClient);
	}
	
	public static VirtualSlot parseSlot(String slotValue)
	{
		if ((slotValue == null) || slotValue.isEmpty())
		{
			return null;
		}
		
		final String normalized = slotValue.trim();
		try
		{
			return fromClientSlot(Long.parseLong(normalized));
		}
		catch (NumberFormatException e)
		{
			return fromAlias(normalized);
		}
	}
	
	public static VirtualSlot resolve(long slotRequested, VirtualItemEntry entry)
	{
		VirtualSlot slot = fromClientSlot(slotRequested);
		if ((slot == null) && (entry != null))
		{
			if (entry.getVirtualSlotId() > 0)
			{
				slot = fromId(entry.getVirtualSlotId());
			}
			
			if ((slot == null) && (entry.getSlotAlias() != null))
			{
				slot = fromAlias(entry.getSlotAlias());
			}
			
			if ((slot == null) && (entry.getSlotIdClient() > 0))
			{
				slot = fromClientSlot(entry.getSlotIdClient());
			}
		}
		
		return slot;
	}
}
