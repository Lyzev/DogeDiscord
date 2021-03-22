package eu.lyzev.commands.types.animals;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class AnimalCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {

        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "animal");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "animal");

        switch (DogeAPI.random(0, 7)) {
            case 0:
                DogeDiscord.getINSTANCE().getCommandManager().perform("cat", member, channel, message);
                break;
            case 1:
                DogeDiscord.getINSTANCE().getCommandManager().perform("dog", member, channel, message);
                break;
            case 2:
                DogeDiscord.getINSTANCE().getCommandManager().perform("duck", member, channel, message);
                break;
            case 3:
                DogeDiscord.getINSTANCE().getCommandManager().perform("fox", member, channel, message);
                break;
            case 4:
                DogeDiscord.getINSTANCE().getCommandManager().perform("lizard", member, channel, message);
                break;
            case 5:
                DogeDiscord.getINSTANCE().getCommandManager().perform("shiba", member, channel, message);
                break;
            case 6:
                DogeDiscord.getINSTANCE().getCommandManager().perform("owl", member, channel, message);
                break;
            default:
                DogeDiscord.getINSTANCE().getCommandManager().perform("bunny", member, channel, message);
                break;
        }
    }
}
