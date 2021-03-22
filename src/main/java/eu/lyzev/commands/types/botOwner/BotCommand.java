package eu.lyzev.commands.types.botOwner;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class BotCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "bot");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "bot");

        EmbedBuilder eb = DogeAPI.createEmbed();
        if (member.getId().equalsIgnoreCase(DogeDiscord.ownerId)) {
            eb.setTitle("Doge Bot");
            eb.setThumbnail(DogeDiscord.getINSTANCE().getJDA().getSelfUser().getAvatarUrl());
            eb.setDescription("Guilds: " + DogeDiscord.getINSTANCE().getJDA().getGuilds().size() + "\nMembers: " + DogeDiscord.getINSTANCE().getJDA().getUsers().size() + "\nText-Channels: " + DogeDiscord.getINSTANCE().getJDA().getTextChannels().size() + "\nVoice-Channels: " + DogeDiscord.getINSTANCE().getJDA().getVoiceChannels().size() + "\nRAM: " + ((double) Runtime.getRuntime().totalMemory() / 1024 / 1024) + "/" + ((double) Runtime.getRuntime().maxMemory() / 1024 / 1024) + "MB" + "\nPing: " + DogeDiscord.getINSTANCE().getJDA().getGatewayPing() + "ms");
        } else {
            eb.setTitle(DogeAPI.randomPhrasePermissions());
            eb.setDescription("You do not have access to this command!\nIf you need help with something, join our [support-server](https://dc.lyzev.eu)!");
        }
        channel.sendMessage(eb.build()).queue();
    }
}
