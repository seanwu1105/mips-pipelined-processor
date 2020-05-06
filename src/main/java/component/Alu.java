package component;

import org.jetbrains.annotations.NotNull;
import signal.Signal;

public class Alu {

    @NotNull
    private AluControl control = AluControl.ADD;
    private int operand1, operand2;

    public void setControl(@NotNull final AluControl control) {
        this.control = control;
    }

    public int getResult() {
        switch (control) {
            case ADD:
                return operand1 + operand2;
            case SUBTRACT:
                return operand1 - operand2;
            case AND:
                return operand1 & operand2;
            case OR:
                return operand1 | operand2;
            case SET_ON_LESS_THAN:
                return operand1 < operand2 ? 1 : 0;
            default:
                throw new IllegalStateException("Unknown ALU control.");
        }
    }

    public void setOperand1(final int operand1) {
        this.operand1 = operand1;
    }

    public void setOperand2(final int operand2) {
        this.operand2 = operand2;
    }

    public enum AluControl implements Signal {
        AND("000"),
        OR("001"),
        ADD("010"),
        SUBTRACT("110"),
        SET_ON_LESS_THAN("111");

        @NotNull
        private final String raw;

        AluControl(@NotNull final String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }
    }
}
