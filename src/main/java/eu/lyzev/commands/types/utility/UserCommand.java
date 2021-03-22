package eu.lyzev.commands.types.utility;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class UserCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "user");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "user");
        EmbedBuilder eb = DogeAPI.createEmbed();
        if (!message.getMentionedMembers().isEmpty())
            member = message.getMentionedMembers().get(0);
        if (member.getUser().getAvatarUrl() != null)
            eb.setThumbnail(member.getUser().getAvatarUrl());
        else
            eb.setThumbnail(member.getUser().getDefaultAvatarUrl());
        eb.setTitle("User: " + member.getUser().getAsTag());
        eb.setFooter(CommandListener.prefix + "user [@member]");
        eb.addField("ID", member.getId(), false);
        String roles = "";
        int index = 0;
        for (Role i : member.getRoles()) {
            if (index == 0)
                roles = roles + i.getName();
            else
                roles = roles + ", " + i.getName();
            index++;
        }
        if (roles.length() > 1)
            eb.addField("Roles" , roles, false);
        if (member.getOnlineStatus().equals(OnlineStatus.ONLINE))
            eb.addField("Status", "ONLINE :green_circle:", false);
        else if (member.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB))
            eb.addField("Status", "DO_NOT_DISTURB :red_circle:", false);
        else if (member.getOnlineStatus().equals(OnlineStatus.IDLE))
            eb.addField("Status", "IDLE :yellow_circle:", false);
        else
            eb.addField("Status", "OFFLINE :black_circle:", false);
        eb.addField(channel.getGuild().getName() + " joined", member.getTimeJoined().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)) + " " + new SimpleDateFormat("HH:mm:ss").format(Timestamp.valueOf(member.getTimeJoined().atZoneSameInstant(ZoneId.of("Z")).toLocalDateTime()).getTime()), false);
        eb.addField("Account created", member.getUser().getTimeCreated().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)), false);
        if (member.getUser().isBot())
            eb.addField("Bot", "", false);
        channel.sendMessage(eb.build()).queue();
    }
}
