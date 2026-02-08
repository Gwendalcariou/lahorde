package game.events;

import game.actions.ActionResult;
import game.core.GameState;
import game.model.Resource;

public final class EventPool {

    public void maybeTrigger(RiskLevel risk, GameState state, ActionResult result) {
        if (risk == RiskLevel.R0)
            return;

        int base = risk.level * 10; // 10..50
        int threatBonus = state.threat().value() / 10; // 0..10
        int p = Math.min(80, base + threatBonus);

        int roll = state.rng().nextInt(100) + 1;
        if (roll > p)
            return;

        // Pondération simple: menace haute => plus d'events dangereux
        int threat = state.threat().value();

        int pick;
        if (threat >= 70) {
            // 0..99 : plus d'embuscades
            pick = weightedPick(state, new int[] { 20, 20, 20, 40 }); // loot, injury, noise, ambush
            trigger(pick, state, result, risk);
        } else {
            pick = weightedPick(state, new int[] { 35, 20, 30, 15 }); // loot, injury, noise, ambush
            trigger(pick, state, result, risk);
        }
    }

    private int weightedPick(GameState s, int[] weights) {
        int total = 0;
        for (int w : weights)
            total += w;
        int r = s.rng().nextInt(total);
        int acc = 0;
        for (int i = 0; i < weights.length; i++) {
            acc += weights[i];
            if (r < acc)
                return i;
        }
        return weights.length - 1;
    }

    private void trigger(int pick, GameState state, ActionResult result, RiskLevel risk) {
        switch (pick) {
            case 0 -> { // FOUND_SURVIVOR_CACHE (loot gros)
                int bonusMat = 4 + state.rng().nextInt(6); // 4..9
                int bonusWater = 1 + state.rng().nextInt(3);
                int spike = 8 + state.rng().nextInt(8);

                result.logLines().add("Evenement: cache de survivant !");
                state.addLoot(Resource.MATERIALS, bonusMat, result);
                state.addLoot(Resource.WATER, bonusWater, result);

                result.threatDelta += spike;
                result.logLines().add("Le remue-menage attire des rodeurs, menace +" + spike + ".");
            }

            case 1 -> { // MINOR_INJURY
                int dmg = 5 + state.rng().nextInt(8);
                state.player().damage(dmg);
                result.logLines().add("Evenement: blessure (-" + dmg + " PV).");
                result.threatDelta += 2;
            }

            case 2 -> { // NOISE_SPIKE
                int spike = 6 + state.rng().nextInt(10);
                result.logLines().add("Evenement: bruit ! menace +" + spike + ".");
                result.threatDelta += spike;
            }

            case 3 -> { // AMBUSH
                int dmg = 10 + state.rng().nextInt(12);
                int spike = 10 + state.rng().nextInt(10);

                state.player().damage(dmg);
                result.threatDelta += spike;

                result.logLines().add("Evenement: embuscade !");
                result.logLines().add("-" + dmg + " PV, menace +" + spike + ".");
            }
        }
    }
}

