package eu.lyzev.commands.types.botOwner;

import com.google.common.io.Files;
import eu.lyzev.commands.ServerCommand;
import eu.lyzev.listener.CommandListener;
import eu.lyzev.main.DogeDiscord;
import eu.lyzev.utils.DogeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BackupCommand implements ServerCommand {
    @Override
    public void performCommand(Member member, TextChannel channel, Message message) {
        if (DogeDiscord.premium.contains(channel.getGuild().getId()))
            CommandListener.INSTANCE().addDelay(member.getId(), 4, CommandListener.prefix + "backup");
        else
            CommandListener.INSTANCE().addDelay(member.getId(), 8, CommandListener.prefix + "backup");

        if (member.getId().equalsIgnoreCase(DogeDiscord.ownerId)) {
            EmbedBuilder eb = DogeAPI.createEmbed();
            eb.setTitle("Doge Backup");
            try {
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

                eb.setDescription("The Backup was successfully!");
                channel.sendMessage(eb.build()).queue();
            } catch (Exception e) {
                eb.setDescription("The backup wasn't successfull...\n\n**Exception**\n```\n" + e.getMessage() + "\n```");
                channel.sendMessage(eb.build()).queue();
            }
        } else {
            EmbedBuilder eb = DogeAPI.createEmbed();
            eb.setTitle(DogeAPI.randomPhrasePermissions());
            eb.setDescription("You do not have access to this command!\nIf you need help with something, join our [support-server](https://dc.lyzev.eu)!");
            channel.sendMessage(eb.build()).queue();
        }
    }
}
