package dev.micah.arun.utils;

public class AttachedBoolean {

    private boolean b;
    private String value;

    public AttachedBoolean(boolean b, String value) {
        this.b = b;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean getBoolean() {
        return b;
    }

}
