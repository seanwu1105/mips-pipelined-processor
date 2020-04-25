package model;

import org.jetbrains.annotations.NotNull;

public enum AluControl implements Signal {
    AND("000"),
    OR("001"),
    ADD("010"),
    SUBTRACT("110"),
    SET_ON_LESS_THAN("111");

    @NotNull
    private final String raw;

    AluControl(@NotNull String raw) {
        this.raw = raw;
    }

    @NotNull
    public String getRaw() {
        return raw;
    }
}
