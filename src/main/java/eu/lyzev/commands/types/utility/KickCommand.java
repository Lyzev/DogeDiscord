package eu.lyzev.commands.types.utility;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class KickCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());

        EmbedBuilder eb = DogeAPI.createEmbed();
        if (member.hasPermission(Permission.KICK_MEMBERS)) {
            eb.setTitle("Doge Kick");

            if (message.getContentDisplay().split(" ").length == 2 && !message.getMentionedMembers().isEmpty()) {
                eb.setDescription(message.getMentionedMembers().get(0).getAsMention() + " was kicked by " + member.getAsMention() + "!");
                channel.getGuild().kick(message.getMentionedMembers().get(0)).queue();
            } else
                eb.setDescription(CommandListener.prefix + "kick [member]");

        } else {
            eb.setTitle(DogeAPI.randomPhrasePermissions());
            eb.setDescription("You do not have access to this command!\nIf you need help with something, join our [support-server](https://dc.lyzev.eu)!");
        }

        channel.sendMessage(eb.build()).queue();
    }
}
