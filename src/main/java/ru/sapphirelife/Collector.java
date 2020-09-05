package ru.sapphirelife;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Collector implements Serializable {

    private List<PlayerData> data = new ArrayList<>();

    public List<PlayerData> getData() {
        return data;
    }

    public void addNew(PlayerData playerData) {
        data.add(playerData);
    }

    public void wipe() {
        data.clear();
    }

    public void forceWipe() {
        data = new ArrayList<>();
    }
}
