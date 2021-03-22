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

public class CoinflipCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());
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
            eb.setTitle("Doge Coinflip");

            String[] args = message.getContentDisplay().split(" ");

            if (args.length == 3 && (args[2].equalsIgnoreCase("doge") || args[2].equalsIgnoreCase("number"))) {
                try {
                    Integer.parseInt(args[1]);
                } catch (Exception ignored) {
                    eb.setDescription("That's not a number...");
                    channel.sendMessage(eb.build()).queue();
                    return;
                }

                int amount = Integer.parseInt(args[1]);

                if (rs.isClosed()) {
                    preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");
                    preparedStatement.setString(1, member.getId());
                    rs = preparedStatement.executeQuery();
                }

                if (amount > rs.getLong("money"))
                    eb.setDescription("You don't have enough money...");
                else {
                    if (DogeAPI.random(1, 3) == 1) {
                        preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                        preparedStatement.setLong(1, rs.getLong("money") + amount);
                        preparedStatement.setString(2, member.getId());

                        preparedStatement.executeUpdate();

                        if (args[2].equalsIgnoreCase("doge"))
                            eb.setDescription("You won the coinflip, the :coin: landed on doge!");
                        else
                            eb.setDescription("You won the coinflip, the :coin: landed on number!");

                    } else {
                        preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                        preparedStatement.setLong(1, rs.getLong("money") - amount);
                        preparedStatement.setString(2, member.getId());

                        preparedStatement.executeUpdate();

                        if (args[2].equalsIgnoreCase("doge"))
                            eb.setDescription("You lost the coinflip, the :coin: landed on number!");
                        else
                            eb.setDescription("You lost the coinflip, the :coin: landed on doge!");

                    }
                }
            } else
                eb.setDescription("`" + CommandListener.prefix + "coinflip [money] [doge/number]`");

            channel.sendMessage(eb.build()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
