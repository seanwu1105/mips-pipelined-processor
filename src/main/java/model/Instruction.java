package model;

import org.jetbrains.annotations.NotNull;

public class Instruction {

    @NotNull
    private final String raw;

    public Instruction(@NotNull String raw) {
        this.raw = raw.replaceAll("\\s", "");
        if (!isLengthCorrect()) {
            throw new IllegalArgumentException("The size of instruction should be 32 bits.");
        }
    }

    private boolean isLengthCorrect() {
        return raw.length() == 32;
    }

    @NotNull
    public OpCode getOpCode() {
        for (OpCode opCode : OpCode.values())
            if (opCode.getRaw().equals(raw.substring(0, 6)))
                return opCode;

        throw new IllegalStateException("Unknown OP code.");
    }

    @NotNull
    public FunctionCode getFunctionCode() {
        for (FunctionCode functionCode : FunctionCode.values())
            if (functionCode.getRaw().equals(raw.substring(26, 32)))
                return functionCode;

        throw new IllegalStateException("Unknown function code.");
    }
}
