package game.core;

public final class ThreatMeter {
    private int threat; // 0..100

    public int value() {
        return threat;
    }

    /** augmente la menace et renvoie true si une horde se déclenche */
    public boolean add(int amount) {
        amount = Math.max(0, amount);
        threat += amount;
        if (threat >= 100) {
            threat = 0;
            return true;
        }
        return false;
    }

    public void setValue(int value) {
        this.threat = Math.max(0, Math.min(100, value));
    }

}
