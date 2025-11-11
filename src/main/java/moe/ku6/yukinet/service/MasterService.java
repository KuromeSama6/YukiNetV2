package moe.ku6.yukinet.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.ku6.yukinet.config.ConfigManager;

@Slf4j
public class MasterService {
    @Getter
    private static MasterService instance;

    private final MasterWebsocket websocket;

    public MasterService() {
        if (instance != null)
            throw new IllegalStateException("MasterService instance already exists!");
        instance = this;

        var config = ConfigManager.getInstance().getMasterConfig();
        websocket = new MasterWebsocket(config.getWebsocketAddress());
    }

    public void Shutdown() {
        log.info("[Master] Shutting down master websocket server");

        try {
            websocket.stop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
