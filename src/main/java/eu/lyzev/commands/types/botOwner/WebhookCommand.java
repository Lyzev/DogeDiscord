package eu.lyzev.commands.types.botOwner;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.redstonecraft.redstoneapi.json.JSONObject;
import net.redstonecraft.redstoneapi.tools.HttpHeader;
import net.redstonecraft.redstoneapi.tools.HttpRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WebhookCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "webhook");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "webhook");

        EmbedBuilder eb = DogeAPI.createEmbed();
        if (member.getId().equalsIgnoreCase(DogeDiscord.ownerId)) {
            if (message.getContentDisplay().split(" ").length > 2 && !message.getMentionedMembers().isEmpty()) {
                message.delete().queue();

                Webhook webhook = null;
                for (Webhook i : channel.retrieveWebhooks().complete()) {
                    if (i.getName().equals("Doge")) {
                        webhook = i;
                        break;
                    }
                }
                if (webhook == null) {
                    webhook = channel.createWebhook("Doge").complete();
                }

                String url = webhook.getUrl();

                JSONObject webhookJSON = new JSONObject();
                webhookJSON.put("content", message.getContentDisplay().substring(message.getContentDisplay().split(" ")[0].length() + message.getContentDisplay().split(" ")[1].length() + 2));
                webhookJSON.put("username", message.getMentionedMembers().get(0).getUser().getName());
                if (message.getMentionedMembers().get(0).getUser().getAvatarUrl() == null)
                    webhookJSON.put("avatar_url", message.getMentionedMembers().get(0).getUser().getDefaultAvatarUrl());
                else
                    webhookJSON.put("avatar_url", message.getMentionedMembers().get(0).getUser().getAvatarUrl());

                try {
                    HttpRequest.post(url, webhookJSON.toJSONString().getBytes(StandardCharsets.UTF_8), new HttpHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36"), new HttpHeader("Content-Type", "application/json"));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else {
                eb.setTitle("Doge Webhook");
                eb.setDescription(CommandListener.prefix + "webhook [@member] [content]");
                channel.sendMessage(eb.build()).queue();
            }
        } else {
            eb.setTitle(DogeAPI.randomPhrasePermissions());
            eb.setDescription("You do not have access to this command!\nIf you need help with something, join our [support-server](https://dc.lyzev.eu)!");
            channel.sendMessage(eb.build()).queue();
        }
    }

    /*
     public static byte[] extractBytes (String ImageName) throws IOException {
     // open image
     File imgPath = new File(ImageName);
     BufferedImage bufferedImage = ImageIO.read(imgPath);

     // get DataBufferBytes from Raster
     WritableRaster raster = bufferedImage .getRaster();
     DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

     return ( data.getData() );
     }
     */

    /*
        public static void main(String[] args) {
        try {
            String url = "https://pays.host/api/images/upload?key=lyzev._Ia0DPe";
            byte[] hurensohn = "--UTF-8\r\nContent-Type: image/png\r\nContent-Disposition: form-data; name=\"pays.host\"; filename=\"file\"\r\nContent-Transfer-Encoding: binary\r\n\r\n".getBytes(StandardCharsets.UTF_8);


            byte[] image = extractBytes("./img/bonk.png");

            byte[] hs = new byte[hurensohn.length + image.length];
            System.arraycopy(hurensohn, 0, hs, 0, hurensohn.length);
            System.arraycopy(image, 0, hs, hurensohn.length, image.length);

            Logger logger = Logger.getLogger("test");
            try {
                logger.setUseParentHandlers(false);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss");
                FileHandler fileHandler = new FileHandler("./logs/latest-log " + simpleDateFormat.format(new Date()) + ".txt");
                fileHandler.setFormatter(new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        return record.getMessage();
                    }
                });
                logger.addHandler(fileHandler);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            try {
                logger.info(HttpRequest.post(url, hs, new HttpHeader("User-Agent", "ShareX/13.4.0"), new HttpHeader("Content-Type", "multipart/form-data")).getContentAsString());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
     */
}
