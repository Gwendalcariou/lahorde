package game.core;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class TechCatalog {
        private static final List<TechDef> ALL = List.of(
                        new TechDef(TechId.WATER_COLLECTOR, "Collecte d'eau I",
                                        "+1 eau garanti sur certaines explorations",
                                        6, 90, 15),
                        new TechDef(TechId.WORKSHOP, "Atelier",
                                        "Réduit le coût matériaux de fabrication simple",
                                        8, 120, 20),
                        new TechDef(TechId.TRAPS, "Pièges",
                                        "-10 dégâts de horde",
                                        10, 150, 25),
                        new TechDef(TechId.STORAGE_I, "Stockage I",
                                        "+25 capacité de stockage",
                                        8, 120, 15),
                        new TechDef(TechId.INFIRMARY, "Infirmerie",
                                        "Soins ameliorés quand tu utilises MEDICINE",
                                        10, 180, 25));

        private static final Map<TechId, TechDef> BY_ID = new EnumMap<>(TechId.class);

        static {
                for (TechDef d : ALL)
                        BY_ID.put(d.id, d);
        }

        private TechCatalog() {
        }

        public static List<TechDef> all() {
                return ALL;
        }

        public static TechDef get(TechId id) {
                return BY_ID.get(id);
        }
}
