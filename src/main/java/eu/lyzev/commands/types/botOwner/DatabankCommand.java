package eu.lyzev.commands.types.botOwner;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;

public class DatabankCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "db");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "db");

        if (member.getId().equalsIgnoreCase(DogeDiscord.ownerId)) {
            channel.sendFile(new File("./data/data.db")).queue();
        } else {
            EmbedBuilder eb = DogeAPI.createEmbed();
            eb.setTitle(DogeAPI.randomPhrasePermissions());
            eb.setDescription("You do not have access to this command!\nIf you need help with something, join our [support-server](https://dc.lyzev.eu)!");
            channel.sendMessage(eb.build()).queue();
        }
    }
}