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

public class GiphyCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 15, message.getContentDisplay().split(" ")[0].toLowerCase());

        JSONObject root = null;
        try {
            root = (JSONObject) new JSONParser().parse(DogeAPI.getUrlConnection("http://api.giphy.com/v1/gifs/random?api_key=dc6zaTOxFJmzC&tag=american+psycho+dog+cat+bunny+rabbit+lizard+animal+cooking+food+cinema+movie+meme+discord+reddit+happy+angry+cool+cold+warm+cute+fast+hero+marvel+comics+birds+cows+horse"));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        assert root != null;

        JSONObject data = (JSONObject) root.get("data");

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle(((String) data.get("title")).toUpperCase(), (String) data.get("source"));
        eb.setThumbnail("https://i.ibb.co/Y3ZpdDK/Poweredby-640px-Black-Vert-Text.png");
        eb.setImage((String) data.get("image_url"));
        eb.setFooter("IMPORTANT: This image/gif may contain copyrights! We are not liable if you use this image/gif or do anything else with it!");

        channel.sendMessage(eb.build()).queue();

    }
}
