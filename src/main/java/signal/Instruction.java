package signal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Instruction implements Signal {

    static public final Instruction NOP = new Instruction("00000000000000000000000000000000");

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

    public int getRs() {
        String rsRaw = raw.substring(6, 11);
        return Integer.parseInt(rsRaw, 2);
    }

    public int getRt() {
        String rtRaw = raw.substring(11, 16);
        return Integer.parseInt(rtRaw, 2);
    }

    public int getRd() {
        String rdRaw = raw.substring(16, 21);
        return Integer.parseInt(rdRaw, 2);
    }

    @NotNull
    public FunctionCode getFunctionCode() {
        for (FunctionCode functionCode : FunctionCode.values())
            if (functionCode.getRaw().equals(raw.substring(26, 32)))
                return functionCode;

        throw new IllegalStateException("Unknown function code.");
    }

    public int getImmediate() {
        String immediateRaw = raw.substring(16, 32);
        return Integer.parseInt(immediateRaw, 2);
    }

    public int toInt() {
        return Integer.parseUnsignedInt(raw, 2);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instruction that = (Instruction) o;

        return raw.equals(that.raw);
    }

    @Override
    public int hashCode() {
        return raw.hashCode();
    }

    @NotNull
    @Override
    public String toString() {
        return getRaw();
    }
}
