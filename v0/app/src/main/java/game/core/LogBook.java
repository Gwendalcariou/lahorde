package game.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LogBook {
    private final List<String> lines = new ArrayList<>();

    public void add(String line) {
        lines.add(line);
    }

    public List<String> lines() {
        return Collections.unmodifiableList(lines);
    }

    public void setLines(java.util.List<String> newLines) {
        lines.clear();
        lines.addAll(newLines);
    }

}
