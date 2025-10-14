package com.example;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ChatToHttpPlugin extends JavaPlugin implements Listener {

    private String webhookUrl;
    private String password;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("c2h").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("c2h")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                Player player = (Player) sender;
                if (player.hasPermission("c2h.reload")) {
                    reloadConfig();
                    sender.sendMessage(getConfig().getString("config-reloaded"));
                    return true;
                } else {
                    sender.sendMessage(getConfig().getString("no-permission-message"));
                    return false;
                }
            } else {
                sender.sendMessage(getConfig().getString("main-text"));
            }
        }
        return false;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        webhookUrl = getConfig().getString("webhook-url");
        password = getConfig().getString("password");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        try {
            String nick = event.getPlayer().getName();
            String message = event.getMessage();

            String encodedNick = URLEncoder.encode(nick, StandardCharsets.UTF_8.toString());
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());

            String urlString = webhookUrl + "?nick=" + encodedNick + "&message=" + encodedMessage + "&password="
                    + password;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}