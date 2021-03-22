package eu.lyzev.listener;

import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.blackjack.BlackjackManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BlackjackListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (DogeDiscord.ready && !event.getAuthor().isBot() && event.isFromType(ChannelType.TEXT) && BlackjackManager.INSTANCE().playingBlackjack(Objects.requireNonNull(event.getMember()))) {
            if (event.getMessage().getContentDisplay().equalsIgnoreCase("e")) {
                BlackjackManager.blackjack.put(event.getMember().getId(), BlackjackManager.blackjack.get(event.getMember().getId()).replace("%false%", "%true%"));

                BlackjackManager.INSTANCE().endGame(event.getMember(), event.getTextChannel());
            } else if (event.getMessage().getContentDisplay().equalsIgnoreCase("s")) {
                BlackjackManager.blackjack.put(event.getMember().getId(), BlackjackManager.blackjack.get(event.getMember().getId()).replace("%false%", "%true%"));

                BlackjackManager.INSTANCE().stay(event.getMember(), event.getTextChannel());
            } else if (event.getMessage().getContentDisplay().equalsIgnoreCase("h")) {
                BlackjackManager.blackjack.put(event.getMember().getId(), BlackjackManager.blackjack.get(event.getMember().getId()).replace("%false%", "%true%"));

                BlackjackManager.INSTANCE().hit(event.getMember(), event.getTextChannel());
            }
        }
    }

}
