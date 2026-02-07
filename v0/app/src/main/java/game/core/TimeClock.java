package game.core;

public final class TimeClock {
    private long minutes; // temps total écoulé

    public long minutes() {
        return minutes;
    }

    public long hours() {
        return minutes / 60;
    }

    public long days() {
        return hours() / 24;
    }

    public void advanceMinutes(long delta) {
        if (delta < 0)
            throw new IllegalArgumentException("delta < 0");
        minutes += delta;
    }

    public void setMinutes(long minutes) {
        if (minutes < 0)
            minutes = 0;
        this.minutes = minutes;
    }

}
