package com.trassert.chattohttp;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ChatToHttpPlugin extends JavaPlugin implements Listener {

    private static final boolean IS_FOLIA = detectFolia();
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();
    private static final String DEFAULT_NO_PERM = "No permission.";
    private static final String DEFAULT_MAIN_TEXT = "Use /c2h reload or /c2h send <url> <json>";

    private Server server;
    private String webhookUrl;
    private String password;
    private String noPermissionMessage;
    private String mainText;

    private static boolean detectFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onEnable() {
        server = getServer();
        saveDefaultConfig();
        reloadConfig();
        server.getPluginManager().registerEvents(this, this);
        var cmd = getCommand("c2h");
        if (cmd != null) cmd.setExecutor(this);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        webhookUrl = getConfig().getString("webhook-url");
        password = getConfig().getString("password");
        noPermissionMessage = getConfig().getString("no-permission-message", DEFAULT_NO_PERM);
        mainText = getConfig().getString("main-text", DEFAULT_MAIN_TEXT);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("c2h")) return false;

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command is only available to players.");
                return true;
            }
            if (!player.hasPermission("c2h.reload")) {
                sender.sendMessage(noPermissionMessage);
                return true;
            }
            reloadConfig();
            sender.sendMessage(getConfig().getString("config-reloaded", "Config reloaded."));
            return true;
        }

        sender.sendMessage(mainText);
        return true;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (webhookUrl == null || password == null) {
            getLogger().warning("webhook-url or password is not set in the config.");
            return;
        }
        String data = "nick=" + URLEncoder.encode(event.getPlayer().getName(), StandardCharsets.UTF_8)
                + "&message=" + URLEncoder.encode(PLAIN_TEXT.serialize(event.message()), StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);
        try {
            HttpResult r = httpPost(webhookUrl, data, "application/x-www-form-urlencoded");
            if (r.code() != 200) getLogger().warning("Webhook returned code: " + r.code());
        } catch (Exception e) {
            getLogger().warning("Failed to send message to webhook: " + e.getMessage());
        }
    }

    private void runAsync(Runnable task) {
        if (IS_FOLIA) {
            server.getAsyncScheduler().runNow(this, t -> task.run());
        } else {
            server.getScheduler().runTaskAsynchronously(this, task);
        }
    }

    private void sendTo(CommandSender sender, String message) {
        if (IS_FOLIA) {
            if (sender instanceof Player player) {
                player.getScheduler().run(this, t -> sender.sendMessage(message), null);
            } else {
                sender.sendMessage(message);
            }
        } else if (sender instanceof Player) {
            server.getScheduler().runTask(this, () -> sender.sendMessage(message));
        } else {
            sender.sendMessage(message);
        }
    }

    private static HttpResult httpPost(String url, String body, String contentType) throws IOException {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URI(url).toURL().openConnection();
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", contentType);
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);

        try (var writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(body);
        }

        int code = conn.getResponseCode();
        var stream = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
        String responseBody = "";
        if (stream != null) {
            try (var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                responseBody = reader.lines().collect(Collectors.joining("\n"));
            } catch (Exception e) {
                responseBody = String.valueOf(e.getMessage());
            }
        }
        conn.disconnect();
        return new HttpResult(code, responseBody);
    }

    private record HttpResult(int code, String body) {}
}