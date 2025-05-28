package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands_deprecated implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {

        /*switch (label) {
            case "knockoff":
                return run_knockoff(args, commandSender);
            case "knockoff_give":
                return run_give(args, commandSender);
            case "knockoff_dropitem":
                return run_dropitem(args, commandSender);
            case "knockoff_debug":
                return run_debug(args, commandSender);
            default:
                return false;
        }*/
        return false;
    }

    private boolean run_knockoff(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        if (args.length > 0) {
            if (args.length > 1) {
                player.sendMessage(Component.text("[!] Too many arguments"));
            } else {
                if (args[0].equals("start")) {
                    if (knockoff.getInstance().GameManager == null) {
                        knockoff.getInstance().DevMode = false;
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
                        knockoff.getInstance().DevMode = false;
                        knockoff.getInstance().GameManager.ForceEndGame();
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

    private boolean run_give(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        if (args.length > 0) {
            KnockoffItem.GiveCustomItem(player, args[0]);
        } else {
            player.sendMessage(Component.text("[!] Incorrect usage of command /knockoff_give. No arguments. Args: /knockoff_give [item] [player]"));
        }
        return true;
    }

    private boolean run_dropitem(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        if (args.length > 0) {
            if (knockoff.getInstance().GameManager != null) {
                Location loc = new Location(player.getWorld(), knockoff.getInstance().mapdata.getCurrentMiddleXLength(), knockoff.getInstance().mapdata.getCurrentYLength(), knockoff.getInstance().mapdata.getCurrentMiddleZLength());
                DropPowerup.DropPowerup(loc, args[0]);
            } else {
                player.sendMessage(Component.text("[!] This command can only be executed when a game is in progress"));
            }
        } else {
            player.sendMessage(Component.text("[!] Incorrect usage of command /knockoff_dropitem. No arguments. Args: /knockoff_dropitem [item]"));
        }
        return true;
    }

    private boolean run_debug(String[] args, CommandSender commandSender) {
        if (args.length > 0) {
            if (args[0].equals("move")) {
                knockoff.getInstance().GameManager.CloneNewMapSection();
            }
        } else {
        }
        return true;
    }
}