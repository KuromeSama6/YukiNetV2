package moe.ku6.yukinet.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;

@UtilityClass
public class SHA256 {
    public static byte[] Checksum(File file) {
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024); // 1MB direct buffer

            while (channel.read(buffer) != -1) {
                buffer.flip();
                digest.update(buffer);
                buffer.clear();
            }

            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
