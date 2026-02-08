package game.world;

import java.util.EnumMap;
import java.util.Map;

public final class WorldMap {
    private final EnumMap<ZoneId, ZoneState> zones = new EnumMap<>(ZoneId.class);

    public WorldMap() {
        for (ZoneId id : ZoneId.values())
            zones.put(id, new ZoneState());
    }

    public ZoneState get(ZoneId id) {
        return zones.get(id);
    }

    public Map<ZoneId, ZoneState> snapshot() {
        return new EnumMap<>(zones);
    }

    public void setZone(ZoneId id, ZoneState state) {
        zones.put(id, state);
    }
}
