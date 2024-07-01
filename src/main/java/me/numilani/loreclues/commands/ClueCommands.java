package me.numilani.loreclues.commands;

import me.numilani.loreclues.LoreClues;
import me.numilani.loreclues.utils.BlockLocationHelper;
import me.numilani.loreclues.utils.PlayerLookHelper;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.sql.SQLException;

public class ClueCommands {
    private LoreClues plugin;

    public ClueCommands(LoreClues plugin) {
        this.plugin = plugin;
    }

    @Permission("loreclues.admin")
    @Command("clue add <msg>")
    public void addClue(CommandSender sender, @Argument("msg") @Greedy String message) throws SQLException {
        if (!(sender instanceof Player)){
            sender.sendMessage("You must run this command as a player!");
            return;
        }

        var player = ((Player)sender);

        player.getWorld().spawnEntity(player.getLocation(), EntityType.INTERACTION);
        plugin.dataSource.createClue(BlockLocationHelper.getSerializedLocation(player.getLocation()), message);

        sender.sendMessage("Clue created at your location!");
    }

    @Permission("loreclues.admin")
    @Command("clue remove")
    public void removeClue(CommandSender sender) throws SQLException {
        if (!(sender instanceof Player)){
            sender.sendMessage("You must run this command as a player!");
            return;
        }

        var player = ((Player)sender);

        var target = PlayerLookHelper.getTargetEntity(player);

        if (target != null && target.getType() == EntityType.INTERACTION){
            if (plugin.dataSource.getClue(BlockLocationHelper.getSerializedLocation(target.getLocation())) != null){
                plugin.dataSource.removeClue(BlockLocationHelper.getSerializedLocation(target.getLocation()));
                target.remove();
                sender.sendMessage("Clue removed!");
                return;
            }
            sender.sendMessage("There's no clue at that location...");
            return;
        }
        sender.sendMessage("There's no clue at that location...");
    }

    @Command("investigate")
    public void viewNearbyClues(CommandSender sender) throws SQLException {
        if (!(sender instanceof Player)){
            sender.sendMessage("You must run this command as a player!");
            return;
        }

        var player = ((Player)sender);

        for (var entity : player.getWorld().getNearbyEntities(player.getLocation(), 32, 32, 32, x -> x.getType() == EntityType.INTERACTION)) {
            if (plugin.dataSource.getClue(BlockLocationHelper.getSerializedLocation(entity.getLocation())) != null){
                player.getWorld().spawnParticle(Particle.SHRIEK, entity.getLocation(), 10, 20);
                player.getWorld().spawnParticle(Particle.SHRIEK, entity.getLocation(), 10, 40);
                player.getWorld().spawnParticle(Particle.SHRIEK, entity.getLocation(), 10, 60);
            }
        }

    }

}
