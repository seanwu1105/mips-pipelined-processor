package controller;

import signal.AluControl;
import signal.FunctionCode;
import org.junit.jupiter.api.Test;

import static controller.MainController.AluOperation.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AluControllerTest {

    @Test
    void testGetAluControl() {
        assertEquals(AluControl.ADD, AluController.getAluControl(R_TYPE, FunctionCode.ADD));
        assertEquals(AluControl.SUBTRACT, AluController.getAluControl(R_TYPE, FunctionCode.SUBTRACT));
        assertEquals(AluControl.AND, AluController.getAluControl(R_TYPE, FunctionCode.AND));
        assertEquals(AluControl.OR, AluController.getAluControl(R_TYPE, FunctionCode.OR));
        assertEquals(AluControl.SET_ON_LESS_THAN, AluController.getAluControl(R_TYPE, FunctionCode.SET_ON_LESS_THAN));
        assertEquals(AluControl.ADD, AluController.getAluControl(MEMORY_REFERENCE, FunctionCode.OR /* don't care */));
        assertEquals(AluControl.ADD, AluController.getAluControl(MEMORY_REFERENCE, FunctionCode.OR /* don't care */));
        assertEquals(AluControl.SUBTRACT, AluController.getAluControl(BRANCH, FunctionCode.OR /* don't care */));
    }
}
