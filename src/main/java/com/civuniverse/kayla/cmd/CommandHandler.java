package com.civuniverse.kayla.cmd;

import com.civuniverse.kayla.OneTimeTeleport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class CommandHandler implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Date date = new Date();

        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "no");
            return true;
        }

        if(args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "You need to specify a player/accept the request!");
            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("accept")) {
                Player player = ((Player) commandSender).getPlayer();

                if(!(OneTimeTeleport.isPlayerPending(player))) {
                    commandSender.sendMessage(ChatColor.RED + "You have no pending requests...");
                    return true;
                } else {
                    UUID uuid = OneTimeTeleport.getSendingPlayer(player);
                    Player sending = Bukkit.getPlayer(uuid);
                    if(OneTimeTeleport.checkUsed(sending)) {
                        commandSender.sendMessage(ChatColor.RED + "Player already used their pending request...");
                        OneTimeTeleport.removePlayerPending(player);
                        return true;
                    }
                    sending.teleport(player);
                    OneTimeTeleport.getPlugin().getLogger().warning(sending.getDisplayName() + " used their OTT.");
                    sending.sendMessage(ChatColor.GREEN + "You've been teleported to " + ChatColor.AQUA + player.getName() + ChatColor.GREEN + " successfully!");
                    OneTimeTeleport.removePlayerPending(player);
                    OneTimeTeleport.addUsed(sending);
                    return true;
                }

            }

            if(args[0].equalsIgnoreCase("deny")) {
                Player player = ((Player) commandSender).getPlayer();

                if(!(OneTimeTeleport.isPlayerPending(player))) {
                    commandSender.sendMessage(ChatColor.RED + "You have no pending requests...");
                    return true;
                } else {
                    UUID uuid = OneTimeTeleport.getSendingPlayer(player);
                    Player sending = Bukkit.getPlayer(uuid);
                    OneTimeTeleport.removePlayerPending(player);
                    commandSender.sendMessage(ChatColor.GREEN + "Denied teleport request successfully!");
                    sending.sendMessage(ChatColor.RED + "Player denied your teleport request!");
                    return true;
                }
            }

            Player player = ((Player) commandSender).getPlayer();

            Integer calc = Math.toIntExact(date.getTime() - player.getFirstPlayed());

            if(!(calc <= 86400000)) {
                player.sendMessage(ChatColor.RED + "You've played for too long to use your OTT!");
                return true;
            }

            if(OneTimeTeleport.checkUsed(player)) {
                player.sendMessage(ChatColor.RED + "You already used your One Time Teleport.");
                return true;
            }

            Player recieve = Bukkit.getPlayer(args[0]);

            if(recieve == null) {
                player.sendMessage(ChatColor.RED + args[0] + " is not online.");
                return true;
            }
            /*
            Record Pending Request...
             */
            OneTimeTeleport.addPlayerPending(recieve, player);
            recieve.sendMessage(ChatColor.GREEN + player.getName() + " has sent you an OTT request. Use /ott accept to accept it, or use /ott deny to deny it.");
            return true;
        }

        return false;
    }
}
