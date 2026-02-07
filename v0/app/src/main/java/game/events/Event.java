package game.events;

import game.actions.ActionResult;
import game.core.GameState;

public interface Event {
    EventType type();

    String description();

    void apply(GameState state, ActionResult result);
}
