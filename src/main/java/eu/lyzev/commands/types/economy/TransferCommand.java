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

public class TransferCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Transfer");
        eb.setFooter(CommandListener.prefix + "money");

        String[] args = message.getContentDisplay().split(" ");

        if (args.length == 3) {

            if (args[1].equalsIgnoreCase("bank")) {
                if (DogeDiscord.premium.contains(channel.getGuild().getId()))
                    CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "transfer");
                else
                    CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "transfer");

                try {
                    long amount;
                    if (args[2].equalsIgnoreCase("all")) {
                        PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                        preparedStatement.setString(1, member.getId());

                        ResultSet rs = preparedStatement.executeQuery();

                        if (rs.isClosed()) {

                            preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO bank VALUES (?, ?, ?)");

                            preparedStatement.setString(1, member.getId());
                            preparedStatement.setLong(2, 0);
                            preparedStatement.setLong(3, 0);

                            preparedStatement.executeUpdate();

                            preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                            preparedStatement.setString(1, member.getId());

                            rs = preparedStatement.executeQuery();
                        }

                        long free = rs.getLong("max") - rs.getLong("money");

                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                        preparedStatement.setString(1, member.getId());

                        rs = preparedStatement.executeQuery();

                        if (rs.isClosed()) {

                            preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO economy VALUES (?, ?)");

                            preparedStatement.setString(1, member.getId());
                            preparedStatement.setLong(2, 0);

                            preparedStatement.executeUpdate();

                            preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                            preparedStatement.setString(1, member.getId());

                            rs = preparedStatement.executeQuery();
                        }

                        if (free < rs.getLong("money"))
                            amount = free;
                        else
                            amount = rs.getLong("money");
                    } else {
                        amount = Long.parseLong(args[2]);
                    }

                    PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                    preparedStatement.setString(1, member.getId());

                    ResultSet rs = preparedStatement.executeQuery();

                    if (rs.isClosed()) {

                        preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO economy VALUES (?, ?)");

                        preparedStatement.setString(1, member.getId());
                        preparedStatement.setLong(2, 0);

                        preparedStatement.executeUpdate();

                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                        preparedStatement.setString(1, member.getId());

                        rs = preparedStatement.executeQuery();
                    }

                    if (amount > rs.getLong("money"))
                        eb.setDescription("You don't have enough of money in your purse...\nPurse: `" + rs.getLong("money") + "$`");
                    else {
                        long purse = rs.getLong("money");

                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                        preparedStatement.setString(1, member.getId());

                        rs = preparedStatement.executeQuery();

                        if (rs.isClosed()) {

                            preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO bank VALUES (?, ?, ?)");

                            preparedStatement.setString(1, member.getId());
                            preparedStatement.setLong(2, 0);
                            preparedStatement.setLong(3, 0);

                            preparedStatement.executeUpdate();

                            preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                            preparedStatement.setString(1, member.getId());

                            rs = preparedStatement.executeQuery();
                        }

                        if (amount > (rs.getLong("money") + rs.getLong("max")))
                            eb.setDescription("Your bank is too small...\nMax: `" + rs.getLong("max") + "$`\nFree: `" + (rs.getLong("max") - rs.getLong("money")) + "$`");
                        else {
                            long bank = rs.getLong("money");

                            preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                            preparedStatement.setLong(1, (purse - amount));
                            preparedStatement.setString(2, member.getId());

                            preparedStatement.executeUpdate();

                            preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE bank SET money = ? WHERE id = ?");

                            preparedStatement.setLong(1, (bank + amount));
                            preparedStatement.setString(2, member.getId());

                            preparedStatement.executeUpdate();

                            eb.setDescription("You successfully transfered `" + amount + "$` to your bank!");
                        }
                    }
                } catch (Exception ignored) {
                    eb.setDescription("That's not a number...");
                    channel.sendMessage(eb.build()).queue();
                    return;
                }
            } else if (args[1].equalsIgnoreCase("purse")) {
                if (DogeDiscord.premium.contains(channel.getId()))
                    CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "transfer");
                else
                    CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "transfer");

                try {
                    long amount;
                    if (args[2].equalsIgnoreCase("all")) {
                        PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                        preparedStatement.setString(1, member.getId());

                        ResultSet rs = preparedStatement.executeQuery();

                        amount = rs.getLong("money");
                    } else {
                        amount = Long.parseLong(args[2]);
                    }

                    PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                    preparedStatement.setString(1, member.getId());

                    ResultSet rs = preparedStatement.executeQuery();

                    if (rs.isClosed()) {

                        preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO bank VALUES (?, ?, ?)");

                        preparedStatement.setString(1, member.getId());
                        preparedStatement.setLong(2, 0);
                        preparedStatement.setLong(3, 0);

                        preparedStatement.executeUpdate();

                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                        preparedStatement.setString(1, member.getId());

                        rs = preparedStatement.executeQuery();
                    }

                    if (amount > rs.getLong("money"))
                        eb.setDescription("You don't have enough of money in your bank account...\nBank: `" + rs.getLong("money") + "$`");
                    else {
                        long bank = rs.getLong("money");

                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                        preparedStatement.setString(1, member.getId());

                        rs = preparedStatement.executeQuery();

                        if (rs.isClosed()) {

                            preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO economy VALUES (?, ?)");

                            preparedStatement.setString(1, member.getId());
                            preparedStatement.setLong(2, 0);

                            preparedStatement.executeUpdate();

                            preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                            preparedStatement.setString(1, member.getId());

                            rs = preparedStatement.executeQuery();
                        }

                        long purse = rs.getLong("money");

                        preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                        preparedStatement.setLong(1, (purse + amount));

                        preparedStatement.setString(2, member.getId());
                        preparedStatement.executeUpdate();

                        preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE bank SET money = ? WHERE id = ?");

                        preparedStatement.setLong(1, (bank - amount));
                        preparedStatement.setString(2, member.getId());

                        preparedStatement.executeUpdate();

                        eb.setDescription("You successfully transfered `" + amount + "$` to your purse!");
                    }
                } catch (Exception ignored) {
                    eb.setDescription("That's not a number...");
                    channel.sendMessage(eb.build()).queue();
                    return;
                }
            } else {
                if (DogeDiscord.premium.contains(channel.getId()))
                    CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "transfer");
                else
                    CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "transfer");
                eb.setDescription(CommandListener.prefix + "transfer [bank/purse] [amount/all]");
            }
        } else {
            if (DogeDiscord.premium.contains(channel.getGuild().getId()))
                CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "transfer");
            else
                CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "transfer");
            eb.setDescription(CommandListener.prefix + "transfer [bank/purse] [amount/all]");
        }

        channel.sendMessage(eb.build()).queue();
    }
}
