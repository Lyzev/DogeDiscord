package eu.lyzev.listener;

import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class CommandListener extends ListenerAdapter {

    public static final String prefix = "d!"; // This is the prefix of the bot's commands!
    private static final HashMap<String, Long> delay = new HashMap<>();
    private static final HashMap<String, Long> channelDelay = new HashMap<>();

    public static CommandListener INSTANCE() {
        return new CommandListener();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try {
            try {
                if (!DogeDiscord.ready || !event.isFromType(ChannelType.TEXT) || event.getAuthor().isBot() || event.getMessage().getContentDisplay().length() < prefix.length() || !event.getMessage().getContentDisplay().toLowerCase().startsWith(prefix) || CaptchaListener.activeCaptcha.get(Objects.requireNonNull(event.getMember()).getId()) != null)
                    return;
            } catch (Exception ignored) {
                return;
            }

            final long cooldown = 10; // Cooldown between the delay in seconds messages send into channel!

            if (channelDelay.get(event.getTextChannel().getId()) != null && (channelDelay.get(event.getTextChannel().getId()) + (cooldown * 1000)) > System.currentTimeMillis() && delay.get(event.getAuthor().getId() + " " + event.getMessage().getContentDisplay().split(" ")[0].toLowerCase()) != null && delay.get(event.getAuthor().getId() + " " + event.getMessage().getContentDisplay().split(" ")[0].toLowerCase()) > System.currentTimeMillis())
                return;

            if (delay.get(event.getAuthor().getId() + " " + event.getMessage().getContentDisplay().split(" ")[0].toLowerCase()) != null && delay.get(event.getAuthor().getId() + " " + event.getMessage().getContentDisplay().split(" ")[0].toLowerCase()) > System.currentTimeMillis()) {
                channelDelay.put(event.getTextChannel().getId(), System.currentTimeMillis());

                EmbedBuilder eb = DogeAPI.createEmbed();
                eb.setTitle(DogeAPI.randomPhraseDelay());
                eb.setDescription("Stop sending too much commands! The bot has a command-delay, so it can't be crashed by users.\nYou have still **" + ((int) ((delay.get(event.getAuthor().getId() + " " + event.getMessage().getContentDisplay().split(" ")[0].toLowerCase()) - System.currentTimeMillis()) / 1000)) + " seconds** to wait!\nThe cooldown can't be disabled!");
                event.getTextChannel().sendMessage(eb.build()).queue();
            } else
                DogeDiscord.getINSTANCE().getCommandManager().perform(event.getMessage().getContentDisplay().substring(prefix.length()).split(" ")[0], event.getMember(), event.getTextChannel(), event.getMessage());

        } catch (InsufficientPermissionException e) {
            try {
                String permission = e.getPermission().toString();
                Objects.requireNonNull(event.getGuild().getOwner()).getUser().openPrivateChannel().queue((pv) ->
                {
                    EmbedBuilder eb = DogeAPI.createEmbed();
                    eb.setTitle("Doge Missing Permission");
                    eb.setDescription("The bot is missing a important permission: **" + permission + "**\n\nIf the bot doesn't have this permission, it can't do its work properly!\nIf you are not sure how to fix the problem, join our [support-server](https://dc.lyzev.eu)!");
                    pv.sendMessage(eb.build()).queue();
                });
            } catch (ErrorResponseException ignored) {}
        }
    }

    public void addDelay(String id, long time, String command) {
        delay.put(id + " " + command, (System.currentTimeMillis() + (time * 1000)));
    }
}
