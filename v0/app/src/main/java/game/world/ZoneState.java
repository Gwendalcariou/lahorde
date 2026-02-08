package game.world;

public final class ZoneState {
    private int remainingLoot = 100; // 0..100
    private int danger = 10; // 0..100
    private long lastVisitedMinutes = -1;

    public int remainingLoot() {
        return remainingLoot;
    }

    public int danger() {
        return danger;
    }

    public long lastVisitedMinutes() {
        return lastVisitedMinutes;
    }

    public void set(int remainingLoot, int danger, long lastVisitedMinutes) {
        this.remainingLoot = clamp(remainingLoot);
        this.danger = clamp(danger);
        this.lastVisitedMinutes = lastVisitedMinutes;
    }

    public void onVisit(int lootTaken, long visitMinutes) {
        int drain = 5 + Math.max(0, lootTaken);
        remainingLoot = clamp(remainingLoot - drain);

        int bump = 5;
        if (lastVisitedMinutes >= 0) {
            long delta = Math.max(0, visitMinutes - lastVisitedMinutes);
            if (delta < 360) {
                bump += 5; // visites rapprochées = danger monte plus vite
            }
        }
        danger = clamp(danger + bump);

        lastVisitedMinutes = visitMinutes;
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }
}

