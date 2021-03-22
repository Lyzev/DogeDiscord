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

public class ShopCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {

        boolean success = false;

        String[] args = message.getContentDisplay().split(" ");

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Shop");

        if (args.length > 1) {

            if (args[1].equalsIgnoreCase("buy")) {

                if (args.length < 3) {
                    eb.setDescription("You need to provide the item which you would like to buy, that's common sense...");
                } else if (args.length < 4) {
                    eb.setDescription("You need to provide the amount of the item to buy, that's common sense...");
                } else {

                    try {
                        ItemList item = ItemList.valueOf(args[2].toUpperCase());

                        try {
                            long amount = Long.parseLong(args[3]);
                            long cost;
                            if (item.equals(ItemList.DOGECOIN))
                                cost = amount * DogeDiscord.dogecoin;
                            else
                                cost = amount * item.getPrice();

                            try {
                                success = true;

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

                                if (cost > rs.getLong("money"))
                                    eb.setDescription("You don't have enough money...");
                                else {
                                    preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                                    preparedStatement.setLong(1, (rs.getLong("money") - cost));
                                    preparedStatement.setString(2, member.getId());

                                    preparedStatement.executeUpdate();

                                    preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM items WHERE id = ? AND item = ?");

                                    preparedStatement.setString(1, member.getId());
                                    preparedStatement.setString(2, item.name());

                                    rs = preparedStatement.executeQuery();

                                    if (rs.isClosed()) {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("INSERT INTO items VALUES (?, ?, ?)");

                                        preparedStatement.setString(1, member.getId());
                                        preparedStatement.setString(2, item.name());
                                        preparedStatement.setLong(3, amount);
                                    } else {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE items SET amount = ? WHERE id = ? AND item = ?");

                                        preparedStatement.setLong(1, (rs.getLong("amount") + amount));
                                        preparedStatement.setString(2, member.getId());
                                        preparedStatement.setString(3, item.name());
                                    }
                                    preparedStatement.executeUpdate();

                                    if (item.equals(ItemList.DOGECOIN))
                                        eb.setDescription("You successfully bought " + amount + "x " + item.getName() + "!\nPrice: `" + (amount * DogeDiscord.dogecoin) + "$`");
                                    else
                                        eb.setDescription("You successfully bought " + amount + "x " + item.getName() + "!\nPrice: `" + (amount * item.getPrice()) + "$`");
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        } catch (Exception ignored) {
                            eb.setDescription("That's not a number...");
                            channel.sendMessage(eb.build()).queue();
                            return;
                        }

                    } catch (Exception ignored) {
                        eb.setDescription("The item which is provided doesn't exist...");
                        channel.sendMessage(eb.build()).queue();
                        return;
                    }

                }
            } else if (args[1].equalsIgnoreCase("sell")) {

                if (args.length < 3) {
                    eb.setDescription("You need to provide the item which you would like to buy, that's common sense...");
                } else if (args.length < 4) {
                    eb.setDescription("You need to provide the amount of the item to buy, that's common sense...");
                } else {


                    try {
                        ItemList item = ItemList.valueOf(args[2].toUpperCase());

                        try {
                            long amount = Long.parseLong(args[3]);

                            try {
                                success = true;

                                PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM items WHERE id = ? AND item = ?");

                                preparedStatement.setString(1, member.getId());
                                preparedStatement.setString(2, item.name());

                                ResultSet rs = preparedStatement.executeQuery();

                                if (rs.isClosed())
                                    eb.setDescription("You don't have this item...");
                                else if (amount > rs.getLong("amount"))
                                    eb.setDescription("You don't have the amount of items...");
                                else {
                                    long price;
                                    if (item.equals(ItemList.DOGECOIN))
                                        price = amount * DogeDiscord.dogecoin;
                                    else
                                        price = amount * item.getPrice();

                                    preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                                    preparedStatement.setString(1, member.getId());

                                    rs = preparedStatement.executeQuery();

                                    preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                                    preparedStatement.setLong(1, (rs.getLong("money") + price));
                                    preparedStatement.setString(2, member.getId());

                                    preparedStatement.executeUpdate();

                                    preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM items WHERE id = ? AND item = ?");

                                    preparedStatement.setString(1, member.getId());
                                    preparedStatement.setString(2, item.name());

                                    rs = preparedStatement.executeQuery();

                                    if (amount == rs.getLong("amount")) {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("DELETE FROM items WHERE id = ? AND item = ?");

                                        preparedStatement.setString(1, member.getId());
                                        preparedStatement.setString(2, item.name());
                                    } else {
                                        preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE items SET amount = ? WHERE id = ? AND item = ?");

                                        preparedStatement.setLong(1, (rs.getLong("amount") - amount));
                                        preparedStatement.setString(2, member.getId());
                                        preparedStatement.setString(3, item.name());
                                    }
                                    preparedStatement.executeUpdate();

                                    if (item.equals(ItemList.DOGECOIN))
                                        eb.setDescription("You successfully sold " + amount + "x " + item.getName() + "!\nPrice: `" + (amount * DogeDiscord.dogecoin) + "$`");
                                    else
                                        eb.setDescription("You successfully sold " + amount + "x " + item.getName() + "!\nPrice: `" + (amount * item.getPrice()) + "$`");
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        } catch (Exception ignored) {
                            eb.setDescription("That's not a number...");
                            channel.sendMessage(eb.build()).queue();
                            return;
                        }

                    } catch (Exception ignored) {
                        eb.setDescription("The item which is provided doesn't exist...");
                        channel.sendMessage(eb.build()).queue();
                        return;
                    }

                }

            } else {
                for (ItemList i : ItemList.values()) {
                    if (i.equals(ItemList.DOGECOIN))
                        eb.addField(i.getName(), i.getDescription() + "\n`ID: " + i.name() + "`\n`" + DogeDiscord.dogecoin + "$`", true);
                    else
                        eb.addField(i.getName(), i.getDescription() + "\n`ID: " + i.name() + "`\n`" + i.getPrice() + "$`", true);
                }

                eb.setFooter("Type " + CommandListener.prefix + "shop [buy/sell] [item-id] [amount]");
            }

        } else {
            eb.setDescription("`" + CommandListener.prefix + "shop [list/buy/sell]`");
        }

        channel.sendMessage(eb.build()).queue();

        if (success) {
            if (DogeDiscord.premium.contains(channel.getGuild().getId()))
                CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "shop");
            else
                CommandListener.INSTANCE().addDelay(member.getId(), 16, CommandListener.prefix + "shop");
        } else {
            if (DogeDiscord.premium.contains(channel.getGuild().getId()))
                CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "shop");
            else
                CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "shop");
        }
    }
}
