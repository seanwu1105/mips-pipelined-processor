package controller;

import model.AluControl;
import model.FunctionCode;
import org.jetbrains.annotations.NotNull;

public class AluController {

    @NotNull
    static public AluControl getAluControl(MainController.AluOp aluOp, FunctionCode functionCode) {
        switch (aluOp) {
            case R_TYPE:
                switch (functionCode) {
                    case ADD:
                        return AluControl.ADD;
                    case SUBTRACT:
                        return AluControl.SUBTRACT;
                    case AND:
                        return AluControl.AND;
                    case OR:
                        return AluControl.OR;
                    case SET_ON_LESS_THAN:
                        return AluControl.SET_ON_LESS_THAN;
                    default:
                        throw new IllegalStateException("Unknown function code.");
                }
            case MEMORY_REFERENCE:
                return AluControl.ADD;
            case BRANCH:
                return AluControl.SUBTRACT;
            default:
                throw new IllegalStateException("Unknown ALU operation code.");
        }
    }
}
