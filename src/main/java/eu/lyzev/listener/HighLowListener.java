package eu.lyzev.listener;

import eu.lyzev.commands.types.economy.HighLowCommand;
import eu.lyzev.commands.types.economy.Shop.InventoryCommand;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class HighLowListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT) || (!event.getMessage().getContentDisplay().equalsIgnoreCase("h") && !event.getMessage().getContentDisplay().equalsIgnoreCase("l") && !event.getMessage().getContentDisplay().equalsIgnoreCase("j")) || HighLowCommand.highLow.get(Objects.requireNonNull(event.getMember()).getId()) == null)
            return;

        long hintNumber = Long.parseLong(HighLowCommand.highLow.get(event.getMember().getId()).split(" ")[0]);
        long hiddenNumber = Long.parseLong(HighLowCommand.highLow.get(event.getMember().getId()).split(" ")[1]);

        HighLowCommand.highLow.remove(event.getMember().getId());

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge High-Low");

        if ((event.getMessage().getContentDisplay().equalsIgnoreCase("h") && hintNumber < hiddenNumber) || (event.getMessage().getContentDisplay().equalsIgnoreCase("l") && hintNumber > hiddenNumber) || (event.getMessage().getContentDisplay().equalsIgnoreCase("j") && hintNumber == hiddenNumber)) {

            long money;

            if (DogeAPI.random(0, 5) == 2)
                money = DogeAPI.random(50, 100);
            else
                money = DogeAPI.random(1, 50);

            if (InventoryCommand.multi.get(event.getMember().getId()) != null)
                money = (long) (money * InventoryCommand.multi.get(event.getMember().getId()));

            try {
                PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                preparedStatement.setString(1, event.getMember().getId());

                ResultSet rs = preparedStatement.executeQuery();

                if (rs.isClosed()) {
                    preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO bank VALUES (?, ?, ?)");

                    preparedStatement.setString(1, event.getMember().getId());
                    preparedStatement.setLong(2, 0);
                    preparedStatement.setLong(3, 0);

                    preparedStatement.executeUpdate();

                    preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                    preparedStatement.setString(1, event.getMember().getId());

                    rs = preparedStatement.executeQuery();
                }

                preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE bank SET max = ? WHERE id = ?");

                preparedStatement.setLong(1, (rs.getLong("max") + (money / 4)));
                preparedStatement.setString(2, event.getMember().getId());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            eb.setDescription("**You won `" + money + "$`!**\nYour hint was **" + hintNumber + "**. The hidden number was **" + hiddenNumber + "**.");

            try {

                PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                preparedStatement.setString(1, event.getMember().getId());

                ResultSet rs = preparedStatement.executeQuery();

                if (rs.isClosed()) {
                    preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO economy VALUES (?, ?)");

                    preparedStatement.setString(1, event.getMember().getId());
                    preparedStatement.setLong(2, 0);

                    preparedStatement.executeUpdate();

                    preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                    preparedStatement.setString(1, event.getMember().getId());

                    rs = preparedStatement.executeQuery();
                }

                money = money + rs.getLong("money");

                preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                preparedStatement.setLong(1, money);
                preparedStatement.setString(2, event.getMember().getId());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else
            eb.setDescription("**You lost!**\nYour hint was **" + hintNumber + "**. The hidden number was **" + hiddenNumber + "**.");

        event.getTextChannel().sendMessage(eb.build()).queue();
    }
}
