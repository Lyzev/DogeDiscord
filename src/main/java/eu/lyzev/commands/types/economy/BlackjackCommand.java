package eu.lyzev.commands.types.economy;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import eu.lyzev.utils.blackjack.BlackjackManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BlackjackCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Blackjack");

        if (BlackjackManager.INSTANCE().playingBlackjack(member))
            eb.setDescription("You are already playing a game!\nType `e` to **end the game**.");
        else {

            String[] args = message.getContentDisplay().split(" ");

            if (args.length == 2) {

                try {
                    int amount = Integer.parseInt(args[1]);

                    PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                    preparedStatement.setString(1, member.getId());

                    ResultSet rs = preparedStatement.executeQuery();

                    if (rs.isClosed()) {
                        preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO economy VALUES (?, ?)");

                        preparedStatement.setString(1, member.getId());
                        preparedStatement.setInt(2, 0);

                        preparedStatement.executeUpdate();
                    }

                    preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                    preparedStatement.setString(1, member.getId());

                    rs = preparedStatement.executeQuery();

                    if (amount < 69)
                        eb.setDescription("You have to bet at least `69$`! Are you broke?");
                    else {
                        if (amount > rs.getInt("money"))
                            eb.setDescription("You don't have the bid money...\nMoney: `" + rs.getInt("money") + "$`");
                        else {
                            if (DogeDiscord.premium.contains(channel.getGuild().getId())) {
                                CommandListener.INSTANCE().addDelay(member.getId(), 30, CommandListener.prefix + "bj");
                                CommandListener.INSTANCE().addDelay(member.getId(), 30, CommandListener.prefix + "blackjack");
                            } else {
                                CommandListener.INSTANCE().addDelay(member.getId(), 45, CommandListener.prefix + "bj");
                                CommandListener.INSTANCE().addDelay(member.getId(), 45, CommandListener.prefix + "blackjack");
                            }

                            preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                            preparedStatement.setString(1, member.getId());

                            rs = preparedStatement.executeQuery();

                            int money = rs.getInt("money") - amount;

                            preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                            preparedStatement.setInt(1, money);
                            preparedStatement.setString(2, member.getId());

                            preparedStatement.executeUpdate();

                            BlackjackManager.INSTANCE().createGame(member, channel, amount, System.currentTimeMillis());
                            return;
                        }
                    }

                } catch (Exception ignored) {
                    eb.setDescription("Am I a joke to you!? That's not a number...\nType `r!help` for more info!");
                    channel.sendMessage(eb.build()).queue();
                    return;
                }

            } else
                eb.setDescription("Are you serious!? You need something to bet, that's commons sense!\nType `" + CommandListener.prefix + "bj (amount)`");

        }

        channel.sendMessage(eb.build()).queue();

        if (DogeDiscord.premium.contains(channel.getGuild().getId())) {
            CommandListener.INSTANCE().addDelay(member.getId(), 5, CommandListener.prefix + "bj");
            CommandListener.INSTANCE().addDelay(member.getId(), 5, CommandListener.prefix + "blackjack");
        } else {
            CommandListener.INSTANCE().addDelay(member.getId(), 10, CommandListener.prefix + "bj");
            CommandListener.INSTANCE().addDelay(member.getId(), 10, CommandListener.prefix + "blackjack");
        }
    }
}
