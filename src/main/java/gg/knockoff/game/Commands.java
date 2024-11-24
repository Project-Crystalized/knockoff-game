package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {

        switch (label) {
            case "knockoff":
                return run_knockoff(args, commandSender);
            default:
                return false;
        }
    }

    private boolean run_knockoff(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        if (args.length > 0) {
            if (args.length > 1) {
                player.sendMessage(Component.text("[!] Too many arguments"));
            } else {
                if (args[0].equals("start")) {
                    if (knockoff.getInstance().GameManager == null) {
                        knockoff.getInstance().is_force_starting = true;
                    } else {
                    player.sendMessage(Component.text("[!] A game is already in progress. Please wait until the game is over to use this command again").color(NamedTextColor.RED));
                    }
                } else if (args[0].equals("startdev")) {
                    if (knockoff.getInstance().GameManager == null) {
                        knockoff.getInstance().DevMode = true;
                        Bukkit.getServer().sendMessage(Component.text("Developer Mode enabled! You may see visual issues but this option is intended for development."));
                        knockoff.getInstance().is_force_starting = true;
                    } else {
                        player.sendMessage(Component.text("[!] A game is already in progress. Please wait until the game is over to use this command again").color(NamedTextColor.RED));
                    }
                } else if (args[0].equals("end")) {
                    if (knockoff.getInstance().GameManager != null) {
                        player.sendMessage(Component.text("[!] not implemented yet").color(NamedTextColor.RED)); //TODO
                    } else {
                        player.sendMessage(Component.text("[!] This command cannot be used in the queue").color(NamedTextColor.RED));
                    }
                } else if (args[0].equals("reload_config")) {
                    if (knockoff.getInstance().GameManager == null) {
                        player.sendMessage(Component.text("[!] not implemented yet").color(NamedTextColor.RED)); //TODO
                    } else {
                        player.sendMessage(Component.text("[!] This command cannot be used in-game").color(NamedTextColor.RED));
                    }
                }
            }
        } else {
            player.sendMessage(Component.text("[!] Incorrect usage of command /knockoff.")
                    .append(Component.text("\n    Usage: /knockoff [start/end/reload_config]")));
        }
        return true;
    }
}