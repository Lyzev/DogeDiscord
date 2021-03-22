package eu.lyzev.commands.types.gaming;

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

public class SkinCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        String[] args = message.getContentDisplay().split(" ");

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Minecraft Skin");

        if (args.length == 2) {

            try {

                if (DogeDiscord.premium.contains(channel.getGuild().getId()))
                    CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());
                else
                    CommandListener.INSTANCE().addDelay(member.getId(), 16, message.getContentDisplay().split(" ")[0].toLowerCase());

                JSONObject root = null;
                try {
                    root = (JSONObject) new JSONParser().parse(DogeAPI.getUrlConnection("https://api.mojang.com/users/profiles/minecraft/" + args[1]));
                } catch (ParseException | IOException ignored1) {
                }

                String minecraftName = (String) root.get("name");
                String uuid = (String) root.get("id");

                eb.setTitle("Doge Minecraft Skin");
                eb.setThumbnail("https://crafatar.com/renders/head/" + uuid);
                eb.setImage("https://crafatar.com/renders/body/" + uuid);
                eb.setFooter("Account by " + minecraftName);

            } catch (Exception ignored) {
                eb.setDescription("You need to provide a existing minecraft-account!\nType `r!skin (minecraft-account)`!");
                channel.sendMessage(eb.build()).queue();

                return;
            }
        } else {
            if (DogeDiscord.premium.contains(channel.getGuild().getId()))
                CommandListener.INSTANCE().addDelay(member.getId(), 4, message.getContentDisplay().split(" ")[0].toLowerCase());
            else
                CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());
            eb.setDescription("You need to provide a minecraft-account, I think that's common sense tbh.");
        }

        channel.sendMessage(eb.build()).queue();
    }
}
