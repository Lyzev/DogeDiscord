package eu.lyzev.utils.blackjack;

import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class BlackjackManager {

    public final static HashMap<String, String> blackjack = new HashMap<>();

    private final static String[] symboles = {"♠", "♦", "♥", "♣"};

    public static BlackjackManager INSTANCE() {
        return new BlackjackManager();
    }

    private static BlackjackCards getCardByIndex(int index) {
        switch (index) {
            case 0:
                return BlackjackCards.ACE;
            case 1:
                return BlackjackCards.TWO;
            case 2:
                return BlackjackCards.THREE;
            case 3:
                return BlackjackCards.FOUR;
            case 4:
                return BlackjackCards.FIVE;
            case 5:
                return BlackjackCards.SIX;
            case 6:
                return BlackjackCards.SEVEN;
            case 7:
                return BlackjackCards.EIGHT;
            case 8:
                return BlackjackCards.NINE;
            case 9:
                return BlackjackCards.TEN;
            case 10:
                return BlackjackCards.JACK;
            case 11:
                return BlackjackCards.QUEEN;
            default:
                return BlackjackCards.KING;
        }
    }

    public boolean playingBlackjack(Member member) {
        return blackjack.get(member.getId()) != null;
    }

    public void createGame(Member member, TextChannel channel, int amount, long time) {
        if (!playingBlackjack(member)) {

            int randomUser = DogeAPI.random(0, 12);
            int randomBot = DogeAPI.random(0, 12);

            String randomUserSymbole = symboles[DogeAPI.random(0, (symboles.length - 1))];
            String randomBotSymbole = symboles[DogeAPI.random(0, (symboles.length - 1))];

            EmbedBuilder eb = DogeAPI.createEmbed();
            eb.setTitle("Doge Blackjack");
            eb.addField(DogeAPI.randomPhraseBlackjack(), "Type `h` to **hit**, type `s` to **stand**, or type `e` to **end the game**.", false);

            if (getCardByIndex(randomUser).getSecondValue() == null)
                eb.addField("You", "Cards: `[" + randomUserSymbole + getCardByIndex(randomUser).getCard() + "]`\nTotal: `" + getCardByIndex(randomUser).getFirstValue() + "`", true);
            else
                eb.addField("You", "Cards: `[" + randomUserSymbole + getCardByIndex(randomUser).getCard() + "]`\nTotal: `" + getCardByIndex(randomUser).getSecondValue() + "`", true);

            if (getCardByIndex(randomBot).getSecondValue() == null)
                eb.addField("Doge", "Cards: `[" + randomBotSymbole + getCardByIndex(randomBot).getCard() + "]` `[?]`\nTotal: `" + getCardByIndex(randomBot).getFirstValue() + "`", true);
            else
                eb.addField("Doge", "Cards: `[" + randomBotSymbole + getCardByIndex(randomBot).getCard() + "]` `[?]`\nTotal: `" + getCardByIndex(randomBot).getSecondValue() + "`", true);

            eb.addField("", "J,Q,K = 10 / A = 1 or 11", false);
            eb.setFooter("If you don't answer/play, the money will be lost (no refund)! Have fun and good luck!");

            blackjack.put(member.getId(), channel.getId() + " " + amount + " " + time + " %false% " + "%you% " + randomUserSymbole + getCardByIndex(randomUser).getCard() + " %bot% " + randomBotSymbole + getCardByIndex(randomBot).getCard());

            channel.sendMessage(eb.build()).queue();

            new Thread(() -> {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (playingBlackjack(member)) {
                    if (blackjack.get(member.getId()).startsWith(channel.getId() + " " + amount + " " + time + " %false% ")) {
                        blackjack.remove(member.getId());
                        channel.sendMessage("You need to response! I guess that's common sense.").queue();
                    } else if (blackjack.get(member.getId()).startsWith(channel.getId() + " " + amount + " " + time)) {
                        try {
                            Thread.sleep(50 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (playingBlackjack(member)) {
                            if (blackjack.get(member.getId()).startsWith(channel.getId() + " " + amount + " " + time)) {
                                blackjack.remove(member.getId());
                                channel.sendMessage("Time limit reached! You have to start a new game.").queue();
                            }
                        }
                    }
                }


            }).start();

        }
    }

    public void endGame(Member member, TextChannel channel) {
        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle(DogeAPI.randomPhraseBlackjackEnd());
        eb.setDescription("The game did end!\nYou lost `" + blackjack.get(member.getId()).split(" ")[1] + "$`.\nTry again for better luck!");
        eb.setFooter("Type d!bj to play again!");

        channel.sendMessage(eb.build()).queue();

        blackjack.remove(member.getId());
    }

    public void hit(Member member, TextChannel channel) {
        boolean bot = false;

        boolean player = false;

        StringBuilder playerCards = new StringBuilder();

        StringBuilder botCards = new StringBuilder();

        int botAmount = 0;

        int playerAmount = 0;

        int acePlayer = 0;

        int aceBot = 0;

        for (String i : blackjack.get(member.getId()).split(" ")) {

            if (i.equalsIgnoreCase("%you%"))
                player = true;
            else if (i.equalsIgnoreCase("%bot%")) {
                player = false;
                bot = true;
            } else if (player) {
                playerCards.append("`[").append(i).append("]`").append(" ");
                for (BlackjackCards card : BlackjackCards.values()) {
                    if (card.getCard().equalsIgnoreCase(i.substring(1))) {

                        if (card.equals(BlackjackCards.ACE)) {
                            playerAmount++;
                            acePlayer++;
                        } else
                            playerAmount = playerAmount + card.getFirstValue();

                    }
                }
            } else if (bot) {
                botCards.append("`[").append(i).append("]`").append(" ");
                for (BlackjackCards card : BlackjackCards.values()) {
                    if (card.getCard().equalsIgnoreCase(i.substring(1))) {

                        if (card.equals(BlackjackCards.ACE)) {
                            botAmount++;
                            aceBot++;
                        } else
                            botAmount = botAmount + card.getFirstValue();

                        break;
                    }
                }
            }
        }

        String playerOldCrads = playerCards.toString();

        int randomCard;

        if (playerAmount > 10)
            randomCard = DogeAPI.random(3, 10);
        else
            randomCard = DogeAPI.random(0, 12);

        String randomSymbole = symboles[DogeAPI.random(0, (symboles.length - 1))];

        if (getCardByIndex(randomCard).equals(BlackjackCards.ACE) && botAmount < 12)
            playerAmount = playerAmount + getCardByIndex(randomCard).getSecondValue();
        else
            playerAmount = playerAmount + getCardByIndex(randomCard).getFirstValue();

        playerCards.append("`[").append(randomSymbole).append(getCardByIndex(randomCard).getCard()).append("]`").append(" ");

        while (aceBot > 0) {
            if ((botAmount + 10) < 22) {
                botAmount = botAmount + 10;
            }

            aceBot--;
        }

        while (acePlayer > 0) {
            if ((playerAmount + 10) < 22) {
                playerAmount = playerAmount + 10;
            }

            acePlayer--;
        }

        blackjack.put(member.getId(), blackjack.get(member.getId()).substring(0, (blackjack.get(member.getId()).length() - botCards.toString().length())).replace(playerOldCrads.replace("`", "").replace("[", "").replace("]", ""), playerCards.toString().replace("`", "").replace("[", "").replace("]", "")) + blackjack.get(member.getId()).substring((blackjack.get(member.getId()).length() - botCards.toString().length())));

        if (playerAmount > 21) {

            EmbedBuilder eb = DogeAPI.createEmbed();
            eb.setTitle("Doge Blackjack");
            eb.addField(DogeAPI.randomPhraseBlackjack(), "Type `h` to **hit**, type `s` to **stand**, or type `e` to **end the game**.", false);
            eb.addField("You", "Cards: " + playerCards + "\nTotal: `" + playerAmount + "`", true);
            eb.addField("Doge", "Cards: " + botCards + " `[?]`\nTotal: `" + botAmount + "`", true);
            eb.addField("", "J,Q,K = 10 / A = 1 or 11", false);
            eb.setFooter("Have fun and good luck!");

            channel.sendMessage(eb.build()).queue();

            EmbedBuilder embedBuilder = DogeAPI.createEmbed();
            embedBuilder.setTitle("Doge Blackjack");
            embedBuilder.setDescription("**Doge won the game!**\nYou lost `" + blackjack.get(member.getId()).split(" ")[1] + "$`.\nType `" + CommandListener.prefix + "bj (amount)` to play again!");

            channel.sendMessage(embedBuilder.build()).queue();

            blackjack.remove(member.getId());

        } else if (playerAmount == 21 || playerCards.toString().split(" ").length > 4) {

            stay(member, channel);

        } else {

            EmbedBuilder eb = DogeAPI.createEmbed();
            eb.setTitle("Doge Blackjack");
            eb.addField(DogeAPI.randomPhraseBlackjack(), "Type `h` to **hit**, type `s` to **stand**, or type `e` to **end the game**.", false);
            eb.addField("You", "Cards: " + playerCards + "\nTotal: `" + playerAmount + "`", true);
            eb.addField("Doge", "Cards: " + botCards + " `[?]`\nTotal: `" + botAmount + "`", true);
            eb.addField("", "J,Q,K = 10 / A = 1 or 11", false);
            eb.setFooter("Have fun and good luck!");

            channel.sendMessage(eb.build()).queue();

        }
    }

    public void stay(Member member, TextChannel channel) {
        boolean bot = false;

        boolean player = false;

        StringBuilder playerCards = new StringBuilder();

        StringBuilder botCards = new StringBuilder();

        int botCardsAmount = 0;

        int botAmount = 0;

        int playerAmount = 0;

        int acePlayer = 0;

        int aceBot = 0;

        for (String i : blackjack.get(member.getId()).split(" ")) {
            if (i.equalsIgnoreCase("%you%"))
                player = true;
            else if (i.equalsIgnoreCase("%bot%")) {
                player = false;
                bot = true;
            } else if (player) {
                playerCards.append("`[").append(i).append("]`").append(" ");
                for (BlackjackCards card : BlackjackCards.values()) {
                    if (card.getCard().equalsIgnoreCase(i.substring(1))) {

                        if (card.equals(BlackjackCards.ACE)) {
                            playerAmount++;
                            acePlayer++;
                        } else
                            playerAmount = playerAmount + card.getFirstValue();

                    }
                }
            } else if (bot) {
                botCards.append("`[").append(i).append("]`").append(" ");
                botCardsAmount++;
                for (BlackjackCards card : BlackjackCards.values()) {
                    if (card.getCard().equalsIgnoreCase(i.substring(1))) {

                        if (card.equals(BlackjackCards.ACE)) {
                            botAmount++;
                            aceBot++;
                        } else
                            botAmount = botAmount + card.getFirstValue();

                    }
                }
            }
        }

        while (botCardsAmount < 6) {

            if (botAmount > 20 || botAmount > playerAmount) break;

            int randomCard;
            if (DogeAPI.random(1, 5) == 3 && botAmount != 20 && botAmount > 11)
                randomCard = 20 - botAmount;
            else
                randomCard = DogeAPI.random(0, 12);

            String randomSymbole = symboles[DogeAPI.random(0, (symboles.length - 1))];

            if (getCardByIndex(randomCard).equals(BlackjackCards.ACE) && botAmount < 12)
                botAmount = botAmount + getCardByIndex(randomCard).getSecondValue();
            else
                botAmount = botAmount + getCardByIndex(randomCard).getFirstValue();

            botCards.append("`[").append(randomSymbole).append(getCardByIndex(randomCard).getCard()).append("]`").append(" ");

            botCardsAmount++;

        }

        while (aceBot > 0) {
            if ((botAmount + 10) < 22) {
                botAmount = botAmount + 10;
            }

            aceBot--;
        }

        while (acePlayer > 0) {
            if ((playerAmount + 10) < 22) {
                playerAmount = playerAmount + 10;
            }

            acePlayer--;
        }

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Blackjack");
        eb.addField(DogeAPI.randomPhraseBlackjack(), "Type `h` to **hit**, type `s` to **stand**, or type `e` to **end the game**.", false);
        eb.addField("You", "Cards: " + playerCards + "\nTotal: `" + playerAmount + "`", true);
        eb.addField("Doge", "Cards: " + botCards + "\nTotal: `" + botAmount + "`", true);
        eb.addField("", "J,Q,K = 10 / A = 1 or 11", false);
        eb.setFooter("Have fun and good luck!");

        channel.sendMessage(eb.build()).queue();

        if ((botCardsAmount > 4 && botAmount < 22) || (botAmount > playerAmount && botAmount < 22)) {
            EmbedBuilder embedBuilder = DogeAPI.createEmbed();
            embedBuilder.setTitle("Doge Blackjack");
            embedBuilder.setDescription("**Doge won the game!**\nYou lost `" + blackjack.get(member.getId()).split(" ")[1] + "$`.\nType `" + CommandListener.prefix + "bj (amount)` to play again!");

            blackjack.remove(member.getId());

            channel.sendMessage(embedBuilder.build()).queue();

            return;
        }

        checkWin(member, channel);
    }

    public void checkWin(Member member, TextChannel channel) {

        boolean bot = false;

        int botCards = 0;

        int aceBot = 0;

        boolean player = false;

        int playerCards = 0;

        int acePlayer = 0;

        for (String i : blackjack.get(member.getId()).split(" ")) {
            if (i.equalsIgnoreCase("%you%"))
                player = true;
            else if (i.equalsIgnoreCase("%bot%")) {
                player = false;
                bot = true;
            } else if (player) {

                for (BlackjackCards card : BlackjackCards.values()) {
                    if (card.getCard().equalsIgnoreCase(i.substring(1))) {

                        if (card.equals(BlackjackCards.ACE)) {
                            playerCards++;
                            acePlayer++;
                        } else
                            playerCards = playerCards + card.getFirstValue();

                    }
                }

            } else if (bot) {

                for (BlackjackCards card : BlackjackCards.values()) {
                    if (card.getCard().equalsIgnoreCase(i.substring(1))) {

                        if (card.equals(BlackjackCards.ACE)) {
                            botCards++;
                            aceBot++;
                        } else
                            botCards = botCards + card.getFirstValue();

                    }
                }

            }
        }

        while (aceBot > 0) {
            if ((botCards + 10) < 22) {
                botCards = botCards + 10;
            }

            aceBot--;
        }

        while (acePlayer > 0) {
            if ((playerCards + 10) < 22) {
                playerCards = playerCards + 10;
            }

            acePlayer--;
        }

        String amount = blackjack.get(member.getId()).split(" ")[1];

        blackjack.remove(member.getId());

        EmbedBuilder eb = DogeAPI.createEmbed();
        eb.setTitle("Doge Blackjack");

        if (playerCards == botCards && botCards == 21) {
            try {
                PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                preparedStatement.setString(1, member.getId());

                ResultSet rs = preparedStatement.executeQuery();

                long money = rs.getLong("money") + Integer.parseInt(amount);

                preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                preparedStatement.setLong(1, money);
                preparedStatement.setString(2, member.getId());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            eb.setDescription("**It's draw!**\nYou didn't lose any money.\nType `" + CommandListener.prefix + "bj (amount)` to play again!");
        } else if ((playerCards < botCards && botCards < 22) || playerCards > 21) {
            eb.setDescription("**Doge won the game!**\nYou lost `" + amount + "$`.\nType `" + CommandListener.prefix + "bj (amount)` to play again!");
        } else {
            try {
                PreparedStatement preparedStatement = DogeDiscord.sql.prepareStatement("SELECT * FROM economy WHERE id = ?");

                preparedStatement.setString(1, member.getId());

                ResultSet rs = preparedStatement.executeQuery();

                long money = rs.getLong("money") + (Long.parseLong(amount) * 2);

                preparedStatement = DogeDiscord.sql.prepareStatement("UPDATE economy SET money = ? WHERE id = ?");

                preparedStatement.setLong(1, money);
                preparedStatement.setString(2, member.getId());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            eb.setDescription("**You won the game!**\nYou won `" + Integer.parseInt(amount) + "$`.\nType `" + CommandListener.prefix + "bj (amount)` to play again!");
        }

        channel.sendMessage(eb.build()).queue();
    }
}
