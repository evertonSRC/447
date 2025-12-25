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
package handlers.voicedcommandhandlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.l2jmobius.gameserver.config.GeneralConfig;
import org.l2jmobius.gameserver.config.custom.OfflinePlayConfig;
import org.l2jmobius.gameserver.data.xml.OptionData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.actor.enums.player.PlayerAction;
import org.l2jmobius.gameserver.model.ensoul.EnsoulOption;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.holders.ItemSkillHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.options.Options;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.ConfirmDlg;
import org.l2jmobius.gameserver.network.serverpackets.CreatureSay;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.util.HtmlUtil;

/**
 * @author Mobius
 */
public class OfflinePlay implements IVoicedCommandHandler
{
	private static final int PAGE_LIMIT = 8;
	
	private static final String[] VOICED_COMMANDS =
	{
		"offlineplay",
		"skills"
	};
	
	private static final Consumer<OnPlayerLogin> ON_PLAYER_LOGIN = event ->
	{
		if (OfflinePlayConfig.ENABLE_OFFLINE_PLAY_COMMAND && !OfflinePlayConfig.OFFLINE_PLAY_LOGIN_MESSAGE.isEmpty())
		{
			event.getPlayer().sendPacket(new CreatureSay(null, ChatType.ANNOUNCEMENT, "OfflinePlay", OfflinePlayConfig.OFFLINE_PLAY_LOGIN_MESSAGE));
		}
	};
	
	public OfflinePlay()
	{
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LOGIN, ON_PLAYER_LOGIN, this));
	}
	
	@Override
	public boolean onCommand(String command, Player player, String params)
	{
		if (!OfflinePlayConfig.ENABLE_OFFLINE_PLAY_COMMAND || (player == null))
		{
			return false;
		}
		
		switch (command)
		{
			case "offlineplay":
			{
				if (OfflinePlayConfig.OFFLINE_PLAY_PREMIUM && !player.hasPremiumStatus())
				{
					player.sendPacket(new ExShowScreenMessage("This command is only available to premium players.", 5000));
					player.sendMessage("This command is only available to premium players.");
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
				
				if (!player.isAutoPlaying())
				{
					player.sendPacket(new ExShowScreenMessage("You need to enable auto play before exiting.", 5000));
					player.sendMessage("You need to enable auto play before exiting.");
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
				
				if (player.getAutoUseSettings().getAutoBuffs().isEmpty() && player.getAutoUseSettings().getAutoSkills().isEmpty())
				{
					player.sendPacket(new ExShowScreenMessage("Please use .skills to select used skills.", 5000));
					player.sendMessage("Please use .skills to select used skills.");
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
				
				if (player.isInVehicle() || player.isInsideZone(ZoneId.PEACE))
				{
					player.sendPacket(new ExShowScreenMessage("You may not log out from this location.", 5000));
					player.sendPacket(SystemMessageId.YOU_MAY_NOT_LOG_OUT_FROM_THIS_LOCATION);
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
				
				if (player.isRegisteredOnEvent())
				{
					player.sendPacket(new ExShowScreenMessage("Cannot use this command while registered on an event.", 5000));
					player.sendMessage("Cannot use this command while registered on an event.");
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
				
				player.addAction(PlayerAction.OFFLINE_PLAY);
				player.sendPacket(new ConfirmDlg("Do you wish to exit and continue auto play?"));
				break;
			}
			case "skills":
			{
				// Generate the skill list. Filter our some skills.
				List<Skill> skills = new ArrayList<>();
				final List<Skill> siegeSkills = SkillData.getInstance().getSiegeSkills(true);
				for (Skill skill : player.getAllSkills())
				{
					if (!siegeSkills.contains(skill) && !skill.isPassive() && !skill.isToggle() && !skills.contains(skill) && !OfflinePlayConfig.DISABLED_AUTO_SKILLS.contains(skill.getId()))
					{
						skills.add(skill);
					}
				}
				
				if (player.hasServitors())
				{
					for (Summon summon : player.getServitors().values())
					{
						for (Skill skill : summon.getAllSkills())
						{
							if (!skill.isPassive() && !skill.isToggle() && !skills.contains(skill) && !OfflinePlayConfig.DISABLED_AUTO_SKILLS.contains(skill.getId()))
							{
								skills.add(skill);
							}
						}
					}
				}
				
				if (player.hasPet())
				{
					for (Skill skill : player.getPet().getAllSkills())
					{
						if (!skill.isPassive() && !skill.isToggle() && !skills.contains(skill) && !OfflinePlayConfig.DISABLED_AUTO_SKILLS.contains(skill.getId()))
						{
							skills.add(skill);
						}
					}
				}
				
				// Remove item skills.
				for (Item item : player.getInventory().getPaperdollItems())
				{
					final ItemTemplate template = item.getTemplate();
					if (template.hasSkills())
					{
						for (ItemSkillHolder holder : template.getAllSkills())
						{
							final Skill skill = holder.getSkill();
							if (skill != null)
							{
								skills.remove(skill);
							}
						}
					}
					
					final Collection<EnsoulOption> specialAbilities = item.getSpecialAbilities();
					if (specialAbilities != null)
					{
						for (EnsoulOption option : specialAbilities)
						{
							if (option != null)
							{
								final Skill skill = option.getSkill();
								if (skill != null)
								{
									skills.remove(skill);
								}
							}
						}
					}
					
					final Collection<EnsoulOption> additionalSpecialAbilities = item.getAdditionalSpecialAbilities();
					if (additionalSpecialAbilities != null)
					{
						for (EnsoulOption option : additionalSpecialAbilities)
						{
							if (option != null)
							{
								final Skill skill = option.getSkill();
								if (skill != null)
								{
									skills.remove(skill);
								}
							}
						}
					}
					
					if (item.getEnchantOptions() != Item.DEFAULT_ENCHANT_OPTIONS)
					{
						for (int id : item.getEnchantOptions())
						{
							final Options options = OptionData.getInstance().getOptions(id);
							if ((options != null) && options.hasActiveSkills())
							{
								for (Skill skill : options.getActiveSkills())
								{
									if (skill != null)
									{
										skills.remove(skill);
									}
								}
							}
						}
					}
					
					if (item.isAugmented())
					{
						final Options options1 = OptionData.getInstance().getOptions(item.getAugmentation().getOption1Id());
						if ((options1 != null) && options1.hasActiveSkills())
						{
							for (Skill skill : options1.getActiveSkills())
							{
								if (skill != null)
								{
									skills.remove(skill);
								}
							}
						}
						
						final Options options2 = OptionData.getInstance().getOptions(item.getAugmentation().getOption2Id());
						if ((options2 != null) && options2.hasActiveSkills())
						{
							for (Skill skill : options2.getActiveSkills())
							{
								if (skill != null)
								{
									skills.remove(skill);
								}
							}
						}
					}
				}
				
				// Manage skill activation.
				final String[] paramArray = params == null ? new String[0] : params.split(" ");
				if (paramArray.length > 1)
				{
					final Integer skillId = Integer.parseInt(paramArray[1]);
					Skill knownSkill = player.getKnownSkill(skillId);
					if (knownSkill == null)
					{
						if (player.hasServitors())
						{
							for (Summon summon : player.getServitors().values())
							{
								knownSkill = summon.getKnownSkill(skillId);
								if (knownSkill != null)
								{
									break;
								}
							}
						}
						
						if ((knownSkill == null) && player.hasPet())
						{
							knownSkill = player.getPet().getKnownSkill(skillId);
						}
					}
					
					if (GeneralConfig.ENABLE_AUTO_SKILL && (knownSkill != null) && skills.contains(knownSkill))
					{
						if (knownSkill.hasNegativeEffect())
						{
							if (player.getAutoUseSettings().getAutoSkills().contains(skillId))
							{
								player.getAutoUseSettings().getAutoSkills().remove(skillId);
							}
							else
							{
								player.getAutoUseSettings().getAutoSkills().add(skillId);
							}
						}
						else
						{
							if (player.getAutoUseSettings().getAutoBuffs().contains(skillId))
							{
								player.getAutoUseSettings().getAutoBuffs().remove(skillId);
							}
							else
							{
								player.getAutoUseSettings().getAutoBuffs().add(skillId);
							}
						}
					}
				}
				
				// Calculate page number.
				final int max = HtmlUtil.countPageNumber(skills.size(), PAGE_LIMIT);
				int page = params == null ? 1 : Integer.parseInt(paramArray[0]);
				if (page > max)
				{
					page = max;
				}
				
				// Cut skills list up to page number.
				final StringBuilder sb = new StringBuilder();
				sb.append("<html noscrollbar><title>Select Skills</title><body>");
				skills = skills.subList(Math.max(0, (page - 1) * PAGE_LIMIT), Math.min(page * PAGE_LIMIT, skills.size()));
				if (skills.isEmpty())
				{
					sb.append("<center><br>No skills found.<br></center>");
				}
				else
				{
					// Generate skill table.
					int row = 0;
					for (Skill skill : skills)
					{
						sb.append(((row % 2) == 0 ? "<table width=\"295\" bgcolor=\"000000\"><tr>" : "<table width=\"295\"><tr>"));
						if (player.getAutoUseSettings().getAutoBuffs().contains(skill.getId()) || player.getAutoUseSettings().getAutoSkills().contains(skill.getId()))
						{
							sb.append("<td height=40 width=40><img src=\"" + skill.getIcon() + "\" width=32 height=32></td><td width=190>" + skill.getName() + "</td><td><button value=\" \" action=\"bypass voice .skills " + page + " " + skill.getId() + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
						}
						else
						{
							sb.append("<td height=40 width=40><img src=\"" + skill.getIcon() + "\" width=32 height=32></td><td width=190><font color=\"B09878\">" + skill.getName() + "</font></td><td><button value=\" \" action=\"bypass voice .skills " + page + " " + skill.getId() + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
						}
						sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=295 height=1>");
					}
					
					// Generate page footer.
					sb.append("<br><img src=\"L2UI.SquareGray\" width=295 height=1><table width=\"100%\" bgcolor=000000><tr>");
					if (page > 1)
					{
						sb.append("<td align=left width=70><a action=\"bypass voice .skills " + (page - 1) + "\"><font color=\"CDB67F\">Previous</font></a></td>");
					}
					else
					{
						sb.append("<td align=left width=70><font color=\"B09878\">Previous</font></td>");
					}
					sb.append("<td align=center width=100>Page " + page + " of " + max + "</td>");
					if (page < max)
					{
						sb.append("<td align=right width=70><a action=\"bypass voice .skills " + (page + 1) + "\"><font color=\"CDB67F\">Next</font></a></td>");
					}
					else
					{
						sb.append("<td align=right width=70><font color=\"B09878\">Next</font></td>");
					}
					sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=295 height=1>");
				}
				sb.append("</body></html>");
				
				// Send the html.
				final NpcHtmlMessage html = new NpcHtmlMessage();
				html.setHtml(sb.toString());
				player.sendPacket(html);
				break;
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getCommandList()
	{
		return VOICED_COMMANDS;
	}
}
