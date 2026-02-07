package game.events;

import game.actions.ActionResult;
import game.core.GameState;
import game.model.Resource;

import java.util.Random;

public final class EventPool {
    private final Random rng = new Random();

    public void maybeTrigger(RiskLevel risk, GameState state, ActionResult result) {
        if (risk == RiskLevel.R0)
            return;

        // Probabilité d'avoir un event : 10% * niveau (R1=10%, R5=50%)
        int p = risk.level * 10;
        int roll = rng.nextInt(100) + 1;
        if (roll > p)
            return;

        // Tirage d'événement (V0 : 3 events)
        int pick = rng.nextInt(3);
        switch (pick) {
            case 0 -> { // loot bonus
                int bonus = 1 + rng.nextInt(3);
                state.inventory().add(Resource.MATERIALS, bonus);
                result.logLines().add("Événement: zone de loot abondante (+" + bonus + " matériaux).");
                result.threatDelta += 2;
            }
            case 1 -> { // blessure mineure
                int dmg = 5 + rng.nextInt(8);
                state.player().damage(dmg);
                result.logLines().add("Événement: blessure mineure (-" + dmg + " PV).");
                result.threatDelta += 1;
            }
            case 2 -> { // bruit
                int spike = 6 + rng.nextInt(8);
                result.logLines().add("Événement: bruit ! menace +" + spike + ".");
                result.threatDelta += spike;
            }
        }
    }
}
