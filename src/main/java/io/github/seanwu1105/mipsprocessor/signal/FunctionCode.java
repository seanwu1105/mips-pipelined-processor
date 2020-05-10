package io.github.seanwu1105.mipsprocessor.signal;

import org.jetbrains.annotations.NotNull;

public enum FunctionCode implements Signal {
    ADD("100000"),
    SUBTRACT("100010"),
    AND("100100"),
    OR("100101"),
    SET_ON_LESS_THAN("101010"),
    NOP("000000");

    @NotNull
    private final String raw;

    FunctionCode(@NotNull final String raw) {
        this.raw = raw;
    }

    @NotNull
    @Override
    public String getRaw() {
        return raw;
    }
}
