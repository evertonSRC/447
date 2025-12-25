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
package quests;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.script.newquestdata.QuestCondType;

import quests.Q10031_ProphecyMachineRestoration.Q10031_ProphecyMachineRestoration;
import quests.Q10032_ToGereth.Q10032_ToGereth;
import quests.Q10033_ProphecyInterpretation.Q10033_ProphecyInterpretation;
import quests.Q10131_ProphecyMachineRestoration.Q10131_ProphecyMachineRestoration;
import quests.Q10132_ToGereth.Q10132_ToGereth;
import quests.Q10133_ProphecyInterpretation.Q10133_ProphecyInterpretation;
import quests.Q10231_ProphecyMachineRestoration.Q10231_ProphecyMachineRestoration;
import quests.Q10232_ToGereth.Q10232_ToGereth;
import quests.Q10233_ProphecyInterpretation.Q10233_ProphecyInterpretation;
import quests.Q10331_ProphecyMachineRestoration.Q10331_ProphecyMachineRestoration;
import quests.Q10332_ToGereth.Q10332_ToGereth;
import quests.Q10333_ProphecyInterpretation.Q10333_ProphecyInterpretation;

/**
 * @author Mobius
 */
public class ProphecyFragment extends Script
{
	private ProphecyFragment()
	{
		addItemTalkId(39540); // Prophecy Fragment
		addItemTalkId(39539); // Prophecy Fragment
	}
	
	@Override
	public String onItemTalk(Item item, Player player)
	{
		QuestState questState = player.getQuestState(Q10031_ProphecyMachineRestoration.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10032_ToGereth.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10033_ProphecyInterpretation.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10131_ProphecyMachineRestoration.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10132_ToGereth.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10133_ProphecyInterpretation.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10231_ProphecyMachineRestoration.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10232_ToGereth.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10233_ProphecyInterpretation.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10331_ProphecyMachineRestoration.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10332_ToGereth.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10333_ProphecyInterpretation.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10131_ProphecyMachineRestoration.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10132_ToGereth.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10133_ProphecyInterpretation.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10231_ProphecyMachineRestoration.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10232_ToGereth.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10233_ProphecyInterpretation.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10331_ProphecyMachineRestoration.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10332_ToGereth.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		questState = player.getQuestState(Q10333_ProphecyInterpretation.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			questState.setCond(QuestCondType.DONE);
			sendEndDialog(player);
			return null;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new ProphecyFragment();
	}
}
