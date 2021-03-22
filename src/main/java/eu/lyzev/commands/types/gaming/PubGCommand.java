package eu.lyzev.commands.types.gaming;

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

public class PubGCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 16, message.getContentDisplay().split(" ")[0].toLowerCase());

        JSONObject root = null;
        try {
            root = (JSONObject) new JSONParser().parse(DogeAPI.getUrlConnection("https://www.reddit.com/r/pubg/top/.json"));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        assert root != null;

        JSONObject data = (JSONObject) root.get("data");

        JSONArray children = (JSONArray) data.get("children");

        JSONObject post = (JSONObject) children.get(DogeAPI.random(0, (int) ((Long) data.get("dist") - 1)));

        JSONObject postData = (JSONObject) post.get("data");

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle((String) postData.get("title"), "https://www.reddit.com" + postData.get("permalink"));
        if (postData.get("url") != null && (((String) postData.get("url")).endsWith(".jpg") || ((String) postData.get("url")).endsWith(".png")) || ((String) postData.get("url")).endsWith(".gif"))
            eb.setImage((String) postData.get("url"));
        else if (postData.get("url") != null)
            eb.setDescription((String) postData.get("url"));
        eb.setFooter(postData.get("ups") + " \uD83D\uDC4D | " + postData.get("num_comments") + " \uD83D\uDCAC\nAuthor: " + postData.get("author") + "\nSubreddit: " + postData.get("subreddit_name_prefixed"));

        channel.sendMessage(eb.build()).queue();

    }
}
