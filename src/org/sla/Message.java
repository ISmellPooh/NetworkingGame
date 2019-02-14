package org.sla;

import java.io.Serializable;

public class Message implements Serializable {
    // Message includes both sender ID and text data being sent
    private String sender;
    private String data;
    // both fields are simple Strings, so default code is used to read/write these Strings

    Message(String what) {
        data = what;
    }

    String sender() {
        return sender;
    }

    String data() {
        return data;
    }

    public String toString() {
        return "\"" + data + "\" from: " + sender;
    }
}
