package moe.ku6.yukinet.config;

import lombok.Getter;
import moe.ku6.jsonwrapper.JsonWrapper;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;

@Getter
public class WorkerConfig {
    private final URI masterWebsocketUri;
    private final String masterWebsocketSecret;

    public WorkerConfig(JsonWrapper data) {
        masterWebsocketUri = URI.create(data.GetString("master.url"));
        masterWebsocketSecret = data.GetString("master.secret");
    }
}
