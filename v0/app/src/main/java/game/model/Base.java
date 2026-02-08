package game.model;

public final class Base {
    private int defense = 10; // réduit dégâts horde
    private int storageCap = 50;

    public int defense() {
        return defense;
    }

    public int storageCap() {
        return storageCap;
    }

    public void upgradeDefense() {
        defense = Math.min(80, defense + 5);
    }

    public void upgradeStorage() {
        storageCap = Math.min(200, storageCap + 25);
    }

    public void setAll(int defense, int storageCap) {
        this.defense = Math.max(0, Math.min(80, defense));
        this.storageCap = Math.max(0, Math.min(200, storageCap));
    }

}

