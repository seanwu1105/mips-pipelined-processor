package signal;

import org.jetbrains.annotations.NotNull;

public class Instruction implements Signal {

    @NotNull
    private final String raw;

    public Instruction(@NotNull String raw) {
        this.raw = raw.replaceAll("\\s", "");
        if (!isLengthCorrect()) {
            throw new IllegalArgumentException("The size of instruction should be 32 bits.");
        }
    }

    public Instruction(int value) {
        this(String.format("%32s", Integer.toBinaryString(value)).replace(' ', '0'));
    }

    private boolean isLengthCorrect() {
        return raw.length() == 32;
    }

    @NotNull
    @Override
    public String getRaw() {
        return raw;
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

    public int toInt() {
        return Integer.parseUnsignedInt(raw, 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instruction that = (Instruction) o;

        return raw.equals(that.raw);
    }

    @Override
    public int hashCode() {
        return raw.hashCode();
    }
}
