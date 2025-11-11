package moe.ku6.yukinet.exception.packet;

public class PacketDecodeException extends Exception {
    public PacketDecodeException(String message) {
        super(message);
    }

    public PacketDecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
