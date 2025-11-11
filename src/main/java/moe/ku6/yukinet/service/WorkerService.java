package moe.ku6.yukinet.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.ku6.yukinet.YukiNet;
import moe.ku6.yukinet.config.ConfigManager;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WorkerService {
    @Getter
    private static WorkerService instance;

    private final Thread websocketThread;
    private final WorkerWebsocketClient websocketClient;

    private final CompletableFuture<Void> connectedFuture = new CompletableFuture<>();

    @Getter
    private boolean masterConnected;

    public WorkerService() {
        if (instance != null)
            throw new IllegalStateException("WorkerService instance already exists!");
        instance = this;

        var config = ConfigManager.getInstance().getWorkerConfig();
        websocketClient = new WorkerWebsocketClient(config.getMasterWebsocketUri(), config.getMasterWebsocketSecret());

        websocketThread = new Thread(() -> {
            try {
                StartWebsocket();
            } catch (InterruptedException e) {
                log.error("Websocket disconnected:");
                log.error(e.getMessage(), e);
            }
        });
        websocketThread.setName("WorkerWebsocketThread");
        websocketThread.start();

        // block until connected
        try {
            connectedFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StartResourcesSync();
    }

    private void StartWebsocket() throws InterruptedException {
        var connected = websocketClient.connectBlocking(5, TimeUnit.SECONDS);

        if (connected) {
            log.info("[Worker] Worker connected.");
            masterConnected = true;
            connectedFuture.complete(null);
            return;
        }

        log.error("[Worker] Worker failed to connect to master websocket. Exiting now.");
        websocketClient.close();
        YukiNet.getInstance().Exit();
    }

    private void StartResourcesSync() {
        log.info("[Worker] Starting resource synchronization with master.");
        if (ConfigManager.getInstance().IsMaster()) {
            log.info("[Worker] This instance is configured as master. Skipping resource synchronization.");
            return;
        }

    }

    public void Shutdown() {
        log.info("[Worker] Shutting down worker service");

        websocketClient.close();
        websocketThread.interrupt();
    }
}
