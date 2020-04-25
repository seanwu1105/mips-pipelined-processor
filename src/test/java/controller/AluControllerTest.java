package controller;

import component.Alu;
import signal.FunctionCode;
import org.junit.jupiter.api.Test;

import static controller.MainController.AluOperation.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AluControllerTest {

    @Test
    void testGetAluControl() {
        assertEquals(Alu.AluControl.ADD, AluController.getAluControl(R_TYPE, FunctionCode.ADD));
        assertEquals(Alu.AluControl.SUBTRACT, AluController.getAluControl(R_TYPE, FunctionCode.SUBTRACT));
        assertEquals(Alu.AluControl.AND, AluController.getAluControl(R_TYPE, FunctionCode.AND));
        assertEquals(Alu.AluControl.OR, AluController.getAluControl(R_TYPE, FunctionCode.OR));
        assertEquals(Alu.AluControl.SET_ON_LESS_THAN, AluController.getAluControl(R_TYPE, FunctionCode.SET_ON_LESS_THAN));
        assertEquals(Alu.AluControl.ADD, AluController.getAluControl(MEMORY_REFERENCE, FunctionCode.OR /* don't care */));
        assertEquals(Alu.AluControl.ADD, AluController.getAluControl(MEMORY_REFERENCE, FunctionCode.OR /* don't care */));
        assertEquals(Alu.AluControl.SUBTRACT, AluController.getAluControl(BRANCH, FunctionCode.OR /* don't care */));
    }
}
