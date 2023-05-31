package com.leonardo.minecraft.core.api.uuid;

import java.util.UUID;

public interface UUIDProvider {

    static UUID fromString(String uuid) {
        if (!uuid.contains("-"))
            uuid = uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5");
        return UUID.fromString(uuid);
    }

    UUID getUUID(String username);

}
