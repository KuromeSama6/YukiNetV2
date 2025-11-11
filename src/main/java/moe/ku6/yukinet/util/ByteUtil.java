package moe.ku6.yukinet.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@UtilityClass
// all data are big-endian
public class ByteUtil {
    public static byte ReadByte(InputStream stream) {
        try {
            return (byte)stream.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void ReadBytes(InputStream stream, byte[] buffer, int offset, int length) {
        try {
            int readBytes = stream.read(buffer, offset, length);
            if (readBytes != length) {
                throw new RuntimeException("Failed to read expected number of bytes");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static short ReadWord(InputStream stream) {
        int high = ReadByte(stream) & 0xFF;
        int low = ReadByte(stream) & 0xFF;
        return (short)((high << 8) | (low & 0xFF));
    }

    public static int ReadDWord(InputStream stream) {
        int b1 = ReadByte(stream) & 0xFF;
        int b2 = ReadByte(stream) & 0xFF;
        int b3 = ReadByte(stream) & 0xFF;
        int b4 = ReadByte(stream) & 0xFF;
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }

    public static long ReadQWord(InputStream stream) {
        long b1 = ReadByte(stream) & 0xFFL;
        long b2 = ReadByte(stream) & 0xFFL;
        long b3 = ReadByte(stream) & 0xFFL;
        long b4 = ReadByte(stream) & 0xFFL;
        long b5 = ReadByte(stream) & 0xFFL;
        long b6 = ReadByte(stream) & 0xFFL;
        long b7 = ReadByte(stream) & 0xFFL;
        long b8 = ReadByte(stream) & 0xFFL;
        return (b1 << 56) | (b2 << 48) | (b3 << 40) | (b4 << 32) |
            (b5 << 24) | (b6 << 16) | (b7 << 8) | b8;
    }

    public static String ReadStringLengthPrefixed(InputStream stream) {
        int size = (int)ReadVarInt(stream);
        byte[] strBytes = new byte[size];
        try {
            int readBytes = stream.read(strBytes);
            if (readBytes != size) {
                throw new RuntimeException("Failed to read expected number of bytes for string");
            }
            return new String(strBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads a variable-length integer from the input stream. The most significant bit (MSB) of each byte indicates if there are more bytes to read.
     * The lower 7 bits of each byte contribute to the final integer value.
     *
     * @param stream The input stream to read from.
     * @return The decoded variable-length integer.
     */
    public static long ReadVarInt(InputStream stream) {
        long value = 0;
        int position = 0;
        byte currentByte;

        do {
            currentByte = ReadByte(stream);
            value |= (long)(currentByte & 0x7F) << position;
            position += 7;

            if (position > 63) {
                throw new RuntimeException("Variable length quantity is too long");
            }
        } while ((currentByte & 0x80) != 0);

        return value;
    }

    public static void WriteByte(OutputStream buf, byte value) {
        try {
            buf.write(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void WriteBytes(OutputStream buf, byte[] value) {
        try {
            buf.write(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void WriteWord(OutputStream buf, short value) {
        WriteByte(buf, (byte)((value >> 8) & 0xFF));
        WriteByte(buf, (byte)(value & 0xFF));
    }

    public static void WriteDWord(OutputStream buf, int value) {
        WriteByte(buf, (byte)((value >> 24) & 0xFF));
        WriteByte(buf, (byte)((value >> 16) & 0xFF));
        WriteByte(buf, (byte)((value >> 8) & 0xFF));
        WriteByte(buf, (byte)(value & 0xFF));
    }

    public static void WriteQWord(OutputStream buf, long value) {
        WriteByte(buf, (byte)((value >> 56) & 0xFF));
        WriteByte(buf, (byte)((value >> 48) & 0xFF));
        WriteByte(buf, (byte)((value >> 40) & 0xFF));
        WriteByte(buf, (byte)((value >> 32) & 0xFF));
        WriteByte(buf, (byte)((value >> 24) & 0xFF));
        WriteByte(buf, (byte)((value >> 16) & 0xFF));
        WriteByte(buf, (byte)((value >> 8) & 0xFF));
        WriteByte(buf, (byte)(value & 0xFF));
    }

    public static void WriteStringLengthPrefixed(OutputStream buf, String value) {
        byte[] strBytes = value.getBytes();
        WriteVarInt(buf, strBytes.length);
        try {
            buf.write(strBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void WriteVarInt(OutputStream stream, long value) {
        do {
            byte temp = (byte)(value & 0x7F);
            value >>>= 7;
            if (value != 0) {
                temp |= (byte)0x80;
            }
            WriteByte(stream, temp);
        } while (value != 0);
    }
}
