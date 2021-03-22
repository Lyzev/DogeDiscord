package eu.lyzev.main;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import com.github.lalyos.jfiglet.FigletFont;
import com.google.common.io.Files;
import eu.lyzev.listener.*;
import eu.lyzev.manage.CommandManager;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.redstonecraft.redstoneapi.sql.SQL;
import net.redstonecraft.redstoneapi.sql.SQLite;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.*;

/**
 * RimeDiscord.
 * This is the source code of rime-bot!
 *
 * @author Lyzev
 * @version v1
 * @date 05.02.2021
 */

public class DogeDiscord {

    public static boolean ready = false; // It will turn true when the bot succesfully logged in!
    public static int hex = 0;
    public static String ownerId = "434959223705829378";
    public static SQL sql; // The database of the bot!
    public static long dogecoin = 200L;
    public static List<String> premium = new ArrayList<>();
    private static DogeDiscord INSTANCE; // Instance of the main!
    private final CommandManager commandManager = new CommandManager(); // Command-Manager of the bot!
    private JDA shardMan; // Shard-Manager of the bot!
    public final static Logger logger = Logger.getLogger("logger");

    public static DogeDiscord getINSTANCE() { // The getter for the Instance!
        return INSTANCE;
    }

    /**
     * <p>This is the main method of the doge-bot!</p>
     * <p>Provide here the bot-token as a parameter!</p>
     *
     * @param args
     */
    public static void main(String[] args) {

        File loggerFolder = new File("./logs");
        if (!loggerFolder.exists())
            loggerFolder.mkdirs();

        for (File i : loggerFolder.listFiles()) {
            if (i.getName().contains("latest-log")) {
                try {
                    Files.copy(i, new File(i.getPath().replace("latest-log ", "")));
                    i.delete();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
        
        try {
            logger.setUseParentHandlers(false);
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
                    return Ansi.colorize("[DogeDiscord] [" + simpleDateFormat.format(new Date()) + "] [" + record.getLevel() + "] " + record.getMessage() + "\n", Attribute.RED_TEXT(), Attribute.BOLD());
                }
            });
            logger.addHandler(consoleHandler);
            
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
            FileHandler fileHandler = new FileHandler("./logs/latest-log " + simpleDateFormat.format(new Date()) + ".txt");
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
                    return "[DogeDiscord] [" + simpleDateFormat.format(new Date()) + "] [" + record.getLevel() + "] " + record.getMessage() + "\n";
                }
            });
            logger.addHandler(fileHandler);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        new DogeDiscord().sendConsole("Log was created/checked!");

        try {
            String[] msg1 = FigletFont.convertOneLine("Doge Bot").split("\\n");
            String[] msg2 = FigletFont.convertOneLine("By Lyzev").split("\\n");
            for (String i : msg1)
                logger.info(Ansi.colorize(i, Attribute.YELLOW_TEXT(), Attribute.BOLD()));
            for (String i : msg2)
                logger.info(Ansi.colorize(i, Attribute.YELLOW_TEXT(), Attribute.BOLD()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Control if the token is provided.
         * When it is provided then it will execute the {@link #dogeDiscord(String)}!
         */
        DogeDiscord dogeDiscord = new DogeDiscord();
        if (args.length > 0 && args[0].replace(" ", "").length() == 59) {
            try {
                dogeDiscord.dogeDiscord(args[0].replace(" ", ""));
            } catch (LoginException | InterruptedException e) {
                e.printStackTrace();
            }
        } else dogeDiscord.sendConsole("Please provide a bot token as parameter when you execute this programm!");

    }

    public JDA getJDA() { // The getter for the Shard-Manager!
        return shardMan;
    }

    public CommandManager getCommandManager() { // The getter for the Command-Manager!
        return commandManager;
    }

    /**
     * <p>In this method the bot will be started!</p>
     * <p>This method is executed by {@link #main(String[])}!</p>
     */
    private void dogeDiscord(String token) throws LoginException, InterruptedException {

        INSTANCE = this;
        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));

        try {
            if (!new File("./data").exists())
                new File("./data").mkdirs();
            sql = new SQLite(new File(new File("./data"), "data.db").getPath());
        } catch (SQLException | ClassNotFoundException sqlException) {
            sqlException.printStackTrace();
        }

        sql.update("CREATE TABLE IF NOT EXISTS economy (id text, money long)");

        sql.update("CREATE TABLE IF NOT EXISTS bank (id text, money long, max long)");

        sql.update("CREATE TABLE IF NOT EXISTS hourly (id text, time long)");

        sql.update("CREATE TABLE IF NOT EXISTS daily (id text, time long)");

        sql.update("CREATE TABLE IF NOT EXISTS weekly (id text, time long)");

        sql.update("CREATE TABLE IF NOT EXISTS items (id text, item text, amount long)");

        sql.update("CREATE TABLE IF NOT EXISTS premium (id text)");

        try {
            PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM premium");

            ResultSet rs = preparedStatement.executeQuery();

            if (!rs.isClosed())
                while (rs.next())
                    premium.add(rs.getString("id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        File backupFolder = new File("./backups");

        if (!backupFolder.exists())
            backupFolder.mkdirs();

        logger.info(Ansi.colorize("Database was checked/created!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));

        new Thread(() -> {
            try {
                autoBackup();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        JDABuilder builder = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_BANS, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setActivity(Activity.playing("Bot is starting..."));
        builder.setStatus(OnlineStatus.ONLINE);
        builder.addEventListeners(new CommandListener());
        builder.addEventListeners(new BlackjackListener());
        builder.addEventListeners(new SearchListener());
        builder.addEventListeners(new HighLowListener());
        builder.addEventListeners(new JoinLeaveListener());
        builder.addEventListeners(new CaptchaListener());
        // builder.addEventListeners(new Listener());

        try {
            shardMan = builder.build();
        } catch (LoginException ignored) {
            this.sendConsole("The provided token is invalid!");
            return;
        }

        Thread.sleep(5000);

        logger.info(Ansi.colorize("Bot online.", Attribute.YELLOW_TEXT(), Attribute.BOLD()));

        new Thread(() -> {
            try {
                dogecoin(1000 * 60 * 5);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                activity(new String[]{"mining Dogecoin!", "d!help", "Prefix: d!", "Dogecoin: %dogecoin%", "Support-Server: dc.eu.lyzev.eu", "on %guilds% Guilds.", "Bot-Invite: doge.eu.lyzev.eu"}, 5 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                CaptchaListener.INSTANCE().resetCaptcha();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        File captchaFolder = new File("./captcha");
        if (!captchaFolder.exists())
            captchaFolder.mkdirs();

        logger.info(Ansi.colorize("Captcha was checked!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));

        File mcserverFolder = new File("./mcserver");
        if (!mcserverFolder.exists())
            mcserverFolder.mkdirs();

        logger.info(Ansi.colorize("McServer-Folder was checked!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));

        ready = true;

        logger.info(Ansi.colorize("Bot is ready to respond!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
        logger.info(Ansi.colorize("Type 'help' to see all commands for the console!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    String input = scanner.nextLine().toLowerCase();
                    if (input.replace(" ", "").equals("help")) {
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("help - To see this message!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("stop - To stop the program!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("bot - To see informations about the bot!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("premium [list/add/remove] - To see/add/remove the premium-guilds!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("captcha [channel-id] [member-id] - To try the captcha!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("backup - To make a backup of all the data!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("invite - To get the invite link of the bot!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("dogecoin - To see the dogecoin price!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                    } else if (input.replace(" ", "").equals("stop")) {
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("The program stops!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        INSTANCE.getJDA().getPresence().setStatus(OnlineStatus.OFFLINE);
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        System.exit(0);
                    } else if (input.replace(" ", "").equals("bot")) {
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("Guilds: " + DogeDiscord.getINSTANCE().getJDA().getGuilds().size(), Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("Members: " + DogeDiscord.getINSTANCE().getJDA().getUsers().size(), Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("Text-Channels: " + DogeDiscord.getINSTANCE().getJDA().getTextChannels().size(), Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("Voice-Channels: " + DogeDiscord.getINSTANCE().getJDA().getVoiceChannels().size(), Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("RAM: " + ((double) Runtime.getRuntime().totalMemory() / 1024 / 1024) + "/" + ((double) Runtime.getRuntime().maxMemory() / 1024 / 1024) + "MB", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("Ping: " + DogeDiscord.getINSTANCE().getJDA().getGatewayPing() + "ms", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                    } else if (input.split(" ")[0].equals("premium")) {
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        if (input.split(" ").length > 1) {
                            if (input.split(" ")[1].equals("list")) {
                                try {
                                    String premium = "";
                                    PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM premium");
                                    ResultSet rs = preparedStatement.executeQuery();
                                    if (!rs.isClosed()) {
                                        int index = 0;
                                        while (rs.next()) {
                                            if (INSTANCE.getJDA().getGuildById(rs.getString("id")) != null) {
                                                if (index == 0)
                                                    premium = premium + INSTANCE.getJDA().getGuildById(rs.getString("id")).getName() + "[" + rs.getString("id") + "]";
                                                else
                                                    premium = premium + ", " + INSTANCE.getJDA().getGuildById(rs.getString("id")).getName() + "[" + rs.getString("id") + "]";
                                                index++;
                                            }
                                        }
                                    }
                                    if (premium.length() > 1)
                                        logger.info(Ansi.colorize(premium, Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                                    else
                                        logger.info(Ansi.colorize("There are no premium-guilds!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else if (input.split(" ")[1].equals("add") && input.split(" ").length == 3) {
                                boolean success = true;
                                try {
                                    Long.parseLong(input.split(" ")[2]);
                                } catch (Exception ignored) {
                                    success = false;
                                }
                                if (getJDA().getGuildById(input.split(" ")[2]) == null)
                                    success = false;
                                if (success) {
                                    if (!premium.contains(input.split(" ")[2])) {
                                        premium.add(input.split(" ")[2]);
                                        PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM premium WHERE id = ?");
                                        preparedStatement.setString(1, input.split(" ")[2]);
                                        ResultSet rs = preparedStatement.executeQuery();
                                        if (rs.isClosed()) {
                                            preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO premium VALUES (?)");
                                            preparedStatement.setString(1, input.split(" ")[2]);
                                            preparedStatement.executeUpdate();
                                        }
                                        logger.info(Ansi.colorize("This guild is now a premium guild!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                                    } else
                                        logger.info(Ansi.colorize("This guild is already a premium guild...", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                                } else
                                    logger.info(Ansi.colorize("That's not a guild-id...", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                            } else if (input.split(" ")[1].equals("remove") && input.split(" ").length == 3) {
                                boolean success = true;
                                try {
                                    Long.parseLong(input.split(" ")[2]);
                                } catch (Exception ignored) {
                                    success = false;
                                }
                                if (getJDA().getGuildById(input.split(" ")[2]) == null)
                                    success = false;
                                if (success) {
                                    if (premium.contains(input.split(" ")[2])) {
                                        premium.remove(input.split(" ")[2]);
                                        PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM premium WHERE id = ?");
                                        preparedStatement.setString(1, input.split(" ")[2]);
                                        preparedStatement.executeUpdate();
                                        logger.info(Ansi.colorize("This guild isn't a premium guild anymore!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                                    } else
                                        logger.info(Ansi.colorize("This guild isn't a premium guild...", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                                } else
                                    logger.info(Ansi.colorize("That's not a guild-id...", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                            } else
                                logger.info(Ansi.colorize("premium [list/add/remove]", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        } else
                            logger.info(Ansi.colorize("premium [list/add/remove]", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                    } else if (input.split(" ")[0].equals("captcha")) {
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        if (input.split(" ").length == 3 && getJDA().getTextChannelById(input.split(" ")[1]) != null && getJDA().getUserById(input.split(" ")[2]) != null) {
                            CaptchaListener.INSTANCE().createCaptcha(getJDA().getTextChannelById(input.split(" ")[1]), getJDA().getUserById(input.split(" ")[2]));
                            logger.info(Ansi.colorize("Captcha for " + getJDA().getUserById(input.split(" ")[2]).getAsTag() + " was created!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        } else
                            logger.info(Ansi.colorize("captcha [channel-id] [member-id]", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                    } else if (input.replace(" ", "").equals("backup")) {
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
                        File folder = new File("./backups");
                        if (!folder.exists())
                            folder.mkdirs();
                        File backupFolder2 = new File("./backups/" + sdf.format(cal.getTime()));
                        if (!backupFolder2.exists())
                            backupFolder2.mkdirs();
                        File dogecoinFolder = new File("./backups/" + sdf.format(cal.getTime()) + "/dogecoin");
                        if (!dogecoinFolder.exists())
                            dogecoinFolder.mkdirs();
                        File dogecoin = null;
                        for (File i : new File("./data/dogecoin").listFiles())
                            dogecoin = i;
                        if (dogecoin != null)
                            Files.copy(dogecoin, new File("./backups/" + sdf.format(cal.getTime()) + "/dogecoin/" + dogecoin.getName()));
                        Files.copy(new File("./data/data.db"), new File("./backups/" + sdf.format(cal.getTime()) + "/data.db"));
                        logger.info(Ansi.colorize("A backup of the database was created!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                    } else if (input.replace(" ", "").equals("invite")) {
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("https://doge.lyzev.eu/", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                    } else if (input.replace(" ", "").equals("dogecoin")) {
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("Dogecoin: " + dogecoin + "$", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                    } else if (input.length() > 0) {
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("This command doesn't exist, try 'help'!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
                    }
                } catch (Exception ignored) {}
            }
        }).start();
    }

    /**
     * <p>This is the updater of the bot activity</p>
     * <p>Note: Delay in ms!</p>
     * <p>For example it is used in {@link #dogeDiscord(String)}!</p>
     */
    private void activity(String[] activities, int delay) throws InterruptedException {
        while (true) {
            for (String i : activities) {
                DogeDiscord.INSTANCE.getJDA().getPresence().setActivity(Activity.playing(i.replace("%dogecoin%", dogecoin + "$").replace("%guilds%", String.valueOf(INSTANCE.getJDA().getGuilds().size()))));

                Thread.sleep(delay);
            }
        }
    }

    private void dogecoin(int delay) throws InterruptedException, IOException {

        File directory = new File("./data/dogecoin");
        if (!directory.exists())
            directory.mkdirs();

        if (directory.listFiles().length == 0) {
            new File("./data/dogecoin/200").createNewFile();
        } else {
            dogecoin = Long.parseLong(directory.listFiles()[0].getName());
        }


        while (true) {
            int randomAmount = DogeAPI.random(1, 35);
            int randomUpOrDown;
            if (dogecoin < 200)
                randomUpOrDown = DogeAPI.random(1, 3);
            else if (dogecoin > 200)
                randomUpOrDown = DogeAPI.random(2, 4);
            else
                randomUpOrDown = DogeAPI.random(1, 4);

            if (dogecoin - randomAmount < 150)
                dogecoin += randomAmount;
            else if (dogecoin + randomAmount > 400)
                dogecoin -= randomAmount;
            else if (randomUpOrDown < 3)
                dogecoin += randomAmount;
            else
                dogecoin -= randomAmount;


            for (File file : directory.listFiles())
                file.delete();

            new File("./data/dogecoin/" + dogecoin).createNewFile();

            Thread.sleep(delay);
        }
    }

    private void autoBackup() throws IOException, InterruptedException {
        while (true) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");

            File folder = new File("./backups");

            if (!folder.exists())
                folder.mkdirs();

            File backupFolder = new File("./backups/" + sdf.format(cal.getTime()));
            if (!backupFolder.exists())
                backupFolder.mkdirs();

            File dogecoinFolder = new File("./backups/" + sdf.format(cal.getTime()) + "/dogecoin");
            if (!dogecoinFolder.exists())
                dogecoinFolder.mkdirs();

            File dogecoin = null;
            for (File i : new File("./data/dogecoin").listFiles())
                dogecoin = i;

            if (dogecoin != null)
                Files.copy(dogecoin, new File("./backups/" + sdf.format(cal.getTime()) + "/dogecoin/" + dogecoin.getName()));

            Files.copy(new File("./data/data.db"), new File("./backups/" + sdf.format(cal.getTime()) + "/data.db"));

            logger.info(Ansi.colorize("A backup of the database was created!", Attribute.YELLOW_TEXT(), Attribute.BOLD()));

            Thread.sleep(1000 * 60 * 60 * 24);
        }
    }

    /**
     * <p>To send a text to the console separated by dashes from the other text in the console.</p>
     * <p>For example it is used in {@link #main(String[])}!</p>
     */
    private void sendConsole(String text) {
        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
        logger.info(Ansi.colorize(text, Attribute.YELLOW_TEXT(), Attribute.BOLD()));
        logger.info(Ansi.colorize("------------------------------", Attribute.YELLOW_TEXT(), Attribute.BOLD()));
    }

}