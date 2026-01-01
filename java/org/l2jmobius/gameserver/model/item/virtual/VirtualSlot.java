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
package org.l2jmobius.gameserver.model.item.virtual;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.commons.util.StringUtil;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;

/**
 * Virtual slots used by the client UI for virtual items (paperdoll parallel).
 * Each slot has a dedicated virtual id to avoid collisions with normal slots.
 */
public enum VirtualSlot
{
	UNDER(Inventory.PAPERDOLL_UNDER, "under_virtual", "underwear", "under"),
	R_EAR(Inventory.PAPERDOLL_REAR, "r_ear_virtual", "rear"),
	L_EAR(Inventory.PAPERDOLL_LEAR, "l_ear_virtual", "lear"),
	NECK(Inventory.PAPERDOLL_NECK, "neck_virtual"),
	R_FINGER(Inventory.PAPERDOLL_RFINGER, "r_finger_virtual", "rfinger"),
	L_FINGER(Inventory.PAPERDOLL_LFINGER, "l_finger_virtual", "lfinger"),
	HEAD(Inventory.PAPERDOLL_HEAD, "head_virtual"),
	R_HAND(Inventory.PAPERDOLL_RHAND, "r_hand_virtual", "rhand"),
	L_HAND(Inventory.PAPERDOLL_LHAND, "l_hand_virtual", "lhand"),
	GLOVES(Inventory.PAPERDOLL_GLOVES, "gloves_virtual"),
	CHEST(Inventory.PAPERDOLL_CHEST, "chest_virtual"),
	LEGS(Inventory.PAPERDOLL_LEGS, "legs_virtual"),
	FEET(Inventory.PAPERDOLL_FEET, "feet_virtual"),
	BACK(Inventory.PAPERDOLL_CLOAK, "back_virtual", "cloak"),
	HAIR(Inventory.PAPERDOLL_HAIR, "hair_virtual"),
	HAIR2(Inventory.PAPERDOLL_HAIR2, "hair2_virtual"),
	R_BRACELET(Inventory.PAPERDOLL_RBRACELET, "r_bracelet_virtual", "rbracelet"),
	L_BRACELET(Inventory.PAPERDOLL_LBRACELET, "l_bracelet_virtual", "lbracelet"),
	AGATHION1(Inventory.PAPERDOLL_AGATHION1, "agathion1_virtual"),
	AGATHION2(Inventory.PAPERDOLL_AGATHION2, "agathion2_virtual"),
	AGATHION3(Inventory.PAPERDOLL_AGATHION3, "agathion3_virtual"),
	AGATHION4(Inventory.PAPERDOLL_AGATHION4, "agathion4_virtual"),
	AGATHION5(Inventory.PAPERDOLL_AGATHION5, "agathion5_virtual"),
	DECO1(Inventory.PAPERDOLL_DECO1, "deco1_virtual", "talisman1_virtual"),
	DECO2(Inventory.PAPERDOLL_DECO2, "deco2_virtual", "talisman2_virtual"),
	DECO3(Inventory.PAPERDOLL_DECO3, "deco3_virtual", "talisman3_virtual"),
	DECO4(Inventory.PAPERDOLL_DECO4, "deco4_virtual", "talisman4_virtual"),
	DECO5(Inventory.PAPERDOLL_DECO5, "deco5_virtual", "talisman5_virtual"),
	DECO6(Inventory.PAPERDOLL_DECO6, "deco6_virtual", "talisman6_virtual"),
	BELT(Inventory.PAPERDOLL_BELT, "belt_virtual"),
	BROOCH(Inventory.PAPERDOLL_BROOCH, "brooch_virtual"),
	BROOCH_JEWEL1(Inventory.PAPERDOLL_BROOCH_JEWEL1, "brooch_jewel1_virtual"),
	BROOCH_JEWEL2(Inventory.PAPERDOLL_BROOCH_JEWEL2, "brooch_jewel2_virtual"),
	BROOCH_JEWEL3(Inventory.PAPERDOLL_BROOCH_JEWEL3, "brooch_jewel3_virtual"),
	BROOCH_JEWEL4(Inventory.PAPERDOLL_BROOCH_JEWEL4, "brooch_jewel4_virtual"),
	BROOCH_JEWEL5(Inventory.PAPERDOLL_BROOCH_JEWEL5, "brooch_jewel5_virtual"),
	BROOCH_JEWEL6(Inventory.PAPERDOLL_BROOCH_JEWEL6, "brooch_jewel6_virtual"),
	ARTIFACT_BOOK(Inventory.PAPERDOLL_ARTIFACT_BOOK, "artifact_book_virtual", "artifactbook_virtual"),
	ARTIFACT1(Inventory.PAPERDOLL_ARTIFACT1, "artifact1_virtual"),
	ARTIFACT2(Inventory.PAPERDOLL_ARTIFACT2, "artifact2_virtual"),
	ARTIFACT3(Inventory.PAPERDOLL_ARTIFACT3, "artifact3_virtual"),
	ARTIFACT4(Inventory.PAPERDOLL_ARTIFACT4, "artifact4_virtual"),
	ARTIFACT5(Inventory.PAPERDOLL_ARTIFACT5, "artifact5_virtual"),
	ARTIFACT6(Inventory.PAPERDOLL_ARTIFACT6, "artifact6_virtual"),
	ARTIFACT7(Inventory.PAPERDOLL_ARTIFACT7, "artifact7_virtual"),
	ARTIFACT8(Inventory.PAPERDOLL_ARTIFACT8, "artifact8_virtual"),
	ARTIFACT9(Inventory.PAPERDOLL_ARTIFACT9, "artifact9_virtual"),
	ARTIFACT10(Inventory.PAPERDOLL_ARTIFACT10, "artifact10_virtual"),
	ARTIFACT11(Inventory.PAPERDOLL_ARTIFACT11, "artifact11_virtual"),
	ARTIFACT12(Inventory.PAPERDOLL_ARTIFACT12, "artifact12_virtual"),
	ARTIFACT13(Inventory.PAPERDOLL_ARTIFACT13, "artifact13_virtual"),
	ARTIFACT14(Inventory.PAPERDOLL_ARTIFACT14, "artifact14_virtual"),
	ARTIFACT15(Inventory.PAPERDOLL_ARTIFACT15, "artifact15_virtual"),
	ARTIFACT16(Inventory.PAPERDOLL_ARTIFACT16, "artifact16_virtual"),
	ARTIFACT17(Inventory.PAPERDOLL_ARTIFACT17, "artifact17_virtual"),
	ARTIFACT18(Inventory.PAPERDOLL_ARTIFACT18, "artifact18_virtual"),
	ARTIFACT19(Inventory.PAPERDOLL_ARTIFACT19, "artifact19_virtual"),
	ARTIFACT20(Inventory.PAPERDOLL_ARTIFACT20, "artifact20_virtual"),
	ARTIFACT21(Inventory.PAPERDOLL_ARTIFACT21, "artifact21_virtual");
	
	private static final int VIRTUAL_SLOT_OFFSET = 1000;
	private static final Map<Integer, VirtualSlot> BY_CLIENT_SLOT = new HashMap<>();
	private static final Map<String, VirtualSlot> BY_NAME = new HashMap<>();
	
	static
	{
		for (VirtualSlot slot : values())
		{
			BY_CLIENT_SLOT.put(slot._clientSlotId, slot);
			for (String name : slot._clientSlotNames)
			{
				registerName(name, slot);
			}
		}
	}
	
	private final int _clientSlotId;
	private final int _virtualSlotId;
	private final String _clientSlotName;
	private final String[] _clientSlotNames;
	
	private VirtualSlot(int clientSlotId, String clientSlotName, String... aliases)
	{
		_clientSlotId = clientSlotId;
		_virtualSlotId = VIRTUAL_SLOT_OFFSET + clientSlotId;
		_clientSlotName = clientSlotName;
		_clientSlotNames = new String[aliases.length + 2];
		_clientSlotNames[0] = clientSlotName;
		_clientSlotNames[1] = stripVirtualSuffix(clientSlotName);
		System.arraycopy(aliases, 0, _clientSlotNames, 2, aliases.length);
	}
	
	public int getVirtualSlotId()
	{
		return _virtualSlotId;
	}
	
	public int getClientSlotId()
	{
		return _clientSlotId;
	}
	
	public String getClientSlotName()
	{
		return _clientSlotName;
	}
	
	public static VirtualSlot fromClientSlot(int clientSlot)
	{
		return BY_CLIENT_SLOT.get(clientSlot);
	}
	
	public static VirtualSlot fromClientSlot(String clientSlot)
	{
		if (clientSlot == null)
		{
			return null;
		}
		
		if (StringUtil.isNumeric(clientSlot))
		{
			return fromClientSlot(Integer.parseInt(clientSlot));
		}
		
		final String normalized = normalizeName(clientSlot);
		if (normalized.isEmpty())
		{
			return null;
		}
		
		VirtualSlot slot = BY_NAME.get(normalized);
		if (slot == null)
		{
			slot = BY_NAME.get(normalized.replace("_", ""));
		}
		
		return slot;
	}
	
	private static void registerName(String name, VirtualSlot slot)
	{
		final String normalized = normalizeName(name);
		if (!normalized.isEmpty())
		{
			BY_NAME.put(normalized, slot);
			BY_NAME.put(normalized.replace("_", ""), slot);
		}
	}
	
	private static String normalizeName(String name)
	{
		if (name == null)
		{
			return "";
		}
		
		String normalized = name.trim().toLowerCase();
		if (normalized.endsWith("_virtual"))
		{
			normalized = stripVirtualSuffix(normalized);
		}
		normalized = normalized.replace('-', '_');
		return normalized;
	}
	
	private static String stripVirtualSuffix(String name)
	{
		if ((name != null) && name.endsWith("_virtual"))
		{
			return name.substring(0, name.length() - "_virtual".length());
		}
		return name;
	}
}
