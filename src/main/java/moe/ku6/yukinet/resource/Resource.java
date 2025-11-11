package moe.ku6.yukinet.resource;

import moe.ku6.yukinet.util.ByteUtil;

import java.io.File;
import java.io.InputStream;

public record Resource(
    File file,
    byte[] checksum
) {
}
