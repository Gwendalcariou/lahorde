package game.persistence;

import java.util.List;
import java.util.Map;

public final class SaveData {
    // --- Player ---
    public int hp;
    public int energy;
    public int fatigue;
    public int mental;

    // --- Base ---
    public int defense;
    public int storageCap;

    // --- Time / threat ---
    public long minutes;
    public int threat;

    // --- Inventory ---
    public Map<String, Integer> inventory; // keys = "WATER", "FOOD", ...

    // --- Log ---
    public List<String> logLines;
}
