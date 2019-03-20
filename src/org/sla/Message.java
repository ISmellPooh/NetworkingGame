package org.sla;

import java.io.Serializable;

public class Message implements Serializable {
    // Message includes both sender ID and text data being sent
    private String sender;
    private String data;
    private int x;
    private int y;
    // both fields are simple Strings, so default code is used to read/write these Strings

    Message(String who, String what) {
        sender = who;
        data = what;
    }

    Message(String who, String what, int newx, int newy) {
        sender = who;
        data = what;
        x = newx;
        y = newy;
    }

    String sender() {
        return sender;
    }

    String data() {
        return data;
    }

    int x() {
        return x;
    }

    int y() {
        return y;
    }

    public String toString() {
        return "\"" + data + "\" (" + x + "," + y + ") from: " + sender;
    }
}
