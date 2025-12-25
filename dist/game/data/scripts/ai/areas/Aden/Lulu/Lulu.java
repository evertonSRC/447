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
package ai.areas.Aden.Lulu;

import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerDlgAnswer;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ConfirmDlg;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Index
 */
public class Lulu extends Script
{
	// NPCs
	private static final int LULU = 34545;
	private static final int WAEL = 34546;
	private static final int EILEEN = 34547;
	
	// Skills
	private static final SkillHolder LULU_LUCK_LV1 = new SkillHolder(32966, 1);
	private static final SkillHolder LULU_LUCK_LV2 = new SkillHolder(32966, 2);
	
	// Misc
	private static final ConfirmDlg CONFIRM_DIALOG = new ConfirmDlg(SystemMessageId.I_CAN_GIVE_YOU_A_GOOD_LUCK_BUFF_WILL_YOU_ACCEPT_IT_IT_WILL_COST_YOU_7_000_000_ADENA);
	private static final int ADENA_COST = 7000000;
	private static final int BUFF_CHANCE = 30;
	private static Npc _luluNpc;
	
	private Lulu()
	{
		addStartNpc(LULU);
		addFirstTalkId(LULU, WAEL, EILEEN);
		addTalkId(LULU, WAEL, EILEEN);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equals("LULU_BLESSING") && (npc != null) && (npc.getId() == LULU))
		{
			if ((_luluNpc != null) && _luluNpc.isDead())
			{
				_luluNpc = null;
			}
			
			if ((_luluNpc == null) && !npc.isDead())
			{
				_luluNpc = npc;
			}
			
			player.sendPacket(CONFIRM_DIALOG);
		}
		
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final int npcId = npc.getId();
		if (npcId == LULU)
		{
			final String htmlText = getHtm(player, "34545.html");
			player.sendPacket(new NpcHtmlMessage(npc.getObjectId(), 0, htmlText, 1));
			return null;
		}
		
		return npcId + ".html";
	}
	
	@RegisterEvent(EventType.ON_PLAYER_DLG_ANSWER)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerDlgAnswer(OnPlayerDlgAnswer event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (event.getMessageId() != SystemMessageId.I_CAN_GIVE_YOU_A_GOOD_LUCK_BUFF_WILL_YOU_ACCEPT_IT_IT_WILL_COST_YOU_7_000_000_ADENA.getId())
		{
			return;
		}
		
		if (event.getAnswer() != 1)
		{
			return;
		}
		
		if (_luluNpc == null)
		{
			return;
		}
		
		if (!World.getInstance().getVisibleObjects(player, Npc.class).contains(_luluNpc))
		{
			return;
		}
		
		if (player.calculateDistance3D(_luluNpc) > Npc.INTERACTION_DISTANCE)
		{
			player.sendPacket(SystemMessageId.YOU_ARE_TOO_FAR_FROM_THE_NPC_FOR_THAT_TO_WORK);
			return;
		}
		
		if (player.reduceAdena(ItemProcessType.FEE, ADENA_COST, _luluNpc, true))
		{
			SkillCaster.triggerCast(_luluNpc, player, getRandom(100) < BUFF_CHANCE ? LULU_LUCK_LV2.getSkill() : LULU_LUCK_LV1.getSkill());
		}
	}
	
	public static void main(String[] args)
	{
		new Lulu();
	}
}
