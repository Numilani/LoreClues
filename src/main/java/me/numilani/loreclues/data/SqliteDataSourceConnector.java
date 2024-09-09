package me.numilani.loreclues.data;

import me.numilani.loreclues.LoreClues;
import me.numilani.loreclues.models.Clue;
import me.numilani.loreclues.utils.BlockLocationHelper;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteDataSourceConnector implements IDataSourceConnector{
    private LoreClues plugin;
    private String dbFilename = "loreclues.db";
    private Connection conn;

    public SqliteDataSourceConnector(LoreClues plugin) throws SQLException {
        this.plugin = plugin;
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), dbFilename).getPath());
    }

    @Override
    public void initDatabase() throws SQLException {
        var statement = conn.createStatement();

        statement.execute("CREATE TABLE Clues(id INTEGER PRIMARY KEY AUTOINCREMENT, location TEXT, message TEXT)");
    }

    @Override
    public void createClue(String serializedLocation, String message) throws SQLException {
        var statement = conn.prepareStatement("INSERT INTO Clues (location, message) VALUES (?, ?)");
        statement.setString(1, serializedLocation);
        statement.setString(2, message);
        statement.execute();
    }

    @Override
    public List<Clue> getAllClues() throws SQLException {
        List<Clue> retval = new ArrayList<>();

        var statement = conn.createStatement();
        var x = statement.executeQuery("SELECT id, location, message from Clues");

        while (x.next()){
            var clue = new Clue();
            clue.Id = x.getInt(1);
            clue.location = BlockLocationHelper.getDeserializedLocation(x.getString(2));
            clue.message = x.getString(3);

            retval.add(clue);
        }

        return retval;
    }

    @Override
    public Clue getClue(String serializedLocation) throws SQLException {
        var statement = conn.prepareStatement("SELECT id, location, message from Clues where location = ?");
        statement.setString(1, serializedLocation);
        var x = statement.executeQuery();


        while (x.next()){
            var clue = new Clue();
            clue.Id = x.getInt(1);
            clue.location = BlockLocationHelper.getDeserializedLocation(x.getString(2));
            clue.message = x.getString(3);
            return clue;
        }
        return null;
    }

    @Override
    public void removeClue(String serializedLocation) throws SQLException {
        var statement = conn.prepareStatement("DELETE FROM Clues WHERE location = ?");
        statement.setString(1, serializedLocation);
        statement.execute();
    }
}
