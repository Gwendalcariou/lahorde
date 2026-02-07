package game.model;

import java.util.EnumMap;
import java.util.Map;

public final class Inventory {
    private final EnumMap<Resource, Integer> items = new EnumMap<>(Resource.class);

    public Inventory() {
        for (Resource r : Resource.values())
            items.put(r, 0);
    }

    public int get(Resource r) {
        return items.getOrDefault(r, 0);
    }

    public void add(Resource r, int amount) {
        if (amount < 0)
            throw new IllegalArgumentException("amount < 0");
        items.put(r, get(r) + amount);
    }

    public boolean tryConsume(Resource r, int amount) {
        if (amount < 0)
            throw new IllegalArgumentException("amount < 0");
        int cur = get(r);
        if (cur < amount)
            return false;
        items.put(r, cur - amount);
        return true;
    }

    public Map<Resource, Integer> snapshot() {
        return new EnumMap<>(items);
    }

    public void set(Resource r, int value) {
        items.put(r, Math.max(0, value));
    }

    public int totalItems() {
        int sum = 0;
        for (Resource r : Resource.values())
            sum += get(r);
        return sum;
    }

    /**
     * Ajoute jusqu’à remplir la capacité. Retourne l’excédent perdu.
     */
    public int addCapped(Resource r, int amount, int capTotal) {
        if (amount <= 0)
            return 0;

        int currentTotal = totalItems();
        int free = Math.max(0, capTotal - currentTotal);

        int kept = Math.min(amount, free);
        int lost = amount - kept;

        if (kept > 0)
            add(r, kept);
        return lost;
    }

}
