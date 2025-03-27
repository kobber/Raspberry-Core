package cc.cassian.raspberry.compat;

import pepjebs.mapatlases.config.MapAtlasesClientConfig;

public class MapAtlasesCompat {
    public static boolean showingCoords() {
        return MapAtlasesClientConfig.drawMinimapCoords.get();
    }
}
