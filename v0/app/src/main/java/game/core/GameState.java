package game.core;

import game.actions.ActionResult;
import game.events.EventPool;
import game.model.Base;
import game.model.Inventory;
import game.model.Player;
import game.model.Resource;
import game.model.Survivor;
import game.world.WorldMap;
import java.util.Random;

public final class GameState {
    private final GameConfig config = new GameConfig();
    private final Player player = new Player();
    private final Base base = new Base();
    private final Inventory inventory = new Inventory();
    private final TimeClock clock = new TimeClock();
    private final ThreatMeter threat = new ThreatMeter();
    private final LogBook log = new LogBook();
    private final EventPool events = new EventPool();
    private final TechTree techs = new TechTree();
    private final WorldMap world = new WorldMap();
    private final GameRules rules = new GameRules();
    private final SurvivorRoster survivors = new SurvivorRoster();
    private long playerHydrationDrainCarryMinutes;
    private long playerHungerDrainCarryMinutes;

    public GameConfig config() {
        return config;
    }

    public Player player() {
        return player;
    }

    public Base base() {
        return base;
    }

    public Inventory inventory() {
        return inventory;
    }

    public TimeClock clock() {
        return clock;
    }

    public ThreatMeter threat() {
        return threat;
    }

    public LogBook log() {
        return log;
    }

    public EventPool events() {
        return events;
    }

    public TechTree techs() {
        return techs;
    }

    public WorldMap world() {
        return world;
    }

    public GameRules rules() {
        return rules;
    }

    public SurvivorRoster survivors() {
        return survivors;
    }

    public boolean isPlayerDead() {
        return player.hp() <= 0;
    }

    public boolean isVictoryAchieved() {
        for (TechId id : TechId.values()) {
            if (!techs.isUnlocked(id))
                return false;
        }
        return true;
    }

    public void bootstrapV0() {
        inventory.add(game.model.Resource.WATER, 8);
        inventory.add(game.model.Resource.FOOD, 6);
        inventory.add(game.model.Resource.MATERIALS, 10);
        inventory.add(game.model.Resource.MEDICINE, 2);
        log.add("Debut de partie. La base tient encore... pour l'instant.");

        survivors.add(new Survivor("Maya", game.model.JobId.MEDECIN, 60));
        survivors.add(new Survivor("Liam", game.model.JobId.OUVRIER, 50));
    }

    public ActionResult assignNpcTask(Survivor s, NpcTaskId taskId) {
        if (s == null || taskId == null)
            return ActionResult.fail("Tache invalide.");

        if (isVictoryAchieved())
            return ActionResult.fail("Partie deja gagnee.");

        if (s.isBusy())
            return ActionResult.fail(s.name() + " est deja occupe.");

        NpcTaskDef def = NpcTaskCatalog.get(taskId);
        if (def == null)
            return ActionResult.fail("Tache inconnue.");

        if (s.hp() <= 0)
            return ActionResult.fail(s.name() + " est hors service.");

        if (!s.spendEnergy(def.energyCost))
            return ActionResult.fail(s.name() + " est trop fatigue.");

        s.startTask(taskId, def.minutesCost);
        ActionResult r = new ActionResult();
        r.title = "Tache assignee";
        r.logLines().add("PNJ " + s.name() + " commence: " + def.label + " (" + def.minutesCost + " min).");
        return r;
    }

    private NpcTaskOutcome resolveNpcTask(Survivor s, NpcTaskId taskId) {
        if (s == null || taskId == null)
            return new NpcTaskOutcome(ActionResult.fail("Tache invalide."), game.events.RiskLevel.R0);

        NpcTaskDef def = NpcTaskCatalog.get(taskId);
        if (def == null)
            return new NpcTaskOutcome(ActionResult.fail("Tache inconnue."), game.events.RiskLevel.R0);

        ActionResult r = new ActionResult();
        r.title = "PNJ: " + s.name() + " - " + def.label;
        r.minutesCost = 0;
        r.energyDelta = 0;
        r.fatigueDelta = 0;
        r.threatDelta = 0;

        game.events.RiskLevel risk = game.events.RiskLevel.R0;

        switch (taskId) {
            case EXPLORE_HOUSES, EXPLORE_WAREHOUSE, EXPLORE_SHOPS, EXPLORE_WILDS -> {
                var zone = game.world.ZoneId.valueOf(taskId.name().replace("EXPLORE_", ""));
                risk = rules.riskForZone(this, zone);
                var loot = rules.rollLoot(this, zone);

                double scale = 0.7;
                int w = (int) Math.round(loot.water * scale);
                int f = (int) Math.round(loot.food * scale);
                int m = (int) Math.round(loot.materials * scale);
                int med = (int) Math.round(loot.medicine * scale);

                if (w + f + m + med == 0 && loot.total() > 0) {
                    if (loot.materials > 0)
                        m = 1;
                    else if (loot.water > 0)
                        w = 1;
                    else if (loot.food > 0)
                        f = 1;
                    else if (loot.medicine > 0)
                        med = 1;
                }

                r.logLines().add("PNJ " + s.name() + " explore: " + zone.name());
                addLoot(Resource.WATER, w, r);
                addLoot(Resource.FOOD, f, r);
                addLoot(Resource.MATERIALS, m, r);
                addLoot(Resource.MEDICINE, med, r);

                long visitAt = clock.minutes();
                world.get(zone).onVisit(w + f + m + med, visitAt);
                int nightThreat = rules.explorationThreatBonus(this);
                r.threatDelta += 5 + nightThreat;
                if (nightThreat > 0) {
                    r.logLines().add("Nuit: menace supplementaire +" + nightThreat + ".");
                }
            }
            case HEAL_SELF -> {
                if (!inventory.tryConsume(Resource.MEDICINE, 1)) {
                    return new NpcTaskOutcome(ActionResult.fail("Pas de medicaments pour " + s.name() + "."),
                            game.events.RiskLevel.R0);
                }
                int heal = rules.medicineHealAmount(this);
                s.heal(heal);
                r.logLines().add("PNJ " + s.name() + " se soigne (+" + heal + " PV).");
            }
            case BUILD_DEFENSE -> {
                if (!inventory.tryConsume(Resource.MATERIALS, 4)) {
                    return new NpcTaskOutcome(ActionResult.fail("Pas assez de materiaux pour construire."),
                            game.events.RiskLevel.R0);
                }
                base.upgradeDefense();
                r.logLines().add("PNJ " + s.name() + " renforce la base (+5 defense).");
                r.threatDelta += 3;
            }
        }

        return new NpcTaskOutcome(r, risk);
    }

    public void applyActionResult(ActionResult r) {
        // temps
        clock.advanceMinutes(r.minutesCost);
        progressNpcTasks(r.minutesCost);
        if (r.minutesCost > 0) {
            applyPlayerMetabolicDrain(r.minutesCost);
        }

        // fatigue/energie (energyDelta gere ici pour repos/manger)
        if (r.energyDelta > 0)
            player.gainEnergy(r.energyDelta);
        if (r.energyDelta < 0)
            player.spendEnergy(-r.energyDelta);

        if (r.fatigueDelta > 0)
            player.addFatigue(r.fatigueDelta);
        if (r.fatigueDelta < 0)
            player.reduceFatigue(-r.fatigueDelta);

        // menace passive avec le temps (simple)
        long hoursPassed = r.minutesCost / 60;
        int passive = (int) (hoursPassed * config.threatPerHour);

        boolean horde = threat.add(r.threatDelta + passive);

        // logs
        for (String line : r.logLines())
            log.add(line);
        log.add("Temps: J+" + clock.days() + " (" + clock.hours() + "h) | Menace: " + threat.value() + "/100");

        if (horde)
            resolveHorde();
    }

    private void progressNpcTasks(long minutes) {
        if (minutes <= 0)
            return;

        java.util.ArrayList<NpcTaskOutcome> completed = new java.util.ArrayList<>();
        java.util.ArrayList<Survivor> completedSurvivors = new java.util.ArrayList<>();

        for (Survivor s : survivors.list()) {
            applySurvivorMetabolicDrain(s, minutes);

            if (s.isBusy()) {
                s.reduceTaskMinutes(minutes);

                if (s.taskRemainingMinutes() == 0 && s.currentTask() != null) {
                    NpcTaskOutcome out = resolveNpcTask(s, s.currentTask());
                    completed.add(out);
                    completedSurvivors.add(s);
                }
                continue;
            }

            // Idle survivors recover energy while resting at base.
            int energyGain = (int) (minutes / 5); // 12 energy per hour
            if (energyGain > 0) {
                s.gainEnergy(energyGain);
            }

            autoCareSurvivor(s);
        }

        for (int i = 0; i < completed.size(); i++) {
            NpcTaskOutcome out = completed.get(i);
            Survivor s = completedSurvivors.get(i);
            s.clearTask();

            LogBook.Channel prev = log.channel();
            LogBook.Channel ch = out.risk != null && out.risk != game.events.RiskLevel.R0
                    ? LogBook.Channel.EXPLORE
                    : LogBook.Channel.BASE;
            log.setChannel(ch);
            if (out.result.ok) {
                if (out.risk != null && out.risk != game.events.RiskLevel.R0) {
                    events.maybeTrigger(out.risk, this, out.result);
                }
                log.add("PNJ " + s.name() + " termine sa tache.");
                applyInstantResult(out.result);
            } else {
                log.add("X " + out.result.title);
            }
            log.setChannel(prev);
        }
    }

    /**
     * Applies an action payload without advancing time.
     * Used for NPC task completion that already progressed in parallel.
     */
    private void applyInstantResult(ActionResult r) {
        if (r.energyDelta > 0)
            player.gainEnergy(r.energyDelta);
        if (r.energyDelta < 0)
            player.spendEnergy(-r.energyDelta);

        if (r.fatigueDelta > 0)
            player.addFatigue(r.fatigueDelta);
        if (r.fatigueDelta < 0)
            player.reduceFatigue(-r.fatigueDelta);

        boolean horde = threat.add(r.threatDelta);

        for (String line : r.logLines())
            log.add(line);
        log.add("Temps: J+" + clock.days() + " (" + clock.hours() + "h) | Menace: " + threat.value() + "/100");

        if (horde)
            resolveHorde();
    }

    private void autoCareSurvivor(Survivor s) {
        if (s.hp() > 0 && s.hp() <= config.npcHpHealThreshold) {
            if (inventory.tryConsume(Resource.MEDICINE, 1)) {
                int heal = rules.medicineHealAmount(this);
                s.heal(heal);
                log.add("PNJ " + s.name() + " se soigne (+" + heal + " PV).");
            }
        }

        if (s.hydration() <= config.npcHydrationThreshold) {
            int target = randomRefillTarget();
            int consumed = 0;
            while (s.hydration() < target && inventory.tryConsume(Resource.WATER, 1)) {
                s.addHydration(25);
                consumed++;
            }
            if (consumed > 0) {
                log.add("PNJ " + s.name() + " boit " + consumed + " ration(s) d'eau (cible " + target + ").");
            }
        }

        if (s.hunger() <= config.npcHungerThreshold) {
            int target = randomRefillTarget();
            int consumed = 0;
            while (s.hunger() < target && inventory.tryConsume(Resource.FOOD, 1)) {
                s.addHunger(20);
                consumed++;
            }
            if (consumed > 0) {
                log.add("PNJ " + s.name() + " mange " + consumed + " ration(s) (cible " + target + ").");
            }
        }
    }

    private int randomRefillTarget() {
        int min = Math.max(0, Math.min(100, config.npcRefillTargetMin));
        int max = Math.max(0, Math.min(100, config.npcRefillTargetMax));
        if (max < min) {
            int t = min;
            min = max;
            max = t;
        }
        return min + rng.nextInt(max - min + 1);
    }

    private void applyPlayerMetabolicDrain(long minutes) {
        int hDrain = drainByMinutes(minutes, 4, true);
        int fDrain = drainByMinutes(minutes, 2, false);

        if (hDrain > 0)
            player.drainHydration(hDrain);
        if (fDrain > 0)
            player.drainHunger(fDrain);

        long hours = minutes / 60;
        if (hours > 0 && player.hydration() == 0) {
            int dmg = 5 * (int) hours;
            player.damage(dmg);
            player.addMental(-5 * (int) hours);
            log.add("Deshydratation ! -" + dmg + " PV, mental en baisse.");
        }

        if (hours > 0 && player.hunger() == 0) {
            player.addFatigue(5 * (int) hours);
            log.add("Faim extreme ! Fatigue en hausse.");
        }
    }

    private void applySurvivorMetabolicDrain(Survivor s, long minutes) {
        int hDrain = s.applyHydrationDrainByMinutes(minutes, 4);
        int fDrain = s.applyHungerDrainByMinutes(minutes, 2);
        long hours = minutes / 60;

        if (hours > 0 && s.hydration() == 0) {
            s.damage(3 * (int) hours);
        }
        if (hours > 0 && s.hunger() == 0) {
            s.damage(2 * (int) hours);
        }
    }

    private int drainByMinutes(long minutes, int pointsPerHour, boolean hydration) {
        long minutesPerPoint = Math.max(1, 60L / pointsPerHour);
        if (minutes <= 0)
            return 0;

        if (hydration) {
            long total = playerHydrationDrainCarryMinutes + minutes;
            int points = (int) (total / minutesPerPoint);
            playerHydrationDrainCarryMinutes = total % minutesPerPoint;
            return points;
        }

        long total = playerHungerDrainCarryMinutes + minutes;
        int points = (int) (total / minutesPerPoint);
        playerHungerDrainCarryMinutes = total % minutesPerPoint;
        return points;
    }

    private void resolveHorde() {
        int dmg = rules.hordeDamage(this);
        player.damage(dmg);
        log.add("Horde ! La base encaisse. Tu prends -" + dmg + " PV (defense=" + base.defense() + ").");
        if (player.hp() <= 0) {
            log.add("Tu t'effondres... fin de partie (V0).");
        }
    }

    public void setMinutes(long minutes) {
        // reset simple : on remplace le clock par avance
        // (on va plutot ajouter un setter dans TimeClock)
        throw new UnsupportedOperationException("Use TimeClock.setMinutes");
    }

    public void addLoot(Resource r, int amount, ActionResult res) {
        if (amount <= 0)
            return;

        int lost = inventory.addCapped(r, amount, rules.storageCap(this));
        int kept = amount - lost;

        if (kept > 0)
            res.logLines().add("+" + kept + " " + r.name().toLowerCase());
        if (lost > 0)
            res.logLines().add("Stock plein : tu abandonnes " + lost + " " + r.name().toLowerCase());
    }

    private long rngSeed = System.nanoTime();
    private transient Random rng = new Random(rngSeed);

    public Random rng() {
        return rng;
    }

    public long rngSeed() {
        return rngSeed;
    }

    public void setRngSeed(long seed) {
        this.rngSeed = seed;
        this.rng = new Random(seed);
    }

}
