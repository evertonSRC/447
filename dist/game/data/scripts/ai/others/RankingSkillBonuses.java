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
package ai.others;

import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.managers.RankManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.enums.SkillFinishType;

/**
 * @author Mobius
 */
public class RankingSkillBonuses extends Script
{
	// Skills
	private static final Skill SERVER_LEVEL_RANKING_1ST_CLASS = SkillData.getInstance().getSkill(32874, 1);
	private static final Skill SERVER_LEVEL_RANKING_2ND_CLASS = SkillData.getInstance().getSkill(32875, 1);
	private static final Skill SERVER_LEVEL_RANKING_3RD_CLASS = SkillData.getInstance().getSkill(32876, 1);
	private static final Skill HUMAN_LEVEL_RANKING_1ST_CLASS = SkillData.getInstance().getSkill(32877, 1);
	private static final Skill ELF_LEVEL_RANKING_1ST_CLASS = SkillData.getInstance().getSkill(32878, 1);
	private static final Skill DARK_ELF_LEVEL_RANKING_1ST_CLASS = SkillData.getInstance().getSkill(32879, 1);
	private static final Skill ORC_LEVEL_RANKING_1ST_CLASS = SkillData.getInstance().getSkill(32880, 1);
	private static final Skill DWARF_LEVEL_RANKING_1ST_CLASS = SkillData.getInstance().getSkill(32881, 1);
	private static final Skill KAMAEL_LEVEL_RANKING_1ST_CLASS = SkillData.getInstance().getSkill(32882, 1);
	private static final Skill ERTHEIA_LEVEL_RANKING_1ST_CLASS = SkillData.getInstance().getSkill(32883, 1);
	private static final Skill SERVER_RANKING_BENEFIT_1 = SkillData.getInstance().getSkill(32884, 1);
	private static final Skill SERVER_RANKING_BENEFIT_2 = SkillData.getInstance().getSkill(32885, 1);
	private static final Skill SERVER_RANKING_BENEFIT_3 = SkillData.getInstance().getSkill(32886, 1);
	private static final Skill RACE_RANKING_BENEFIT = SkillData.getInstance().getSkill(32887, 1);
	private static final Skill CLASS_RANKING_BENEFIT = SkillData.getInstance().getSkill(33134, 1);
	private static final Skill SIGEL_RANK_BENEFIT = SkillData.getInstance().getSkill(33126, 1);
	private static final Skill WARRIOR_RANK_BENEFIT = SkillData.getInstance().getSkill(33127, 1);
	private static final Skill ROGUE_RANK_BENEFIT = SkillData.getInstance().getSkill(33128, 1);
	private static final Skill ARCHER_RANK_BENEFIT = SkillData.getInstance().getSkill(33129, 1);
	private static final Skill ISS_RANK_BENEFIT = SkillData.getInstance().getSkill(33130, 1);
	private static final Skill FEOH_RANK_BENEFIT = SkillData.getInstance().getSkill(33131, 1);
	private static final Skill SUMMONER_RANK_BENEFIT = SkillData.getInstance().getSkill(33132, 1);
	private static final Skill HEALER_RANK_BENEFIT = SkillData.getInstance().getSkill(33133, 1);
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		// Remove existing effects and skills.
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, SERVER_LEVEL_RANKING_1ST_CLASS);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, SERVER_LEVEL_RANKING_2ND_CLASS);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, SERVER_LEVEL_RANKING_3RD_CLASS);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, HUMAN_LEVEL_RANKING_1ST_CLASS);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, ELF_LEVEL_RANKING_1ST_CLASS);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, DARK_ELF_LEVEL_RANKING_1ST_CLASS);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, ORC_LEVEL_RANKING_1ST_CLASS);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, DWARF_LEVEL_RANKING_1ST_CLASS);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, KAMAEL_LEVEL_RANKING_1ST_CLASS);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, ERTHEIA_LEVEL_RANKING_1ST_CLASS);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, SIGEL_RANK_BENEFIT);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, WARRIOR_RANK_BENEFIT);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, ROGUE_RANK_BENEFIT);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, ARCHER_RANK_BENEFIT);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, ISS_RANK_BENEFIT);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, FEOH_RANK_BENEFIT);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, SUMMONER_RANK_BENEFIT);
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, HEALER_RANK_BENEFIT);
		player.removeSkill(SERVER_RANKING_BENEFIT_1);
		player.removeSkill(SERVER_RANKING_BENEFIT_2);
		player.removeSkill(SERVER_RANKING_BENEFIT_3);
		player.removeSkill(RACE_RANKING_BENEFIT);
		player.removeSkill(CLASS_RANKING_BENEFIT);
		
		// Add global rank skills.
		int rank = RankManager.getInstance().getPlayerGlobalRank(player);
		if (rank > 0)
		{
			if (rank <= 10)
			{
				SERVER_LEVEL_RANKING_1ST_CLASS.applyEffects(player, player);
				player.addSkill(SERVER_RANKING_BENEFIT_1, false);
				player.addSkill(SERVER_RANKING_BENEFIT_2, false);
				player.addSkill(SERVER_RANKING_BENEFIT_3, false);
			}
			else if (rank <= 50)
			{
				SERVER_LEVEL_RANKING_2ND_CLASS.applyEffects(player, player);
				player.addSkill(SERVER_RANKING_BENEFIT_1, false);
				player.addSkill(SERVER_RANKING_BENEFIT_2, false);
			}
			else if (rank <= 100)
			{
				SERVER_LEVEL_RANKING_3RD_CLASS.applyEffects(player, player);
				player.addSkill(SERVER_RANKING_BENEFIT_1, false);
			}
		}
		
		// Apply race rank effects.
		final int raceRank = RankManager.getInstance().getPlayerRaceRank(player);
		if ((raceRank > 0) && (raceRank <= 10))
		{
			switch (player.getRace())
			{
				case HUMAN:
				{
					HUMAN_LEVEL_RANKING_1ST_CLASS.applyEffects(player, player);
					break;
				}
				case ELF:
				{
					ELF_LEVEL_RANKING_1ST_CLASS.applyEffects(player, player);
					break;
				}
				case DARK_ELF:
				{
					DARK_ELF_LEVEL_RANKING_1ST_CLASS.applyEffects(player, player);
					break;
				}
				case ORC:
				{
					ORC_LEVEL_RANKING_1ST_CLASS.applyEffects(player, player);
					break;
				}
				case DWARF:
				{
					DWARF_LEVEL_RANKING_1ST_CLASS.applyEffects(player, player);
					break;
				}
				case KAMAEL:
				{
					KAMAEL_LEVEL_RANKING_1ST_CLASS.applyEffects(player, player);
					break;
				}
				case ERTHEIA:
				{
					ERTHEIA_LEVEL_RANKING_1ST_CLASS.applyEffects(player, player);
					break;
				}
			}
			
			player.addSkill(RACE_RANKING_BENEFIT, false);
		}
		
		// Apply class rank effects.
		final int classRank = RankManager.getInstance().getPlayerClassRank(player);
		if ((classRank > 0) && (classRank <= 10))
		{
			if (((player.getBaseClass() >= 148) && (player.getBaseClass() <= 151)) || (player.getBaseClass() == 216))
			{
				SIGEL_RANK_BENEFIT.applyEffects(player, player);
			}
			else if (((player.getBaseClass() >= 152) && (player.getBaseClass() <= 157)) || (player.getBaseClass() == 188))
			{
				WARRIOR_RANK_BENEFIT.applyEffects(player, player);
			}
			else if ((player.getBaseClass() >= 158) && (player.getBaseClass() <= 161))
			{
				ROGUE_RANK_BENEFIT.applyEffects(player, player);
			}
			else if ((player.getBaseClass() >= 162) && (player.getBaseClass() <= 165))
			{
				ARCHER_RANK_BENEFIT.applyEffects(player, player);
			}
			else if ((player.getBaseClass() >= 171) && (player.getBaseClass() <= 175))
			{
				ISS_RANK_BENEFIT.applyEffects(player, player);
			}
			else if (((player.getBaseClass() >= 166) && (player.getBaseClass() <= 170)) || (player.getBaseClass() == 189) || (player.getBaseClass() == 235))
			{
				FEOH_RANK_BENEFIT.applyEffects(player, player);
			}
			else if ((player.getBaseClass() >= 176) && (player.getBaseClass() <= 178))
			{
				SUMMONER_RANK_BENEFIT.applyEffects(player, player);
			}
			else if ((player.getBaseClass() >= 179) && (player.getBaseClass() <= 181))
			{
				HEALER_RANK_BENEFIT.applyEffects(player, player);
			}
			
			player.addSkill(CLASS_RANKING_BENEFIT, false);
		}
	}
	
	public static void main(String[] args)
	{
		new RankingSkillBonuses();
	}
}
