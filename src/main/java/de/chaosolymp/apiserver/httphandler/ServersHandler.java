package de.chaosolymp.apiserver.httphandler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.chaosolymp.apiserver.ApiServerPlugin;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;
import java.io.OutputStream;

public final class ServersHandler implements HttpHandler {

    private final transient ApiServerPlugin plugin;
    private final transient Gson gson;

    public ServersHandler(final ApiServerPlugin plugin, final Gson gson) {
        this.plugin = plugin;
        this.gson = gson;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        JsonArray array = new JsonArray();
        this.plugin.getProxy().getServers().forEach((key, value) -> array.add(this.getServerObject(key, value)));

        final String response = this.gson.toJson(array);
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private JsonObject getServerObject(final String serverKey, final ServerInfo info) {
        JsonObject object = new JsonObject();
        object.addProperty("server-key", serverKey);
        object.addProperty("name", info.getName());
        object.addProperty("motd", info.getMotd());
        object.addProperty("restricted", info.isRestricted());
        object.addProperty("online", this.plugin.getServerPingHandler().isServerOnline(serverKey));
        return object;
    }
}
