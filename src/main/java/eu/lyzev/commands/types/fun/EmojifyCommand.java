package eu.lyzev.commands.types.fun;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class EmojifyCommand implements ServerCommand {
    private static boolean isAlphabet(String character) {
        switch (character.toLowerCase()) {
            case "a":
            case "b":
            case "c":
            case "d":
            case "e":
            case "f":
            case "g":
            case "h":
            case "i":
            case "j":
            case "k":
            case "l":
            case "m":
            case "n":
            case "o":
            case "p":
            case "q":
            case "r":
            case "s":
            case "t":
            case "u":
            case "v":
            case "w":
            case "x":
            case "y":
            case "z":
                return true;
            default:
                return false;
        }
    }

    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Emojify");

        if (message.getContentDisplay().split(" ").length > 1) {

            StringBuilder msg = new StringBuilder();
            for (String i : message.getContentDisplay().substring(message.getContentDisplay().split(" ")[0].length() + 1).toLowerCase().split("")) {
                if (isAlphabet(i))
                    msg.append(":regional_indicator_").append(i).append(": ");
                else
                    msg.append(i);
            }

            eb.setDescription(msg);
            eb.setFooter("By " + member.getUser().getAsTag());

            channel.sendMessage(eb.build()).queue();

        } else {
            eb.setDescription("What should I emojify for you?\nr!emojify (text)");
            channel.sendMessage(eb.build()).queue();
        }
    }
}
