package com.trassert.chattohttp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class ChatToHttpPlugin extends JavaPlugin implements Listener {

    private String webhookUrl;
    private String password;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        var command = getCommand("c2h");
        if (command != null) {
            command.setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label,
            @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("c2h")) {
            return false;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Эта команда доступна только игрокам.");
                return true;
            }
            if (!player.hasPermission("c2h.reload")) {
                sender.sendMessage(getConfig().getString("no-permission-message", "Нет прав."));
                return true;
            }
            reloadConfig();
            sender.sendMessage(getConfig().getString("config-reloaded", "Конфиг перезагружен."));
            return true;
        } else {
            sender.sendMessage(getConfig().getString("main-text", "Используйте /c2h reload"));
            return true;
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        webhookUrl = getConfig().getString("webhook-url");
        password = getConfig().getString("password");
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (webhookUrl == null || password == null) {
            getLogger().warning("webhook-url или password не заданы в конфиге.");
            return;
        }

        var serializer = PlainTextComponentSerializer.builder().build();
        String nick = event.getPlayer().getName();
        String message = serializer.serialize(event.message());

        String encodedNick = URLEncoder.encode(nick, StandardCharsets.UTF_8);
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8);

        String postData = "nick=" + encodedNick + "&message=" + encodedMessage + "&password=" + encodedPassword;

        try {
            URI uri = new URI(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            try (var writer = new java.io.OutputStreamWriter(connection.getOutputStream())) {
                writer.write(postData);
                writer.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                getLogger().warning("Webhook returned code: " + responseCode);
            }
            connection.disconnect();
        } catch (URISyntaxException | IOException e) {
            getLogger().warning("Не удалось отправить сообщение на webhook: " + e.getMessage());
        }
    }
}