package eu.lyzev.listener;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import eu.lyzev.main.DogeDiscord;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinLeaveListener extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        DogeDiscord.logger.info(Ansi.colorize("Doge joined a guild! Guild-Name: " + event.getGuild().getName() + " | Guild-ID: " + event.getGuild().getId(), Attribute.YELLOW_TEXT(), Attribute.BOLD()));
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        DogeDiscord.logger.info(Ansi.colorize("Doge leaved a guild! Guild-Name: " + event.getGuild().getName() + " | Guild-ID: " + event.getGuild().getId(), Attribute.YELLOW_TEXT(), Attribute.BOLD()));
    }
}
