package game.persistence;

import java.util.List;
import java.util.Map;

public final class SaveData {
    public int saveVersion;

    // --- Player ---
    public int hp;
    public int energy;
    public int fatigue;
    public int mental;
    public int hydration;
    public int hunger;

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
    public List<LogEntry> logEntries;

    // --- Techs ---
    public List<String> techs;

    // --- World map ---
    public Map<String, ZoneSave> zones; // keys = "HOUSES", "WAREHOUSE", ...

    // --- Survivors ---
    public List<SurvivorSave> survivors;

    public long rngSeed;

    public static final class ZoneSave {
        public int remainingLoot;
        public int danger;
        public long lastVisitedMinutes;
    }

    public static final class LogEntry {
        public String channel;
        public String text;
    }

    public static final class SurvivorSave {
        public String name;
        public String job;
        public int relation;
        public int hp;
        public int energy;
        public int hydration;
        public int hunger;
        public String task;
        public long taskRemainingMinutes;
    }

}
