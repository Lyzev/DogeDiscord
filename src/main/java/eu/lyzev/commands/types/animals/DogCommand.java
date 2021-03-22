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

public class DogCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "dog");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "dog");

        JSONObject root = null;
        try {
            root = (JSONObject) new JSONParser().parse(DogeAPI.getUrlConnection("https://dog.ceo/api/breeds/image/random"));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        assert root != null;
        String success = (String) root.get("status");

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Random Dog");

        if (success.equalsIgnoreCase("success")) {
            eb.setImage(((String) root.get("message")));
            eb.setFooter("Source: " + root.get("message") + "\nIMPORTANT: This image may contain copyrights! We are not liable if you use this image or do anything else with it!");
        } else {
            eb.setDescription("Arghhhhhh, there was a connection problem!\nPlease try again later!");
        }

        channel.sendMessage(eb.build()).queue();
    }
}
