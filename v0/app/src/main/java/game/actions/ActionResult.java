package game.actions;

import java.util.ArrayList;
import java.util.List;

public final class ActionResult {
    public boolean ok = true;
    public String title = "";
    public long minutesCost = 0;
    public int energyDelta = 0; // + = gain, - = dépense
    public int fatigueDelta = 0; // + = augmente fatigue
    public int threatDelta = 0;

    private final List<String> logLines = new ArrayList<>();

    public List<String> logLines() {
        return logLines;
    }

    public static ActionResult fail(String msg) {
        ActionResult r = new ActionResult();
        r.ok = false;
        r.title = msg;
        r.logLines.add(msg);
        return r;
    }
}

