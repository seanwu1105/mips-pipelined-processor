package controller;

import org.jetbrains.annotations.NotNull;
import signal.Instruction;
import signal.OpCode;
import signal.Signal;

public class MainController {
    private Instruction instruction;

    public AluOperation getAluOperation() {
        switch (instruction.getOpCode()) {
            case LOAD_WORD:
            case SAVE_WORD:
                return AluOperation.MEMORY_REFERENCE;
            case BRANCH_ON_EQUAL:
                return AluOperation.BRANCH;
            default:
                return AluOperation.R_TYPE;
        }
    }

    public AluSource getAluSource() {
        if (getAluOperation() == AluOperation.MEMORY_REFERENCE)
            return AluSource.IMMEDIATE;
        return AluSource.REGISTER;
    }

    public MemoryRead getMemoryRead() {
        if (instruction.getOpCode() == OpCode.LOAD_WORD)
            return MemoryRead.TRUE;
        return MemoryRead.FALSE;
    }

    public MemoryWrite getMemoryWrite() {
        if (instruction.getOpCode() == OpCode.SAVE_WORD)
            return MemoryWrite.TRUE;
        return MemoryWrite.FALSE;
    }

    public MemoryToRegister getMemoryToRegister() {
        if (instruction.getOpCode() == OpCode.LOAD_WORD)
            return MemoryToRegister.FROM_MEMORY;
        return MemoryToRegister.FROM_ALU_RESULT;
    }

    public RegisterDestination getRegisterDestination() {
        if (getAluOperation() == AluOperation.R_TYPE)
            return RegisterDestination.RD;
        return RegisterDestination.RT;
    }

    public RegisterWrite getRegisterWrite() {
        if (instruction.getOpCode() == OpCode.BRANCH_ON_EQUAL || instruction.getOpCode() == OpCode.SAVE_WORD)
            return RegisterWrite.FALSE;
        return RegisterWrite.TRUE;
    }

    public Branch getBranch() {
        if (getAluOperation() == AluOperation.BRANCH)
            return Branch.TRUE;
        return Branch.FALSE;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public Instruction getInstruction() {
        return instruction;
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
    }
}
