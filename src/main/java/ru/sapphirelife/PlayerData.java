package ru.sapphirelife;

import java.io.Serializable;

public class PlayerData implements Serializable {

    private final String nick;
    private final String date;

    protected PlayerData(String nick, String date) {
        this.nick = nick;
        this.date = date;
    }

    @Override
    public String toString() {
        return nick + " | " + date;
    }

    public String getNick() {
        return nick;
    }

    public String getDate() {
        return date;
    }
}
