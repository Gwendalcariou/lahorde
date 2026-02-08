package game.core;

import game.model.Survivor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SurvivorRoster {
    private final List<Survivor> survivors = new ArrayList<>();

    public void add(Survivor s) {
        if (s != null)
            survivors.add(s);
    }

    public List<Survivor> list() {
        return Collections.unmodifiableList(survivors);
    }

    public void setAll(List<Survivor> list) {
        survivors.clear();
        if (list != null)
            survivors.addAll(list);
    }
}
