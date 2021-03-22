package eu.lyzev.commands.types.economy;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.commands.types.economy.Shop.InventoryCommand;
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

public class BegCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 30, CommandListener.prefix + "beg");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 45, CommandListener.prefix + "beg");

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

            EmbedBuilder eb = DogeAPI.createEmbed();
            eb.setTitle("Doge Begging");

            if (DogeAPI.random(0, 4) < 3) {
                long money;

                if (DogeAPI.random(0, 5) == 2)
                    money = DogeAPI.random(30, 100);
                else
                    money = DogeAPI.random(1, 50);

                if (InventoryCommand.multi.get(member.getId()) != null)
                    money = (long) (money * InventoryCommand.multi.get(member.getId()));

                try {
                    preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                    preparedStatement.setString(1, member.getId());

                    rs = preparedStatement.executeQuery();

                    if (rs.isClosed()) {

                        preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO bank VALUES (?, ?)");

                        preparedStatement.setString(1, member.getId());
                        preparedStatement.setLong(2, 0);

                        preparedStatement.executeUpdate();

                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                        preparedStatement.setString(1, member.getId());

                        rs = preparedStatement.executeQuery();
                    }

                    preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE bank SET max = ? WHERE id = ?");

                    preparedStatement.setLong(1, (rs.getLong("max") + (money / 4)));
                    preparedStatement.setString(2, member.getId());

                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                eb.setDescription(DogeAPI.randomPhraseBegYes().replace("%money%", "`" + money + "$`"));

                preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                preparedStatement.setString(1, member.getId());

                rs = preparedStatement.executeQuery();

                money = money + rs.getLong("money");

                preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                preparedStatement.setLong(1, money);
                preparedStatement.setString(2, member.getId());

                preparedStatement.executeUpdate();

            } else
                eb.setDescription(DogeAPI.randomPhraseBegNo());

            channel.sendMessage(eb.build()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
