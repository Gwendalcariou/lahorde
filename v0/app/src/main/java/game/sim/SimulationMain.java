package game.sim;

import game.actions.ActionFactory;
import game.actions.ActionId;
import game.actions.ActionResult;
import game.actions.GameAction;
import game.core.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Headless simulation runner for balance checks.
 * Usage: java game.sim.SimulationMain [runs] [dayLimit]
 */
public final class SimulationMain {
    private SimulationMain() {
    }

    public static void main(String[] args) {
        int runs = args.length > 0 ? parseOr(args[0], 100) : 100;
        int dayLimit = args.length > 1 ? parseOr(args[1], 30) : 30;

        Summary summary = runBatch(runs, dayLimit);
        System.out.println("runs=" + runs);
        System.out.println("dayLimit=" + dayLimit);
        System.out.println("avgDaysSurvived=" + String.format("%.2f", summary.averageDays()));
        System.out.println("deaths=" + summary.deaths);
        System.out.println("survivedToLimit=" + summary.survivedToLimit);
        System.out.println("avgWaterEnd=" + String.format("%.2f", summary.averageWater()));
        System.out.println("avgFoodEnd=" + String.format("%.2f", summary.averageFood()));
        System.out.println("avgMaterialsEnd=" + String.format("%.2f", summary.averageMaterials()));
        System.out.println("avgMedicineEnd=" + String.format("%.2f", summary.averageMedicine()));
    }

    public static Summary runBatch(int runs, int dayLimit) {
        Summary s = new Summary();
        ActionFactory factory = new ActionFactory();

        for (int i = 0; i < runs; i++) {
            GameState state = new GameState();
            state.bootstrapV0();
            state.setRngSeed(1000L + i);
            Map<ActionId, GameAction> actions = factory.createAll();

            while (state.player().hp() > 0 && state.clock().days() < dayLimit) {
                ActionId next = chooseAction(state);
                GameAction action = actions.get(next);
                ActionResult result = action.apply(state);

                if (!result.ok) {
                    // fallback to rest to avoid dead loops on invalid actions
                    GameAction rest = actions.get(ActionId.REST);
                    result = rest.apply(state);
                    if (!result.ok) {
                        break;
                    }
                    state.events().maybeTrigger(rest.risk(state), state, result);
                    state.applyActionResult(result);
                    continue;
                }

                state.events().maybeTrigger(action.risk(state), state, result);
                state.applyActionResult(result);
            }

            s.daysSurvived.add((double) state.clock().days());
            if (state.player().hp() <= 0) {
                s.deaths++;
            } else {
                s.survivedToLimit++;
            }
            s.waterEnd.add((double) state.inventory().get(game.model.Resource.WATER));
            s.foodEnd.add((double) state.inventory().get(game.model.Resource.FOOD));
            s.materialsEnd.add((double) state.inventory().get(game.model.Resource.MATERIALS));
            s.medicineEnd.add((double) state.inventory().get(game.model.Resource.MEDICINE));
        }

        return s;
    }

    private static ActionId chooseAction(GameState state) {
        if (state.player().hydration() < 35 && state.inventory().get(game.model.Resource.WATER) > 0) {
            return ActionId.DRINK;
        }
        if (state.player().hunger() < 35 && state.inventory().get(game.model.Resource.FOOD) > 0) {
            return ActionId.EAT;
        }
        if (state.player().hp() < 45 && state.inventory().get(game.model.Resource.MEDICINE) > 0) {
            return ActionId.USE_MEDICINE;
        }
        if (state.player().energy() < 35) {
            return ActionId.REST;
        }

        int roll = state.rng().nextInt(100);
        if (roll < 50)
            return pickExplore(state);
        if (roll < 65)
            return ActionId.CRAFT_SIMPLE;
        if (roll < 75)
            return ActionId.UPGRADE_DEFENSE;
        if (roll < 85)
            return ActionId.REST;
        if (roll < 92)
            return ActionId.EAT;
        return ActionId.DRINK;
    }

    private static ActionId pickExplore(GameState state) {
        ActionId[] ids = new ActionId[] {
                ActionId.EXPLORE_HOUSES,
                ActionId.EXPLORE_WAREHOUSE,
                ActionId.EXPLORE_SHOPS,
                ActionId.EXPLORE_WILDS
        };
        return ids[state.rng().nextInt(ids.length)];
    }

    private static int parseOr(String raw, int fallback) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    public static final class Summary {
        public int deaths;
        public int survivedToLimit;
        public final List<Double> daysSurvived = new ArrayList<>();
        public final List<Double> waterEnd = new ArrayList<>();
        public final List<Double> foodEnd = new ArrayList<>();
        public final List<Double> materialsEnd = new ArrayList<>();
        public final List<Double> medicineEnd = new ArrayList<>();

        double averageDays() {
            return avg(daysSurvived);
        }

        double averageWater() {
            return avg(waterEnd);
        }

        double averageFood() {
            return avg(foodEnd);
        }

        double averageMaterials() {
            return avg(materialsEnd);
        }

        double averageMedicine() {
            return avg(medicineEnd);
        }

        private double avg(List<Double> values) {
            if (values.isEmpty())
                return 0;
            double sum = 0;
            for (double v : values)
                sum += v;
            return sum / values.size();
        }
    }
}
