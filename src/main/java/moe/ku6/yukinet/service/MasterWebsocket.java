package moe.ku6.yukinet.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.ku6.yukinet.YukiNet;
import moe.ku6.yukinet.config.ConfigManager;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class MasterWebsocket extends WebSocketServer {
    private final CompletableFuture<Void> startFuture = new CompletableFuture<>();

    public MasterWebsocket(InetSocketAddress address) {
        super(address);
        log.info("[Master] Master websocket server starting at {}:{}", address.getHostString(), address.getPort());

        setDaemon(false);
        setTcpNoDelay(true);
        setReuseAddr(false);


        start();
        try {
            startFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onStart() {
        startFuture.complete(null);
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        log.info("[Master] New websocket handshake from {}", conn.getRemoteSocketAddress());

        ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
        // handshake validation

        // version
        var version = request.getFieldValue("YukiNet-Version");
        if (version == null || !version.equals(YukiNet.VERSION)) {
            log.error("[Master] Incompatible version from {}, master version is {}, worker version is {}", conn.getRemoteSocketAddress(), YukiNet.VERSION, version);
            throw new InvalidDataException(1002, "Incompatible version");
        }

        // secret
        String secret = request.getFieldValue("YukiNet-Secret");
        var masterSecret = ConfigManager.getInstance().getMasterConfig().getWebsocketSecret();
        if (secret == null || !secret.equals(masterSecret)) {
            log.error("[Master] Invalid secret from {}, closing", conn.getRemoteSocketAddress());
            throw new InvalidDataException(1002, "Invalid secret");
        }

        return builder;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        log.info("[Master] Worker connected: {}", webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }
}
