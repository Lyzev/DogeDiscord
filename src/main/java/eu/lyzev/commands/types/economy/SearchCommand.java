package eu.lyzev.commands.types.economy;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class SearchCommand implements ServerCommand {

    public static HashMap<String, String> search = new HashMap<>();

    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 30, CommandListener.prefix + "search");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 45, CommandListener.prefix + "search");

        try {
            PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

            preparedStatement.setString(1, member.getId());

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.isClosed()) {
                preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO economy VALUES (?, ?)");

                preparedStatement.setString(1, member.getId());
                preparedStatement.setLong(2, 0);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String search1 = DogeAPI.randomSearch();
        String search2 = DogeAPI.randomSearch();
        String search3 = DogeAPI.randomSearch();

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Search");
        eb.setDescription("Where do you want to search?\n`" + search1 + "`, `" + search2 + "`, `" + search3 + "`");
        eb.setFooter("Type your decision in the channel!");
        channel.sendMessage(eb.build()).queue();

        search.put(member.getId(), (System.currentTimeMillis() + 10 * 1000) + " " + channel.getId() + " " + search1 + " " + search2 + " " + search3);

        new Thread(() -> {
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (search.get(member.getId()) != null) {
                search.remove(member.getId());
                channel.sendMessage(member.getAsMention() + ", you asked to search somewhere but you didn't respond...").queue();
            }

        }).start();
    }
}
