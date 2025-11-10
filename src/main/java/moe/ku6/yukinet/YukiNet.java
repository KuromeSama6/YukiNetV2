package moe.ku6.yukinet;

import lombok.Getter;
import org.fusesource.jansi.Ansi;

import java.io.File;

public class YukiNet {
    @Getter
    private static YukiNet instance;
    @Getter
    private File cwd;

    private YukiNet() throws Exception {
        try (var is = getClass().getResourceAsStream("/static/splash.txt")) {
            if (is == null) {
                throw new RuntimeException("Could not find splash file");
            }

            var splash = new String(is.readAllBytes());
            System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).bold().a(splash).reset());
        }

        cwd = new File(System.getProperty("user.dir"));
    }

    public static void main(String[] args) throws Exception {
        if (instance != null)
            throw new IllegalStateException("Instance already exists!");

        instance = new YukiNet();
    }
}
