package game.model;

public final class Player {
    private int hp = 100;
    private int energy = 100; // 0..100
    private int fatigue = 0; // 0..100 (plus haut = pire)
    private int mental = 80; // 0..100 (plus bas = pire)

    public int hp() {
        return hp;
    }

    public int energy() {
        return energy;
    }

    public int fatigue() {
        return fatigue;
    }

    public int mental() {
        return mental;
    }

    public void damage(int amount) {
        hp = Math.max(0, hp - Math.max(0, amount));
    }

    public void heal(int amount) {
        hp = Math.min(100, hp + Math.max(0, amount));
    }

    public boolean spendEnergy(int amount) {
        amount = Math.max(0, amount);
        if (energy < amount)
            return false;
        energy -= amount;
        return true;
    }

    public void gainEnergy(int amount) {
        energy = Math.min(100, energy + Math.max(0, amount));
    }

    public void addFatigue(int amount) {
        fatigue = Math.min(100, fatigue + Math.max(0, amount));
    }

    public void reduceFatigue(int amount) {
        fatigue = Math.max(0, fatigue - Math.max(0, amount));
    }

    public void addMental(int amount) {
        mental = Math.min(100, mental + amount);
    }

    public void setAll(int hp, int energy, int fatigue, int mental) {
        this.hp = clamp(hp);
        this.energy = clamp(energy);
        this.fatigue = clamp(fatigue);
        this.mental = clamp(mental);
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }

}
