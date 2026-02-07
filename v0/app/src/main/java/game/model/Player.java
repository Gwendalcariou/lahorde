package game.model;

public final class Player {
    private int hp = 100;
    private int energy = 100; // 0..100
    private int fatigue = 0; // 0..100 (plus haut = pire)
    private int mental = 80; // 0..100 (plus bas = pire)
    private int hydration = 80; // 0..100
    private int hunger = 80; // 0..100

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

    public void setAll(int hp, int energy, int fatigue, int mental, int hydration, int hunger) {
        this.hp = clamp(hp);
        this.energy = clamp(energy);
        this.fatigue = clamp(fatigue);
        this.mental = clamp(mental);
        this.hydration = clamp(hydration);
        this.hunger = clamp(hunger);
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }

    public int hydration() {
        return hydration;
    }

    public int hunger() {
        return hunger;
    }

    public void addHydration(int amount) {
        hydration = clamp(hydration + amount);
    }

    public void addHunger(int amount) {
        hunger = clamp(hunger + amount);
    }

    public void drainHydration(int amount) {
        hydration = clamp(hydration - amount);
    }

    public void drainHunger(int amount) {
        hunger = clamp(hunger - amount);
    }

}
