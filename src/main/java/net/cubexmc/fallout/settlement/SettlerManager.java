/*
 * Copyright (c) 2016 CubeXMC. All Rights Reserved.
 * Created by PantherMan594.
 */

package net.cubexmc.fallout.settlement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by David on 3/28.
 *
 * @author David
 */
public class SettlerManager {
    private Map<UUID, SettlerManager> settlersMap;

    public SettlerManager() {
        settlersMap = new HashMap<>();
    }

    public void spawn(String name, UUID uuid, Occupation occupation, Task task) {

    }

    public Map<UUID, SettlerManager> getSettlersMap() {
        return settlersMap;
    }

    public enum Occupation {
        FARMER,
        MINER,
        BUILDER,
        MECHANIC,
        NONE
    }

    public enum Task {
        HARVEST,
        WATER,
        PLANT,
        MINE,
        BUILD,
        RUN,
        NONE
    }
}
