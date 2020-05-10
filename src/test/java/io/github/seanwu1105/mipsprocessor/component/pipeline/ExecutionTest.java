package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.component.Alu;
import io.github.seanwu1105.mipsprocessor.component.ForwardingUnit;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.FunctionCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExecutionTest {

    final int programCounter = 0, registerData1 = 11, registerData2 = 12, immediate = 20, rt = 2, rd = 3;
    private final InstructionDecodeToExecutionRegister idExe = mock(InstructionDecodeToExecutionRegister.class);
    private final ForwardingUnit forwardingUnit = mock(ForwardingUnit.class);
    private final ExecutionToMemoryAccessRegister exeMem = mock(ExecutionToMemoryAccessRegister.class);
    private final MemoryAccessToWriteBackRegister memWb = mock(MemoryAccessToWriteBackRegister.class);

    private Execution execution;

    ExecutionTest() {
        when(idExe.getProgramCounter()).thenReturn(programCounter);
        when(idExe.getRegisterData1()).thenReturn(registerData1);
        when(idExe.getRegisterData2()).thenReturn(registerData2);
        when(idExe.getImmediate()).thenReturn(immediate);
        when(idExe.getRt()).thenReturn(rt);
        when(idExe.getRd()).thenReturn(rd);
        when(forwardingUnit.getOperand1ForwardingSignal()).thenReturn(ForwardingUnit.ForwardingSignal.FROM_ID);
        when(forwardingUnit.getOperand2ForwardingSignal()).thenReturn(ForwardingUnit.ForwardingSignal.FROM_ID);
    }

    @BeforeEach
    void buildUp() {
        execution = new Execution(idExe, new Alu(), new Alu());
        execution.setForwardingUnit(forwardingUnit);
        execution.setExecutionToMemoryAccessRegister(exeMem);
        execution.setMemoryAccessToWriteBackRegister(memWb);
    }

    @Test
    void testPropertiesPass() {
        final MainController.Branch expectedBranch = MainController.Branch.FALSE;
        final MainController.MemoryRead expectedMemoryRead = MainController.MemoryRead.TRUE;
        final MainController.MemoryWrite expectedMemoryWrite = MainController.MemoryWrite.FALSE;
        final MainController.RegisterWrite expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final MainController.MemoryToRegister expectedMemoryToRegister = MainController.MemoryToRegister.FROM_MEMORY;

        when(idExe.getBranch()).thenReturn(expectedBranch);
        when(idExe.getMemoryRead()).thenReturn(expectedMemoryRead);
        when(idExe.getMemoryWrite()).thenReturn(expectedMemoryWrite);
        when(idExe.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(idExe.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);
        when(idExe.getRegisterDestination()).thenReturn(MainController.RegisterDestination.RD);
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.R_TYPE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.REGISTER);
        when(idExe.getFunctionCode()).thenReturn(FunctionCode.ADD);

        execution.run();

        assertEquals(expectedBranch, execution.getBranch());
        assertEquals(expectedMemoryRead, execution.getMemoryRead());
        assertEquals(expectedMemoryWrite, execution.getMemoryWrite());
        assertEquals(expectedRegisterWrite, execution.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, execution.getMemoryToRegister());
        assertEquals(registerData2, execution.getRegisterData2());
    }

    @Test
    void testAdd() {
        when(idExe.getRegisterDestination()).thenReturn(MainController.RegisterDestination.RD);
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.R_TYPE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.REGISTER);
        when(idExe.getFunctionCode()).thenReturn(FunctionCode.ADD);

        execution.run();

        assertEquals(programCounter + immediate * 4, execution.getBranchResult());
        assertEquals(registerData1 + registerData2, execution.getAluResult());
        assertEquals(rd, execution.getWriteRegisterAddress());
    }

    @Test
    void testSubtract() {
        when(idExe.getRegisterDestination()).thenReturn(MainController.RegisterDestination.RD);
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.R_TYPE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.REGISTER);
        when(idExe.getFunctionCode()).thenReturn(FunctionCode.SUBTRACT);

        execution.run();

        assertEquals(programCounter + immediate * 4, execution.getBranchResult());
        assertEquals(registerData1 - registerData2, execution.getAluResult());
        assertEquals(rd, execution.getWriteRegisterAddress());
    }

    @Test
    void testAnd() {
        when(idExe.getRegisterDestination()).thenReturn(MainController.RegisterDestination.RD);
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.R_TYPE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.REGISTER);
        when(idExe.getFunctionCode()).thenReturn(FunctionCode.AND);

        execution.run();

        assertEquals(programCounter + immediate * 4, execution.getBranchResult());
        assertEquals(registerData1 & registerData2, execution.getAluResult());
        assertEquals(rd, execution.getWriteRegisterAddress());
    }

    @Test
    void testOr() {
        when(idExe.getRegisterDestination()).thenReturn(MainController.RegisterDestination.RD);
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.R_TYPE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.REGISTER);
        when(idExe.getFunctionCode()).thenReturn(FunctionCode.OR);

        execution.run();

        assertEquals(programCounter + immediate * 4, execution.getBranchResult());
        assertEquals(registerData1 | registerData2, execution.getAluResult());
        assertEquals(rd, execution.getWriteRegisterAddress());
    }

    @Test
    void testSetOnLessThan() {
        when(idExe.getRegisterDestination()).thenReturn(MainController.RegisterDestination.RD);
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.R_TYPE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.REGISTER);
        when(idExe.getFunctionCode()).thenReturn(FunctionCode.SET_ON_LESS_THAN);

        execution.run();

        assertEquals(programCounter + immediate * 4, execution.getBranchResult());
        assertEquals(1, execution.getAluResult());
        assertEquals(rd, execution.getWriteRegisterAddress());
    }

    @Test
    void testLoadWord() {
        when(idExe.getRegisterDestination()).thenReturn(MainController.RegisterDestination.RT);
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.MEMORY_REFERENCE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.IMMEDIATE);

        execution.run();

        assertEquals(programCounter + immediate * 4, execution.getBranchResult());
        assertEquals(registerData1 + immediate, execution.getAluResult());
        assertEquals(rt, execution.getWriteRegisterAddress());
    }

    @Test
    void testSaveWord() {
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.MEMORY_REFERENCE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.IMMEDIATE);

        execution.run();

        assertEquals(programCounter + immediate * 4, execution.getBranchResult());
        assertEquals(registerData1 + immediate, execution.getAluResult());
    }

    @Test
    void testBranchOnEqual() {
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.BRANCH);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.REGISTER);

        execution.run();

        assertEquals(programCounter + immediate * 4, execution.getBranchResult());
        assertEquals(registerData1 - registerData2, execution.getAluResult());
    }

    @Test
    void testDataHazardAtExecutionStage() {
        int forwardedValue = 5;
        when(forwardingUnit.getOperand1ForwardingSignal()).thenReturn(ForwardingUnit.ForwardingSignal.FROM_EXE);
        when(forwardingUnit.getOperand2ForwardingSignal()).thenReturn(ForwardingUnit.ForwardingSignal.FROM_EXE);
        when(exeMem.getAluResult()).thenReturn(forwardedValue);

        when(idExe.getRegisterDestination()).thenReturn(MainController.RegisterDestination.RD);
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.R_TYPE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.REGISTER);
        when(idExe.getFunctionCode()).thenReturn(FunctionCode.ADD);

        execution.run();

        assertEquals(forwardedValue + forwardedValue, execution.getAluResult());
    }

    @Test
    void testDataHazardAtMemoryAccessStage() {
        int forwardedValue = 5;
        when(forwardingUnit.getOperand1ForwardingSignal()).thenReturn(ForwardingUnit.ForwardingSignal.FROM_MEM);
        when(forwardingUnit.getOperand2ForwardingSignal()).thenReturn(ForwardingUnit.ForwardingSignal.FROM_MEM);
        when(memWb.getWriteRegisterData()).thenReturn(forwardedValue);

        when(idExe.getRegisterDestination()).thenReturn(MainController.RegisterDestination.RD);
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.R_TYPE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.REGISTER);
        when(idExe.getFunctionCode()).thenReturn(FunctionCode.ADD);

        execution.run();

        assertEquals(forwardedValue + forwardedValue, execution.getAluResult());
    }
}