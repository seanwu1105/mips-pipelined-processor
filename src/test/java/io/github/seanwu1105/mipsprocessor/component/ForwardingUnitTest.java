package io.github.seanwu1105.mipsprocessor.component;

import io.github.seanwu1105.mipsprocessor.component.pipeline.ExecutionToMemoryAccessRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionDecodeToExecutionRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.MemoryAccessToWriteBackRegister;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.seanwu1105.mipsprocessor.component.ForwardingUnit.ForwardingSignal.FROM_EXE;
import static io.github.seanwu1105.mipsprocessor.component.ForwardingUnit.ForwardingSignal.FROM_ID;
import static io.github.seanwu1105.mipsprocessor.component.ForwardingUnit.ForwardingSignal.FROM_MEM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ForwardingUnitTest {

    @NotNull
    private InstructionDecodeToExecutionRegister idExe;
    @NotNull
    private ExecutionToMemoryAccessRegister exeMem;
    @NotNull
    private MemoryAccessToWriteBackRegister memWb;
    @NotNull
    private ForwardingUnit forwardingUnit;

    @BeforeEach
    void buildUp() {
        idExe = mock(InstructionDecodeToExecutionRegister.class);
        exeMem = mock(ExecutionToMemoryAccessRegister.class);
        memWb = mock(MemoryAccessToWriteBackRegister.class);
        forwardingUnit = new ForwardingUnit(idExe, exeMem, memWb);
    }

    @Test
    void testForwardExeHazard() {
        final var hazardAddress = 2;
        when(exeMem.getRegisterWrite()).thenReturn(MainController.RegisterWrite.TRUE);
        when(exeMem.getWriteRegisterAddress()).thenReturn(hazardAddress);
        when(memWb.getRegisterWrite()).thenReturn(MainController.RegisterWrite.TRUE);
        when(memWb.getWriteRegisterAddress()).thenReturn(hazardAddress);
        when(idExe.getRs()).thenReturn(hazardAddress);
        when(idExe.getRt()).thenReturn(hazardAddress);
        assertEquals(FROM_EXE, forwardingUnit.getOperand1ForwardingSignal());
        assertEquals(FROM_EXE, forwardingUnit.getOperand2ForwardingSignal());
    }

    @Test
    void testForwardMemHazard() {
        final var hazardAddress = 2;
        when(exeMem.getRegisterWrite()).thenReturn(MainController.RegisterWrite.FALSE);
        when(exeMem.getWriteRegisterAddress()).thenReturn(hazardAddress);
        when(memWb.getRegisterWrite()).thenReturn(MainController.RegisterWrite.TRUE);
        when(memWb.getWriteRegisterAddress()).thenReturn(hazardAddress);
        when(idExe.getRs()).thenReturn(hazardAddress);
        when(idExe.getRt()).thenReturn(hazardAddress);
        assertEquals(FROM_MEM, forwardingUnit.getOperand1ForwardingSignal());
        assertEquals(FROM_MEM, forwardingUnit.getOperand2ForwardingSignal());
    }

    @Test
    void testNoHazard() {
        when(exeMem.getRegisterWrite()).thenReturn(MainController.RegisterWrite.TRUE);
        when(exeMem.getWriteRegisterAddress()).thenReturn(2);
        when(idExe.getRs()).thenReturn(3);
        when(idExe.getRt()).thenReturn(4);
        assertEquals(FROM_ID, forwardingUnit.getOperand1ForwardingSignal());
        assertEquals(FROM_ID, forwardingUnit.getOperand2ForwardingSignal());

        when(exeMem.getRegisterWrite()).thenReturn(MainController.RegisterWrite.FALSE);
        when(exeMem.getWriteRegisterAddress()).thenReturn(2);
        when(idExe.getRs()).thenReturn(2);
        when(idExe.getRt()).thenReturn(2);
        assertEquals(FROM_ID, forwardingUnit.getOperand1ForwardingSignal());
        assertEquals(FROM_ID, forwardingUnit.getOperand2ForwardingSignal());

        when(memWb.getRegisterWrite()).thenReturn(MainController.RegisterWrite.TRUE);
        when(memWb.getWriteRegisterAddress()).thenReturn(2);
        when(idExe.getRs()).thenReturn(3);
        when(idExe.getRt()).thenReturn(4);
        assertEquals(FROM_ID, forwardingUnit.getOperand1ForwardingSignal());
        assertEquals(FROM_ID, forwardingUnit.getOperand2ForwardingSignal());

        when(memWb.getRegisterWrite()).thenReturn(MainController.RegisterWrite.FALSE);
        when(memWb.getWriteRegisterAddress()).thenReturn(2);
        when(idExe.getRs()).thenReturn(2);
        when(idExe.getRt()).thenReturn(2);
        assertEquals(FROM_ID, forwardingUnit.getOperand1ForwardingSignal());
        assertEquals(FROM_ID, forwardingUnit.getOperand2ForwardingSignal());
    }
}