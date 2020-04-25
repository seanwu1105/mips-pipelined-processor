package component;

import org.jetbrains.annotations.NotNull;
import signal.Signal;

public class Alu implements Component {

    @NotNull
    private AluControl control = AluControl.ADD;
    private int operand1, operand2, result;

    public void setControl(@NotNull AluControl control) {
        this.control = control;
    }

    @Override
    public void run() {
        switch (control) {
            case ADD:
                result = operand1 + operand2;
                break;
            case SUBTRACT:
                result = operand1 - operand2;
                break;
            case AND:
                result = operand1 & operand2;
                break;
            case OR:
                result = operand1 | operand2;
                break;
            case SET_ON_LESS_THAN:
                result = operand1 < operand2 ? 1 : 0;
                break;
        }
    }

    public int getResult() {
        return result;
    }

    public void setOperand1(int operand1) {
        this.operand1 = operand1;
    }

    public void setOperand2(int operand2) {
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

        AluControl(@NotNull String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }
    }
}
