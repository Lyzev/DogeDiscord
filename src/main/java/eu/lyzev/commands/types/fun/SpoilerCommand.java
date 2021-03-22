package eu.lyzev.commands.types.fun;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class SpoilerCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());

        if (message.getContentDisplay().split(" ").length > 1) {

            StringBuilder msg = new StringBuilder();

            for (String i : message.getContentDisplay().substring(message.getContentDisplay().split(" ")[0].length() + 1).replace(" ", "").split(""))
                msg.append("||").append(i).append("||");

            channel.sendMessage(msg.toString()).queue();

        } else {
            EmbedBuilder eb = DogeAPI.createEmbed();
            eb.setTitle("Doge Clap");
            eb.setDescription("What should I spoiler for you?\nr!spoiler (text)");
            channel.sendMessage(eb.build()).queue();
        }
    }
}
