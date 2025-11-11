package moe.ku6.yukinet.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import moe.ku6.yukinet.communication.packet.Packet;
import org.apache.tools.ant.taskdefs.Pack;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum PacketType {
    BEGIN_RESOURCE_SYNC(0x0001, null),
    RESOURCE_SYNC_DATA(0x0002, null),
    END_RESOURCE_SYNC(0x0003, null),
    REQUEST_RESOURCE(0x0004, null),

    ;

    private static final Map<Short, PacketType> byId = new HashMap<>();

    static {
        for (var type : values()) {
            byId.put(type.getId(), type);
        }
    }

    private final short id;
    private final Class<? extends Packet> packetClass;

    PacketType(int id, Class<? extends Packet> packetClass) {
        this.id = (short)id;
        this.packetClass = packetClass;
    }

    public static PacketType ById(int id) {
        return byId.get((short)id);
    }
}
