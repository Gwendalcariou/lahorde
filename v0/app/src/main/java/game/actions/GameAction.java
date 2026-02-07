package game.actions;

import game.core.GameState;
import game.events.RiskLevel;

public interface GameAction {
    ActionId id();

    String label();

    RiskLevel risk();

    ActionResult apply(GameState state);
}
