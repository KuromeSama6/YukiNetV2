package moe.ku6.yukinet.communication.packet;

import lombok.Getter;
import moe.ku6.yukinet.communication.PacketType;
import moe.ku6.yukinet.exception.packet.PacketDecodeException;
import moe.ku6.yukinet.util.ByteUtil;

import java.io.InputStream;

public abstract class Packet {
    @Getter
    protected final PacketType type;

    public Packet(PacketType type) {
        this.type = type;
    }

    public Packet(InputStream stream) throws PacketDecodeException {
        var type = ByteUtil.ReadWord(stream);
        this.type = PacketType.ById(type);

        if (this.type == null)
            throw new PacketDecodeException("Unknown packet type: " + type);
    }
}
