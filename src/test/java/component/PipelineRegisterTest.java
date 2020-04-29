package component;

import component.pipeline.*;
import controller.MainController;
import org.junit.jupiter.api.Test;
import signal.Instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PipelineRegisterTest {

    @Test
    void testGetInstructionFetchToInstructionDecodeRegisterProperties() {
        int expectedProgramCounter = 8;
        Instruction expectedInstruction = new Instruction("00000000000000000000000000000000");

        InstructionFetch instructionFetch = mock(InstructionFetch.class);
        when(instructionFetch.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(instructionFetch.getInstruction()).thenReturn(expectedInstruction);

        InstructionFetchToInstructionDecodeRegister ifId = new InstructionFetchToInstructionDecodeRegister(instructionFetch);
        ifId.update();

        assertEquals(expectedProgramCounter, ifId.getProgramCounter());
        assertEquals(expectedInstruction, ifId.getInstruction());
    }

    @Test
    void testGetInstructionDecodeToExecutionRegisterControlSignals() {
        MainController.RegisterDestination expectedRegisterDestination = MainController.RegisterDestination.RD;
        MainController.AluOperation expectedAluOperation = MainController.AluOperation.R_TYPE;
        MainController.AluSource expectedAluSource = MainController.AluSource.REGISTER;
        MainController.Branch expectedBranch = MainController.Branch.FALSE;
        MainController.MemoryRead expectedMemoryRead = MainController.MemoryRead.FALSE;
        MainController.MemoryWrite expectedMemoryWrite = MainController.MemoryWrite.FALSE;
        MainController.RegisterWrite expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        MainController.MemoryToRegister expectedMemoryToRegister = MainController.MemoryToRegister.FROM_ALU_RESULT;

        InstructionDecode instructionDecode = mock(InstructionDecode.class);
        when(instructionDecode.getRegisterDestination()).thenReturn(expectedRegisterDestination);
        when(instructionDecode.getAluOperation()).thenReturn(expectedAluOperation);
        when(instructionDecode.getAluSource()).thenReturn(expectedAluSource);
        when(instructionDecode.getBranch()).thenReturn(expectedBranch);
        when(instructionDecode.getMemoryRead()).thenReturn(expectedMemoryRead);
        when(instructionDecode.getMemoryWrite()).thenReturn(expectedMemoryWrite);
        when(instructionDecode.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(instructionDecode.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);

        InstructionDecodeToExecutionRegister idExe = new InstructionDecodeToExecutionRegister(instructionDecode);
        idExe.update();

        assertEquals(expectedRegisterDestination, idExe.getRegisterDestination());
        assertEquals(expectedAluOperation, idExe.getAluOperation());
        assertEquals(expectedAluSource, idExe.getAluSource());
        assertEquals(expectedBranch, idExe.getBranch());
        assertEquals(expectedMemoryRead, idExe.getMemoryRead());
        assertEquals(expectedMemoryWrite, idExe.getMemoryWrite());
        assertEquals(expectedRegisterWrite, idExe.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, idExe.getMemoryToRegister());
    }

    @Test
    void testGetInstructionDecodeToExecutionRegisterProperties() {
        int expectedNewProgramCounter = 12;
        int expectedRegisterData1 = 4;
        int expectedRegisterData2 = 5;
        int expectedImmediate = 10;
        int expectedRt = 6;
        int expectedRd = 7;

        InstructionDecode instructionDecode = mock(InstructionDecode.class);
        when(instructionDecode.getProgramCounter()).thenReturn(expectedNewProgramCounter);
        when(instructionDecode.getRegisterData1()).thenReturn(expectedRegisterData1);
        when(instructionDecode.getRegisterData2()).thenReturn(expectedRegisterData2);
        when(instructionDecode.getImmediate()).thenReturn(expectedImmediate);
        when(instructionDecode.getRt()).thenReturn(expectedRt);
        when(instructionDecode.getRd()).thenReturn(expectedRd);

        InstructionDecodeToExecutionRegister idExe = new InstructionDecodeToExecutionRegister(instructionDecode);
        idExe.update();

        assertEquals(expectedNewProgramCounter, idExe.getProgramCounter());
        assertEquals(expectedRegisterData1, idExe.getRegisterData1());
        assertEquals(expectedRegisterData2, idExe.getRegisterData2());
        assertEquals(expectedImmediate, idExe.getImmediate());
        assertEquals(expectedRt, idExe.getRt());
        assertEquals(expectedRd, idExe.getRd());
    }

    @Test
    void testGetExecutionToMemoryAccessRegisterControlSignals() {
        MainController.RegisterWrite expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        MainController.MemoryToRegister expectedMemoryToRegister = MainController.MemoryToRegister.FROM_MEMORY;
        MainController.Branch expectedBranch = MainController.Branch.FALSE;
        MainController.MemoryRead expectedMemoryRead = MainController.MemoryRead.TRUE;
        MainController.MemoryWrite expectedMemoryWrite = MainController.MemoryWrite.FALSE;

        Execution execution = mock(Execution.class);
        when(execution.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(execution.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);
        when(execution.getBranch()).thenReturn(expectedBranch);
        when(execution.getMemoryRead()).thenReturn(expectedMemoryRead);
        when(execution.getMemoryWrite()).thenReturn(expectedMemoryWrite);

        ExecutionToMemoryAccessRegister exeMem = new ExecutionToMemoryAccessRegister(execution);
        exeMem.update();

        assertEquals(expectedRegisterWrite, exeMem.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, exeMem.getMemoryToRegister());
        assertEquals(expectedBranch, exeMem.getBranch());
        assertEquals(expectedMemoryRead, exeMem.getMemoryRead());
        assertEquals(expectedMemoryWrite, exeMem.getMemoryWrite());
    }

    @Test
    void testGetExecutionToMemoryAccessRegisterProperties() {
    }

    @Test
    void testGetMemoryAccessToWriteBackRegisterProperties() {
    }
}