package controller;

import org.jetbrains.annotations.NotNull;
import signal.Instruction;
import signal.OpCode;
import signal.Signal;

public class MainController {

    @NotNull
    private Instruction instruction = Instruction.NOP;

    public void setInstruction(@NotNull Instruction instruction) {
        this.instruction = instruction;
    }

    @NotNull
    public AluOperation getAluOperation() {
        if (instruction == Instruction.NOP) return AluOperation.MEMORY_REFERENCE;
        switch (instruction.getOpCode()) {
            case R_TYPE:
                return AluOperation.R_TYPE;
            case BRANCH_ON_EQUAL:
                return AluOperation.BRANCH;
            default:
                return AluOperation.MEMORY_REFERENCE;
        }
    }

    @NotNull
    public AluSource getAluSource() {
        if (getAluOperation() == AluOperation.MEMORY_REFERENCE && instruction != Instruction.NOP)
            return AluSource.IMMEDIATE;
        return AluSource.REGISTER;
    }

    @NotNull
    public MemoryRead getMemoryRead() {
        if (instruction.getOpCode() == OpCode.LOAD_WORD)
            return MemoryRead.TRUE;
        return MemoryRead.FALSE;
    }

    @NotNull
    public MemoryWrite getMemoryWrite() {
        if (instruction.getOpCode() == OpCode.SAVE_WORD)
            return MemoryWrite.TRUE;
        return MemoryWrite.FALSE;
    }

    @NotNull
    public MemoryToRegister getMemoryToRegister() {
        if (instruction.getOpCode() == OpCode.LOAD_WORD)
            return MemoryToRegister.FROM_MEMORY;
        return MemoryToRegister.FROM_ALU_RESULT;
    }

    @NotNull
    public RegisterDestination getRegisterDestination() {
        if (instruction == Instruction.NOP) return RegisterDestination.RT;
        if (getAluOperation() == AluOperation.R_TYPE)
            return RegisterDestination.RD;
        return RegisterDestination.RT;
    }

    @NotNull
    public RegisterWrite getRegisterWrite() {
        if (instruction == Instruction.NOP) return RegisterWrite.FALSE;
        if (instruction.getOpCode() == OpCode.R_TYPE || instruction.getOpCode() == OpCode.LOAD_WORD)
            return RegisterWrite.TRUE;
        return RegisterWrite.FALSE;
    }

    @NotNull
    public Branch getBranch() {
        if (getAluOperation() == AluOperation.BRANCH)
            return Branch.TRUE;
        return Branch.FALSE;
    }

    public enum AluOperation implements Signal {
        R_TYPE("10"),
        MEMORY_REFERENCE("00"),
        BRANCH("01");

        @NotNull
        private final String raw;

        AluOperation(@NotNull String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @Override
        public String toString() {
            return getRaw();
        }
    }

    public enum AluSource implements Signal {
        REGISTER("0"),
        IMMEDIATE("1");

        @NotNull
        private final String raw;

        AluSource(@NotNull String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @Override
        public String toString() {
            return getRaw();
        }
    }

    public enum MemoryRead implements Signal {
        TRUE("1"),
        FALSE("0");

        @NotNull
        private final String raw;

        MemoryRead(@NotNull String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @Override
        public String toString() {
            return getRaw();
        }
    }

    public enum MemoryWrite implements Signal {
        TRUE("1"),
        FALSE("0");

        @NotNull
        private final String raw;

        MemoryWrite(@NotNull String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @Override
        public String toString() {
            return getRaw();
        }
    }

    public enum MemoryToRegister implements Signal {
        FROM_MEMORY("1"),
        FROM_ALU_RESULT("0");

        @NotNull
        private final String raw;

        MemoryToRegister(@NotNull String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @Override
        public String toString() {
            return getRaw();
        }
    }

    public enum RegisterDestination implements Signal {
        RD("1"),
        RT("0");

        @NotNull
        private final String raw;

        RegisterDestination(@NotNull String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @Override
        public String toString() {
            return getRaw();
        }
    }

    public enum RegisterWrite implements Signal {
        TRUE("1"),
        FALSE("0");

        @NotNull
        private final String raw;

        RegisterWrite(@NotNull String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @Override
        public String toString() {
            return getRaw();
        }
    }

    public enum Branch implements Signal {
        TRUE("1"),
        FALSE("0");

        @NotNull
        private final String raw;

        Branch(@NotNull String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @Override
        public String toString() {
            return getRaw();
        }
    }
}
