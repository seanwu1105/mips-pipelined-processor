package controller;

import component.Alu;
import org.jetbrains.annotations.NotNull;
import signal.FunctionCode;

public class AluController {

    @NotNull
    static public Alu.AluControl getAluControl(
            @NotNull MainController.AluOperation aluOperation,
            @NotNull FunctionCode functionCode
    ) {
        switch (aluOperation) {
            case R_TYPE:
                switch (functionCode) {
                    case ADD:
                        return Alu.AluControl.ADD;
                    case SUBTRACT:
                        return Alu.AluControl.SUBTRACT;
                    case AND:
                        return Alu.AluControl.AND;
                    case OR:
                        return Alu.AluControl.OR;
                    case SET_ON_LESS_THAN:
                        return Alu.AluControl.SET_ON_LESS_THAN;
                    default:
                        throw new IllegalStateException("Unknown function code.");
                }
            case MEMORY_REFERENCE:
                return Alu.AluControl.ADD;
            case BRANCH:
                return Alu.AluControl.SUBTRACT;
            default:
                throw new IllegalStateException("Unknown ALU operation code.");
        }
    }
}
