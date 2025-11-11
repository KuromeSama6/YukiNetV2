package moe.ku6.yukinet.config;

import lombok.Getter;
import moe.ku6.jsonwrapper.JsonWrapper;

import java.net.InetSocketAddress;

@Getter
public class MasterConfig {
    private final InetSocketAddress websocketAddress, apiAddress;
    private final String websocketSecret;

    public MasterConfig(JsonWrapper data) {
        var wsHost = data.GetString("websocket.host");
        var wsPort = data.GetInt("websocket.port");
        websocketSecret = data.GetString("websocket.secret");
        websocketAddress = new InetSocketAddress(wsHost, wsPort);

        var apiHost = data.GetString("api.host");
        var apiPort = data.GetInt("api.port");
        apiAddress = new InetSocketAddress(apiHost, apiPort);
    }
}
