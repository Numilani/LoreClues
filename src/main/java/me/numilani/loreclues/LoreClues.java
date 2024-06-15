package me.numilani.loreclues;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import me.numilani.loreclues.commands.ClueCommands;
import me.numilani.loreclues.data.IDataSourceConnector;
import me.numilani.loreclues.data.SqliteDataSourceConnector;
import me.numilani.loreclues.listeners.HintListeners;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.function.Function;

public final class LoreClues extends JavaPlugin {
//    public CloudSimpleHandler cmdHandler = new CloudSimpleHandler();
    public PaperCommandManager<CommandSender> cmdHandler;
    public AnnotationParser<CommandSender> cmdParser;
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

        try {
            cmdHandler = new PaperCommandManager<>(this, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
            cmdParser = new AnnotationParser<>(cmdHandler, CommandSender.class, parserParameters -> SimpleCommandMeta.empty());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
