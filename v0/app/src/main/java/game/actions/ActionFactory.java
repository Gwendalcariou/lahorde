package game.actions;

import game.core.GameState;
import game.events.RiskLevel;
import game.model.Resource;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public final class ActionFactory {
    private final Random rng = new Random();

    public Map<ActionId, GameAction> createAll() {
        EnumMap<ActionId, GameAction> map = new EnumMap<>(ActionId.class);

        map.put(ActionId.REST, new GameAction() {
            public ActionId id() {
                return ActionId.REST;
            }

            public String label() {
                return "Se reposer (30 min)";
            }

            public RiskLevel risk() {
                return RiskLevel.R0;
            }

            public ActionResult apply(GameState s) {
                ActionResult r = new ActionResult();
                r.title = "Repos";
                r.minutesCost = 30;
                r.energyDelta = +20;
                r.fatigueDelta = -15;
                r.threatDelta = +2;
                r.logLines().add("Tu souffles un peu. Énergie +20, fatigue -15.");
                return r;
            }
        });

        map.put(ActionId.DRINK, new GameAction() {
            public ActionId id() {
                return ActionId.DRINK;
            }

            public String label() {
                return "Boire (eau -1)";
            }

            public RiskLevel risk() {
                return RiskLevel.R0;
            }

            public ActionResult apply(GameState s) {
                if (!s.inventory().tryConsume(Resource.WATER, 1)) {
                    return ActionResult.fail("Pas d'eau.");
                }
                ActionResult r = new ActionResult();
                r.title = "Boire";
                r.minutesCost = 2;
                r.energyDelta = +0;
                r.fatigueDelta = -2;
                r.threatDelta = 0;
                r.logLines().add("Tu bois une ration d'eau.");
                return r;
            }
        });

        map.put(ActionId.EAT, new GameAction() {
            public ActionId id() {
                return ActionId.EAT;
            }

            public String label() {
                return "Manger (nourriture -1)";
            }

            public RiskLevel risk() {
                return RiskLevel.R0;
            }

            public ActionResult apply(GameState s) {
                if (!s.inventory().tryConsume(Resource.FOOD, 1)) {
                    return ActionResult.fail("Pas de nourriture.");
                }
                ActionResult r = new ActionResult();
                r.title = "Manger";
                r.minutesCost = 5;
                r.energyDelta = +10;
                r.fatigueDelta = -2;
                r.threatDelta = 0;
                r.logLines().add("Tu manges quelque chose. Énergie +10.");
                return r;
            }
        });

        map.put(ActionId.CRAFT_SIMPLE, new GameAction() {
            public ActionId id() {
                return ActionId.CRAFT_SIMPLE;
            }

            public String label() {
                return "Fabriquer (matériaux -3)";
            }

            public RiskLevel risk() {
                return RiskLevel.R2;
            }

            public ActionResult apply(GameState s) {
                if (!s.inventory().tryConsume(Resource.MATERIALS, 3)) {
                    return ActionResult.fail("Pas assez de matériaux (3 requis).");
                }
                ActionResult r = new ActionResult();
                r.title = "Fabrication";
                r.minutesCost = 45;
                r.energyDelta = -15;
                r.fatigueDelta = +8;
                r.threatDelta = +6;
                r.logLines().add("Tu bricoles un petit équipement utile (V0: abstraction).");
                return r;
            }
        });

        map.put(ActionId.UPGRADE_DEFENSE, new GameAction() {
            public ActionId id() {
                return ActionId.UPGRADE_DEFENSE;
            }

            public String label() {
                return "Renforcer défense (matériaux -6)";
            }

            public RiskLevel risk() {
                return RiskLevel.R2;
            }

            public ActionResult apply(GameState s) {
                if (!s.inventory().tryConsume(Resource.MATERIALS, 6)) {
                    return ActionResult.fail("Pas assez de matériaux (6 requis).");
                }
                s.base().upgradeDefense();
                ActionResult r = new ActionResult();
                r.title = "Renforts";
                r.minutesCost = 60;
                r.energyDelta = -20;
                r.fatigueDelta = +10;
                r.threatDelta = +7;
                r.logLines().add("Tu renforces la base. Défense +5.");
                return r;
            }
        });

        // Exploration = loot + risque + menace
        map.put(ActionId.EXPLORE_HOUSES, explore("Explorer zone d'habitation", RiskLevel.R3, 60, 25, 10, 8,
                new int[] { 0, 2 }, new int[] { 0, 2 }, new int[] { 1, 4 }, new int[] { 0, 1 }));

        map.put(ActionId.EXPLORE_WAREHOUSE, explore("Explorer entrepôt", RiskLevel.R3, 70, 28, 12, 9,
                new int[] { 0, 1 }, new int[] { 0, 1 }, new int[] { 3, 7 }, new int[] { 0, 2 }));

        map.put(ActionId.EXPLORE_SHOPS, explore("Explorer zone commerciale", RiskLevel.R2, 55, 22, 8, 7,
                new int[] { 1, 4 }, new int[] { 0, 2 }, new int[] { 0, 3 }, new int[] { 0, 1 }));

        map.put(ActionId.EXPLORE_WILDS, explore("Explorer zone libre (chasse)", RiskLevel.R2, 80, 25, 10, 6,
                new int[] { 0, 2 }, new int[] { 2, 5 }, new int[] { 0, 2 }, new int[] { 0, 1 }));

        return map;
    }

    private GameAction explore(String label, RiskLevel risk, long minutes, int energyCost, int fatigueAdd,
            int threatAdd,
            int[] waterRange, int[] foodRange, int[] matRange, int[] medRange) {

        return new GameAction() {
            public ActionId id() {
                return ActionId.valueOf(labelToId(label));
            }

            public String label() {
                return label + " (" + minutes + " min)";
            }

            public RiskLevel risk() {
                return risk;
            }

            public ActionResult apply(GameState s) {
                if (!s.player().spendEnergy(energyCost)) {
                    return ActionResult.fail("Pas assez d'énergie.");
                }

                ActionResult r = new ActionResult();
                r.title = label;
                r.minutesCost = minutes;
                r.energyDelta = 0; // déjà dépensée
                r.fatigueDelta = fatigueAdd;
                r.threatDelta = threatAdd;

                int w = randIn(waterRange);
                int f = randIn(foodRange);
                int m = randIn(matRange);
                int med = randIn(medRange);

                if (w > 0)
                    s.inventory().add(Resource.WATER, w);
                if (f > 0)
                    s.inventory().add(Resource.FOOD, f);
                if (m > 0)
                    s.inventory().add(Resource.MATERIALS, m);
                if (med > 0)
                    s.inventory().add(Resource.MEDICINE, med);

                r.logLines()
                        .add("Loot: eau +" + w + ", nourriture +" + f + ", matériaux +" + m + ", médocs +" + med + ".");
                return r;
            }

            private int randIn(int[] range) {
                int a = range[0], b = range[1];
                if (b < a)
                    return 0;
                return a + rng.nextInt(b - a + 1);
            }

            private String labelToId(String lbl) {
                // mapping simple basé sur ton set V0
                if (lbl.contains("habitation"))
                    return "EXPLORE_HOUSES";
                if (lbl.contains("entrepôt"))
                    return "EXPLORE_WAREHOUSE";
                if (lbl.contains("commerciale"))
                    return "EXPLORE_SHOPS";
                return "EXPLORE_WILDS";
            }
        };
    }
}
