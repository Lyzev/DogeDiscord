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

public class PayCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {

        String[] args = message.getContentDisplay().split(" ");

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Pay");

        boolean success = false;

        if (args.length == 3) {

            if (message.getMentionedMembers().isEmpty())
                eb.setDescription("You didn't provide a user...\nType `" + CommandListener.prefix + "pay (@user) (money)`!");
            else {

                try {

                    try {

                        success = true;

                        long money = Long.parseLong(args[2]);

                        PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                        preparedStatement.setString(1, member.getId());

                        ResultSet rs = preparedStatement.executeQuery();

                        if (rs.isClosed()) {
                            preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO economy VALUES (?, ?)");

                            preparedStatement.setString(1, member.getId());
                            preparedStatement.setInt(2, 0);

                            preparedStatement.executeUpdate();

                            preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                            preparedStatement.setString(1, member.getId());

                            rs = preparedStatement.executeQuery();
                        }

                        if (money > rs.getLong("money"))
                            eb.setDescription("You don't have enough money...\nType `" + CommandListener.prefix + "pay (@user) (money)`!");
                        else {

                            preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                            preparedStatement.setString(1, message.getMentionedMembers().get(0).getId());

                            rs = preparedStatement.executeQuery();

                            if (rs.isClosed())
                                eb.setDescription("The specified user does not have a bank account yet!\nType `" + CommandListener.prefix + "pay (@user) (money)`!");
                            else {

                                eb.setDescription("You payed " + message.getMentionedMembers().get(0).getAsMention() + " `" + money + "$`!");

                                long money1 = money;

                                money = rs.getLong("money") + money;

                                preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                                preparedStatement.setLong(1, money);
                                preparedStatement.setString(2, message.getMentionedMembers().get(0).getId());

                                preparedStatement.executeUpdate();

                                preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                                preparedStatement.setString(1, member.getId());

                                rs = preparedStatement.executeQuery();

                                money1 = rs.getLong("money") - money1;

                                preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                                preparedStatement.setLong(1, money1);
                                preparedStatement.setString(2, member.getId());

                                preparedStatement.executeUpdate();

                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } catch (Exception ignored) {
                    eb.setDescription("You didn't provide a amount of money...\nType `" + CommandListener.prefix + "pay (@user) (money)`!");
                    channel.sendMessage(eb.build()).queue();
                    return;
                }

            }


        } else
            eb.setDescription("You need to provide a user and a amount of money!\nType `" + CommandListener.prefix + "pay (@user) (money)`!");


        channel.sendMessage(eb.build()).queue();

        if (success) {
            if (DogeDiscord.premium.contains(channel.getGuild().getId()))
                CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "pay");
            else
                CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "pay");
        } else {
            if (DogeDiscord.premium.contains(channel.getGuild().getId()))
                CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "pay");
            else
                CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "pay");
        }

    }
}
