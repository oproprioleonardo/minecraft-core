import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.leonardo.minecraft.core.api.uuid.UUIDProvider;
import okhttp3.*;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertTrue;

public class UUIDFetcherTest {

    @Test
    public void setUp() {
        OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();

        String uuid = "";
        final String name = "oleonardosilva";

        final String url = "https://api.mojang.com/users/profiles/minecraft/" + name + "?at=" + System.currentTimeMillis();

        try {
            final Request request = new Request.Builder()
                    .url(url)
                    .header("Connection", "close")
                    .header("Accept-Encoding", "identity")
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build();
            final Call call = client.newCall(request);
            final Response response = call.execute();
            final ResponseBody body = response.body();
            final String jsonBody = body != null ? body.string() : null;
            if (response.isSuccessful()) {
                uuid = new GsonBuilder().setPrettyPrinting().create()
                        .fromJson(jsonBody, JsonObject.class)
                        .get("id")
                        .getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        uuid = Objects.requireNonNull(UUIDProvider.fromString(uuid)).toString();
        System.out.println(uuid);
        assertTrue(true);
    }

}
