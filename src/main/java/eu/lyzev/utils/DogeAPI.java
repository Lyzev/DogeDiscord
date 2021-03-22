package eu.lyzev.utils;

import eu.lyzev.main.DogeDiscord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.redstonecraft.redstoneapi.tools.HttpHeader;
import net.redstonecraft.redstoneapi.tools.HttpRequest;

import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class DogeAPI {

    public static String randomPhraseDelay() {
        String[] phrases = {"Too spicy, take a breather!", "Take a breather...", "Chill...", "Bro...", "Chill bro..", "Please wait!", "Don't spam!", "Take a break!", "Get some fresh air!", "You write faster than Usain Bolt runs...", "Too fast...", "Wait!", "Don't forget to breath!", "Slow down!", "Arghhhh, slow down!", "Woah bro, slow it down!", "My brain lags!"};
        return phrases[random(0, (phrases.length - 1))];
    }

    public static String randomPhrasePermissions() {
        String[] phrases = {"Sry!", "Sry...", "Sorry...", "Sorry!", "Sorry bro..", "Sry bro...", "Why!?", "Stop it!", "You don't have enough rights! ;)", "Why...", "Don't try this...", "Wait!", "Stop!", "Arghhhhh!", "Wtf...", "Where did you find this command!?", "My brain lags!"};
        return phrases[random(0, (phrases.length - 1))];
    }

    public static String randomPhraseBlackjackEnd() {
        String[] phrases = {"Sry!", "Sorry!", "Sry...", "Sorry...", "Maybe next time!", "Good luck for the future!", "Lose...", "Lose!", "I'm sorry for you...", "Blackjack is trash!", "Seems bad!", "This day is going bad for you...", "This doesn't seem nice!", "HAHAHAHHAHA!", "You are too bad for the game ;)"};
        return phrases[random(0, (phrases.length - 1))];
    }

    public static String randomPhraseBlackjack() {
        String[] phrases = {"It's not luck, it's skill!", "Make your decision!", "Take your time to make a decision!", "Are you gonna win!?", "It's looking good for you, isn't it?", "Is it your first game?", "Are you gonna lose!?", "Good luck!", "Seems good for you!", "Thinking is winning!"};
        return phrases[random(0, (phrases.length - 1))];
    }

    public static String randomPhraseBegYes() {
        String[] phrases = {"Your mom gave you %money% to play blackjack!", "Your mom gave you %money%!", "Your stepmom gave you %money%!", "The guy who asked your mom for a date gives you %money%.", "Your teacher is giving you %money%!", "Your dad gives you %money%!", "Your dad who was buying milk gives you %money%!", "Your new stepdad gives you %money%!", "Your girlfriend who is a gold digger gives you %money%!", "The person behind you gives you %money%!", "The monster under your bed gives you %money%!", "You found %money% on the street!", "You found %money% in your stepmom's bra!", "You found %money% under your bed!", "The pogchamp guy gave you %money%!", "Your mum who doesn't like you gives you %money%!", "Your stepmom got stuck in the washing machine, so you robbed her and found %money%.", "Your stepsister gave you %money% because your helped her when she was stucked.", "Money Boy gave you %money%.", "You found %money% in your school.", "Your stepteacher gave you %money%.", "You found %money% in the toilet.", "You robbed a homeless man and took %money% from him.", "You scammer gets scammed a homeless man for %money%.", "You found %money% in a pocket.", "You robbed a bank and earned %money% of it."};
        return phrases[random(0, (phrases.length - 1))];
    }

    public static String randomPhraseHuman() {
        String[] phrases = {"Your human found %money%!", "Your human worked for you, you earned %money%!", "Your human sold some green stuff and earned %money% for you!", "Your human robbed a bank and earned %money% for you!"};
        return phrases[random(0, (phrases.length - 1))];
    }

    public static String randomPhraseBegNo() {
        String[] phrases = {"Sry!", "Sorry!", "Sry...", "Sorry...", "Maybe next time!", "Good luck for the future!", "Nope...", "Go away!", "I'm sorry for you...", "Begging is trash!", "Seems bad!", "This day is going bad for you...", "This doesn't seem nice for you!", "HAHAHAHHAHA!", "You are begging like a little baby!", "Your mom said no!", "Your dad said no!", "The person behind you said no!", "You found nothing in your stepmom's bra!", "You found nothing on the street!", "Your teacher is too broke to give you money!", "Your stepsister is too broke to give you money...", "Ash Ketchup is too broke to give money!", "Begging is for babys!", "You robbed a homeless man but he didn't have any money for you!", "You found nothing in your teacher's bra.", "There are `50$` infront of you but you didn't take them..."};
        return phrases[random(0, (phrases.length - 1))];
    }

    public static String randomSearch() {
        String[] phrases = {"bus", "doge", "car", "bra", "school", "street", "bush", "bed", "keyboard", "reddit", "discord", "garbage", "homeless man", "girlfriend", "pocket", "bank", "toilet", "bathroom", "trousers", "shirt", "purse", "stepmom", "stepsister", "mailbox", "couch", "sink", "sea", "beach", "lake", "dog", "cat", "van", "coat"};
        return phrases[random(0, (phrases.length - 1))];
    }

    public static String randomBonk() {
        String[] phrases = {"https://media0.giphy.com/media/30lxTuJueXE7C/giphy.gif", "https://media1.tenor.com/images/c18e9d2fe73f7a7a511761ee8b476ecc/tenor.gif?itemid=19823428", "https://i.imgur.com/uVmafnS.gif", "https://i.pinimg.com/originals/b0/d2/70/b0d270b7c07757cc6c3fb6efc60229e8.gif", "https://emoji.gg/assets/emoji/3867_BONK.gif"};
        return phrases[random(0, (phrases.length - 1))];
    }

    public static int random(int firstNumber, int secondNumber) {
        return new Random().nextInt((secondNumber - firstNumber) + 1) + firstNumber;
    }

    public static EmbedBuilder createEmbed() {
        DogeDiscord.hex = DogeDiscord.hex + 50000;
        if (DogeDiscord.hex > 15623000)
            DogeDiscord.hex = 0;
        StringBuilder hex = new StringBuilder(Integer.toHexString(DogeDiscord.hex));
        while (hex.length() < 6)
            hex.insert(0, "0");
        hex.insert(0, "#");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode(hex.toString()));

        return eb;
    }

    public static String getUrlConnection(String url) throws IOException, IllegalArgumentException {

        return HttpRequest.get(url, new HttpHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0")).getContentAsString();

        /* ------------------ OLD Method
                URLConnection openConnection = new URL(url).openConnection();
        openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

        Scanner scanner = new Scanner(openConnection.getInputStream());

        StringBuilder sb = new StringBuilder();

        while (scanner.hasNext())
            sb.append(scanner.next());

        return sb;
         */

    }
}
