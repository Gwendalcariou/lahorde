package game.core;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class NpcTaskCatalog {
    private static final List<NpcTaskDef> ALL = List.of(
            new NpcTaskDef(NpcTaskId.EXPLORE_HOUSES, "Explorer habitations", 60, 20),
            new NpcTaskDef(NpcTaskId.EXPLORE_WAREHOUSE, "Explorer entrepot", 70, 24),
            new NpcTaskDef(NpcTaskId.EXPLORE_SHOPS, "Explorer commerces", 55, 18),
            new NpcTaskDef(NpcTaskId.EXPLORE_WILDS, "Explorer zones sauvages", 80, 22),
            new NpcTaskDef(NpcTaskId.HEAL_SELF, "Se soigner", 20, 8),
            new NpcTaskDef(NpcTaskId.BUILD_DEFENSE, "Construire defenses", 60, 18));

    private static final Map<NpcTaskId, NpcTaskDef> BY_ID = new EnumMap<>(NpcTaskId.class);

    static {
        for (NpcTaskDef d : ALL)
            BY_ID.put(d.id, d);
    }

    private NpcTaskCatalog() {
    }

    public static List<NpcTaskDef> all() {
        return ALL;
    }

    public static NpcTaskDef get(NpcTaskId id) {
        return BY_ID.get(id);
    }
}
