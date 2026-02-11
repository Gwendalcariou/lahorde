package game.actions;

import game.core.GameState;
import game.events.RiskLevel;
import game.model.Resource;
import game.world.ZoneId;

import java.util.EnumMap;
import java.util.Map;

public final class ActionFactory {

    public Map<ActionId, GameAction> createAll() {
        EnumMap<ActionId, GameAction> map = new EnumMap<>(ActionId.class);

        map.put(ActionId.REST, new GameAction() {
            public ActionId id() {
                return ActionId.REST;
            }

            public String label() {
                return "Se reposer (30 min)";
            }

            public RiskLevel risk(GameState s) {
                return RiskLevel.R0;
            }

            public ActionResult apply(GameState s) {
                if (s.player().hunger() == 0) {
                    return ActionResult.fail("Tu es trop affame pour bien recuperer.");
                }

                ActionResult r = new ActionResult();
                r.title = "Repos";
                r.minutesCost = 30;
                r.energyDelta = +20;
                r.fatigueDelta = -15;
                r.threatDelta = +2;
                r.logLines().add("Tu souffles un peu. Energie +20, fatigue -15.");
                return r;
            }
        });

        map.put(ActionId.SLEEP, new GameAction() {
            public ActionId id() {
                return ActionId.SLEEP;
            }

            public String label() {
                return "Dormir (8h)";
            }

            public RiskLevel risk(GameState s) {
                return RiskLevel.R0;
            }

            public ActionResult apply(GameState s) {
                ActionResult r = new ActionResult();
                r.title = "Sommeil";
                r.minutesCost = 8 * 60;
                r.energyDelta = 0;
                r.fatigueDelta = -30;
                r.threatDelta = +3;
                s.player().gainEnergy(100);
                r.logLines().add("Tu dors 8h. Energie restauree au maximum.");
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

            public RiskLevel risk(GameState s) {
                return RiskLevel.R0;
            }

            public ActionResult apply(GameState s) {
                if (!s.inventory().tryConsume(Resource.WATER, 1)) {
                    return ActionResult.fail("Pas d'eau.");
                }
                ActionResult r = new ActionResult();
                r.title = "Boire";
                r.minutesCost = 2;
                r.energyDelta = 0;
                r.fatigueDelta = -2;
                r.threatDelta = 0;
                r.logLines().add("Tu bois une ration d'eau (+25 hydratation).");
                s.player().addHydration(25);
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

            public RiskLevel risk(GameState s) {
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
                r.logLines().add("Tu manges quelque chose (+20 satiete & +10 energie).");
                s.player().addHunger(20);
                return r;
            }
        });

        map.put(ActionId.USE_MEDICINE, new GameAction() {
            public ActionId id() {
                return ActionId.USE_MEDICINE;
            }

            public String label() {
                return "Se soigner (medicaments -1)";
            }

            public RiskLevel risk(GameState s) {
                return RiskLevel.R0;
            }

            public ActionResult apply(GameState s) {
                if (!s.inventory().tryConsume(Resource.MEDICINE, 1)) {
                    return ActionResult.fail("Pas de medicaments.");
                }
                int heal = s.rules().medicineHealAmount(s);
                s.player().heal(heal);

                ActionResult r = new ActionResult();
                r.title = "Soins";
                r.minutesCost = 10;
                r.energyDelta = 0;
                r.fatigueDelta = -2;
                r.threatDelta = 0;
                r.logLines().add("Tu te soignes (+" + heal + " PV).");
                return r;
            }
        });

        map.put(ActionId.CRAFT_SIMPLE, new GameAction() {
            public ActionId id() {
                return ActionId.CRAFT_SIMPLE;
            }

            public String label() {
                return "Fabriquer (materiaux)";
            }

            public RiskLevel risk(GameState s) {
                return RiskLevel.R2;
            }

            public ActionResult apply(GameState s) {
                int cost = s.rules().craftSimpleMaterialsCost(s);
                if (!s.inventory().tryConsume(Resource.MATERIALS, cost)) {
                    return ActionResult.fail("Pas assez de materiaux (" + cost + " requis).");
                }
                int energyCost = s.rules().adjustedEnergyCost(s, 15);
                if (!s.player().spendEnergy(energyCost)) {
                    s.inventory().add(Resource.MATERIALS, cost);
                    return ActionResult.fail("Pas assez d'energie.");
                }
                ActionResult r = new ActionResult();
                r.title = "Fabrication";
                r.minutesCost = 45;
                r.energyDelta = 0;
                r.fatigueDelta = +8;
                r.threatDelta = +6;
                r.logLines().add("Tu bricoles un petit equipement utile (V0: abstraction).");
                return r;
            }
        });

        map.put(ActionId.UPGRADE_DEFENSE, new GameAction() {
            public ActionId id() {
                return ActionId.UPGRADE_DEFENSE;
            }

            public String label() {
                return "Renforcer defense (materiaux -6)";
            }

            public RiskLevel risk(GameState s) {
                return RiskLevel.R2;
            }

            public ActionResult apply(GameState s) {
                if (!s.inventory().tryConsume(Resource.MATERIALS, 6)) {
                    return ActionResult.fail("Pas assez de materiaux (6 requis).");
                }
                int energyCost = s.rules().adjustedEnergyCost(s, 20);
                if (!s.player().spendEnergy(energyCost)) {
                    s.inventory().add(Resource.MATERIALS, 6);
                    return ActionResult.fail("Pas assez d'energie.");
                }
                s.base().upgradeDefense();
                ActionResult r = new ActionResult();
                r.title = "Renforts";
                r.minutesCost = 60;
                r.energyDelta = 0;
                r.fatigueDelta = +10;
                r.threatDelta = +7;
                r.logLines().add("Tu renforces la base. Defense +5.");
                return r;
            }
        });

        // Exploration = loot + risque + menace
        map.put(ActionId.EXPLORE_HOUSES, exploreZone(ZoneId.HOUSES, "Explorer zone d'habitation", 60, 25, 10, 8));

        map.put(ActionId.EXPLORE_WAREHOUSE, exploreZone(ZoneId.WAREHOUSE, "Explorer entrepôt", 70, 28, 12, 9));

        map.put(ActionId.EXPLORE_SHOPS, exploreZone(ZoneId.SHOPS, "Explorer zone commerciale", 55, 22, 8, 7));

        map.put(ActionId.EXPLORE_WILDS, exploreZone(ZoneId.WILDS, "Explorer zone libre (chasse)", 80, 25, 10, 6));

        return map;
    }

    private GameAction exploreZone(ZoneId zone, String label, long minutes, int energyCost, int fatigueAdd,
            int threatAdd) {

        return new GameAction() {
            public ActionId id() {
                return ActionId.valueOf("EXPLORE_" + zone.name());
            }

            public String label() {
                return label + " (" + minutes + " min)";
            }

            public RiskLevel risk(GameState s) {
                return s.rules().riskForZone(s, zone);
            }

            public ActionResult apply(GameState s) {
                int adjustedEnergy = s.rules().adjustedEnergyCost(s, energyCost);
                if (!s.player().spendEnergy(adjustedEnergy)) {
                    return ActionResult.fail("Pas assez d'energie.");
                }

                ActionResult r = new ActionResult();
                r.title = label;
                r.minutesCost = minutes;
                r.energyDelta = 0; // déjà dépensée
                r.fatigueDelta = fatigueAdd;
                int nightThreat = s.rules().explorationThreatBonus(s);
                r.threatDelta = threatAdd + nightThreat;

                var zoneState = s.world().get(zone);
                r.logLines().add("Zone: " + zone.name() + " | Loot " + zoneState.remainingLoot() + "% | Danger "
                        + zoneState.danger() + "%");
                if (nightThreat > 0) {
                    r.logLines().add("Nuit: energie +cout et menace +" + nightThreat + " en exploration.");
                }

                var loot = s.rules().rollLoot(s, zone);

                r.logLines().add("Resultat exploration :");
                s.addLoot(Resource.WATER, loot.water, r);
                s.addLoot(Resource.FOOD, loot.food, r);
                s.addLoot(Resource.MATERIALS, loot.materials, r);
                s.addLoot(Resource.MEDICINE, loot.medicine, r);

                long visitAt = s.clock().minutes() + r.minutesCost;
                s.world().get(zone).onVisit(loot.total(), visitAt);
                return r;
            }
        };
    }
}
