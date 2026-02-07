package game.events;

public enum RiskLevel {
    R0(0), R1(1), R2(2), R3(3), R4(4), R5(5);

    public final int level;

    RiskLevel(int level) {
        this.level = level;
    }
}
