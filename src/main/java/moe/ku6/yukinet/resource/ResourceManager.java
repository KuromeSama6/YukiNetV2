package moe.ku6.yukinet.resource;

import lombok.Getter;
import moe.ku6.yukinet.YukiNet;

import java.io.File;

public class ResourceManager {
    @Getter
    private static ResourceManager instance;
    @Getter
    private final File resourceDirectory;

    public ResourceManager() {
        if (instance != null)
            throw new IllegalStateException("ResourceManager instance already exists!");
        instance = this;

        resourceDirectory = new File(YukiNet.getInstance().getCwd(), "resources");
        if (!resourceDirectory.exists()) resourceDirectory.mkdirs();

    }
}
