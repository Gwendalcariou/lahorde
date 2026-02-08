package game;

import game.actions.ActionFactory;
import game.actions.ActionId;
import game.actions.ActionResult;
import game.actions.GameAction;
import game.core.GameState;
import game.core.LogBook;
import game.core.NpcTaskCatalog;
import game.core.NpcTaskId;
import game.core.TechCatalog;
import game.core.TechDef;
import game.core.TechId;
import game.model.Survivor;
import game.model.Resource;
import game.persistence.SaveManager;
import game.world.ZoneId;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.EnumMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainApp extends Application {
    private GameState state;
    private final SaveManager saves = new SaveManager();

    private TextArea baseLogArea;
    private TextArea exploreLogArea;
    private Label statsLine1;
    private Label statsLine2;
    private Label exploreStatsLine1;
    private Label exploreStatsLine2;
    private Label invLabel;

    private final EnumMap<ZoneId, Label> zoneLabels = new EnumMap<>(ZoneId.class);
    private final EnumMap<TechId, Button> techButtons = new EnumMap<>(TechId.class);
    private final EnumMap<TechId, Label> techStatus = new EnumMap<>(TechId.class);
    private final List<SurvivorRow> survivorRows = new ArrayList<>();

    private Map<ActionId, GameAction> actions;

    @Override
    public void start(Stage stage) {
        state = new GameState();
        state.bootstrapV0();

        actions = new ActionFactory().createAll();

        var root = new BorderPane();
        root.setPadding(new Insets(10));

        root.setTop(buildSaveLoadBar());
        root.setCenter(buildTabs());

        refreshUI();

        stage.setTitle("Survie Solo - V1");
        stage.setScene(new Scene(root, 1000, 640));
        stage.show();
    }

    private TabPane buildTabs() {
        var tabs = new TabPane();
        tabs.getTabs().add(buildBaseTab());
        tabs.getTabs().add(buildExploreTab());
        // Logs are split into Base and Exploration; no separate Journal tab.
        return tabs;
    }

    private Tab buildBaseTab() {
        statsLine1 = new Label();
        statsLine2 = new Label();
        invLabel = new Label();

        var baseActions = buildActionsPane("Actions Base",
                ActionId.REST,
                ActionId.DRINK,
                ActionId.EAT,
                ActionId.USE_MEDICINE,
                ActionId.CRAFT_SIMPLE,
                ActionId.UPGRADE_DEFENSE);

        var techs = buildTechPane();

        var survivors = buildSurvivorsPane();

        baseLogArea = buildLogArea();
        var box = new VBox(6, statsLine1, statsLine2, invLabel, baseActions, techs, survivors,
                new Label("Journal Base"), baseLogArea);
        box.setPadding(new Insets(8));

        var tab = new Tab("Base", box);
        tab.setClosable(false);
        return tab;
    }

    private Tab buildExploreTab() {
        var grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);
        grid.setPadding(new Insets(8));

        int row = 0;
        for (ZoneId id : ZoneId.values()) {
            var info = new Label();
            zoneLabels.put(id, info);

            var action = actions.get(ActionId.valueOf("EXPLORE_" + id.name()));
            var btn = new Button(action.label());
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> runAction(action));

            grid.add(info, 0, row);
            grid.add(btn, 1, row);
            row++;
        }

        exploreStatsLine1 = new Label();
        exploreStatsLine2 = new Label();
        exploreLogArea = buildLogArea();
        var wrap = new VBox(6, exploreStatsLine1, exploreStatsLine2, grid, new Label("Journal Exploration"),
                exploreLogArea);
        wrap.setPadding(new Insets(8));

        var tab = new Tab("Exploration", wrap);
        tab.setClosable(false);
        return tab;
    }

    private HBox buildSaveLoadBar() {
        var saveBtn = new Button("Sauver");
        saveBtn.setOnAction(e -> {
            withLogChannel(LogBook.Channel.BASE, () -> state.log().add("Sauvegarde effectuee."));
            refreshUI();
        });

        var loadBtn = new Button("Charger");
        loadBtn.setOnAction(e -> {
            var loaded = saves.loadOrNull();
            if (loaded == null) {
                withLogChannel(LogBook.Channel.BASE, () -> state.log().add("Aucune sauvegarde trouvee."));
            } else {
                state = loaded;
                actions = new ActionFactory().createAll();
                withLogChannel(LogBook.Channel.BASE, () -> state.log().add("Sauvegarde chargee."));
            }
            refreshUI();
        });

        return new HBox(8, saveBtn, loadBtn);
    }

    private VBox buildActionsPane(String title, ActionId... ids) {
        var box = new VBox(8);
        box.getChildren().add(new Label(title));
        for (ActionId id : ids) {
            GameAction a = actions.get(id);
            var btn = new Button(a.label());
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> runAction(a));
            box.getChildren().add(btn);
        }
        return box;
    }

    private VBox buildTechPane() {
        var box = new VBox(6);
        box.getChildren().add(new Label("Technologies"));

        for (TechDef def : TechCatalog.all()) {
            var title = new Label(def.label + " - " + def.description);
            var status = new Label();
            var btn = new Button("Deverrouiller");
            btn.setOnAction(e -> unlockTech(def));

            techButtons.put(def.id, btn);
            techStatus.put(def.id, status);

            var row = new HBox(8, title, status, btn);
            box.getChildren().add(row);
        }

        return box;
    }

    private VBox buildSurvivorsPane() {
        var box = new VBox(6);
        box.getChildren().add(new Label("Survivants"));

        survivorRows.clear();
        for (Survivor s : state.survivors().list()) {
            var info = new Label();
            var combo = new ComboBox<NpcTaskId>();
            combo.getItems().addAll(NpcTaskCatalog.all().stream().map(d -> d.id).toList());
            combo.getSelectionModel().selectFirst();
            combo.setConverter(new StringConverter<>() {
                @Override
                public String toString(NpcTaskId id) {
                    return id == null ? "" : npcTaskLabel(id);
                }

                @Override
                public NpcTaskId fromString(String string) {
                    return null;
                }
            });
            combo.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(NpcTaskId item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : npcTaskLabel(item));
                }
            });

            var btn = new Button("Assigner");
            btn.setOnAction(e -> runNpcTask(s, combo.getValue()));

            var row = new HBox(8, info, combo, btn);
            box.getChildren().add(row);
            survivorRows.add(new SurvivorRow(s, info, combo));
        }
        return box;
    }

    private void unlockTech(TechDef def) {
        LogBook.Channel prev = state.log().channel();
        state.log().setChannel(LogBook.Channel.BASE);
        if (state.techs().isUnlocked(def.id)) {
            state.log().add("Tech deja deverrouillee.");
            refreshUI();
            state.log().setChannel(prev);
            return;
        }

        if (state.player().energy() < def.energyCost) {
            state.log().add("Pas assez d'energie pour cette tech.");
            refreshUI();
            state.log().setChannel(prev);
            return;
        }

        if (!state.inventory().tryConsume(Resource.MATERIALS, def.materialsCost)) {
            state.log().add("Pas assez de materiaux pour cette tech.");
            refreshUI();
            state.log().setChannel(prev);
            return;
        }

        state.player().spendEnergy(def.energyCost);
        state.techs().unlock(def.id);

        ActionResult r = new ActionResult();
        r.title = "Tech: " + def.label;
        r.minutesCost = def.minutesCost;
        r.energyDelta = 0;
        r.fatigueDelta = 0;
        r.threatDelta = 0;
        r.logLines().add("Tech deverrouillee: " + def.label + ".");

        state.applyActionResult(r);
        refreshUI();
        state.log().setChannel(prev);
    }

    private void runAction(GameAction action) {
        if (state.player().hp() <= 0) {
            withLogChannel(LogBook.Channel.BASE, () -> state.log().add("La partie est terminee (V1)."));
            refreshUI();
            return;
        }

        LogBook.Channel channel = channelForAction(action.id());
        LogBook.Channel prev = state.log().channel();
        state.log().setChannel(channel);

        ActionResult r = action.apply(state);
        if (!r.ok) {
            state.log().add("X " + r.title);
            refreshUI();
            state.log().setChannel(prev);
            return;
        }

        // Events selon le risque
        state.events().maybeTrigger(action.risk(state), state, r);

        // Appliquer le resultat
        state.applyActionResult(r);

        refreshUI();
        state.log().setChannel(prev);
    }

    private void runNpcTask(Survivor s, NpcTaskId taskId) {
        if (s == null || taskId == null) {
            withLogChannel(LogBook.Channel.BASE, () -> state.log().add("Tache PNJ invalide."));
            refreshUI();
            return;
        }

        LogBook.Channel prev = state.log().channel();
        state.log().setChannel(LogBook.Channel.BASE);

        ActionResult r = state.assignNpcTask(s, taskId);
        if (!r.ok) {
            state.log().add("X " + r.title);
            refreshUI();
            state.log().setChannel(prev);
            return;
        }

        for (String line : r.logLines()) {
            state.log().add(line);
        }
        refreshUI();
        state.log().setChannel(prev);
    }

    private void refreshUI() {
        String line1 = "PV: " + state.player().hp() +
                " | Energie: " + state.player().energy() +
                " | Fatigue: " + state.player().fatigue() +
                " | Mental: " + state.player().mental();
        String line2 = "Hydratation: " + state.player().hydration() +
                " | Faim: " + state.player().hunger() +
                " | Defense: " + state.base().defense() +
                " | Stockage: " + state.rules().storageCap(state) +
                " | Temps: " + state.clock().days() + "j " +
                (state.clock().hours() % 24) + "h" +
                " | Menace: " + state.threat().value() + "/100";

        statsLine1.setText(line1);
        statsLine2.setText(line2);
        if (exploreStatsLine1 != null) {
            exploreStatsLine1.setText(line1);
        }
        if (exploreStatsLine2 != null) {
            exploreStatsLine2.setText(line2);
        }

        invLabel.setText(
                "Inventaire - Eau: " + state.inventory().get(Resource.WATER) +
                        " | Nourriture: " + state.inventory().get(Resource.FOOD) +
                        " | Materiaux: " + state.inventory().get(Resource.MATERIALS) +
                        " | Medicaments: " + state.inventory().get(Resource.MEDICINE));

        for (ZoneId id : ZoneId.values()) {
            Label lbl = zoneLabels.get(id);
            if (lbl != null) {
                lbl.setText(zoneLabelText(id));
            }
        }

        for (TechDef def : TechCatalog.all()) {
            Label status = techStatus.get(def.id);
            Button btn = techButtons.get(def.id);
            boolean unlocked = state.techs().isUnlocked(def.id);
            if (status != null) {
                if (unlocked) {
                    status.setText("[OK]");
                } else {
                    status.setText("Cout: " + def.materialsCost + " mat | " + def.minutesCost + " min | "
                            + def.energyCost + " energie");
                }
            }
            if (btn != null) {
                btn.setDisable(unlocked);
            }
        }

        for (SurvivorRow row : survivorRows) {
            row.info.setText(survivorLabel(row.survivor));
        }

        baseLogArea.setText(joinLines(state.log().lines(LogBook.Channel.BASE)));
        baseLogArea.setScrollTop(Double.MAX_VALUE);

        exploreLogArea.setText(joinLines(state.log().lines(LogBook.Channel.EXPLORE)));
        exploreLogArea.setScrollTop(Double.MAX_VALUE);
    }

    private String zoneLabelText(ZoneId id) {
        var z = state.world().get(id);
        return switch (id) {
            case HOUSES -> "Habitations";
            case WAREHOUSE -> "Entrepot";
            case SHOPS -> "Commerces";
            case WILDS -> "Zones sauvages";
        } + " | Loot: " + z.remainingLoot() + "% | Danger: " + z.danger() + "%";
    }

    private String survivorLabel(Survivor s) {
        String task = s.isBusy() && s.currentTask() != null
                ? npcTaskLabel(s.currentTask()) + " (" + s.taskRemainingMinutes() + " min)"
                : "Libre";
        return s.name() + " (" + s.job().name() + ") | HP: " + s.hp() + " | EN: " + s.energy() +
                " | HYD: " + s.hydration() + " | FAIM: " + s.hunger() + " | REL: " + s.relation() +
                " | Tache: " + task;
    }

    private String npcTaskLabel(NpcTaskId id) {
        var def = NpcTaskCatalog.get(id);
        return def == null ? id.name() : def.label;
    }

    private TextArea buildLogArea() {
        var area = new TextArea();
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefRowCount(8);
        return area;
    }

    private LogBook.Channel channelForAction(ActionId id) {
        if (id.name().startsWith("EXPLORE_")) {
            return LogBook.Channel.EXPLORE;
        }
        return LogBook.Channel.BASE;
    }


    private String joinLines(java.util.List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private void withLogChannel(LogBook.Channel channel, Runnable action) {
        LogBook.Channel prev = state.log().channel();
        state.log().setChannel(channel);
        try {
            action.run();
        } finally {
            state.log().setChannel(prev);
        }
    }

    private static final class SurvivorRow {
        final Survivor survivor;
        final Label info;
        final ComboBox<NpcTaskId> task;

        SurvivorRow(Survivor survivor, Label info, ComboBox<NpcTaskId> task) {
            this.survivor = survivor;
            this.info = info;
            this.task = task;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


