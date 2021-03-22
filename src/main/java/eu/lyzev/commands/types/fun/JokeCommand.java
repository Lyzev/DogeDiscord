package eu.lyzev.commands.types.fun;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class JokeCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 16, message.getContentDisplay().split(" ")[0].toLowerCase());

        JSONObject root = null;
        try {
            root = (JSONObject) new JSONParser().parse(DogeAPI.getUrlConnection("https://official-joke-api.appspot.com/jokes/random"));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Random Joke");
        assert root != null;
        eb.setDescription("**" + root.get("setup") + "**\n*" + root.get("punchline") + "*\n");
        eb.setFooter("IMPORTANT: This joke may contain copyrights! We are not liable if you use this joke or do anything else with it!");

        channel.sendMessage(eb.build()).queue();
    }
}
