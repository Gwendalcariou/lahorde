package game.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LogBook {
    public enum Channel {
        BASE,
        EXPLORE
    }

    public static final class Entry {
        public final Channel channel;
        public final String text;

        public Entry(Channel channel, String text) {
            this.channel = channel;
            this.text = text;
        }
    }

    private final List<Entry> entries = new ArrayList<>();
    private Channel current = Channel.BASE;

    public void add(String line) {
        add(current, line);
    }

    public void add(Channel channel, String line) {
        entries.add(new Entry(channel, line));
    }

    public Channel channel() {
        return current;
    }

    public void setChannel(Channel channel) {
        if (channel != null) {
            current = channel;
        }
    }

    public List<Entry> entries() {
        return Collections.unmodifiableList(entries);
    }

    public List<String> lines(Channel channel) {
        ArrayList<String> out = new ArrayList<>();
        for (Entry e : entries) {
            if (e.channel == channel) {
                out.add(e.text);
            }
        }
        return out;
    }

    public void setLines(List<String> newLines) {
        entries.clear();
        if (newLines == null) {
            return;
        }
        for (String line : newLines) {
            entries.add(new Entry(Channel.BASE, line));
        }
    }

    public void setEntries(List<Entry> newEntries) {
        entries.clear();
        if (newEntries != null) {
            entries.addAll(newEntries);
        }
    }
}
