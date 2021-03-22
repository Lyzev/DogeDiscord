package eu.lyzev.commands.types.gaming;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class McServerCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Minecraft Server");

        if (message.getContentDisplay().split(" ").length != 2) {
            if (DogeDiscord.premium.contains(channel.getGuild().getId()))
                CommandListener.INSTANCE().addDelay(member.getId(), 4, message.getContentDisplay().split(" ")[0].toLowerCase());
            else
                CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());
            eb.setDescription("You need to provide a server-ip, that's a common sense...\nType `r!mcserver (server-ip)`");
            channel.sendMessage(eb.build()).queue();
            return;
        }

        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 16, message.getContentDisplay().split(" ")[0].toLowerCase());

        JSONObject root = null;
        try {
            root = (JSONObject) new JSONParser().parse(DogeAPI.getUrlConnection("https://api.mcsrvstat.us/2/" + message.getContentDisplay().split(" ")[1]));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        String ip = (String) root.get("ip");
        if (ip == null || ip.isEmpty()) {
            eb.setDescription("You need to provide a server-ip, that's a common sense...\nType `r!mcserver (server-ip)`");
            channel.sendMessage(eb.build()).queue();
            return;
        }

        String hostname = (String) root.get("hostname");

        long port = (long) root.get("port");
        boolean onlinestatus = (boolean) root.get("online");

        String online = "Offline";

        if (onlinestatus) {
            online = "Online";
        }

        JSONObject players = (JSONObject) root.get("players");

        long playersOnline = 0;

        try {
            if (players.get("online") != null) {
                playersOnline = (long) players.get("online");
            }
        } catch (NullPointerException ignored) {
        }

        long playersMax = 0;
        try {
            if (players.get("max") != null) {
                playersMax = (long) players.get("max");
            }
        } catch (NullPointerException ignored) {
        }

        String version = (String) root.get("version");

        String icon = (String) root.get("icon");

        byte[] imagedata = Base64.decodeBase64(icon.substring(icon.indexOf(",") + 1));
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File("./mcserver/" + root.get("hostname") + ".png");
        try {
            assert bufferedImage != null;
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (version != null || !version.isEmpty() || version.startsWith("Requires")) {
                if (version.contains("/")) {
                    version = version.replace("/", "-");
                }
            } else {
                version = "UNKOWN";
            }
        } catch (NullPointerException ignored) {
            version = "UNKOWN";
        }

        eb.addField("IP/Hostname:", "Hostname: " + hostname + "\nIP: " + ip + ":" + port, false);
        eb.addField("Status:", online, false);
        eb.addField("Players:", playersOnline + "/" + playersMax, false);
        eb.addField("Version:", version, false);
        channel.sendFile(file).queue();
        file.delete();
        channel.sendMessage(eb.build()).queue();
    }
}
