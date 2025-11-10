package moe.ku6.yukinet.command.impl;

import lombok.extern.slf4j.Slf4j;
import moe.ku6.yukinet.YukiNet;
import moe.ku6.yukinet.command.ICommand;
import moe.ku6.yukinet.console.InteractiveConsole;

@Slf4j
public class ExitCommand implements ICommand<Object> {
    @Override
    public String[] GetNames() {
        return new String[] {"exit", "quit"};
    }

    @Override
    public String GetManual() {
        return "Exit the program.";
    }

    @Override
    public void HandleInternal(Object args) throws Exception {
        YukiNet.getInstance().Exit();
    }
}
