package com.leonardo.minecraft.core.internal.uuid;

import com.google.gson.JsonObject;
import com.leonardo.minecraft.core.Core;
import com.leonardo.minecraft.core.api.uuid.UUIDProvider;
import okhttp3.*;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CachedOkHttpUUIDProvider implements UUIDProvider {

    private final Core core;
    private final Map<String, String> uuids = new HashMap<>();

    public CachedOkHttpUUIDProvider(Core core) {
        this.core = core;
    }

    @Override
    public UUID getUUID(String username) {
        String uuid = this.uuids.getOrDefault(username.toLowerCase(), "");
        if (uuid.equals("")) {
            final String name = username.toLowerCase();
            if (!Bukkit.getOnlineMode())
                uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
            else {
                final String url = "https://api.mojang.com/users/profiles/minecraft/" + name + "?at=" + System.currentTimeMillis();

                try {
                    final Request request = new Request.Builder()
                            .url(url)
                            .header("Connection", "close")
                            .header("Accept-Encoding", "identity")
                            .cacheControl(CacheControl.FORCE_NETWORK)
                            .build();
                    final Call call = this.core.getClient().newCall(request);
                    final Response response = call.execute();
                    final ResponseBody body = response.body();
                    final String jsonBody = body != null ? body.string() : null;
                    if (response.isSuccessful()) {
                        uuid = this.core.getGson()
                                .fromJson(jsonBody, JsonObject.class)
                                .get("id")
                                .getAsString();
                    } else {
                        this.core.getLogger().info("Error fetching UUID of " + name);
                        this.core.getLogger().info("Message: " + response.message());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            uuid = Objects.requireNonNull(UUIDProvider.fromString(uuid)).toString();
            this.uuids.put(name, uuid);
        }
        return UUID.fromString(uuid);
    }

}
