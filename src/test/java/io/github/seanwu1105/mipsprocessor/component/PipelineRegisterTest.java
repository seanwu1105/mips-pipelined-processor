package io.github.seanwu1105.mipsprocessor.component;

import io.github.seanwu1105.mipsprocessor.component.pipeline.Execution;
import io.github.seanwu1105.mipsprocessor.component.pipeline.ExecutionToMemoryAccessRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionDecode;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionDecodeToExecutionRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionFetch;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionFetchToInstructionDecodeRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.MemoryAccess;
import io.github.seanwu1105.mipsprocessor.component.pipeline.MemoryAccessToWriteBackRegister;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.FunctionCode;
import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PipelineRegisterTest {

    @Test
    void testGetInstructionFetchToInstructionDecodeRegisterProperties() {
        final var expectedProgramCounter = 8;
        final var expectedInstruction = new Instruction("00000000000000000000000000000000");

        final var instructionFetch = mock(InstructionFetch.class);
        when(instructionFetch.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(instructionFetch.getInstruction()).thenReturn(expectedInstruction);

        final var ifId = new InstructionFetchToInstructionDecodeRegister(instructionFetch);
        ifId.update();

        assertEquals(expectedProgramCounter, ifId.getProgramCounter());
        assertEquals(expectedInstruction, ifId.getInstruction());
    }

    @Test
    void testGetInstructionDecodeToExecutionRegisterControlSignals() {
        final var expectedRegisterDestination = MainController.RegisterDestination.RD;
        final var expectedAluOperation = MainController.AluOperation.R_TYPE;
        final var expectedAluSource = MainController.AluSource.REGISTER;
        final var expectedBranch = MainController.Branch.FALSE;
        final var expectedMemoryRead = MainController.MemoryRead.FALSE;
        final var expectedMemoryWrite = MainController.MemoryWrite.FALSE;
        final var expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final var expectedMemoryToRegister = MainController.MemoryToRegister.FROM_ALU_RESULT;

        final var instructionDecode = mock(InstructionDecode.class);
        when(instructionDecode.getRegisterDestination()).thenReturn(expectedRegisterDestination);
        when(instructionDecode.getAluOperation()).thenReturn(expectedAluOperation);
        when(instructionDecode.getAluSource()).thenReturn(expectedAluSource);
        when(instructionDecode.getBranch()).thenReturn(expectedBranch);
        when(instructionDecode.getMemoryRead()).thenReturn(expectedMemoryRead);
        when(instructionDecode.getMemoryWrite()).thenReturn(expectedMemoryWrite);
        when(instructionDecode.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(instructionDecode.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);

        final var idExe = new InstructionDecodeToExecutionRegister(instructionDecode);
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
        final var expectedProgramCounter = 12;
        final var expectedRegisterData1 = 4;
        final var expectedRegisterData2 = 5;
        final var expectedImmediate = 10;
        final var expectedFunctionCode = FunctionCode.OR;
        final var expectedRt = 6;
        final var expectedRd = 7;

        final var instructionDecode = mock(InstructionDecode.class);
        when(instructionDecode.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(instructionDecode.getRegisterData1()).thenReturn(expectedRegisterData1);
        when(instructionDecode.getRegisterData2()).thenReturn(expectedRegisterData2);
        when(instructionDecode.getImmediate()).thenReturn(expectedImmediate);
        when(instructionDecode.getFunctionCode()).thenReturn(expectedFunctionCode);
        when(instructionDecode.getRt()).thenReturn(expectedRt);
        when(instructionDecode.getRd()).thenReturn(expectedRd);

        final var idExe = new InstructionDecodeToExecutionRegister(instructionDecode);
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
        final var expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final var expectedMemoryToRegister = MainController.MemoryToRegister.FROM_MEMORY;
        final var expectedMemoryRead = MainController.MemoryRead.TRUE;
        final var expectedMemoryWrite = MainController.MemoryWrite.FALSE;
        final var expectedBranch = MainController.Branch.FALSE;
        final var expectedAluResult = 0;

        final var execution = mock(Execution.class);
        when(execution.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(execution.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);
        when(execution.getMemoryRead()).thenReturn(expectedMemoryRead);
        when(execution.getMemoryWrite()).thenReturn(expectedMemoryWrite);
        when(execution.getBranch()).thenReturn(expectedBranch);
        when(execution.getAluResult()).thenReturn(expectedAluResult);

        final var exeMem = new ExecutionToMemoryAccessRegister(execution);
        exeMem.update();

        assertEquals(expectedRegisterWrite, exeMem.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, exeMem.getMemoryToRegister());
        assertFalse(exeMem.shouldBranch());
        assertEquals(expectedMemoryRead, exeMem.getMemoryRead());
        assertEquals(expectedMemoryWrite, exeMem.getMemoryWrite());
    }

    @Test
    void testGetExecutionToMemoryAccessRegisterProperties() {
        final var expectedBranchResult = 5 * 4 + 12;
        final var expectedAluResult = 6;
        final var expectedRegisterData2 = 7;
        final var expectedWriteRegisterAddress = 2;

        final var execution = mock(Execution.class);
        when(execution.getBranchResult()).thenReturn(expectedBranchResult);
        when(execution.getAluResult()).thenReturn(expectedAluResult);
        when(execution.getRegisterData2()).thenReturn(expectedRegisterData2);
        when(execution.getWriteRegisterAddress()).thenReturn(expectedWriteRegisterAddress);

        final var exeMem = new ExecutionToMemoryAccessRegister(execution);
        exeMem.update();

        assertEquals(expectedBranchResult, exeMem.getBranchResult());
        assertEquals(expectedAluResult, exeMem.getAluResult());
        assertEquals(expectedRegisterData2, exeMem.getRegisterData2());
        assertEquals(expectedWriteRegisterAddress, exeMem.getWriteRegisterAddress());
    }

    @Test
    void testGetMemoryAccessToWriteBackRegisterControlSignals() {
        final var expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final var expectedMemoryToRegister = MainController.MemoryToRegister.FROM_MEMORY;

        final var memoryAccess = mock(MemoryAccess.class);
        when(memoryAccess.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(memoryAccess.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);

        final var memWb = new MemoryAccessToWriteBackRegister(memoryAccess);
        memWb.update();

        assertEquals(expectedRegisterWrite, memWb.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, memWb.getMemoryToRegister());
    }

    @Test
    void testGetMemoryAccessToWriteBackRegisterProperties() {
        final var expectedMemoryReadData = 2;
        final var expectedAluResult = 3;
        final var expectedWriteRegisterAddress = 4;

        final var memoryAccess = mock(MemoryAccess.class);
        final var memWb = new MemoryAccessToWriteBackRegister(memoryAccess);
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