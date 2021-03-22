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

public class BaltopCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "baltop");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "baltop");


        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Baltop");

        try {
            // List<String> baltop = new ArrayList<>();

            PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy ORDER BY money DESC");

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.isClosed()) {
                eb.setDescription("There are no bank accounts in this guild!");
                channel.sendMessage(eb.build()).queue();
                return;
            }

            int rank = 1;

            while (rs.next()) {
                if (channel.getGuild().getMemberById(rs.getString("id")) != null) {
                    eb.addField(rank + ". " + channel.getGuild().getMemberById(rs.getString("id")).getUser().getAsTag(), "Money: `" + rs.getLong("money") + "$`", true);
                    rank++;
                }

                if (rank > 4) break;
            }

            channel.sendMessage(eb.build()).queue();

            /**
             *             baltop.add(rs.getString("id") + "-" + rs.getLong("money"));
             *
             *             // 2.
             *             preparedStatement = DogeDiscord.sql.prepareStatement("SELECT *, MAX(money) AS money FROM economy WHERE money < (SELECT MAX(money) FROM economy);");
             *
             *             rs = preparedStatement.executeQuery();
             *
             *             if (!rs.isClosed()) {
             *                 baltop.add(rs.getString("id") + "-" + rs.getLong("money"));
             *             }
             *
             *             // 3.
             *             preparedStatement = DogeDiscord.sql.prepareStatement("SELECT *, MAX(money) AS money FROM economy WHERE money < (SELECT MAX(money) FROM economy WHERE money < (SELECT MAX(money) FROM economy));");
             *
             *             rs = preparedStatement.executeQuery();
             *
             *             if (!rs.isClosed()) {
             *                 baltop.add(rs.getString("id") + "-" + rs.getLong("money"));
             *             }
             *
             *             System.out.println(String.join("\n", baltop));
             *
             *             int rank = 1;
             *
             *             for (String i : baltop) {
             *                 if (channel.getGuild().getMemberById(i.split("-")[0]) != null) {
             *                     eb.addField(rank + ". " + channel.getGuild().getMemberById(i.split("-")[0]).getUser().getAsTag(), "Money: `" + i.split("-")[1] + "$`", true);
             *                     rank++;
             *                 }
             *             }
             *
             *             channel.sendMessage(eb.build()).queue();
             */
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
