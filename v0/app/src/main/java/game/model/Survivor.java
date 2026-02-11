package game.model;

public final class Survivor {
    private final String name;
    private final JobId job;
    private int relation; // 0..100

    private int hp = 100;
    private int energy = 100;
    private int hydration = 80;
    private int hunger = 80;
    private long hydrationDrainCarryMinutes;
    private long hungerDrainCarryMinutes;
    private game.core.NpcTaskId currentTask;
    private long taskRemainingMinutes;

    public Survivor(String name, JobId job, int relation) {
        this.name = name;
        this.job = job;
        this.relation = clamp(relation);
    }

    public String name() {
        return name;
    }

    public JobId job() {
        return job;
    }

    public int relation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = clamp(relation);
    }

    public int hp() {
        return hp;
    }

    public int energy() {
        return energy;
    }

    public int hydration() {
        return hydration;
    }

    public int hunger() {
        return hunger;
    }

    public game.core.NpcTaskId currentTask() {
        return currentTask;
    }

    public long taskRemainingMinutes() {
        return taskRemainingMinutes;
    }

    public boolean isBusy() {
        return currentTask != null && taskRemainingMinutes > 0;
    }

    public void startTask(game.core.NpcTaskId taskId, long minutes) {
        this.currentTask = taskId;
        this.taskRemainingMinutes = Math.max(0, minutes);
    }

    public void clearTask() {
        this.currentTask = null;
        this.taskRemainingMinutes = 0;
    }

    public void reduceTaskMinutes(long minutes) {
        if (minutes <= 0)
            return;
        taskRemainingMinutes = Math.max(0, taskRemainingMinutes - minutes);
        if (taskRemainingMinutes == 0) {
            // keep currentTask for completion handling
        }
    }

    public void damage(int amount) {
        hp = clamp(hp - Math.max(0, amount));
    }

    public void heal(int amount) {
        hp = clamp(hp + Math.max(0, amount));
    }

    public boolean spendEnergy(int amount) {
        amount = Math.max(0, amount);
        if (energy < amount)
            return false;
        energy -= amount;
        return true;
    }

    public void gainEnergy(int amount) {
        energy = clamp(energy + Math.max(0, amount));
    }

    public void addHydration(int amount) {
        hydration = clamp(hydration + amount);
    }

    public void addHunger(int amount) {
        hunger = clamp(hunger + amount);
    }

    public void drainHydration(int amount) {
        hydration = clamp(hydration - Math.max(0, amount));
    }

    public void drainHunger(int amount) {
        hunger = clamp(hunger - Math.max(0, amount));
    }

    public void setAll(int hp, int energy, int hydration, int hunger, int relation) {
        this.hp = clamp(hp);
        this.energy = clamp(energy);
        this.hydration = clamp(hydration);
        this.hunger = clamp(hunger);
        this.relation = clamp(relation);
        this.hydrationDrainCarryMinutes = 0;
        this.hungerDrainCarryMinutes = 0;
    }

    public int applyHydrationDrainByMinutes(long minutes, int pointsPerHour) {
        if (minutes <= 0 || pointsPerHour <= 0)
            return 0;
        long minutesPerPoint = Math.max(1, 60L / pointsPerHour);
        long total = hydrationDrainCarryMinutes + minutes;
        int points = (int) (total / minutesPerPoint);
        hydrationDrainCarryMinutes = total % minutesPerPoint;
        if (points > 0)
            drainHydration(points);
        return points;
    }

    public int applyHungerDrainByMinutes(long minutes, int pointsPerHour) {
        if (minutes <= 0 || pointsPerHour <= 0)
            return 0;
        long minutesPerPoint = Math.max(1, 60L / pointsPerHour);
        long total = hungerDrainCarryMinutes + minutes;
        int points = (int) (total / minutesPerPoint);
        hungerDrainCarryMinutes = total % minutesPerPoint;
        if (points > 0)
            drainHunger(points);
        return points;
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }
}
