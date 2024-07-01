package me.numilani.loreclues;

import com.bergerkiller.bukkit.common.config.FileConfiguration;
import me.numilani.loreclues.commands.ClueCommands;
import me.numilani.loreclues.data.IDataSourceConnector;
import me.numilani.loreclues.data.SqliteDataSourceConnector;
import me.numilani.loreclues.listeners.HintListeners;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;

import java.sql.SQLException;
import java.util.function.Function;

public final class LoreClues extends JavaPlugin {
//    public CloudSimpleHandler cmdHandler = new CloudSimpleHandler();
    public LegacyPaperCommandManager<CommandSender> cmdHandler;
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
            cmdHandler = LegacyPaperCommandManager.createNative(this, ExecutionCoordinator.simpleCoordinator());
            cmdParser = new AnnotationParser<>(cmdHandler, CommandSender.class, parserParameters -> SimpleCommandMeta.empty());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        cmdParser.parse(new ClueCommands(this));
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
