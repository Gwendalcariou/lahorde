package game.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.core.GameState;
import game.model.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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

        d.logLines = List.copyOf(s.log().lines());

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

        if (d.logLines != null)
            s.log().setLines(d.logLines);
        s.setRngSeed(d.rngSeed);

        return s;
    }
}
