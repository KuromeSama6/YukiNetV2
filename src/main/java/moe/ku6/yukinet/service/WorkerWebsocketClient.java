package moe.ku6.yukinet.service;

import lombok.extern.slf4j.Slf4j;
import moe.ku6.yukinet.YukiNet;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WorkerWebsocketClient extends WebSocketClient {
    public WorkerWebsocketClient(URI serverUri, String secret) {
        super(serverUri, CreateHeaders(secret));

        setDaemon(false);
        setTcpNoDelay(true);
        setReuseAddr(false);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("[Worker] Connected to master: {}", getURI());
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    private static Map<String, String> CreateHeaders(String secret) {
        Map<String, String> headers = new HashMap<>();
        headers.put("YukiNet-Secret", secret);
        headers.put("YukiNet-Version", YukiNet.VERSION);
        return headers;
    }
}
