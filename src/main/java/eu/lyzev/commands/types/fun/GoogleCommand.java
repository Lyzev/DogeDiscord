package eu.lyzev.commands.types.fun;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class GoogleCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());

        if (message.getContentDisplay().split(" ").length > 1) {
            channel.sendMessage("http://lmgtfy.com/?q=" + (message.getContentDisplay().substring(message.getContentDisplay().split(" ")[0].length() + 1))).queue();
        } else {
            EmbedBuilder eb = DogeAPI.createEmbed();
            eb.setTitle("Doge Google");
            eb.setDescription("What should I google for you?\nr!google (text)");
            channel.sendMessage(eb.build()).queue();
        }
    }
}
