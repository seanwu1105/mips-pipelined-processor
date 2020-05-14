package io.github.seanwu1105.mipsprocessor.controller;

import io.github.seanwu1105.mipsprocessor.component.Alu;
import io.github.seanwu1105.mipsprocessor.signal.FunctionCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AluController {

    @NotNull
    public static Alu.AluControl getAluControl(
            @NotNull final MainController.AluOperation aluOperation,
            @Nullable final FunctionCode functionCode
    ) {
        switch (aluOperation) {
            case R_TYPE:
                switch (Objects.requireNonNull(functionCode)) {
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
            case I_TYPE_ADD:
                return Alu.AluControl.ADD;
            case I_TYPE_AND:
                return Alu.AluControl.AND;
            case I_TYPE_SUBTRACT:
                return Alu.AluControl.SUBTRACT;
            default:
                throw new IllegalStateException("Unknown ALU operation code.");
        }
    }
}
