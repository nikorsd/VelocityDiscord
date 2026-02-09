package ooo.foooooooooooo.velocitydiscord.discord.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import ooo.foooooooooooo.velocitydiscord.discord.Discord;
import ooo.foooooooooooo.velocitydiscord.discord.UserLinkData;

import java.util.Objects;

public class LinkCommand implements ICommand {
  public static final String COMMAND_NAME = "link";

  private final Discord discord;

  public LinkCommand(Discord discord) {
    this.discord = discord;
  }

  @Override
  public void handle(SlashCommandInteraction interaction) {
    // Check bot's permissions
    if (!Objects.requireNonNull(interaction.getMember()).getGuild().getSelfMember().hasPermission(Permission.NICKNAME_MANAGE)) {
      interaction.reply("I do not have permission to change your nickname. Please check my role permissions.").setEphemeral(true).queue();
      return;
    }

    OptionMapping codeParam = interaction.getOption("code");

    if (codeParam == null) {
      interaction.reply("You must provide a code to link your account.").setEphemeral(true).queue();
      return;
    }

    String code = codeParam.getAsString();

    if (discord.getPendingLinkCodes().get(code) != null) {
      System.out.println(interaction.getMember().getId() + " " + discord.getPendingLinkCodes().get(code));
      Objects.requireNonNull(interaction.getGuild()).modifyNickname(interaction.getMember(), discord.getPendingLinkCodes().get(code)).queue();

      try {
        UserLinkData.save(interaction.getMember().getId(), discord.getPendingLinkCodes().get(code));
      }
      catch (Exception e) {System.out.println("Error saving user link data");}

      System.out.println("Linked Discord user " + interaction.getUser().getAsTag() + " to Minecraft username: " + discord.getPendingLinkCodes().get(code));
      interaction.reply("Successfully linked your account with the code: " + code).setEphemeral(true).queue();
      discord.getPendingLinkCodes().remove(code);
    } else {
      interaction.reply("Invalid code. Please make sure you provide a valid code.").setEphemeral(true).queue();
      System.out.println(discord.getPendingLinkCodes());
      System.out.println(discord.getPendingLinkCodes().get(code));
    }
  }

  @Override
  public String description() {
    return "Link your account with a code.";
  }
}
