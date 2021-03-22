package eu.lyzev.commands.types.economy.Shop;

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

public class InventoryCommand implements ServerCommand {

    private static final HashMap<String, Long> pizza = new HashMap<>();
    private static final HashMap<String, Long> waterBottle = new HashMap<>();
    private static final HashMap<String, Long> gameBoy = new HashMap<>();
    private static final HashMap<String, Long> human = new HashMap<>();
    private static final HashMap<String, Long> coinBomb = new HashMap<>();
    private static final HashMap<String, Long> robbery = new HashMap<>();
    public static HashMap<String, Double> multi = new HashMap<>();

    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        boolean success = false;

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Inventory");

        String[] args = message.getContentDisplay().split(" ");

        try {

            if ((args.length == 4 && args[1].equalsIgnoreCase("use") && !message.getMentionedMembers().isEmpty()) || (args.length == 3 && args[1].equalsIgnoreCase("use"))) {

                try {
                    ItemList item = ItemList.valueOf(args[2].toUpperCase());

                    PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM items WHERE id = ? AND item = ?");

                    preparedStatement.setString(1, member.getId());
                    preparedStatement.setString(2, item.name());

                    ResultSet rs = preparedStatement.executeQuery();

                    if (!rs.isClosed()) {

                        success = true;

                        switch (item) {
                            case PIZZA:
                                if (pizza.get(member.getId()) == null || pizza.get(member.getId()) < System.currentTimeMillis()) {
                                    pizza.put(member.getId(), System.currentTimeMillis() + (1000 * 60 * 5));

                                    int random = DogeAPI.random(1, 110);
                                    if (random < 61) {
                                        eb.setDescription("You ate a pizza which is why you are happy now!\nYou earn 1.5x more money because you are happy!");
                                        eb.setFooter("The effect remains for 2min and the shop sales, blackjack and daily/weekly reward aren't affected.");
                                        addBoost(0.5, 2, member.getId());

                                        if (rs.getLong("amount") == 1) {
                                            preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                            preparedStatement.setString(1, member.getId());
                                            preparedStatement.setString(2, item.name());
                                            preparedStatement.executeUpdate();
                                        } else {
                                            preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE items SET amount = ? WHERE id = ? AND item = ?");

                                            preparedStatement.setLong(1, (rs.getLong("amount") - 1));
                                            preparedStatement.setString(2, member.getId());
                                            preparedStatement.setString(3, item.name());

                                            preparedStatement.executeUpdate();
                                        }
                                    } else if (random < 109)
                                        eb.setDescription("You aren't hungry at the moment!");
                                    else {
                                        eb.setDescription("You lost all your pizzas...");

                                        preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                        preparedStatement.setString(1, member.getId());
                                        preparedStatement.setString(2, item.name());
                                        preparedStatement.executeUpdate();
                                    }

                                } else
                                    eb.setDescription("Don't abuse the pizza...you can use it in `" + ((pizza.get(member.getId()) - System.currentTimeMillis()) / 60000) + "min` again!");
                                break;
                            case WATER_BOTTLE:
                                if (waterBottle.get(member.getId()) == null || waterBottle.get(member.getId()) < System.currentTimeMillis()) {
                                    waterBottle.put(member.getId(), System.currentTimeMillis() + (1000 * 60 * 5));

                                    int random = DogeAPI.random(1, 110);
                                    if (random < 61) {
                                        eb.setDescription("You drank a water bottle empty and you are now hydrated!\nYou earn 1.5x more money because you are hydrated!");
                                        eb.setFooter("The effect remains for 2min and the shop sales, blackjack and daily/weekly reward aren't affected.");
                                        addBoost(0.5, 2, member.getId());

                                        if (rs.getLong("amount") == 1) {
                                            preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                            preparedStatement.setString(1, member.getId());
                                            preparedStatement.setString(2, item.name());
                                            preparedStatement.executeUpdate();
                                        } else {
                                            preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE items SET amount = ? WHERE id = ? AND item = ?");

                                            preparedStatement.setLong(1, (rs.getLong("amount") - 1));
                                            preparedStatement.setString(2, member.getId());
                                            preparedStatement.setString(3, item.name());

                                            preparedStatement.executeUpdate();
                                        }
                                    } else if (random < 109)
                                        eb.setDescription("You just did a bottle flip!");
                                    else {
                                        eb.setDescription("You lost all your water bottles...");

                                        preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                        preparedStatement.setString(1, member.getId());
                                        preparedStatement.setString(2, item.name());
                                        preparedStatement.executeUpdate();
                                    }

                                } else
                                    eb.setDescription("Don't abuse the water bottle...you can use it in `" + ((waterBottle.get(member.getId()) - System.currentTimeMillis()) / 60000) + "min` again!");
                                break;
                            case ROBBERY:

                                if (message.getMentionedMembers().isEmpty() || message.getContentDisplay().split(" ").length != 4 || (!message.getMentionedMembers().isEmpty() && message.getMentionedMembers().get(0).getId().equals(member.getId()))) {
                                    eb.setDescription("`" + CommandListener.prefix + "inv use robbery [member]`");
                                } else {

                                    if (message.getMentionedMembers().get(0).getId().equalsIgnoreCase(DogeDiscord.ownerId)) {
                                        eb.setDescription("You can't rob the owner of the bot...");
                                        channel.sendMessage(eb.build()).queue();

                                        CommandListener.INSTANCE().addDelay(member.getId(), 15, CommandListener.prefix + "inv");
                                        CommandListener.INSTANCE().addDelay(member.getId(), 15, CommandListener.prefix + "inventory");
                                        return;
                                    }

                                    preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                                    preparedStatement.setString(1, message.getMentionedMembers().get(0).getId());

                                    rs = preparedStatement.executeQuery();

                                    if (rs.isClosed()) {
                                        eb.setDescription("This member doesn't even have a purse...");
                                    } else if (rs.getLong("money") < 1) {
                                        eb.setDescription("This member is too broke to steal him money...");
                                    } else {
                                        if (robbery.get(member.getId()) == null || robbery.get(member.getId()) < System.currentTimeMillis()) {
                                            robbery.put(member.getId(), System.currentTimeMillis() + (1000 * 60 * 45));

                                            int rob = DogeAPI.random(1, 200);
                                            if (rob < 161) {
                                                int random;
                                                if (rs.getLong("money") > 5000)
                                                    random = DogeAPI.random(1, 5000);
                                                else
                                                    random = DogeAPI.random(1, (int) rs.getLong("money"));
                                                eb.setDescription("The robbery was successfully, you stole " + message.getMentionedMembers().get(0).getAsMention() + " `" + random + "$`!");

                                                preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                                                preparedStatement.setLong(1, (rs.getLong("money") - random));
                                                preparedStatement.setString(2, message.getMentionedMembers().get(0).getId());

                                                preparedStatement.executeUpdate();

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

                                                preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                                                preparedStatement.setLong(1, (rs.getLong("money") + random));
                                                preparedStatement.setString(2, member.getId());

                                                preparedStatement.executeUpdate();

                                                preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM items WHERE id = ? AND item = ?");

                                                preparedStatement.setString(1, member.getId());
                                                preparedStatement.setString(2, item.name());

                                                rs = preparedStatement.executeQuery();

                                                if (rs.getLong("amount") == 1) {
                                                    preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                                    preparedStatement.setString(1, member.getId());
                                                    preparedStatement.setString(2, item.name());
                                                    preparedStatement.executeUpdate();
                                                } else {
                                                    preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE items SET amount = ? WHERE id = ? AND item = ?");

                                                    preparedStatement.setLong(1, (rs.getLong("amount") - 1));
                                                    preparedStatement.setString(2, member.getId());
                                                    preparedStatement.setString(3, item.name());

                                                    preparedStatement.executeUpdate();
                                                }
                                            } else if (rob < 200) {
                                                preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM items WHERE id = ? AND item = ?");

                                                preparedStatement.setString(1, member.getId());
                                                preparedStatement.setString(2, item.name());

                                                rs = preparedStatement.executeQuery();

                                                if (rs.getLong("amount") == 1) {
                                                    preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                                    preparedStatement.setString(1, member.getId());
                                                    preparedStatement.setString(2, item.name());
                                                    preparedStatement.executeUpdate();
                                                } else {
                                                    preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE items SET amount = ? WHERE id = ? AND item = ?");

                                                    preparedStatement.setLong(1, (rs.getLong("amount") - 1));
                                                    preparedStatement.setString(2, member.getId());
                                                    preparedStatement.setString(3, item.name());

                                                    preparedStatement.executeUpdate();
                                                }

                                                eb.setDescription("The robbery on " + message.getMentionedMembers().get(0).getAsMention() + " wasn't successfully!");
                                            } else {
                                                eb.setDescription("You lost all your robbery items...");

                                                preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                                preparedStatement.setString(1, member.getId());
                                                preparedStatement.setString(2, item.name());
                                                preparedStatement.executeUpdate();
                                            }

                                        } else
                                            eb.setDescription("Don't abuse the robbery item...you can use it in `" + ((robbery.get(member.getId()) - System.currentTimeMillis()) / 60000) + "min` again!");
                                    }
                                }
                                break;
                            case GAME_BOY:
                                if (gameBoy.get(member.getId()) == null || gameBoy.get(member.getId()) < System.currentTimeMillis()) {
                                    gameBoy.put(member.getId(), System.currentTimeMillis() + (1000 * 60 * 15));

                                    int random = DogeAPI.random(1, 110);
                                    if (random < 61) {
                                        long money = DogeAPI.random(150 * ((int) rs.getLong("amount")), 350 * ((int) rs.getLong("amount")));

                                        eb.setDescription("You won a game and earned `" + money + "$`!");

                                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                                        preparedStatement.setString(1, member.getId());

                                        rs = preparedStatement.executeQuery();

                                        if (rs.isClosed()) {
                                            preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO economy VALUES (?, ?)");

                                            preparedStatement.setString(1, member.getId());
                                            preparedStatement.setLong(2, money);

                                            preparedStatement.executeUpdate();
                                        } else {
                                            money = money + rs.getLong("money");

                                            preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                                            preparedStatement.setLong(1, money);
                                            preparedStatement.setString(2, member.getId());

                                            preparedStatement.executeUpdate();
                                        }
                                    } else if (random < 109)
                                        eb.setDescription("You just played a game and relaxed!");
                                    else {
                                        eb.setDescription("Your game boy is broken now because you raged in fifa...");

                                        preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                        preparedStatement.setString(1, member.getId());
                                        preparedStatement.setString(2, item.name());
                                        preparedStatement.executeUpdate();
                                    }

                                } else
                                    eb.setDescription("Don't abuse the game boy...you can use it in `" + ((gameBoy.get(member.getId()) - System.currentTimeMillis()) / 60000) + "min` again!");
                                break;
                            case HUMAN:
                                if (human.get(member.getId()) == null || human.get(member.getId()) < System.currentTimeMillis()) {
                                    human.put(member.getId(), System.currentTimeMillis() + (1000 * 60 * 45));

                                    int random = DogeAPI.random(1, 110);
                                    if (random < 109) {
                                        long money = DogeAPI.random(350 * ((int) rs.getLong("amount")), 550 * ((int) rs.getLong("amount")));

                                        eb.setDescription(DogeAPI.randomPhraseHuman().replace("%money%", "`" + money + "$`"));

                                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                                        preparedStatement.setString(1, member.getId());

                                        rs = preparedStatement.executeQuery();

                                        if (rs.isClosed()) {
                                            preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO economy VALUES (?, ?)");

                                            preparedStatement.setString(1, member.getId());
                                            preparedStatement.setLong(2, money);

                                            preparedStatement.executeUpdate();
                                        } else {
                                            money = money + rs.getLong("money");

                                            preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                                            preparedStatement.setLong(1, money);
                                            preparedStatement.setString(2, member.getId());

                                            preparedStatement.executeUpdate();
                                        }
                                    } else {
                                        eb.setDescription("All of your humans died because of the hard work...");

                                        preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                        preparedStatement.setString(1, member.getId());
                                        preparedStatement.setString(2, item.name());
                                        preparedStatement.executeUpdate();
                                    }

                                } else
                                    eb.setDescription("Don't abuse your humans...you can use them in `" + ((human.get(member.getId()) - System.currentTimeMillis()) / 60000) + "min` again!");
                                break;
                            case COIN_BOMB:
                                if (coinBomb.get(member.getId()) == null || coinBomb.get(member.getId()) < System.currentTimeMillis()) {
                                    eb.setDescription("You detonated a coin bomb! Every user on this server received money, which is why you are now popular!");

                                    coinBomb.put(member.getId(), System.currentTimeMillis() + (1000 * 60 * 5));

                                    if (rs.getLong("amount") == 1) {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                        preparedStatement.setString(1, member.getId());
                                        preparedStatement.setString(2, item.name());
                                        preparedStatement.executeUpdate();
                                    } else {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE items SET amount = ? WHERE id = ? AND item = ?");

                                        preparedStatement.setLong(1, (rs.getLong("amount") - 1));
                                        preparedStatement.setString(2, member.getId());
                                        preparedStatement.setString(3, item.name());

                                        preparedStatement.executeUpdate();
                                    }

                                    for (Member m : member.getGuild().getMembers()) {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                                        preparedStatement.setString(1, m.getId());

                                        rs = preparedStatement.executeQuery();

                                        if (!rs.isClosed()) {
                                            preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                                            preparedStatement.setLong(1, (rs.getLong("money") + DogeAPI.random(70, 130)));
                                            preparedStatement.setString(2, m.getId());

                                            preparedStatement.executeUpdate();
                                        }
                                    }

                                } else
                                    eb.setDescription("Don't abuse coin bombs...you can use it in `" + ((pizza.get(member.getId()) - System.currentTimeMillis()) / 60000) + "min` again!");
                                break;
                            case XL_COIN_BOMB:
                                if (coinBomb.get(member.getId()) == null || coinBomb.get(member.getId()) < System.currentTimeMillis()) {
                                    eb.setDescription("You detonated a xl coin bomb! Every user on this server received money, which is why you are now popular!");

                                    coinBomb.put(member.getId(), System.currentTimeMillis() + (1000 * 60 * 5));

                                    if (rs.getLong("amount") == 1) {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                        preparedStatement.setString(1, member.getId());
                                        preparedStatement.setString(2, item.name());
                                        preparedStatement.executeUpdate();
                                    } else {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE items SET amount = ? WHERE id = ? AND item = ?");

                                        preparedStatement.setLong(1, (rs.getLong("amount") - 1));
                                        preparedStatement.setString(2, member.getId());
                                        preparedStatement.setString(3, item.name());

                                        preparedStatement.executeUpdate();
                                    }

                                    for (Member m : member.getGuild().getMembers()) {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                                        preparedStatement.setString(1, m.getId());

                                        rs = preparedStatement.executeQuery();

                                        if (!rs.isClosed()) {
                                            preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                                            preparedStatement.setLong(1, (rs.getLong("money") + DogeAPI.random(470, 530)));
                                            preparedStatement.setString(2, m.getId());

                                            preparedStatement.executeUpdate();
                                        }
                                    }

                                } else
                                    eb.setDescription("Don't abuse xl coin bombs...you can use it in `" + ((pizza.get(member.getId()) - System.currentTimeMillis()) / 60000) + "min` again!");
                                break;
                            case XXL_COIN_BOMB:
                                if (coinBomb.get(member.getId()) == null || coinBomb.get(member.getId()) < System.currentTimeMillis()) {
                                    eb.setDescription("You detonated a xxl coin bomb! Every user on this server received money, which is why you are now popular!");

                                    coinBomb.put(member.getId(), System.currentTimeMillis() + (1000 * 60 * 5));

                                    if (rs.getLong("amount") == 1) {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");
                                        preparedStatement.setString(1, member.getId());
                                        preparedStatement.setString(2, item.name());
                                        preparedStatement.executeUpdate();
                                    } else {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE items SET amount = ? WHERE id = ? AND item = ?");

                                        preparedStatement.setLong(1, (rs.getLong("amount") - 1));
                                        preparedStatement.setString(2, member.getId());
                                        preparedStatement.setString(3, item.name());

                                        preparedStatement.executeUpdate();
                                    }

                                    for (Member m : member.getGuild().getMembers()) {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                                        preparedStatement.setString(1, m.getId());

                                        rs = preparedStatement.executeQuery();

                                        if (!rs.isClosed()) {
                                            preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                                            preparedStatement.setLong(1, (rs.getLong("money") + DogeAPI.random(700, 1300)));
                                            preparedStatement.setString(2, m.getId());

                                            preparedStatement.executeUpdate();
                                        }
                                    }

                                } else
                                    eb.setDescription("Don't abuse xxl coin bombs...you can use it in `" + ((coinBomb.get(member.getId()) - System.currentTimeMillis()) / 60000) + "min` again!");
                                break;
                            case DOGECOIN:
                            case DOGE:
                                eb.setDescription(item.getDescription());
                                break;
                        }

                    } else
                        eb.setDescription("You don't own this item...");

                } catch (Exception ignored) {
                    eb.setDescription("That's not an item...");
                    channel.sendMessage(eb.build()).queue();
                    return;
                }

            } else {
                eb.setFooter("Type " + CommandListener.prefix + "inv use [item-id] to use an item!");

                PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM items WHERE id = ?");

                preparedStatement.setString(1, member.getId());

                ResultSet rs = preparedStatement.executeQuery();

                if (rs.isClosed())
                    eb.setDescription("Empty...");
                else
                    while (rs.next())
                        eb.addField(ItemList.valueOf(rs.getString("item")).getName(), "`" + ItemList.valueOf(rs.getString("item")).getDescription() + "`\n`Amount: " + rs.getLong("amount") + "`\n`ID: " + rs.getString("item") + "`", false);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (success) {
            if (DogeDiscord.premium.contains(channel.getGuild().getId())) {
                CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "inv");
                CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "inventory");
            } else {
                CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "inv");
                CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "inventory");
            }
        } else {
            if (DogeDiscord.premium.contains(channel.getGuild().getId())) {
                CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "inv");
                CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "inventory");
            } else {
                CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "inv");
                CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "inventory");
            }
        }

        channel.sendMessage(eb.build()).queue();
    }

    private void addBoost(double addMulti, long time, String id) {
        if (multi.get(id) == null)
            multi.put(id, addMulti + 1);
        else
            multi.replace(id, multi.get(id) + addMulti);

        new Thread(() -> {
            try {
                Thread.sleep(time * 1000 * 60);
                if (multi.get(id) != null)
                    if (multi.get(id) == addMulti || multi.get(id) == (addMulti + 1))
                        multi.remove(id);
                    else
                        multi.replace(id, multi.get(id) - addMulti);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
