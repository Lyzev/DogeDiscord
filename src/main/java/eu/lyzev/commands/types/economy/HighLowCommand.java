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

public class HighLowCommand implements ServerCommand {

    public static HashMap<String, String> highLow = new HashMap<>();

    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 30, CommandListener.prefix + "highlow");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 45, CommandListener.prefix + "highlow");

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

        long hiddenNumber = DogeAPI.random(1, 100);
        long hintNumber = DogeAPI.random(40, 60);

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge High-Low");
        eb.setDescription("There is a hidden number between 1-100. Your hint is **" + hintNumber + "**!\nType `h` for **higher**, `l` for **lower** and `j` for **jackpot**.");
        eb.setFooter("Higher: hint < hidden, Lower: hint > hidden, Jackpot: hint = hidden");
        channel.sendMessage(eb.build()).queue();

        highLow.put(member.getId(), hintNumber + " " + hiddenNumber);

        new Thread(() -> {
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (highLow.get(member.getId()) != null) {
                highLow.remove(member.getId());
                channel.sendMessage("You have to respond, I guess that's common sense...").queue();
            }
        }).start();

    }
}
