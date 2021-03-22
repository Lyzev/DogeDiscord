package eu.lyzev.commands.types.utility;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;

public class GuildCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "guild");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "guild");
        EmbedBuilder eb = DogeAPI.createEmbed();
        if (channel.getGuild().retrieveInvites().complete().size() == 0)
            channel.createInvite().complete();
        eb.setTitle("Guild: " + member.getGuild().getName(), channel.getGuild().retrieveInvites().complete().get(0).getUrl());
        eb.setThumbnail(channel.getGuild().getIconUrl());
        eb.addField("Owner", Objects.requireNonNull(channel.getGuild().getOwner()).getUser().getAsTag(), false);
        eb.addField("Members", ((channel.getGuild().getMemberCache().stream().filter((m) -> m.getOnlineStatus() == OnlineStatus.ONLINE).count()) + (channel.getGuild().getMemberCache().stream().filter((m) -> m.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB).count()) + (channel.getGuild().getMemberCache().stream().filter((m) -> m.getOnlineStatus() == OnlineStatus.IDLE).count())) + "/" + (channel.getGuild().getMemberCache().size()), false);
        eb.addField("Guild Id", channel.getGuild().getId(), false);
        eb.addField("Roles", "" + channel.getGuild().getRoleCache().size(), false);
        eb.addField("Created", channel.getGuild().getTimeCreated().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)), false);
        if (channel.getGuild().getAfkChannel() != null)
            eb.addField("AFK", "Timeout: " + channel.getGuild().getAfkTimeout().getSeconds() + " seconds\nChannel: " + channel.getGuild().getAfkChannel().getName(), false);
        else
            eb.addField("AFK", "Timeout: " + channel.getGuild().getAfkTimeout().getSeconds() + " seconds\nNo channel has been selected", false);
        channel.sendMessage(eb.build()).queue();
    }
}