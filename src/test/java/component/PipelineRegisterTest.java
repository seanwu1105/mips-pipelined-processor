package component;

import component.pipeline.*;
import controller.MainController;
import org.junit.jupiter.api.Test;
import signal.FunctionCode;
import signal.Instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PipelineRegisterTest {

    @Test
    void testGetInstructionFetchToInstructionDecodeRegisterProperties() {
        final int expectedProgramCounter = 8;
        final Instruction expectedInstruction = new Instruction("00000000000000000000000000000000");

        final InstructionFetch instructionFetch = mock(InstructionFetch.class);
        when(instructionFetch.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(instructionFetch.getInstruction()).thenReturn(expectedInstruction);

        final InstructionFetchToInstructionDecodeRegister ifId = new InstructionFetchToInstructionDecodeRegister(instructionFetch);
        ifId.update();

        assertEquals(expectedProgramCounter, ifId.getProgramCounter());
        assertEquals(expectedInstruction, ifId.getInstruction());
    }

    @Test
    void testGetInstructionDecodeToExecutionRegisterControlSignals() {
        final MainController.RegisterDestination expectedRegisterDestination = MainController.RegisterDestination.RD;
        final MainController.AluOperation expectedAluOperation = MainController.AluOperation.R_TYPE;
        final MainController.AluSource expectedAluSource = MainController.AluSource.REGISTER;
        final MainController.Branch expectedBranch = MainController.Branch.FALSE;
        final MainController.MemoryRead expectedMemoryRead = MainController.MemoryRead.FALSE;
        final MainController.MemoryWrite expectedMemoryWrite = MainController.MemoryWrite.FALSE;
        final MainController.RegisterWrite expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final MainController.MemoryToRegister expectedMemoryToRegister = MainController.MemoryToRegister.FROM_ALU_RESULT;

        final InstructionDecode instructionDecode = mock(InstructionDecode.class);
        when(instructionDecode.getRegisterDestination()).thenReturn(expectedRegisterDestination);
        when(instructionDecode.getAluOperation()).thenReturn(expectedAluOperation);
        when(instructionDecode.getAluSource()).thenReturn(expectedAluSource);
        when(instructionDecode.getBranch()).thenReturn(expectedBranch);
        when(instructionDecode.getMemoryRead()).thenReturn(expectedMemoryRead);
        when(instructionDecode.getMemoryWrite()).thenReturn(expectedMemoryWrite);
        when(instructionDecode.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(instructionDecode.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);

        final InstructionDecodeToExecutionRegister idExe = new InstructionDecodeToExecutionRegister(instructionDecode);
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
        final int expectedProgramCounter = 12;
        final int expectedRegisterData1 = 4;
        final int expectedRegisterData2 = 5;
        final int expectedImmediate = 10;
        final FunctionCode expectedFunctionCode = FunctionCode.OR;
        final int expectedRt = 6;
        final int expectedRd = 7;

        final InstructionDecode instructionDecode = mock(InstructionDecode.class);
        when(instructionDecode.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(instructionDecode.getRegisterData1()).thenReturn(expectedRegisterData1);
        when(instructionDecode.getRegisterData2()).thenReturn(expectedRegisterData2);
        when(instructionDecode.getImmediate()).thenReturn(expectedImmediate);
        when(instructionDecode.getFunctionCode()).thenReturn(expectedFunctionCode);
        when(instructionDecode.getRt()).thenReturn(expectedRt);
        when(instructionDecode.getRd()).thenReturn(expectedRd);

        final InstructionDecodeToExecutionRegister idExe = new InstructionDecodeToExecutionRegister(instructionDecode);
        idExe.update();

        assertEquals(expectedProgramCounter, idExe.getProgramCounter());
        assertEquals(expectedRegisterData1, idExe.getRegisterData1());
        assertEquals(expectedRegisterData2, idExe.getRegisterData2());
        assertEquals(expectedImmediate, idExe.getImmediate());
        assertEquals(expectedFunctionCode, idExe.getFunctionCode());
        assertEquals(expectedRt, idExe.getRt());
        assertEquals(expectedRd, idExe.getRd());
    }

    @Test
    void testGetExecutionToMemoryAccessRegisterControlSignals() {
        final MainController.RegisterWrite expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final MainController.MemoryToRegister expectedMemoryToRegister = MainController.MemoryToRegister.FROM_MEMORY;
        final MainController.MemoryRead expectedMemoryRead = MainController.MemoryRead.TRUE;
        final MainController.MemoryWrite expectedMemoryWrite = MainController.MemoryWrite.FALSE;
        final MainController.Branch expectedBranch = MainController.Branch.FALSE;
        final int expectedAluResult = 0;

        final Execution execution = mock(Execution.class);
        when(execution.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(execution.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);
        when(execution.getMemoryRead()).thenReturn(expectedMemoryRead);
        when(execution.getMemoryWrite()).thenReturn(expectedMemoryWrite);
        when(execution.getBranch()).thenReturn(expectedBranch);
        when(execution.getAluResult()).thenReturn(expectedAluResult);

        final ExecutionToMemoryAccessRegister exeMem = new ExecutionToMemoryAccessRegister(execution);
        exeMem.update();

        assertEquals(expectedRegisterWrite, exeMem.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, exeMem.getMemoryToRegister());
        assertFalse(exeMem.shouldBranch());
        assertEquals(expectedMemoryRead, exeMem.getMemoryRead());
        assertEquals(expectedMemoryWrite, exeMem.getMemoryWrite());
    }

    @Test
    void testGetExecutionToMemoryAccessRegisterProperties() {
        final int expectedBranchResult = 5 * 4 + 12;
        final int expectedAluResult = 6;
        final int expectedRegisterData2 = 7;
        final int expectedWriteRegisterAddress = 2;

        final Execution execution = mock(Execution.class);
        when(execution.getBranchResult()).thenReturn(expectedBranchResult);
        when(execution.getAluResult()).thenReturn(expectedAluResult);
        when(execution.getRegisterData2()).thenReturn(expectedRegisterData2);
        when(execution.getWriteRegisterAddress()).thenReturn(expectedWriteRegisterAddress);

        final ExecutionToMemoryAccessRegister exeMem = new ExecutionToMemoryAccessRegister(execution);
        exeMem.update();

        assertEquals(expectedBranchResult, exeMem.getBranchResult());
        assertEquals(expectedAluResult, exeMem.getAluResult());
        assertEquals(expectedRegisterData2, exeMem.getRegisterData2());
        assertEquals(expectedWriteRegisterAddress, exeMem.getWriteRegisterAddress());
    }

    @Test
    void testGetMemoryAccessToWriteBackRegisterControlSignals() {
        final MainController.RegisterWrite expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final MainController.MemoryToRegister expectedMemoryToRegister = MainController.MemoryToRegister.FROM_MEMORY;

        final MemoryAccess memoryAccess = mock(MemoryAccess.class);
        when(memoryAccess.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(memoryAccess.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);

        final MemoryAccessToWriteBackRegister memWb = new MemoryAccessToWriteBackRegister(memoryAccess);
        memWb.update();

        assertEquals(expectedRegisterWrite, memWb.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, memWb.getMemoryToRegister());
    }

    @Test
    void testGetMemoryAccessToWriteBackRegisterProperties() {
        final int expectedMemoryReadData = 2;
        final int expectedAluResult = 3;
        final int expectedWriteRegisterAddress = 4;

        final MemoryAccess memoryAccess = mock(MemoryAccess.class);
        final MemoryAccessToWriteBackRegister memWb = new MemoryAccessToWriteBackRegister(memoryAccess);
        when(memoryAccess.getMemoryReadData()).thenReturn(expectedMemoryReadData);
        when(memoryAccess.getAluResult()).thenReturn(expectedAluResult);
        when(memoryAccess.getWriteRegisterAddress()).thenReturn(expectedWriteRegisterAddress);

        when(memoryAccess.getMemoryToRegister()).thenReturn(MainController.MemoryToRegister.FROM_MEMORY);

        memWb.update();

        assertEquals(expectedWriteRegisterAddress, memWb.getWriteRegisterAddress());
        assertEquals(expectedMemoryReadData, memWb.getWriteRegisterData());

        when(memoryAccess.getMemoryToRegister()).thenReturn(MainController.MemoryToRegister.FROM_ALU_RESULT);

        memWb.update();

        assertEquals(expectedWriteRegisterAddress, memWb.getWriteRegisterAddress());
        assertEquals(expectedAluResult, memWb.getWriteRegisterData());
    }
}