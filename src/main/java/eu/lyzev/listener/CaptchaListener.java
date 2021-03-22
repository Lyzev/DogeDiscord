package eu.lyzev.listener;

import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import eu.lyzev.utils.OxCaptcha;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

public class CaptchaListener extends ListenerAdapter {

    public static HashMap<String, Long> captcha = new HashMap<>();
    public static HashMap<String, String> activeCaptcha = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            if (activeCaptcha.get(event.getAuthor().getId()) != null && event.isFromType(ChannelType.TEXT))
                if (activeCaptcha.get(event.getAuthor().getId()).equals(event.getMessage().getContentDisplay())) {
                    activeCaptcha.remove(event.getAuthor().getId());
                    captcha.remove(event.getAuthor().getId());
                    event.getTextChannel().sendMessage("Your answer was right, you can use the bot again!").queue();
                } else
                    event.getTextChannel().sendMessage("Sorry...but your answer isn't correct, try again!").queue();
        } catch (Exception ignored) {}
    }

    public static CaptchaListener INSTANCE() {
        return new CaptchaListener();
    }

    public void createCaptcha(TextChannel channel, User user) {
        OxCaptcha captcha = new OxCaptcha(200, 50);
        captcha.background();
        captcha.setFont("DejaVu Serif", 1, 35);
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 5;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        captcha.textCentered(generatedString, 1, 1);
        captcha.distortionElastic();
        captcha.distortionShear2();
        captcha.recenter();
        if (DogeAPI.random(1, 2) == 1)
            captcha.noiseStraightLine();
        else
            captcha.noiseCurvedLine();
        captcha.blurGaussian(0.6);
        try {
            captcha.save("./captcha/" + generatedString + ".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        activeCaptcha.put(user.getId(), generatedString);
        channel.sendMessage("__**dCaptcha**__\n" + user.getAsMention() + ", **Type** the **letters below** in the next **3min** or your **money** will be **resetted**!").queue();
        channel.sendFile(new File("./captcha/" + generatedString + ".png")).queue();
        new File("./captcha/" + generatedString + ".png").delete();
        new Thread(() -> {
            try {
                Thread.sleep(1000 * 60 * 3);
                if (activeCaptcha.get(user.getId()) != null) {
                    activeCaptcha.remove(user.getId());
                    PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM economy WHERE id = ?");
                    preparedStatement.setString(1, user.getId());
                    preparedStatement.executeUpdate();
                    preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM bank WHERE id = ?");
                    preparedStatement.setString(1, user.getId());
                    preparedStatement.executeUpdate();
                    channel.sendMessage(user.getAsMention() + ", I'm sorry, but you were to slow...").queue();
                }
            } catch (InterruptedException | SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void resetCaptcha() throws InterruptedException {
        while (true) {
            Thread.sleep(1000 * 60 * 60 * 2);
            captcha.clear();
        }
    }
}
