package model;

import org.jetbrains.annotations.NotNull;

public enum FunctionCode {
    ADD("100000"),
    SUBTRACT("100010"),
    AND("100100"),
    OR("100101"),
    SET_ON_LESS_THAN("101010");

    @NotNull
    private final String raw;

    FunctionCode(@NotNull String raw) {
        this.raw = raw;
    }

    public @NotNull String getRaw() {
        return raw;
    }
}
