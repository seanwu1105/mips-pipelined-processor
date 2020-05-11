package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.component.Alu;
import io.github.seanwu1105.mipsprocessor.component.ForwardingUnit;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.FunctionCode;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExecutionTest {

    private final int registerData1 = 11;
    private final int registerData2 = 12;
    private final int immediate = 20;
    private final int rt = 2;
    private final int rd = 3;
    @NotNull
    private ForwardingUnit forwardingUnit;
    @NotNull
    private InstructionDecodeToExecutionRegister idExe;
    @NotNull
    private ExecutionToMemoryAccessRegister exeMem;
    @NotNull
    private MemoryAccessToWriteBackRegister memWb;
    @NotNull
    private Execution execution;

    @BeforeEach
    void buildUp() {
        final var programCounter = 0;
        forwardingUnit = mock(ForwardingUnit.class);
        idExe = mock(InstructionDecodeToExecutionRegister.class);
        exeMem = mock(ExecutionToMemoryAccessRegister.class);
        memWb = mock(MemoryAccessToWriteBackRegister.class);
        when(forwardingUnit.getOperand1ForwardingSignal()).thenReturn(ForwardingUnit.ForwardingSignal.FROM_ID);
        when(forwardingUnit.getOperand2ForwardingSignal()).thenReturn(ForwardingUnit.ForwardingSignal.FROM_ID);
        when(idExe.getProgramCounter()).thenReturn(programCounter);
        when(idExe.getRegisterData1()).thenReturn(registerData1);
        when(idExe.getRegisterData2()).thenReturn(registerData2);
        when(idExe.getImmediate()).thenReturn(immediate);
        when(idExe.getRt()).thenReturn(rt);
        when(idExe.getRd()).thenReturn(rd);

        execution = new Execution(idExe, new Alu());
        execution.setForwardingUnit(forwardingUnit);
        execution.setExecutionToMemoryAccessRegister(exeMem);
        execution.setMemoryAccessToWriteBackRegister(memWb);
    }

    @Test
    void testPropertiesPass() {
        final var expectedBranch = MainController.Branch.FALSE;
        final var expectedMemoryRead = MainController.MemoryRead.TRUE;
        final var expectedMemoryWrite = MainController.MemoryWrite.FALSE;
        final var expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final var expectedMemoryToRegister = MainController.MemoryToRegister.FROM_MEMORY;

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

        assertEquals(1, execution.getAluResult());
        assertEquals(rd, execution.getWriteRegisterAddress());
    }

    @Test
    void testLoadWord() {
        when(idExe.getRegisterDestination()).thenReturn(MainController.RegisterDestination.RT);
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.MEMORY_REFERENCE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.IMMEDIATE);

        execution.run();

        assertEquals(registerData1 + immediate, execution.getAluResult());
        assertEquals(rt, execution.getWriteRegisterAddress());
    }

    @Test
    void testSaveWord() {
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.MEMORY_REFERENCE);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.IMMEDIATE);

        execution.run();

        assertEquals(registerData1 + immediate, execution.getAluResult());
    }

    @Test
    void testBranchOnEqual() {
        when(idExe.getAluOperation()).thenReturn(MainController.AluOperation.BRANCH);
        when(idExe.getAluSource()).thenReturn(MainController.AluSource.REGISTER);

        execution.run();

        assertEquals(registerData1 - registerData2, execution.getAluResult());
    }

    @Test
    void testDataHazardAtExecutionStage() {
        final var forwardedValue = 5;
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
        final var forwardedValue = 5;
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