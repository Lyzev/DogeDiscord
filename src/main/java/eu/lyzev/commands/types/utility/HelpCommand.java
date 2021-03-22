package eu.lyzev.commands.types.utility;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class HelpCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 2, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 4, message.getContentDisplay().split(" ")[0].toLowerCase());

        EmbedBuilder eb = DogeAPI.createEmbed();
        if (message.getContentDisplay().split(" ").length > 1) {
            switch (message.getContentDisplay().toLowerCase().split(" ")[1]) {
                case "all":
                    eb.setTitle(":earth_americas: All");
                    eb.setDescription("`help`, `invite`, `calc`, `user`, `guild`, `ban`, `kick`, `meme`, `blackjack`, `bj`, `bonk`, `dogecoin`, `highlow`, `showerthoughts`, `nudes`, `joke`, `roast`, `google`, `clap`, `emojify`, `lenny`, `owo`, `spoiler`, `giphy`, `reddit`, `animal`, `dog`, `cat`, `lizard`, `fox`, `shiba`, `owl`, `duck`, `bunny`, `baltop`, `inv`, `shop`, `money`, `coinflip`, `transfer`, `pay`, `beg`, `highlow`, `search`, `blackjack`, `bj`, `daily`, `weekly`, `mojang`, `skin`, `mcserver`, `minecraft`, `pubg`, `valorant`, `rocketleague`, `r6`");
                    eb.setFooter("Use " + CommandListener.prefix + " before each command!");
                    break;
                case "util":
                case "utility":
                    eb.setTitle(":tools: Utility Commands");
                    eb.setDescription("`help`, `invite`, `calc`, `user`, `guild`, `ban`, `kick`");
                    eb.setFooter("Use " + CommandListener.prefix + " before each command!");
                    break;
                case "fun":
                    eb.setTitle(":rofl: Fun Commands");
                    eb.setDescription("`meme`, `blackjack`, `bj`, `calc`, `bonk`, `dogecoin`, `highlow`, `showerthoughts`, `nudes`, `joke`, `roast`, `google`, `clap`, `emojify`, `lenny`, `owo`, `spoiler`, `giphy`, `reddit`, `minecraft`");
                    eb.setFooter("Use " + CommandListener.prefix + " before each command!");
                    break;
                case "animal":
                    eb.setTitle(":dog: Animal Commands");
                    eb.setDescription("`animal`, `dog`, `cat`, `lizard`, `fox`, `shiba`, `owl`, `duck`, `bunny`");
                    eb.setFooter("Use " + CommandListener.prefix + " before each command!");
                    break;
                case "economy":
                case "eco":
                    eb.setTitle(":moneybag: Economy");
                    eb.setDescription("`baltop`, `inv`, `shop`, `coinflip`, `money`, `transfer`, `pay`, `beg`, `highlow`, `search`, `blackjack`, `bj`, `daily`, `weekly`");
                    eb.setFooter("Use " + CommandListener.prefix + " before each command!");
                    break;
                case "gaming":
                    eb.setTitle(":video_game: Gaming");
                    eb.setDescription("`mojang`, `skin`, `mcserver`, `minecraft`, `pubg`, `valorant`, `rocketleague`, `r6`");
                    eb.setFooter("Use " + CommandListener.prefix + " before each command!");
                    break;
                default:
                    eb.setTitle("Doge Command List");
                    eb.addField(":earth_americas: All", "`help all`", true);
                    eb.addField(":tools: Utility", "`help util`", true);
                    eb.addField(":rofl: Fun", "`help fun`", true);
                    eb.addField(":moneybag: Economy", "`help eco`", true);
                    eb.addField(":dog: Animal", "`help animal`", true);
                    eb.addField(":video_game: Gaming", "`help gaming`", true);
                    eb.setFooter("Use " + CommandListener.prefix + " before each command!");
                    break;
            }
        } else {
            eb.setTitle("Doge Command List");
            eb.addField(":earth_americas: All", "`help all`", true);
            eb.addField(":tools: Utility", "`help util`", true);
            eb.addField(":rofl: Fun", "`help fun`", true);
            eb.addField(":moneybag: Economy", "`help eco`", true);
            eb.addField(":dog: Animal", "`help animal`", true);
            eb.addField(":video_game: Gaming", "`help gaming`", true);
            eb.setFooter("Use " + CommandListener.prefix + " before each command!");
        }
        channel.sendMessage(eb.build()).queue();
    }
}
