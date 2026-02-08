package game.core;

public final class NpcTaskDef {
    public final NpcTaskId id;
    public final String label;
    public final long minutesCost;
    public final int energyCost;

    public NpcTaskDef(NpcTaskId id, String label, long minutesCost, int energyCost) {
        this.id = id;
        this.label = label;
        this.minutesCost = minutesCost;
        this.energyCost = energyCost;
    }
}
