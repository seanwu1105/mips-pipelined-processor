package controller;

import model.Instruction;
import org.jetbrains.annotations.NotNull;

public class MainController {

    static public AluOp getAluOp(Instruction instruction) {
        switch (instruction.getOpCode()) {
            case R_TYPE:
                return AluOp.R_TYPE;
            case LOAD_WORD:
            case SAVE_WORD:
                return AluOp.MEMORY_REFERENCE;
            case BRANCH_ON_EQUAL:
                return AluOp.BRANCH;
            default:
                throw new IllegalStateException("Unknown OP code.");
        }
    }

    public enum AluOp {
        R_TYPE("10"),
        MEMORY_REFERENCE("00"),
        BRANCH("01");

        @NotNull
        private final String raw;

        AluOp(@NotNull String raw) {
            this.raw = raw;
        }

        public @NotNull String getRaw() {
            return raw;
        }
    }
}
