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

public class RoastCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 16, message.getContentDisplay().split(" ")[0].toLowerCase());

        JSONObject root = null;
        try {
            root = (JSONObject) new JSONParser().parse(DogeAPI.getUrlConnection("https://insult.mattbas.org/api/en/insult.json"));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Random Roast");

        assert root != null;
        if (!(boolean) root.get("error")) {
            eb.setDescription("**" + root.get("insult") + ".**");
            eb.setFooter("IMPORTANT: This roast may contain copyrights! We are not liable if you use this joke or do anything else with it!");
        } else
            eb.setDescription("Arghhhhhh, there was a connection problem!\nPlease try again later!");

        channel.sendMessage(eb.build()).queue();
    }
}
