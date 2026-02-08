package game.core;

public final class TechDef {
    public final TechId id;
    public final String label;
    public final String description;
    public final int materialsCost;
    public final long minutesCost;
    public final int energyCost;

    public TechDef(TechId id, String label, String description, int materialsCost, long minutesCost, int energyCost) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.materialsCost = materialsCost;
        this.minutesCost = minutesCost;
        this.energyCost = energyCost;
    }
}
