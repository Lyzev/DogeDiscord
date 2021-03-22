package eu.lyzev.commands.types.animals;

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

public class DuckCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "duck");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "duck");

        JSONObject root = null;
        try {
            root = (JSONObject) new JSONParser().parse(DogeAPI.getUrlConnection("https://random-d.uk/api/v1/random?type=png"));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Random Duck");
        assert root != null;
        eb.setImage(((String) root.get("url")));
        eb.setFooter("Source: " + root.get("url") + "\nIMPORTANT: This image may contain copyrights! We are not liable if you use this image or do anything else with it!");

        channel.sendMessage(eb.build()).queue();
    }
}
