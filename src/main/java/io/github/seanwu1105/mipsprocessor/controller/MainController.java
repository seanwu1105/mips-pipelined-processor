package io.github.seanwu1105.mipsprocessor.controller;

import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import io.github.seanwu1105.mipsprocessor.signal.OpCode;
import io.github.seanwu1105.mipsprocessor.signal.Signal;
import org.jetbrains.annotations.NotNull;

public class MainController {

    @NotNull
    private Instruction instruction = Instruction.NOP;

    public void setInstruction(@NotNull final Instruction instruction) {
        this.instruction = instruction;
    }

    @NotNull
    public AluOperation getAluOperation() {
        if (instruction == Instruction.NOP)
            return AluOperation.I_TYPE_ADD;
        switch (instruction.getOpCode()) {
            case R_TYPE:
                return AluOperation.R_TYPE;
            case BRANCH_ON_EQUAL:
            case BRANCH_ON_NOT_EQUAL:
                return AluOperation.I_TYPE_SUBTRACT;
            case AND_IMMEDIATE:
                return AluOperation.I_TYPE_AND;
            case LOAD_WORD:
            case SAVE_WORD:
            case ADD_IMMEDIATE:
            default:
                return AluOperation.I_TYPE_ADD;
        }
    }

    @NotNull
    public AluSource getAluSource() {
        if (instruction == Instruction.NOP) return AluSource.REGISTER;
        switch (instruction.getOpCode()) {
            case R_TYPE:
            case BRANCH_ON_EQUAL:
            case BRANCH_ON_NOT_EQUAL:
                return AluSource.REGISTER;
            case ADD_IMMEDIATE:
            case AND_IMMEDIATE:
            case LOAD_WORD:
            case SAVE_WORD:
                return AluSource.IMMEDIATE;
            default:
                throw new IllegalStateException("Unknown OP code.");
        }
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
        if (instruction.getOpCode() == OpCode.SAVE_WORD || instruction.getOpCode() == OpCode.BRANCH_ON_EQUAL || instruction.getOpCode() == OpCode.BRANCH_ON_NOT_EQUAL)
            return RegisterWrite.FALSE;
        return RegisterWrite.TRUE;
    }

    @NotNull
    public Branch getBranch() {
        if (instruction.getOpCode() == OpCode.BRANCH_ON_EQUAL || instruction.getOpCode() == OpCode.BRANCH_ON_NOT_EQUAL)
            return Branch.TRUE;
        return Branch.FALSE;
    }

    public enum AluOperation implements Signal {
        R_TYPE("10"),
        I_TYPE_ADD("00"),
        I_TYPE_SUBTRACT("01"),
        I_TYPE_AND("11");

        @NotNull
        private final String raw;

        AluOperation(@NotNull final String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @NotNull
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

        AluSource(@NotNull final String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @NotNull
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

        MemoryRead(@NotNull final String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @NotNull
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

        MemoryWrite(@NotNull final String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @NotNull
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

        MemoryToRegister(@NotNull final String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @NotNull
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

        RegisterDestination(@NotNull final String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @NotNull
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

        RegisterWrite(@NotNull final String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @NotNull
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

        Branch(@NotNull final String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }

        @NotNull
        @Override
        public String toString() {
            return getRaw();
        }
    }
}
