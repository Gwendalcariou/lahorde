package game.core;

import game.events.RiskLevel;
import game.world.ZoneId;
import game.world.ZoneState;

public final class GameRules {
    private static final int NIGHT_START_HOUR = 22;
    private static final int NIGHT_END_HOUR = 6;
    private static final double NIGHT_ENERGY_MULTIPLIER = 1.3;
    private static final int NIGHT_EXPLORATION_THREAT_BONUS = 4;

    public static final class LootResult {
        public final int water;
        public final int food;
        public final int materials;
        public final int medicine;

        public LootResult(int water, int food, int materials, int medicine) {
            this.water = water;
            this.food = food;
            this.materials = materials;
            this.medicine = medicine;
        }

        public int total() {
            return water + food + materials + medicine;
        }
    }

    public int storageCap(GameState s) {
        int cap = s.base().storageCap();
        if (s.techs().isUnlocked(TechId.STORAGE_I))
            cap += 25;
        return cap;
    }

    public int craftSimpleMaterialsCost(GameState s) {
        return s.techs().isUnlocked(TechId.WORKSHOP) ? 2 : 3;
    }

    public boolean isNight(GameState s) {
        int h = s.clock().hourOfDay();
        return h >= NIGHT_START_HOUR || h < NIGHT_END_HOUR;
    }

    public String dayPeriodLabel(GameState s) {
        return isNight(s) ? "Nuit" : "Jour";
    }

    public int adjustedEnergyCost(GameState s, int baseCost) {
        if (baseCost <= 0)
            return 0;
        if (!isNight(s))
            return baseCost;
        return (int) Math.ceil(baseCost * NIGHT_ENERGY_MULTIPLIER);
    }

    public int explorationThreatBonus(GameState s) {
        return isNight(s) ? NIGHT_EXPLORATION_THREAT_BONUS : 0;
    }

    public int medicineHealAmount(GameState s) {
        int base = 15;
        if (s.techs().isUnlocked(TechId.INFIRMARY))
            base += 10;
        return base;
    }

    public int hordeDamage(GameState s) {
        int raw = s.config().hordeBaseDamage;
        int mitigated = raw - (s.base().defense() / 2);
        if (s.techs().isUnlocked(TechId.TRAPS))
            mitigated -= 10;
        return Math.max(5, mitigated);
    }

    public RiskLevel riskForZone(GameState s, ZoneId zone) {
        int d = s.world().get(zone).danger();
        if (d < 20)
            return RiskLevel.R1;
        if (d < 40)
            return RiskLevel.R2;
        if (d < 60)
            return RiskLevel.R3;
        if (d < 80)
            return RiskLevel.R4;
        return RiskLevel.R5;
    }

    public LootResult rollLoot(GameState s, ZoneId zone) {
        ZoneState z = s.world().get(zone);
        double lootFactor = Math.max(0.0, z.remainingLoot() / 100.0);

        int[] wRange;
        int[] fRange;
        int[] mRange;
        int[] medRange;

        switch (zone) {
            case HOUSES -> {
                wRange = new int[] { 0, 2 };
                fRange = new int[] { 0, 2 };
                mRange = new int[] { 1, 4 };
                medRange = new int[] { 0, 1 };
            }
            case WAREHOUSE -> {
                wRange = new int[] { 0, 1 };
                fRange = new int[] { 0, 1 };
                mRange = new int[] { 3, 7 };
                medRange = new int[] { 0, 2 };
            }
            case SHOPS -> {
                wRange = new int[] { 1, 4 };
                fRange = new int[] { 0, 2 };
                mRange = new int[] { 0, 3 };
                medRange = new int[] { 0, 1 };
            }
            case WILDS -> {
                wRange = new int[] { 0, 2 };
                fRange = new int[] { 2, 5 };
                mRange = new int[] { 0, 2 };
                medRange = new int[] { 0, 1 };
            }
            default -> throw new IllegalStateException("Unknown zone: " + zone);
        }

        int w = rollRange(s, wRange);
        int f = rollRange(s, fRange);
        int m = rollRange(s, mRange);
        int med = rollRange(s, medRange);

        w = (int) Math.floor(w * lootFactor);
        f = (int) Math.floor(f * lootFactor);
        m = (int) Math.floor(m * lootFactor);
        med = (int) Math.floor(med * lootFactor);

        if (s.techs().isUnlocked(TechId.WATER_COLLECTOR)) {
            if (zone == ZoneId.HOUSES || zone == ZoneId.WILDS)
                w += 1;
        }

        return new LootResult(w, f, m, med);
    }

    private int rollRange(GameState s, int[] range) {
        int a = range[0], b = range[1];
        if (b < a)
            return 0;
        return a + s.rng().nextInt(b - a + 1);
    }
}
