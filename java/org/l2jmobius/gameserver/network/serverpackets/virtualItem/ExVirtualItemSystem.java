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
package org.l2jmobius.gameserver.network.serverpackets.virtualItem;

import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.config.IllusoryEquipmentConfig;
import org.l2jmobius.gameserver.data.holders.VirtualItemHolder;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.VirtualItemData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExVirtualItemSystem extends ServerPacket
{
	private static final int PERMANENT_EVENT_REMAINING_SECONDS = Integer.MAX_VALUE;

	private final Player _player;
	private final int _type;
	private final int _selectIndexMain;
	private final int _selectIndexSub;
	private final long _selectSlot;
	private final List<VirtualItemHolder> _updateVisItemInfo;
	
	public ExVirtualItemSystem(Player player, int type, int selectIndexMain, int selectIndexSub, long selectSlot, List<VirtualItemHolder> updateVisItemInfo)
	{
		_player = player;
		_type = type;
		_selectIndexMain = selectIndexMain;
		_selectIndexSub = selectIndexSub;
		_selectSlot = selectSlot;
		_updateVisItemInfo = updateVisItemInfo;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_VIRTUALITEM_SYSTEM.writeId(this, buffer);
		final int illusoryPointsAcquired = _player.getVariables().getInt(PlayerVariables.ILLUSORY_POINTS_ACQUIRED, 0);
		final int illusoryPointsUsed = _player.getVariables().getInt(PlayerVariables.ILLUSORY_POINTS_USED, 0);
		
		// These values should be taken from a db table.
		int testIndex = 3; // tested values 1 / 2 / 3 / 4
		int testIndexSub = 4;
		int testSlot = 1; // tested values 1 / 2 / 3
		
		if (_type == 1)// XXX Read existing virtual items.
		{
			buffer.writeByte(_type); // var int cType;
			buffer.writeByte(true); // var int cResult;
			buffer.writeInt(PERMANENT_EVENT_REMAINING_SECONDS); // Event is permanent while enabled.
			buffer.writeInt(illusoryPointsAcquired); // var int nTotalGetVISPoint;
			buffer.writeInt(illusoryPointsUsed); // var int nTotalUsedVISPoint;
			buffer.writeInt(_selectIndexMain); // var int nSelectIndexMain;
			buffer.writeInt(_selectIndexSub); // var int nSelectIndexSub;
			buffer.writeLong(_selectSlot); // var int nSelectSlot;
			
			// for (VirtualItemHolder virtualItem : _updateVisItemInfo)
			// VirtualItemHolder(_nIndexMain, _nIndexSub, _nSlot, _nCostVISPoint, _nItemClass, _nEnchant);
			// virtual Item infos from xml ??
			
			buffer.writeInt(1); // equipment array size
			buffer.writeInt(testIndex); // var int nIndexMain;
			buffer.writeInt(testIndexSub); // var int nIndexSub;
			buffer.writeInt(testSlot); // var int nSlot;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getCostVISPoint()); // var int _nCostVISPoint;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getItemId()); // var int nItemClass;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getEnchant()); // var int nEnchant;
			
			buffer.writeInt(2); // equipment array size
			buffer.writeInt(testIndex); // var int nIndexMain;
			buffer.writeInt(testIndexSub); // var int nIndexSub;
			buffer.writeInt(2); // var int nSlot;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getCostVISPoint()); // var int _nCostVISPoint;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getItemId()); // var int nItemClass;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getEnchant()); // var int nEnchant;
			
			buffer.writeInt(3); // equipment array size
			buffer.writeInt(testIndex); // var int nIndexMain;
			buffer.writeInt(testIndexSub); // var int nIndexSub;
			buffer.writeInt(3); // var int nSlot;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getCostVISPoint()); // var int _nCostVISPoint;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getItemId()); // var int nItemClass;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getEnchant()); // var int nEnchant;
		}
		else if (_type == 2) // XXX Reset all.
		{
			// Reset all used points
			_player.getVariables().set(PlayerVariables.ILLUSORY_POINTS_USED, 0); // Total Illusory Points used
			buffer.writeByte(_type); // var int cType;
			buffer.writeByte(true); // var int cResult;
			buffer.writeInt(PERMANENT_EVENT_REMAINING_SECONDS); // Event is permanent while enabled.
			buffer.writeInt(illusoryPointsAcquired); // var int nTotalGetVISPoint;
			buffer.writeInt(illusoryPointsUsed); // var int nTotalUsedVISPoint;
			buffer.writeInt(IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_POINTS_LIMIT); // max available points default 600
			
			buffer.writeInt(3); // equipment array size
			buffer.writeInt(testIndex); // var int nIndexMain;
			buffer.writeInt(testIndexSub); // var int nIndexSub;
			buffer.writeInt(3); // var int nSlot;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getCostVISPoint()); // var int _nCostVISPoint;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getItemId()); // var int nItemClass;
			buffer.writeInt(VirtualItemData.getInstance().getVirtualItem(testIndex).getEnchant()); // var int nEnchant;
		}
		
		else if (_type == 3) // XXX Update virtual items
		{
			buffer.writeByte(_type); // var int cType;
			buffer.writeByte(true); // var int cResult;
			buffer.writeInt(PERMANENT_EVENT_REMAINING_SECONDS); // Event is permanent while enabled.
			buffer.writeInt(illusoryPointsAcquired); // var int nTotalGetVISPoint;
			buffer.writeInt(illusoryPointsUsed); // var int nTotalUsedVISPoint;
			buffer.writeInt(_selectIndexMain); // var int nSelectIndexMain;
			buffer.writeInt(_selectIndexSub); // var int nSelectIndexSub;
			buffer.writeLong(_selectSlot); // var int nSelectSlot;
			for (VirtualItemHolder virtualItem : _updateVisItemInfo)
			{
				// Item info
				buffer.writeInt(virtualItem.getIndexMain()); // var int nIndexMain;
				buffer.writeInt(virtualItem.getIndexSub()); // var int nIndexSub;
				buffer.writeLong(virtualItem.getSlot()); // var int nSlot;
				buffer.writeInt(virtualItem.getCostVISPoint()); // var int nCostVISPoint;
				buffer.writeInt(virtualItem.getItemId()); // var int nItemClass;
				buffer.writeInt(virtualItem.getEnchant()); // var int nEnchant;
				
				// TODO: Add a item/skill check
				if (virtualItem.getItemId() != 0)
				{
					_player.sendMessage("Item Class: " + virtualItem.getItemId());
					
					// Debug.
					if (IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DEBUG_ENABLED)
					{
						_player.sendMessage("ExVirtualItemSystem ----------------------------");
						_player.sendMessage("cType:" + _type);
						_player.sendMessage("nSelectIndexMain:" + _selectIndexMain);
						_player.sendMessage("nSelectIndexSub:" + _selectIndexSub);
						_player.sendMessage("nIndexMain:" + virtualItem.getIndexMain());
						_player.sendMessage("nIndexSub:" + virtualItem.getIndexSub());
						_player.sendMessage("nSlot:" + virtualItem.getSlot());
						_player.sendMessage("Item/Skill Cost:" + virtualItem.getCostVISPoint());
						_player.sendMessage("Item/Skill Id:" + virtualItem.getItemId());
						_player.sendMessage("Item/Skill Enchant:" + virtualItem.getEnchant());
						_player.sendMessage("------------------------------------------------");
					}
					
					// XXX Skills have always slot 123.
					if ((virtualItem.getSlot() == 1) || (virtualItem.getSlot() == 2) || (virtualItem.getSlot() == 3))
					{
						// Skill
						final Skill skill = SkillData.getInstance().getSkill(virtualItem.getItemId(), virtualItem.getEnchant());
						if (skill != null)
						{
							_player.addSkill(skill, true);
							_player.sendSkillList();
						}
					}
					
					// armors/weapons slot 0
					// agathions/brooch jewels slot 4
					// talismans slot 5
					else
					{
						// Item
						final Item visItem = new Item(virtualItem.getItemId());
						visItem.setCount(1);
						visItem.setEnchantLevel(virtualItem.getEnchant());
						visItem.setOwnerId(_player.getObjectId());
						visItem.setVirtual(true);
						_player.getInventory().equipItem(visItem);
						
						// Send packets
						final InventoryUpdate iu = new InventoryUpdate();
						iu.addModifiedItem(visItem);
						_player.sendInventoryUpdate(iu);
						_player.broadcastInfo();
					}
					
					// Addd used points to var
					_player.getVariables().set(PlayerVariables.ILLUSORY_POINTS_USED, _player.getVariables().getInt(PlayerVariables.ILLUSORY_POINTS_USED, 0) + virtualItem.getCostVISPoint()); // Total Illusory Points used
				}
			}
		}
		
		_player.sendPacket(new ExVirtualItemSystemBaseInfo(_player));
		_player.sendPacket(new ExVirtualItemSystemPointInfo(_player, IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_POINTS_LIMIT - _player.getVariables().getInt(PlayerVariables.ILLUSORY_POINTS_USED, 0)));
	}
}
