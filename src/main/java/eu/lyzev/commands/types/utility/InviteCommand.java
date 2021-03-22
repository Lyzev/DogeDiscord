package eu.lyzev.commands.types.utility;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class InviteCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 2, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 4, message.getContentDisplay().split(" ")[0].toLowerCase());

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Invite");
        eb.addField(":robot: Doge-Bot", "[Click here!](https://doge.lyzev.eu)", true);
        eb.addField(":desktop: Doge-Server", "[Click here!](https://dc.lyzev.eu)", true);
        eb.addField(":envelope_with_arrow: Doge-Support", "[Click here!](https://mail.lyzev.eu)", true);
        channel.sendMessage(eb.build()).queue();
    }
}
