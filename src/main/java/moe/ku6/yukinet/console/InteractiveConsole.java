package moe.ku6.yukinet.console;

import lombok.extern.slf4j.Slf4j;
import moe.ku6.yukinet.YukiNet;
import moe.ku6.yukinet.command.CommandManager;
import org.apache.tools.ant.types.Commandline;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;

@Slf4j
public class InteractiveConsole {
    private final Terminal terminal;
    private final LineReader lineReader;

    public InteractiveConsole() throws IOException {
        terminal = TerminalBuilder.builder()
                .jansi(true)
                .system(true)
                .build();

        var historyFile = new File(YukiNet.getInstance().getCwd() + "/.cmd_history");
        if (!historyFile.exists()) {
            historyFile.createNewFile();
        }

        lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .variable(LineReader.HISTORY_FILE, historyFile)
                .build();
    }

    public void Start() throws IOException {
        ConsoleLoop();
        terminal.close();
    }

    public String ReadBlocking(String prompt) {
        try {
            return lineReader.readLine(prompt);

        } catch (UserInterruptException | EndOfFileException e) {
            return null;
        }
    }

    public boolean Prompt(String prompt) {
        var line = ReadBlocking("%s (y/n): ".formatted(prompt));
        if (line == null || line.isEmpty()) return false;

        return line.toLowerCase().startsWith("y");
    }

    private void ConsoleLoop() {
        while (true) {
            try {
                var line = lineReader.readLine(">> ");
                if (line.isEmpty()) continue;

                var args = Commandline.translateCommandline(line);
                if (args.length == 0) continue;

                CommandManager.getInstance().ProcessCommand(args);

            } catch (UserInterruptException | EndOfFileException e) {
                YukiNet.getInstance().Exit();
                break;
            }

        }
    }
}
