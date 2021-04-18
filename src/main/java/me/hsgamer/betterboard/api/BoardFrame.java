package me.hsgamer.betterboard.api;

import java.util.List;

public final class BoardFrame {
    private final String title;
    private final List<String> lines;

    public BoardFrame(String title, List<String> lines) {
        this.title = title;
        this.lines = lines;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getLines() {
        return lines;
    }
}
