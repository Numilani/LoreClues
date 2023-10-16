package me.numilani.loreclues;

import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import me.numilani.loreclues.commands.ClueCommands;
import me.numilani.loreclues.data.IDataSourceConnector;
import me.numilani.loreclues.data.SqliteDataSourceConnector;
import me.numilani.loreclues.listeners.HintListeners;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class LoreClues extends JavaPlugin {
    public CloudSimpleHandler cmdHandler = new CloudSimpleHandler();
    public IDataSourceConnector dataSource;

    @Override
    public void onEnable() {
        // First run setup
        var isFirstRun = false;
        if (!(new FileConfiguration(this, "config.yml").exists())) {
            isFirstRun = true;
            doPluginInit();
        }

        getServer().getPluginManager().registerEvents(new HintListeners(this), this);

        try{
            dataSource = new SqliteDataSourceConnector(this);
            if (isFirstRun) dataSource.initDatabase();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        cmdHandler.enable(this);
        cmdHandler.getParser().parse(new ClueCommands(this));

    }

    private void doPluginInit() {
        var cfgFile = new FileConfiguration(this, "config.yml");
        cfgFile.set("settings.checkInterval", 90);

        cfgFile.saveSync();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
