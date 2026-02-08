package game.core;

import game.actions.ActionResult;
import game.events.RiskLevel;

public final class NpcTaskOutcome {
    public final ActionResult result;
    public final RiskLevel risk;

    public NpcTaskOutcome(ActionResult result, RiskLevel risk) {
        this.result = result;
        this.risk = risk;
    }
}
