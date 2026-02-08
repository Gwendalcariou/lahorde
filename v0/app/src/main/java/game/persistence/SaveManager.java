package game.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.core.GameState;
import game.core.LogBook;
import game.model.JobId;
import game.model.Resource;
import game.model.Survivor;
import game.world.ZoneId;
import game.world.ZoneState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class SaveManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path savePath = Path.of("save_v0.json");

    public void save(GameState state) {
        SaveData data = toData(state);

        try {
            Files.writeString(savePath, gson.toJson(data));
        } catch (IOException e) {
            throw new RuntimeException("Save failed", e);
        }
    }

    public GameState loadOrNull() {
        if (!Files.exists(savePath))
            return null;

        try {
            SaveData data = gson.fromJson(Files.readString(savePath), SaveData.class);
            return fromData(data);
        } catch (IOException e) {
            return null;
        }
    }

    public boolean hasSave() {
        return Files.exists(savePath);
    }

    // -------- mapping --------

    private SaveData toData(GameState s) {
        SaveData d = new SaveData();

        d.hp = s.player().hp();
        d.energy = s.player().energy();
        d.fatigue = s.player().fatigue();
        d.mental = s.player().mental();
        d.hydration = s.player().hydration();
        d.hunger = s.player().hunger();

        d.defense = s.base().defense();
        d.storageCap = s.base().storageCap();

        d.minutes = s.clock().minutes();
        d.threat = s.threat().value();

        java.util.HashMap<String, Integer> map = new java.util.HashMap<>();
        for (Resource r : Resource.values()) {
            map.put(r.name(), s.inventory().get(r));
        }
        d.inventory = map;

        List<SaveData.LogEntry> logs = new java.util.ArrayList<>();
        for (LogBook.Entry e : s.log().entries()) {
            SaveData.LogEntry le = new SaveData.LogEntry();
            le.channel = e.channel.name();
            le.text = e.text;
            logs.add(le);
        }
        d.logEntries = logs;

        d.techs = s.techs().snapshotNames();

        java.util.HashMap<String, SaveData.ZoneSave> zones = new java.util.HashMap<>();
        for (ZoneId id : ZoneId.values()) {
            ZoneState z = s.world().get(id);
            SaveData.ZoneSave zs = new SaveData.ZoneSave();
            zs.remainingLoot = z.remainingLoot();
            zs.danger = z.danger();
            zs.lastVisitedMinutes = z.lastVisitedMinutes();
            zones.put(id.name(), zs);
        }
        d.zones = zones;

        java.util.ArrayList<SaveData.SurvivorSave> survs = new java.util.ArrayList<>();
        for (Survivor surv : s.survivors().list()) {
            SaveData.SurvivorSave ss = new SaveData.SurvivorSave();
            ss.name = surv.name();
            ss.job = surv.job().name();
            ss.relation = surv.relation();
            ss.hp = surv.hp();
            ss.energy = surv.energy();
            ss.hydration = surv.hydration();
            ss.hunger = surv.hunger();
            ss.task = surv.currentTask() == null ? null : surv.currentTask().name();
            ss.taskRemainingMinutes = surv.taskRemainingMinutes();
            survs.add(ss);
        }
        d.survivors = survs;

        d.rngSeed = s.rngSeed();

        return d;
    }

    private GameState fromData(SaveData d) {
        GameState s = new GameState();

        // On reconstruit depuis la save
        s.player().setAll(d.hp, d.energy, d.fatigue, d.mental, d.hydration, d.hunger);
        s.base().setAll(d.defense, d.storageCap);
        s.clock().setMinutes(d.minutes);
        s.threat().setValue(d.threat);

        if (d.inventory != null) {
            for (Resource r : Resource.values()) {
                Integer v = d.inventory.get(r.name());
                s.inventory().set(r, v == null ? 0 : v);
            }
        }

        if (d.logEntries != null) {
            java.util.ArrayList<LogBook.Entry> entries = new java.util.ArrayList<>();
            for (SaveData.LogEntry le : d.logEntries) {
                if (le == null || le.text == null)
                    continue;
                LogBook.Channel ch;
                try {
                    ch = LogBook.Channel.valueOf(le.channel);
                } catch (Exception e) {
                    ch = LogBook.Channel.BASE;
                }
                entries.add(new LogBook.Entry(ch, le.text));
            }
            s.log().setEntries(entries);
        } else if (d.logLines != null) {
            s.log().setLines(d.logLines);
        }

        if (d.techs != null)
            s.techs().setAllByName(d.techs);

        if (d.zones != null) {
            for (ZoneId id : ZoneId.values()) {
                SaveData.ZoneSave zs = d.zones.get(id.name());
                if (zs != null) {
                    s.world().get(id).set(zs.remainingLoot, zs.danger, zs.lastVisitedMinutes);
                }
            }
        }

        if (d.survivors != null) {
            java.util.ArrayList<Survivor> list = new java.util.ArrayList<>();
            for (SaveData.SurvivorSave ss : d.survivors) {
                if (ss == null || ss.name == null || ss.job == null)
                    continue;
                JobId job;
                try {
                    job = JobId.valueOf(ss.job);
                } catch (Exception e) {
                    job = JobId.OUVRIER;
                }
                Survivor surv = new Survivor(ss.name, job, ss.relation);
                surv.setAll(ss.hp, ss.energy, ss.hydration, ss.hunger, ss.relation);
                if (ss.task != null) {
                    try {
                        game.core.NpcTaskId tid = game.core.NpcTaskId.valueOf(ss.task);
                        surv.startTask(tid, ss.taskRemainingMinutes);
                    } catch (Exception ignored) {
                    }
                }
                list.add(surv);
            }
            s.survivors().setAll(list);
        }
        s.setRngSeed(d.rngSeed);

        return s;
    }
}
