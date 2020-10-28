package com.civuniverse.kayla;

import com.civuniverse.kayla.cmd.CommandHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class OneTimeTeleport extends JavaPlugin {
    private static Plugin plugin;

    /*
    Files
     */
    private File pending = new File("./plugins/OTT/pending.tsv");
    private File used = new File("./plugins/OTT/used.tsv");

    /*
    First UUID is for Players that are recieving, Second UUID is players that are sending.
     */
    private static HashMap<UUID, UUID> pendingTeleports = new HashMap<UUID, UUID>();

    /*
    UUID's of players who have used their teleports.
     */
    private static List<UUID> usedTeleports = new ArrayList<UUID>();

    @Override
    public void onEnable() {
        super.onEnable();
        plugin = this;
        getCommand("ott").setExecutor(new CommandHandler());
        try {
            loadDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        try {
            saveDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadDatabase() throws IOException, ClassNotFoundException {
        FileInputStream f = new FileInputStream(pending);
        ObjectInputStream s = new ObjectInputStream(f);
        HashMap<UUID, UUID> loadPend = (HashMap<UUID, UUID>) s.readObject();
        loadPend(loadPend);
        s.close();

        FileInputStream fi = new FileInputStream(used);
        ObjectInputStream si = new ObjectInputStream(fi);
        List<UUID> loadUsed = (ArrayList<UUID>) si.readObject();
        loadUsed(loadUsed);
        si.close();
    }

    private void saveDatabase() throws IOException {
        FileOutputStream f = new FileOutputStream(pending);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(pendingTeleports);
        s.close();

        FileOutputStream fi = new FileOutputStream(used);
        ObjectOutputStream si = new ObjectOutputStream(fi);
        si.writeObject(usedTeleports);
        si.close();
    }

    public void loadPend(HashMap<UUID, UUID> map) {
        pendingTeleports = map;
    }

    public void loadUsed(List<UUID> list) {
        usedTeleports = list;
    }

    public static void addPlayerPending(OfflinePlayer recieving, OfflinePlayer sending) {
        UUID uuids = sending.getUniqueId(); //Player that is sending the OTT
        UUID uuidr = recieving.getUniqueId(); //Player that is recieving OTT
        pendingTeleports.put(uuidr, uuids);
    }

    public static boolean isPlayerPending(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();

        return pendingTeleports.containsKey(uuid);
    }

    public static UUID getSendingPlayer(OfflinePlayer player) {
        UUID uuid = pendingTeleports.get(player.getUniqueId());

        return uuid;
    }

    public static void removePlayerPending(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();

        pendingTeleports.remove(uuid);
    }

    public static void addUsed(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();

        usedTeleports.add(uuid);
    }

    public static boolean checkUsed(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();

        if(usedTeleports.contains(uuid)) {
            return true;
        } else {
            return false;
        }
    }



    public static Plugin getPlugin() {
        return plugin;
    }
}
