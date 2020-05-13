package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.FunctionCode;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstructionDecodeToExecutionRegisterTest {

    @NotNull
    private InstructionDecode instructionDecode;
    @NotNull
    private InstructionDecodeToExecutionRegister idExe;

    @BeforeEach
    void buildUp() {
        instructionDecode = mock(InstructionDecode.class);
        idExe = new InstructionDecodeToExecutionRegister(instructionDecode);
    }

    @Test
    void testUpdate() {
        final var expectedRegisterDestination = MainController.RegisterDestination.RD;
        final var expectedAluOperation = MainController.AluOperation.R_TYPE;
        final var expectedAluSource = MainController.AluSource.REGISTER;
        final var expectedBranch = MainController.Branch.FALSE;
        final var expectedMemoryRead = MainController.MemoryRead.FALSE;
        final var expectedMemoryWrite = MainController.MemoryWrite.FALSE;
        final var expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final var expectedMemoryToRegister = MainController.MemoryToRegister.FROM_ALU_RESULT;
        final var expectedRegisterData1 = 4;
        final var expectedRegisterData2 = 5;
        final var expectedImmediate = 10;
        final var expectedFunctionCode = FunctionCode.OR;
        final var expectedRt = 6;
        final var expectedRd = 7;

        when(instructionDecode.getRegisterDestination()).thenReturn(expectedRegisterDestination);
        when(instructionDecode.getAluOperation()).thenReturn(expectedAluOperation);
        when(instructionDecode.getAluSource()).thenReturn(expectedAluSource);
        when(instructionDecode.getBranch()).thenReturn(expectedBranch);
        when(instructionDecode.getMemoryRead()).thenReturn(expectedMemoryRead);
        when(instructionDecode.getMemoryWrite()).thenReturn(expectedMemoryWrite);
        when(instructionDecode.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(instructionDecode.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);
        when(instructionDecode.getRegisterData1()).thenReturn(expectedRegisterData1);
        when(instructionDecode.getRegisterData2()).thenReturn(expectedRegisterData2);
        when(instructionDecode.getImmediate()).thenReturn(expectedImmediate);
        when(instructionDecode.getFunctionCode()).thenReturn(expectedFunctionCode);
        when(instructionDecode.getRt()).thenReturn(expectedRt);
        when(instructionDecode.getRd()).thenReturn(expectedRd);

        idExe.update();

        assertEquals(expectedRegisterDestination, idExe.getRegisterDestination());
        assertEquals(expectedAluOperation, idExe.getAluOperation());
        assertEquals(expectedAluSource, idExe.getAluSource());
        assertEquals(expectedBranch, idExe.getBranch());
        assertEquals(expectedMemoryRead, idExe.getMemoryRead());
        assertEquals(expectedMemoryWrite, idExe.getMemoryWrite());
        assertEquals(expectedRegisterWrite, idExe.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, idExe.getMemoryToRegister());
        assertEquals(expectedRegisterData1, idExe.getRegisterData1());
        assertEquals(expectedRegisterData2, idExe.getRegisterData2());
        assertEquals(expectedImmediate, idExe.getImmediate());
        assertEquals(expectedFunctionCode, idExe.getFunctionCode());
        assertEquals(expectedRt, idExe.getRt());
        assertEquals(expectedRd, idExe.getRd());
    }
}