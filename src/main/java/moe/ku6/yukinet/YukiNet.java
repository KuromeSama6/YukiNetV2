package moe.ku6.yukinet;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.ku6.yukinet.command.CommandManager;
import moe.ku6.yukinet.config.ConfigManager;
import moe.ku6.yukinet.console.InteractiveConsole;
import moe.ku6.yukinet.resource.ResourceManager;
import org.fusesource.jansi.Ansi;

import java.io.File;

@Slf4j
public class YukiNet {
    @Getter
    private static YukiNet instance;
    @Getter
    private final File cwd;
    private final InteractiveConsole console;

    private YukiNet() throws Exception {
        instance = this;

        try (var is = getClass().getResourceAsStream("/static/splash.txt")) {
            if (is == null) {
                throw new RuntimeException("Could not find splash file");
            }

            var splash = new String(is.readAllBytes());
            System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).bold().a(splash).reset());
        }

        cwd = new File(System.getProperty("user.dir"));

        new CommandManager();
        new ResourceManager();

        var configManager = new ConfigManager();
        configManager.Load();

        console = new InteractiveConsole();

        LoadConfig();

        log.info(org.jline.jansi.Ansi.ansi().fgBrightCyan().a("Ready. Type 'help' for general guidance, 'man' for command usage, hit Ctrl+D to exit.").reset().toString());

        // block
        console.Start();
    }

    private void LoadConfig() {

    }

    public void Exit() {
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        if (instance != null)
            throw new IllegalStateException("Instance already exists!");

        new YukiNet();
    }
}
