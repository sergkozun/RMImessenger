package Util;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {

    private UUID uuid;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                '}';
    }

}
