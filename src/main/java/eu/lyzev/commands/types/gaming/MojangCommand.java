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

public class MojangCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 16, message.getContentDisplay().split(" ")[0].toLowerCase());

        JSONArray root1 = null;
        try {
            root1 = (JSONArray) new JSONParser().parse(DogeAPI.getUrlConnection("https://status.mojang.com/check"));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        JSONObject root;
        assert root1 != null;
        root = (JSONObject) root1.get(0);

        String status = ":red_circle: OFFLINE";

        if (root.get("minecraft.net").equals("green")) {
            status = ":green_circle: ONLINE";
        } else if (root.get("minecraft.net").equals("yellow")) {
            status = ":yellow_circle: SOME ISSUES";
        }

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Mojang");
        eb.addField("Minecraft.net", status, true);

        root = (JSONObject) root1.get(7);

        status = ":red_circle: OFFLINE";

        if (root.get("mojang.com").equals("green")) {
            status = ":green_circle: ONLINE";
        } else if (root.get("mojang.com").equals("yellow")) {
            status = ":yellow_circle: SOME ISSUES";
        }

        eb.addField("Mojang.com", status, true);

        channel.sendMessage(eb.build()).queue();
    }
}
