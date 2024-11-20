package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (knockoff.getInstance().GameManager == null) {
            if (args.length > 0) {
                if (args[0].equals("dev")) {
                    knockoff.getInstance().DevMode = true;
                    Bukkit.getServer().sendMessage(Component.text("Developer Mode enabled! You may see visual issues but this option is intended for development."));
                }
            }
            knockoff.getInstance().is_force_starting = true;
        } else {
            Bukkit.getServer().sendMessage(Component.text("[!] A game is already in progress. Please wait until the game is over to use this command again").color(NamedTextColor.RED));
        }
        return true;
    }
}