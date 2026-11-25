package dev.engram.daily;
import java.nio.charset.StandardCharsets;
final class LocalModel {
    static { System.loadLibrary("engram"); }
    private static native byte[] generateNative(String path,byte[] system,byte[] user);
    static String generate(String path,String system,String user) {
        return new String(generateNative(path,system.getBytes(StandardCharsets.UTF_8),user.getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8);
    }
}
