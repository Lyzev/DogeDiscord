package eu.lyzev.manage;

import eu.lyzev.commands.ServerCommand;
import eu.lyzev.commands.types.animals.*;
import eu.lyzev.commands.types.botOwner.BackupCommand;
import eu.lyzev.commands.types.botOwner.BotCommand;
import eu.lyzev.commands.types.botOwner.DatabankCommand;
import eu.lyzev.commands.types.botOwner.WebhookCommand;
import eu.lyzev.commands.types.economy.*;
import eu.lyzev.commands.types.economy.Shop.InventoryCommand;
import eu.lyzev.commands.types.economy.Shop.ShopCommand;
import eu.lyzev.commands.types.fun.*;
import eu.lyzev.commands.types.gaming.*;
import eu.lyzev.commands.types.utility.*;
import eu.lyzev.listener.CaptchaListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
    public ConcurrentHashMap<String, ServerCommand> commands;

    public CommandManager() {

        this.commands = new ConcurrentHashMap<>();

        this.commands.put("help", new HelpCommand());

        this.commands.put("invite", new InviteCommand());

        this.commands.put("money", new MoneyCommand());

        this.commands.put("beg", new BegCommand());

        this.commands.put("bj", new BlackjackCommand());
        this.commands.put("blackjack", new BlackjackCommand());

        this.commands.put("bot", new BotCommand());

        this.commands.put("joke", new JokeCommand());

        this.commands.put("animal", new AnimalCommand());

        this.commands.put("dog", new DogCommand());

        this.commands.put("cat", new CatCommand());

        this.commands.put("bunny", new BunnyCommand());

        this.commands.put("duck", new DuckCommand());

        this.commands.put("fox", new FoxCommand());

        this.commands.put("owl", new OwlCommand());

        this.commands.put("shiba", new ShibaCommand());

        this.commands.put("lizard", new LizardCommand());

        this.commands.put("meme", new MemeCommand());

        this.commands.put("roast", new RoastCommand());

        this.commands.put("google", new GoogleCommand());

        this.commands.put("daily", new DailyCommand());

        this.commands.put("clap", new ClapCommand());

        this.commands.put("mojang", new MojangCommand());

        this.commands.put("skin", new SkinCommand());

        this.commands.put("emojify", new EmojifyCommand());

        this.commands.put("mcserver", new McServerCommand());

        this.commands.put("weekly", new WeeklyCommand());

        this.commands.put("showerthoughts", new ShowerthoughtsCommand());

        this.commands.put("lenny", new LennyCommand());

        this.commands.put("owo", new OwoCommand());

        this.commands.put("spoiler", new SpoilerCommand());

        this.commands.put("giphy", new GiphyCommand());

        this.commands.put("search", new SearchCommand());

        this.commands.put("pay", new PayCommand());

        this.commands.put("baltop", new BaltopCommand());

        this.commands.put("highlow", new HighLowCommand());

        this.commands.put("shop", new ShopCommand());

        this.commands.put("db", new DatabankCommand());

        this.commands.put("kick", new KickCommand());
        this.commands.put("ban", new BanCommand());

        this.commands.put("bonk", new BonkCommand());

        this.commands.put("dogecoin", new DogecoinCommand());

        this.commands.put("transfer", new TransferCommand());

        this.commands.put("webhook", new WebhookCommand());

        this.commands.put("backup", new BackupCommand());

        this.commands.put("user", new UserCommand());
        this.commands.put("guild", new GuildCommand());

        this.commands.put("reddit", new RedditCommand());

        this.commands.put("minecraft", new MinecraftCommand());

        this.commands.put("pubg", new PubGCommand());

        this.commands.put("valorant", new ValorantCommand());

        this.commands.put("rocketleague", new RocketLeagueCommand());

        this.commands.put("r6", new R6Command());

        this.commands.put("coinflip", new CoinflipCommand());

        this.commands.put("calc", new CalculatorCommand());

        this.commands.put("inv", new InventoryCommand());
        this.commands.put("inventory", new InventoryCommand());

        // this.commands.put("command", new Command());
    }

    public void perform(String command, Member member, TextChannel channel, Message message) {

        ServerCommand cmd;
        if ((cmd = this.commands.get(command.toLowerCase())) != null) {

            if (DogeAPI.random(1, 5) == 3) {
                try {
                    String id = member.getId();

                    PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                    preparedStatement.setString(1, id);

                    ResultSet rs = preparedStatement.executeQuery();

                    if (rs.isClosed()) {
                        preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO bank VALUES (?, ?, ?)");

                        preparedStatement.setString(1, id);
                        preparedStatement.setLong(2, 0);
                        preparedStatement.setLong(3, 0);

                        preparedStatement.executeUpdate();

                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM bank WHERE id = ?");

                        preparedStatement.setString(1, id);

                        rs = preparedStatement.executeQuery();
                    }

                    preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE bank SET max = ? WHERE id = ?");

                    preparedStatement.setLong(1, (rs.getLong("max") + DogeAPI.random(5, 25)));
                    preparedStatement.setString(2, id);

                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (cmd.equals(this.commands.get("beg")) || cmd.equals(this.commands.get("search")) || cmd.equals(this.commands.get("highlow")))
                if (CaptchaListener.captcha.get(member.getId()) != null && CaptchaListener.captcha.get(member.getId()) > 100)
                    CaptchaListener.INSTANCE().createCaptcha(channel, member.getUser());
                else if (CaptchaListener.captcha.get(member.getId()) != null)
                    CaptchaListener.captcha.replace(member.getId(), CaptchaListener.captcha.get(member.getId()) + 1);
                else
                    CaptchaListener.captcha.put(member.getId(), 1L);

            cmd.performCommand(member, channel, message);

        }

    }
}
