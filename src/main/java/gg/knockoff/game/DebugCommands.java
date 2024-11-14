package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DebugCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {

        switch (label) {
            case "force_start":
                return run_force_start(args, commandSender);
            default:
                return false;
        }
    }

    private boolean run_force_start(String[] args, CommandSender commandSender) {
        knockoff.getInstance().is_force_starting = true;
        Bukkit.getServer().sendMessage(Component.text("Force starting the GAME!!!"));
        return true;
    }
}
