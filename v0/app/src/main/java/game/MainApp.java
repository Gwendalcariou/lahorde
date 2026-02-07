package game;

import game.actions.*;
import game.core.GameState;
import game.model.Resource;
import game.persistence.SaveManager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Map;

public class MainApp extends Application {
    private GameState state;
    private final SaveManager saves = new SaveManager();

    private TextArea logArea;
    private Label statsLabel;
    private Label invLabel;

    private Map<ActionId, GameAction> actions;

    @Override
    public void start(Stage stage) {
        state = new GameState();
        state.bootstrapV0();

        actions = new ActionFactory().createAll();

        var root = new BorderPane();
        root.setPadding(new Insets(10));

        statsLabel = new Label();
        invLabel = new Label();

        var top = new VBox(6, statsLabel, invLabel, buildSaveLoadBar());
        root.setTop(top);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        root.setCenter(logArea);

        root.setRight(buildActionsPane());

        refreshUI();

        stage.setTitle("Survie Solo — V0");
        stage.setScene(new Scene(root, 1000, 600));
        stage.show();
    }

    private HBox buildSaveLoadBar() {
        var saveBtn = new Button("Sauver");
        saveBtn.setOnAction(e -> {
            saves.save(state);
            state.log().add("💾 Sauvegarde effectuée.");
            refreshUI();
        });

        var loadBtn = new Button("Charger");
        loadBtn.setOnAction(e -> {
            var loaded = saves.loadOrNull();
            if (loaded == null) {
                state.log().add("Aucune sauvegarde trouvée.");
            } else {
                state = loaded;
                actions = new ActionFactory().createAll();
                state.log().add("📂 Sauvegarde chargée.");
            }
            refreshUI();
        });

        return new HBox(8, saveBtn, loadBtn);
    }

    private VBox buildActionsPane() {
        var box = new VBox(8);
        box.setPadding(new Insets(0, 0, 0, 10));
        box.getChildren().add(new Label("Actions"));

        for (ActionId id : ActionId.values()) {
            GameAction a = actions.get(id);
            var btn = new Button(a.label());
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> runAction(a));
            box.getChildren().add(btn);
        }

        return box;
    }

    private void runAction(GameAction action) {
        if (state.player().hp() <= 0) {
            state.log().add("La partie est terminée (V0).");
            refreshUI();
            return;
        }

        ActionResult r = action.apply(state);
        if (!r.ok) {
            state.log().add("❌ " + r.title);
            refreshUI();
            return;
        }

        // Events selon le risque
        state.events().maybeTrigger(action.risk(), state, r);

        // Appliquer le résultat
        state.applyActionResult(r);

        refreshUI();
    }

    private void refreshUI() {
        statsLabel.setText(
                "PV: " + state.player().hp() +
                        " | Énergie: " + state.player().energy() +
                        " | Fatigue: " + state.player().fatigue() +
                        " | Mental: " + state.player().mental() +
                        " | Défense base: " + state.base().defense() +
                        " | Temps: " + state.clock().days() + "j " + (state.clock().hours() % 24) + "h" +
                        " | Menace: " + state.threat().value() + "/100");

        invLabel.setText(
                "Inventaire — Eau: " + state.inventory().get(Resource.WATER) +
                        " | Nourriture: " + state.inventory().get(Resource.FOOD) +
                        " | Matériaux: " + state.inventory().get(Resource.MATERIALS) +
                        " | Médicaments: " + state.inventory().get(Resource.MEDICINE));

        StringBuilder sb = new StringBuilder();
        for (String line : state.log().lines())
            sb.append(line).append("\n");
        logArea.setText(sb.toString());
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
