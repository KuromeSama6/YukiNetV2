package moe.ku6.yukinet;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.ku6.jsonwrapper.JsonUtil;
import moe.ku6.jsonwrapper.JsonWrapper;
import moe.ku6.yukinet.command.CommandManager;
import moe.ku6.yukinet.config.ConfigManager;
import moe.ku6.yukinet.console.InteractiveConsole;
import moe.ku6.yukinet.exception.config.MissingConfigException;
import moe.ku6.yukinet.resource.ResourceManager;
import moe.ku6.yukinet.service.MasterService;
import moe.ku6.yukinet.service.WorkerService;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class YukiNet {
    public static final String VERSION = YukiNet.class.getPackage().getImplementationVersion();

    @Getter
    private static YukiNet instance;
    @Getter
    private final File cwd;
    private final InteractiveConsole console;

    private YukiNet() throws Exception {
        instance = this;
        PrintSplash();

        if (VERSION == null) {
            log.error("Could not determine YukiNet version. Exiting now.");
            System.exit(1);
        }

        cwd = new File(System.getProperty("user.dir"));

        new CommandManager();
        new ResourceManager();

        var configManager = new ConfigManager();
        try {
            configManager.Load();

        } catch (MissingConfigException e) {
            var master = new JsonWrapper(JsonUtil.ReadToStringAutoClose(Objects.requireNonNull(getClass().getResourceAsStream("/config/master.json"))));
            master.Save(new File(cwd, "config/master.json"));
            var worker = new JsonWrapper(JsonUtil.ReadToStringAutoClose(Objects.requireNonNull(getClass().getResourceAsStream("/config/worker.json"))));
            worker.Save(new File(cwd, "config/worker.json"));

            log.error("No configuration files found. Is this the first run? Exiting now.");
            log.info("Note - Default configuration files have been created in the 'config' directory, both master.json and worker.json. If this YukiNet node is a worker-only node, please delete master.json.");

            Exit();
            console = null;
            return;
        }

        console = new InteractiveConsole();

        // create services
        if (configManager.IsMaster()) new MasterService();
        if (configManager.IsWorker()) new WorkerService();

        log.info(org.jline.jansi.Ansi.ansi().fgBrightCyan().a("Ready. Type 'help' for general guidance, 'man' for command usage, hit Ctrl+D to exit.").reset().toString());

        // block
        console.Start();
    }

    public void Exit() {
        if (ConfigManager.getInstance().IsMaster()) {
            MasterService.getInstance().Shutdown();
        }

        if (ConfigManager.getInstance().IsWorker()) {
            WorkerService.getInstance().Shutdown();
        }

        System.exit(0);
    }

    private void PrintSplash() throws IOException {
        try (var is = getClass().getResourceAsStream("/static/splash.txt")) {
            if (is == null) {
                throw new RuntimeException("Could not find splash file");
            }

            var splash = new String(is.readAllBytes());
            System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).bold().a(splash).reset());
            System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).bold().a("YukiNet v" + VERSION).reset());
        }
    }

    public static void main(String[] args) throws Exception {
        if (instance != null)
            throw new IllegalStateException("Instance already exists!");

        new YukiNet();
    }
}
