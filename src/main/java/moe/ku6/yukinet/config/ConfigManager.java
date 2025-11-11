package moe.ku6.yukinet.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.ku6.jsonwrapper.JsonUtil;
import moe.ku6.yukinet.YukiNet;
import moe.ku6.yukinet.exception.config.ConfigLoadException;
import moe.ku6.yukinet.exception.config.MissingConfigException;

import java.io.File;

@Slf4j
public class ConfigManager {
    @Getter
    private static ConfigManager instance;

    @Getter
    private MasterConfig masterConfig;
    @Getter
    private WorkerConfig workerConfig;

    public ConfigManager() {
        if (instance != null)
            throw new IllegalStateException("ConfigManager instance already exists!");
        instance = this;
    }

    public void Load() throws MissingConfigException {
        var masterConfig = new File(YukiNet.getInstance().getCwd(), "config/master.json");
        masterConfig.getParentFile().mkdirs();

        var workerConfig = new File(YukiNet.getInstance().getCwd(), "config/worker.json");
        workerConfig.getParentFile().mkdirs();

        if (!masterConfig.exists() && !workerConfig.exists()) {
            throw new MissingConfigException();
        }

        try {
            LoadMasterConfig();
            LoadWorkerConfig();
        } catch (ConfigLoadException e) {
            log.error("Error loading configuration: {}", e.getMessage());

            YukiNet.getInstance().Exit();
            return;
        }
    }

    public boolean IsMaster() {
        return masterConfig != null;
    }

    public boolean IsWorker() {
        return workerConfig != null;
    }

    private void LoadMasterConfig() throws ConfigLoadException {
        var file = new File(YukiNet.getInstance().getCwd(), "config/master.json");
        if (!file.exists()) return;

        var json = JsonUtil.Read(file);
        try {
            masterConfig = new MasterConfig(json);
        } catch (AssertionError e) {
            throw new ConfigLoadException("Failed to load master configuration: " + e.getMessage());
        }
    }

    private void LoadWorkerConfig() throws ConfigLoadException {
        var file = new File(YukiNet.getInstance().getCwd(), "config/worker.json");
        if (!file.exists()) return;

        var json = JsonUtil.Read(file);

        try {
            workerConfig = new WorkerConfig(json);
        } catch (AssertionError e) {
            throw new ConfigLoadException("Failed to load worker configuration: " + e.getMessage());
        }
    }
}
