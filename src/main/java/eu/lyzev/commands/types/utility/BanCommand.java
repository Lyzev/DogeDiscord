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

public class BanCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());

        EmbedBuilder eb = DogeAPI.createEmbed();
        if (member.hasPermission(Permission.BAN_MEMBERS)) {
            eb.setTitle("Doge Ban");

            if (message.getContentDisplay().split(" ").length == 3 && !message.getMentionedMembers().isEmpty()) {
                try {
                    int days = Integer.parseInt(message.getContentDisplay().split(" ")[2]);

                    eb.setDescription(message.getMentionedMembers().get(0).getAsMention() + " was banned by " + member.getAsMention() + "!");
                    channel.getGuild().ban(message.getMentionedMembers().get(0), days).queue();
                } catch (Exception ignored) {
                    eb.setDescription(CommandListener.prefix + "ban [member]");
                    channel.sendMessage(eb.build()).queue();
                    return;
                }
            } else
                eb.setDescription(CommandListener.prefix + "ban [member]");

        } else {
            eb.setTitle(DogeAPI.randomPhrasePermissions());
            eb.setDescription("You do not have access to this command!\nIf you need help with something, join our [support-server](https://dc.lyzev.eu)!");
        }

        channel.sendMessage(eb.build()).queue();
    }
}
