package game.core;

import game.actions.ActionResult;
import game.events.EventPool;
import game.model.Base;
import game.model.Inventory;
import game.model.Player;

public final class GameState {
    private final GameConfig config = new GameConfig();
    private final Player player = new Player();
    private final Base base = new Base();
    private final Inventory inventory = new Inventory();
    private final TimeClock clock = new TimeClock();
    private final ThreatMeter threat = new ThreatMeter();
    private final LogBook log = new LogBook();
    private final EventPool events = new EventPool();

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

    public void bootstrapV0() {
        inventory.add(game.model.Resource.WATER, 8);
        inventory.add(game.model.Resource.FOOD, 6);
        inventory.add(game.model.Resource.MATERIALS, 10);
        inventory.add(game.model.Resource.MEDICINE, 2);
        log.add("Début de partie. La base tient encore… pour l'instant.");
    }

    public void applyActionResult(ActionResult r) {
        // temps
        clock.advanceMinutes(r.minutesCost);

        // fatigue/énergie (energyDelta géré ici pour repos/manger)
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

    private void resolveHorde() {
        int raw = config.hordeBaseDamage;
        int mitigated = Math.max(5, raw - (base.defense() / 2));
        player.damage(mitigated);
        log.add("⚠ Horde ! La base encaisse. Tu prends -" + mitigated + " PV (défense=" + base.defense() + ").");
        if (player.hp() <= 0) {
            log.add("☠ Tu t'effondres… fin de partie (V0).");
        }
    }

    public void setMinutes(long minutes) {
        // reset simple : on remplace le clock par avance
        // (on va plutôt ajouter un setter dans TimeClock)
        throw new UnsupportedOperationException("Use TimeClock.setMinutes");
    }

}
