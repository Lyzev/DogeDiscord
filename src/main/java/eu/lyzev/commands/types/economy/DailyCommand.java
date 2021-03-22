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

public class DailyCommand implements ServerCommand {

    private static final long reward = 420;

    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        CommandListener.INSTANCE().addDelay(member.getId(), 60, CommandListener.prefix + "daily");

        try {

            boolean daily = false;

            PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM daily WHERE id = ?");

            preparedStatement.setString(1, member.getId());

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.isClosed()) {
                daily = true;

                preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO daily VALUES (?, ?)");

                preparedStatement.setString(1, member.getId());
                preparedStatement.setLong(2, (System.currentTimeMillis() + (24 * 3600 * 1000)));

                preparedStatement.executeUpdate();


            } else {

                if (rs.getLong("time") < System.currentTimeMillis())
                    daily = true;

            }

            if (daily) {

                preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                preparedStatement.setString(1, member.getId());

                rs = preparedStatement.executeQuery();

                preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                preparedStatement.setLong(1, rs.getLong("money") + reward);
                preparedStatement.setString(2, member.getId());

                preparedStatement.executeUpdate();

                preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE daily SET time = ? WHERE id = ?");

                preparedStatement.setLong(1, (System.currentTimeMillis() + (24 * 3600 * 1000)));
                preparedStatement.setString(2, member.getId());

                preparedStatement.executeUpdate();

                EmbedBuilder eb = DogeAPI.createEmbed();
                eb.setTitle("Doge Daily Reward");
                eb.setDescription("You claimed the reward, so you did get `" + reward + "$` into your pocket.");

                channel.sendMessage(eb.build()).queue();

            } else {

                preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM daily WHERE id = ?");

                preparedStatement.setString(1, member.getId());

                rs = preparedStatement.executeQuery();

                if (!rs.isClosed()) {
                    int hours = (int) ((rs.getLong("time") - System.currentTimeMillis()) / (3600 * 1000));

                    EmbedBuilder eb = DogeAPI.createEmbed();
                    eb.setTitle("Doge Daily Reward");
                    eb.setDescription("You already claimed your reward, you have to wait `" + hours + "h` to claim the reward again.");

                    channel.sendMessage(eb.build()).queue();
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
