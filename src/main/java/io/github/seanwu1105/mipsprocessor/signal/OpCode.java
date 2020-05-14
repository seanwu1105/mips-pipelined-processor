package io.github.seanwu1105.mipsprocessor.signal;

import org.jetbrains.annotations.NotNull;

public enum OpCode implements Signal {
    R_TYPE("000000"),
    ADD_IMMEDIATE("001000"),
    AND_IMMEDIATE("001100"),
    LOAD_WORD("100011"),
    SAVE_WORD("101011"),
    BRANCH_ON_EQUAL("000100"),
    BRANCH_ON_NOT_EQUAL("000101");


    @NotNull
    private final String raw;

    OpCode(@NotNull final String raw) {
        this.raw = raw.replaceAll("\\s", "");
    }

    @NotNull
    @Override
    public String getRaw() {
        return raw;
    }
}
