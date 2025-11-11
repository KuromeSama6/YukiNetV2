package moe.ku6.yukinet.resource;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.ku6.yukinet.YukiNet;
import moe.ku6.yukinet.util.SHA256;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ResourceManager {
    @Getter
    private static ResourceManager instance;
    @Getter
    private final File resourceDirectory;

    private final Map<String, Resource> resourceMapping = new HashMap<>();

    public ResourceManager() throws IOException {
        if (instance != null)
            throw new IllegalStateException("ResourceManager instance already exists!");
        instance = this;

        resourceDirectory = new File(YukiNet.getInstance().getCwd(), "resources");
        if (!resourceDirectory.exists()) resourceDirectory.mkdirs();

        try (var stream = Files.walk(resourceDirectory.toPath(), FileVisitOption.FOLLOW_LINKS)
            .filter(c -> c.toFile().isFile())
        ) {
            log.info("Reading resources");

            stream.forEach(c -> {
                var file = c.toFile();
                var relativePath = resourceDirectory.toPath().relativize(c).toString().replace("\\", "/");
                resourceMapping.put(relativePath, new Resource(file, SHA256.Checksum(file)));
            });
        }

        log.info("Loaded {} resources", resourceMapping.size());
    }
}
