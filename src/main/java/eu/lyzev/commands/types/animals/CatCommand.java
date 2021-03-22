package eu.lyzev.commands.types.animals;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class CatCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "cat");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "cat");

        JSONArray root = null;
        try {
            root = (JSONArray) new JSONParser().parse(DogeAPI.getUrlConnection("https://api.thecatapi.com/v1/images/search"));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        assert root != null;
        JSONObject imageJSONObject = (JSONObject) root.get(0);

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Random Cat");
        eb.setImage(((String) imageJSONObject.get("url")));
        eb.setFooter("Source: " + imageJSONObject.get("url") + "\nIMPORTANT: This image may contain copyrights! We are not liable if you use this image or do anything else with it!");

        channel.sendMessage(eb.build()).queue();
    }
}
