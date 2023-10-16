package me.numilani.loreclues.listeners;

import me.numilani.loreclues.LoreClues;
import me.numilani.loreclues.utils.BlockLocationHelper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.sql.SQLException;

public class HintListeners implements Listener {
    private LoreClues plugin;

    public HintListeners(LoreClues plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onInteract(PlayerInteractAtEntityEvent event) throws SQLException {
        if (event.getRightClicked().getType() == EntityType.INTERACTION){
            var clue = plugin.dataSource.getClue(BlockLocationHelper.getSerializedLocation(event.getRightClicked().getLocation()));
            if (clue != null){
                event.getPlayer().sendMessage(clue.message);
            }
        }
    }
}
