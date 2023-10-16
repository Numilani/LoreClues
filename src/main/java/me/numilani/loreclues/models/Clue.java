package me.numilani.loreclues.models;

import lombok.Data;
import org.bukkit.Location;

@Data
public class Clue {
    public int Id;
    public Location location;
    public String message;
}
