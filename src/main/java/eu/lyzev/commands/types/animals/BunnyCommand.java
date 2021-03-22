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

public class BunnyCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "bunny");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "bunny");

        JSONObject root = null;
        try {
            root = (JSONObject) new JSONParser().parse(DogeAPI.getUrlConnection("https://api.bunnies.io/v2/loop/random/?media=gif,png"));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        assert root != null;
        JSONObject media = (JSONObject) root.get("media");

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Random Bunny");
        eb.setImage(((String) media.get("gif")));
        eb.setFooter("Source: " + media.get("source") + "\nIMPORTANT: This image may contain copyrights! We are not liable if you use this image or do anything else with it!");

        channel.sendMessage(eb.build()).queue();
    }
}
