package signal;

import org.jetbrains.annotations.NotNull;

public enum OpCode implements Signal {
    R_TYPE("000000"),
    LOAD_WORD("100011"),
    SAVE_WORD("101011"),
    BRANCH_ON_EQUAL("000100");


    @NotNull
    private final String raw;

    OpCode(@NotNull String raw) {
        this.raw = raw.replaceAll("\\s", "");
    }

    @NotNull
    @Override
    public String getRaw() {
        return raw;
    }
}
