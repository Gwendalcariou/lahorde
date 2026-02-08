package game.core;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public final class TechTree {
    private final EnumSet<TechId> unlocked = EnumSet.noneOf(TechId.class);

    public boolean isUnlocked(TechId id) {
        return unlocked.contains(id);
    }

    public void unlock(TechId id) {
        unlocked.add(id);
    }

    public void setAll(Collection<TechId> ids) {
        unlocked.clear();
        unlocked.addAll(ids);
    }

    public void setAllByName(List<String> names) {
        unlocked.clear();
        if (names == null)
            return;
        for (String n : names) {
            try {
                unlocked.add(TechId.valueOf(n));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public List<String> snapshotNames() {
        return unlocked.stream().map(Enum::name).toList();
    }
}
