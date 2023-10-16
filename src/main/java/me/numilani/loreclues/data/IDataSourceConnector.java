package me.numilani.loreclues.data;

import me.numilani.loreclues.models.Clue;

import java.sql.SQLException;
import java.util.List;

public interface IDataSourceConnector {
    void initDatabase() throws SQLException;
    void createClue(String serializedLocation, String message) throws SQLException;
    List<Clue> getAllClues() throws SQLException;
    Clue getClue(String serializedLocation) throws SQLException;
    void removeClue(String serializedLocation) throws SQLException;
}
