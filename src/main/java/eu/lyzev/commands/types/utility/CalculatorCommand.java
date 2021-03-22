package eu.lyzev.commands.types.utility;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstoneapi.tools.StringUtils;

import javax.script.ScriptEngineManager;

public class CalculatorCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, message.getContentDisplay().split(" ")[0].toLowerCase());
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, message.getContentDisplay().split(" ")[0].toLowerCase());
        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Calculator");
        eb.setFooter("NOTE: The calculator can't solve quations and terms and if you use something like π or 2^2, please write 3.141 or 2*2!");
        if (message.getContentDisplay().split(" ").length == 2) {
            try {
                if (StringUtils.isValid(message.getContentDisplay().split(" ")[1].replace(",", "."), "0123456789+-/*.,()[]".toCharArray()))
                    eb.setDescription(message.getContentDisplay().split(" ")[1].replace(",", ".") + " = " + new ScriptEngineManager().getEngineByName("JavaScript").eval(message.getContentDisplay().split(" ")[1].replace(",", ".")));
                else
                    eb.setDescription("This isn't a math expression, which can be solved by the bot, please try again!");
            } catch (Exception ignored) {
                eb.setDescription("This isn't a math expression, which can be solved by the bot, please try again!");
                channel.sendMessage(eb.build()).queue();
                return;
            }
        } else
            eb.setDescription("`" + CommandListener.prefix + "calc [math-expression]`");
        channel.sendMessage(eb.build()).queue();
    }
}
