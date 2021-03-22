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

public class MoneyCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Money");

        if (!message.getMentionedMembers().isEmpty() || message.getContentDisplay().split(" ").length == 1) {
            if (DogeDiscord.premium.contains(channel.getGuild().getId()))
                CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "money");
            else
                CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "money");

            try {

                boolean mention = false;

                if (!message.getMentionedMembers().isEmpty()) {
                    mention = true;
                    member = message.getMentionedMembers().get(0);
                }

                PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                preparedStatement.setString(1, member.getId());

                ResultSet rs = preparedStatement.executeQuery();

                if (rs.isClosed()) {

                    if (mention) {
                        eb.setDescription("The specified user does not have a purse yet!");
                        channel.sendMessage(eb.build()).queue();
                        return;
                    }

                    preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO economy VALUES (?, ?)");

                    preparedStatement.setString(1, member.getId());
                    preparedStatement.setLong(2, 0);

                    preparedStatement.executeUpdate();

                    preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                    preparedStatement.setString(1, member.getId());

                    rs = preparedStatement.executeQuery();
                }

                if (mention)
                    eb.setDescription(member.getUser().getAsTag() + " current balance is `" + rs.getLong("money") + "$`!");
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

                    long bank = rs.getLong("money");

                    long bankMax = rs.getLong("max");

                    eb.setDescription("**Purse:** " + purse + "$\n**Bank:** " + bank + "$/" + bankMax + "$");
                    eb.setFooter(CommandListener.prefix + "transfer [bank/purse] [amount/all]");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            if (DogeDiscord.premium.contains(channel.getGuild().getId()))
                CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "money");
            else
                CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "money");
            eb.setDescription("Type `" + CommandListener.prefix + "money` or `" + CommandListener.prefix + "money (@user)`!");
        }

        channel.sendMessage(eb.build()).queue();
    }
}
