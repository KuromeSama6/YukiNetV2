package moe.ku6.yukinet.config;

import lombok.Getter;

import java.io.IOException;

public class ConfigManager {
    @Getter
    private static ConfigManager instance;

    public ConfigManager() {
        if (instance != null)
            throw new IllegalStateException("ConfigManager instance already exists!");
        instance = this;
    }

    public void Load() throws IOException {

    }
}
