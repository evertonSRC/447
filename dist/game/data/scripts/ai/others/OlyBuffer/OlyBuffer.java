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
package ai.others.OlyBuffer;

import org.l2jmobius.gameserver.config.OlympiadConfig;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.CommonSkill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;

/**
 * Olympiad Buffer AI.
 * @author St3eT, Mobius
 */
public class OlyBuffer extends Script
{
	// NPC
	private static final int OLYMPIAD_BUFFER = 36402;
	
	// Skills
	private static final CommonSkill[] BUFFS =
	{
		CommonSkill.OLYMPIAD_HARMONY,
		CommonSkill.OLYMPIAD_MELODY,
	};
	
	private OlyBuffer()
	{
		if (OlympiadConfig.OLYMPIAD_ENABLED)
		{
			addStartNpc(OLYMPIAD_BUFFER);
			addFirstTalkId(OLYMPIAD_BUFFER);
			addTalkId(OLYMPIAD_BUFFER);
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		if (npc.isScriptValue(0))
		{
			htmltext = "olympiad_master001.htm";
		}
		
		return htmltext;
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "buff":
			{
				applyBuffs(npc, player);
				break;
			}
		}
		
		npc.setScriptValue(1);
		getTimers().addTimer("DELETE_ME", 5000, evnt -> npc.deleteMe());
		return "olympiad_master003.htm";
	}
	
	private void applyBuffs(Npc npc, Player player)
	{
		for (CommonSkill holder : BUFFS)
		{
			SkillCaster.triggerCast(npc, player, holder.getSkill());
		}
	}
	
	public static void main(String[] args)
	{
		new OlyBuffer();
	}
}
